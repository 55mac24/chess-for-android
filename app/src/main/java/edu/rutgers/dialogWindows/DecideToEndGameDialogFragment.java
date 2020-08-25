package edu.rutgers.dialogWindows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import edu.rutgers.chess.R;

public class DecideToEndGameDialogFragment extends DialogFragment {
    public interface DecideToEndGameDialogListener{
        public void onDialogYesClick(DialogFragment dialog);
        public void onDialogNoClick(DialogFragment dialog);
    }
    DecideToEndGameDialogListener listener;
    private String title = "",  msg = "Are you sure you want to ";
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (DecideToEndGameDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + "must implement listener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title + " Game")
                .setMessage(msg)
                .setPositiveButton(title.toLowerCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.onDialogYesClick(DecideToEndGameDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.noOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNoClick(DecideToEndGameDialogFragment.this);
                    }
                });

        return builder.create();
    }
    public void setTitle(String title){
        this.title = title;
        this.msg = this.msg + title.toLowerCase() + " the game?";
    }

}
