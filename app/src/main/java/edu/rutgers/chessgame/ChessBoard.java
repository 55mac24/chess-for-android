package edu.rutgers.chessgame;
import java.lang.reflect.Array;
import java.util.Random;
import java.util.ArrayList;
public class ChessBoard {
    // chess definitions
    private static int board_start_dim = 0, board_end_dim = 8;
    private static int x_index = 0, y_index = 1;
    private static int  black_index = 0, white_index = 1, king_index = 2;
    private static char white = 'w', black = 'b';
    private static char[] pieces = {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R', 'P'};

    //Chess Game setup
    private boolean wasMoveTakeover = false;
    private ChessPiece[][] chessBoard = new ChessPiece[board_end_dim][board_end_dim];
    private static int breakAfterBlockingPiece = 0;
    private int move = 0;
    //Return codes
    public int error = -4, check = 0, checkmate = 1, stalemate = 2, invalidMove = -2, canStillPlay = -1;
    private String AI_MOVE = "";
    private ArrayList<ArrayList<ChessPiece>> gameChessMoves = new ArrayList<>();
    private ArrayList<ArrayList<ChessPiece>> lastGameMoves = new ArrayList<>();
    /*
    Hao 's change
     */
   public String getAllmove(){
       String temp="";
       for(int i =0;i<lastGameMoves.size();i++){
           temp+=convertBoardIndexToString(lastGameMoves.get(i).get(0).getI(),lastGameMoves.get(i).get(0).getJ());
           temp+=" "+convertBoardIndexToString(lastGameMoves.get(i).get(1).getI(),lastGameMoves.get(i).get(1).getJ())+";";
       }
       return temp;
    }


    public ChessBoard() {

        for (int i = board_start_dim; i < board_end_dim; i++) {
            boolean isActive = false;
            int piece_loc = 0;
            char color = black;
            if (i <= 1 || i >= 6) {
                isActive = true;
                piece_loc = 0;
                if (i >= 6) {
                    color = white;
                }

            }
            for (int j = board_start_dim; j < board_end_dim; j++) {
                if ((i == 1) || (i == 6)) {
                    piece_loc = pieces.length - 1;
                }
                if (i <= 1 || i >= 6) {
                    chessBoard[i][j] = new ChessPiece(color, pieces[piece_loc], i, j, isActive);
                    piece_loc = piece_loc + 1;
                } else {
                    chessBoard[i][j] = new ChessPiece('#', '#', i, j, isActive);
                }
            }
        }

    }

    public boolean getMoveType(){
        return this.wasMoveTakeover;
    }
    private  boolean isPieceOnFifthRank(ChessPiece piece){
        if(piece.getColor() == white && piece.locationToString().charAt(1) == '5'){
            return true;
        }else return piece.getColor() == black && piece.locationToString().charAt(1) == '4';
    }
    private  boolean isPieceOnFourthRank(ChessPiece piece){
        if(piece.getColor() == white && piece.locationToString().charAt(1) == '4'){
            return true;
        }else return piece.getColor() == black && piece.locationToString().charAt(1) == '5';
    }
    private  int perform_pawn_operation(String source_point_pawn, String target_point) {

        ChessPiece pawn = getPieceIndexFromChessLocation(source_point_pawn);
        int isPassant = 0;

        int[] pawn_coordinate = convertChessLocationToBoardIndex(pawn.locationToString());
        int x_o = pawn_coordinate[x_index], y_o = pawn_coordinate[y_index];

        int[][] passant_moves = {{2, 0}, {1, 1}, {1, -1}};
        int[] target_coordinate = convertChessLocationToBoardIndex(target_point);
        int x_t = target_coordinate[x_index], y_t = target_coordinate[y_index];

        int moveByPiece = (pawn.getColor() == white) ? -1 : 1;
        char opposite_color = (pawn.getColor() == white) ? black : white;
        int x_p = x_t - x_o, y_p = y_t - y_o;
        x_p = (x_p < 1) ? x_p * -1:x_p;

        if (isPieceOnFifthRank(pawn)
                && ((x_p == passant_moves[1][x_index] && y_p == passant_moves[1][y_index])
                || (x_p == passant_moves[2][x_index] && y_p == passant_moves[2][y_index]))) {

            //outputPointList(possibleMoves(pawn, breakAfterBlockingPiece));

            ChessPiece wasMoved = moveChessPiece(source_point_pawn, target_point);
            if (wasMoved != null) { // capture opposite color piece below

                int x_capture = wasMoved.getI() + ((wasMoved.getColor() == white) ? 1 : -1);

                String chess_board_location = convertBoardIndexToString(x_capture, wasMoved.getJ());
                ChessPiece capture_piece = getPieceIndexFromChessLocation(chess_board_location);

                if (isPieceOnFourthRank(capture_piece) && capture_piece.getColor() == opposite_color
                        && capture_piece.isActive()  && capture_piece.isEnpassant()
                        && capture_piece.getIsMoved() == 1) {
                    capture_piece.unsetEnpassant();
                    capture_piece.captured();
                }
                wasMoved.moved(wasMoved.getIsMoved());
            } else {

                return invalidMove;
            }

        } else {

            int x_up = x_o + moveByPiece;
            if(((x_up == x_t && y_t == y_o && inBounds(x_up, y_o) )
                    ||  (x_p == passant_moves[0][x_index] && y_p == passant_moves[0][y_index]))
                    && chessBoard[x_up][y_o].isActive()){

                return invalidMove;
            } // can't move pawn forward to over take piece
            int pawnHasMoved = pawn.getIsMoved();
            ChessPiece new_piece = moveChessPiece(source_point_pawn, target_point);
            if (new_piece == null) {

                return invalidMove;

            }
            int x_f = (x_o - x_t) * ((new_piece.getColor() == white) ? 1 : -1), y_f = (y_o - y_t);

            if (pawnHasMoved == 0 && x_f == passant_moves[0][x_index] && y_f == passant_moves[0][y_index]) {

                new_piece.setEnpassant();

            } else {

                new_piece.unsetEnpassant();
            }

        }
        return isPassant;
    }

    private int canMoveMax(char piece) {
        if (piece == 'R' || piece == 'B' || piece == 'Q') {
            return board_end_dim - 1;
        } else {
            return 1;
        }
    }

    private boolean inBounds(int x, int y) {

        return (x >= 0 && x < 8) && (y >= 0 && y < 8);

    }

    public void outputPointList(ArrayList<Point> list) {
        System.out.print("Piece can now move: ");
        int count = 0;
        for (Point point : list) {
            if (count == list.size() - 1) {
                System.out.print(point.toString());
            } else {
                System.out.print(point.toString() + ", ");
            }

            count = count + 1;
        }
        System.out.println();
    }

    public ArrayList<ChessPiece> getInActiveChessPieces(char color) {
        ArrayList<ChessPiece> inactive = new ArrayList<>();
        for (int i = 0; i < board_end_dim; i++) {
            for (int j = 0; j < board_end_dim; j++) {
                ChessPiece board_piece = chessBoard[i][j];
                if (board_piece.getColor() == color && !board_piece.isActive()) {
                    inactive.add(board_piece);
                }
            }
        }
        return inactive;
    }
    public String getAI_MOVE(){
       return this.AI_MOVE;
    }
    private void setAI_Move(String source_point, String target_point){
        this.AI_MOVE = source_point + " " + target_point;
    }
    public void resetAI_Move(){
       this.AI_MOVE = "";
    }
    public int makeAIDecision(){
        ArrayList<ArrayList<ChessPiece>> chessBoardPieces = activeChessBoardPieces();
        ArrayList<ChessPiece> colorToSelectPieceFrom;
        if(this.move % 2 == 0){
            colorToSelectPieceFrom = chessBoardPieces.get(white_index);
        }else{
            colorToSelectPieceFrom = chessBoardPieces.get(black_index);
        }
        int checkIfMovePutOtherKingInCheck = (move % 2 == 0) ? black_index : white_index;
        ChessPiece selectedChessPiece = null;
        while(colorToSelectPieceFrom.size() > 0){

            Random pickRandomPiece = new Random();
            int indexOfPiece = pickRandomPiece.nextInt(colorToSelectPieceFrom.size());
            selectedChessPiece = colorToSelectPieceFrom.get(indexOfPiece);
            if(selectedChessPiece != null){
                ArrayList<Point> pieceCanMoveTo = possibleMoves(selectedChessPiece, breakAfterBlockingPiece);
                while(pieceCanMoveTo.size() > 0){
                    int indexOfPieceMove = pickRandomPiece.nextInt(pieceCanMoveTo.size());
                    Point targetChessBoardLocation = pieceCanMoveTo.get(indexOfPieceMove);

                    String source_point = selectedChessPiece.locationToString();
                    String target_point = convertBoardIndexToString(targetChessBoardLocation.getX(),targetChessBoardLocation.getY());
                    int wasMoveSuccessful = play_move(source_point, target_point, "");
                    if(wasMoveSuccessful == invalidMove){
                        pieceCanMoveTo.remove(targetChessBoardLocation);

                    }else{
                        setAI_Move(source_point,target_point);
                        return wasMoveSuccessful;
                    }
                }
            }
                colorToSelectPieceFrom.remove(selectedChessPiece);
        }

        System.out.println("Potential error");
        return game_status(checkIfMovePutOtherKingInCheck);

    }
    private ChessPiece swapChessPiece(int source_x, int source_y, int target_x, int target_y) {

        ChessPiece target_loc_piece = chessBoard[target_x][target_y];
        ChessPiece source_loc_piece = chessBoard[source_x][source_y];

        int s_moved = source_loc_piece.getIsMoved();
        char t_color = target_loc_piece.getColor();
        char t_piece = target_loc_piece.getPiece();

        //if color is not similar, set source piece to captured as the piece locations will be swapped
        this.wasMoveTakeover = target_loc_piece.isActive();
        if(!target_loc_piece.isActive() || (t_color != source_loc_piece.getColor() && source_loc_piece.isActive())){
            chessBoard[target_x][target_y].active();
            chessBoard[source_x][source_y].captured();

        }

        chessBoard[target_x][target_y].setColor(source_loc_piece.getColor());
        chessBoard[target_x][target_y].setPiece(source_loc_piece.getPiece());
        chessBoard[target_x][target_y].moved(s_moved);

        chessBoard[source_x][source_y].setColor(t_color);
        chessBoard[source_x][source_y].setPiece(t_piece);


        //If either is empty, set to inactive
        if(chessBoard[source_x][source_y].getColor() == ' ' || chessBoard[source_x][source_y].getColor() == '#'){
            chessBoard[source_x][source_y].captured();
        }
        if(chessBoard[target_x][target_y].getColor() == ' ' || chessBoard[target_x][target_y].getColor() == '#'){
            chessBoard[target_x][target_y].captured();
        }
        return chessBoard[target_x][target_y];
    }

    private String convertBoardIndexToString(int x_int, int y_int) {
        char alpha = (char) (y_int + 97), row_number = (char) ((board_end_dim - x_int) + '0');
        return "" + alpha + row_number;
    }

    public int[] convertChessLocationToBoardIndex(String move) {
        char alpha = move.charAt(0);
        char row_number = move.charAt(1);
        int i = board_end_dim - (row_number - '0'), j = (int) alpha - 97;
        return new int[]{i, j};
    }

    private ChessPiece getPieceIndexFromChessLocation(String location) {
        int i = 0, j = 1;
        int[] piece_coordinates = convertChessLocationToBoardIndex(location);
        return chessBoard[piece_coordinates[i]][piece_coordinates[j]];
    }

    private ArrayList<ArrayList<ChessPiece>> activeChessBoardPieces(){
        ArrayList<ChessPiece> activePieces_black = new ArrayList<>();
        ArrayList<ChessPiece> activePieces_white = new ArrayList<>();
        ArrayList<ChessPiece> activePieces_king = new ArrayList<>();
        for(int i = 0; i < board_end_dim;i++){
            for(int j = 0; j < board_end_dim; j++){
                if(chessBoard[i][j].getColor() == black && chessBoard[i][j].isActive()){
                    if(chessBoard[i][j].getPiece() == 'K'){
                        activePieces_king.add(black_index,chessBoard[i][j]);
                    }else{
                        activePieces_black.add(chessBoard[i][j]);
                    }
                }else if(chessBoard[i][j].getColor() == white && chessBoard[i][j].isActive()){
                    if(chessBoard[i][j].getPiece() == 'K'){
                        activePieces_king.add(white_index,chessBoard[i][j]);
                    }else{
                        activePieces_white.add(chessBoard[i][j]);
                    }
                }
            }
        }
        ArrayList<ArrayList<ChessPiece>> activePieces = new ArrayList<>();
        activePieces.add(black_index, activePieces_black);
        activePieces.add(white_index,activePieces_white);
        activePieces.add(king_index,activePieces_king);
        return activePieces;
    }
    private boolean canDefenderProtectKingFromEnemy(ArrayList<ChessPiece> activePieces_sameKingColor, ArrayList<Point> enemies){
        for(ChessPiece defender: activePieces_sameKingColor){
            ArrayList<Point> defender_moves = possibleMoves(defender, breakAfterBlockingPiece);
            for(Point defender_move: defender_moves){

                if(enemies.remove(defender_move)){
                    // System.out.println("Defender: " + defender.locationToString());
                    break;
                };

            }
        }
        return enemies.size() == 0;
    }
    private int isKingInCheckMate(ArrayList<ChessPiece> activePieces_oppositeKingColor,
                                 ArrayList<ChessPiece> activePieces_sameKingColor, ChessPiece king){
        ArrayList<Point> king_moves;
        int continueAfterBlockingPiece = -1;
        if(king != null){
            king_moves = possibleMoves(king, continueAfterBlockingPiece);
        }else{
            //System.out.println("Error no king");
            return error;
        }

        ArrayList<Point> unblocked_kings_moves = possibleMoves(king, continueAfterBlockingPiece);
        for(Point move: king_moves){
            ChessPiece isPieceActive = chessBoard[move.getX()][move.getY()];
            if(isPieceActive.isActive() && isPieceActive.getColor() == king.getColor()){
                if(unblocked_kings_moves.size() > 0){
                    unblocked_kings_moves.remove(move);
                }

            }
        }

        int x_k = king.getI(), y_k = king.getJ();

        ArrayList<Point> enemy_attackers = new ArrayList<>();
        int isKingInCurrentlyInCheck = 0;
        for(ChessPiece piece: activePieces_oppositeKingColor){

            ArrayList<Point> piece_moves_stopped_by_defender = possibleMoves(piece, breakAfterBlockingPiece);
            Point enemy = new Point(piece.getI(),piece.getJ());
            //outputPointList(piece_moves_stopped_by_defender);
            for(Point pieceCanMove: piece_moves_stopped_by_defender){

                int x_p = pieceCanMove.getX(), y_p = pieceCanMove.getY();

                if(x_p == x_k && y_p == y_k){ // if move is kings current location, then king is check
                    //System.out.println(enemy.getX() + ", " + enemy.getY());
                    //outputPointList(piece_moves_stopped_by_defender);
                    isKingInCurrentlyInCheck++;
                    if(!enemy_attackers.contains(enemy)){
                        enemy_attackers.add(enemy);
                    }
                }

                if(unblocked_kings_moves.size() > 0){
                    //System.out.println("Move for check: " + convertBoardIndexToString(pieceCanMove.getX(),pieceCanMove.getY()));
                    unblocked_kings_moves.remove(pieceCanMove); // will remove a king's move that can cause a check

                }

            }
        }

        boolean canDefenderCaptureThreat = canDefenderProtectKingFromEnemy(activePieces_sameKingColor, enemy_attackers);
/*
        System.out.print("Enemies ");
        outputPointList(enemy_attackers);
        System.out.print(canDefenderCaptureThreat + " Unblocked 2: ");
        outputPointList(unblocked_kings_moves);

 */
        //System.out.println(canDefenderCaptureThreat + " " + isKingInCurrentlyInCheck + " " + unblocked_kings_moves.size());
        if(isKingInCurrentlyInCheck == 0 && !canDefenderCaptureThreat && unblocked_kings_moves.size()==0){
            return stalemate;
        }else if(isKingInCurrentlyInCheck > 0 && !canDefenderCaptureThreat && unblocked_kings_moves.size()==0){
            return checkmate;
        }else if(isKingInCurrentlyInCheck > 0){
            return check;
        }else{
            return canStillPlay;
        }
    }

    private ArrayList<Point> getChessPieceInBoard(char color, char piece) {
        ArrayList<Point> pieceLocation = new ArrayList<>();
        for (int i = board_start_dim; i < board_end_dim; i++) {
            for (int j = 0; j < board_end_dim; j++) {
                ChessPiece chessPiece = chessBoard[i][j];
                if (chessPiece.getColor() == color && chessPiece.getPiece() == piece) {
                    int[] chessPiece_coordinates = convertChessLocationToBoardIndex(chessPiece.locationToString());

                    pieceLocation.add(new Point(chessPiece_coordinates[x_index], chessPiece_coordinates[y_index]));
                }
            }
        }

        return pieceLocation;
    }

    private int isMoveCastle(ChessPiece king, String move) {

        int[][] directions = {{0, 2}, {0, -2}};
        int[] location = convertChessLocationToBoardIndex(king.locationToString());
        //System.out.println("O: " + piece.locationToString() +" L_0: " + location[x_index] + " L_1: " + location[y_index]);
        int[] move_board_coordinate = convertChessLocationToBoardIndex(move);
        int x_o = location[x_index];
        int y_o = location[y_index];
        for (int[] direction : directions) {

            int x_i = x_o + direction[x_index];
            int y_i = y_o + direction[y_index];
            //System.out.println("D_0: " + direction[x_index] + " D_1: " + direction[y_index]);
            //System.out.println("X_i: " + x_i + " Y_i: " + y_i);
            if (inBounds(x_i, y_i) && move_board_coordinate[x_index] == x_i && move_board_coordinate[y_index] == y_i) {
                if (y_i > y_o) {
                    return 1; // right
                } else {
                    return -1; // left
                }
            }
        }
        return 0;
    }

    private boolean canKingCastle(ChessPiece king) {
        if (king.getIsMoved() > 0) {
            //System.out.println("Cannot castle king as it has been moved already");
            return false;
        }
        int i = king.getI();
        boolean isKingRowToRookClear_left = true, isKingRowToRookClear_right = true;
        for (int j = 0; j < board_end_dim; j++) {
            if (chessBoard[i][j].isActive()
                    && (chessBoard[i][j].getPiece() != 'R'
                    && chessBoard[i][j].getPiece() != 'K')) {

                if (king.getJ() < j) {
                    isKingRowToRookClear_left = false;
                } else {
                    isKingRowToRookClear_right = false;
                }
            }

        }
        //System.out.println();
        if (!isKingRowToRookClear_left && !isKingRowToRookClear_right) {
            return false;
        }

        ArrayList<Point> rook_location = getChessPieceInBoard(king.getColor(), 'R');
        int validRooksForCastle = rook_location.size();

        for (Point rook : rook_location) {
            String rook_boardLocation = convertBoardIndexToString(rook.getX(), rook.getY());
            //System.out.println("R: " + rook_boardLocation);
            ChessPiece rook_piece = getPieceIndexFromChessLocation(rook_boardLocation);
            if (rook_piece.getIsMoved() > 0) {
                validRooksForCastle = validRooksForCastle - 1;
            }
        }
        return validRooksForCastle > 0;
    }
    public int getMove(){
        return this.move;
    }
    public void update_moves(){
        this.move = this.move + 1;
    }

    private int game_status(int doesMoveResultInCheck){

        ArrayList<ArrayList<ChessPiece>> activePieces = activeChessBoardPieces();
        ArrayList<ChessPiece> activePieces_black = activePieces.get(black_index);
        ArrayList<ChessPiece> activePieces_white = activePieces.get(white_index);

        ChessPiece king;
        int status = error;

        if(doesMoveResultInCheck == black_index){
            // check if white move resulted in black king in check
            king = activePieces.get(king_index).get(black_index);
            status = isKingInCheckMate(activePieces_white,activePieces_black,king);

        }else if( doesMoveResultInCheck == white_index){
            // check if black move resulted in white king in check
            king = activePieces.get(king_index).get(white_index);
            status = isKingInCheckMate(activePieces_black,activePieces_white,king);
        }

        if(status == error){
            System.out.println("Error");
            System.exit(1);
            return error;
        }else if(status == checkmate){
            return checkmate;
        }else if(status == check){
            return check;
        }else{
            return canStillPlay;
        }
    }
    private void resetChessPiece(ChessPiece original){
        System.out.println(original.toString() + " | Reset: " + original.getI() + "," + original.getJ());
        chessBoard[original.getI()][original.getJ()].updateChessPiece(original);
    }
    private void updateGameMoveHistory(){
       gameChessMoves = new ArrayList<>();
       gameChessMoves.addAll(lastGameMoves);
    }

    public void undoLastMove(){

        if(lastGameMoves.size() < 1){
            return;
        }
        int lastMoveIndex = lastGameMoves.size() - 1;

        ArrayList<ChessPiece> lastChessMove = lastGameMoves.get(lastMoveIndex);
        if(lastChessMove.get(0).getColor() == 'C' && lastChessMove.get(0).getPiece() == 'K'){
            ArrayList<ChessPiece> kingChessMove = lastGameMoves.get(lastMoveIndex - 1);
            resetChessPiece(kingChessMove.get(0));
            resetChessPiece(kingChessMove.get(1));
            ArrayList<ChessPiece> rookChessMove = lastGameMoves.get(lastMoveIndex - 2);
            resetChessPiece(rookChessMove.get(0));
            resetChessPiece(rookChessMove.get(1));
            lastGameMoves.remove(kingChessMove);
            lastGameMoves.remove(rookChessMove);

        }else{
            resetChessPiece(lastChessMove.get(0));
            resetChessPiece(lastChessMove.get(1));
        }
        lastGameMoves.remove(lastChessMove);
        updateGameMoveHistory();
        this.move = this.move - 1;
    }

    public int play_move(String source_point, String target_point, String promote) {

        int[] source = convertChessLocationToBoardIndex(source_point);
        int[] target = convertChessLocationToBoardIndex(target_point);

        ChessPiece source_chess_piece = chessBoard[source[x_index]][source[y_index]];

        ChessPiece wasMoveSuccessfully = null;

        int isCastleMove = isMoveCastle(source_chess_piece, target_point);
        int checkIfMovePutOtherKingInCheck = (move % 2 == 0) ? black_index : white_index;
        if (source_chess_piece.getPiece() == 'K' && isCastleMove != 0) {

            boolean isCastled = performKingCastle(source_chess_piece, target_point, isCastleMove);
            if (isCastled) {
                return canStillPlay;
            }else{
                return invalidMove;
            }

        }else if (source_chess_piece.getPiece() == 'P') {
            if (((target[x_index] == board_start_dim) && source_chess_piece.getColor() == white) ||
                    (target[x_index] == board_end_dim - 1 && source_chess_piece.getColor() == black)) {

                char promote_piece;
                if(promote.isEmpty()){

                    promote_piece = 'Q';

                }else{

                    char checkIfValidPiece = promote.charAt(0);

                    if(checkIfValidPiece == 'Q' || checkIfValidPiece == 'K'
                            || checkIfValidPiece == 'B' || checkIfValidPiece == 'R') {

                        promote_piece = checkIfValidPiece;

                    }else {

                        return invalidMove;

                    }

                }
                wasMoveSuccessfully = moveChessPiece(source_point, target_point);

                if(wasMoveSuccessfully == null){
                    return invalidMove;
                }
                wasMoveSuccessfully.active();
                wasMoveSuccessfully.setPiece(promote_piece);
                return canStillPlay;

            } else {

                int wasPawnMovedSuccessfully = perform_pawn_operation(source_point, target_point);
                if(wasPawnMovedSuccessfully == 0){
                    return game_status(checkIfMovePutOtherKingInCheck);
                }else{
                    return invalidMove;
                }

            }
        }else{
            wasMoveSuccessfully = moveChessPiece(source_point, target_point);
            if (wasMoveSuccessfully == null) {
                return invalidMove;
            }
            wasMoveSuccessfully.active();
        }


        return game_status(checkIfMovePutOtherKingInCheck);

    }

    private boolean performKingCastle(ChessPiece king, String target_point, int isCastleMove) {

        ArrayList<Point> rook_location = getChessPieceInBoard(king.getColor(), 'R');
        if (!canKingCastle(king)) {

            //System.out.println("Cannot castle because there is no valid rook for castling.");
            return false;
        }

        String king_source_point = king.locationToString();

        king = moveChessPiece(king_source_point, target_point);
        if(king == null){
            return false;
        }
        //System.out.println("J: " + king.getJ() +" C: " + isCastleMove);
        int x = king.getI();
        for (Point rook : rook_location) {

            if(x == rook.getX()){
                int y_r = -1;
                if (isCastleMove == 1 && (rook.getY() > king.getJ())) { // king castle right
                    y_r = king.getJ() - 1;
                } else if (isCastleMove == -1 && (rook.getY() < king.getJ())) {
                    y_r = king.getJ() + 1;
                }
                if (y_r > -1) {
                    ArrayList<ChessPiece> rookCastleMove = new ArrayList<>();
                    rookCastleMove.add(0, new ChessPiece(chessBoard[rook.getX()][rook.getY()]));
                    rookCastleMove.add(1, new ChessPiece(chessBoard[x][y_r]));
                    gameChessMoves.add(rookCastleMove);
                    lastGameMoves.add(rookCastleMove);
                    swapChessPiece(rook.getX(),rook.getY(),x, y_r);
                    ArrayList<ChessPiece> castleNotificationPiece = new ArrayList<>();
                    ChessPiece notify = new ChessPiece('C','K',0,0,false);
                    castleNotificationPiece.add(notify);
                    lastGameMoves.add(castleNotificationPiece);
                    return true;
                }
            }

        }

        // int[] target = convertChessLocationToBoardIndex(target_point);
        //swapChessPiece(king.getI(), king.getJ(), target[x_index], target[y_index]);
        moveChessPiece(target_point, king_source_point);
        return false;

    }
    private int[][] typesOfDirections(char piece, int moved) {
        if (piece == 'R') {
            return new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; //up, down, left, right
        } else if (piece == 'N') {
            return new int[][]{{2, 1}, {-2, 1}, {2, -1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};
        } else if (piece == 'B') {
            return new int[][]{{-1, 1}, {1, -1}, {1, 1}, {-1, -1}}; // \ /
        } else if (piece == 'P') {
            if (moved > 0) {
                return new int[][]{{1, 0}, {1, 1}, {1, -1}}; // |/\
            } else {
                return new int[][]{{2, 0}, {1, 0}, {1, 1}, {1, -1}}; //
            }
        } else { // King or queen same moves

            if(piece == 'K' && moved == 0){
                return new int[][]{{0,2},{0,-2},{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, -1}, {-1, 1}, {-1, -1}, {1, 1}};
            }else{
                return new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, -1}, {-1, 1}, {-1, -1}, {1, 1}};
            }

        }
    }

