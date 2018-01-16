package lveapp.traducteur.Presenter.History;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import lveapp.traducteur.Model.DAOHistory;
import lveapp.traducteur.Model.History;
import lveapp.traducteur.View.Interfaces.HistoryView;

/**
 * Created by Maranatha on 15/01/2018.
 */

public class HistoryAsyntask extends AsyncTask<Void, Void, ArrayList<History>> {

    private Context context;
    private HistoryView.ILoadHistory iLoadHistory;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<History> doInBackground(Void... voids) {
        try {
            DAOHistory daoHistory = new DAOHistory(context);
            ArrayList<History> histories = daoHistory.getAllData();
            Log.i("TAG_HISTORY_LIST", "doInBackground = "+histories.toString());
            return histories;
        }
        catch (Exception ex){
            iLoadHistory.OnLoadHistoryError(context);
            Log.i("TAG_HISTORY_LIST", "doInBackground Exception : "+ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<History> histories) {
        super.onPostExecute(histories);
        if(histories != null) {
            iLoadHistory.OnLoadHistoryFinished(context, histories);
        }
        else {
            iLoadHistory.OnLoadHistoryError(context);
            Log.i("TAG_HISTORY_LIST", "onPostExecute Exception");
        }
    }

    public void initialization(Context context, HistoryView.ILoadHistory iLoadHistory){
        this.context = context;
        this.iLoadHistory = iLoadHistory;
    }
}
