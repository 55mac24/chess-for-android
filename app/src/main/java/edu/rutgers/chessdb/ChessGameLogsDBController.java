package edu.rutgers.chessdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import edu.rutgers.chessdb.ChessGameLogsContract.*;
import java.util.ArrayList;


public class ChessGameLogsDBController {


    public void addGameToStorage(String title, String date, ArrayList<String> moves, Context context){
        ChessGameLogsDBHelper db_helper = new ChessGameLogsDBHelper(context);
        SQLiteDatabase db = db_helper.getWritableDatabase();
        for(int i = 0; i < moves.size(); i++){
            ContentValues values = new ContentValues();
            String move = moves.get(i);
            values.put(ChessEntry.COLUMN_NAME_TITLE, title);
            values.put(ChessEntry.COLUMN_NAME_DATE, date);
            values.put(ChessEntry.COLUMN_NAME_MOVE, move);
            values.put(ChessEntry.COLUMN_NAME_PLAYED, i);
            long newRowId = db.insert(ChessEntry.TABLE_NAME, null, values);
        }
    }
    public ArrayList<ChessGameParameters> readGamesFromStorage(Context context){
        ChessGameLogsDBHelper db_helper = new ChessGameLogsDBHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String[] projection = {
                ChessEntry.COLUMN_NAME_TITLE,
                ChessEntry.COLUMN_NAME_DATE
        };
        String selection = ChessEntry.COLUMN_NAME_TITLE;
        String[] selectionArgs = {};
        String sortOrder = ChessEntry.COLUMN_NAME_TITLE + " ASC";
        Cursor cursor = db.query(
                ChessEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<ChessGameParameters> games = new ArrayList<>();
        while(cursor.moveToNext()){
            String gameName = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_TITLE)
            );
            String gameDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_DATE)
            );
            games.add(new ChessGameParameters(gameName, gameDate));
        }
        cursor.close();
        return games;
    }
    public boolean doesListContainGame(ArrayList<ChessGameParameters> games, String title){
        for(ChessGameParameters game: games){
            System.out.println(game.getGameName()+"22222");
            if(game.getGameName().equals(title)){
                return true;
            }
        }
        return false;
    }
    public ChessGameParameters getGameFromList(ArrayList<ChessGameParameters> games, String title){
        for(ChessGameParameters game: games){
            if(game.getGameName().equals(title)){
                return game;
            }
        }
        return null;
    }
    public ChessGameParameters readMovesFromGameInStorage(String title, Context context){
        ChessGameLogsDBHelper db_helper = new ChessGameLogsDBHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String[] projection = {
                ChessEntry.COLUMN_NAME_TITLE,
                ChessEntry.COLUMN_NAME_DATE,
                ChessEntry.COLUMN_NAME_MOVE,
                ChessEntry.COLUMN_NAME_PLAYED
        };
        String selection = ChessEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { title };
        String sortOrder = ChessEntry.COLUMN_NAME_PLAYED + " ASC";
        Cursor cursor = db.query(
                ChessEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<ChessGameParameters> games = new ArrayList<>();
        while(cursor.moveToNext()){
            String gameName = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_TITLE)
            );
            String gameDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_DATE)
            );
            String gameMove = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_MOVE)
            );
            String gameMovePlayed = cursor.getString(
                    cursor.getColumnIndexOrThrow(ChessEntry.COLUMN_NAME_PLAYED)
            );
            ChessGameParameters addGame;
            if(!doesListContainGame(games, title)){
                addGame = new ChessGameParameters(gameName, gameDate);

            }else{
                addGame = getGameFromList(games, title);
            }
            addGame.addGameMove(gameMove, gameMovePlayed);
            games.add(addGame);
        }
        cursor.close();
        return getGameFromList(games, title);
    }
}
