package lveapp.traducteur.Presenter.Common;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import lveapp.traducteur.R;

/**
 * Created by Maranatha on 13/01/2018.
 */

public class CommonPresenter {
    // Ref database
    public static final String DATABASE_NAME = "TRADUCTEUR.DB";
    public static final int DATABASE_VERSION = 1;
    // Link translation
    public static final String linkTranslate = "http://mymemory.translated.net/api/get?q={SEARCH}&langpair={LANG_1}|{LANG_2}";
    // Language abreviation list
    public static final String[] languageAbrev = {"fr", "en", "it", "es", "de"};
    // Language detail list
    public static final String[] languageDetail = {"Français", "Anglais", "Italien", "Espagnole", "Allemand"};
    // key total line to convert and incrementation
    public static final String KEY_TOTAL_LINE_TO_CONVERT = "KEY_TOTAL_LINE_TO_CONVERT";
    public static final String KEY_INCREMENT_LINE_TO_CONVERT = "KEY_INCREMENT_LINE_TO_CONVERT";
    // value permission to read sms
    public static final int VALUE_PERMISSION_REQUEST_READ_SMS = 100;
    // Value and key to receive selected sms to translate
    public static final int VALUE_RECEIVE_SMS_TO_CONVERT = 2;
    public static final String KEY_RECEIVE_SMS_RETURN_DATA = "KEY_RECEIVE_SMS_RETURN_DATA";
    // Value to receive selected history to translate
    public static final int VALUE_RECEIVE_HISTORY_TO_CONVERT = 5;
    public static final String KEY_RECEIVE_HISTORY_RETURN_DATA = "KEY_RECEIVE_HISTORY_RETURN_DATA";
    // Ref key language
    public static final String KEY_TEXT_TO_TRANSLATE = "KEY_TEXT_TO_TRANSLATE";
    public static final String KEY_LANGUAGE_DEPARTURE = "KEY_LANGUAGE_DEPARTURE";
    public static final String KEY_LANGUAGE_ARRIVAL = "KEY_LANGUAGE_ARRIVAL";
    public static final String KEY_COPY_TEXT_IN_PRESS = "KEY_COPY_TEXT_IN_PRESS";

    public static Locale getLocalLanguageBy(String langDetail){
        Locale localeLang = null;
        if(langDetail.equalsIgnoreCase(languageDetail[0]))
            localeLang = Locale.FRANCE;
        if(langDetail.equalsIgnoreCase(languageDetail[1]))
            localeLang = Locale.ENGLISH;
        if(langDetail.equalsIgnoreCase(languageDetail[2]))
            localeLang = Locale.ITALIAN;
        if(langDetail.equalsIgnoreCase(languageDetail[3]))
            localeLang = new Locale("es", "ES");
        if(langDetail.equalsIgnoreCase(languageDetail[4]))
            localeLang = Locale.GERMAN;
        return localeLang;
    }

    /**
     * Get Language abreviation
     * @param langDetail
     * @return
     */
    public static String getLangAbrevBy(String langDetail){
        for (int i=0; i<languageDetail.length; i++){
            if(languageDetail[i].equalsIgnoreCase(langDetail)){
                return languageAbrev[i];
            }
        }
        return null;
    }

    /**
     * Get Language detail
     * @param langAbrev
     * @return
     */
    public static String getLangDetailBy(String langAbrev){
        for (int i=0; i<languageAbrev.length; i++){
            if(languageAbrev[i].equalsIgnoreCase(langAbrev)){
                return languageDetail[i];
            }
        }
        return null;
    }

