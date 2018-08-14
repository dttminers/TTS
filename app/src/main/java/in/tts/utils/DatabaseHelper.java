package in.tts.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.List;

import in.tts.model.PdfModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "read_it_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            // create notes table
            db.execSQL(PdfModel.CREATE_TABLE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + PdfModel.TABLE_NAME);

            // Create tables again
            onCreate(db);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public long insertNote(String name, String created) {
        long id = -1;
        try {
            // get writable database as we want to write data
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            // `id` and `timestamp` will be inserted automatically.
            // no need to add them
            values.put(PdfModel.COLUMN_NAME, name);
            values.put(PdfModel.COLUMN_DATE, created);

            Cursor cursor = db.query(PdfModel.TABLE_NAME,
                    new String[]{PdfModel.COLUMN_ID, PdfModel.COLUMN_NAME, PdfModel.COLUMN_DATE, PdfModel.COLUMN_TIMESTAMP},
                    PdfModel.COLUMN_NAME + "=?",
                    new String[]{String.valueOf(name)}, null, null, null, null);
            Log.d("TAG ", " cursor " + cursor.getCount());
            if (cursor.getCount() == 0) {
                 //insert row
                id = db.insert(PdfModel.TABLE_NAME, null, values);
            }
            cursor.close();
            // close db connection
            db.close();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }

        // return newly inserted row id
        return id;
    }

    public PdfModel getNote(long id) {
        try {
            // get readable database as we are not inserting anything
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(PdfModel.TABLE_NAME,
                    new String[]{PdfModel.COLUMN_ID, PdfModel.COLUMN_NAME, PdfModel.COLUMN_DATE, PdfModel.COLUMN_TIMESTAMP},
                    PdfModel.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null)
                cursor.moveToFirst();

            // prepare note object
            PdfModel pdfModel = new PdfModel(
                    cursor.getInt(cursor.getColumnIndex(PdfModel.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(PdfModel.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(PdfModel.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(PdfModel.COLUMN_TIMESTAMP)));

            // close the db connection
            cursor.close();

            return pdfModel;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            return null;
        }
    }

    public List<PdfModel> getAllNotes() {
        List<PdfModel> pdfModels = new ArrayList<>();
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + PdfModel.TABLE_NAME + " ORDER BY " +
                    PdfModel.COLUMN_TIMESTAMP + " DESC";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor1 = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    do {
                        PdfModel pdfModel = new PdfModel();
                        pdfModel.setId(cursor1.getInt(cursor1.getColumnIndex(PdfModel.COLUMN_ID)));
                        pdfModel.setName(cursor1.getString(cursor1.getColumnIndex(PdfModel.COLUMN_NAME)));
                        pdfModel.setCreatedOn(cursor1.getString(cursor1.getColumnIndex(PdfModel.COLUMN_DATE)));
                        pdfModel.setTimestamp(cursor1.getString(cursor1.getColumnIndex(PdfModel.COLUMN_TIMESTAMP)));

                        pdfModels.add(pdfModel);
                    } while (cursor1.moveToNext());
                }
            }

            // close db connection
            db.close();

            // return notes list

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);

        }
        return pdfModels;
    }

    public int getNotesCount() {
        try {
            String countQuery = "SELECT  * FROM " + PdfModel.TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);

            int count = cursor.getCount();
            cursor.close();


            // return count
            return count;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            return 0;
        }
    }

    public int updateNote(PdfModel pdfModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PdfModel.COLUMN_NAME, pdfModel.getName());
            values.put(PdfModel.COLUMN_DATE, pdfModel.getCreatedOn());

            // updating row
            return db.update(PdfModel.TABLE_NAME, values, PdfModel.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(pdfModel.getId())});
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            return 0;
        }
    }

    public void deleteNote(PdfModel note) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(PdfModel.TABLE_NAME, PdfModel.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
            db.close();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public void dropDatabase() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + PdfModel.TABLE_NAME);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }
}
