package lveapp.traducteur.Presenter.Home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;

import lveapp.traducteur.Model.DAOHistory;
import lveapp.traducteur.Model.History;
import lveapp.traducteur.Model.Translation;
import lveapp.traducteur.Presenter.Common.CommonPresenter;
import lveapp.traducteur.R;
import lveapp.traducteur.View.Activities.SMSActivity;
import lveapp.traducteur.View.Interfaces.HomeView;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_COPY_TEXT_IN_PRESS;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_INCREMENT_LINE_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_LANGUAGE_ARRIVAL;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_LANGUAGE_DEPARTURE;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_TEXT_TO_TRANSLATE;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_TOTAL_LINE_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_GOOGLE_VOICE_RETURN_DATA;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_RECEIVE_HISTORY_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.VALUE_RECEIVE_SMS_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.saveDataInSharePreferences;

/**
 * Created by Maranatha on 13/01/2018.
 */

public class HomePresenter implements HomeView.IPresenter, HomeView.ILoadTranslation {
    // Ref HomeActivity interface
    private HomeView.IHome iHome;
    // Ref TranslateAsyntask
    private TranslateAsyntask translateAsyntask;
    // Ref retrieving text from Asyntask
    private String languageDeparture;
    private String messageToTranslate;
    private String languageArrival;
    private String messageTranslated;
    private String translatedText = "";
    // Ref separation of translated text
    private Hashtable<Integer, String> linesToTranslate;

    // Constructor
    public HomePresenter(HomeView.IHome iHome) {
        this.iHome = iHome;
    }