    /**
     * Verify if connection exists
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context){
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType) return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Display simple message text
     * @param context
     * @param title
     * @param message
     */
    public static void showMessage(final Context context, String title, String message, final boolean closeActivity){
        Hashtable<String, Integer> resolutionEcran = resolutionEcran(context);
        int width = resolutionEcran.get("largeur");
        int height = resolutionEcran.get("hauteur");
        int imgWidth = width <= height ? width : height;
        int newWidth = (int)(imgWidth*0.75f);
        int newHeight = (int)(imgWidth*0.40f);

        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        dialog.getWindow().setLayout((int)(newWidth*1.30f), ActionBar.LayoutParams.WRAP_CONTENT);

        TextView titre = dialog.findViewById(R.id.title);
        TextView detaille = dialog.findViewById(R.id.msg_detail);

        titre.setText(title.toUpperCase());

        buildTextViewToHtmlData(detaille, message);

        Button button_close = dialog.findViewById(R.id.button_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(closeActivity){
                    ((Activity)context).finish();
                }
            }
        });

        dialog.show();
    }

    /**
     * Share application
     * @param context
     * @param title
     * @param message
     */
    public static void shareApplication(Context context, String title, String message)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, title));
    }

    /**
     * Save data in share preferences
     * @param context
     * @param key
     * @param contentData
     */
    public static void saveDataInSharePreferences(Context context, String key, String contentData){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, contentData);
            editor.commit(); // <=> editor.apply();
        }
        catch (Exception ex){}
    }

    /**
     * Retrieve data in share preferences
     * @param context
     * @param key
     * @return
     */
    public static String getDataFromSharePreferences(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * Remove data by its key in share preferences
     * @param context
     * @param key
     */
    public static void removeDataFromSharePreferences(Context context, String key){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.commit(); // <=> editor.apply();
        }
        catch (Exception ex){}
    }

    /**
     * Change format date : Year - Month - Day
     * @param date
     * @return
     */
    public static String changeFormatDate(String date){
        String dateReturn = null;
        if(date.contains("-")){
            dateReturn = date.split("-")[2].trim()+"/"+date.split("-")[1].trim()+"/"+date.split("-")[0].trim();
        }
        else{
            try {
                long longTime = Long.parseLong(date);
                dateReturn = DateFormat.format("dd/MM/yyyy", new Date(longTime)).toString();
            }
            catch (Exception ex){}
        }
        return dateReturn;
    }

    /**
     * Check permission to read SMS
     */
    public static boolean askPermissionToReadSMS(Context context){
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.READ_SMS},VALUE_PERMISSION_REQUEST_READ_SMS);
            return true;
        }
        return false;
    }

    /**
     * Display date of to day in string formate : Day-Month-Year
     * @return
     */
    public static String getStringDateDayMonthYear(){
        String formatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    /**
     * Display date of to day in string formate : Year-Month-Day
     * @return
     */
    public static String getStringDateYearMonthDay(){
        String formatDate = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    // Number of date between two dates
    public static long getNumberOfDate(String dateDepart, String dateRetour, String formatDate)
    {
        Date depart = null;
        Date retour = null;
        try
        {
            depart = new SimpleDateFormat(formatDate).parse(dateDepart);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            retour = new SimpleDateFormat(formatDate).parse(dateRetour);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        long nbJours = (depart.getTime()- retour.getTime())/86400000;
        //--
        return Math.abs(nbJours);
    }

    /**
     * Build text to html
     * @param textView
     * @param textValue
     */
    public static void buildTextViewToHtmlData(TextView textView, String textValue){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(textValue.replace("\n", "<br />"), Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            textView.setText(Html.fromHtml(textValue.replace("\n", "<br />")));
        }
    }

    /**
     * Reduce text length
     * @param text
     * @param maxLength
     * @return
     */
    public static String reduceLengthOfTheText(String text, int maxLength){
        try {
            int textLength = text.length();
            String mText = text.trim().replace("\n\n", "\n").trim();
            String textReduce = mText.substring(0, ((textLength > maxLength) ? maxLength : textLength));
            String textReturn = textReduce+((textLength > maxLength) ? "..." : "");
            return textReturn;
        }
        catch (Exception ex){
            return text;
        }
    }

    /**
     * Screen resolution
     * @param context
     * @return
     */
    private static Hashtable<String, Integer> resolutionEcran(Context context) {
        Hashtable<String, Integer> dimension = new Hashtable<>();
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        dimension.put("largeur", width);
        dimension.put("hauteur", height);
        return dimension;
    }
}
