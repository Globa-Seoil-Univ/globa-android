package team.y2k2.globa.notification.share;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ShareFragmentAdapter extends RecyclerView.Adapter<ShareFragmentAdapter.MyViewHolder> {

    private final List<ShareFragmentItem> items;
    private final NotificationViewModel notificationViewModel;
    private final NotificationActivity activity;
    private final int whiteColor, primaryColor;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public ShareFragmentAdapter(List<ShareFragmentItem> items, NotificationActivity activity, ShareFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.notificationViewModel = new ViewModelProvider(fragment.requireActivity()).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }

    public void setItems(List<ShareFragmentItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShareFragmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_share, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareFragmentAdapter.MyViewHolder holder, int position) {
        ShareFragmentItem item = items.get(position);

        if (item.getProfile() != null && !item.getProfile().isEmpty()) {
            if (item.getProfile().startsWith("http")) {
                Glide.with(holder.itemView.getContext()).load(item.getProfile()).error(R.mipmap.ic_launcher).into(holder.profileImage);
            } else {
                StorageReference imageRef = storage.getReference().child(item.getProfile());
                Glide.with(holder.itemView.getContext()).load(ProfileImage.convertGsToHttps(imageRef.toString())).error(R.mipmap.ic_launcher).into(holder.profileImage);
            }
        } else {
            Glide.with(holder.itemView.getContext()).load(R.mipmap.ic_launcher).into(holder.profileImage);
        }

        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

        holder.layout.setBackgroundColor(item.isRead() ? whiteColor : primaryColor);

        holder.layout.setOnClickListener(v -> {
            if (!item.isRead()) {
                Log.d("알림 읽음", "공유 알림 읽음 표시 및 API 전송");
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }
        });

        if ("2".equals(item.getType())) {
            holder.confirmBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setVisibility(View.VISIBLE);
            holder.confirmBtn.setOnClickListener(v -> {
                notificationViewModel.acceptInvite(item.getFolderId(), item.getShareId());
                holder.confirmBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.layout.setBackgroundColor(whiteColor);
            });
            holder.cancelBtn.setOnClickListener(v -> {
                notificationViewModel.denyInvite(item.getFolderId(), item.getShareId(), item.getNotificationId());
                holder.confirmBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.layout.setBackgroundColor(whiteColor);
            });
        } else {
            holder.confirmBtn.setVisibility(View.GONE);
            holder.cancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final ImageView profileImage;
        private final TextView title;
        private final TextView content;
        private final TextView createdTime;
        private final Button confirmBtn;
        private final Button cancelBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraintlayout_item_notification_share);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_share);
            title = itemView.findViewById(R.id.textview_item_notification_share_title);
            content = itemView.findViewById(R.id.textview_item_notification_share_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_share_created_time);
            confirmBtn = itemView.findViewById(R.id.button_item_notification_share_access);
            cancelBtn = itemView.findViewById(R.id.button_item_notification_share_denied);
        }
    }
}