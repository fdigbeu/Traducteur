package lveapp.traducteur.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import lveapp.traducteur.Presenter.Common.CommonPresenter;

import static lveapp.traducteur.Presenter.Common.CommonPresenter.DATABASE_NAME;
import static lveapp.traducteur.Presenter.Common.CommonPresenter.DATABASE_VERSION;

/**
 * Created by Maranatha on 16/01/2018.
 */

public class DAOHistory extends SQLiteOpenHelper {
    // Table name
    private static final String TABLE_NAME = "T_HISTORY";
    // Table fields
    private static final String COL_1 = "ID";
    private static final String COL_2 = "LANG_DEPARTURE";
    private static final String COL_3 = "LANG_ARRIVALE";
    private static final String COL_4 = "MSG_DEPARTURE";
    private static final String COL_5 = "MSG_ARRIVALE";
    private static final String COL_6 = "DATE";
    // Table fields number
    private static final int COL_NUM_1 = 0;
    private static final int COL_NUM_2 = 1;
    private static final int COL_NUM_3 = 2;
    private static final int COL_NUM_4 = 3;
    private static final int COL_NUM_5 = 4;
    private static final int COL_NUM_6 = 5;

    // Constructor
    public DAOHistory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public ArrayList<History> getAllData(){
        ArrayList<History> histories = new ArrayList<>();
        Cursor cursor = getAllCursorData();
        if(cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                histories.add(new History(cursor.getInt(COL_NUM_1), cursor.getString(COL_NUM_2), cursor.getString(COL_NUM_3), cursor.getString(COL_NUM_4), cursor.getString(COL_NUM_5), cursor.getString(COL_NUM_6)));
            }
        }
        return histories;
    }

    public ArrayList<History> getAllDataByTextToTranslate(String textToTranslate){
        ArrayList<History> histories = new ArrayList<>();
        Cursor cursor = getAllCursorDataByTextToTranslate(textToTranslate);
        if(cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                histories.add(new History(cursor.getInt(COL_NUM_1), cursor.getString(COL_NUM_2), cursor.getString(COL_NUM_3), cursor.getString(COL_NUM_4), cursor.getString(COL_NUM_5), cursor.getString(COL_NUM_6)));
            }
        }
        return histories;
    }

    public boolean insertData(String langDeparture, String langArrivale, String messageDeparture, String messageArrivale){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, langDeparture.replace("'", "''"));
        contentValues.put(COL_3, langArrivale.replace("'", "''"));
        contentValues.put(COL_4, messageDeparture.replace("'", "''"));
        contentValues.put(COL_5, messageArrivale.replace("'", "''"));
        contentValues.put(COL_6, CommonPresenter.getStringDateDayMonthYear());
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return (result != -1);
    }

    public History getById(int id){
        History history = null;
        Cursor cursor = getCursorDataById(id);
        if(cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                history = new History(cursor.getInt(COL_NUM_1), cursor.getString(COL_NUM_2), cursor.getString(COL_NUM_3), cursor.getString(COL_NUM_4), cursor.getString(COL_NUM_5), cursor.getString(COL_NUM_6));
            }
        }
        return history;
    }

    public Integer deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_1+" = ?", new String[]{""+id});
        return result;
    }

    public boolean updateData(int id, String langDeparture, String langArrivale, String messageDeparture, String messageArrivale){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, langDeparture.replace("'", "''"));
        contentValues.put(COL_3, langArrivale.replace("'", "''"));
        contentValues.put(COL_4, messageDeparture.replace("'", "''"));
        contentValues.put(COL_5, messageArrivale.replace("'", "''"));
        int result = db.update(TABLE_NAME, contentValues, COL_1+ " = ?", new String[]{""+id});
        return (result > 0);
    }

    private Cursor getAllCursorData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+COL_1+" DESC", null);
        return res;
    }

    private Cursor getCursorDataById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COL_1+" = "+id, null);
        return res;
    }

    private Cursor getAllCursorDataByTextToTranslate(String textToTranslate){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COL_4+" LIKE '"+textToTranslate.replace("'", "''")+"'", null);
        return res;
    }
}
