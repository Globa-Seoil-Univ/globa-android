package team.y2k2.globa.docs.detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;

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
import team.y2k2.globa.docs.detail.comment.FocusViewModel;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;
import team.y2k2.globa.main.ProfileImage;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {

    ApiClient apiClient;

    ArrayList<DocsDetailItem> detailItems;
    DocsActivity activity;

    String folderId;
    String recordId;

    String myProfile;
    String myName;

    int selectedPosition;
    String selectedId;

    TextView commentTv;
    RecyclerView commentRv;
    EditText commentEt;
    ImageButton commentBtn;

    private Disposable disposable;
    private DocsDetailCommentAdapter commentAdapter;

    ArrayList<DocsDetailCommentItem> commentItems = new ArrayList<>();

    private final int BUTTON_COMMENT_CONFIRM = 0;
    private final int BUTTON_COMMENT_UPDATE = 1;
    private int buttonStatus = BUTTON_COMMENT_CONFIRM;

    ProfileImage profileImage;

    private FocusViewModel focusViewModel;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.profileImage = new ProfileImage();
        this.detailItems = detailItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.apiClient = new ApiClient(activity);
        this.myProfile = profileImage.convertGsToHttps(FirebaseStorage.getInstance().getReference().child(activity.getProfile()).toString());
        this.myName = activity.getName();
        focusViewModel = new ViewModelProvider(activity).get(FocusViewModel.class);
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_detail, parent, false);
        return new AdapterViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        String title = detailItems.get(position).getTitle();
        String time = formatDuration(Integer.parseInt(detailItems.get(position).getTime()));
        String sectionId = detailItems.get(position).getSectionId();

        holder.title.setText(title);
        holder.time.setText(time);

        String description = detailItems.get(position).getDescription();
        List<Highlight> highlights = detailItems.get(position).getHighlights();

        if(highlights.size() == 0) {
            holder.comment.setY(0);
            holder.comment.setText("");
            holder.comment.setVisibility(View.INVISIBLE);
        }
        else {
            String commentString = description.substring(highlights.get(0).getStartIndex(), highlights.get(0).getEndIndex());

            holder.comment.setOnClickListener(v -> {
                // 외부에서 생성된 selection을 updateSelection에 전달
                // offset 위치에 해당하는 highlight 찾기

                for (Highlight highlight : highlights) {
                    String highlightId = String.valueOf(highlight.getHighlightId());

                    Log.d(getClass().getSimpleName(), "folderId: " + folderId + ", recordId: " + recordId + ", sectionId: " + sectionId + ", highlightId: " + highlightId);
                    List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();
                    commentItems = new ArrayList<>();
                    for (Comment comment : comments) {
                        String profile = comment.getUser().getProfile();
                        String name = comment.getUser().getName();
                        String createdTime = comment.getCreatedTime();
                        String content = comment.getContent();
                        String commentId = comment.getCommentId();
                        boolean hasReply = comment.isHasReply();
                        commentItems.add(new DocsDetailCommentItem(profile, name, createdTime, content, commentId, hasReply));
                    }

                    showCommentSheetDialog(commentItems, sectionId, highlightId, commentString, null, null);
                }
            });
            holder.comment.setOnLongClickListener(v -> {
                // offset 위치에 해당하는 highlight 찾기
                for (int i = 0; i < highlights.size(); i++) {
                    Highlight highlight = highlights.get(i);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    final int highlightIndex = i; // highlightIndex 값을 final로 선언

                    builder.setTitle("댓글 삭제")
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("예", (dialog, witch) -> {
                                // 예 버튼 클릭 시 동작 (댓글 삭제 로직)
                                Toast.makeText(activity, "댓글을 삭제했습니다.", Toast.LENGTH_LONG).show();

                                ApiClient apiClient = new ApiClient(activity);

                                String highlightId = String.valueOf(highlight.getHighlightId()); // highlightId 값을 가져옴
                                List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();
                                String commentId = comments.get(highlightIndex).getCommentId();
                                Log.d(getClass().getName(), "commentId : " + commentId);
                                Response<Void> result = apiClient.deleteComment(folderId, recordId, sectionId, highlightId, commentId);

                                Log.d(getClass().getName(), "code : " + result.code());
                            })
                            .setNegativeButton("아니오", (dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            });
        }

        holder.title.setOnClickListener(v -> {
            int startTime = Integer.parseInt(detailItems.get(position).getTime());
            activity.setDuration(startTime);
        });

        final SpannableString selection = setSpannableStringHighlight(new SpannableString(description), highlights, holder, sectionId);

        holder.description.setOnTouchListener(new View.OnTouchListener() {
            int startIdx = 0;
            int endIdx = 0;
            long downTime = 0;
            Runnable longClickRunnable = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                        endIdx = startIdx;
                        downTime = System.currentTimeMillis(); // 터치 시작 시간 기록

                        // 롱클릭 이벤트 처리를 위한 Runnable 생성
                        longClickRunnable = () -> {
                            int touchX = (int) event.getX();
                            int touchY = (int) event.getY();
                            int offset = holder.description.getOffsetForPosition(touchX, touchY);
                        };
                        // 롱클릭 시간 후에 longClickRunnable 실행 예약
                        holder.description.postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        holder.description.removeCallbacks(longClickRunnable); // 롱클릭 Runnable 취소

                        long upTime = System.currentTimeMillis(); // 터치 종료 시간 기록
                        long duration = upTime - downTime; // 터치 시간 계산

                        if (duration < ViewConfiguration.getLongPressTimeout()) {

                        } else {
                            // 롱 클릭 후 드래그한 경우 (팝업 메뉴 표시)
                            if (startIdx >= endIdx) {
                                int temp = startIdx;
                                startIdx = endIdx;
                                endIdx = temp;
                            }

                            if (startIdx != -1 && endIdx != -1) {
                                // 외부에서 생성된 selection을 updateSelection에 전달
                                updateSelection(selection, startIdx, endIdx);
                                String selectedText = holder.description.getText().subSequence(startIdx, endIdx).toString();
                                Toast.makeText(holder.itemView.getContext(), "선택한 텍스트: " + selectedText, Toast.LENGTH_SHORT).show();

                                showPopupMenu(v, selectedText, false, sectionId, startIdx, endIdx);
                            }
                        }

                        // ACTION_UP에서 selection을 다시 설정하여 드래그 표시 제거
                        holder.description.setText(selection);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        endIdx = holder.description.getOffsetForPosition(event.getX(), event.getY());
                        // 외부에서 생성된 selection을 updateSelection에 전달
                        updateSelection(selection, startIdx, endIdx);
                        holder.description.setText(selection);
                        break;
                    }
                }
                return false;
            }
        });

        holder.description.setText(selection);
        holder.description.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public int getItemCount() {
        return (null != detailItems ? detailItems.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView description;
        TextView comment;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_docs_detail_title);
            time = itemView.findViewById(R.id.textview_item_docs_detail_time);
            description = itemView.findViewById(R.id.textview_item_docs_detail_description);
            comment = itemView.findViewById(R.id.textview_item_docs_detail_comment);
        }
    }

    // 새로운 하이라이트 생성 시 작동
    public void showPopupMenu(View v, String selectedText, boolean isCommentExist, String sectionId, int startIdx, int endIdx) {
        PopupMenu popupMenu = new PopupMenu(activity, v);

        if(isCommentExist) {
            popupMenu.getMenu().removeItem(0);
        }
        popupMenu.getMenuInflater().inflate(R.menu.highlight_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_comment) {
                String startIndex = String.valueOf(startIdx);
                String endIndex = String.valueOf(endIdx);
                showCommentSheetDialog(null, sectionId, null, selectedText, startIndex, endIndex);
            }
            else if (item.getItemId() == R.id.action_search) {
                Intent searchIntent = new Intent(activity, KeywordDetailActivity.class);
                searchIntent.putExtra("keyword", selectedText);
                activity.startActivity(searchIntent);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public SpannableString setSpannableStringHighlight(SpannableString selection, List<Highlight> highlights, AdapterViewHolder holder, String sectionId){
        for (int i = 0; i < highlights.size(); i++) {
            Highlight highlight = highlights.get(i);

            String highlightId = String.valueOf(highlight.getHighlightId());
            int startIdx = highlight.getStartIndex();
            int endIdx = highlight.getEndIndex();
            String selectedString = selection.toString().substring(startIdx, endIdx);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.d(getClass().getSimpleName(), "folderId: " + folderId + ", recordId: " + recordId + ", sectionId: " + sectionId + ", highlightId: " + highlightId);
                    List<Comment> comments = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 10).getComments();
                    commentItems = new ArrayList<>();
                    for (Comment comment : comments) {
                        String profile = comment.getUser().getProfile();
                        String name = comment.getUser().getName();
                        String createdTime = comment.getCreatedTime();
                        String content = comment.getContent();
                        String commentId = comment.getCommentId();
                        boolean hasReply = comment.isHasReply();
                        commentItems.add(new DocsDetailCommentItem(profile, name, createdTime, content, commentId, hasReply));
                    }

                    Log.d(getClass().getSimpleName(), "선택 단어(highlightString) : " + selectedString);
//                    showCommentSheetDialog(commentItems, selectedString, sectionId, highlightId);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    ds.setUnderlineText(false);
                }
            };

            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(holder.itemView.getResources().getColor(R.color.primary_3));

            selection.setSpan(backgroundColorSpan, startIdx, endIdx, 0);
            selection.setSpan(clickableSpan, startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return selection;
    }

    private SpannableString updateSelection(SpannableString selection, int start, int end) {
        int startIdx = start;
        int endIdx = end;

        if(startIdx >= endIdx) {
            int temp = startIdx;
            startIdx = endIdx;
            endIdx = temp;
        }

        // 기존에 설정된 드래그 표시 Span (노란색 배경) 만 제거
        BackgroundColorSpan[] spans = selection.getSpans(0, selection.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            if (span.getBackgroundColor() == Color.YELLOW) { // 드래그 표시 span 인지 확인
                selection.removeSpan(span);
            }
        }

        selection.setSpan(new BackgroundColorSpan(Color.YELLOW), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return selection;
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

    private void showCommentSheetDialog(ArrayList<DocsDetailCommentItem> commentItems, String sectionId, String highlightId, String name, String startIdx, String endIdx) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        Log.d(getClass().getName(), "section ID: " + sectionId);

        commentTv = bottomSheetView.findViewById(R.id.textview_comment_name);
        commentRv = bottomSheetView.findViewById(R.id.recyclerview_comment);
        commentEt = bottomSheetView.findViewById(R.id.edittext_comment);
        commentBtn = bottomSheetView.findViewById(R.id.imagebutton_comment_confirm);

        Log.d("댓글 창", "버튼 상태 buttonStatus: " + buttonStatus);
        /* buttonStatus
            0: 댓글 작성 상태
            1: 대댓글 작성 상태
            2: 댓글 수정 상태
            3: 대댓글 수정 상태
         */

        // 선택된 단어 표시
        commentTv.setText(name);

        commentAdapter = new DocsDetailCommentAdapter(commentItems, activity, sectionId, highlightId, this);
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
                            Log.d("댓글 추가", "내 프로필 경로: " + myProfile);
                            Log.d("댓글 추가", "내 이름: " + myName + ", 작성 내용: " + text);
                            Log.d(getClass().getSimpleName(), "댓글 버튼 상태: " + buttonStatus);
                            commentAdapter.addNewItem(new DocsDetailCommentItem(myProfile, myName, "방금전", text, "commentId", false));
                            // API Request 필요
                            if(commentItems == null) {
                                // 댓글 최초 추가
                                Log.d(getClass().getSimpleName(), "댓글 최초 추가 시작");
                                apiClient.requestInsertFirstComment(folderId, recordId, sectionId, startIdx, endIdx, text);
                                Log.d(getClass().getSimpleName(), "댓글 최초 추가 종료");
                            } else {
                                // 댓글 추가 (최초X)
                                Log.d(getClass().getSimpleName(), "댓글 추가 시작");
                                apiClient.requestInsertComment(folderId, recordId, sectionId, highlightId, text);
                            }
                        } else if(buttonStatus == BUTTON_COMMENT_UPDATE) {
                            // 댓글 아이템 수정 (수정 필요)
                            commentAdapter.updateItem(text, selectedPosition);
                            // API Request 필요
                            apiClient.updateComment(folderId, recordId, sectionId, highlightId, selectedId, text);
                        }

                        commentEt.setText("");
                    } else {
                        Toast.makeText(activity, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("RxJavaError", "RxJavaError 오류 내용: " + error);
                    Toast.makeText(activity, "댓글 처리중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            );

        commentEt.setOnFocusChangeListener((v, hasFocus) -> {
            // 포커스를 얻으면 true, 잃으면 false;
            focusViewModel.setCommentFocusLiveData(hasFocus);
        });

        bottomSheetDialog.show();

    }

    public void clearDisposable() {
        if(disposable != null && !disposable.isDisposed()) {
            Log.d("댓글", "댓글 disposable 메모리 해제 시작");
            disposable.dispose();
        }
        if(commentAdapter != null) {
            commentAdapter.clearSubDisposable();
        }
    }

    public void focusOnCommentEt() {
        commentEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEt, InputMethodManager.SHOW_IMPLICIT);
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
    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

}