package team.y2k2.globa.main.folder.add;

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

public class FolderAddAdapter extends RecyclerView.Adapter<FolderAddAdapter.MyViewHolder> {

    private List<FolderAddItem> itemList;
    private FolderAddActivity activity;

    public FolderAddAdapter(List<FolderAddItem> itemList, FolderAddActivity activity) {
        this.itemList = itemList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FolderAddAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_share, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAddAdapter.MyViewHolder holder, int position) {

        FolderAddItem item = itemList.get(position);
        if(item.getProfile() != null) {
            Glide.with(activity).load(item.getProfile())
                    .error(R.drawable.profile_user)
                    .into(holder.profile);
        } else {
            Glide.with(activity).load(R.drawable.profile_user)
                    .error(R.drawable.profile_user)
                    .into(holder.profile);
        }

        holder.layout.setOnClickListener(v -> {
            removeItem(position);
        });

    }

    @Override
    public int getItemCount() {
        return (itemList != null ? itemList.size() : 0);
    }

    public FolderAddItem getItem(int position) {
        return itemList.get(position);
    }

    public void removeItem(int position) {
        if(position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profile;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.imageview_foldershare_profile_item);
            layout = itemView.findViewById(R.id.constraintlayout_foldershare_item);
        }
    }
}
