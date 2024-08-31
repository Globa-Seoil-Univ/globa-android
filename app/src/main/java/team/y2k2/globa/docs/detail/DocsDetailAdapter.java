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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Comment;
import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentAdapter;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentItem;
import team.y2k2.globa.docs.detail.comment.subcomment.DocsDetailSubCommentItem;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {

    ApiClient apiClient;

    ArrayList<DocsDetailItem> detailItems;
    DocsActivity activity;

    String folderId;
    String recordId;
    String DocsSectionId;
    String DocsHighlightId;

    String myProfile;
    String myName;

    int selectedPosition;

    TextView commentTv;
    RecyclerView commentRv;
    EditText commentEt;
    ImageButton commentBtn;

    private Disposable disposable;
    private DocsDetailCommentAdapter commentAdapter;

    ArrayList<DocsDetailCommentItem> commentItems;

    private final int BUTTON_COMMENT_CONFIRM = 0;
    private final int BUTTON_COMMENT_SUB_CONFIRM = 1;
    private final int BUTTON_COMMENT_UPDATE = 2;
    private final int BUTTON_COMMENT_SUB_UPDATE = 3;
    private int buttonStatus = BUTTON_COMMENT_CONFIRM;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.detailItems = detailItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.apiClient = new ApiClient(activity);
        this.myProfile = activity.getProfile();
        this.myName = activity.getName();
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

        DocsSectionId = sectionId;
        DocsHighlightId = highlightId;

        GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                /*
                // 캡스톤 댓글
                BottomSheetDialog commentBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
                commentBottomSheet.setContentView(R.layout.dialog_comment);

                TextView name = commentBottomSheet.findViewById(R.id.textview_comment_name);
                name.setText(comments.get(highlightIndex).getContent());

                commentBottomSheet.show();
                 */

                List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();
                commentItems = new ArrayList<>();
                for(Comment comment : comments) {
                    String profile = comment.getUser().getProfile();
                    String name = comment.getUser().getName();
                    String createdTime = comment.getCreatedTime();
                    String content = comment.getContent();
                    String commentId = comment.getCommentId();
                    boolean hasReply = comment.isHasReply();
                    commentItems.add(new DocsDetailCommentItem(profile, name, createdTime, content, commentId, hasReply));
                }

                showCommentSheetDialog(commentItems, highlightString.toString(), sectionId, null, null, highlightId);

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
        popupMenu.getMenuInflater().inflate(R.menu.highlight_menu, popupMenu.getMenu());

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

                    showCommentSheetDialog(null, selectedText, sectionId, startIdx, endIdx, null);



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

    private void showCommentSheetDialog(ArrayList<DocsDetailCommentItem> commentItems, String name, String sectionId, String startIdx, String endIdx, String highlightId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        commentTv = bottomSheetView.findViewById(R.id.textview_comment_name);
        commentRv = bottomSheetView.findViewById(R.id.recyclerview_comment);
        commentEt = bottomSheetView.findViewById(R.id.edittext_comment);
        commentBtn = bottomSheetView.findViewById(R.id.imagebutton_comment_confirm);

        // 선택된 단어 표시
        commentTv.setText(name);

        // null처리 관련 작업 필요...?
        commentAdapter = new DocsDetailCommentAdapter(commentItems, activity);
        commentRv.setLayoutManager(new LinearLayoutManager(activity));
        commentRv.setAdapter(commentAdapter);

        // 댓글 달기 버튼 이벤트 처리
        Observable<Object> commentBtnClickStream = Observable.create(emitter -> {
            commentBtn.setOnClickListener(v -> emitter.onNext(new Object()));
        });
        disposable = commentBtnClickStream.throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    String text = commentEt.getText().toString();
                    if(!text.isEmpty()) {
                        if(buttonStatus == BUTTON_COMMENT_CONFIRM) {
                            // 댓글 아이템 리스트 추가
                            commentAdapter.addNewItem(new DocsDetailCommentItem(myProfile, myName, "방금전", text, "commentId", false));
                            // API Request 필요
                            if(commentItems == null) {
                                // 댓글 최초 추가
                                apiClient.requestInsertFirstComment(folderId, recordId, sectionId, startIdx, endIdx, text);
                            } else {
                                // 댓글 추가 (최초X)
                                apiClient.requestInsertComment(folderId, recordId, sectionId, highlightId, text);
                            }
                        } else if(buttonStatus == BUTTON_COMMENT_SUB_CONFIRM) {
                            // 대댓글 아이템 리스트 추가
                            commentAdapter.addItemToSubCommentRecyclerView(selectedPosition, new DocsDetailSubCommentItem(myProfile, myName, "방금전", text));
                            // API Request 필요
                            apiClient.requestInsertSubComment(folderId, recordId, sectionId, highlightId, "parentId", text);
                        } else if(buttonStatus == BUTTON_COMMENT_UPDATE) {
                            // 댓글 아이템 수정 (수정 필요)
                            commentAdapter.updateItem(text, 0);
                            // API Request 필요
                            apiClient.updateComment(folderId, recordId, sectionId, highlightId, "commentId", text);
                        } else if(buttonStatus == BUTTON_COMMENT_SUB_UPDATE) {
                            // 대댓글 아이템 수정 (수정 필요)
                            commentAdapter.updateItemToSubCommentRecyclerView(text, 0, 0);
                            // API Request 필요
                            apiClient.updateComment(folderId, recordId, sectionId, highlightId, "commentId", text);
                        }

                        commentEt.setText("");
                    } else {
                        Toast.makeText(activity, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                });

        bottomSheetDialog.show();

    }

    public void clearDisposable() {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void setButtonStatus(int status) {
        this.buttonStatus = status;
    }
    public int getButtonStatus() {
        return buttonStatus;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public String getDocsHighlightId() {
        return DocsHighlightId;
    }
    public String getDocsSectionId() {
        return DocsSectionId;
    }
}