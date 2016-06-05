package cz.muni.danser;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;
import cz.muni.danser.model.Playlist;
import cz.muni.danser.model.SongPlaylist;
import cz.muni.danser.model.StringParsable;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    protected List<Listable> mDataset;
    private OnItemClickListener listener;
    private int mLayout;
    protected static Context c;
    public int selectedPos = -1;

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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public CardView mCardView;
        public ImageButton mButton;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item_text_view);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mButton = (ImageButton) v.findViewById(R.id.playlist_list_item_delete);

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
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                    listener.onItemClick(item);
                }
            });
            if(mButton != null){
                mButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        final Playlist playlist = (Playlist) item;
                        final int position = getAdapterPosition();
                        final List<DanceSong> songs = playlist.songs();
                        SongUtils.deletePlaylist(playlist.getId());
                        mDataset.remove(position);
                        notifyDataSetChanged();
                        Snackbar.make(mCardView,"Playlist deleted",Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        playlist.save();
                                        SongUtils.saveSongsToPaylist(playlist.getId(),songs);
                                        mDataset.add(position, playlist);
                                        notifyDataSetChanged();
                                    }
                                }).show();
                    }
                });






            }
        }
    }
}
