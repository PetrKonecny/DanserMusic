package cz.muni.danser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import java.util.Collection;
import cz.muni.danser.api.Api;
import cz.muni.danser.model.Translatable;

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

    @NonNull
    public static String getTranslatedMainText(Translatable o){
        String translated = getStringFromResourceName(Api.getContext(), o.getResourceMap().get("mainTitle"));
        if(translated == null){
            translated = o.getMainText();
        }
        if(translated == null){
            translated = "";
        }
        return translated;
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
        }

    public static <T extends Model> void activeAndroidSaveCollection(Collection<T> items){
        Collection<T> saved = new Select().from(items.iterator().next().getClass()).execute();
        if(saved != null) {
            items.removeAll(saved);
        }
        if(items.size() > 0) {
            ActiveAndroid.beginTransaction();
            try {
                for (T item : items) {
                    item.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
    }
}
