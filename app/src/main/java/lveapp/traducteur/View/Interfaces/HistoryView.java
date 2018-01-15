package lveapp.traducteur.View.Interfaces;

import android.content.Context;

import java.util.ArrayList;

import lveapp.traducteur.Model.History;

/**
 * Created by Maranatha on 15/01/2018.
 */

public class HistoryView {
    public interface IHistory{
        public void initialize();
        public void events();
        public void loadHistoryData(ArrayList<History> histories, int numberColumns);
        public void progressBarVisibility(int visibility);
        public void recyclerViewVisibility(int visibility);
        public void closeActivity();
    }

    public interface IPresenter{
        public void loadHistoryData(Context context);
        public void closeActivity();
    }

    public interface ILoadHistory{
        public void OnLoadHistoryFinished(ArrayList<History> histories);
        public void OnLoadHistoryError();
    }
}
