package team.y2k2.globa.notification.docs;

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

import java.util.Collection;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class DocsFragmentAdapter extends RecyclerView.Adapter<DocsFragmentAdapter.MyViewHolder> {

    List<DocsFragmentItem> items;
    NotificationActivity activity;
    NotificationViewModel notificationViewModel;
    int whiteColor, primaryColor;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageRef;

    public DocsFragmentAdapter(List<DocsFragmentItem> items, NotificationActivity activity, DocsFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.notificationViewModel = new ViewModelProvider(fragment).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }

    @NonNull
    @Override
    public DocsFragmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_docs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsFragmentAdapter.MyViewHolder holder, int position) {

        DocsFragmentItem item = items.get(position);

        if(item.getProfile().startsWith("http")) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getProfile())
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
        } else {
            imageRef = storage.getReference().child(item.getProfile());
            Glide.with(holder.itemView.getContext())
                    .load(ProfileImage.convertGsToHttps(imageRef.toString()))
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
        }

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
                Log.d("알림 읽음", "문서 알림 읽음 표시 및 API 전송");
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

            layout = itemView.findViewById(R.id.constraintlayout_item_notification_docs);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_docs);
            title = itemView.findViewById(R.id.textview_item_notification_docs_title);
            content = itemView.findViewById(R.id.textview_item_notification_docs_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_docs_createdtime);

        }
    }
}
