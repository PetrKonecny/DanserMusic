package cz.muni.danser;

import android.support.v7.widget.CardView;

import java.util.List;

public class ListAdapterWithSelected extends ListAdapter {
    public ListAdapterWithSelected(List dataset, final OnItemClickListener listener, int layout) {
        super(dataset, listener, layout);
    }

    @Override
    public void onItemViewClick(CardView v, CardView oldV){
        //oldV.setCardBackgroundColor(c.getResources().getColor(R.color.solid_white));
        v.setCardBackgroundColor(c.getResources().getColor(R.color.red));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        super.onBindViewHolder(holder, position);
        int color;
        if(position == selectedItemIndex){
            color = R.color.red;
        } else {
            color = R.color.solid_white;
        }
        holder.mCardView.setCardBackgroundColor(c.getResources().getColor(color));
    }
}
