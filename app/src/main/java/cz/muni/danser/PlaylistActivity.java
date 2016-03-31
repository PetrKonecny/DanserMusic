package cz.muni.danser;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistActivity extends AppCompatActivity {
    @Bind(R.id.playlist_recycler_view)
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<Playlist> playlists;
    private String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube";

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
        // Handle the result from exceptions
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (true) { //online
                new GetUsernameTask(PlaylistActivity.this, mEmail, SCOPE).execute(playlists.get(0).tracks());
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void generate(View view) {
        getUsername();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mRecyclerView.setHasFixedSize(true);

        if (!intent.hasExtra("playlistName")) {
            mLayoutManager = new LinearLayoutManager(this);
            playlists = new Select().all().from(Playlist.class).execute();
            mAdapter = new ListAdapter<>(playlists, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable playlist) {
                    Intent intent = new Intent(PlaylistActivity.this, PlaylistActivity.class);
                    intent.putExtra("playlistName", playlist.getMainText());
                    startActivity(intent);
                }
            }, R.layout.list_item_view);
        } else {
            Playlist playlist = new Select().from(Playlist.class).where("playlistName = ?", intent.getStringExtra("playlistName")).executeSingle();
            mLayoutManager = new LinearLayoutManager(this);
            mAdapter = new ListAdapter<>(playlist.tracks(), new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable track) {
                    Intent intent = new Intent(PlaylistActivity.this, TrackDetailActivity.class);
                    intent.putExtra("track", (Track) track);
                    startActivity(intent);
                }
            }, R.layout.list_item_view);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /***************************************/

    public class GetUsernameTask extends AsyncTask<List<Track>, String, String> {
        Activity mActivity;
        String mScope;
        String mEmail;

        public GetUsernameTask(Activity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected String doInBackground(List<Track>... params) {
            String token = null;
            String id = null;
            try {
                token = fetchToken();
            } catch (IOException e) {
                Log.d("playlist async",e.getMessage());
            }
            if (token != null) {
                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.
                GoogleCredential credential = new GoogleCredential().setAccessToken(token);
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                        credential).setApplicationName("Danser Music").build();
                try {
                    YouTube.Playlists.Insert playlistInsertCommand = youtube.playlists().insert("snippet", createPlaylist("", ""));
                    com.google.api.services.youtube.model.Playlist playlistInserted = playlistInsertCommand.execute();
                    id = playlistInserted.getId();
                }catch(IOException e) {
                    Log.d("playlist async",e.getMessage());
                }
                YouTube.PlaylistItems items = youtube.playlistItems();
                for (Track track : params[0]) {
                    if (track.getYoutubeId() != null) {
                        try {
                            Log.d("youtube id", track.getYoutubeId().split(",")[0]);
                            items.insert("snippet,contentDetails", createPlaylistItem(track.getTrackName(), id, track.getYoutubeId().split(",")[0])).execute();
                        } catch (IOException e){
                            Log.d("playlist async",e.getMessage());
                            continue;
                        }
                    }
                }
            }
            return null;
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            //...return null;
        }

        public com.google.api.services.youtube.model.Playlist createPlaylist(String title, String description) {
            PlaylistSnippet playlistSnippet = new PlaylistSnippet();
            playlistSnippet.setTitle("Test Playlist " + Calendar.getInstance().getTime());
            playlistSnippet.setDescription("A private playlist created with the YouTube API v3");

            com.google.api.services.youtube.model.Playlist youTubePlaylist = new com.google.api.services.youtube.model.Playlist();
            youTubePlaylist.setSnippet(playlistSnippet);
            return youTubePlaylist;
        }


        public PlaylistItem createPlaylistItem(String title, String playlistId, String videoId) throws IOException {

            // Define a resourceId that identifies the video being added to the
            // playlist.
            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            // Set fields included in the playlistItem resource's "snippet" part.
            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setTitle(title);
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);

            // Create the playlistItem resource and set its snippet to the
            // object created above.
            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            // Call the API to add the playlist item to the specified playlist.
            // In the API call, the first argument identifies the resource parts
            // that the API response should contain, and the second argument is
            // the playlist item being inserted.
            return playlistItem;

        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
                //...
            }
            return null;
        }
        //...

        static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

        /**
         * This method is a hook for background threads and async tasks that need to
         * provide the user a response UI when an exception occurs.
         */
        public void handleException(final Exception e) {
            // Because this call comes from the AsyncTask, we must ensure that the following
            // code instead executes on the UI thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof GooglePlayServicesAvailabilityException) {
                        // The Google Play services APK is old, disabled, or not present.
                        // Show a dialog created by Google Play services that allows
                        // the user to update the APK
                        int statusCode = ((GooglePlayServicesAvailabilityException) e)
                                .getConnectionStatusCode();
                        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                PlaylistActivity.this,
                                REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                        dialog.show();
                    } else if (e instanceof UserRecoverableAuthException) {
                        // Unable to authenticate, such as when the user has not yet granted
                        // the app access to the account, but the user can fix this.
                        // Forward the user to an activity in Google Play services.
                        Intent intent = ((UserRecoverableAuthException) e).getIntent();
                        startActivityForResult(intent,
                                REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    }
                }
            });
        }
    }
}
