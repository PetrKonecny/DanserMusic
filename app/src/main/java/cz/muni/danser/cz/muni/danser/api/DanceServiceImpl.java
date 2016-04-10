package cz.muni.danser.cz.muni.danser.api;

import android.content.Context;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Petr2 on 4/10/2016.
 */
public class DanceServiceImpl implements DanceService {

    private Callbacks callbacks;

    public DanceServiceImpl(Callbacks callbacks){
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void getAllDancesCallback(List<Dance> dances);
        void getDanceCallback(Dance dance);
        void getTracksForDanceCallback(List<Track> tracks);
        void exceptionCallback(Throwable t);
    }

    @Override
    public void getAllDances() {
        Call<List<Dance>> call = Api.getRetrofitApi().getDances();
        call.enqueue(new Callback<List<Dance>>() {
            @Override
            public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                callbacks.getAllDancesCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Dance>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }

    @Override
    public void getDance(int danceType) {
        Call<Dance> call = Api.getRetrofitApi().getDance(danceType);
        call.enqueue(new Callback<Dance>() {
            @Override
            public void onResponse(Call<Dance> call, Response<Dance> response) {
                callbacks.getDanceCallback(response.body());
            }

            @Override
            public void onFailure(Call<Dance> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }

    @Override
    public void getTracks(int danceType) {
        Call<List<Track>> call = Api.getRetrofitApi().getTracks(danceType);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                callbacks.getTracksForDanceCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }
}
