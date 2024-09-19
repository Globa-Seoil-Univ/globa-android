package team.y2k2.globa.docs.detail.comment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.SubComment;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.detail.comment.subcomment.DocsDetailSubCommentAdapter;
import team.y2k2.globa.docs.detail.comment.subcomment.DocsDetailSubCommentItem;
import team.y2k2.globa.main.ProfileImage;

public class DocsDetailCommentAdapter extends RecyclerView.Adapter<DocsDetailCommentAdapter.AdapterViewHolder> {

    ArrayList<DocsDetailCommentItem> commentItems;

    DocsActivity activity;
    String folderId;
    String recordId;
    String sectionId;
    String highlightId;

    String myProfile;
    String myName;

    int selectedPosition;
    String selectedId;

    ImageView subCommentImg;
    TextView subCommentTv;
    RecyclerView subCommentRv;
    EditText subCommentEt;
    ImageButton subCommentBtn;

    private Disposable disposable;

    private MutableLiveData<Boolean> subCommentEtFocus = new MutableLiveData<>();

    ArrayList<DocsDetailSubCommentItem> subCommentItems = new ArrayList<>();

    private DocsDetailAdapter mainAdapter;
    private DocsDetailSubCommentAdapter subAdapter;

    private final int BUTTON_COMMENT_CONFIRM = 0;
    private final int BUTTON_COMMENT_UPDATE = 1;
    private final int BUTTON_COMMENT_SUB_CONFIRM = 2;
    private final int BUTTON_COMMENT_SUB_UPDATE = 3;

    private int subButtonStatus = BUTTON_COMMENT_SUB_CONFIRM;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    ApiClient apiClient;

    private FocusViewModel focusViewModel;

    public DocsDetailCommentAdapter(ArrayList<DocsDetailCommentItem> commentItems, DocsActivity activity, String sectionId, String highlightId, DocsDetailAdapter mainAdapter) {
        this.commentItems = commentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = sectionId;
        this.highlightId = highlightId;
        this.mainAdapter = mainAdapter;
        focusViewModel = new ViewModelProvider(activity).get(FocusViewModel.class);
    }

    public void addNewItem(DocsDetailCommentItem newItem) {
        if(commentItems == null) {
            commentItems = new ArrayList<>();
        }
        commentItems.add(newItem);
        notifyDataSetChanged();
    }

