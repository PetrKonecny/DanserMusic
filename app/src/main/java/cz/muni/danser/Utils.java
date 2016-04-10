package cz.muni.danser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Petr2 on 4/6/2016.
 */
public final class Utils {
    public static String getStringFromResourceName(Context c, String resourceName){
        String r;
        try{
            r = c.getString(R.string.class.getField(resourceName).getInt(null));
        } catch(Exception e){
            return null;
        }
        return r;
    }

    public static boolean isNetworkAvailable() {
        Context context = Api.getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
        }
}
