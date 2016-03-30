package cz.muni.danser;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

/**
 * Created by Petr2 on 3/23/2016.
 */
public class ListAdapter<T> extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<T> mDataset;
    private OnItemClickListener listener;
    private int mLayout;

    public ListAdapter(List<T> dataset, OnItemClickListener listener, int layout){
        this.listener = listener;
        this.mDataset = dataset;
        this.mLayout = layout;
    }


    public interface OnItemClickListener {
        void onItemClick(Listable item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind((Listable)(mDataset.get(position)), listener);
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

        public void bind(final Listable item, final OnItemClickListener listener) {
            mTextView.setText(((Listable)item).getMainText());
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick((Listable)item);
                }
            });
        }
    }
}
