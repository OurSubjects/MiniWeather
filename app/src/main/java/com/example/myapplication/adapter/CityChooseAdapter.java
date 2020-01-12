package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.myClass.City;

import java.util.List;

public class CityChooseAdapter extends ArrayAdapter<City> {
    private int resourceId;
    public CityChooseAdapter(Context context, int resourceId, List<City> objects) {
        super(context, resourceId, objects);
        this.resourceId=resourceId;
    }

    //这个方法在每个子项被滚动到屏幕内的时候会被调用
    public View getView(int position, View convertView, ViewGroup parent) {
        City cityObject = getItem(position);
        View view;
        ViewHolder viewHolder;//定义一个viewHolder保存控件id
        //避免重复加载布局
        if(convertView == null){
            view =  LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.cityName = (TextView) view.findViewById(R.id.location_li);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        if(cityObject.getCityParent().equals("")){
            viewHolder.cityName.setText(cityObject.getCity()+"-"+cityObject.getCity());
        }else {
            viewHolder.cityName.setText(cityObject.getCity()+"-"+cityObject.getCityParent());
        }
        return view;
    }
    class ViewHolder{
        TextView cityName;
    }
}
