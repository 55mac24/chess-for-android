package edu.rutgers.chessdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.rutgers.chessdb.ChessGameLogsContract.*;
public class ChessGameLogsDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ChessGameLogs.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ChessEntry.TABLE_NAME + " (" +
                    ChessEntry.COLUMN_NAME_TITLE + " TEXT," +
                    ChessEntry.COLUMN_NAME_DATE + " TEXT," +
                    ChessEntry.COLUMN_NAME_MOVE + " TEXT," +
                    ChessEntry.COLUMN_NAME_PLAYED + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " +ChessEntry.TABLE_NAME;


    public ChessGameLogsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
