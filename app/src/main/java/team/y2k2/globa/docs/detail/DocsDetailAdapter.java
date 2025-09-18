package team.y2k2.globa.docs.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.CommentApiClient;
import team.y2k2.globa.api.model.entity.Comment;
import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.databinding.ItemDocsDetailBinding;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentAdapter;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentItem;
import team.y2k2.globa.docs.detail.comment.FocusViewModel;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.util.i18n.DateTimeFormatter;

public class DocsDetailAdapter extends RecyclerView.Adapter<DocsDetailAdapter.AdapterViewHolder> {
    private static final int BUTTON_COMMENT_CONFIRM = 0;
    private static final int BUTTON_COMMENT_UPDATE = 1;

    private final CommentApiClient apiClient;
    private final ArrayList<DocsDetailItem> detailItems;
    private final DocsActivity activity;
    private final String folderId;
    private final String recordId;
    private final String myProfile;
    private final String myName;
    private final FocusViewModel focusViewModel;
    private final DocsDetailViewModel docsDetailViewModel;

    private int selectedPosition;
    private int buttonStatus = BUTTON_COMMENT_CONFIRM;
    private String selectedId;
    private EditText commentEt;
    private Disposable disposable;
    private DocsDetailCommentAdapter commentAdapter;
    private String selectedText;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.detailItems = detailItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.apiClient = new CommentApiClient();

        String profileUrl = activity.getProfile();
        if (profileUrl == null || profileUrl.isEmpty()) {
            this.myProfile = "";
        } else if (profileUrl.startsWith("http")) {
            this.myProfile = profileUrl;
        } else {
            this.myProfile = ProfileImage.convertGsToHttps(FirebaseStorage.getInstance().getReference().child(profileUrl).toString());
        }

