package cz.muni.danser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.muni.danser.model.Listable;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SongListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView loadingView;
    private ListAdapter mAdapter;
    private OnListFragmentInteractionListener mActivity;
    private boolean dual;
    private boolean pending;

    public void setPending(boolean pending) {
        this.pending = pending;
    }

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
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            emptyView = (TextView) view.findViewById(R.id.empty_view);
            loadingView = (TextView) view.findViewById(R.id.loading_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
        recyclerView.setHasFixedSize(true);
        if(songs.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            if(pending) {
                loadingView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
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
        refreshList(mActivity.getSongs(),((Context) mActivity).getResources().getBoolean(R.bool.dualPane));
    }

    public interface OnListFragmentInteractionListener {
        void onListItemClick(Listable item);
        List<Listable> getSongs();
    }
}
