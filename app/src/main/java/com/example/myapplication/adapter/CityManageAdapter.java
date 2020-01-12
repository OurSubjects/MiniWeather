package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.myClass.City;
import java.util.List;

public class CityManageAdapter extends RecyclerView.Adapter<CityManageAdapter.MyViewHolder>{
    private List<City> locationManagementList;
    private OnItemClickListener onItemClickListener = null;; //声明点击接口

    static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cityView; //保存子项最外层布局实例
        TextView location_city;
        TextView location_province;
        ImageView deleteImage;
        ImageView positionImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            cityView = (CardView)itemView;
            location_city = (TextView) itemView.findViewById(R.id.location_city_rv);
            location_province = (TextView) itemView.findViewById(R.id.location_province_rv);
            deleteImage = (ImageView)itemView.findViewById(R.id.remove_rv);
            positionImage= (ImageView)itemView.findViewById(R.id.isPosition);
        }
    }
    public CityManageAdapter(List<City> locationManagementList){
        this.locationManagementList = locationManagementList;
    }
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_1,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        final City city = locationManagementList.get(position);
        holder.location_city.setText(city.getCity());
        holder.location_province.setText(city.getCityParent());
        if(city.isChoose()){
            holder.positionImage.setImageResource(R.drawable.positioning);
        }
        holder.cityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在ItemView的地方进行监听点击事件，并且实现接口
                onItemClickListener.onItemClick(v,position, city);
            }
        });
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在ImageView的地方进行监听点击事件，并且实现接口
                onItemClickListener.onImageClick(v,position, city);
            }
        });
    }

    public int getItemCount() {
        return locationManagementList.size();
    }
    /**
     * 定义RecyclerView选项单击事件的回调接口
     */
    public static interface OnItemClickListener{
        void onItemClick(View v,int position, City city);  //处理item点击事件
        void onImageClick(View v,int position, City city);  //处理Image删除的点击事件
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
