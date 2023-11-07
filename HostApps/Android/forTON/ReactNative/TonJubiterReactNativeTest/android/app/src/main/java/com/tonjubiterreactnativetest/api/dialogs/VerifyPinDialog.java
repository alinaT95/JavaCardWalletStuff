//package com.tonjubiterreactnativetest.api.dialogs;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.tonjubiterreactnativetest.R;

//public class VerifyPinDialog {
//
//    private AlertDialog mDialog;
//    private EditText mEditText;
//
//
//    public VerifyPinDialog(Context context, final Callback listener) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_verify_pin, null);
//
//        mEditText = view.findViewById(R.id.verify_et);
//        mDialog = new AlertDialog.Builder(context)
//                .setTitle("Please enter PIN")
//                .setView(view)
//                .setCancelable(false)
//                .create();
//        ((Button) view.findViewById(R.id.btn_sure)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        listener.onClickListener(getPin());
//                    }
//                }).start();
//            }
//        });
//    }
//
//    public interface Callback {
//        void onClickListener(String pin);
//    }
//
//    public String getPin() {
//        return mEditText.getText().toString().trim();
//    }
//
//    public void show() {
//        mDialog.show();
//    }
//}
