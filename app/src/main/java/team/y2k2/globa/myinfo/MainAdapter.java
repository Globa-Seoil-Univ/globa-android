package team.y2k2.globa.myinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{

    private List<MainData> itemList;
    private Context context;

    public MainAdapter(List<MainData> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.myinfo_itemlist, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainAdapter.MyViewHolder holder, int position) {

        MainData item = itemList.get(position);
        holder.tv_title.setText(item.getTv_title());
        holder.tv_name.setText(item.getTv_name());
        holder.img_next.setImageResource(item.getImg_next());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_name;
        ImageView img_next;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.textview_myinfoItemlist_title);
            tv_name = itemView.findViewById(R.id.textview_myinfoItemlist_name);
            img_next = itemView.findViewById(R.id.imageview_myinfoItemlist_next);
        }

    }
}
