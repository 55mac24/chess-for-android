package edu.rutgers.dialogWindows;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class GameStatusDialogFragment extends DialogFragment{
    private String title;
    private String msg;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //store in file

                    }
                });

        return builder.create();
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }

}
