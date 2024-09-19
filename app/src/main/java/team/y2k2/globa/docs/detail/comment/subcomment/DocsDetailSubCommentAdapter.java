package team.y2k2.globa.docs.detail.comment.subcomment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentAdapter;
import team.y2k2.globa.docs.detail.comment.FocusViewModel;
import team.y2k2.globa.main.ProfileImage;

public class DocsDetailSubCommentAdapter extends RecyclerView.Adapter<DocsDetailSubCommentAdapter.AdapterViewHolder> {

    List<DocsDetailSubCommentItem> subCommentItems;

    DocsActivity activity;
    String folderId;
    String recordId;
    String sectionId;
    String highlightId;

    private DocsDetailCommentAdapter commentAdapter;

    private final int BUTTON_COMMENT_SUB_CONFIRM = 2;
    private final int BUTTON_COMMENT_SUB_UPDATE = 3;

    private ApiClient apiClient;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    private FocusViewModel focusViewModel;

    public DocsDetailSubCommentAdapter(List<DocsDetailSubCommentItem> subCommentItems, DocsActivity activity, String sectionId, String highlightId, DocsDetailCommentAdapter commentAdapter) {
        this.subCommentItems = subCommentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = sectionId;
        this.highlightId = highlightId;
        this.commentAdapter = commentAdapter;
        this.apiClient = new ApiClient(activity);
        focusViewModel = new ViewModelProvider(activity).get(FocusViewModel.class);
    }

    public void addNewItem(DocsDetailSubCommentItem newItem) {
        if(subCommentItems == null) {
            subCommentItems = new ArrayList<>();
        }
        subCommentItems.add(newItem);
        notifyDataSetChanged();
    }

    public void updateItem(String text, int position) {
        subCommentItems.get(position).setContent(text);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        subCommentItems.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public DocsDetailSubCommentAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_sub, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsDetailSubCommentAdapter.AdapterViewHolder holder, int position) {

        String profile = subCommentItems.get(position).getProfile();
        String name = subCommentItems.get(position).getName();
        String createdTime = subCommentItems.get(position).getCreatedTime();
        String content = subCommentItems.get(position).getContent();

        if(profile != null) {
            Log.d(getClass().getSimpleName(), "프로필 경로: " + profile);
            if(profile.startsWith("http")) {
                Glide.with(activity).load(profile).error(R.mipmap.ic_launcher).into(holder.profileImage);
            } else {
                profileImageRef = storage.getReference().child(profile);
                Glide.with(activity).load(ProfileImage.convertGsToHttps(profileImageRef.toString())).error(R.mipmap.ic_launcher).into(holder.profileImage);
            }
        } else {
            Glide.with(activity).load(R.drawable.profile_user).error(R.mipmap.ic_launcher).into(holder.profileImage);
        }

        holder.name.setText(name);
        holder.createdTime.setText(createdTime);
        holder.content.setText(content);

        holder.itemView.setOnLongClickListener(v -> {
            v.showContextMenu();
            return true;
        });
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            MenuInflater inflater = new MenuInflater(activity);
            inflater.inflate(R.menu.comment_menu, menu);

            menu.findItem(R.id.action_comment_update).setOnMenuItemClickListener(menuItem -> {

                commentAdapter.focusOnSubCommentEt();

                focusViewModel.getSubCommentFocusLiveData().observe(activity, hasFocus -> {
                    if(hasFocus) {
                        // 대댓글 수정 동작 (버튼 상태 변경)
                        Log.d("대댓글 버튼 상태", "대댓글 버튼 상태 수정 상태로 번환 시작");
                        commentAdapter.setSubButtonStatus(BUTTON_COMMENT_SUB_UPDATE);
                        commentAdapter.setSelectedPosition(position);
                        commentAdapter.setSelectedId(subCommentItems.get(position).getCommentId());
                        Log.d("선택한 대댓글", "선택한 대댓글 위치: " + position);
                        Log.d("대댓글 버튼 상태", "대댓글 버튼 상태 subButtonStatus: " + commentAdapter.getSubButtonStatus());
                    } else {
                        Log.d(getClass().getSimpleName(), "EditText Focus 실패, 대댓글 버튼 상태 전환 실패");
                        commentAdapter.setSubButtonStatus(BUTTON_COMMENT_SUB_CONFIRM);
                        Toast.makeText(activity, "답글 수정 불가", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            });
            menu.findItem(R.id.action_comment_delete).setOnMenuItemClickListener(menuItem -> {
                // 대댓글 삭제 동작
                showBottomSheetDialog(subCommentItems.get(position).getCommentId(), position);
                return true;
            });
        });
    }

    @Override
    public int getItemCount() {
        return (subCommentItems != null ? subCommentItems.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView name;
        TextView createdTime;
        TextView content;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageview_item_comment_sub_icon);
            name = itemView.findViewById(R.id.textview_item_comment_sub_username);
            createdTime = itemView.findViewById(R.id.textview_item_comment_sub_datetime);
            content = itemView.findViewById(R.id.textview_item_comment_sub_content);
        }
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

}
