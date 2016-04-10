package cz.muni.danser;

import android.support.annotation.NonNull;

/**
 * Created by Pavel on 10. 4. 2016.
 */
public abstract class Translatable implements StringParsable, Listable {
    @NonNull
    final public String getTranslatedMainText(){
        String translated = Utils.getStringFromResourceName(Api.getContext(), this.getResourceMap().get("mainTitle"));
        if(translated == null){
            translated = this.getMainText();
        }
        if(translated == null){
            translated = "";
        }
        return translated;
    }
}
