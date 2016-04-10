package cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Petr2 on 4/10/2016.
 */
public class TrackServiceImpl implements TrackService {

    private Callbacks callbacks;

    public TrackServiceImpl(Callbacks callbacks){
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void getAllTracksCallback(List<Track> tracks);
        void getTrackCallback(Track track);
        void searchTracksCallback(List<Track> tracks);
        void suggestTracksCallback(List<Track> tracks);
        void exceptionCallback(Throwable t);
    }

    @Override
    public void getAllTracks() {
        Call<List<Track>> call = Api.getRetrofitApi().getAllTracks();
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                callbacks.getAllTracksCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t)  {
                callbacks.exceptionCallback(t);
            }
        });
    }

    @Override
    public void getTrack(String mbid) {
        Call<Track> call = Api.getRetrofitApi().getTrack(mbid);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                callbacks.getTrackCallback(response.body());
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }

    @Override
    public void searchTracks(String trackName, Integer limit) {
        Call<List<Track>> call = Api.getRetrofitApi().searchTracks(trackName,limit);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                callbacks.searchTracksCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });

    }

    @Override
    public void suggestTracks(String trackName) {
        Call<List<Track>> call = Api.getRetrofitApi().searchTracks(trackName,10);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                callbacks.suggestTracksCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });    }
}
