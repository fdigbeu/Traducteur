package lveapp.traducteur.View.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.Hashtable;

/**
 * Created by Maranatha on 13/01/2018.
 */

public class HomeView {
    public interface IHome{
        public void initialize();
        public void events();
        public void enableAllWidgets(boolean enable);
        public void enableCompareButton(boolean enable);
        public void enableSpinners(boolean enable);
        public void enableTopWidgets(boolean enable);
        public void enableBottomWidgets(boolean enable);
        public void enableTextToTranslate(boolean enable);
        public void enableTranslateButton(boolean enable);
        public void cleanTextButtonVisibility(int visibility);
        public void cleanAllTranslatedText();
        public void feedTranslate(String text_2);
        public void progressBarVisibility(int visibility);
        public void loadLanguageList(ArrayAdapter<String> adapter);
        public void feedEditText(String text);
        public void displayErrorOnEditText(String message);
        public void displayDialogMessage(String title, String message);
        public void stopTextToSpeechReading();
        public void changeLanguagePosition(int position_1, int position_2);
        public void readTextFromTextToSpeechLangDeparture();
        public void readTextFromTextToSpeechLangArrival();
        public void simulateTranslateButtonClick();
        public void simulateCleanTextButtonClick();
        public void copyTextToClipData(String text);
        public void displaySMSActivity();
        public void displayHistoryActivity();
    }

    public interface IPresenter{
        public void loadTranslateData(Context context, Intent intent);
        public void retrieveUserAction(View view, Hashtable<String, String> values);
        public void displayDialogMessage(Context context, String title, String message);
        public void initializeTextToSpeechLangDeparture(int returnCode, TextToSpeech toSpeech, String langDetail);
        public void initializeTextToSpeechLangArrival(int returnCode, TextToSpeech toSpeech, String langDetail);
        public void retrieveBroadcastReceiver(Intent intent);
        public void onStopTextToSpeechReading(TextToSpeech tts);
        public void onActivityDestroyed(TextToSpeech tts);
        public void canceledAsyntask();
        public void checkPermissionToReadSMS(Context context);
        public void clearAllTranslatedText();
        public void retrieveTextChangeAction(String textToTranslate, String textTranslated);
        public void retrieveUserAction(int actionId, KeyEvent event, Context context, Hashtable<String, String> values);
        public void displaySMSActivity();
        public void displayHistoryActivity();
        public void shareApplication(Context context);
        public void loadTextToTranslate(Context context, String text, int requestCode);
    }

    public interface ILoadTranslation{
        public void onTranslating(String textTranslated);
        public void onTranslatedFinished(Context context, String textTranslated);
        public void onLoadError();
    }
}
