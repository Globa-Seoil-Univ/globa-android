package team.y2k2.globa.docs.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.core.Observable;
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

    ItemDocsDetailBinding binding;

    private final CommentApiClient apiClient;
    private final ArrayList<DocsDetailItem> detailItems;
    private final DocsActivity activity;
    private final String folderId;
    private final String recordId;
    private final String myProfile;
    private final String myName;
    private final ArrayList<DocsDetailCommentItem> commentItems = new ArrayList<>();
    private final FocusViewModel focusViewModel;
    private final DocsDetailViewModel docsDetailViewModel;
    private int selectedPosition;
    private int buttonStatus = BUTTON_COMMENT_CONFIRM;
    private String selectedId;
    private EditText commentEt;
    private ImageButton commentBtn;
    private Disposable disposable;
    private DocsDetailCommentAdapter commentAdapter;
    private String selectedText;

    public DocsDetailAdapter(ArrayList<DocsDetailItem> detailItems, DocsActivity activity) {
        this.detailItems = detailItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.apiClient = new CommentApiClient();
        this.myProfile = activity.getProfile().startsWith("http") ? activity.getProfile() : ProfileImage.convertGsToHttps(FirebaseStorage.getInstance().getReference().child(activity.getProfile()).toString());
        this.myName = activity.getName();
        this.focusViewModel = new ViewModelProvider(activity).get(FocusViewModel.class);
        this.docsDetailViewModel = new ViewModelProvider(activity).get(DocsDetailViewModel.class);
    }

    @Override
    public int getItemCount() {
        return (null != detailItems ? detailItems.size() : 0);
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemDocsDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdapterViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        DocsDetailItem item = detailItems.get(position);

        binding.textviewItemDocsDetailTitle.setText(item.getTitle());
        binding.textviewItemDocsDetailTitle.setOnClickListener(v -> activity.setDuration(Integer.parseInt(item.getTime())));
        String time = DateTimeFormatter.getTimeFormat(Integer.parseInt(item.getTime()));
        binding.textviewItemDocsDetailTime.setText(time);

        SpannableString descriptionSpannable = setSpannableStringHighlight(new SpannableString(item.getDescription()), item.getHighlights(), holder, item.getSectionId());
        binding.textviewItemDocsDetailDescription.setText(descriptionSpannable);
        binding.textviewItemDocsDetailDescription.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textviewItemDocsDetailDescription.setOnTouchListener(createTouchListener(holder, item.getHighlights(), position, item.getSectionId()));
    }

    private View.OnTouchListener createTouchListener(AdapterViewHolder holder, List<Highlight> highlights, int position, String sectionId) {
        return (v, event) -> {
            int startIdx = holder.description.getSelectionStart();
            int endIdx = holder.description.getSelectionEnd();
            if (startIdx == -1 || endIdx == -1) return false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - holder.downTime < 900) {
                        handleHighlightOrPopupMenu(holder, highlights, position, sectionId, startIdx, endIdx);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    holder.downTime = System.currentTimeMillis();
                    break;
            }
            return false;
        };
    }

    private void handleHighlightOrPopupMenu(AdapterViewHolder holder, List<Highlight> highlights, int position, String sectionId, int startIdx, int endIdx) {
        for (Highlight highlight : highlights) {
            if (startIdx >= highlight.getStartIndex() && startIdx <= highlight.getEndIndex() || endIdx >= highlight.getStartIndex() && endIdx <= highlight.getEndIndex()) {
                selectedPosition = position;
                selectedId = String.valueOf(highlight.getHighlightId());
                docsDetailViewModel.getCommentLiveData().observe(activity, isReceived -> {
                    if (isReceived) {
                        buttonStatus = BUTTON_COMMENT_CONFIRM;
                        showCommentSheetDialog(commentItems, sectionId, selectedId, holder.description.getText().subSequence(highlight.getStartIndex(), highlight.getEndIndex()).toString(), String.valueOf(highlight.getStartIndex()), String.valueOf(highlight.getEndIndex()));
                        docsDetailViewModel.setCommentLiveData(false);
                    }
                });
                return;
            }
        }
        showPopupMenu(holder.description, holder, folderId, recordId, sectionId);
    }


    private void showPopupMenu(View v, AdapterViewHolder holder, String folderId, String recordId, String sectionId) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.getMenuInflater().inflate(R.menu.highlight_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int startIdx = holder.description.getSelectionStart();
            int endIdx = holder.description.getSelectionEnd();
            String selectedText = holder.description.getText().subSequence(startIdx, endIdx).toString();

            if (item.getItemId() == R.id.action_comment) {
                showCommentSheetDialog(null, sectionId, null, selectedText, String.valueOf(startIdx), String.valueOf(endIdx));
            } else if (item.getItemId() == R.id.action_search) {
                Intent searchIntent = new Intent(activity, KeywordDetailActivity.class);
                searchIntent.putExtra("keyword", selectedText);
                activity.startActivity(searchIntent);
            }
            return true;
        });
        popupMenu.show();
    }

    private SpannableString setSpannableStringHighlight(SpannableString selection, List<Highlight> highlights, AdapterViewHolder holder, String sectionId) {
        for (Highlight highlight : highlights) {
            int startIdx = highlight.getStartIndex();
            int endIdx = highlight.getEndIndex();
            String highlightId = String.valueOf(highlight.getHighlightId());

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    loadAndShowComments(sectionId, highlightId);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    ds.setUnderlineText(false);
                }
            };

            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_3));
            selection.setSpan(backgroundColorSpan, startIdx, endIdx, 0);
            selection.setSpan(clickableSpan, startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return selection;
    }

    private void loadAndShowComments(String sectionId, String highlightId) {
        CommentResponse response = apiClient.getComments(folderId, recordId, sectionId, highlightId, 1, 100);
        List<Comment> comments = response.getComments();

        commentItems.clear();
        if (comments != null) {
            for (Comment comment : comments) {
                String profile = comment.getUser().getProfile();
                String name = comment.getUser().getName();
                String createdTime = comment.getCreatedTime();
                String content = comment.getContent();
                String commentId = comment.getCommentId();
                boolean hasReply = comment.isHasReply();
                boolean isDeleted = comment.isDeleted();
                commentItems.add(new DocsDetailCommentItem(profile, name, createdTime, content, commentId, hasReply, isDeleted));
            }
        }
        docsDetailViewModel.setCommentLiveData(true);
    }

    private void showCommentSheetDialog(ArrayList<DocsDetailCommentItem> commentItems, String sectionId, String highlightId, String name, String startIdx, String endIdx) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView commentTv = bottomSheetView.findViewById(R.id.textview_comment_name);
        RecyclerView commentRv = bottomSheetView.findViewById(R.id.recyclerview_comment);
        commentEt = bottomSheetView.findViewById(R.id.edittext_comment);
        commentBtn = bottomSheetView.findViewById(R.id.image_button_comment_confirm);
        commentTv.setText(name);

        commentAdapter = new DocsDetailCommentAdapter(commentItems, activity, sectionId, highlightId, this);
        commentRv.setLayoutManager(new LinearLayoutManager(activity));
        commentRv.setAdapter(commentAdapter);

        setupCommentButton(bottomSheetDialog, sectionId, highlightId, startIdx, endIdx);

        commentEt.setOnFocusChangeListener((v, hasFocus) -> focusViewModel.setCommentFocusLiveData(hasFocus));

        bottomSheetDialog.show();
    }

    private void setupCommentButton(BottomSheetDialog bottomSheetDialog, String sectionId, String highlightId, String startIdx, String endIdx) {
        Observable<Object> commentBtnClickStream = Observable.create(emitter -> commentBtn.setOnClickListener(v -> emitter.onNext(new Object())));
        disposable = commentBtnClickStream.throttleFirst(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(event -> {
            String text = commentEt.getText().toString();
            if (!text.isEmpty()) {
                handleCommentAction(bottomSheetDialog, sectionId, highlightId, startIdx, endIdx, text);
            } else {
                Toast.makeText(activity, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(activity, "댓글 처리중 오류 발생", Toast.LENGTH_SHORT).show());
    }

    private void handleCommentAction(BottomSheetDialog bottomSheetDialog, String sectionId, String highlightId, String startIdx, String endIdx, String text) {
        if (buttonStatus == BUTTON_COMMENT_CONFIRM) {
            commentAdapter.addNewItem(new DocsDetailCommentItem(myProfile, myName, "방금전", text, "commentId", false, false));
            apiClient.requestInsertComment(folderId, recordId, sectionId, highlightId, text);
        } else if (buttonStatus == BUTTON_COMMENT_UPDATE) {
            commentAdapter.updateItem(text, selectedPosition);
            apiClient.updateComment(folderId, recordId, sectionId, highlightId, selectedId, text);
        }
        commentEt.setText("");
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
        commentEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEt, InputMethodManager.SHOW_IMPLICIT);
        commentEt.setText(selectedText);
    }

    public int getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(int status) {
        this.buttonStatus = status;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView time;
        final TextView description;
        long downTime;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_item_docs_detail_title);
            time = itemView.findViewById(R.id.textview_item_docs_detail_time);
            description = itemView.findViewById(R.id.textview_item_docs_detail_description);
        }
    }
}