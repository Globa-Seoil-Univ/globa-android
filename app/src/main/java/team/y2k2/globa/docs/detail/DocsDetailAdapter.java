package team.y2k2.globa.docs.detail;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Comment;
import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {
    ArrayList<DocsDetailItem> detailItems;
    DocsActivity activity;

    String folderId;
    String recordId;

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.detailItems = detailItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();

        // 댓글 레이아웃 bottomSheetBehavior 선언
//        FrameLayout bottomSheet = activity.findViewById(R.id.framelayout_docs_comment);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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
        String sectionId = detailItems.get(position).getSectionId();

        holder.title.setText(title);
        holder.time.setText(time);

        holder.title.setOnClickListener(v -> {
            int startTime = Integer.parseInt(detailItems.get(position).getTime());
            activity.setDuration(startTime);
        });

        String description = detailItems.get(position).getDescription();
        SpannableString highlightString = new SpannableString(description);
        holder.description.setOnTouchListener(new View.OnTouchListener() {
            int startIdx = 0;
            int endIdx = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "S:" + startIdx + " | E:" + endIdx);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(getClass().getName(), "ACTION_UP | S:" + startIdx + " | E:" + endIdx);

                    if (startIdx >= endIdx) {
                        int temp = startIdx;
                        startIdx = endIdx;
                        endIdx = temp;
                    }

                    if (startIdx != -1 && endIdx != -1) {
                        updateSelection(holder.description, startIdx, endIdx);
                        String selectedText = holder.description.getText().subSequence(startIdx, endIdx).toString();
                        Toast.makeText(holder.itemView.getContext(), "선택한 텍스트: " + selectedText, Toast.LENGTH_SHORT).show();
                        showPopupMenu(v, activity.getFolderId(), activity.getRecordId(), detailItems.get(position).getSectionId(), String.valueOf(startIdx), String.valueOf(endIdx), selectedText);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    endIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                    updateSelection(holder.description, startIdx, endIdx);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                    endIdx = startIdx;
                }
                return false;
            }
        });

        List<Highlight> highlights = detailItems.get(position).getHighlights();
        for (int i = 0; i < highlights.size(); i++) {
            Highlight highlight = highlights.get(i);
            applyHighlightAndClick(holder, highlightString, sectionId, highlight, i);
        }

        holder.description.setText(highlightString);
        holder.description.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // 만들어진 하이라이트 클릭 (댓글 가져오기)
    private void applyHighlightAndClick(@NonNull AdapterViewHolder holder, SpannableString highlightString, String sectionId, Highlight highlight, int highlightIndex) {
        String highlightId = String.valueOf(highlight.getHighlightId());
        int startIdx = highlight.getStartIndex();
        int endIdx = highlight.getEndIndex();

        GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                ApiClient apiClient = new ApiClient(activity);
                List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();

                BottomSheetDialog commentBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
                commentBottomSheet.setContentView(R.layout.dialog_comment);

                TextView name = commentBottomSheet.findViewById(R.id.textview_comment_name);
                name.setText(comments.get(highlightIndex).getContent());

                commentBottomSheet.show();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity); // context는 현재 Activity 또는 Fragment

                builder.setTitle("댓글 삭제")
                        .setMessage("댓글을 삭제하시겠습니까?")
                        .setPositiveButton("예", (dialog, witch) -> {
                            // 예 버튼 클릭 시 동작 (댓글 삭제 로직)
                            Toast.makeText(activity,"댓글을 삭제했습니다.", Toast.LENGTH_LONG).show();

                            ApiClient apiClient = new ApiClient(activity);

                            List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();
                            String commentId = comments.get(highlightIndex).getCommentId();
                            Log.d(getClass().getName(), "commentId : " + commentId);
                            Response<Void> result = apiClient.deleteComment(folderId, recordId, sectionId, highlightId, commentId);

                            Log.d(getClass().getName(), "code : " + result.code());
                        })
                        .setNegativeButton("아니오", (dialog, which) -> {
                            dialog.dismiss();
                            Toast.makeText(activity, "아니오를 선택함", Toast.LENGTH_SHORT).show();
                                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) { }
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

        holder.description.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
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

    // 새로운 하이라이트 생성 시 작동
    public void showPopupMenu(View v, String folderId, String recordId, String sectionId, String startIdx, String endIdx, String selectedText) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.getMenuInflater().inflate(R.menu.comment, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_comment) {
                    /* 다이얼로그 방식
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
                     */

                    // 댓글 창 띄우기
                    if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        Log.d("새로운 하이라이트 댓글", "댓글 창 띄우기 시작");
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    TextView textView = activity.findViewById(R.id.textview_docs_comment_name);
                    textView.setText("선택 단어 : " + selectedText);

                    ImageButton confirm = activity.findViewById(R.id.imagebutton_docs_comment_confirm);
                    confirm.setOnClickListener(v -> {
                        // api 송신
                        ApiClient apiClient = new ApiClient(v.getContext());

                        EditText comment = activity.findViewById(R.id.edittext_docs_comment_input);
                        apiClient.requestInsertFirstComment(folderId, recordId, sectionId, startIdx, endIdx, comment.getText().toString());

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    });

                    // 댓글 레이아웃 바깥영역 클릭 시 댓글 레이아웃 감추기
                    CoordinatorLayout coordinatorLayout = activity.findViewById(R.id.coordinatorlayout_docs);
                    coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                return true;
                            }

                            return false;
                        }
                    });


                } else if (item.getItemId() == R.id.action_search) {
                    // 단어 검색
                    Intent searchIntent = new Intent(activity, KeywordDetailActivity.class);
                    searchIntent.putExtra("keyword", selectedText);
                    activity.startActivity(searchIntent);

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