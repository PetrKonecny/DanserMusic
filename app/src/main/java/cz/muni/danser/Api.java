package cz.muni.danser;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Petr2 on 3/24/2016.
 */
public class Api {
    final private static String apiURL = "https://api.dansermusic.com/";
    public static TrackService trackService;

    public static TrackService getTrackService() {
        if (trackService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.apiURL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()))
                    .build();

            trackService = retrofit.create(TrackService.class);
        }
        return trackService;
    }
}
