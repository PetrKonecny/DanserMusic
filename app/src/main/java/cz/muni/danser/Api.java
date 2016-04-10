package cz.muni.danser;

import android.content.Context;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Petr2 on 3/24/2016.
 */
final public class Api {
    final private static String apiURL = "https://api.dansermusic.com/";
    public static TrackService trackService;
    private static Context context;
    public static Context getContext(){
        return context;
    }
    public static void setContext(Context context){
        Api.context = context;
    }

    public static TrackService getTrackService() {
        //setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder().cache(cache).addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR).build();

        if (trackService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.apiURL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()))
                    .build();

            trackService = retrofit.create(TrackService.class);
        }
        return trackService;
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (Utils.isNetworkAvailable()) {
                int maxAge = 60; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 7; // tolerate 1 week
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
}
