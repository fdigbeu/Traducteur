package lveapp.traducteur.Presenter.SMS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.ArrayList;

import lveapp.traducteur.Model.Sms;
import lveapp.traducteur.Presenter.Common.CommonPresenter;
import lveapp.traducteur.View.Interfaces.SMSView;

/**
 * Created by Maranatha on 15/01/2018.
 */

public class SMSAsyntask extends AsyncTask<Void, Void, ArrayList<Sms>>{

    private Context context;
    private SMSView.ILoadSMS iLoadSMS;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Sms> doInBackground(Void... voids) {
        try {
            Uri inboxURI = Uri.parse("content://sms/inbox");
            ArrayList<Sms> textos = new ArrayList<>();
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(inboxURI, null, null, null, null);
            while (c.moveToNext()){
                String id = c.getString(c.getColumnIndexOrThrow("_id")).toString();
                String address = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String msg = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String readState = c.getString(c.getColumnIndexOrThrow("read")).toString();
                String time = c.getString(c.getColumnIndexOrThrow("date")).toString();
                String folderName = null;
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    folderName = "inbox";
                } else {
                    folderName = "sent";
                }
                //--
                textos.add(new Sms(id, address, msg, readState, CommonPresenter.changeFormatDate(time), folderName));
            }
            return textos;
        }
        catch (Exception ex){
            iLoadSMS.OnLoadSMSError(context);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Sms> textos) {
        super.onPostExecute(textos);
        if(textos != null)
            iLoadSMS.OnLoadSMSFinished(context, textos);
        else
            iLoadSMS.OnLoadSMSError(context);
    }

    public void initialization(Context context, SMSView.ILoadSMS iLoadSMS){
        this.context = context;
        this.iLoadSMS = iLoadSMS;
    }
}
