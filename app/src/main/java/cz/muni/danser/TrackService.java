package cz.muni.danser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Petr2 on 3/24/2016.
 */
public interface TrackService {
    @GET("categories")
    Call<List<DanceCategory>> getCategories();

    @GET("categories/{category}")
    Call<DanceCategory> getCategory(@Path("category") String category);

    @GET("categories/{category}/dances")
    Call<List<Dance>> getDances(@Path("category") String category);

    @GET("dances")
    Call<List<Dance>> getDances();

    @GET("dances/{dance_type}")
    Call<Dance> getDance(@Path("dance_type") int danceType);

    @GET("dances/{dance_type}/tracks")
    Call<List<Track>> getTracks(@Path("dance_type") int danceType);

    @GET("tracks")
    Call<List<Track>> getAllTracks();

    @GET("tracks/{mbid}")
    Call<Track> getTrack(@Path("mbid") String mbid);

    @GET("tracks")
    Call<List<Track>> searchTracks(@Query("track_name") String trackName, @Query("limit") Integer limit);
}
