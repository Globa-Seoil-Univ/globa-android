package team.y2k2.globa.notification.total;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class TotalFragmentAdapter extends RecyclerView.Adapter<TotalFragmentAdapter.MyViewHolder> {

    NotificationActivity activity;
    List<TotalFragmentItem> items;
    NotificationViewModel notificationViewModel;

    public TotalFragmentAdapter(List<TotalFragmentItem> items, NotificationActivity activity) {
        this.items = items;
        this.activity = activity;
        notificationViewModel = new ViewModelProvider(activity).get(NotificationViewModel.class);
    }

    @NonNull
    @Override
    public TotalFragmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_total, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TotalFragmentAdapter.MyViewHolder holder, int position) {

        TotalFragmentItem item = items.get(position);

        if(item.getProfile() != null && !item.getProfile().isEmpty()) {
            // 프로필이 있을 때
            Glide.with(holder.itemView.getContext())
                    .load(convertGsToHttps(item.getProfile()))
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
        } else {
            // 프로필이 없을 때
            Glide.with(holder.itemView.getContext())
                    .load(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
            Log.d("알림 프로필", "알림 프로필 없음, 아이템 위치: " + position);
        }
        
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

        if(item.getType().equals("2")) {
            // 공유 초대 알림
            holder.confirmBtn.setOnClickListener(v -> {
                // 공유 수락 버튼 이벤트
                Log.d("수락 버튼", "공유 초대 수락 버튼 클릭");
                Log.d("수락 버튼", "folder_id : " + item.getFolderId() + " share_id : " + item.getShareId());
                notificationViewModel.acceptInvite(item.getFolderId(), item.getShareId());
            });
            holder.cancelBtn.setOnClickListener(v -> {
                // 공유 거절 버튼 이벤트
                Log.d("거절 버튼", "공유 초대 거절 버튼 클릭");
                Log.d("거절 버튼", "folder_id : " + item.getFolderId() + " share_id : " + item.getShareId() + " notification_id : " + item.getNotificationId());
                notificationViewModel.denyInvite(item.getFolderId(), item.getShareId(), item.getNotificationId());
            });
        } else {
            // 공유 초대 알림이 아닌 경우 버튼 숨기기
            holder.confirmBtn.setVisibility(View.GONE);
            holder.cancelBtn.setVisibility(View.GONE);
        }

        if(item.getContent().isEmpty()) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView title;
        TextView content;
        TextView createdTime;
        Button confirmBtn;
        Button cancelBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageview_item_notification_total);
            title = itemView.findViewById(R.id.textview_item_notification_total_title);
            content = itemView.findViewById(R.id.textview_item_notification_total_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_total_createdtime);
            confirmBtn = itemView.findViewById(R.id.button_item_notification_total_access);
            cancelBtn = itemView.findViewById(R.id.button_item_notification_total_denied);
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
