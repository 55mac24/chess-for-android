package edu.rutgers.dialogWindows;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import edu.rutgers.chess.R;

public class PromotePieceDialogFragment extends DialogFragment {
    private String pieceToPromote = "";
    private String[] possiblePiecesToPromote = {"R", "K", "B", "Q", "P"};

    public interface PromotePieceDialogListener{
        public void onDialogSelectionClick(DialogFragment dialog, String promoteChessPiece);
    }
    PromotePieceDialogListener listener;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (PromotePieceDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()
            + "must implement listener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Chess Piece for Promotion")
                .setItems(R.array.pieces, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPieceToPromote(which);
                        listener.onDialogSelectionClick(PromotePieceDialogFragment.this, getPieceToPromote());
                    }
                });

        return builder.create();
    }

    private void setPieceToPromote(int pieceToPromote){
        this.pieceToPromote = possiblePiecesToPromote[pieceToPromote];
    }
    public String getPieceToPromote(){
        return this.pieceToPromote;
    }
}
