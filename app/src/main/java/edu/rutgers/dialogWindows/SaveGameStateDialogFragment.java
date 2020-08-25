package edu.rutgers.dialogWindows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.DialogFragment;

import edu.rutgers.chess.R;
import edu.rutgers.chess.TwoPlayerGame;

public class SaveGameStateDialogFragment extends DialogFragment {
    public interface SaveGameStateDialogListener{
        public boolean onDialogSaveClick(DialogFragment dialog,String name);
        public void onDialogDiscardClick(DialogFragment dialog);
    }
    SaveGameStateDialogListener listener;
    private EditText name;
    private TextView text;
    private String msg = "Would you like to save the game?";
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (SaveGameStateDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + "must implement listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.save_dialog,null);
        builder.setTitle("Save Game")
                .setView(view)
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gamename = name.getText().toString();
                        if(listener.onDialogSaveClick(SaveGameStateDialogFragment.this,gamename)){

                        }else{
                            Toast.makeText(getContext(),"Name duplicate, failed to save, please redo your instruction and save",Toast.LENGTH_LONG).show();
                        };
                    }
                })
                .setNegativeButton("discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogDiscardClick(SaveGameStateDialogFragment.this);
                    }
                });
        name=view.findViewById(R.id.gamename);
        text=view.findViewById(R.id.text);
        return builder.create();
    }


}
