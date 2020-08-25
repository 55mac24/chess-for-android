package edu.rutgers.chessgame;

public class Point {
    private int x;
    private int y;
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object point){
        return point instanceof Point && this.x == ((Point)point).x && this.y == ((Point)point).y;
    }

    public String toString(){
        return "("+this.x+","+this.y+")";
    }
}
