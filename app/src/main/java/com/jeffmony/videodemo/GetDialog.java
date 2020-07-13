package com.jeffmony.videodemo;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 作者： zcc on 2020/7/11 14:43
 * 邮箱：m15632271759_1@163.com
 */
public class GetDialog {
    private Dialog twoInputDialog = null;

    public interface EdtStrResult {
        void onStr(String str1);
    }
    public Dialog getedtTwoDialog2(final Context context,EdtStrResult click) {
        twoInputDialog = new Dialog(context, R.style.dialog_style);
        LinearLayout layout;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_input_url, null);
        EditText edt1 = layout.findViewById(R.id.edt_url);
        layout.findViewById(R.id.tv_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1 = edt1.getText().toString().trim();
                if(TextUtils.isEmpty(str1)){
                    Toast.makeText(context,"输入网址为空",Toast.LENGTH_SHORT);
                    return;
                }
                click.onStr(str1);
                twoInputDialog.dismiss();
            }
        });
        layout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoInputDialog.dismiss();
            }
        });
        Window window = twoInputDialog.getWindow();
        window.setContentView(layout);
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setWindowAnimations(R.style.popup_alpha_style);
        return twoInputDialog;
    }
    public Dialog getTipDialog(final Context context,String content) {
       Dialog tipDialog = new Dialog(context, R.style.dialog_style);
        LinearLayout layout;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_tips, null);
        TextView tvContent = layout.findViewById(R.id.tv_content);
        if(!TextUtils.isEmpty(content)){
            tvContent.setText(content);
        }
        layout.findViewById(R.id.tv_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
        layout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
        Window window = tipDialog.getWindow();
        window.setContentView(layout);
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setWindowAnimations(R.style.popup_alpha_style);
        return tipDialog;
    }
}
