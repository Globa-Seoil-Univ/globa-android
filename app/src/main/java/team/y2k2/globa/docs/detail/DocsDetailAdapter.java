package team.y2k2.globa.docs.detail;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_detail, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        if (position == 0) {
            DocsDetailHighlightModel model = new DocsDetailHighlightModel();
            String description = items.get(position).getDescription();
            SpannableString highlightString = new SpannableString(description);

            for (int i = 0; i < model.getItems().size(); i++) {
                DocsDetailHighlightItem highlight = model.getItems().get(i);

                int startIdx = highlight.getStartIndex();
                int endIdx = highlight.getEndIndex();

                // 클릭 가능한 Span 설정
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // 클릭 시 동작할 내용 구현
                        Log.d(getClass().getName(), "클릭됨");
                        Toast.makeText(widget.getContext(), "SpannableString 클릭됨", Toast.LENGTH_SHORT).show();

                        BottomSheetDialog commentBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
                        commentBottomSheet.setContentView(R.layout.dialog_comment);
                        commentBottomSheet.show();

                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.WHITE);
                        ds.setUnderlineText(false);
                    }
                };

                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(holder.itemView.getResources().getColor(highlight.getHighlightColor()));

                highlightString.setSpan(backgroundColorSpan, startIdx, endIdx, 0);
                highlightString.setSpan(clickableSpan, startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);



            }
            holder.description.setText(highlightString);
            holder.description.setMovementMethod(android.text.method.LinkMovementMethod.getInstance()); // SpannableString을 클릭 가능하게 만듦
        } else {
            holder.description.setText(items.get(position).getDescription());
        }

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