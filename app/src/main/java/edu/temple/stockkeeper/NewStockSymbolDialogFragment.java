package edu.temple.stockkeeper;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class NewStockSymbolDialogFragment extends DialogFragment {
    private View v;
    private InputMethodManager imm;
    private EditText editText;

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.fragment_new_stock_symbol_dialog, null);
        builder.setView(v)
                .setMessage(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // add symbol to file
//                        TextView tv = (TextView) v.findViewById(R.id.inputSymbol);
//                        String newSymbol = tv.getText().toString();
                        mListener.onDialogPositiveClick(NewStockSymbolDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onDialogNegativeClick(NewStockSymbolDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it

//        editText = (EditText) v.findViewById(R.id.inputSymbol);
//        editText.requestFocus();
//        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        editText.clearFocus();
//        View view = getActivity().getCurrentFocus();
//        if (view != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }

        super.onDismiss(dialog);
    }

    public View getView(){ return v; }


    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}
