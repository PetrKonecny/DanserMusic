package cz.muni.danser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;


public class SongDetailFragment extends Fragment {
    TableLayout mTable;
    private DanceSong danceSong;

    public SongDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addRow(int label_resource_id, View view){
        TableRow row = new TableRow(this.getActivity());
        row.addView(textViewFromString(getString(label_resource_id)));
        row.addView(view);
        mTable.addView(row);
    }

    private TextView textViewFromString(String string){
        TextView textView = new TextView(this.getActivity());
        textView.setText(string);
        return textView;
    }

    private TextView textViewFromString(String string, int id){
        TextView textView = textViewFromString(string);
        textView.setId(id);
        return textView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);
        mTable = (TableLayout) view.findViewById(R.id.track_detail_table);
        view.findViewById(R.id.detail_favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Favorite.onClick",danceSong.getSongName());
                if(danceSong.favoriteSong()) {
                    Toast.makeText(SongDetailFragment.this.getActivity(), "Song added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.detail_layout).setVisibility(View.GONE);
        return view;
    }

    public void updateDanceSong(@NonNull DanceSong danceSong){
        getActivity().findViewById(R.id.detail_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.no_detail_layout).setVisibility(View.GONE);
        mTable.removeAllViews();
        if(danceSong.getWorkMbid() != null){
            addRow(R.string.work_mbid_label, textViewFromString(danceSong.getWorkMbid()));
        }

        addRow(R.string.dance_label, textViewFromString(danceSong.getDance().getMainText()));

        ApiImpl api = new ApiImpl();
        api.setExceptionCallback(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Toast.makeText(SongDetailFragment.this.getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        api.getRecordings(danceSong, new Consumer<List<DanceRecording>>() {
            @Override
            public void accept(List<DanceRecording> danceRecordings) {
                addRow(R.string.recordings_number, textViewFromString(String.valueOf(danceRecordings.size())));

            }
        });
        this.danceSong = danceSong;
    }

}
