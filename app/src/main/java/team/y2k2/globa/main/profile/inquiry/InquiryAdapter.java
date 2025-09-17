package team.y2k2.globa.main.profile.inquiry;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.notification.inquiry.InquiryDetailActivity;
import team.y2k2.globa.notification.inquiry.InquiryItem;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.AdapterViewHolder> {
    private final ArrayList<InquiryItem> items;

    public InquiryAdapter(ArrayList<InquiryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inquiry_list, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.date.setText(items.get(position).getCreatedTime());

        holder.title.setOnClickListener(v -> {
        Intent intent = new Intent(holder.itemView.getContext(), InquiryDetailActivity.class);
        intent.putExtra("inquiryId", items.get(position).getInquiryId());
        holder.itemView.getContext().startActivity(intent);

        holder.status.setText(items.get(position).isSolved() ? "답변 완료" : "답변 대기");
        holder.status.setTextColor(items.get(position).isSolved() ? holder.itemView.getResources().getColor(R.color.primary) : holder.itemView.getResources().getColor(R.color.darkGray));
        holder.status.setVisibility(items.get(position).isSolved() ? View.VISIBLE : View.GONE);

    });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView status;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_inquiry_title);
            date = itemView.findViewById(R.id.textview_inquiry_date);
            status = itemView.findViewById(R.id.textview_inquiry_status);
        }
    }

    public void updateData(ArrayList<InquiryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }
}