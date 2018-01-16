package lveapp.traducteur.Presenter.SMS;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import lveapp.traducteur.Model.Sms;
import lveapp.traducteur.View.Interfaces.SMSView;

/**
 * Created by Maranatha on 13/01/2018.
 */

public class SMSPresenter implements SMSView.IPresenter, SMSView.ILoadSMS {

    private SMSView.ISMS isms;
    private SMSAsyntask smsAsyntask;

    public SMSPresenter(SMSView.ISMS isms) {
        this.isms = isms;
    }

    @Override
    public void loadSMSData(Context context) {
        try {
            isms.initialize();
            isms.events();
            isms.progressBarVisibility(View.VISIBLE);
            isms.recyclerViewVisibility(View.GONE);
            //--
            smsAsyntask = new SMSAsyntask();
            smsAsyntask.initialization(context, this);
            smsAsyntask.execute();
        }
        catch (Exception ex){}
    }

    /**
     * SMS Item is selected from SMSRecycler Adapter
     * @param texto
     */
    @Override
    public void OnItemSMSSelected(Sms texto){
        try {
            isms.OnItemSMSSelected(texto);
        }
        catch (Exception ex){}
    }

    @Override
    public void closeActivity() {
        try {
            isms.closeActivity();
        }
        catch (Exception ex){}
    }

    @Override
    public void OnLoadSMSFinished(ArrayList<Sms> textos) {
        isms.progressBarVisibility(View.GONE);
        isms.recyclerViewVisibility(View.VISIBLE);
        isms.loadSMSData(textos, 1);
        //Log.i("TAG_SMS_LIST", textos.toString());
    }

    @Override
    public void OnLoadSMSError() {
        Log.i("TAG_SMS_ERROR", "SMS ERROR");
    }
}
