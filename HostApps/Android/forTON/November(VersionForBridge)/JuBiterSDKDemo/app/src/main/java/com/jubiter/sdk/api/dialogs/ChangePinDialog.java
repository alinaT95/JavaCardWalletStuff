package com.jubiter.sdk.api.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jubiter.sdk.api.R;

public class ChangePinDialog {
    private AlertDialog mDialog;
    private EditText mEditOldPin;
    private EditText mEditNewPin;


    public ChangePinDialog(Context context, final ChangePinDialog.Callback listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_pin, null);

        mEditOldPin = view.findViewById(R.id.old_pin);
        mEditNewPin = view.findViewById(R.id.new_pin);
        mDialog = new AlertDialog.Builder(context)
                .setTitle("Please enter old and new PIN")
                .setView(view)
                .setCancelable(false)
                .create();
        ((Button) view.findViewById(R.id.btn_sure)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onClickListener(getOldPin(), getNewPin());
                    }
                }).start();
            }
        });
    }

    public interface Callback {
        void onClickListener(String oldPin, String newPin);
    }

    public String getOldPin() {
        return mEditOldPin.getText().toString().trim();
    }

    public String getNewPin() {
        return mEditNewPin.getText().toString().trim();
    }

    public void show() {
        mDialog.show();
    }
}
