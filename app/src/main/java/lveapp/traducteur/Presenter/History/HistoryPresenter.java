package lveapp.traducteur.Presenter.History;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import lveapp.traducteur.Model.History;
import lveapp.traducteur.Presenter.Common.CommonPresenter;
import lveapp.traducteur.R;
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

    /**
     * History Item is selected from HistoryRecycler Adapter
     * @param history
     */
    @Override
    public void OnItemHistorySelected(History history){
        try {
            iHistory.OnItemHistorySelected(history);
        }
        catch (Exception ex){}
    }

    @Override
    public void OnLoadHistoryFinished(Context context, ArrayList<History> histories) {

        if(histories != null && histories.size()>0){
            iHistory.loadHistoryData(histories, 1);
        }
        else {
            String title = context.getResources().getString(R.string.lb_empty_history);
            String message = context.getResources().getString(R.string.lb_detail_empty_history);
            CommonPresenter.showMessage(context, title, message, true);
        }
        Log.i("TAG_HISTORY_LIST", histories.toString());
        iHistory.progressBarVisibility(View.GONE);
        iHistory.recyclerViewVisibility(View.VISIBLE);
    }

    @Override
    public void OnLoadHistoryError(Context context) {
        Log.i("TAG_HISTORY_ERROR", "HISTORY ERROR");
        iHistory.progressBarVisibility(View.GONE);
        iHistory.recyclerViewVisibility(View.GONE);
    }

    /**
     * Build text to html
     * @param textView
     * @param textValue
     */
    @Override
    public void buildTextViewToHtmlData(TextView textView, String textValue){
        CommonPresenter.buildTextViewToHtmlData(textView, textValue);
    }

    /**
     * Reduce text length
     * @param text
     * @param maxLength
     * @return
     */
    public String reduceTextLength(String text, int maxLength){
        String textValue = CommonPresenter.reduceLengthOfTheText(text, maxLength);
        return textValue;
    }
}
