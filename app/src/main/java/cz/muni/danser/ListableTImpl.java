package cz.muni.danser;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Pavel on 31. 3. 2016.
 */
public abstract class ListableTImpl implements ListableT {
    public String getTranslationIdentifier(){
        return null;
    }
    final public String getMainText(Context c){
        String r = getMainText();
        try{
            r = c.getString(R.string.class.getField(getTranslationIdentifier()).getInt(null));
        } catch(NoSuchFieldException e){
        } catch(IllegalAccessException e){
        } catch(Resources.NotFoundException e){
        }
        return r;
    }
}
