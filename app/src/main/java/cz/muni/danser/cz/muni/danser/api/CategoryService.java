package cz.muni.danser.cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Petr2 on 4/10/2016.
 */
public interface CategoryService {

    void getCategories();

    void getCategory(String category);

    void getDances(String category);

}
