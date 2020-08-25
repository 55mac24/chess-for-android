package edu.rutgers.chessdb;

import java.util.ArrayList;

public class ChessGameParameters {
    private String gameName;
    private String gameDate;
    private ArrayList<String> moves;
    public ChessGameParameters(String gameName, String gameDate){
        this.gameName = gameName;
        this.gameDate = gameDate;
        moves = new ArrayList<>();
    }

    public String getGameName() {
        return this.gameName;
    }
    public String getGameDate(){
        return this.gameDate;
    }

    public void addGameMove(String move, String playedAt){
        int playedAtIndex = Integer.parseInt(playedAt);
        moves.add(playedAtIndex,move);
    }
    public ArrayList<String> getGameMoves(){
        return this.moves;
    }
}
