package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChoosePathActivity extends AppCompatActivity {

    private static final String ROOT_PATH = "/";
    private static String currentAbsPath = ROOT_PATH;
    private static String currentRelPath = "";
    private List<String> paths = new ArrayList<String>();
    private ArrayAdapter adapter;
    private TextView currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_path);
        setTitle(getString(R.string.choose_path));

        /* 左上角返回按钮设置（这里的作用为返回到上一级目录） */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 设置显示返回按钮
        getSupportActionBar().setHomeButtonEnabled(true); // 设置返回按钮允许按下

        // 接收来自启动该 Activity 的 Activity 传来的值
        String receive = getIntent().getStringExtra("currentPath");
        if (receive != null)
            currentAbsPath = receive;
        File initFile;
        // 先要验证一下路径的合法性，如果不合法，就尝试回退到上级目录，直到根目录为止
        do {
            initFile = new File(currentAbsPath);
            if (initFile.exists() && initFile.isDirectory())
                break;
            currentAbsPath = currentAbsPath.substring(0, currentAbsPath.lastIndexOf('/'));
        } while (currentAbsPath.length() != 0);
        if (currentAbsPath.length() == 0) {
            currentAbsPath = ROOT_PATH;
            initFile = new File(currentAbsPath);
        }
        final File[] initFiles = initFile.listFiles();
        if (initFiles != null && initFiles.length > 0) {
            for (File f : initFiles) {
                if (f.isDirectory())
                    paths.add(f.getName());
            }
        }
        currentPath = (TextView) findViewById(R.id.current_path);
        currentPath.setText(currentAbsPath);
        ListView path = (ListView) findViewById(R.id.path_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, paths);
        path.setAdapter(adapter);
        path.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentRelPath = paths.get(position);
                if (!currentAbsPath.equals(ROOT_PATH))
                    currentAbsPath += "/";
                currentAbsPath += currentRelPath;
                currentPath.setText(currentAbsPath);
                File file = new File(currentAbsPath);
                File[] files = file.listFiles();
                paths.clear();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        if (f.isDirectory())
                            paths.add(f.getName());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /** 回退到上级目录 */
    private void backToLast() {
        if (!currentAbsPath.equals(ROOT_PATH)) {
            currentAbsPath = currentAbsPath.substring(0, currentAbsPath.lastIndexOf('/'));
            if (currentAbsPath.length() > 0)
                currentRelPath = currentAbsPath.substring(currentAbsPath.lastIndexOf('/') + 1);
            else {
                currentAbsPath = ROOT_PATH;
                currentRelPath = "";
            }
            File file = new File(currentAbsPath);
            File[] files = file.listFiles();
            paths.clear();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory())
                        paths.add(f.getName());
                }
            }
            currentPath.setText(currentAbsPath);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_path_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                Intent intent = new Intent();
                intent.putExtra("newCurrentPath", currentAbsPath);
                setResult(0, intent);
                finish();
                break;
            case android.R.id.home:
                backToLast();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout)findViewById(R.id.choose_path_background);
        InputStream is;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        is = getResources().openRawResource(+R.drawable.bg);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
        BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
        bd.setAlpha(125);
        layout.setBackgroundDrawable(bd);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(1, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
