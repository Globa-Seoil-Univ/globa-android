package team.y2k2.globa.folder.permission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.folder.permission.spinner.FolderPermissionSpinnerAdapter;
import team.y2k2.globa.folder.permission.spinner.FolderPermissionSpinnerModel;

public class FolderPermissionItemAdapter extends RecyclerView.Adapter<FolderPermissionItemAdapter.AdapterViewHolder> {
    ArrayList<FolderPermissionItem> items;

   FolderPermissionSpinnerModel model = new FolderPermissionSpinnerModel();

    public FolderPermissionItemAdapter(ArrayList<FolderPermissionItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FolderPermissionItemAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_permission, parent, false);
        return new FolderPermissionItemAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderPermissionItemAdapter.AdapterViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.profileImage.setImageResource(items.get(position).getProfileImage());


        FolderPermissionSpinnerAdapter adapter = new FolderPermissionSpinnerAdapter(holder.itemView.getContext(), model.getOptions());
        holder.spinner.setAdapter(adapter);
        holder.spinner.setDropDownVerticalOffset(5);

    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;
        TextView name;
        ImageView profileImage;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            spinner = itemView.findViewById(R.id.spinner_folder_permission);
            name = itemView.findViewById(R.id.textview_folder_permission_name);
            profileImage = itemView.findViewById(R.id.imageview_folder_permission_profile_image);
        }
    }
}