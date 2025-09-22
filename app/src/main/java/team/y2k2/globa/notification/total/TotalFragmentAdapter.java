package team.y2k2.globa.notification.total;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;
import team.y2k2.globa.notification.inquiry.InquiryDetailActivity;

public class TotalFragmentAdapter extends RecyclerView.Adapter<TotalFragmentAdapter.MyViewHolder> {

    private final NotificationActivity activity;
    private final List<TotalFragmentItem> items;
    private final NotificationViewModel notificationViewModel;
    private final int whiteColor;
    private final int primaryColor;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public TotalFragmentAdapter(List<TotalFragmentItem> items, NotificationActivity activity, TotalFragment fragment) {
        this.items = items;
        this.activity = activity;
        notificationViewModel = new ViewModelProvider(fragment.requireActivity()).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }
    public void setItems(List<TotalFragmentItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
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

        if (item.getProfile() != null && !item.getProfile().isEmpty()) {
            if (item.getProfile().startsWith("http")) {
                Glide.with(holder.itemView.getContext()).load(item.getProfile()).error(R.drawable.profile_user).into(holder.profileImage);
            } else {
                StorageReference imageRef = storage.getReference().child(item.getProfile());
                Glide.with(holder.itemView.getContext()).load(ProfileImage.convertGsToHttps(imageRef.toString())).error(R.drawable.profile_user).into(holder.profileImage);
            }
        } else {
            Glide.with(holder.itemView.getContext()).load(R.drawable.profile_user).into(holder.profileImage);
        }

        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

        holder.layout.setOnClickListener(v -> {
            if (!item.isRead()) {
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }

            switch (item.getType()) {
                case "6": // 문서 추가 성공
                    if (item.getFolderId() != null && !item.getFolderId().isEmpty() && item.getRecordId() != null && !item.getRecordId().isEmpty()) {
                        Intent docsIntent = new Intent(activity, DocsActivity.class);
                        docsIntent.putExtra("folderId", item.getFolderId());
                        docsIntent.putExtra("recordId", item.getRecordId());
                        activity.startActivity(docsIntent);
                    } else {
                        Toast.makeText(activity, "문서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "7": // 문서 추가 실패
                    Toast.makeText(activity, "문서 추가에 실패한 기록입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case "8": // 문의 답변
                    Intent inquiryIntent = new Intent(activity, InquiryDetailActivity.class);
                    inquiryIntent.putExtra("inquiryId", item.getInquiryId());
                    activity.startActivity(inquiryIntent);
                    break;
            }
        });

        holder.layout.setBackgroundColor(item.isRead() ? whiteColor : primaryColor);

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

        holder.content.setVisibility(item.getContent().isEmpty() ? View.GONE : View.VISIBLE);
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
            layout = itemView.findViewById(R.id.constraintlayout_item_notification_total);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_total);
            title = itemView.findViewById(R.id.textview_item_notification_total_title);
            content = itemView.findViewById(R.id.textview_item_notification_total_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_total_created_time);
            confirmBtn = itemView.findViewById(R.id.button_item_notification_total_access);
            cancelBtn = itemView.findViewById(R.id.button_item_notification_total_denied);
        }
    }
}

