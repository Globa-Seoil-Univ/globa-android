package team.y2k2.globa.notification.notice;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class NoticeFragmentAdapter extends RecyclerView.Adapter<NoticeFragmentAdapter.MyViewHolder> {

    NotificationActivity activity;
    List<NoticeFragmentItem> items;
    NotificationViewModel notificationViewModel;
    int whiteColor, primaryColor;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageRef;

    public NoticeFragmentAdapter(List<NoticeFragmentItem> items, NotificationActivity activity, NoticeFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.notificationViewModel = new ViewModelProvider(fragment).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }

    @NonNull
    @Override
    public NoticeFragmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_notice, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeFragmentAdapter.MyViewHolder holder, int position) {

        NoticeFragmentItem item = items.get(position);

        imageRef = storage.getReference().child(item.getProfile());

        Glide.with(holder.itemView.getContext())
                .load(ProfileImage.convertGsToHttps(imageRef.toString()))
                .error(R.mipmap.ic_launcher)
                .into(holder.profileImage);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

        if(!item.isRead()) {
            holder.layout.setBackgroundColor(primaryColor);
        } else {
            holder.layout.setBackgroundColor(whiteColor);
        }

        holder.layout.setOnClickListener(v -> {
            // 알림 읽음 표시
            if(!item.isRead()){
                Log.d("알림 읽음", "공지사항 알림 읽음 표시 및 API 전송");
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        ImageView profileImage;
        TextView title;
        TextView content;
        TextView createdTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.constraintlayout_item_notification_notice);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_notice);
            title = itemView.findViewById(R.id.textview_item_notification_notice_title);
            content = itemView.findViewById(R.id.textview_item_notification_notice_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_notice_createdtime);

        }
    }
}
