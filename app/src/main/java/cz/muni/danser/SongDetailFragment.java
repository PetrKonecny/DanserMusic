package cz.muni.danser;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private void setButtonLink(int button_res_id, String url){
        Button button = (Button) getActivity().findViewById(button_res_id);
        button.setText(Html.fromHtml(String.format("<a href=\"%1$s\">%2$s</a>", url, button.getText())));
        button.setMovementMethod(LinkMovementMethod.getInstance());
        button.setEnabled(true);
    }

    private void addRow(int label_resource_id, String s, String url){
        TableRow row = new TableRow(this.getActivity());
        TextView label = textViewFromString(getString(label_resource_id));
        label.setTypeface(null, Typeface.BOLD);
        label.setPadding(0, 0, (int) (getActivity().getResources().getDisplayMetrics().density * 4), 0);
        row.addView(label);
        TextView text = textViewFromString(s);
        if(url != null){
            text.setText(Html.fromHtml(String.format("<a href=\"%1$s\">%2$s</a>", url, s)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }
        row.addView(text);
        mTable.addView(row);
    }

    private void addRow(int label_resource_id, String s){
        addRow(label_resource_id, s, null);
    }

    private TextView textViewFromString(String string){
        TextView textView = new TextView(this.getActivity());
        textView.setText(string);
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
                    Toast.makeText(SongDetailFragment.this.getActivity(), R.string.song_added_to_favorites, Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.detail_layout).setVisibility(View.GONE);

        return view;
    }

    public void updateDanceSong(@NonNull DanceSong danceSong){
        getActivity().findViewById(R.id.detail_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.no_detail_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.detail_mbid_button).setEnabled(false);
        getActivity().findViewById(R.id.detail_spotify_button).setEnabled(false);
        getActivity().findViewById(R.id.detail_youtube_button).setEnabled(false);
        mTable.removeAllViews();

        addRow(R.string.dance_label, Utils.getTranslatedMainText(danceSong.getDance()));
        if(danceSong.getWorkMbid() != null){
            String workMbidLink = String.format(getString(R.string.work_mbid_url), danceSong.getWorkMbid());
            setButtonLink(R.id.detail_mbid_button, workMbidLink);
        }

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
                addRow(R.string.recordings_number, String.valueOf(danceRecordings.size()));

                Set<String> artists = new HashSet<>(), releases = new HashSet<>();
                String youtube = null, spotify = null;
                int lengthMin = Integer.MAX_VALUE, lengthMax = Integer.MIN_VALUE,
                        releaseYearMin = Integer.MAX_VALUE, releaseYearMax = Integer.MIN_VALUE;
                float bpmMin = Float.MAX_VALUE, bpmMax = Float.MIN_VALUE;
                for(DanceRecording recording : danceRecordings){
                    artists.add(recording.getArtistName());
                    releases.add(recording.getReleaseName());
                    if(recording.getYoutubeId() != null){
                        youtube = recording.getYoutubeId();
                    }
                    if(recording.getSpotifyId() != null){
                        spotify = recording.getSpotifyId();
                    }
                    if(recording.getLength() != 0 && recording.getLength() < lengthMin){
                        lengthMin = recording.getLength();
                    }
                    if(recording.getLength() != 0 && recording.getLength() > lengthMax){
                        lengthMax = recording.getLength();
                    }
                    if(recording.getReleaseYear() != 0 && recording.getReleaseYear() < releaseYearMin){
                        releaseYearMin = recording.getReleaseYear();
                    }
                    if(recording.getReleaseYear() != 0 && recording.getReleaseYear() > releaseYearMax){
                        releaseYearMax = recording.getReleaseYear();
                    }
                    if(Float.compare(recording.getBpm(),0) != 0 && recording.getBpm() < bpmMin){
                        bpmMin = recording.getBpm();
                    }
                    if(Float.compare(recording.getBpm(),0) != 0 && recording.getBpm() > bpmMax){
                        bpmMax = recording.getBpm();
                    }
                }

                addRow(R.string.artist, artists.toString().substring(1, artists.toString().length()-1));

                if(youtube != null){
                    String youtubeLink = String.format(getString(R.string.youtube_url), youtube);
                    setButtonLink(R.id.detail_youtube_button, youtubeLink);
                }

                if(spotify != null){
                    String spotifyLink = String.format(getString(R.string.spotify_url), spotify);
                    setButtonLink(R.id.detail_spotify_button, spotifyLink);
                }

                if(lengthMin != Integer.MAX_VALUE) {
                    lengthMin /= 1000;
                    lengthMax /= 1000;
                    String length = String.format(getString(R.string.XminutesYseconds), lengthMin / 60, lengthMin % 60);
                    if (lengthMin != lengthMax) {
                        length += " - " + String.format(getString(R.string.XminutesYseconds), lengthMax / 60, lengthMax % 60);
                    }
                    addRow(R.string.length_label, length);
                }
                if(releaseYearMin != Integer.MAX_VALUE) {
                    String releaseYear = String.valueOf(releaseYearMin);
                    if (releaseYearMin != releaseYearMax) {
                        releaseYear += " - " + String.valueOf(releaseYearMax);
                    }
                    addRow(R.string.release_year, releaseYear);
                }
                if(Float.compare(bpmMin, Float.MAX_VALUE) != 0) {
                    String bpm = String.format("%.2f", bpmMin);
                    if (Float.compare(bpmMin, bpmMax) != 0) {
                        bpm += " - " + String.format("%.2f", bpmMax);
                    }
                    bpm += " bpm";
                    addRow(R.string.tempo_label, bpm);
                }
                addRow(R.string.releases_label, String.valueOf(releases.size()));
            }
        });
        this.danceSong = danceSong;
    }

}
