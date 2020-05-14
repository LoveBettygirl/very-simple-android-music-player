package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class SettingsAdapter extends BaseAdapter {

    private Context context; // 使用此Adapter的源上下文
    private int[] layoutType; // 指定ListView的每一项要使用布局的类型
    private List<String> list; // 数据源
    private LayoutInflater inflater;
    /* 缓存ListView的每一个item所对应的控件，减少每次调用findViewById()的代价 */
    private View0 view0;
    private View1 view11;
    private View1 view12;
    private View2 view2;

    private class View0 {
        CheckBox checkBox;
        TextView textView;
    }

    private class View1 {
        TextView textView1;
        TextView textView2;
    }

    private class View2 {
        TextView textView;
    }

    public SettingsAdapter(Context context, int[] type, List<String> src) {
        super();
        this.context = context;
        this.layoutType = type;
        this.list = src;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutType[position];
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 1) {
            if (list.get(0).equals("F"))
                return true;
            else if (list.get(0).equals("T")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final int pos = position;
        // 为ListView的每个item创建控件
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = inflater.inflate(R.layout.view0, parent, false);
                    view0 = new View0();
                    view0.textView = (TextView) convertView.findViewById(R.id.textview_0);
                    view0.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_0);
                    convertView.setTag(view0);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.view1, parent, false);
                    if (position == 1) {
                        view11 = new View1();
                        view11.textView1 = (TextView) convertView.findViewById(R.id.textview_11);
                        view11.textView2 = (TextView) convertView.findViewById(R.id.textview_12);
                        convertView.setTag(view11);
                    }
                    else if (position == 2) {
                        view12 = new View1();
                        view12.textView1 = (TextView) convertView.findViewById(R.id.textview_11);
                        view12.textView2 = (TextView) convertView.findViewById(R.id.textview_12);
                        convertView.setTag(view12);
                    }
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.view2, parent, false);
                    view2 = new View2();
                    view2.textView = (TextView) convertView.findViewById(R.id.textview_2);
                    convertView.setTag(view2);
                    break;
            }
        }
        else {
            switch (type) {
                case 0:
                    view0 = (View0) convertView.getTag();
                    break;
                case 1:
                    if (position == 1) {
                        view11 = (View1) convertView.getTag();
                    }
                    else if (position == 2) {
                        view12 = (View1) convertView.getTag();
                    }
                    break;
                case 2:
                    view2 = (View2) convertView.getTag();
                    break;
            }
        }

        // 在这里设置ListView的每个item中的控件应该显示的内容和监听事件
        switch (position) {
            case 0:
                if (((String)getItem(position)).equals("T")) {
                    view0.checkBox.setChecked(true);
                }
                else if (((String)getItem(position)).equals("F")) {
                    view0.checkBox.setChecked(false);
                }
                view0.textView.setText(R.string.all_path);
                final View finalConvertView = convertView;
                view0.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            list.set(0, "T");
                            view11.textView1.setTextColor(finalConvertView.getResources().getColor(R.color.lightGray));
                            view11.textView2.setTextColor(finalConvertView.getResources().getColor(R.color.lightGray));
                            view11.textView2.setText(finalConvertView.getResources().getString(R.string.all_path));
                        }
                        else {
                            list.set(0, "F");
                            view11.textView1.setTextColor(finalConvertView.getResources().getColor(R.color.black));
                            view11.textView2.setTextColor(finalConvertView.getResources().getColor(R.color.defaultGray));
                            view11.textView2.setText((String)getItem(pos));
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
            case 1:
                view11.textView1.setText(R.string.set_path);
                view11.textView2.setText((String)getItem(position));
                if (list.get(0).equals("F")) {
                    view11.textView1.setTextColor(convertView.getResources().getColor(R.color.black));
                    view11.textView2.setTextColor(convertView.getResources().getColor(R.color.defaultGray));
                    view11.textView2.setText((String)getItem(position));
                }
                else if (list.get(0).equals("T")) {
                    view11.textView1.setTextColor(convertView.getResources().getColor(R.color.gray));
                    view11.textView2.setTextColor(convertView.getResources().getColor(R.color.gray));
                    view11.textView2.setText(convertView.getResources().getString(R.string.all_path));
                }
                break;
            case 2:
                view12.textView1.setText(R.string.format_filter);
                view12.textView2.setText((String)getItem(position));
                break;
            case 3:
                view2.textView.setText(R.string.about);
                break;
        }
        return convertView;
    }
}