    public void updateItem(String text, int position) {
        commentItems.get(position).setContent(text);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        commentItems.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public DocsDetailCommentAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsDetailCommentAdapter.AdapterViewHolder holder, int position) {

        String profile = commentItems.get(position).getProfile();
        String name = commentItems.get(position).getName();
        String createdTime = commentItems.get(position).getCreatedTime();
        String content = commentItems.get(position).getContent();
        String commentId = commentItems.get(position).getCommentId();

        if(profile != null) {
            if(profile.startsWith("http")) {
                Glide.with(activity).load(profile).error(R.mipmap.ic_launcher).into(holder.profileImage);
            } else {
                profileImageRef = storage.getReference().child(profile);
                Glide.with(activity).load(ProfileImage.convertGsToHttps(profileImageRef.toString())).error(R.mipmap.ic_launcher).into(holder.profileImage);
            }
        } else {
            Glide.with(activity).load(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.profileImage);
        }

        holder.name.setText(name);
        holder.createdTime.setText(createdTime);
        holder.content.setText(content);
        holder.showSubComments.setText("답글 보기");

        holder.showSubComments.setOnClickListener(v -> {
            // 답글 보기 클릭 이벤트
            Log.d("대댓글 보기", "대댓글 보기 클릭 이벤트 시작");

            // 대댓글 호출
            apiClient = new ApiClient(activity);
            List<SubComment> subCommentList = apiClient.getSubComments(folderId, recordId, sectionId, highlightId, commentId, 1, 10).getSubComments();
            subCommentItems.clear();
            for(SubComment subComment : subCommentList) {
                String subProfile = subComment.getUser().getProfile();
                String subName = subComment.getUser().getName();
                String subCreatedTime = subComment.getCreatedTime();
                String subContent = subComment.getContent();
                String subId = subComment.getCommentId();
                Log.d("대댓글 호출", "subProfile: " + subProfile + ", subName: " + subName + ", subCreatedTime: " + subCreatedTime +
                        ", subContent: " + subContent);
                subCommentItems.add(new DocsDetailSubCommentItem(subProfile, subName, subCreatedTime, subContent, subId));
            }

            String parentId = commentItems.get(position).getCommentId();
            String parentProfile = commentItems.get(position).getProfile();
            String parentContent = commentItems.get(position).getContent();
            Log.d(getClass().getSimpleName(), "선택 댓글 ID: " + parentId);
            Log.d(getClass().getSimpleName(), "선택 댓글 프로필: " + parentProfile);
            Log.d(getClass().getSimpleName(), "선택 댓글 내용: " + parentContent);

            showSubCommentSheetDialog(subCommentItems, parentProfile, parentContent, sectionId, highlightId, parentId);

        });

        // 댓글 수정 또는 삭제
        holder.itemView.setOnLongClickListener(view -> {
            view.showContextMenu();
            return true;
        });
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            MenuInflater inflater = new MenuInflater(activity);
            inflater.inflate(R.menu.comment_menu, menu);

            menu.findItem(R.id.action_comment_update).setOnMenuItemClickListener(menuItem -> {
                // 댓글 수정 동작
                mainAdapter.focusOnCommentEt();

                focusViewModel.getCommentFocusLiveData().observe(activity, hasFocus -> {
                    if(hasFocus) {
                        // 댓글 수정 동작 (버튼 상태 변경)
                        Log.d("댓글 버튼 상태", "댓글 버튼 상태 수정 상태로 전환 시작");
                        mainAdapter.setButtonStatus(BUTTON_COMMENT_UPDATE);
                        mainAdapter.setSelectedPosition(position);
                        mainAdapter.setSelectedId(commentItems.get(position).getCommentId());
                        Log.d("선택한 댓글", "선택한 댓글 위치: " + position);
                        Log.d("댓글 버튼 상태", "댓글 버튼 상태 buttonStatus: " + mainAdapter.getButtonStatus());
                    } else {
                        Log.d(getClass().getSimpleName(), "EditText Focus 실패, 댓글 버튼 상태 전환 실패");
                        mainAdapter.setButtonStatus(BUTTON_COMMENT_CONFIRM);
                        Toast.makeText(activity, "댓글 수정 불가", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            });
            menu.findItem(R.id.action_comment_delete).setOnMenuItemClickListener(menuItem -> {
                // 댓글 삭제 동작
                showBottomSheetDialog(commentItems.get(position).getCommentId(), position);
                return true;
            });
        });

    }

    @Override
    public int getItemCount() {
        return (commentItems != null ? commentItems.size() : 0);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView name;
        TextView createdTime;
        TextView content;
        TextView showSubComments;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageview_item_comment_icon);
            name = itemView.findViewById(R.id.textview_item_comment_username);
            createdTime = itemView.findViewById(R.id.textview_item_comment_datetime);
            content = itemView.findViewById(R.id.textview_item_comment_content);
            showSubComments = itemView.findViewById(R.id.textview_item_comment_visible);

        }
    }

    private void showSubCommentSheetDialog(ArrayList<DocsDetailSubCommentItem> subCommentItems, String profile, String content,
                                           String sectionId, String highlightId, String parentId) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_comment_sub, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        subCommentImg = bottomSheetView.findViewById(R.id.imageview_comment_sub_parent);
        subCommentTv = bottomSheetView.findViewById(R.id.textview_comment_sub_parent);
        subCommentRv = bottomSheetDialog.findViewById(R.id.recyclerview_comment_sub);
        subCommentEt = bottomSheetDialog.findViewById(R.id.edittext_comment_sub);
        subCommentBtn = bottomSheetDialog.findViewById(R.id.imagebutton_comment_sub_confirm);

