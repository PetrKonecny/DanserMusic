package cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiImpl implements GeneralApi {
    private Consumer<Throwable> exceptionCallback = new Consumer<Throwable>(){
        @Override
        public void accept(Throwable throwable) {
        }
    };

    public void setExceptionCallback(Consumer<Throwable> exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
    }

    @Override
    public void getCategories(final Consumer<List<DanceCategory>> callback) {
        Call<List<DanceCategory>> call = Api.getRetrofitApi().getCategories();
        call.enqueue(new Callback<List<DanceCategory>>() {
            @Override
            public void onResponse(Call<List<DanceCategory>> call, Response<List<DanceCategory>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceCategory>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void getCategory(String category, final Consumer<DanceCategory> callback) {
        Call<DanceCategory> call = Api.getRetrofitApi().getCategory(category);
        call.enqueue(new Callback<DanceCategory>() {
            @Override
            public void onResponse(Call<DanceCategory> call, Response<DanceCategory> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<DanceCategory> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });

    }

    @Override
    public void getDances(String category, final Consumer<List<Dance>> callback) {
        Call<List<Dance>> call = Api.getRetrofitApi().getDances(category);
        call.enqueue(new Callback<List<Dance>>() {
            @Override
            public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<Dance>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void getAllDances(final Consumer<List<Dance>> callback) {
        Call<List<Dance>> call = Api.getRetrofitApi().getDances();
        call.enqueue(new Callback<List<Dance>>() {
            @Override
            public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<Dance>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void getDance(int danceType, final Consumer<Dance> callback) {
        Call<Dance> call = Api.getRetrofitApi().getDance(danceType);
        call.enqueue(new Callback<Dance>() {
            @Override
            public void onResponse(Call<Dance> call, Response<Dance> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<Dance> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void getSongs(int danceType, final Consumer<List<DanceSong>> callback) {
        Call<List<DanceSong>> call = Api.getRetrofitApi().getDanceSongs(danceType);
        call.enqueue(new Callback<List<DanceSong>>() {
            @Override
            public void onResponse(Call<List<DanceSong>> call, Response<List<DanceSong>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceSong>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void searchSongs(String trackName, Integer limit, final Consumer<List<DanceSong>> callback) {
        Call<List<DanceSong>> call = Api.getRetrofitApi().searchDanceSongs(trackName,limit);
        call.enqueue(new Callback<List<DanceSong>>() {
            @Override
            public void onResponse(Call<List<DanceSong>> call, Response<List<DanceSong>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceSong>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });

    }

    @Override
    public void suggestSongs(String trackName, final Consumer<List<DanceSong>> callback) {
        Call<List<DanceSong>> call = Api.getRetrofitApi().searchDanceSongs(trackName,5);
        call.enqueue(new Callback<List<DanceSong>>() {
            @Override
            public void onResponse(Call<List<DanceSong>> call, Response<List<DanceSong>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceSong>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    @Override
    public void getRecordings(DanceSong danceSong, final Consumer<List<DanceRecording>> callback) {
        Call<List<DanceRecording>> call = Api.getRetrofitApi().getRecordings(danceSong.getSongForDanceId());
        call.enqueue(new Callback<List<DanceRecording>>() {
            @Override
            public void onResponse(Call<List<DanceRecording>> call, Response<List<DanceRecording>> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceRecording>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }
}
