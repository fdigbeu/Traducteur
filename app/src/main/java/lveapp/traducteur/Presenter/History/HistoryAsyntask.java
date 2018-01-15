package lveapp.traducteur.Presenter.History;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

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
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<History> histories) {
        super.onPostExecute(histories);
    }

    public void initialization(Context context, HistoryView.ILoadHistory iLoadHistory){
        this.context = context;
        this.iLoadHistory = iLoadHistory;
    }
}