    private ArrayList<Point> possibleMoves(ChessPiece piece, int howToHandleBlockingPieces) {

        ArrayList<Point> moves = new ArrayList<>();
        int[][] directions = typesOfDirections(piece.getPiece(), piece.getIsMoved());

        int steps = canMoveMax(piece.getPiece()) + 1;

        int x_o = piece.getI(), y_o = piece.getJ(), indexOfDirection = 0;

        int movePieceBy = (piece.getColor() == white) ? -1 : 1;
        char piece_type = piece.getPiece(), piece_color = piece.getColor();
        for (int[] direction : directions) {
            for (int i = 1; i < steps; i++) {
                int x_i = x_o + (movePieceBy * (direction[x_index] * i));
                int y_i = y_o + (movePieceBy * (direction[y_index] * i));
                if (inBounds(x_i, y_i)) {
                    if(chessBoard[x_i][y_i].isActive() && chessBoard[x_i][y_i].getColor() == piece_color
                            && piece_type != 'K'){
                        break;
                    }else if(piece_type == 'P' && chessBoard[x_i][y_i].isActive()
                            && chessBoard[x_i][y_i].getColor() != piece.getColor()
                            && (
                            (directions.length == 3 && indexOfDirection == 0)
                                    ||      (directions.length == 4 && (indexOfDirection == 1 || indexOfDirection == 0))
                    )){

                        break; // pawn cannot move forward to overtake piece
                    }else if(piece_type == 'P' && !chessBoard[x_i][y_i].isActive()
                            && (
                            (directions.length == 3 && (indexOfDirection == 1 || indexOfDirection == 2))
                                    ||      (directions.length == 4 && (indexOfDirection == 2 || indexOfDirection == 3))
                    )
                    ){

                        if(!chessBoard[x_i][y_i].isActive()){


                            if(chessBoard[x_o][y_i].isEnpassant()){
                                moves.add(new Point(x_i,y_i));

                            }


                        }
                        break;
                        // pawn can only move diagonal to remove piece unless enpassant
                    }else{
                        //System.out.println("X_i_1: " + x_i + " Y_i_1: " + y_i + " " + chessBoard[x_i][y_i].isActive());
                        moves.add(new Point(x_i, y_i));
                        if(chessBoard[x_i][y_i].isActive() && chessBoard[x_i][y_i].getColor() != piece_color

                                && howToHandleBlockingPieces == breakAfterBlockingPiece){
                            break;
                        }
                    }

                }
            }
            indexOfDirection++;
        }

        return moves;
    }
    private boolean validateChessMove(ChessPiece piece, String target_point) {
        ArrayList<Point> piece_can_move = possibleMoves(piece, breakAfterBlockingPiece);

        ChessPiece target_piece_obj = getPieceIndexFromChessLocation(target_point);
        if (target_piece_obj.isActive() && target_piece_obj.getColor() == piece.getColor()) {
            return false; //Can't remove same piece
        }

        int[] target_piece_coordinates = convertChessLocationToBoardIndex(target_point);
        Point target_piece = new Point(target_piece_coordinates[x_index], target_piece_coordinates[y_index]);
        return piece_can_move.contains(target_piece);
    }
    private ChessPiece moveChessPiece(String source_point, String target_point) {

        int[] source = convertChessLocationToBoardIndex(source_point);
        int[] target = convertChessLocationToBoardIndex(target_point);

        boolean canPieceMoveToTarget = validateChessMove(chessBoard[source[x_index]][source[y_index]], target_point);
        ChessPiece new_piece = null;


        if (canPieceMoveToTarget) {
            ArrayList<ChessPiece> game_move = new ArrayList<>();
            game_move.add(0, new ChessPiece(chessBoard[source[x_index]][source[y_index]]));
            game_move.add(1, new ChessPiece(chessBoard[target[x_index]][target[y_index]]));

            boolean source_piece_active = chessBoard[source[x_index]][source[y_index]].isActive();
            boolean target_piece_active = chessBoard[target[x_index]][target[y_index]].isActive();


            new_piece = swapChessPiece(source[x_index], source[y_index], target[x_index], target[y_index]);

            int doesMoveResultInCheck = (move % 2 == 0) ? white_index : black_index;
            if(game_status(doesMoveResultInCheck) == check &&
                    (
                            (new_piece.getColor() == white && move % 2 == 0)
                                    ||
                                    (new_piece.getColor() == black && move % 2 == 1)
                    )){ //if move results in check, undo the swap

                swapChessPiece(source[x_index], source[y_index], target[x_index], target[y_index]);

                ChessPiece source_piece = chessBoard[source[x_index]][source[y_index]];
                ChessPiece target_piece = chessBoard[target[x_index]][target[y_index]];

                if(source_piece_active){
                    source_piece.active();
                }else{
                    source_piece.captured();
                }
                if(target_piece_active){
                    target_piece.active();
                }else{
                    target_piece.captured();
                }
                new_piece = null;
                chessBoard[source[x_index]][source[y_index]].reset_moved();
            }else{

                gameChessMoves.add(game_move);
                lastGameMoves.add(game_move);
                chessBoard[source[x_index]][source[y_index]].moved(-1);
            }

            //ArrayList<Point> piece_can_move = possibleMoves(chessBoard[target[x_index]][target[y_index]], breakAfterBlockingPiece);
            //outputPointList(piece_can_move);

        }

        return new_piece;
    }
    public ChessPiece[][] getChessBoard(){
        return this.chessBoard;
    }
    public void outputChessBoard() {

        //System.out.println("    0  1  2  3  4  5  6  7 ");
        for (int i = board_start_dim; i < board_end_dim; i++) {
            //System.out.print(i + " ");
            for (int j = board_start_dim; j < board_end_dim + 1; j++) {
                if(j > 0){
                    System.out.print(" ");
                }
                if (j < board_end_dim) {
                    if (chessBoard[i][j].isActive()) {
                        System.out.print( chessBoard[i][j].toString());
                    } else if((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)){
                        System.out.print("##");
                    } else{
                        System.out.print("  ");
                    }
                } else {
                    System.out.println((board_end_dim - i));
                }
            }
        }
        System.out.println(" a  b  c  d  e  f  g  h ");

    }
}
