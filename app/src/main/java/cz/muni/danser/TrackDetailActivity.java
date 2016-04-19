package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.muni.danser.api.Api;
import cz.muni.danser.api.DanceService;
import cz.muni.danser.api.DanceServiceImpl;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackDetailActivity extends AppCompatActivity implements DanceServiceImpl.Callbacks {

    @Bind(R.id.track_name) TextView mTextViewName;
    @Bind(R.id.artist_name) TextView mTextViewArtistName;
    @Bind(R.id.release_name) TextView mTextViewReleaseName;
    @Bind(R.id.release_year) TextView mTextViewReleaseYear;
    @Bind(R.id.youtube_id) TextView mYoutubeId;
    @Bind(R.id.spotify_id) TextView mSpotifyId;
    @Bind(R.id.dance_type) TextView mDanceType;
    Track track;
    DanceService danceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        ButterKnife.bind(this);
        danceService = new DanceServiceImpl(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        track = extras.getParcelable("track");
        mTextViewName.setText(track.getTrackName());
        mTextViewArtistName.setText(track.getArtistName());
        mTextViewReleaseName.setText(track.getReleaseName());
        mTextViewReleaseYear.setText(String.valueOf(track.getReleaseYear()));
        if(track.getYoutubeId() != null){
            String[] youtubeIds = track.getYoutubeId().split(",");
            if(youtubeIds.length > 0){
                mYoutubeId.setText("https://www.youtube.com/watch?v="+youtubeIds[0]);
            }
        }
        if(track.getSpotifyId() != null){
            String[] spotifyIds = track.getSpotifyId().split(",");
            if(spotifyIds.length > 0){
                mSpotifyId.setText("https://open.spotify.com/track/"+spotifyIds[0]);
            }
        }
        danceService.getDance(track.getDanceType());
    }

    public void favoriteTrack(View view){
        if(track.favoriteTrack()) {
            Toast.makeText(this, "Track added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getAllDancesCallback(List<Dance> dances) {

    }

    @Override
    public void getDanceCallback(Dance dance) {
        mDanceType.setText(dance.getMainText());
    }

    @Override
    public void getTracksForDanceCallback(List<Track> tracks) {

    }

    @Override
    public void exceptionCallback(Throwable t) {
        Log.d("trackdetail", t.getLocalizedMessage());
    }
}
