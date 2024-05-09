package team.y2k2.globa.notification.inquiry;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class NotificationInquiryAdapter extends RecyclerView.Adapter<NotificationInquiryAdapter.AdapterViewHolder> {
    ArrayList<NotificationInquiryItem> items;

    public NotificationInquiryAdapter(ArrayList<NotificationInquiryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NotificationInquiryAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_inquiry, parent, false);
        return new NotificationInquiryAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationInquiryAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_notification_inquiry_title);
            description = itemView.findViewById(R.id.textview_item_notification_inquiry_description);
        }
    }
}
