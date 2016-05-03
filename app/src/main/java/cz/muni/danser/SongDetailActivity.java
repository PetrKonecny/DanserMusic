package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.muni.danser.model.DanceSong;

public class SongDetailActivity extends AppCompatActivity {
    @Bind(R.id.song_name) TextView mTextViewName;
    @Bind(R.id.track_detail_table) TableLayout mTable;
    private DanceSong danceSong;

    private void addRow(int label_resource_id, View view){
        TableRow row = new TableRow(this);
            row.addView(textViewFromString(getString(label_resource_id)));
            row.addView(view);
        mTable.addView(row);
    }

    private TextView textViewFromString(String string){
        TextView textView = new TextView(this);
        textView.setText(string);
        return textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        danceSong = extras.getParcelable("danceSong");

        mTextViewName.setText(danceSong.getMainText());

        addRow(R.string.artist, textViewFromString(danceSong.getArtistNames()));
        addRow(R.string.release_label, textViewFromString(danceSong.getReleases()));
        addRow(R.string.release_year, textViewFromString(danceSong.getReleaseYears()));
        if(danceSong.getYoutubeIds() != null && danceSong.getYoutubeIds().size() > 0){
            TextView mYouTube = textViewFromString("https://www.youtube.com/watch?v="+danceSong.getYoutubeIds().iterator().next());
            mYouTube.setAutoLinkMask(Linkify.WEB_URLS);
            addRow(R.string.youtube, mYouTube);
        } //else: suggest button
        if(danceSong.getSpotifyIds() != null && danceSong.getSpotifyIds().size() > 0){
            TextView mSpotify = textViewFromString("https://open.spotify.com/track/"+danceSong.getSpotifyIds().iterator().next());
            mSpotify.setAutoLinkMask(Linkify.WEB_URLS);
            addRow(R.string.spotify, mSpotify);
        } //else: suggest button
    }

    public void favoriteTrack(View view){
        if(danceSong.favoriteSong()) {
            Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
