package team.y2k2.globa.docs.detail;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightItem;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightModel;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {
    ArrayList<DocsDetailItem> items;
    DocsActivity activity;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> items, DocsActivity activity) {
        this.items = items;
        this.activity = activity;
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

            holder.description.setOnTouchListener(new View.OnTouchListener() {
                int startIdx = 0;
                int endIdx = 0;

                float startPivot;
                float endPivot;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(getClass().getName(), "S:" + startIdx + " | E:" + endIdx);

                    startPivot = event.getX();
                    endPivot = event.getY();

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d(getClass().getName(), "ACTION_UP | S:" + startIdx + " | E:" + endIdx);

//                        endIdx = holder.description.getOffsetForPosition(startPivot, endPivot);

                        if(startIdx >= endIdx) {
                            int temp = startIdx;
                            startIdx = endIdx;
                            endIdx = temp;
                        }

                        if (startIdx != -1 && endIdx != -1) {
//                            activity.binding.linearlayoutDocsComment.setVisibility(View.VISIBLE);
                            updateSelection(holder.description, startIdx, endIdx);
                            String selectedText = holder.description.getText().subSequence(startIdx, endIdx).toString();
                            Toast.makeText(holder.itemView.getContext(), "선택한 텍스트: " + selectedText, Toast.LENGTH_SHORT).show();
                            showPopupMenu(v, selectedText);
                        }


                    }
                    else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                        endIdx = holder.description.getOffsetForPosition(startPivot, endPivot);
                        // Pivot 위치 계산
//                        Toast.makeText(holder.itemView.getContext(), "선택됨 " + startIdx +" | " + endIdx , Toast.LENGTH_SHORT).show();

                        updateSelection(holder.description, startIdx, endIdx);


                    }
                    else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startIdx = holder.description.getOffsetForPosition(startPivot, endPivot);
                        endIdx = startIdx;


//                        activity.binding.linearlayoutDocsComment.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });

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

                        commentBottomSheet.findViewById(R.id.recyclerview_comment);
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

    public void showPopupMenu(View v, String selectedText) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.getMenuInflater().inflate(R.menu.comment, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_comment) {
                    // 댓글 작성 기능 수행
                    BottomSheetDialog commentBottomSheet = new BottomSheetDialog(v.getContext());
                    commentBottomSheet.setContentView(R.layout.dialog_comment);
                    commentBottomSheet.show();

                    TextView textView = commentBottomSheet.findViewById(R.id.textview_comment_point);
                    textView.setText("> " + selectedText);
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void updateSelection(TextView textView, int start, int end) {
        int startIdx = start;
        int endIdx = end;

        if(startIdx >= endIdx) {
            int temp = startIdx;
            startIdx = endIdx;
            endIdx = temp;
        }

        // 선택된 텍스트에 배경 강조
        if (start == end || start < 0 || end < 0) {
            return;
        }

        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        // 이전에 설정된 span 삭제
        spannableString.removeSpan(BackgroundColorSpan.class);



        spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 텍스트뷰에 설정
        textView.setText(spannableString);
    }
}