        this.myName = activity.getName();
        this.focusViewModel = new ViewModelProvider(activity).get(FocusViewModel.class);
        this.docsDetailViewModel = new ViewModelProvider(activity).get(DocsDetailViewModel.class);
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocsDetailBinding binding = ItemDocsDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        DocsDetailItem item = detailItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return (detailItems != null ? detailItems.size() : 0);
    }

    private void showPopupMenu(View v, String sectionId) {
        PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.TOP);
        popupMenu.getMenuInflater().inflate(R.menu.highlight_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            TextView textView = (TextView) v;
            int startIdx = textView.getSelectionStart();
            int endIdx = textView.getSelectionEnd();
            if (startIdx == -1 || endIdx == -1 || startIdx == endIdx) {
                Toast.makeText(activity, "텍스트를 선택해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
            String selectedText = textView.getText().subSequence(startIdx, endIdx).toString();

            if (item.getItemId() == R.id.action_comment) {
                showCommentSheetDialog(new ArrayList<>(), sectionId, null, selectedText, String.valueOf(startIdx), String.valueOf(endIdx));
            } else if (item.getItemId() == R.id.action_search) {
                Intent searchIntent = new Intent(activity, KeywordDetailActivity.class);
                searchIntent.putExtra("keyword", selectedText);
                activity.startActivity(searchIntent);
            }
            return true;
        });
        popupMenu.show();
    }

    private void loadCommentsAndShowDialog(String sectionId, String highlightId, String highlightedText) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            CommentResponse response = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 100);
            ArrayList<DocsDetailCommentItem> commentItems = new ArrayList<>();
            if (response != null && response.getComments() != null) {
                for (Comment comment : response.getComments()) {
                    commentItems.add(new DocsDetailCommentItem(
                            comment.getUser().getProfile(), comment.getUser().getName(), comment.getCreatedTime(),
                            comment.getContent(), comment.getCommentId(), comment.isHasReply(), comment.isDeleted()));
                }
            }
            handler.post(() -> {
                buttonStatus = BUTTON_COMMENT_CONFIRM;
                showCommentSheetDialog(commentItems, sectionId, highlightId, highlightedText, null, null);
            });
        });
    }

    private void showCommentSheetDialog(ArrayList<DocsDetailCommentItem> commentItems, String sectionId, String highlightId, String name, String startIdx, String endIdx) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView commentTv = bottomSheetView.findViewById(R.id.textview_comment_name);
        RecyclerView commentRv = bottomSheetView.findViewById(R.id.recyclerview_comment);
        commentEt = bottomSheetView.findViewById(R.id.edittext_comment);
        ImageButton commentBtn = bottomSheetView.findViewById(R.id.image_button_comment_confirm);
        commentTv.setText(name);

        commentAdapter = new DocsDetailCommentAdapter(commentItems, activity, sectionId, highlightId, this);
        commentRv.setLayoutManager(new LinearLayoutManager(activity));
        commentRv.setAdapter(commentAdapter);

        setupCommentButton(bottomSheetDialog, commentBtn, sectionId, highlightId, startIdx, endIdx);
        commentEt.setOnFocusChangeListener((v, hasFocus) -> focusViewModel.setCommentFocusLiveData(hasFocus));
        bottomSheetDialog.show();
    }

    private void setupCommentButton(BottomSheetDialog bottomSheetDialog, ImageButton commentBtn, String sectionId, String highlightId, String startIdx, String endIdx) {
        Observable<Object> commentBtnClickStream = Observable.create(emitter -> commentBtn.setOnClickListener(v -> emitter.onNext(new Object())));
        disposable = commentBtnClickStream.throttleFirst(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(event -> {
            String text = commentEt.getText().toString();
            if (!text.trim().isEmpty()) {
                handleCommentAction(bottomSheetDialog, sectionId, highlightId, startIdx, endIdx, text.trim());
            } else {
                Toast.makeText(activity, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(activity, "댓글 처리중 오류 발생", Toast.LENGTH_SHORT).show());
    }

    private void handleCommentAction(BottomSheetDialog bottomSheetDialog, String sectionId, String highlightId, String startIdx, String endIdx, String text) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Response<Void> response;
            boolean isFirstComment = (highlightId == null || highlightId.isEmpty());

            if (buttonStatus == BUTTON_COMMENT_CONFIRM) {
                if (isFirstComment) {
                    response = apiClient.requestInsertFirstComment(folderId, recordId, sectionId, startIdx, endIdx, text);
                } else {
                    response = apiClient.requestInsertComment(folderId, recordId, sectionId, highlightId, text);
                }
                handler.post(() -> {
                    if (response != null && response.isSuccessful()) {
                        Toast.makeText(activity, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        if (isFirstComment) {
                            docsDetailViewModel.setIsFirstCommentLiveData(true);
                            bottomSheetDialog.dismiss();
                        } else {
                            commentAdapter.addNewItem(new DocsDetailCommentItem(myProfile, myName, "방금 전", text, "tempId", false, false));
                            commentEt.setText("");
                        }
                    } else {
                        Toast.makeText(activity, "댓글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (buttonStatus == BUTTON_COMMENT_UPDATE) {
                response = apiClient.updateComment(folderId, recordId, sectionId, highlightId, selectedId, text);
                handler.post(() -> {
                    if (response != null && response.isSuccessful()) {
                        commentAdapter.updateItem(text, selectedPosition);
                        commentEt.setText("");
                        Toast.makeText(activity, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "댓글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void clearDisposable() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (commentAdapter != null) {
            commentAdapter.clearSubDisposable();
        }
    }

    public void focusOnCommentEt() {
        if (commentEt != null) {
            commentEt.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(commentEt, InputMethodManager.SHOW_IMPLICIT);
            commentEt.setText(selectedText);
        }
    }

    public int getButtonStatus() { return buttonStatus; }
    public void setButtonStatus(int status) { this.buttonStatus = status; }
    public void setSelectedPosition(int position) { this.selectedPosition = position; }
    public void setSelectedId(String selectedId) { this.selectedId = selectedId; }
    public void setSelectedText(String selectedText) { this.selectedText = selectedText; }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        final ItemDocsDetailBinding binding;

        public AdapterViewHolder(ItemDocsDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DocsDetailItem item) {
            binding.textviewItemDocsDetailTitle.setText(item.getTitle());
            binding.textviewItemDocsDetailTitle.setOnClickListener(v -> activity.setDuration(Integer.parseInt(item.getTime())));

            int timeInSeconds = Integer.parseInt(item.getTime());
            String time = DateTimeFormatter.getTimeFormat(timeInSeconds * 1000);
            binding.textviewItemDocsDetailTime.setText(time);

            SpannableString spannable = new SpannableString(item.getDescription());
            for (Highlight highlight : item.getHighlights()) {
                int startIdx = highlight.getStartIndex();
                int endIdx = highlight.getEndIndex();
                if (startIdx < 0 || endIdx > spannable.length() || startIdx >= endIdx) continue;

                String highlightId = String.valueOf(highlight.getHighlightId());
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String highlightedText = ((TextView) widget).getText().subSequence(startIdx, endIdx).toString();
                        loadCommentsAndShowDialog(item.getSectionId(), highlightId, highlightedText);
                    }
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                        ds.setColor(binding.textviewItemDocsDetailDescription.getCurrentTextColor());
                    }
                };
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.primary_3));
                spannable.setSpan(backgroundColorSpan, startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(clickableSpan, startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            binding.textviewItemDocsDetailDescription.setText(spannable);
            binding.textviewItemDocsDetailDescription.setMovementMethod(LinkMovementMethod.getInstance());

            binding.textviewItemDocsDetailDescription.setOnTouchListener((v, event) -> {
                TextView textView = (TextView) v;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int startSelection = textView.getSelectionStart();
                    int endSelection = textView.getSelectionEnd();

                    if (startSelection != -1 && endSelection != -1 && startSelection != endSelection) {
                        Spannable spannableText = (Spannable) textView.getText();
                        ClickableSpan[] clickedSpans = spannableText.getSpans(startSelection, endSelection, ClickableSpan.class);

                        if (clickedSpans.length == 0) {
                            // 💡 showPopupMenu 대신 새로운 showCustomPopupWindow 호출
                            showCustomPopupWindow(v, item.getSectionId(), event);
                        }
                    }
                }
                return false;
            });
            binding.textviewItemDocsDetailDescription.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    menu.clear();
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // 액션 모드가 끝날 때의 동작
                }
            });

        }
        private void showCustomPopupWindow(View anchorView, String sectionId, MotionEvent event) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_highlight_menu, null);

            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            TextView commentMenu = popupView.findViewById(R.id.popup_menu_comment);
            TextView searchMenu = popupView.findViewById(R.id.popup_menu_search);

            TextView textView = (TextView) anchorView;

            commentMenu.setOnClickListener(v -> {
                int finalStartIdx = textView.getSelectionStart();
                int finalEndIdx = textView.getSelectionEnd();
                String finalSelectedText = textView.getText().subSequence(finalStartIdx, finalEndIdx).toString();

                showCommentSheetDialog(new ArrayList<>(), sectionId, null, finalSelectedText, String.valueOf(finalStartIdx), String.valueOf(finalEndIdx));
                popupWindow.dismiss();
            });

            searchMenu.setOnClickListener(v -> {
                int finalStartIdx = textView.getSelectionStart();
                int finalEndIdx = textView.getSelectionEnd();
                String finalSelectedText = textView.getText().subSequence(finalStartIdx, finalEndIdx).toString();

                Intent searchIntent = new Intent(activity, KeywordDetailActivity.class);
                searchIntent.putExtra("keyword", finalSelectedText);
                activity.startActivity(searchIntent);
                popupWindow.dismiss();
            });

            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOutsideTouchable(true);

            // 터치한 좌표에 팝업 윈도우를 표시
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.NO_GRAVITY, x, y);
        }
    }
}