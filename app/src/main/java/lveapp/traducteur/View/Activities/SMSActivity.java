package lveapp.traducteur.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.ArrayList;

import lveapp.traducteur.Model.Sms;
import lveapp.traducteur.Presenter.SMS.SMSPresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Adapters.SMSRecyclerAdapter;
import lveapp.traducteur.View.Interfaces.SMSView;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_RECEIVE_SMS_RETURN_DATA;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_RECEIVE_SMS_TO_CONVERT;

public class SMSActivity extends AppCompatActivity implements SMSView.ISMS {
    // Ref widgets
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    // Ref presenter
    private SMSPresenter smsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        // Load presenter data
        smsPresenter = new SMSPresenter(this);
        smsPresenter.loadSMSData(SMSActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                smsPresenter.closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initialize() {
        // Widgets findView
        recyclerView = findViewById(R.id.smsRecyclerView);
        progressBar = findViewById(R.id.smsProgressBar);
        // Display Home Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void events() {

    }

    @Override
    public void loadSMSData(ArrayList<Sms> textos, int numberColumns) {
        GridLayoutManager gridLayout = new GridLayoutManager(SMSActivity.this, numberColumns);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setHasFixedSize(true);
        SMSRecyclerAdapter adapter = new SMSRecyclerAdapter(textos, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnItemSMSSelected(Sms texto){
        Intent intent = new Intent();
        intent.putExtra(KEY_RECEIVE_SMS_RETURN_DATA, texto.getMsg());
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
