package edu.rutgers.chess;
import edu.rutgers.chessgame.*;
import edu.rutgers.dialogWindows.DecideToEndGameDialogFragment;
import edu.rutgers.dialogWindows.GameStatusDialogFragment;
import edu.rutgers.dialogWindows.PromotePieceDialogFragment;
import edu.rutgers.dialogWindows.SaveGameDialogFragment;
import edu.rutgers.dialogWindows.SaveGameStateDialogFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.rutgers.chessdb.*;
import edu.rutgers.recordmove.DatabaseHelper;
import edu.rutgers.recordmove.recorder;

public class TwoPlayerGame extends AppCompatActivity implements
        PromotePieceDialogFragment.PromotePieceDialogListener,
        DecideToEndGameDialogFragment.DecideToEndGameDialogListener,
        SaveGameStateDialogFragment.SaveGameStateDialogListener,
        SaveGameDialogFragment.SaveGameDialoglistener {

    TableLayout chessboardLayout ;
    ChessBoard board;

    ImageView selected_piece = null, selected_target_cell = null;

    boolean wasMoveUndone = false;

    View playChessMoveView = null;

    ArrayList<String> gameMoveHistory = new ArrayList<>();
    DatabaseHelper mybd;
    String[] replayinstruction;
    boolean replaymode=false;
    TextView Replaynote;
    int replaymove;
    int currentmove;
    /*
           Promote Piece Prompt for Draw/Resign
     */
    public void promptPiecePromotionDialog(){
        DialogFragment promotionDialog = new PromotePieceDialogFragment();
        promotionDialog.show(getSupportFragmentManager(), "Promote Piece");
    }
    @Override
    public void onDialogSelectionClick(DialogFragment dialog, String pieceToPromote){
        if(playChessMoveView != null){
            playChessMove(playChessMoveView, pieceToPromote);
            playChessMoveView = null;
        }else{
            System.err.println("null view");
        }
    }
    /*
           Game End Prompt for Draw/Resign
     */
    public void promptGameEndDialog(String title){

        DecideToEndGameDialogFragment gameEndDialog = new DecideToEndGameDialogFragment();
        gameEndDialog.setTitle(title);
        gameEndDialog.show(getSupportFragmentManager(), "End Game");
    }
    @Override
    public void onDialogYesClick(DialogFragment dialog){
        outputGameHistory();
        promptSaveGameDialog();
    }
    @Override
    public void onDialogNoClick(DialogFragment dialog){
        //continue playing game
    }
    /*
        Save game prompt
    */
    public void promptSaveGameDialog(){
        SaveGameStateDialogFragment saveGameEndDialog = new SaveGameStateDialogFragment();
        saveGameEndDialog.show(getSupportFragmentManager(), "End Game");
    }
    @Override
    public boolean onDialogSaveClick(DialogFragment dialog,String name){
        String title = name; //change to user inputted title — make sure its unique!
        String temp=recorder.ToStringInstruction(gameMoveHistory);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date_obj = new Date();
        String date = dateFormat.format(date_obj);
        ArrayList<String> namelist=mybd.getName();
        //System.out.println("1"+namelist);
        //System.out.println("3"+temp);
        if(name.compareTo("")==0||namelist.contains(name)||temp.length()<1){
            System.out.println("Save failed");
            return false;
        }
        mybd.insert(name,temp);
        namelist=mybd.getName();
        endGameSession();
        Intent intent = new Intent(this, GameLogMenu.class);
        startActivity(intent);
        return true;
    }
    @Override
    public void onDialogDiscardClick(DialogFragment dialog){
        endGameSession();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    /*
        Two Player Game Activity Methods
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game);
        board = new ChessBoard();
        updateChessBoard();
        mybd=new DatabaseHelper(this);
        Intent intent=getIntent();
        if(intent.hasExtra("instruction")){
            Replaynote=findViewById(R.id.note);
            Replaynote.setText("In replay mode, press move to replay the game");
            replayinstruction=recorder.getinstruction(intent.getStringExtra("instruction"));
            //System.out.println("110: "+replayinstruction[0]);
            replaymove=replayinstruction.length+1;
            replaymode=true;
            currentmove=0;
        }
    }
    MediaPlayer pieceMoveSound = null;
    public void updateChessBoard(){
        //System.out.println(recorder.ToStringInstruction(gameMoveHistory));
        chessboardLayout = (TableLayout)findViewById(R.id.main_chessboard);
        ChessPiece[][] chessboard = board.getChessBoard();

        for(int i = 0; i < chessboardLayout.getChildCount() - 1; i++){
            View childTableRow = chessboardLayout.getChildAt(i);
            if( childTableRow instanceof TableRow){
                TableRow row = (TableRow)childTableRow;
                for(int j = 0; j < row.getChildCount() - 1; j++){
                    ImageView chessboard_location_img = (ImageView) row.getChildAt(j);
                    String chess_piece = "empty";
                    if (chessboard[i][j].isActive()) {

                        chess_piece =  chessboard[i][j].toString().toLowerCase();

                    }

                    chessboard_location_img.setImageResource(getResources().getIdentifier(chess_piece, "drawable",getPackageName()));
                    if((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)){

                        chessboard_location_img.setBackgroundColor(Color.rgb(119,149,86)); // Dark Brown
                        //System.out.print("##");
                    } else{

                        chessboard_location_img.setBackgroundColor(Color.rgb(255,238,171)); // Light Beige
                        //System.out.print("  ");
                    }
                }
            }

        }
        //stopChessMoveSound();
    }
    public void endGameSession(){
        board = new ChessBoard();
        updateChessBoard();
        gameMoveHistory = new ArrayList<>();
        selected_piece = null;
        selected_target_cell = null;
    }
    public void outputMsgDialog(String title, String msg){
        GameStatusDialogFragment promptGameStatus = new GameStatusDialogFragment();
        promptGameStatus.setTitle(title);
        promptGameStatus.setMsg(msg);
        promptGameStatus.show(getSupportFragmentManager(), "Game Status");
    }

    public boolean checkIfPromotion(){
        if(selected_piece == null || selected_target_cell == null){
            return false;
        }
        String source_location = getResources().getResourceEntryName(selected_piece.getId());
        int[] source_board_index = board.convertChessLocationToBoardIndex(source_location);
        int source_x = source_board_index[0], source_y = source_board_index[1];
        String target_location = getResources().getResourceEntryName(selected_target_cell.getId());
        int[] target_board_index = board.convertChessLocationToBoardIndex(target_location);
        int target_x = target_board_index[0];
        ChessPiece[][] chessboard = board.getChessBoard();
        //System.out.println("Check Promotion: " + board.getMove() + " " + chessboard[source_x][source_y].getPiece());
        return chessboard[source_x][source_y].getPiece() == 'P'
                && ((target_x == 0 && board.getMove() % 2 == 0) ||
                (target_x == 7 && board.getMove() % 2 != 0));
    }

    public void undoLastChessMove(View view){
        //stopChessMoveSound();
        if(wasMoveUndone || board.getMove() < 1 || gameMoveHistory.size() == 0){

            return;
        }
        wasMoveUndone = true;
        gameMoveHistory.remove(gameMoveHistory.size()-1);
        board.undoLastMove();
        updateChessBoard();
        currentmove--;
    }
    public void setSelectedCellColor(ImageView view, boolean setColor){
        String location = getResources().getResourceEntryName(view.getId());
        int[] board_index = board.convertChessLocationToBoardIndex(location);
        int x = board_index[0], y = board_index[1];
        if(setColor){
            if((x % 2 == 0 && y % 2 != 0) || (x % 2 != 0 && y % 2 == 0)){

                view.setBackgroundColor(Color.rgb(174,193,51)); // Light Green Brown

            } else{

                view.setBackgroundColor(Color.rgb(238,240,117)); // Light Selected Yellow

            }
        }else{
            if((x % 2 == 0 && y % 2 != 0) || (x % 2 != 0 && y % 2 == 0)){

                view.setBackgroundColor(Color.rgb(119,149,86)); // Dark Green

            } else{

                view.setBackgroundColor(Color.rgb(255,238,171)); // Light Beige

            }
        }

    }
    public void resetSelectedCells(){
        if(selected_piece != null){
            setSelectedCellColor(selected_piece, false);
            selected_piece = null;
        }
        if(selected_target_cell != null){
            setSelectedCellColor(selected_target_cell, false);
            selected_target_cell = null;
        }
    }
    public void playRandomMove(View view){
        if(replaymode){
            replaymode = false;
            replaymove();
        }
        String isGameComplete = "", title = "Game Status", msg ="";
        boolean isValid = true;
        int game_status = board.makeAIDecision();
        if (game_status == board.check) {

            //System.out.println("Game Status: Check");
            isGameComplete = "check";

        } else if (game_status == board.checkmate) {

            //System.out.println("Game Status: Checkmate");
            isGameComplete = "checkmate";

        } else if (game_status == board.stalemate) {

            //System.out.println("Game Status: Stalemate");
            isGameComplete = "stalemate";

        }else if(game_status == board.invalidMove){

            //System.out.println("Game Status: Invalid Move");
            isValid = false;

        }

        if(isValid){
            resetSelectedCells();
            board.update_moves();
            updateChessBoard();
            wasMoveUndone = false;
            gameMoveHistory.add(board.getAI_MOVE());
            if(isGameComplete.equals("checkmate")){ // alert game end
                gameEndMsgAlert(title);
                promptSaveGameDialog();
            }else if(isGameComplete.equals("check")){
                if (board.getMove() % 2 == 0) {
                    msg = "White King in check";
                } else {
                    msg = "Black King in check";
                }
            }
        }else{
            resetSelectedCells();
            msg = "Please try again the move is invalid.";
        }
        if(msg.length() > 0){
            outputMsgDialog(title, msg);
        }
        board.resetAI_Move();
    }
    public String gameEndMsg(){
        if (board.getMove() % 2 == 1) {
            return "White Wins";
        } else {
            return "Black Wins";
        }
    }

    public void gameEndMsgAlert(String title){
        String msg = gameEndMsg();
        outputMsgDialog(title, msg);
    }
    public void drawChessGame(View view){
        String title = "Draw - " + gameEndMsg();
        promptGameEndDialog(title);
    }
    public void resignChessGame(View view){
        String title = "Resign - " + gameEndMsg();
        promptGameEndDialog(title);
    }
    public void playChessMove(View view, String promotion){
        String title = "Game Status", msg = "";
        String source_point;
        String target_point;
        if(replaymode&&currentmove<replaymove-1){
            String[] temp=replayinstruction[currentmove].split(" ");
            source_point=temp[0];
            target_point=temp[1];
            if(temp.length > 2){
                promotion = temp[2];
            }
            //System.out.println(currentmove+"!"+replaymove);
            currentmove++;
        }else {
            source_point = getResources().getResourceEntryName(selected_piece.getId());
            target_point = getResources().getResourceEntryName(selected_target_cell.getId());
        }
        //System.out.println("S: " + source_point + " | T: " + target_point);
        boolean isValid  = true;
        String isGameComplete = "";

        StringBuilder gameMoveEntry = new StringBuilder();
        String moveWithoutPromotion = source_point + " " + target_point;
        gameMoveEntry.append(moveWithoutPromotion);
        if(promotion.length() > 0){
            String promotionMove = " " + promotion;
            gameMoveEntry.append(promotionMove);
        }
        int game_status = board.play_move(source_point, target_point, promotion);

        if (game_status == board.check) {

            //System.out.println("Game Status: Check");
            isGameComplete = "check";

        } else if (game_status == board.checkmate) {

            //System.out.println("Game Status: Checkmate");
            isGameComplete = "checkmate";

        } else if (game_status == board.stalemate) {

            //System.out.println("Game Status: Stalemate");
            isGameComplete = "stalemate";

        }else if(game_status == board.invalidMove){

            //System.out.println("Game Status: Invalid Move");
            isValid = false;

        }

        if(isValid){
            resetSelectedCells();
            board.update_moves();
            updateChessBoard();
            wasMoveUndone = false;
            gameMoveHistory.add(gameMoveEntry.toString());
            if(isGameComplete.equals("checkmate")){ // alert game end
                gameEndMsgAlert(title);
                promptSaveGameDialog();
            }else if(isGameComplete.equals("check")){
                if (board.getMove() % 2 == 0) {
                    msg = "White King in check";
                } else {
                    msg = "Black King in check";
                }
            }
        }else{
            resetSelectedCells();
            msg = "Please try again the move is invalid.";
        }
        if(msg.length() > 0){
            outputMsgDialog(title, msg);
        }
    }
    public void validateChessAppMove(View view){
        replaymove();
        if(replaymode&&currentmove<replaymove-1){
            if(selected_piece == null || selected_target_cell == null){

            }else{
                currentmove=replaymove;
                replaymode=false;
                Replaynote.setText("");
            }
            playChessMove(view,"");
        }
        else if(selected_piece == null || selected_target_cell == null){
            String title = "", msg = "";
            title = "Cannot play move";
            if(selected_piece == null && selected_target_cell == null){
                msg = "You must select a valid source and target point before playing a move";
            }else if(selected_piece == null){
                msg = "You must select a valid source point before playing a move";
            }else{
                msg = "You must select a valid target point before playing a move";
            }
            outputMsgDialog(title, msg);
        }else {

            if (checkIfPromotion()) {
                playChessMoveView = view;
                promptPiecePromotionDialog();
            }else{
                playChessMove(view, "");
            }
        }
    }
    public void setMove(View view){
        ImageView selected_cell = (ImageView)findViewById(view.getId());

        if(selected_cell.equals(selected_piece)){
            setSelectedCellColor(selected_piece , false);
            selected_piece = null;
        }else if (selected_cell.equals(selected_target_cell)){
            setSelectedCellColor(selected_target_cell , false);
            selected_target_cell = null;
        }else{
            String location = getResources().getResourceEntryName(view.getId());
            int[] board_index = board.convertChessLocationToBoardIndex(location);
            int x = board_index[0], y = board_index[1];
            ChessPiece[][] chessboard = board.getChessBoard();
            if((board.getMove() % 2 == 0 && chessboard[x][y].getColor() == 'w' && chessboard[x][y].isActive())
                    || (board.getMove() % 2 != 0 && chessboard[x][y].getColor() == 'b' && chessboard[x][y].isActive())){
                if(selected_piece == null){
                    selected_piece = selected_cell;
                }else{
                    setSelectedCellColor(selected_piece, false);
                    selected_piece = selected_cell;
                }
            }else{
                if(selected_target_cell == null){
                    selected_target_cell = selected_cell;
                }else{
                    setSelectedCellColor(selected_target_cell, false);
                    selected_target_cell = selected_cell;
                }
            }

            if(selected_piece != null){
                selected_piece.setBackgroundColor(Color.rgb(174,193,51));
                //setSelectedCellColor(selected_piece, true);
            }
            if(selected_target_cell != null){
                selected_target_cell.setBackgroundColor(Color.rgb(238,240,117));
                //setSelectedCellColor(selected_target_cell, true);
            }
            //System.out.println("Location: "+ location + " | Clicks: " + clicks + " | B: " + board.getMove());
        }

    }
    public void playChessMoveSound(){

        int audioToPlay;
        if(board.getMoveType()){
            audioToPlay = getResources().getIdentifier("chessmovetakeover","raw",getPackageName());
        }else{
            audioToPlay = getResources().getIdentifier("chessmoveopen","raw",getPackageName());
        }
        pieceMoveSound = MediaPlayer.create(this, audioToPlay);
        pieceMoveSound.start();
        pieceMoveSound.stop();
    }
    public void stopChessMoveSound(){

        if(pieceMoveSound != null && pieceMoveSound.isPlaying()){
            pieceMoveSound.stop();
            pieceMoveSound.release();
            pieceMoveSound = null;
        }

    }
    public void outputGameHistory(){
        System.out.println("########## GAME MOVE HISTORY ##########");
        for(int i = 0; i < gameMoveHistory.size(); i++){
            System.out.println(gameMoveHistory.get(i));
        }
        System.out.println("########## END OF MOVE HISTORY ##########");
    }

    @Override
    public void savegame(String name) {
    }
    public void replaymove() {
        if (replaymode) {
            if (currentmove != replaymove) {
                return;
            } else {
                Replaynote.setText(" ");
                replaymode = false;
            }
        }else{
            if(Replaynote != null){
                Replaynote.setText("");
            }
        }
    }
}
