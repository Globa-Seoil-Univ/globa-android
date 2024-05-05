package team.y2k2.globa.docs.summary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class DocsSummaryAdapter extends RecyclerView.Adapter<DocsSummaryAdapter.AdapterViewHolder> {
    ArrayList<DocsSummaryItem> items;

    public DocsSummaryAdapter(ArrayList<DocsSummaryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_summary, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.time.setText(items.get(position).getTime());

        DocsSummaryDescriptionAdapter adapter = new DocsSummaryDescriptionAdapter(items.get(position).getDescriptions());

        holder.descriptions.setAdapter(adapter);
        holder.descriptions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }
    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        RecyclerView descriptions;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_docs_summary_title);
            time = itemView.findViewById(R.id.textview_item_docs_summary_time);
            descriptions = itemView.findViewById(R.id.recyclerview_item_docs_summary_description);
        }
    }
}