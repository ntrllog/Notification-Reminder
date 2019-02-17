package com.example.notificationreminder;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    OnMyDialogResult mDialogResult;

    public CustomDialog(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_notification);

        Button enter = findViewById(R.id.dialog_enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText content = findViewById(R.id.dialog_content);
                String s = content.getText().toString();
                if(mDialogResult != null){
                    mDialogResult.finish(s);
                }
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }

}
