package lveapp.traducteur.Presenter.History;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import lveapp.traducteur.Model.History;
import lveapp.traducteur.View.Interfaces.HistoryView;

/**
 * Created by Maranatha on 13/01/2018.
 */

public class HistoryPresenter implements HistoryView.IPresenter, HistoryView.ILoadHistory{
    private HistoryView.IHistory iHistory;
    private HistoryAsyntask historyAsyntask;

    public HistoryPresenter(HistoryView.IHistory iHistory) {
        this.iHistory = iHistory;
    }

    @Override
    public void loadHistoryData(Context context) {
        try {
            iHistory.initialize();
            iHistory.events();
            iHistory.progressBarVisibility(View.VISIBLE);
            iHistory.recyclerViewVisibility(View.GONE);
            //--
            historyAsyntask = new HistoryAsyntask();
            historyAsyntask.initialization(context, this);
            historyAsyntask.execute();
        }
        catch (Exception ex){}
    }

    @Override
    public void closeActivity() {
        try {
            iHistory.closeActivity();
        }
        catch (Exception ex){}
    }

    @Override
    public void OnLoadHistoryFinished(ArrayList<History> histories) {
        Log.i("TAG_HISTORY_LIST", histories.toString());
        iHistory.progressBarVisibility(View.GONE);
        iHistory.recyclerViewVisibility(View.VISIBLE);
    }

    @Override
    public void OnLoadHistoryError() {
        Log.i("TAG_HISTORY_ERROR", "HISTORY ERROR");
    }
}
