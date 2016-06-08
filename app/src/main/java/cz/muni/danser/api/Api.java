package cz.muni.danser.api;

import android.content.Context;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
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
import retrofit2.http.QueryMap;

final public class Api {
    final private static String API_URL = "https://api.dansermusic.com/";
    final private static String API_TOKEN = "kGuMi,0iwe/2@en7*cz9GnAwp-|K{Q#v";
    final private static String API_VERSION = "3";
    private static RetrofitApi retrofitApi;
    private static Context context;

    public static Context getContext(){
        return context;
    }
    public static void setContext(Context context){
        Api.context = context;
    }

    public static OkHttpClient getCache(){
        /*File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);*/
        return new OkHttpClient.Builder()
                //.cache(cache)
                .addInterceptor(DANSER_HEADERS_INTERCEPTOR)
                .build();
    }

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(Api.API_URL)
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

        @GET("dances/{dance_type}/songs_for_dance")
        Call<List<DanceSong>> getDanceSongs(@Path("dance_type") int danceType);

        @GET("songs_for_dance")
        Call<List<DanceSong>> searchDanceSongs(@Query("song_name") String songName, @Query("limit") Integer limit);

        @GET("songs_for_dance/{song_for_dance}/recordings")
        Call<List<DanceRecording>> getRecordings(@Path("song_for_dance") int songForDanceId);

        @GET("songs_for_dance/{songs_for_dance}/recordings")
        Call<Map<Integer, List<DanceRecording>>> getManyRecordings(@Path("songs_for_dance") String danceSongsIds, @QueryMap Map<String, String> requiredFields);

        @GET("generate/preset/{preset}")
        Call<List<DanceSong>> generatePlaylistFromPreset(@Path("preset") int preset);

    }

    private static final Interceptor DANSER_HEADERS_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            Request newRequest = request.newBuilder()
                    .header("X-Danser-token", API_TOKEN)
                    .header("X-Danser-version", API_VERSION)
                    .method(request.method(), request.body())
                    .build();
            return chain.proceed(newRequest);
        }
    };

}
