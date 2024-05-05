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

public class DocsSummaryDescriptionAdapter extends RecyclerView.Adapter<DocsSummaryDescriptionAdapter.AdapterViewHolder> {
    ArrayList<String> items;

    public DocsSummaryDescriptionAdapter(ArrayList<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_summary_description, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }
    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_docs_summary_description);
        }
    }
}