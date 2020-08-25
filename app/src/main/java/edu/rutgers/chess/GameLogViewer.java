package edu.rutgers.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import edu.rutgers.chessdb.ChessGameLogsDBController;
import edu.rutgers.chessdb.ChessGameParameters;

public class GameLogViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelog_viewer);
    }
    public void getSpecificGameMoves(View view){
        ChessGameLogsDBController db = new ChessGameLogsDBController();
        ChessGameParameters game = db.readMovesFromGameInStorage("temp1", getApplicationContext());
        ArrayList<String> game_moves = game.getGameMoves();
        outputGameMoves(game_moves);
    }
    public void outputGameMoves(ArrayList<String> game_moves){
        System.out.println("########## GAME MOVE HISTORY ##########");
        for(int i = 0; i < game_moves.size(); i++){
            System.out.println(game_moves.get(i));
        }
        System.out.println("########## END OF MOVE HISTORY ##########");
    }
}
