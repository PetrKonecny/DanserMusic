package cz.muni.danser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Petr2 on 3/24/2016.
 */
public interface TrackService {

    @GET("tracks")
    Call<List<Track>> getAllTracks();

    @GET("tracks/search")
    Call<List<Track>> searchTracks(@Query("track_name") String trackName, @Query("limit") int limit);

}