    /**
     * Load translate data
     * @param context
     * @param intent
     */
    @Override
    public void loadTranslateData(Context context, Intent intent) {
        try {
            iHome.initialize();
            iHome.events();
            iHome.enableAllWidgets(false);
            iHome.enableCompareButton(true);
            iHome.enableSpinners(true);
            iHome.cleanTextButtonVisibility(View.GONE);
            // Load language list
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, CommonPresenter.languageDetail);
            iHome.loadLanguageList(adapter);
            // Verify if it's load by share
            if(intent != null){
                String action = intent.getAction();
                String type = intent.getType();
                // Single send : Retrieve share data text
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if ("text/plain".equals(type)) {
                        iHome.feedEditText(intent.getStringExtra(Intent.EXTRA_TEXT));
                        iHome.simulateTranslateButtonClick();
                    }
                }
            }
        }
        catch (Exception ex){}
    }


    @Override
    public void loadTextToTranslate(Context context, String text, int requestCode){
        try {
            switch (requestCode){
                case VALUE_RECEIVE_SMS_TO_CONVERT:
                    iHome.feedEditText(text);
                    iHome.simulateTranslateButtonClick();
                    break;
                case VALUE_RECEIVE_HISTORY_TO_CONVERT:
                    int historyID = Integer.parseInt(text);
                    DAOHistory daoHistory = new DAOHistory(context);
                    History history = daoHistory.getById(historyID);
                    languageDeparture = history.getLangDeparture();
                    languageArrival = history.getLangArrivale();
                    messageToTranslate = history.getMessageDeparture();
                    messageTranslated =  history.getMessageArrivale();
                    iHome.feedHistoryTranslated(CommonPresenter.getPositionByDetailLang(languageDeparture), messageToTranslate, CommonPresenter.getPositionByDetailLang(languageArrival), messageTranslated);
                    iHome.enableAllWidgets(true);
                    iHome.cleanTextButtonVisibility(View.VISIBLE);
                    // Save translation
                    boolean success = daoHistory.insertData(languageDeparture, languageArrival, messageToTranslate, messageTranslated);
                    if(!success){
                        Toast.makeText(context, context.getResources().getString(R.string.lb_save_error), Toast.LENGTH_LONG).show();
                    }
                    break;
                case VALUE_GOOGLE_VOICE_RETURN_DATA:
                    iHome.feedEditText(text);
                    iHome.simulateTranslateButtonClick();
                    break;
            }
        }
        catch (Exception ex){}
    }

    @Override
    public void displayGoogleVoiceDialog(String langDepature) {
        Locale localeLang = CommonPresenter.getLocalLanguageBy(langDepature);
        iHome.displayGoogleVoiceDialog(localeLang);
    }

    /**
     * If user clicks on enter button on mobile touch
     * @param actionId
     * @param event
     * @param context
     * @param values
     */
    @Override
    public void retrieveUserAction(int actionId, KeyEvent event, Context context, Hashtable<String, String> values) {
        try {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                // Hidde beacause Editext is multiline
                //translationTraitement(context, values);
                //iHome.enableSpinners(true);
            }
        }
        catch (Exception ex){}
    }

    @Override
    public void retrieveUserAction(View view, Hashtable<String, String> values) {
        try {
            switch (view.getId()){
                case R.id.btn_translate:
                    // Values are not null and key KEY_TEXT_TO_TRANSLATE exists
                    if (values != null && values.containsKey(KEY_TEXT_TO_TRANSLATE)) {
                        iHome.cleanTranslatedText();
                        // Verify if text exists in the history table
                        String textToTranslate = values.get(KEY_TEXT_TO_TRANSLATE);
                        DAOHistory daoHistory = new DAOHistory(view.getContext());
                        ArrayList<History> histories = daoHistory.getAllDataByTextToTranslate(textToTranslate);
                        if(histories != null && histories.size() > 0){
                            // Vérify languages
                            int historyId = histories.get(0).getId();
                            String langDeparture = histories.get(0).getLangDeparture();
                            String langArrival = histories.get(0).getLangArrivale();
                            //--
                            if(langDeparture.equalsIgnoreCase(values.get(KEY_LANGUAGE_DEPARTURE)) && langArrival.equalsIgnoreCase(values.get(KEY_LANGUAGE_ARRIVAL))){
                                loadTextToTranslate(view.getContext(), ""+historyId, VALUE_RECEIVE_HISTORY_TO_CONVERT);
                                return;
                            }
                        }

                        // Connexion exists
                        if(CommonPresenter.isMobileConnected(view.getContext())) {
                            translationTraitement(view.getContext(), values);
                        }
                        else{
                            // No connection
                            String title = view.getContext().getString(R.string.lb_no_connection);
                            String message = view.getContext().getString(R.string.lb_detail_no_connection);
                            iHome.displayDialogMessage(title, message);
                        }
                    }
                    else{
                        // Translate is null or empty
                        iHome.displayErrorOnEditText(view.getContext().getString(R.string.lb_field_required));
                    }
                    break;

                // Change languages position
                case R.id.btn_compare:
                    String langDeparture = values.get(KEY_LANGUAGE_DEPARTURE);
                    String langArrival = values.get(KEY_LANGUAGE_ARRIVAL);
                    iHome.changeLanguagePosition(Integer.parseInt(langArrival), Integer.parseInt(langDeparture));
                    break;

                // Read text from language departure
                case R.id.btn_read_1:
                    iHome.readTextFromTextToSpeechLangDeparture();
                    break;

                // Read text from language arrival
                case R.id.btn_read_2:
                    iHome.readTextFromTextToSpeechLangArrival();
                    break;

                // Share departure traduction text
                case R.id.btn_share_1:
                    if (values != null && values.containsKey(KEY_TEXT_TO_TRANSLATE)) {
                        String title = view.getContext().getResources().getString(R.string.lb_share_width);
                        String message = values.get(KEY_TEXT_TO_TRANSLATE);
                        CommonPresenter.shareApplication(view.getContext(), title, message);
                    }
                    break;

                // Share arrival traduction text
                case R.id.btn_share_2:
                    if (values != null && values.containsKey(KEY_TEXT_TO_TRANSLATE)) {
                        String title = view.getContext().getResources().getString(R.string.lb_share_width);
                        String message = values.get(KEY_TEXT_TO_TRANSLATE);
                        CommonPresenter.shareApplication(view.getContext(), title, message);
                    }
                    break;

                // Copy departure text in press
                case R.id.btn_content_copy_1:
                    if (values != null && values.containsKey(KEY_COPY_TEXT_IN_PRESS)) {
                        iHome.copyTextToClipData(values.get(KEY_COPY_TEXT_IN_PRESS));
                    }
                    break;

                // Copy arrival text in press
                case R.id.btn_content_copy_2:
                    if (values != null && values.containsKey(KEY_COPY_TEXT_IN_PRESS)) {
                        iHome.copyTextToClipData(values.get(KEY_COPY_TEXT_IN_PRESS));
                    }
                    break;
            }
        }
        catch (Exception ex){}
    }

    /**
     * Translation traitement
     * @param context
     * @param values
     */
    private void translationTraitement(Context context, Hashtable<String, String> values){
        try {
            final int minLines = 100;
            final int maxLines = 500;
            messageToTranslate = values.get(KEY_TEXT_TO_TRANSLATE);
            languageDeparture = values.get(KEY_LANGUAGE_DEPARTURE);
            languageArrival = values.get(KEY_LANGUAGE_ARRIVAL);
            // Text to translate is not null or empty
            if(messageToTranslate != null && messageToTranslate.trim().length() > 0) {
                // Languages are not the sames
                if(!languageDeparture.equalsIgnoreCase(languageArrival)) {
                    linesToTranslate = new Hashtable<>();
                    linesToTranslate.put(0, messageToTranslate);
                    // Retrieve abreviations
                    String langAbrevDeparture = CommonPresenter.getLangAbrevBy(languageDeparture);
                    String langAbrevArrival = CommonPresenter.getLangAbrevBy(languageArrival);
                    String urlws = CommonPresenter.linkTranslate.replace("{LANG_1}", langAbrevDeparture).replace("{LANG_2}", langAbrevArrival);
                    //Log.i("TAG_TEXT_LENGTH", "textToTranslate.length() = "+textToTranslate.length());
                    //Log.i("TAG_URL", "URL = "+urlws);
                    // If number of characters excedeed 500
                    if(messageToTranslate.length() > maxLines){
                        String line = "";
                        int total = 0;
                        String lines[] = messageToTranslate.split(" ");
                        int compteur=0;
                        for(int i=0; i<lines.length; i++){
                            total += lines[i].length();
                            line += lines[i]+" ";
                            if((total > minLines && total < maxLines) || (i == lines.length-1)){
                                if(lines[i].trim().endsWith("...") ||
                                        lines[i].trim().endsWith(".") ||
                                        lines[i].trim().endsWith(":") ||
                                        lines[i].trim().endsWith(";") ||
                                        lines[i].trim().endsWith("!") ||
                                        lines[i].trim().endsWith(",")){
                                    linesToTranslate.put(compteur, line);
                                    //Log.i("TAG_TEXT_BOUCLE", compteur+" : "+line);
                                    compteur++;
                                    line = "";
                                    total = 0;
                                }
                            }
                        }
                    }
                    // Initialize share preferences
                    saveDataInSharePreferences(context, KEY_TOTAL_LINE_TO_CONVERT, ""+linesToTranslate.size());
                    saveDataInSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT, "0");
                    // Lock widgets...
                    iHome.enableAllWidgets(false);
                    iHome.enableTranslateButton(false);
                    iHome.enableTextToTranslate(false);
                    iHome.progressBarVisibility(View.VISIBLE);
                    // Load traduction
                    translateAsyntask = new TranslateAsyntask();
                    translateAsyntask.initialization(context, this, linesToTranslate, languageDeparture, languageArrival, urlws);
                    translateAsyntask.execute();
                }
                else{
                    // Languages are the same
                    String title = context.getResources().getString(R.string.lb_lang_same);
                    String message = context.getResources().getString(R.string.lb_detail_lang_same);
                    iHome.displayDialogMessage(title, message);
                }
            }
            else{
                // Translate is null or empty
                iHome.displayErrorOnEditText(context.getResources().getString(R.string.lb_field_required));
            }
        }
        catch (Exception ex){}
    }

    @Override
    public void displayDialogMessage(Context context, String title, String message) {
        CommonPresenter.showMessage(context, title, message, false);
    }

    @Override
    public void retrieveTextChangeAction(String textToTranslate, String textTranslated){
        if(textTranslated != null){
            if(textToTranslate != null){
                iHome.enableTopWidgets(!(textToTranslate.length()==0) && !(textTranslated.length()==0));
                iHome.enableBottomWidgets(!(textToTranslate.length()==0) && !(textTranslated.length()==0));
                // If text to translate is empty and traslated text is not empty
                if((textToTranslate.length()==0) && !(textTranslated.length()==0)){
                    iHome.simulateCleanTextButtonClick();
                    iHome.stopTextToSpeechReading();
                    //Log.i("TAG_TEXT_CLEAN", "textTranslated = "+textTranslated);
                    //Log.i("TAG_TEXT_CLEAN", "textToTranslate = "+textToTranslate);
                }
            }
        }
    }

    @Override
    public void initializeTextToSpeechLangDeparture(int returnCode, TextToSpeech toSpeech, String langDetail){
        try {
            if (returnCode == TextToSpeech.SUCCESS) {

                int result = toSpeech.setLanguage(CommonPresenter.getLocalLanguageBy(langDetail));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Language is not supported
                }
                else {
                    // Language is not supported
                }

            }
            else {
                // Initilization Failed!
            }
        }
        catch (Exception ex){
            // Error
        }
    }

    @Override
    public void initializeTextToSpeechLangArrival(int returnCode, TextToSpeech toSpeech, String langDetail){
        try {
            if (returnCode == TextToSpeech.SUCCESS) {

                int result = toSpeech.setLanguage(CommonPresenter.getLocalLanguageBy(langDetail));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Language is not supported
                }
                else {
                    // Language is not supported
                }

            }
            else {
                // Initilization Failed!
            }
        }
        catch (Exception ex){}
    }

    /**
     * TextToSpeech : Retrieve broadcast receiver
     * @param intent
     */
    @Override
    public void retrieveBroadcastReceiver(Intent intent){
        if(intent.getAction().equalsIgnoreCase(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)){
            // Text is finished to read
        }
    }

    @Override
    public void clearAllTranslatedText(){
        iHome.cleanAllTranslatedText();
        iHome.cleanTextButtonVisibility(View.GONE);
    }

    @Override
    public void onTranslating(String textTranslated) {
        translatedText += textTranslated+" ";
        Log.i("TAG_TEXT_PART", translatedText);
    }

    @Override
    public void onTranslatedFinished(Context context, String textTranslated) {
        try {
            translatedText += textTranslated;
            Log.i("TAG_TEXT_FINISHED", translatedText);
            iHome.feedTranslate(translatedText.trim());
            iHome.enableAllWidgets(true);
            iHome.enableTranslateButton(true);
            iHome.enableTextToTranslate(true);
            iHome.progressBarVisibility(View.GONE);
            iHome.cleanTextButtonVisibility(View.VISIBLE);
            // Empty text value
            messageTranslated = translatedText;
            translatedText = "";
            // Save translation
            DAOHistory daoHistory = new DAOHistory(context);
            boolean success = daoHistory.insertData(languageDeparture, languageArrival, messageToTranslate, messageTranslated);
            if(!success){
                Toast.makeText(context, context.getResources().getString(R.string.lb_save_error), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){}
    }

    @Override
    public void onLoadError() {
        try {
            iHome.enableAllWidgets(true);
            iHome.enableTranslateButton(true);
            iHome.enableTextToTranslate(true);
            iHome.cleanTextButtonVisibility(View.VISIBLE);
            iHome.progressBarVisibility(View.GONE);
        }
        catch (Exception ex){}
    }

    /**
     * On Stop text to speach
     * @param tts
     */
    @Override
    public void onStopTextToSpeechReading(TextToSpeech tts){
        try {
            if (tts != null) {
                tts.stop();
            }
        }
        catch (Exception ex){}
    }

    /**
     * On Activity desctroyed
     * @param tts  onStopTextToSpeechReading
     */
    @Override
    public void onActivityDestroyed(TextToSpeech tts){
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }
        catch (Exception ex){}
    }

    /**
     * Canceled Asyntask
     */
    @Override
    public void canceledAsyntask(){
        try {
            if(translateAsyntask != null){
                translateAsyntask.cancel(true);
            }
        }
        catch (Exception ex){}
    }

    /**
     * Permission to read SMS
     */
    @Override
    public void checkPermissionToReadSMS(Context context){
        if(!CommonPresenter.askPermissionToReadSMS(context)){
            iHome.displaySMSActivity();
        }
    }

    /**
     * Display SMS Activity
     */
    @Override
    public void displaySMSActivity(){
        iHome.displaySMSActivity();
    }

    /**
     * Display History Activity
     */
    @Override
    public void displayHistoryActivity(){
        iHome.displayHistoryActivity();
    }

    /**
     * Share application
     * @param context
     */
    @Override
    public void shareApplication(Context context) {
        String title = context.getResources().getString(R.string.lb_share_width);
        String message = context.getResources().getString(R.string.lb_message_share_app)+"\n\n"+context.getResources().getString(R.string.lb_url_play_store);
        CommonPresenter.shareApplication(context, title, message);
    }
}
