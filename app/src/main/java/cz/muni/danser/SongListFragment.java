package cz.muni.danser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.muni.danser.model.Listable;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SongListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private OnListFragmentInteractionListener mActivity;
    private boolean dual;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        return view;
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager){
        recyclerView.setLayoutManager(manager);
    }

    public void refreshList(List<Listable> songs, boolean dual) {
        this.dual = dual;
        refreshList(songs);
    }

    public void refreshList(List<Listable> songs) {
        int layout;
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            layout = R.layout.square_list_item_view;
        }else{
            layout = R.layout.list_item_view;
        }

        if(this.dual){
            mAdapter = new ListAdapterWithSelected(songs, new ListAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(Listable item) {
                    mActivity.onListItemClick(item);
                }
            },layout);
        } else {
            mAdapter = new ListAdapter(songs, new ListAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(Listable item) {
                    mActivity.onListItemClick(item);
                }
            },layout);
        }

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (OnListFragmentInteractionListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshList(mActivity.getSongs());
    }

    public interface OnListFragmentInteractionListener {
        void onListItemClick(Listable item);
        List<Listable> getSongs();
    }
}
