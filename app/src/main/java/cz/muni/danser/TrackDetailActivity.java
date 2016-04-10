package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import cz.muni.danser.model.Track;

public class TrackDetailActivity extends AppCompatActivity {

    @Bind(R.id.track_name) TextView mTextViewName;
    @Bind(R.id.artist_name) TextView mTextViewArtistName;
    @Bind(R.id.youtube_id) TextView mYoutubeId;
    @Bind(R.id.spotify_id) TextView mSpotifyId;
    @Bind(R.id.dance_type) TextView mDanceType;
    Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        track = extras.getParcelable("track");
        mTextViewName.setText(track.getTrackName());
        mTextViewArtistName.setText(track.getArtistName());
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
    }

    public void favoriteTrack(View view){
        if(track.favoriteTrack()) {
            Toast.makeText(this, "Track added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
