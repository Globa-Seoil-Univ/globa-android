package team.y2k2.globa.main.folder.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import team.y2k2.globa.R;

public class FolderShareAdapter extends RecyclerView.Adapter<FolderShareAdapter.MyViewHolder> {

    private List<FolderShareItem> itemList;
    private Context context;

    public FolderShareAdapter(List<FolderShareItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public FolderShareAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder_share, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderShareAdapter.MyViewHolder holder, int position) {

        FolderShareItem item = itemList.get(position);
        Glide.with(context).load(item.getImage()).into(holder.image);

        holder.layout.setOnClickListener(v -> {
            removeItem(position);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public FolderShareItem getItem(int position) {
        return itemList.get(position);
    }

    public void removeItem(int position) {
        if(position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageview_foldershare_profile_item);
            layout = itemView.findViewById(R.id.constraintlayout_foldershare_item);
        }
    }
}
