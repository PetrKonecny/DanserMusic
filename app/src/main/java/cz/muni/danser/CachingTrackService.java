package cz.muni.danser;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import cz.muni.danser.cz.muni.danser.api.Api;
import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import cz.muni.danser.cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pavel on 9. 4. 2016.
 */
public class CachingTrackService {
    /*
    private static TreeMap<String, DanceCategory> categories = new TreeMap<>();
    private static List<DanceCategory> categoriesCachedList = new ArrayList<>();

    private static TreeMap<Integer, Dance> dances = new TreeMap<>();
    private static List<Dance> dancesCachedList = new ArrayList<>();

    private static TreeMap<String, Track> tracks = new TreeMap<>();
    private static List<Track> tracksCachedList = new ArrayList<>();

    private StringCallback failureCallback;

    private boolean cache = false;

    private void updateCachedLists(){
        if(categoriesCachedList == null){
            categoriesCachedList = new ArrayList<>(categories.values());
        }
        else{
            categoriesCachedList.clear();
            categoriesCachedList.addAll(categories.values());
        }
        Collections.sort(categoriesCachedList);

        if(dancesCachedList == null){
            dancesCachedList = new ArrayList<>(dances.values());
        }
        else{
            dancesCachedList.clear();
            dancesCachedList.addAll(dances.values());
        }
        Collections.sort(dancesCachedList);

        if(tracksCachedList == null){
            tracksCachedList = new ArrayList<>(tracks.values());
        }
        else{
            tracksCachedList.clear();
            tracksCachedList.addAll(tracks.values());
        }
        Collections.sort(tracksCachedList);
    }

    public CachingTrackService(Bundle savedInstanceState){
        if(savedInstanceState != null){
            categories = (TreeMap<String, DanceCategory>)savedInstanceState.getSerializable("DANCE_CATEGORIES");
            dances = (TreeMap<Integer, Dance>)savedInstanceState.getSerializable("DANCES");
            tracks = (TreeMap<String, Track>)savedInstanceState.getSerializable("TRACKS");
            updateCachedLists();
        }
    }

    public void save(Bundle savedInstanceState){
        savedInstanceState.putSerializable("DANCE_CATEGORIES", categories);
        savedInstanceState.putSerializable("DANCES", dances);
        savedInstanceState.putSerializable("TRACKS", tracks);
    }

    public CachingTrackService cache(boolean cache){
        this.cache = cache;
        return this;
    }
    public CachingTrackService failure(StringCallback failureCallback){
        this.failureCallback = failureCallback;
        return this;
    }


    private void updateCategories(List<DanceCategory> list){
        for(DanceCategory category : list){
            categories.put(category.getDanceCategory(), category);//replaces if exists
        }
    }
    private void updateDances(List<Dance> list){
        for(Dance dance : list){
            dances.put(dance.getDanceType(), dance);//replaces if exists
        }
    }
    private void updateTracks(List<Track> list){
        for(Track track : list){
            tracks.put(track.getMbid(), track);//replaces if exists
        }
    }

    public List<DanceCategory> getCategories(){
        return getCategories(null);
    }

    public List<DanceCategory> getCategories(final SimpleCallback callback) {
        categoriesCachedList.clear();
        categoriesCachedList.addAll(categories.values());
        Collections.sort(categoriesCachedList);

        Call<List<DanceCategory>> call = Api.getTrackService().getCategories();
        if((callback!=null || !cache) && Utils.isNetworkAvailable()){
            call.enqueue(new Callback<List<DanceCategory>>() {
                @Override
                public void onResponse(Call<List<DanceCategory>> call, Response<List<DanceCategory>> response) {
                    updateCategories(response.body());
                    CachingTrackService.this.cache(true).getCategories();
                    if(callback != null){
                        callback.callback();
                    }
                }
                @Override
                public void onFailure(Call<List<DanceCategory>> call, Throwable t) {
                    if(failureCallback != null){
                        failureCallback.callback(t.getLocalizedMessage());
                    }
                }
            });
        }
        return categoriesCachedList;
    }

    public List<Dance> getDances(String category){
        return getDances(category, null);
    }
    public List<Dance> getDances(final String category, final SimpleCallback callback) {
        dancesCachedList.clear();
        for(Dance dance : dances.values()){
            if(dance.getDanceCategory().equals(category)){
                dancesCachedList.add(dance);
            }
        }
        Collections.sort(dancesCachedList);

        Call<List<Dance>> call = Api.getTrackService().getDances(category);
        if((callback != null || !cache) && Utils.isNetworkAvailable()){
            call.enqueue(new Callback<List<Dance>>() {
                @Override
                public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                    updateDances(response.body());
                    CachingTrackService.this.cache(true).getDances(category);
                    if(callback != null){
                        callback.callback();
                    }
                }
                @Override
                public void onFailure(Call<List<Dance>> call, Throwable t) {
                    if(failureCallback != null){
                        failureCallback.callback(t.getLocalizedMessage());
                    }
                }
            });
        }
        return dancesCachedList;
    }

    public Dance getDance(int danceType, final TextView view) {
        Call<Dance> call = Api.getTrackService().getDance(danceType);
        if(!cache && Utils.isNetworkAvailable()){
            call.enqueue(new Callback<Dance>() {
                @Override
                public void onResponse(Call<Dance> call, Response<Dance> response) {
                    Dance dance = response.body();
                    dances.put(dance.getDanceType(), dance);
                    view.setText(dance.getMainText());
                }
                @Override
                public void onFailure(Call<Dance> call, Throwable t) {
                    if(failureCallback != null){
                        failureCallback.callback(t.getLocalizedMessage());
                    }
                }
            });
        }
        return dances.get(danceType);
    }

    public List<Track> getTracks(int danceType){
        return getTracks(danceType, null);
    }
    public List<Track> getTracks(final int danceType, final SimpleCallback callback) {
        tracksCachedList.clear();
        for(Track track : tracks.values()){
            if(track.getDanceType() == danceType){
                tracksCachedList.add(track);
            }
        }
        Collections.sort(tracksCachedList);

        Call<List<Track>> call = Api.getTrackService().getTracks(danceType);
        if((callback != null || !cache) && Utils.isNetworkAvailable()){
            call.enqueue(new Callback<List<Track>>() {
                @Override
                public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                    updateTracks(response.body());
                    CachingTrackService.this.cache(true).getTracks(danceType);
                    if(callback != null) {
                        callback.callback();
                    }
                }
                @Override
                public void onFailure(Call<List<Track>> call, Throwable t) {
                    if(failureCallback != null){
                        failureCallback.callback(t.getLocalizedMessage());
                    }
                }
            });
        }
        return tracksCachedList;
    }
    */
}
