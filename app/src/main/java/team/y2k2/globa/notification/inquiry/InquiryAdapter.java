package team.y2k2.globa.notification.inquiry;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Inquiry;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.AdapterViewHolder> {
    ArrayList<InquiryItem> items;

    public InquiryAdapter(ArrayList<InquiryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public InquiryAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_inquiry, parent, false);
        return new InquiryAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getContent());

        holder.title.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), InquiryDetailActivity.class);
            intent.putExtra("inquiryId", items.get(position).getInquiryId());
            holder.itemView.getContext().startActivity(intent);
        });
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