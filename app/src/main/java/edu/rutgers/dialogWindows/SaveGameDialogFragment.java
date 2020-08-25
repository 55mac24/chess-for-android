package edu.rutgers.dialogWindows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import edu.rutgers.chess.R;


public class SaveGameDialogFragment extends AppCompatDialogFragment {
    private EditText name;
    public interface SaveGameDialoglistener{
        void savegame(String name);
    }
    private SaveGameDialoglistener listerner;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.save_dialog,null);
        builder.setView(view)
                .setTitle("Save Name")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gamename = name.getText().toString();
                        listerner.savegame(gamename);
                    }
                });
        name=view.findViewById(R.id.gamename);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listerner = (SaveGameDialoglistener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
                    "must implement SaveGameDialoglistener");
        }
    }
}
