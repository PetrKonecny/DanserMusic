package cz.muni.danser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.sqlbrite.BriteDatabase;

import java.util.Collection;

import cz.muni.danser.api.Api;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Translatable;

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

    public static <T extends Model> void activeAndroidSaveCollection(final Collection<T> itemsToBeSaved){
        Collection<T> itemsAlreadySaved = null;
        if(!itemsToBeSaved.isEmpty()) {
            itemsAlreadySaved = new Select().from(itemsToBeSaved.iterator().next().getClass()).execute();
            if (itemsAlreadySaved != null) {
                itemsToBeSaved.removeAll(itemsAlreadySaved);
            }
        }

        if(itemsToBeSaved.size() > 0) {
            BriteDatabase.Transaction transaction = ActiveAndroid.beginTransaction();
            try {
                for (T item : itemsToBeSaved) {
                    if(item instanceof DanceSong){
                        DanceSong song = (DanceSong)item;
                        if(song.getDance().getIsUnique()) {
                            song.getDance().save();
                        }else{
                            song.setDance((Dance) new Select().from(Dance.class).where("DanceType = ?",song.getDance().getDanceType()).executeSingle());
                        }
                    }
                    item.save();
                }
                ActiveAndroid.setTransactionSuccessful(transaction);
            } finally {
                ActiveAndroid.endTransaction(transaction);
            }
        }
        if(itemsAlreadySaved != null){
            itemsToBeSaved.addAll(itemsAlreadySaved);
        }
    }

    public static int yesOrNo(boolean yesNo){
        if(yesNo){
            return R.string.yes;
        } else {
            return R.string.no;
        }
    }
}
