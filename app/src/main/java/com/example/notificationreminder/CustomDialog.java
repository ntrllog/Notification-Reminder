package com.example.notificationreminder;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    private OnMyDialogResult mDialogResult;
    private String content;

    CustomDialog(Activity a, String c) {
        super(a);
        this.content = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_notification);
        final EditText content = findViewById(R.id.dialog_content);
        content.setText(this.content); // grab notification text and put in in dialog
        content.setSelectAllOnFocus(true); // highlight text on popup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // bring up keyboard automatically
        content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // auto capitalize first letter

        Button enter = findViewById(R.id.dialog_enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }

}
