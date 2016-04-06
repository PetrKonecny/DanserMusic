package cz.muni.danser;

import java.util.Map;

/**
 * Created by Petr2 on 4/6/2016.
 * Implement if Object has some attributes that have string representations in application resources
 */
public interface StringParsable {
    public Map<String,String> getResourceMap();
}
