package team.y2k2.globa.main.folder.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import team.y2k2.globa.R;

public class FolderAddAdapter extends RecyclerView.Adapter<FolderAddAdapter.ViewHolder> {

    private int[] mImages;
    private LayoutInflater mInflater;

    FolderAddAdapter(Context context, int[] images) {
        this.mInflater = LayoutInflater.from(context);
        this.mImages = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_folder_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageId = mImages[position];
        holder.imageView.setImageResource(imageId);
    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview_folderadd_profile);
        }

    }

}
