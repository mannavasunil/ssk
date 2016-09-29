package com.dst.dt203657.sharearide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dst.dt203657.sharearide.com.dst.sharearide.representations.OptARide;
import com.google.android.gms.plus.PlusOneButton;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


public class OpterDialogFragment extends DialogFragment {

    public interface OpterDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, OptARide source,OptARide target);
    }

    private OpterDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OpterDialogListener)context.getApplicationContext();
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Opter Information")
                .setPositiveButton("Opt A Ride", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notifyToTarget(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                notifyToTarget(Activity.RESULT_CANCELED);
                            }
                        });
        return builder.create();
    }

    private void notifyToTarget(int code) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), code, null);
        }
    }*/



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Opter Information");

        LayoutInflater inflater = getActivity().getLayoutInflater();
       final  View dialogView = inflater.inflate(R.layout.opter_info_save_dialog, null);
        builder.setView(dialogView)
        .setPositiveButton("Opt A Ride", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText opterName = (EditText)dialogView.findViewById(R.id.opterName);
                        EditText opterPhone = (EditText) dialogView.findViewById(R.id.opterPhone);
                        if(opterName != null && opterPhone != null && isEnteredDataValid(opterName,opterPhone)) {
                            OptARide optARide = new OptARide();
                            optARide.setOpterName(opterName.getText().toString());
                            optARide.setOpterMobileNumber(BigInteger.valueOf(Long.parseLong(opterPhone.getText().toString())));

                            mListener.onDialogPositiveClick(OpterDialogFragment.this, optARide,null);
                        }

                    }
        })
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                    OpterDialogFragment.this.getDialog().cancel();
                        }
                    });

        return builder.create();

                }


    public boolean isEnteredDataValid(final EditText opterName,final EditText opterPhone) {
        boolean isEnteredDataValid = true;
        if(opterName.getText().toString().trim().equals("") ) {
            opterName.setError( "Name is required!" );
            isEnteredDataValid = false;
        } else if(opterPhone.getText().toString().trim().equals("")) {
            opterPhone.setError( "Phone Number is required!" );
            isEnteredDataValid = false;
        }
        return isEnteredDataValid;
    }

}

