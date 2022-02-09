package ba.etf.unsa.rma.klase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class DatabaseHelperKategorije extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "kategorije.db";
    public static final String TABLE_NAME = "kategorije_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "naziv";
    public static final String COL_3 = "idIkonice";


    public DatabaseHelperKategorije(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY, naziv TEXT, idIkonice TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String naziv, String idIkonice){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, naziv);
        contentValues.put(COL_3, idIkonice);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        }
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

}
