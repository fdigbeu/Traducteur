package lveapp.traducteur.View.Activities;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import lveapp.traducteur.Presenter.Home.HomePresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Interfaces.HomeView;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_COPY_TEXT_IN_PRESS;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_LANGUAGE_ARRIVAL;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_LANGUAGE_DEPARTURE;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_TEXT_TO_TRANSLATE;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_PERMISSION_REQUEST_READ_SMS;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_RECEIVE_HISTORY_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_RECEIVE_SMS_TO_CONVERT;

public class HomeActivity extends AppCompatActivity implements HomeView.IHome, TextToSpeech.OnInitListener{
    // Ref attributes
    private Hashtable<String, String> translateValues;
    // Ref widgets
    private Spinner language_1, language_2;
    private Button btn_translate;
    private ImageButton btn_compare, btn_read_1, btn_read_2, btn_share_1, btn_share_2;
    private ImageButton btn_content_copy_1, btn_content_copy_2, btn_clean_text;
    private EditText traduction_language_1;
    private EditText traduction_language_2;
    private ProgressBar progressBar;
    private LinearLayout layout_start, layout_end;
    // Ref TexttoSpeech
    private TextToSpeech textToSpeech_1, textToSpeech_2;
    private int initTextToSpeech;
    private BroadcastReceiver ttsReceiver;
    // Ref presenter
    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // TexttoSpeech object
        textToSpeech_1 = new TextToSpeech(this, this);
        textToSpeech_2 = new TextToSpeech(this, this);
        // Hashtable object
        translateValues = new Hashtable<>();
        // Load Presenter
        homePresenter = new HomePresenter(this);
        homePresenter.loadTranslateData(HomeActivity.this, this.getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            // Voice
            case R.id.action_voice:
                break;
            // SMS
            case R.id.action_sms:
                homePresenter.checkPermissionToReadSMS(HomeActivity.this);
                break;
            // History
            case R.id.action_history:
                homePresenter.displayHistoryActivity();
                break;
            // Share app
            case R.id.action_share_app:
                homePresenter.shareApplication(HomeActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initialize() {
        // Ref widgets
        language_1 = findViewById(R.id.language_1);
        language_2 = findViewById(R.id.language_2);
        btn_translate = findViewById(R.id.btn_translate);
        btn_compare = findViewById(R.id.btn_compare);
        btn_clean_text = findViewById(R.id.btn_clean_text);
        btn_read_1 = findViewById(R.id.btn_read_1);
        btn_read_2 = findViewById(R.id.btn_read_2);
        btn_share_1 = findViewById(R.id.btn_share_1);
        btn_share_2 = findViewById(R.id.btn_share_2);
        btn_content_copy_1 = findViewById(R.id.btn_content_copy_1);
        btn_content_copy_2 = findViewById(R.id.btn_content_copy_2);
        traduction_language_1 = findViewById(R.id.traduction_language_1);
        traduction_language_2 = findViewById(R.id.traduction_language_2);
        progressBar = findViewById(R.id.progressBar);
        layout_start = findViewById(R.id.layout_start);
        layout_end = findViewById(R.id.layout_end);
        // Prepared TextToSpeech receiver
        ttsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                homePresenter.retrieveBroadcastReceiver(intent);
            }
        };
        registerReceiver(ttsReceiver, new IntentFilter(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED));
    }

