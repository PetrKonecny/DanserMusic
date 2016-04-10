package cz.muni.danser.cz.muni.danser.model;

import java.util.Map;

/**
 * Created by Petr2 on 4/6/2016.
 * Implement if Object has some attributes that have string representations in application resources
 */
public interface StringParsable {
    Map<String,String> getResourceMap();
}
