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
import lveapp.traducteur.Presenter.History.HistoryPresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Interfaces.HistoryView;

/**
 * Created by Maranatha on 10/10/2017.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {

    private ArrayList<History> historyItems;
    private Hashtable<Integer, MyViewHolder> mViewHolder;
    private HistoryView.IHistory iHistory;

    public HistoryRecyclerAdapter(ArrayList<History> historyItems, HistoryView.IHistory iHistory) {
        this.historyItems = historyItems;
        this.iHistory = iHistory;
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
        final int maxLength = 100;
        HistoryPresenter historyPresenter = new HistoryPresenter(iHistory);
        String titleValue = "<font color=\"#441c04\">"+mHistory.getLangDeparture()+"</font> -> <font color=\"#804040\">"+mHistory.getLangArrivale()+"</font>";
        historyPresenter.buildTextViewToHtmlData(holder.itemTitle, titleValue);
        historyPresenter.buildTextViewToHtmlData(holder.itemDate, mHistory.getDate());
        String subTitleValue = "<font color=\"#441c04\">"+historyPresenter.reduceTextLength(mHistory.getMessageDeparture(), maxLength)+"</font>\n<font color=\"#804040\">"+historyPresenter.reduceTextLength(mHistory.getMessageArrivale(), maxLength)+"</font>";
        historyPresenter.buildTextViewToHtmlData(holder.itemSubtitle, subTitleValue);
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
                    History mHistory = historyItems.get(positionItem);
                    HistoryPresenter historyPresenter = new HistoryPresenter(iHistory);
                    historyPresenter.OnItemHistorySelected(mHistory);
                }
            });
        }
    }
}