    @Override
    public void events() {
        // If data change in this edit text
        traduction_language_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                homePresenter.retrieveTextChangeAction(charSequence.toString().trim(), traduction_language_2.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        // If data has focus : allow press by keyboard
        traduction_language_1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    translateValues.put(KEY_TEXT_TO_TRANSLATE, textView.getText().toString().trim());
                    homePresenter.retrieveUserAction(i, keyEvent, HomeActivity.this, translateValues);
                } catch (Exception ex){}
                return false;
            }
        });
        // Clean the text to translate
        btn_clean_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePresenter.clearAllTranslatedText();
            }
        });
        // If user clicks on translate button
        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_TEXT_TO_TRANSLATE, traduction_language_1.getText().toString().trim());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks on translate button
        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_LANGUAGE_DEPARTURE, ""+language_1.getSelectedItemPosition());
                translateValues.put(KEY_LANGUAGE_ARRIVAL, ""+language_2.getSelectedItemPosition());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to read audio from text to translate
        btn_read_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to read audio from translated text
        btn_read_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to departure language
        language_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translateValues.put(KEY_LANGUAGE_DEPARTURE, adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // If user clicks to arrival language
        language_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translateValues.put(KEY_LANGUAGE_ARRIVAL, adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        // If user clicks to share text to translate
        btn_share_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_TEXT_TO_TRANSLATE, traduction_language_1.getText().toString().trim());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to share translated text
        btn_share_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_TEXT_TO_TRANSLATE, traduction_language_2.getText().toString().trim());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to copy text to translate
        btn_content_copy_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_COPY_TEXT_IN_PRESS, traduction_language_1.getText().toString().trim());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
        // If user clicks to copy text to translate
        btn_content_copy_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateValues.put(KEY_COPY_TEXT_IN_PRESS, traduction_language_2.getText().toString().trim());
                homePresenter.retrieveUserAction(view, translateValues);
            }
        });
    }

    @Override
    public void enableAllWidgets(boolean enable) {
        language_1.setEnabled(enable);
        btn_read_1.setEnabled(enable);
        btn_content_copy_1.setEnabled(enable);
        btn_share_1.setEnabled(enable);
        btn_compare.setEnabled(enable);
        language_2.setEnabled(enable);
        btn_read_2.setEnabled(enable);
        btn_content_copy_2.setEnabled(enable);
        btn_share_2.setEnabled(enable);
    }

    @Override
    public void enableCompareButton(boolean enable) {
        btn_compare.setEnabled(enable);
    }

    @Override
    public void cleanTextButtonVisibility(int visibility){
        btn_clean_text.setVisibility(visibility);
    }

    @Override
    public void enableSpinners(boolean enable){
        language_1.setEnabled(enable);
        language_2.setEnabled(enable);
    }

    @Override
    public void enableTopWidgets(boolean enable) {
        btn_read_1.setEnabled(enable);
        btn_content_copy_1.setEnabled(enable);
        btn_share_1.setEnabled(enable);
    }

    @Override
    public void enableBottomWidgets(boolean enable) {
        btn_read_2.setEnabled(enable);
        btn_content_copy_2.setEnabled(enable);
        btn_share_2.setEnabled(enable);
    }

    @Override
    public void enableTextToTranslate(boolean enable) {
        traduction_language_1.setEnabled(enable);
    }

    @Override
    public void enableTranslateButton(boolean enable){
        btn_translate.setEnabled(enable);
    }

    @Override
    public void cleanAllTranslatedText() {
        traduction_language_1.setText("");
        traduction_language_2.setText("");
    }

    @Override
    public void feedTranslate(String text_2) {
        traduction_language_2.setText(text_2);
        //--
        homePresenter.initializeTextToSpeechLangDeparture(initTextToSpeech, textToSpeech_1, translateValues.get(KEY_LANGUAGE_DEPARTURE));
        homePresenter.initializeTextToSpeechLangArrival(initTextToSpeech, textToSpeech_2, translateValues.get(KEY_LANGUAGE_ARRIVAL));
    }

    @Override
    public void progressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void loadLanguageList(ArrayAdapter<String> adapter) {
        language_1.setAdapter(adapter);
        language_1.setSelection(0);
        language_2.setAdapter(adapter);
        language_2.setSelection(1);
    }

    @Override
    public void changeLanguagePosition(int position_1, int position_2){
        language_1.setSelection(position_1);
        language_2.setSelection(position_2);
    }

    @Override
    public void readTextFromTextToSpeechLangDeparture() {
        try {
            textToSpeech_1.speak(traduction_language_1.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
            textToSpeech_1.setPitch(1.1f);
        }
        catch (Exception ex){}
    }

    @Override
    public void readTextFromTextToSpeechLangArrival() {
        try {
            textToSpeech_2.speak(traduction_language_2.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
            textToSpeech_2.setPitch(1.1f);
        }
        catch (Exception ex){}
    }

    @Override
    public void simulateTranslateButtonClick() {
        new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                btn_translate.performClick();
            }
        }.start();
    }

    @Override
    public void simulateCleanTextButtonClick() {
        new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                btn_clean_text.performClick();
            }
        }.start();
    }

    @Override
    public void feedEditText(String text){
        traduction_language_1.setText(text);
    }

    @Override
    public void copyTextToClipData(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(KEY_COPY_TEXT_IN_PRESS, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(HomeActivity.this, getResources().getString(R.string.lb_traduction_copy), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displaySMSActivity() {
        Intent intent = new Intent(HomeActivity.this, SMSActivity.class);
        startActivityForResult(intent, VALUE_RECEIVE_SMS_TO_CONVERT);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void displayHistoryActivity() {
        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
        startActivityForResult(intent, VALUE_RECEIVE_HISTORY_TO_CONVERT);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void displayErrorOnEditText(String message){
        traduction_language_1.setError(message);
    }

    @Override
    public void displayDialogMessage(String title, String message) {
        homePresenter.displayDialogMessage(HomeActivity.this, title, message);
    }

    @Override
    public void stopTextToSpeechReading(){
        homePresenter.onStopTextToSpeechReading(textToSpeech_1);
        homePresenter.onStopTextToSpeechReading(textToSpeech_2);
    }

    @Override
    public void onInit(int i) {
        initTextToSpeech = i;
        homePresenter.initializeTextToSpeechLangDeparture(initTextToSpeech, textToSpeech_1, translateValues.get(KEY_LANGUAGE_DEPARTURE));
        homePresenter.initializeTextToSpeechLangArrival(initTextToSpeech, textToSpeech_2, translateValues.get(KEY_LANGUAGE_ARRIVAL));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homePresenter.onActivityDestroyed(textToSpeech_1);
        homePresenter.onActivityDestroyed(textToSpeech_2);
        homePresenter.canceledAsyntask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == VALUE_PERMISSION_REQUEST_READ_SMS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                homePresenter.displaySMSActivity();
            }
            else{
                String title = getResources().getString(R.string.lb_required_permission);
                String message = getResources().getString(R.string.lb_detail_required_sms_permission);
                homePresenter.displayDialogMessage(HomeActivity.this, title, message);
            }
        }
    }
}
