package cz.muni.danser;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlaylistActivity extends AppCompatActivity {
    @Bind(R.id.playlist_recycler_view)
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<Playlist> playlists;
    Playlist playlist;
    private static String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "dansermusic://spotifycallback";
    private static final String CLIENT_ID = "4cb6768e46ae40ebbb7efaec7ea66ca9";

    static final int YOUTUBE_REQUEST_CODE = 1000;

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, YOUTUBE_REQUEST_CODE);
    }

    private String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YOUTUBE_REQUEST_CODE) {
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
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    initSpotifyAddPlaylist(response.getAccessToken(), playlist.tracks());
                    break;

                // Auth flow returned an error
                case ERROR:
                    Toast.makeText(PlaylistActivity.this,"Can't connect to Spotify service", Toast.LENGTH_LONG).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }

        // Handle the result from exceptions
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().hasExtra("playlistName")) {
            getMenuInflater().inflate(R.menu.menu_playlist, menu);
        }
        return true;
    }

    private void createSpotifyPlaylist(final String id, final List<Track> tracks, final SpotifyService service){
        Map<String,Object> body = new HashMap<>();
        body.put("name", "new Danser playlist");
        body.put("public", false);
        service.createPlaylist(id, body, new Callback<kaaes.spotify.webapi.android.models.Playlist>() {
            @Override
            public void success(kaaes.spotify.webapi.android.models.Playlist playlist, Response response) {
                addTracksToPlaylist(id,playlist.id,tracks,service);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(PlaylistActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addTracksToPlaylist(String id, String playlistId, List<Track> tracks, SpotifyService service){
        Map<String,Object> body = new HashMap<>();
        List<String> uris = new ArrayList<>();
        for(Track track : tracks){
            if(track.getSpotifyId() != null){
                uris.add("spotify:track:"+track.getSpotifyId().split(",")[0]);
            }
        }
        if(uris.isEmpty()){
            Toast.makeText(PlaylistActivity.this,"No songs in your playlist have Spotify IDs.", Toast.LENGTH_LONG).show();
        } else {
            body.put("uris",uris);
            service.addTracksToPlaylist(id, playlistId, null, body, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Toast.makeText(PlaylistActivity.this,"Playlist was added to your spotify acccount", Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(PlaylistActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void initSpotifyAddPlaylist(String token, final List<Track> tracks){
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        final SpotifyService spotify = api.getService();
        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                createSpotifyPlaylist(userPrivate.id, tracks, spotify);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(PlaylistActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            if (Utils.isNetworkAvailable()) {
                new ExportPlaylistToYoutubeTask(PlaylistActivity.this, mEmail, SCOPE).execute(playlist.tracks());
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void generate(MenuItem item) {
        getUsername();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mRecyclerView.setHasFixedSize(true);
        ActionBar bar = getSupportActionBar();

        if (!intent.hasExtra("playlistName")) {
            if(bar != null){
                bar.setTitle(getString(R.string.playlists));
            }
            mLayoutManager = new LinearLayoutManager(this);
            playlists = new Select().all().from(Playlist.class).execute();
            mAdapter = new ListAdapter(playlists, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable playlist) {
                    Intent intent = new Intent(PlaylistActivity.this, PlaylistActivity.class);
                    intent.putExtra("playlistName", playlist.getMainText());
                    startActivity(intent);
                }
            }, R.layout.list_item_view);
        } else {
            playlist = new Select().from(Playlist.class).where("playlistName = ?", intent.getStringExtra("playlistName")).executeSingle();
            if(bar != null){
                bar.setTitle(playlist.getMainText());
            }
            mLayoutManager = new LinearLayoutManager(this);
            mAdapter = new ListAdapter(playlist.tracks(), new ListAdapter.OnItemClickListener() {
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

    public void exportToSpotify(MenuItem item){

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"playlist-modify-public", "playlist-modify-private", "user-read-private"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);

    }



    public class ExportPlaylistToYoutubeTask extends AsyncTask<List<Track>, String, String> {
        Activity mActivity;
        String mScope;
        String mEmail;

        public ExportPlaylistToYoutubeTask(Activity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(PlaylistActivity.this,s,Toast.LENGTH_LONG).show();
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
                    YouTube.Playlists.Insert playlistInsertCommand = youtube.playlists().insert("snippet", createPlaylist(playlist.getMainText(), "Playlist generated by Danser Music (www.dansermusic.com)"));
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
                            //continue;
                        }
                    }
                }
                return "YouTube playlist was generated.";
            } else {
                return "Unable to obtain token to create playlist.";
            }
        }

        public com.google.api.services.youtube.model.Playlist createPlaylist(String title, String description) {
            PlaylistSnippet playlistSnippet = new PlaylistSnippet();
            playlistSnippet.setTitle(title);
            playlistSnippet.setDescription(description);

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
