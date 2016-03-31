package cz.muni.danser;

import android.content.Context;

/**
 * For items that can get translations for their labels from strings.
 * Created by Pavel on 3/31/2016.
 */
public interface ListableT extends Listable{
    public String getMainText(Context c);
}
