package edu.rutgers.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void beginTwoPlayer(View view){
        Intent intent = new Intent(this, TwoPlayerGame.class);
        startActivity(intent);
    }
    public void beginGameViewer(View view){
        Intent intent = new Intent(this, GameLogMenu.class);
        startActivity(intent);
    }
}
