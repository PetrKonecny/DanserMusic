package cz.muni.danser;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Petr2 on 3/23/2016.
 */
public class DanceCategoryListAdapter extends RecyclerView.Adapter<DanceCategoryListAdapter.ViewHolder> {

    private List<DanceCategory> mDataset;
    private OnItemClickListener listener;

    public DanceCategoryListAdapter(List<DanceCategory> dataset, OnItemClickListener listener){
        this.listener = listener;
        this.mDataset = dataset;
    }


    public interface OnItemClickListener {
        void onItemClick(DanceCategory danceCategory);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.square_list_item_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mDataset.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public CardView mCardView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item_text_view);
            mCardView = (CardView) v.findViewById(R.id.card_view);
        }

        public void bind(final DanceCategory item, final OnItemClickListener listener) {
            mTextView.setText(item.getDanceCategory());
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
