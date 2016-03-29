package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrackDetailActivity extends AppCompatActivity {

    @Bind(R.id.track_name) TextView mTextViewName;
    @Bind(R.id.artist_name) TextView mTextViewArtistName;
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
    }

    public void favoriteTrack(View view){
        if(track.favoriteTrack()) {
            Toast.makeText(this, "Track added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
