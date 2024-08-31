package team.y2k2.globa.main.folder.permission;

import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.main.folder.permission.spinner.FolderPermissionSpinnerAdapter;
import team.y2k2.globa.main.folder.permission.spinner.FolderPermissionSpinnerModel;

public class FolderPermissionItemAdapter extends RecyclerView.Adapter<FolderPermissionItemAdapter.AdapterViewHolder> {
    ArrayList<FolderPermissionItem> items;
    FolderPermissionSpinnerModel model = new FolderPermissionSpinnerModel();
    private onItemLongClickListener longClickListener;

    public interface onItemLongClickListener {
        void onItemLongClick(int position);
    }

    public FolderPermissionItemAdapter(ArrayList<FolderPermissionItem> items, onItemLongClickListener longClickListener) {
        this.items = items;
        this.longClickListener = longClickListener;
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
                .load(convertGsToHttps(item.getProfileImageUrl()))
                .error(R.mipmap.ic_launcher)
                .into(holder.profileImage);

        holder.name.setText(item.getName());
        holder.spinner.setSelection(item.getSelectedOption());

        holder.itemView.setOnLongClickListener(v -> {
            Log.d("롱클릭", "롱클릭 발생!");
            if(longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }

            return true;
        });

    }

    public void remove(int position) {
        try {
            items.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
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

    public static String convertGsToHttps(String gsUrl) {
        if (!gsUrl.startsWith("gs://")) {
            throw new IllegalArgumentException("Invalid gs:// URL");
        }

        // Extract bucket name and path
        int bucketEndIndex = gsUrl.indexOf("/", 5); // Find the end of bucket name
        if (bucketEndIndex == -1 || bucketEndIndex == gsUrl.length() - 1) {
            throw new IllegalArgumentException("Invalid gs:// URL format");
        }

        String bucketName = gsUrl.substring(5, bucketEndIndex);
        String filePath = gsUrl.substring(bucketEndIndex + 1);

        // URL encode the file path
        String encodedFilePath;
        try {
            encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL encoding failed", e);
        }

        // Construct the HTTPS URL
        String httpsUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName +
                "/o/" + encodedFilePath + "?alt=media";

        return httpsUrl;
    }
}
