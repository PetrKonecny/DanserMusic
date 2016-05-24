package cz.muni.danser;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

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
import com.spotify.sdk.android.authentication.LoginActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ExportFragment extends Fragment {

    private static String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube";
    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "dansermusic://spotifycallback";
    private static final String CLIENT_ID = "4cb6768e46ae40ebbb7efaec7ea66ca9";
    private static final int YOUTUBE_REQUEST_CODE = 1000;
    private List<DanceSong> songs;
    private String playlistName;
    private String mEmail;
    private static ApiImpl api;

    public ExportFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        api = new ApiImpl();
        api.setExceptionCallback(new Consumer<Throwable>(){
            @Override
            public void accept(Throwable throwable) {
                Toast.makeText(ExportFragment.this.getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setSongs(List<DanceSong> songs) {
        this.songs = songs;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, YOUTUBE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YOUTUBE_REQUEST_CODE) {
            // Receiving a result from the AccountPicker
            if (resultCode == getActivity().RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(getActivity(), R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    initSpotifyAddPlaylist(response.getAccessToken(), songs);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Toast.makeText(ExportFragment.this.getActivity(), "Can't connect to Spotify service", Toast.LENGTH_LONG).show();
                    break;

                // Most likely auth flow was cancelled
                default:
            }
        }

        // Handle the result from exceptions
    }

    private void createSpotifyPlaylist(final String id, final List<DanceSong> danceSongs, final SpotifyService service) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "new Danser playlist");
        body.put("public", false);
        service.createPlaylist(id, body, new Callback<Playlist>() {
            @Override
            public void success(kaaes.spotify.webapi.android.models.Playlist playlist, Response response) {
                addTracksToPlaylist(id, playlist.id, danceSongs, service);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ExportFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addTracksToPlaylist(final String id, final String playlistId, List<DanceSong> danceSongs, final SpotifyService service) {
        final Map<String, Object> body = new HashMap<>();
        api.getManyRecordings(danceSongs, Arrays.asList(new String[]{"spotify"}), new Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>>() {
            @Override
            public void accept(LinkedHashMap<DanceSong, List<DanceRecording>> map) {
                if (map.size() == 0) {
                    Toast.makeText(ExportFragment.this.getActivity(), "No songs in your playlist have Spotify IDs.", Toast.LENGTH_LONG).show();
                } else {
                    body.put("uris", SongUtils.getSpotifyUrisForSongs(map));
                    service.addTracksToPlaylist(id, playlistId, null, body, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            Toast.makeText(ExportFragment.this.getActivity(), "Playlist was added to your spotify acccount", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(ExportFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void initSpotifyAddPlaylist(String token, final List<DanceSong> danceSongs) {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        final SpotifyService spotify = api.getService();
        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                createSpotifyPlaylist(userPrivate.id, danceSongs, spotify);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ExportFragment.this.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
            if (Utils.isNetworkAvailable(getActivity())) {
                new ExportPlaylistToYoutubeTask(ExportFragment.this.getActivity(), mEmail, SCOPE).execute(songs);
            } else {
                Toast.makeText(getActivity(), R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void exportToYoutube() {
        getUsername();
    }

    public void exportToSpotify() {

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"playlist-modify-public", "playlist-modify-private", "user-read-private"});
        AuthenticationRequest request = builder.build();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("EXTRA_AUTH_REQUEST", request);
        startActivityForResult(intent, SPOTIFY_REQUEST_CODE);
    }

    public class ExportPlaylistToYoutubeTask extends AsyncTask<List<DanceSong>, String, String> {
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
            Toast.makeText(ExportFragment.this.getActivity(), s, Toast.LENGTH_LONG).show();
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected String doInBackground(List<DanceSong>... params) {
            String token = null;
            String id = null;
            try {
                token = fetchToken();
            } catch (IOException e) {
                Log.d("playlist async", e.getMessage());
            }
            if (token == null) {
                return "Unable to obtain token to create playlist.";
            }

            GoogleCredential credential = new GoogleCredential().setAccessToken(token);
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                    credential).setApplicationName("Danser Music").build();
            try {
                YouTube.Playlists.Insert playlistInsertCommand = youtube.playlists().insert("snippet", createPlaylist(playlistName, "Playlist generated by Danser Music (www.dansermusic.com)"));
                com.google.api.services.youtube.model.Playlist playlistInserted = playlistInsertCommand.execute();
                id = playlistInserted.getId();
            } catch (IOException e) {
                Log.d("playlist async", e.getMessage());
            }
            final YouTube.PlaylistItems items = youtube.playlistItems();
            final String finalId = id;
            final List<String> results = new ArrayList<>();
            try {
                api.getManyRecordingsSync(params[0],  Arrays.asList(new String[]{"youtube"}), new Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>>() {
                    @Override
                    public void accept(LinkedHashMap<DanceSong, List<DanceRecording>> map) {
                        if (map.size() == 0) {
                            results.add("No songs in your playlist have YouTube IDs.");
                        } else {
                            LinkedHashMap<DanceSong, String> ids = SongUtils.getYoutubeIdsForSongs(map);
                            for(Map.Entry<DanceSong, String> entry : ids.entrySet()){
                                try {
                                    items.insert("snippet,contentDetails", createPlaylistItem(entry.getKey().getSongName(), finalId, entry.getValue())).execute();
                                } catch (IOException e) {
                                    Log.d("playlist async", e.getMessage());
                                    //continue;
                                }
                            }
                            results.add("YouTube playlist was generated.");
                        }
                    }
                });
            } catch (IOException e) {
                results.add(getString(R.string.not_online));
            }
            return results.get(0);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof GooglePlayServicesAvailabilityException) {
                        // The Google Play services APK is old, disabled, or not present.
                        // Show a dialog created by Google Play services that allows
                        // the user to update the APK
                        int statusCode = ((GooglePlayServicesAvailabilityException) e)
                                .getConnectionStatusCode();
                        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                ExportFragment.this.getActivity(),
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
