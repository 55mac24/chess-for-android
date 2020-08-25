package edu.rutgers.chessgame;

public class ChessPiece {

    static int board_end_dim = 8;

    private char color, piece;
    private char x_board_loc, y_board_loc;
    private int i,j, isMoved;
    private boolean enpassant, status;

    public ChessPiece(char color, char piece, int i, int j, boolean isActive) {

        this.color = color;
        this.piece = piece;
        this.x_board_loc = (char)(j + 97);
        this.y_board_loc = (char)((board_end_dim - i) + '0');
        this.i = i;
        this.j = j;
        this.status = isActive;
        this.isMoved = 0;
        this.enpassant = false;

    }

    public int getIsMoved(){
        return this.isMoved;
    }

    public void setEnpassant(){
        this.enpassant = true;
    }

    public void unsetEnpassant(){
        this.enpassant = false;
    }
    public boolean isEnpassant() {
        return enpassant;
    }

    public int getI() {
        return i;
    }

    public void active(){
        this.status = true;
    }
    public void captured(){
        this.status = false;
    }
    public int getJ() {
        return j;
    }

    public void setColor(char color) {
        this.color = color;
    }
    public void moved(int moved){
        this.isMoved = moved + 1;
    }
    public void reset_moved(){
        this.isMoved = this.isMoved - 1;
    }
    public char getColor() {
        return this.color;
    }

    public void setPiece(char piece) {
        this.piece = piece;
    }

    public char getPiece(){
        return this.piece;
    }

    public boolean isActive(){
        return this.status;
    }
    public String locationToString(){
        return "" + this.x_board_loc + this.y_board_loc;
    }
    public String toString(){
        return ""+this.color + this.piece;
    }
    public void updateChessPiece(ChessPiece change) {

        this.color = change.getColor();
        this.piece = change.getPiece();
        this.status = change.isActive();
        this.isMoved = change.getIsMoved();
        this.enpassant = change.isEnpassant();

    }
    public ChessPiece(ChessPiece chess_piece) {

        this.color = chess_piece.color;
        this.piece = chess_piece.piece;
        this.x_board_loc = chess_piece.x_board_loc;
        this.y_board_loc = chess_piece.y_board_loc;
        this.i = chess_piece.i;
        this.j = chess_piece.j;
        this.status = chess_piece.status;
        this.isMoved = chess_piece.isMoved;
        this.enpassant = chess_piece.enpassant;

    }
}
