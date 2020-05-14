package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicinfo.MusicInfo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectMusicActivity extends AppCompatActivity {

    private String musicPath;
    private int format = 0;
    private boolean allMusic = true;
    private MyArrayAdapter adapter;
    private List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    private TextView textView;
    private ListView listView;
    private SearchView searchView;
    private boolean searchMode = false;
    private String[] strings;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_music);
        setTitle(getString(R.string.music_player));

        String[] STORAGE_PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE };
        strings = new String[]{ getString(R.string.mp3_music_count), getString(R.string.m4a_music_count),
        getString(R.string.music_count) };
        textView = (TextView) findViewById(R.id.music_count);
        listView = (ListView) findViewById(R.id.music_view);
        listView.setTextFilterEnabled(true);
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setVisibility(View.VISIBLE);
        openSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                searchMode = newText.length() != 0;
                // 使用用户输入的内容对ListView的item进行过滤，无输入则取消过滤，且不会显示黑色弹框
                adapter.getFilter().filter(newText);
                setTextView();
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                setTextView(); // 实时更新被过滤得到的item数
            }
        });
        // Android 6.0 及以上需要动态申请SD卡访问权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int storagePermission = SelectMusicActivity.this.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //检测是否有权限，如果没有权限，就需要申请
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                SelectMusicActivity.this.requestPermissions(STORAGE_PERMISSIONS, 1);
            }
            else {
                // 如果有权限就直接初始化音乐列表
                initMusicList();
            }
        }
        else {
            // Android 版本低于6.0则直接初始化音乐列表（要申请的权限已在AndroidManifest.xml中列出）
            initMusicList();
        }
        SelectMusicActivity.this.registerForContextMenu(listView); // 为ListView的每一项注册上下文菜单
    }

    private void setTextView() {
        if (searchMode)
            textView.setText(String.format(getString(R.string.search_result), searchText,
                    adapter.getCount(), musicList.size()));
        else
            textView.setText(String.format(strings[format], musicList.size()));
    }

    private void openSearchView() {
        if (searchView.getVisibility() == View.VISIBLE)
            searchView.setVisibility(View.GONE);
        else
            searchView.setVisibility(View.VISIBLE);
    }

    private void initMusicList() {
        musicPath = Environment.getExternalStorageDirectory().getPath();
        musicList.clear();
        getMusicList(musicPath);
        adapter = new MyArrayAdapter<MusicInfo>(this, android.R.layout.simple_list_item_1,musicList);
        listView.setAdapter(adapter);
        setTextView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic((int)(adapter.getItemId(position)));
            }
        });
    }

    private void playMusic(int position) {
        final String TAG = Tag.getTag(getClass().getSimpleName());
        MusicInfo info = musicList.get(position);
        String currAbsPath = info.getAbsPath();
        Log.d(TAG, currAbsPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            Uri uri;
            // Android 7.0 及以上需要用 FileProvider 创建uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Context context = getApplicationContext();
                File file = info.getFile();
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context,context.getPackageName() + ".provider", file);
            }
            else {
                uri = Uri.parse("file://" + currAbsPath);
            }
            intent.setDataAndType(uri, "audio/" + info.getMusicFormat());
            startActivity(intent);
        }
        catch (Exception e) {
            Toast.makeText(SelectMusicActivity.this,
                    String.format(getString(R.string.play_music_error), currAbsPath),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void deleteMusic(int position) {
        final MusicInfo info = musicList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectMusicActivity.this);
        builder.setTitle(getString(R.string.delete_music_or_not));
        builder.setMessage(String.format(getString(R.string.delete_music_info), info.getAbsPath()));
        final int pos = position;
        builder.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = info.getFile();
                if (file.delete()) {
                    musicList.remove(pos);
                    adapter.notifyDataSetChanged();
                    if (searchMode) // 如果还在搜索，就重新过滤一下
                        adapter.getFilter().filter(searchText);
                    setTextView();
                }
            }
        });
        builder.setNegativeButton(getText(R.string.cancel), null);
        builder.create().show();
    }

    private void getMusicList(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && allMusic) {
                    getMusicList(file.getAbsolutePath());
                }
                else {
                    String currPath = file.getAbsolutePath();
                    switch (format) {
                        case 0:
                            if (currPath.toLowerCase().endsWith(".mp3"))
                                musicList.add(new MusicInfo(file));
                            break;
                        case 1:
                            if (currPath.toLowerCase().endsWith(".m4a"))
                                musicList.add(new MusicInfo(file));
                            break;
                        case 2:
                            if (currPath.toLowerCase().endsWith(".mp3") ||
                                    currPath.toLowerCase().endsWith(".m4a"))
                                musicList.add(new MusicInfo(file));
                            break;
                    }
                }
            }
        }
    }

    private void showMusicInfo(int position) {
        MusicInfo info = musicList.get(position);
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(SelectMusicActivity.this);
        final View dialogView = LayoutInflater.from(SelectMusicActivity.this)
                .inflate(R.layout.music_info_dialog,null);
        infoDialog.setTitle(getString(R.string.music_details));
        infoDialog.setView(dialogView); // 向对话框加载自定义布局文件
        TextView fileNameText = (TextView) dialogView.findViewById(R.id.file_name_text);
        TextView fileFormatText = (TextView) dialogView.findViewById(R.id.file_format_text);
        TextView fileDateText = (TextView) dialogView.findViewById(R.id.file_date_text);
        TextView fileSizeText = (TextView) dialogView.findViewById(R.id.file_size_text);
        TextView titleText = (TextView) dialogView.findViewById(R.id.title_text);
        TextView artistText = (TextView) dialogView.findViewById(R.id.artist_text);
        TextView albumText = (TextView) dialogView.findViewById(R.id.album_text);
        TextView durationText = (TextView) dialogView.findViewById(R.id.duration_text);
        TextView absPathText = (TextView) dialogView.findViewById(R.id.abs_path_text);
        ImageView albumPic = (ImageView) dialogView.findViewById(R.id.album_pic);
        fileNameText.setText(String.format(getString(R.string.file_name_text), info));
        fileFormatText.setText(String.format(getString(R.string.file_format_text), info.getMusicFormat()));
        fileDateText.setText(String.format(getString(R.string.file_date_text), info.getMusicDate()));
        fileSizeText.setText(String.format(getString(R.string.file_size_text), info.getMusicSize()));
        absPathText.setText(String.format(getString(R.string.abs_path_text), info.getAbsPath()));
        String unknown = getString(R.string.unknown);
        if (info.getTitle() != null) {
            titleText.setText(String.format(getString(R.string.title_text), info.getTitle()));
        }
        else {
            titleText.setText(String.format(getString(R.string.title_text), unknown));
        }
        if (info.getArtist() != null) {
            artistText.setText(String.format(getString(R.string.artist_text), info.getArtist()));
        }
        else {
            artistText.setText(String.format(getString(R.string.artist_text), unknown));
        }
        if (info.getAlbum() != null) {
            albumText.setText(String.format(getString(R.string.album_text), info.getAlbum()));
        }
        else {
            albumText.setText(String.format(getString(R.string.album_text), unknown));
        }
        if (info.getDuration() != null) {
            durationText.setText(String.format(getString(R.string.duration_text), info.getDuration()));
        }
        else {
            durationText.setText(String.format(getString(R.string.duration_text), unknown));
        }
        if (info.getPic() != null) {
            if (info.getPic().length != 0) {
                // 将byte[] 转为 drawable 并显示在 ImageView 控件中
                Bitmap bitmap = BitmapFactory.decodeByteArray(info.getPic(), 0, info.getPic().length);
                BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
                albumPic.setImageDrawable(bd);
            }
        }
        infoDialog.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        infoDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_music_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.music_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.search_item:
                openSearchView();
                break;
            case R.id.settings_item:
                intent = new Intent();
                intent.setClass(SelectMusicActivity.this, SettingsActivity.class);
                intent.putExtra("currentPath", musicPath);
                intent.putExtra("allMusic", allMusic);
                intent.putExtra("format", format);
                SelectMusicActivity.this.startActivityForResult(intent, 0);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // 获取对应于ListView的position
        int position = (int)(adapter.getItemId(menuInfo.position));
        switch(item.getItemId()) {
            case R.id.play_music_item:
                playMusic(position);
                break;
            case R.id.music_info_item:
                showMusicInfo(position);
                break;
            case R.id.delete_music_item:
                deleteMusic(position);
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) { // 接收来自 SettingsActivity 传来的值
            String receiveAll = data.getStringExtra("allMusic");
            if (receiveAll != null) {
                if (receiveAll.equals("T"))
                    allMusic = true;
                else if (receiveAll.equals("F"))
                    allMusic = false;
            }
            String receivePath = null;
            if (!allMusic) {
                receivePath = data.getStringExtra("newCurrentPath");
                if (receivePath != null) {
                    musicPath = receivePath;
                }
            }
            else {
                musicPath = Environment.getExternalStorageDirectory().getPath();
            }
            int receiveFormat = data.getIntExtra("format", -1);
            if (receiveFormat != -1) {
                format = receiveFormat;
            }
            musicList.clear();
            getMusicList(musicPath);
            adapter.notifyDataSetChanged();
            if (searchMode) // 如果还在搜索，就重新过滤一下
                adapter.getFilter().filter(searchText);
            setTextView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String sdCard = Environment.getExternalStorageState();
                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                    initMusicList();
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SelectMusicActivity.this,
                                getString(R.string.sdcard_not_permitted), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout)findViewById(R.id.select_music_background);
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

    private long exitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(SelectMusicActivity.this, getString(R.string.exit_program),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        SelectMusicActivity.this.unregisterForContextMenu(listView);
        super.onDestroy();
    }
}
