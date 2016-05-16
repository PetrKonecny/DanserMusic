package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private static final int DANCE_VIEW_ID = View.generateViewId();

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

    private TextView textViewFromString(String string, int id){
        TextView textView = textViewFromString(string);
        textView.setId(id);
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
        if(danceSong == null){
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        mTextViewName.setText(danceSong.getMainText());

        if(danceSong.getWorkMbid() != null){
            addRow(R.string.work_mbid_label, textViewFromString(danceSong.getWorkMbid()));
        }

        addRow(R.string.dance_label, textViewFromString(danceSong.getDance().getMainText(), DANCE_VIEW_ID));
    }

    public void favoriteSong(View view){
        if(danceSong.favoriteSong()) {
            Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
