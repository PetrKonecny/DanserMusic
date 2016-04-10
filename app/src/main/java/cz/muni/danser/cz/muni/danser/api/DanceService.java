package cz.muni.danser.cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Petr2 on 4/10/2016.
 */
public interface DanceService {

    void getAllDances();

    void getDance(int danceType);

    void getTracks(int danceType);

}
