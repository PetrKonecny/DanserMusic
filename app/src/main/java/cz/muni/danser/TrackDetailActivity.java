package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrackDetailActivity extends AppCompatActivity {

    @Bind(R.id.track_name) TextView mTextVIewName;
    @Bind(R.id.track_mbid) TextView mTextViewMbid;
    Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        track = extras.getParcelable("track");
        mTextVIewName.setText(track.getTrackName());
        mTextViewMbid.setText(track.getMbid());
    }
}
