package team.y2k2.globa.main.folder.permission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.main.folder.permission.spinner.FolderPermissionSpinnerAdapter;
import team.y2k2.globa.main.folder.permission.spinner.FolderPermissionSpinnerModel;

public class FolderPermissionItemAdapter extends RecyclerView.Adapter<FolderPermissionItemAdapter.AdapterViewHolder> {
    ArrayList<FolderPermissionItem> items;
    FolderPermissionSpinnerModel model = new FolderPermissionSpinnerModel();
    private ItemLongClickListener longClickListener;

    public FolderPermissionItemAdapter(ArrayList<FolderPermissionItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_permission, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        FolderPermissionItem item = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getProfileImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.profileImage);

        holder.name.setText(item.getName());
        holder.spinner.setSelection(item.getSelectedOption());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public FolderPermissionItem getItem(int position) {
        return items.get(position);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;
        TextView name;
        ImageView profileImage;
        FolderPermissionSpinnerAdapter adapter;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.spinner_folder_permission);
            name = itemView.findViewById(R.id.textview_folder_permission_name);
            profileImage = itemView.findViewById(R.id.imageview_folder_permission_profile_image);

            adapter = new FolderPermissionSpinnerAdapter(itemView.getContext(), model.getOptions());
            spinner.setAdapter(adapter);
            spinner.setDropDownVerticalOffset(5);

            // Spinner 선택 리스너 설정
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Spinner 선택했을 때
                    items.get(getAdapterPosition()).setSelectedOption(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

        }
        public String getPermission() {
            return spinner.getSelectedItem().toString();
        }

    }
}
