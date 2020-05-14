package com.example.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<String>();
    private SettingsAdapter adapter;
    private int format = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        /* 左上角返回按钮设置 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 设置显示返回按钮
        getSupportActionBar().setHomeButtonEnabled(true); // 设置返回按钮允许按下

        ListView listView = (ListView) findViewById(R.id.settings_view);
        // 接收来自启动该 Activity 的 Activity 传来的值
        boolean receiveBool = getIntent().getBooleanExtra("allMusic", false);
        String receive = null;
        if (!receiveBool) {
            list.add("F");
            receive = getIntent().getStringExtra("currentPath");
        }
        else {
            list.add("T");
        }
        if (receive != null)
            list.add(receive);
        else
            list.add("/sdcard");
        int receiveFormat = getIntent().getIntExtra("format", -1);
        if (receiveFormat != -1)
            format = receiveFormat;
        int[] layoutType = { 0, 1, 1, 2 }; // 指定ListView不同的item使用不同的Layout
        list.add("");
        list.add(null);
        switch (format) {
            case 0:
                list.set(2, getString(R.string.only_mp3));
                break;
            case 1:
                list.set(2, getString(R.string.only_m4a));
                break;
            case 2:
                list.set(2, getString(R.string.mp3_m4a));
                break;
            default:
                break;
        }
        adapter = new SettingsAdapter(SettingsActivity.this, layoutType, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String src = list.get(0);
                        if (src.equals("T"))
                            list.set(0, "F");
                        else if (src.equals("F"))
                            list.set(0, "T");
                    case 1:
                        openFileManager();
                        break;
                    case 2:
                        switchFormat();
                        break;
                    case 3:
                        new AboutDialog().showAboutDialog(SettingsActivity.this);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void openFileManager() {
        Intent intent = new Intent();
        intent.setClass(SettingsActivity.this, ChoosePathActivity.class);
        intent.putExtra("currentPath", list.get(1));
        SettingsActivity.this.startActivityForResult(intent, 0);
    }

    private void saveSettings() {
        Intent intent = new Intent();
        intent.putExtra("newCurrentPath", list.get(1));
        intent.putExtra("format", format);
        intent.putExtra("allMusic", list.get(0));
        setResult(0, intent);
        finish();
    }

    private void switchFormat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.format_filter));
        final String[] items = { getString(R.string.only_mp3),
                getString(R.string.only_m4a),
                getString(R.string.mp3_m4a) };
        final boolean[] checkedItems = new boolean[items.length];
        builder.setSingleChoiceItems(items, format, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
                checkedItems[which] = true;
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        format = i;
                        list.set(2, items[i]);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveSettings();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) { // 接收来自 ChoosePathActivity 传来的值
            String receive = data.getStringExtra("newCurrentPath");
            if (receive != null) {
                list.set(1, receive);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout)findViewById(R.id.settings_background);
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
            saveSettings();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
