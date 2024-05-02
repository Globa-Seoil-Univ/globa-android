package team.y2k2.globa.docs.detail;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightItem;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightModel;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {
    ArrayList<DocsDetailItem> items;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DocsDetailAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_detail, parent, false);
        return new DocsDetailAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsDetailAdapter.AdapterViewHolder holder, int position) {

        if(position == 0) {
            DocsDetailHighlightModel model = new DocsDetailHighlightModel();
            String description = items.get(position).getDescription();
            SpannableString highlightString = new SpannableString(description);

            for(int i = 0; i < model.getItems().size(); i++) {
                DocsDetailHighlightItem highlight = model.getItems().get(i);

                highlightString.setSpan(new BackgroundColorSpan(holder.itemView.getResources().getColor(highlight.getHighlightColor())), highlight.getStartIndex(), highlight.getEndIndex(), 0);
                highlightString.setSpan(new ForegroundColorSpan(Color.WHITE), highlight.getStartIndex(), highlight.getEndIndex(), 0);
            }
            holder.description.setText(highlightString);
        }
        else
            holder.description.setText(items.get(position).getDescription());


        holder.title.setText(items.get(position).getTitle());
        holder.time.setText(items.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }
    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView description;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_docs_detail_title);
            time = itemView.findViewById(R.id.textview_item_docs_detail_time);
            description = itemView.findViewById(R.id.textview_item_docs_detail_description);
        }
    }
}