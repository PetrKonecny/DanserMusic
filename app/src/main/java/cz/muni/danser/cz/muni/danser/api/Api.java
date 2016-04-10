package cz.muni.danser.cz.muni.danser.api;

import android.content.Context;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.muni.danser.Utils;
import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import cz.muni.danser.cz.muni.danser.model.Track;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Petr2 on 3/24/2016.
 */
final public class Api {
    final private static String apiURL = "https://api.dansermusic.com/";
    private static RetrofitApi retrofitApi;
    private static Context context;

    public static Context getContext(){
        return context;
    }
    public static void setContext(Context context){
        Api.context = context;
    }

    public static OkHttpClient getCache(){
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        return new OkHttpClient.Builder().cache(cache).addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR).build();
    }

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(Api.apiURL)
                .client(getCache())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()))
                .build();
    }

    public static RetrofitApi getRetrofitApi(){
        if(retrofitApi == null){
            retrofitApi = getRetrofit().create(RetrofitApi.class);
        }
        return retrofitApi;
    }
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (Utils.isNetworkAvailable(context)) {
                request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
            } else {
                request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
            }
            return chain.proceed(request);
        }
    };

    public interface RetrofitApi {

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

}
