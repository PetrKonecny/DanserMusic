package cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.model.DanceSong;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongServiceImpl implements SongService {
    private Callbacks callbacks;

    public SongServiceImpl(Callbacks callbacks){
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void searchSongsCallback(List<DanceSong> danceSongs);
        void suggestSongsCallback(List<DanceSong> danceSongs);
        void exceptionCallback(Throwable t);
    }

    @Override
    public void searchSongs(String trackName, Integer limit) {
        Call<List<DanceSong>> call = Api.getRetrofitApi().searchDanceSongs(trackName,limit);
        call.enqueue(new Callback<List<DanceSong>>() {
            @Override
            public void onResponse(Call<List<DanceSong>> call, Response<List<DanceSong>> response) {
                callbacks.searchSongsCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceSong>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });

    }

    @Override
    public void suggestSongs(String trackName) {
        Call<List<DanceSong>> call = Api.getRetrofitApi().searchDanceSongs(trackName,5);
        call.enqueue(new Callback<List<DanceSong>>() {
            @Override
            public void onResponse(Call<List<DanceSong>> call, Response<List<DanceSong>> response) {
                callbacks.suggestSongsCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceSong>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }
}
