package edu.rutgers.chess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.rutgers.recordmove.DatabaseHelper;
import edu.rutgers.recordmove.recorder;

public class GameLogMenu extends AppCompatActivity {

    Button Replay, Namesort, Timesort;
    ListView NameCol, TimeCol;
    EditText inputname;
    ArrayList<String[]> List = new ArrayList<>();
    ArrayList<String> name =new ArrayList<>(), time =new ArrayList<>(), instruction=new ArrayList<>();
    public String outputinstruction, selectedGameToReplay = "";

    DatabaseHelper databaseHelper;
    int clicks = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelog_menu);
        Replay = findViewById(R.id.Replay);
        Namesort = findViewById(R.id.Sortname);
        Timesort = findViewById(R.id.Sorttime);
        NameCol = findViewById(R.id.NameCol);
        TimeCol = findViewById((R.id.TimeCol));
        inputname = findViewById(R.id.Replayname);
        databaseHelper=new DatabaseHelper(this);
        name = databaseHelper.getName();
        time = databaseHelper.getTime();
        instruction = databaseHelper.getinstruction();
        for(int i=0;i<databaseHelper.getinstruction().size();i++){
            String[] temp=new String[3];
            temp[0]=name.get(i);
            temp[1]=time.get(i);
            temp[2]=instruction.get(i);
            List.add(temp);
        }

        populateListView();
        NameCol.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_game = parent.getItemAtPosition(position).toString();

                inputname.setText(selected_game);


            }

        });
    }
    public void populateListView(){
        for(int i=0;i<List.size();i++){
            name.set(i,List.get(i)[0]);
            time.set(i,List.get(i)[1]);
        }
        ListAdapter adaptern = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        NameCol.setAdapter(adaptern);

        ListAdapter adaptert = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, time);
        TimeCol.setAdapter(adaptert);
    }
    public void donamesort(View view){
        List=recorder.ListNameSort(List);
        populateListView();
        System.out.println(name);
    }
    public void dotimesort(View view){
        List=recorder.ListTimeSort(List);
        populateListView();
        System.out.println(name);
    }
    public void Replay(View view){
        String temp = inputname.getText().toString();
        if(temp==null){
            inputname.setText("");
            inputname.setHint("Please enter a name");
            return;
        }else if(!name.contains(temp)){
            inputname.setText("");
            inputname.setHint("No game name "+temp);
        }else{
            outputinstruction=instruction.get(name.indexOf(temp));
            Intent intent = new Intent(this,TwoPlayerGame.class);
            intent.putExtra("instruction",outputinstruction);
            startActivity(intent);
        }
    }
}
