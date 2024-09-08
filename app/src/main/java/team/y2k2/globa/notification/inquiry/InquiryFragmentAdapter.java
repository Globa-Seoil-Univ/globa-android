package team.y2k2.globa.notification.inquiry;

import android.content.Intent;
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

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.main.profile.inquiry.InquiryActivity;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class InquiryFragmentAdapter extends RecyclerView.Adapter<InquiryFragmentAdapter.MyViewHolder> {

    NotificationActivity activity;
    List<InquiryFragmentItem> items;
    NotificationViewModel notificationViewModel;
    int whiteColor, primaryColor;

    public InquiryFragmentAdapter(List<InquiryFragmentItem> items, NotificationActivity activity, InquiryFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.notificationViewModel = new ViewModelProvider(fragment).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }

    @NonNull
    @Override
    public InquiryFragmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_inquiry, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryFragmentAdapter.MyViewHolder holder, int position) {

        InquiryFragmentItem item = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(R.mipmap.ic_launcher)
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

            if(!item.isRead()) {
                Log.d("알림 읽음", "문의 알림 읽음 표시 및 API 전송");
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }


            Intent intent = new Intent(activity, InquiryDetailActivity.class);
            intent.putExtra("inquiryId", item.getInquiryId());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout;
        ImageView profileImage;
        TextView title;
        TextView content;
        TextView createdTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.constraintlayout_item_notification_inquiry);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_inquiry);
            title = itemView.findViewById(R.id.textview_item_notification_inquiry_title);
            content = itemView.findViewById(R.id.textview_item_notification_inquiry_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_inquiry_createdtime);

        }
    }

}
