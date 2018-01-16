package lveapp.traducteur.View.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

import lveapp.traducteur.Model.Sms;
import lveapp.traducteur.Presenter.SMS.SMSPresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Interfaces.SMSView;

/**
 * Created by Maranatha on 10/10/2017.
 */

public class SMSRecyclerAdapter extends RecyclerView.Adapter<SMSRecyclerAdapter.MyViewHolder> {

    private ArrayList<Sms> textoItems;
    private Hashtable<Integer, MyViewHolder> mViewHolder;
    private SMSView.ISMS isms;

    public SMSRecyclerAdapter(ArrayList<Sms> textoItems, SMSView.ISMS isms) {
        this.textoItems = textoItems;
        this.isms = isms;
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
        Sms mTexto = textoItems.get(position);
        holder.itemTitle.setText(mTexto.getAddress());
        holder.itemDate.setText(mTexto.getTime());
        holder.itemSubtitle.setText(mTexto.getMsg());
        holder.itemIcon.setImageResource(R.drawable.ic_sms_50dp);
    }

    @Override
    public int getItemCount() {
        return textoItems.size();
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
                    Sms mTexto = textoItems.get(positionItem);
                    SMSPresenter smsPresenter = new SMSPresenter(isms);
                    smsPresenter.OnItemSMSSelected(mTexto);
                }
            });
        }
    }
}
