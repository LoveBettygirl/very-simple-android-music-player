package com.example.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dialog);
        setTitle("About");
        /* 如果使用Intent打开这个对话框将是以Activity形式打开，会不太好看 */
        /* 如果要使用Intent打开，要修改about_yes_button的android:visibility属性为visible */
        Button button = (Button)findViewById(R.id.about_yes_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialog.this.finish();
            }
        });
    }

    public void showAboutDialog(AppCompatActivity srcActivity) {
        AlertDialog.Builder aboutDialog =
                new AlertDialog.Builder(srcActivity);
        final View dialogView = LayoutInflater.from(srcActivity)
                .inflate(R.layout.activity_about_dialog,null);
        aboutDialog.setTitle(dialogView.getResources().getString(R.string.about));
        aboutDialog.setView(dialogView); // 向对话框加载自定义布局文件
        aboutDialog.setPositiveButton(dialogView.getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        aboutDialog.show();
    }
}
