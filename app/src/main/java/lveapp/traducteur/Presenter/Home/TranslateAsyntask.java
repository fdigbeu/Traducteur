package lveapp.traducteur.Presenter.Home;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import lveapp.traducteur.Model.Translation;
import lveapp.traducteur.View.Interfaces.HomeView;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_INCREMENT_LINE_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.KEY_TOTAL_LINE_TO_CONVERT;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.getDataFromSharePreferences;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.saveDataInSharePreferences;

/**
 * Created by Maranatha on 11/01/2018.
 */

public class TranslateAsyntask extends AsyncTask<Void, Void, Translation> {

    private String urlws;
    private Context context;
    private HttpURLConnection urlConnection;
    private HomeView.ILoadTranslation iLoadTranslation;
    private Hashtable<Integer, String> linesToTranslate;
    private Translation translation;
    private String langDeparture;
    private String langArrival;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Translation doInBackground(Void... voids) {
        StringBuilder responseHttp =  new StringBuilder();

        try {
            int currentIncrement = Integer.parseInt(getDataFromSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT));
            int nextIncrement = currentIncrement+1;
            saveDataInSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT, ""+nextIncrement);
            String textToTranslate = linesToTranslate.get(currentIncrement).trim();
            String textToTranslateEncode = URLEncoder.encode(textToTranslate.trim(), "UTF8");
            String urlToSend = urlws.replace("{SEARCH}", textToTranslateEncode.trim());
            URL url = new URL(urlToSend.trim());
            Log.i("TAG_ASYNTASK_TEXT", currentIncrement+" : "+textToTranslate.trim());
            Log.i("TAG_ASYNTASK_URL", currentIncrement+" : "+urlToSend.trim());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null){
                responseHttp.append(line);
            }

        } catch (Exception e) {
            // If Error exist
            iLoadTranslation.onLoadError();
        }
        finally {
            urlConnection.disconnect();
        }
        //--
        try {
            String JSONString = responseHttp.toString();
            JSONObject JSON = new JSONObject(JSONString);
            JSONObject items = JSON.getJSONObject("responseData");

            String translatedText = items.getString("translatedText");
            translation = new Translation(translatedText, langArrival);
        }
        catch (JSONException e) {
            iLoadTranslation.onLoadError();
        }

        return translation;
    }

    @Override
    protected void onPostExecute(Translation translation) {
        super.onPostExecute(translation);
        //--
        if(translation != null && !translation.getText().equalsIgnoreCase("null")){
            int maxIncrement = Integer.parseInt(getDataFromSharePreferences(context, KEY_TOTAL_LINE_TO_CONVERT))-1;
            int nextIncrement = Integer.parseInt(getDataFromSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT));
            if(nextIncrement <= maxIncrement){
                // Feed translating text
                iLoadTranslation.onTranslating(translation.getText());
                //--
                TranslateAsyntask translateAsyntask = new TranslateAsyntask();
                translateAsyntask.initialization(context, iLoadTranslation, linesToTranslate, langDeparture, langArrival, urlws);
                translateAsyntask.execute();
                return;
            }
            // All text is finished to be translated
            iLoadTranslation.onTranslatedFinished(translation.getText());
        }
        else{
            // If translation.getText() == "null" request again
            int nextIncrement = Integer.parseInt(getDataFromSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT));
            saveDataInSharePreferences(context, KEY_INCREMENT_LINE_TO_CONVERT, ""+(nextIncrement-1));
            TranslateAsyntask translateAsyntask = new TranslateAsyntask();
            translateAsyntask.initialization(context, iLoadTranslation, linesToTranslate, langDeparture, langArrival, urlws);
            translateAsyntask.execute();
            Log.i("TAG_OBJECT", "translation.getText() equals null");
        }
    }

    public void initialization(Context context, HomeView.ILoadTranslation iLoadTranslation, Hashtable<Integer, String> linesToTranslate, String langDeparture, String langArrival, String urlws){
        this.context = context;
        this.iLoadTranslation = iLoadTranslation;
        this.urlws = urlws;
        this.linesToTranslate = linesToTranslate;
        this.langDeparture = langDeparture;
        this.langArrival = langArrival;
    }
}
