package team.y2k2.globa.notification.total;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;
import team.y2k2.globa.notification.inquiry.InquiryDetailActivity;

public class TotalFragmentAdapter extends RecyclerView.Adapter<TotalFragmentAdapter.MyViewHolder> {

    NotificationActivity activity;
    List<TotalFragmentItem> items;
    NotificationViewModel notificationViewModel;
    ProfileImage profileImage;
    int whiteColor, primaryColor;

    public TotalFragmentAdapter(List<TotalFragmentItem> items, NotificationActivity activity, TotalFragment fragment) {
        this.items = items;
        this.activity = activity;
        notificationViewModel = new ViewModelProvider(fragment).get(NotificationViewModel.class);
        this.profileImage = new ProfileImage();
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
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
            Log.d("이미지 경로", "이미지 경로: " + item.getProfile());
            Glide.with(holder.itemView.getContext())
                    .load(item.getProfile())
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

        holder.layout.setOnClickListener(v -> {

            Log.d("아이템 클릭", "아이템 클릭 타입: " + item.getType());
            // 알림 읽음 표시
            if(!item.isRead()) {
                Log.d("알림 읽음", "전체 알림 읽음 표시 및 API 전송");
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }

            if(item.getType() == "8") {
                Intent intent = new Intent(activity, InquiryDetailActivity.class);
                Log.d(getClass().getSimpleName(), "문의 아이디: " + item.getInquiryId());
                intent.putExtra("inquiryId", item.getInquiryId());
                activity.startActivity(intent);
            }

        });

        if(!item.isRead()) {
            holder.layout.setBackgroundColor(primaryColor);
        } else {
            holder.layout.setBackgroundColor(whiteColor);
        }

        if(item.getType().equals("2")) {
            // 공유 초대 알림
            holder.confirmBtn.setOnClickListener(v -> {
                // 공유 수락 버튼 이벤트
                Log.d("수락 버튼", "공유 초대 수락 버튼 클릭");
                Log.d("수락 버튼", "folder_id : " + item.getFolderId() + " share_id : " + item.getShareId());
                notificationViewModel.acceptInvite(item.getFolderId(), item.getShareId());
                holder.confirmBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.layout.setBackgroundColor(whiteColor);
            });
            holder.cancelBtn.setOnClickListener(v -> {
                // 공유 거절 버튼 이벤트
                Log.d("거절 버튼", "공유 초대 거절 버튼 클릭");
                Log.d("거절 버튼", "folder_id : " + item.getFolderId() + " share_id : " + item.getShareId() + " notification_id : " + item.getNotificationId());
                notificationViewModel.denyInvite(item.getFolderId(), item.getShareId(), item.getNotificationId());
                holder.confirmBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.layout.setBackgroundColor(whiteColor);
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

        ConstraintLayout layout;
        ImageView profileImage;
        TextView title;
        TextView content;
        TextView createdTime;
        Button confirmBtn;
        Button cancelBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.constraintlayout_item_notification_total);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_total);
            title = itemView.findViewById(R.id.textview_item_notification_total_title);
            content = itemView.findViewById(R.id.textview_item_notification_total_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_total_createdtime);
            confirmBtn = itemView.findViewById(R.id.button_item_notification_total_access);
            cancelBtn = itemView.findViewById(R.id.button_item_notification_total_denied);
        }
    }

}
