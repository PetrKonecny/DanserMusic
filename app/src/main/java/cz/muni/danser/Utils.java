package cz.muni.danser;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Petr2 on 4/6/2016.
 */
public final class Utils {
    public static String getStringFromResourceName(Context c, String resourceName){
        String r = null;
        try{
            r = c.getString(R.string.class.getField(resourceName).getInt(null));
        } catch(Exception e){
            return r;
        }
        return r;
    }

}
