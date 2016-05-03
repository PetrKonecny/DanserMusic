package cz.muni.danser.api;

/**
 * Created by Petr2 on 4/10/2016.
 */
public interface DanceService {

    void getAllDances();

    void getDance(int danceType);

    void getSongs(int danceType);

}
