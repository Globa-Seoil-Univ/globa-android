package team.y2k2.globa.notification.docs;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class DocsFragmentAdapter extends RecyclerView.Adapter<DocsFragmentAdapter.MyViewHolder> {

    private final List<DocsFragmentItem> items;
    private final NotificationActivity activity;
    private final NotificationViewModel notificationViewModel;
    private final int whiteColor;
    private final int primaryColor;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public DocsFragmentAdapter(List<DocsFragmentItem> items, NotificationActivity activity, DocsFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.notificationViewModel = new ViewModelProvider(fragment.requireActivity()).get(NotificationViewModel.class);
        this.whiteColor = ContextCompat.getColor(activity, R.color.white);
        this.primaryColor = ContextCompat.getColor(activity, R.color.primary_1);
    }

    public void setItems(List<DocsFragmentItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
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

        Glide.with(holder.itemView.getContext())
                .load(R.mipmap.ic_launcher)
                .into(holder.profileImage);

        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

        holder.layout.setBackgroundColor(item.isRead() ? whiteColor : primaryColor);

        holder.layout.setOnClickListener(v -> {
            if (!item.isRead()) {
                Log.d("알림 읽음", "문서 알림 읽음 표시 및 API 전송");
                holder.layout.setBackgroundColor(whiteColor);
                notificationViewModel.readNotification(item.getNotificationId());
            }
            if ("6".equals(item.getType())) {
                if (item.getFolderId() != null && !item.getFolderId().isEmpty() && item.getRecordId() != null && !item.getRecordId().isEmpty()) {
                    Intent intent = new Intent(activity, DocsActivity.class);
                    intent.putExtra("folderId", item.getFolderId());
                    intent.putExtra("recordId", item.getRecordId());
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "문서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } else if ("7".equals(item.getType())) {
                Toast.makeText(activity, "문서 추가에 실패한 기록입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 내용이 없으면 숨김 처리
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraintlayout_item_notification_docs);
            profileImage = itemView.findViewById(R.id.imageview_item_notification_docs);
            title = itemView.findViewById(R.id.textview_item_notification_docs_title);
            content = itemView.findViewById(R.id.textview_item_notification_docs_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_docs_created_time);
        }
    }
}
