package team.y2k2.globa.notification.docs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.notification.NotificationActivity;

public class DocsFragmentAdapter extends RecyclerView.Adapter<DocsFragmentAdapter.MyViewHolder> {

    List<DocsFragmentItem> items;
    NotificationActivity activity;

    public DocsFragmentAdapter(List<DocsFragmentItem> items, NotificationActivity activity) {
        this.items = items;
        this.activity = activity;
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
                .error(R.mipmap.ic_launcher)
                .into(holder.profileImage);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.createdTime.setText(item.getCreatedTime());

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageview_item_notification_docs);
            title = itemView.findViewById(R.id.textview_item_notification_docs_title);
            content = itemView.findViewById(R.id.textview_item_notification_docs_content);
            createdTime = itemView.findViewById(R.id.textview_item_notification_docs_createdtime);

        }
    }
}
