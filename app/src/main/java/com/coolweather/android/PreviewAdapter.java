package com.coolweather.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30.
 */
public class PreviewAdapter extends ArrayAdapter<PreviewItem> {

    private int resourceId;
    public PreviewAdapter(Context context,int textViewResourceId,List<PreviewItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        PreviewItem pre=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView name=(TextView)view.findViewById(R.id.previewitem_cityname);
        TextView degree=(TextView)view.findViewById(R.id.previewitem_degree);
        name.setText(pre.getCityName());
        degree.setText(pre.getDregree());
        return view;
    }
}
