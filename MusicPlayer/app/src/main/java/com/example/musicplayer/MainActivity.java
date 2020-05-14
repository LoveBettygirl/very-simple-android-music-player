package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 3秒后自动切换界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, SelectMusicActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in); // 淡出淡入效果
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.main_background);
        InputStream is;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        is = getResources().openRawResource(+R.drawable.welcome);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
        BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
        layout.setBackgroundDrawable(bd);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.exit(0);
        return super.onKeyDown(keyCode, event);
    }
}
