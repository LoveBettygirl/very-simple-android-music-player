package com.example.musicplayer;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MyArrayAdapter<T> extends ArrayAdapter<T> implements Filterable {
    private MyArrayFilter filter;
    private List<T> mList; // 对应于ListView显示的数据源
    private List<T> originalList; // 数据源的备份
    /* 记录ListView的每个被过滤之后显示item所对应源数据的位置，每次过滤前都会清空
    *  如果是空的，则说明过滤结果不包含任何item，或者取消过滤模式 */
    private List<Integer> sortedList = new ArrayList<>();

    public MyArrayAdapter(Context context, @LayoutRes int resource, List<T> list) {
        super(context, resource, list);
        this.mList = list;
        this.originalList = list;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        else {
            return mList.size();
        }

    }

    @Override
    public T getItem(int position) {
        if (mList == null) {
            return null;
        }
        else {
            return mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return sortedList.size() == 0 ? position : sortedList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyArrayFilter();
        }
        return filter;
    }

    private class MyArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (originalList == null) {
                originalList = new ArrayList<T>(mList);
            }
            sortedList.clear();
            // 如果过滤的源字符串为null或者长度为0，则取消过滤，从备份中恢复数据源
            if (charSequence == null || charSequence.length() == 0) {
                results.values = originalList;
                results.count = originalList.size();
            }
            else {
                final ArrayList<T> values;
                values = new ArrayList<>(originalList); // 将originalList拷贝到新的ArrayList中
                List<T> filteredList = new ArrayList<>();
                int count = values.size();
                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.toString().toLowerCase();
                    if (valueText.contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(value);
                        sortedList.add(i);
                    }
                }
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mList = (List<T>) results.values; // 将过滤结果作为新的数据源
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
