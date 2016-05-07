package cz.muni.danser.api;

import cz.muni.danser.model.DanceSong;

public interface RecordingService {
    void getRecordings(DanceSong danceSong);
}
