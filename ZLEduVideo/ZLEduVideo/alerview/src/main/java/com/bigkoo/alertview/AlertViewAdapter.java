package com.bigkoo.alertview;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sai on 15/8/9.
 */
public class AlertViewAdapter extends BaseAdapter {
    private static final int LAYOUT_ID = R.layout.item_alertbutton;

    private List<String> mDatas;
    private List<String> mDestructive;
    private int mLayoutId;

    public AlertViewAdapter(int layoutId, List<String> datas, List<String> destructive) {
        this.mDatas = datas;
        this.mDestructive = destructive;
        mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = mDatas.get(position);
        Holder holder = null;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            int layoutId = mLayoutId == 0 || mLayoutId == -1 ? LAYOUT_ID : mLayoutId;
            view = inflater.inflate(layoutId, null);
            holder = createHolder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.UpdateUI(parent.getContext(), data, position);
        return view;
    }

    public Holder createHolder(View view) {
        return new Holder(view);
    }

    class Holder {
        private TextView tvAlert;

        public Holder(View view) {
            tvAlert = (TextView) view.findViewById(R.id.tvAlert);
        }

        public void UpdateUI(Context context, String data, int position) {
            if (!TextUtils.isEmpty(data)) {
                tvAlert.setText(Html.fromHtml(data));
            } else {
                tvAlert.setText(data);
            }
            if (mDestructive != null && mDestructive.contains(data)) {
                tvAlert.setTextColor(context.getResources().getColor(R.color.fontColorDeep));
            } else {
                tvAlert.setTextColor(context.getResources().getColor(R.color.fontColorDeep));
            }
        }
    }
}