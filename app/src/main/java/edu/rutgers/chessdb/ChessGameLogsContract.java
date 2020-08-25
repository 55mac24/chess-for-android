package edu.rutgers.chessdb;

import android.provider.BaseColumns;

public final class ChessGameLogsContract {
    private ChessGameLogsContract(){}
    public static class ChessEntry implements BaseColumns{
        public static final String TABLE_NAME = "gamelogs";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MOVE = "move";
        public static final String COLUMN_NAME_PLAYED = "played";
    }

}