        Log.d("대댓글 창", "대댓글 창 열림 subButtonStatus: ");

        if(profile != null) {
            if(profile.startsWith("http")) {
                Glide.with(activity).load(profile).error(R.drawable.profile_user).into(subCommentImg);
            } else {
                profileImageRef = storage.getReference().child(profile);
                Glide.with(activity).load(ProfileImage.convertGsToHttps(profileImageRef.toString())).error(R.drawable.profile_user).into(subCommentImg);
            }
        } else {
            Glide.with(activity).load(R.drawable.profile_user).error(R.drawable.profile_user).into(subCommentImg);
        }

        subCommentTv.setText(content);

        subAdapter = new DocsDetailSubCommentAdapter(subCommentItems, activity, sectionId, highlightId, this);
        subCommentRv.setLayoutManager(new LinearLayoutManager(activity));
        subCommentRv.setAdapter(subAdapter);

        Observable<Object> subCommentBtnClickStream = Observable.create(emitter -> {
            subCommentBtn.setOnClickListener(v -> emitter.onNext(new Object()));
        });
        disposable = subCommentBtnClickStream.throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    String text = subCommentEt.getText().toString();
                    if(!text.isEmpty()) {
                        Log.d(getClass().getSimpleName(), "대댓글 버튼 상태: " + subButtonStatus);
                        if(subButtonStatus == BUTTON_COMMENT_SUB_CONFIRM) {
                            Log.d("대댓글 추가", "내 프로필: " + myProfile + ", 내 이름: " + myName + ", 작성 내용: " + text);
                            // 대댓글 아이템 리스트 추가
                            subAdapter.addNewItem(new DocsDetailSubCommentItem(myProfile, myName, "방금전", text, "commentId"));

                            // API Request
                            apiClient.requestInsertSubComment(folderId, recordId,sectionId, highlightId, parentId, text);
                        } else if(subButtonStatus == BUTTON_COMMENT_SUB_UPDATE) {
                            // 대댓글 아이템 수정
                            subAdapter.updateItem(text, selectedPosition);

                            // API Request
                            apiClient.updateComment(folderId, recordId, sectionId, highlightId, selectedId, text);
                        }

                        subCommentEt.setText("");
                    } else {
                        Toast.makeText(activity, "답글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("RxJavaError", "RxJavaError 대댓글 오류 내용: " + error);
                    Toast.makeText(activity, "답글 처리중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            );

        subCommentEt.setOnFocusChangeListener((v, hasFocus) -> {
            // 포커스를 얻으면 true, 읽으면 false;
            subCommentEtFocus.setValue(hasFocus);
            focusViewModel.setSubCommentFocusLiveData(hasFocus);
        });

        bottomSheetDialog.show();

    }

    private void showBottomSheetDialog(String commentId, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_delete_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView confirmBtn = bottomSheetView.findViewById(R.id.textview_delete_comment_confirm);
        TextView cancelBtn = bottomSheetView.findViewById(R.id.textview_delete_comment_cancel);

        confirmBtn.setOnClickListener(v -> {
            removeItem(position);
            if(apiClient == null) {
                apiClient = new ApiClient(activity);
            }
            apiClient.deleteComment(folderId, recordId, sectionId, highlightId, commentId);
            bottomSheetDialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    public void clearSubDisposable() {
        if(disposable != null && !disposable.isDisposed()) {
            Log.d("대댓글", "대댓글 disposable 메모리 해제 시작");
            disposable.dispose();
        }
    }

    public void focusOnSubCommentEt() {
        subCommentEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(subCommentEt, InputMethodManager.SHOW_IMPLICIT);
    }


    public void setSubButtonStatus(int subButtonStatus) {
        this.subButtonStatus = subButtonStatus;
    }
    public int getSubButtonStatus() {
        return subButtonStatus;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

}
