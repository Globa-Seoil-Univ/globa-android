package team.y2k2.globa.docs.detail;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightItem;
import team.y2k2.globa.docs.detail.highlight.DocsDetailHighlightModel;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {
    ArrayList<DocsDetailItem> detailItems;

    DocsActivity activity;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.detailItems = detailItems;
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
        String title = detailItems.get(position).getTitle();
        String time = formatDuration(Integer.parseInt(detailItems.get(position).getTime()));

        holder.title.setText(title);
        holder.time.setText(time);

        holder.title.setOnClickListener(v -> {
            int startTime = Integer.parseInt(detailItems.get(position).getTime());
            activity.setDuration(startTime);
        });


//        if (position == -1) {
            String description = detailItems.get(position).getDescription();
            SpannableString highlightString = new SpannableString(description);

            holder.description.setOnTouchListener(new View.OnTouchListener() {
                int startIdx = 0;
                int endIdx = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(getClass().getName(), "S:" + startIdx + " | E:" + endIdx);

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
                            showPopupMenu(v, activity.getFolderId(), activity.getRecordId(), detailItems.get(position).getSectionId(), String.valueOf(startIdx), String.valueOf(endIdx), selectedText);
                        }


                    }
                    else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                        endIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                        // Pivot 위치 계산
                        updateSelection(holder.description, startIdx, endIdx);

//                        Toast.makeText(holder.itemView.getContext(), "선택됨 " + startIdx +" | " + endIdx , Toast.LENGTH_SHORT).show();
                    }
                    else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                        endIdx = startIdx;


//                        activity.binding.linearlayoutDocsComment.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
            List<Highlight> highlights = detailItems.get(position).getHighlights();

            for (int i = 0; i < highlights.size(); i++) {
                Highlight highlight = highlights.get(i);

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

                        TextView name = commentBottomSheet.findViewById(R.id.textview_comment_name);

                        name.setText(detailItems.get(position).getDescription());

                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.WHITE);
                        ds.setUnderlineText(false);
                    }
                };
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(holder.itemView.getResources().getColor(R.color.primary_3));

                highlightString.setSpan(backgroundColorSpan, startIdx, endIdx, 0);
                highlightString.setSpan(clickableSpan, startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.description.setText(highlightString);
            holder.description.setMovementMethod(android.text.method.LinkMovementMethod.getInstance()); // SpannableString 클릭 되도록 함

    }

    @Override
    public int getItemCount() {
        return (null != detailItems ? detailItems.size() : 0);
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

    public void showPopupMenu(View v, String folderId, String recordId, String sectionId, String startIdx, String endIdx, String selectedText) {
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

                    TextView textView = commentBottomSheet.findViewById(R.id.textview_comment_name);
                    textView.setText("본문 :" + selectedText);

                    ImageButton confirm = commentBottomSheet.findViewById(R.id.imagebutton_comment_confirm);


                    confirm.setOnClickListener(v -> {
                        ApiClient apiClient = new ApiClient(v.getContext());

                        EditText comment = commentBottomSheet.findViewById(R.id.edittext_comment);

                        apiClient.requestInsertFirstComment(folderId, recordId, sectionId, startIdx, endIdx, comment.getText().toString());
                        commentBottomSheet.dismiss();
                    });
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

    public static String formatDuration(int durationSecond) {
        int hours = durationSecond / 3600;
        durationSecond %= 3600;

        int minutes = durationSecond / 60;
        int seconds = durationSecond % 60;

        if(hours > 0)
            return String.format("%2d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}