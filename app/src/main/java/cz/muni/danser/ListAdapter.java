package cz.muni.danser;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import cz.muni.danser.model.Listable;
import cz.muni.danser.model.StringParsable;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<?> mDataset;
    private OnItemClickListener listener;
    private int mLayout;
    protected static Context c;
    protected int selectedItemIndex = 0;

    public ListAdapter(List dataset, OnItemClickListener listener, int layout){
        this.listener = listener;
        this.mDataset = dataset;
        this.mLayout = layout;
    }


    public interface OnItemClickListener {
        void onItemClick(Listable item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c=parent.getContext())
                .inflate(mLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind((Listable)(mDataset.get(position)), listener);
    }

    public void setSelectedItemIndex(int pos){
        selectedItemIndex = pos;
    }

    public void onItemViewClick(CardView v, CardView oldV){
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public CardView mCardView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item_text_view);
            mCardView = (CardView) v.findViewById(R.id.card_view);
        }

        public void bind(final Listable item, final OnItemClickListener listener) {
            String text = null;
            if(item instanceof StringParsable) {
                text = Utils.getStringFromResourceName(c, ((StringParsable) item).getResourceMap().get("mainTitle"));
            }
            if(text == null){
                text = item.getMainText();
            }
            mTextView.setText(text);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemViewClick((CardView) v, null); //TODO: instead of null, pass previous CardView
                    int position = mDataset.indexOf(item);
                    setSelectedItemIndex(position);
                    listener.onItemClick(item);
                }
            });
        }
    }
}
