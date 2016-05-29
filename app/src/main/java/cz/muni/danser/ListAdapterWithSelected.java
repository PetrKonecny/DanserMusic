package cz.muni.danser;

import android.support.v7.widget.CardView;

import java.util.List;


public class ListAdapterWithSelected extends ListAdapter {

    public ListAdapterWithSelected(List dataset, final OnItemClickListener listener, int layout) {
        super(dataset, listener, layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.mCardView.setSelected(selectedPos == position);
        int color;
        if(holder.mCardView.isSelected()){
            color = R.color.cardview_dark_background;
        }else{
            color = R.color.solid_white;
        }
        holder.mCardView.setCardBackgroundColor(c.getResources().getColor(color));
        super.onBindViewHolder(holder, position);
    }
}
