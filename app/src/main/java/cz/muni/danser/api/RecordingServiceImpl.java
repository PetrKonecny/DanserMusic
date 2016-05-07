package cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordingServiceImpl implements RecordingService {
    private Callbacks callbacks;

    public RecordingServiceImpl(Callbacks callbacks){
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void getRecoringsCallback(List<DanceRecording> danceRecorings);
        void exceptionCallback(Throwable t);
    }

    @Override
    public void getRecordings(DanceSong danceSong) {
        Call<List<DanceRecording>> call = Api.getRetrofitApi().getRecordings(danceSong.getSongForDanceId());
        call.enqueue(new Callback<List<DanceRecording>>() {
            @Override
            public void onResponse(Call<List<DanceRecording>> call, Response<List<DanceRecording>> response) {
                callbacks.getRecoringsCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceRecording>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });
    }
}
