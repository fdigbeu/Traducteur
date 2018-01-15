package lveapp.traducteur.View.Interfaces;

import android.content.Context;

import java.util.ArrayList;

import lveapp.traducteur.Model.Sms;

/**
 * Created by Maranatha on 15/01/2018.
 */

public class SMSView {
    public interface ISMS{
        public void initialize();
        public void events();
        public void loadSMSData(ArrayList<Sms> textos, int numberColumns);
        public void progressBarVisibility(int visibility);
        public void recyclerViewVisibility(int visibility);
        public void closeActivity();
    }

    public interface IPresenter{
        public void loadSMSData(Context context);
        public void closeActivity();
    }

    public interface ILoadSMS{
        public void OnLoadSMSFinished(ArrayList<Sms> textos);
        public void OnLoadSMSError();
    }
}
