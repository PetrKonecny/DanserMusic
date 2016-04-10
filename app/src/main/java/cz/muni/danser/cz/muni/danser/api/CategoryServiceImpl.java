package cz.muni.danser.cz.muni.danser.api;

import android.content.Context;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Petr2 on 4/10/2016.
 */
public class CategoryServiceImpl implements CategoryService {

    private Callbacks callbacks;

    public CategoryServiceImpl (Callbacks callbacks){
        this.callbacks = callbacks;
    }


    public interface Callbacks {
        void getAllCategoriesCallback(List<DanceCategory> categories);
        void getCategoryCallback(DanceCategory category);
        void getDancesForCategoryCallback(List<Dance> dances);
        void exceptionCallback(Throwable t);
    }

    @Override
    public void getCategories() {
        Call<List<DanceCategory>> call = Api.getRetrofitApi().getCategories();
        call.enqueue(new Callback<List<DanceCategory>>() {
            @Override
            public void onResponse(Call<List<DanceCategory>> call, Response<List<DanceCategory>> response) {
                callbacks.getAllCategoriesCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<DanceCategory>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });


    }

    @Override
    public void getCategory(String category) {
        Call<DanceCategory> call = Api.getRetrofitApi().getCategory(category);
        call.enqueue(new Callback<DanceCategory>() {
            @Override
            public void onResponse(Call<DanceCategory> call, Response<DanceCategory> response) {
                callbacks.getCategoryCallback(response.body());
            }

            @Override
            public void onFailure(Call<DanceCategory> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });

    }

    @Override
    public void getDances(String category) {
        Call<List<Dance>> call = Api.getRetrofitApi().getDances(category);
        call.enqueue(new Callback<List<Dance>>() {
            @Override
            public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                callbacks.getDancesForCategoryCallback(response.body());
            }

            @Override
            public void onFailure(Call<List<Dance>> call, Throwable t) {
                callbacks.exceptionCallback(t);
            }
        });

    }
}
