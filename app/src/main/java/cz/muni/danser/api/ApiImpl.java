package cz.muni.danser.api;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Playlist;
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

    private Map<String, String> prepareRequiredFields(List<String> requiredFields){
        Map<String, String> map = new HashMap<>();
        for(String field : requiredFields){
            map.put("has_"+field.toLowerCase(), "1");
        }
        return map;
    }

    @Override
    public void getManyRecordings(final List<DanceSong> danceSongs, final List<String> requiredFields, final Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>> callback) {
        Call<Map<Integer, List<DanceRecording>>> call = Api.getRetrofitApi().getManyRecordings(
                songListToSongIds(danceSongs),
                prepareRequiredFields(requiredFields)
        );
        call.enqueue(new Callback<Map<Integer, List<DanceRecording>>>() {
            @Override
            public void onResponse(Call<Map<Integer, List<DanceRecording>>> call, Response<Map<Integer, List<DanceRecording>>> response) {
                callback.accept( integerMapToSongMap(danceSongs, response.body()) );
            }

            @Override
            public void onFailure(Call<Map<Integer, List<DanceRecording>>> call, Throwable t) {
                exceptionCallback.accept(t);
            }
        });
    }

    public void getManyRecordingsSync(final List<DanceSong> danceSongs, final List<String> requiredFields, final Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>> callback){
        try {
            Call<Map<Integer, List<DanceRecording>>> call = Api.getRetrofitApi().getManyRecordings(songListToSongIds(danceSongs),
                    prepareRequiredFields(requiredFields));
            Map<Integer, List<DanceRecording>> map = call.execute().body();
            callback.accept( integerMapToSongMap(danceSongs, map) );
        } catch (IOException e) {
            exceptionCallback.accept(e);
        }
    }

    private String songListToSongIds(List<DanceSong> danceSongs){
        if(danceSongs.size() == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(DanceSong danceSong : danceSongs){
            if(first){
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(danceSong.getSongForDanceId());
        }
        return sb.toString();
    }

    private LinkedHashMap<DanceSong, List<DanceRecording>> integerMapToSongMap(List<DanceSong> danceSongs, Map<Integer, List<DanceRecording>> integerMap){
        LinkedHashMap<DanceSong, List<DanceRecording>> map = new LinkedHashMap<>();
        for(DanceSong song : danceSongs) {
            map.put(song, integerMap.get(song.getSongForDanceId()));
        }
        return map;
    }

    public List<Playlist> getPlaylists() {
        return new Select().all().from(Playlist.class).execute();
    }

    @Override
    public Playlist getPlaylist(long id) {
        return new Select().from(Playlist.class).where("Id = ?",id).executeSingle();
    }

    @Override
    public Playlist getPlaylistByName(String name) {
        return new Select().from(Playlist.class).where("playlistName = ?", name).executeSingle();
    }

}
