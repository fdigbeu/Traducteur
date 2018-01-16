package lveapp.traducteur.View.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.ArrayList;

import lveapp.traducteur.Model.History;
import lveapp.traducteur.Presenter.History.HistoryPresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Adapters.HistoryRecyclerAdapter;
import lveapp.traducteur.View.Interfaces.HistoryView;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_RECEIVE_HISTORY_RETURN_DATA;

public class HistoryActivity extends AppCompatActivity implements HistoryView.IHistory {
    // Ref widgets
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    // Ref presenter
    private HistoryPresenter historyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Load data presenter
        historyPresenter = new HistoryPresenter(this);
        historyPresenter.loadHistoryData(HistoryActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                historyPresenter.closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initialize() {
        recyclerView = findViewById(R.id.historyRecyclerView);
        progressBar = findViewById(R.id.historyProgressBar);
        // Display Home Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void events() {

    }

    @Override
    public void loadHistoryData(ArrayList<History> histories, int numberColumns) {
        GridLayoutManager gridLayout = new GridLayoutManager(HistoryActivity.this, numberColumns);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setHasFixedSize(true);
        HistoryRecyclerAdapter adapter = new HistoryRecyclerAdapter(histories, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnItemHistorySelected(History history){
        Intent intent = new Intent();
        intent.putExtra(KEY_RECEIVE_HISTORY_RETURN_DATA, ""+history.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void progressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void recyclerViewVisibility(int visibility) {
        recyclerView.setVisibility(visibility);
    }

    @Override
    public void closeActivity() {
        this.finish();
    }
}
