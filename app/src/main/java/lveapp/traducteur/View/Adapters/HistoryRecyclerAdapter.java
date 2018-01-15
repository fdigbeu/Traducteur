package lveapp.traducteur.View.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

import lveapp.traducteur.Model.History;
import lveapp.traducteur.R;

/**
 * Created by Maranatha on 10/10/2017.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {

    private ArrayList<History> historyItems;
    private Hashtable<Integer, MyViewHolder> mViewHolder;

    public HistoryRecyclerAdapter(ArrayList<History> historyItems) {
        this.historyItems = historyItems;
        mViewHolder = new Hashtable<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new  MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.positionItem = position;
        mViewHolder.put(position, holder);
        History mHistory = historyItems.get(position);
        holder.itemTitle.setText(mHistory.getLangDeparture()+"/"+mHistory.getLangArrivale());
        holder.itemDate.setText(mHistory.getDate());
        holder.itemSubtitle.setText(mHistory.getMessageDeparture()+"\n"+mHistory.getMessageArrivale());
        holder.itemIcon.setImageResource(R.drawable.ic_history_50dp);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        int positionItem;
        View linearLayout;
        ImageView itemIcon;
        TextView itemTitle;
        TextView itemDate;
        TextView itemSubtitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            //--
            linearLayout = itemView.findViewById(R.id.container);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDate = itemView.findViewById(R.id.item_date);
            itemSubtitle = itemView.findViewById(R.id.item_subtitle);
            // Events
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
