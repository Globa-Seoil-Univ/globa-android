package team.y2k2.globa.docs.detail.comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.SubComment;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.detail.comment.subcomment.DocsDetailSubCommentAdapter;
import team.y2k2.globa.docs.detail.comment.subcomment.DocsDetailSubCommentItem;

public class DocsDetailCommentAdapter extends RecyclerView.Adapter<DocsDetailCommentAdapter.AdapterViewHolder> {

    ArrayList<DocsDetailCommentItem> commentItems;

    DocsActivity activity;
    String folderId;
    String recordId;
    String sectionId;
    String highlightId;

    ArrayList<DocsDetailSubCommentItem> subCommentItems = new ArrayList<>();

    private DocsDetailAdapter mainAdapter;
    DocsDetailSubCommentAdapter subAdapter;

    private final int BUTTON_COMMENT_CONFIRM = 0;
    private final int BUTTON_COMMENT_SUB_CONFIRM = 1;
    private final int BUTTON_COMMENT_UPDATE = 2;
    private final int BUTTON_COMMENT_SUB_UPDATE = 3;

    ApiClient apiClient;

    public DocsDetailCommentAdapter(ArrayList<DocsDetailCommentItem> commentItems, DocsActivity activity, String sectionId, String highlightId, DocsDetailAdapter mainAdapter) {
        this.commentItems = commentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = sectionId;
        this.highlightId = highlightId;
        this.mainAdapter = mainAdapter;
    }

    public void addItemToSubCommentRecyclerView(int position, DocsDetailSubCommentItem item) {
        commentItems.get(position).getSubCommentItemList().add(item);
        notifyItemChanged(position);
    }

    public void addNewItem(DocsDetailCommentItem newItem) {
        if(commentItems == null) {
            commentItems = new ArrayList<>();
        }
        commentItems.add(newItem);
        notifyDataSetChanged();
    }

    public void updateItemToSubCommentRecyclerView(String text, int parentPosition, int position) {
        // 대댓글 수정
        List<DocsDetailSubCommentItem> subCommentItemList = commentItems.get(parentPosition).getSubCommentItemList();
        subAdapter.updateData(subCommentItemList, text, position);
        notifyItemChanged(parentPosition);
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
        boolean isHasSubComment = commentItems.get(position).isHasSubComment();

        if(profile != null) {
            Glide.with(activity).load(profile).error(R.mipmap.ic_launcher).into(holder.profileImage);
        } else {
            Glide.with(activity).load(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.profileImage);
        }


        holder.name.setText(name);
        holder.createdTime.setText(createdTime);
        holder.content.setText(content);
        holder.writeSubComment.setText("답글 작성");
        if(isHasSubComment) {
            holder.showSubComments.setText("답글 보기");
        } else {
            holder.showSubComments.setVisibility(View.GONE);
        }

        holder.showSubComments.setOnClickListener(v -> {
            // 답글 더보기 클릭 이벤트
            if(holder.subCommentRecyclerview.getVisibility() == View.GONE) {
                holder.subCommentRecyclerview.setVisibility(View.VISIBLE);

                Log.d("대댓글 보기", "대댓글 보기 클릭 이벤트 시작");

                // 대댓글 호출
                apiClient = new ApiClient(activity);
                List<SubComment> subCommentList = apiClient.getSubComments(folderId, recordId, sectionId, highlightId, commentId, 1, 10).getSubComments();
                for(SubComment subComment : subCommentList) {
                    String subProfile = subComment.getUser().getProfile();
                    String subName = subComment.getUser().getName();
                    String subCreatedTime = subComment.getCreatedTime();
                    String subContent = subComment.getContent();
                    Log.d("대댓글 호출", "subProfile: " + subProfile + ", subName: " + subName + ", subCreatedTime: " + subCreatedTime +
                            ", subContent: " + subContent);
                    subCommentItems.add(new DocsDetailSubCommentItem(subProfile, subName, subCreatedTime, subContent));
                }

                subAdapter.updateAllData(subCommentItems);

            } else {
                holder.subCommentRecyclerview.setVisibility(View.GONE);
            }

        });

        holder.writeSubComment.setOnClickListener(v -> {
            mainAdapter.setParentId(commentItems.get(position).getCommentId());
            Log.d("대댓글 작성", "parentId 설정 완료 ID: " + mainAdapter.getParentId());
            // 답글 달기 클릭 이벤트 (대댓글 달기 활성화)
            if(mainAdapter.getButtonStatus() == BUTTON_COMMENT_CONFIRM) {
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 대댓글 작성을 전환 시작");
                mainAdapter.setButtonStatus(BUTTON_COMMENT_SUB_CONFIRM);
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 buttonStatus: " + mainAdapter.getButtonStatus());
            } else if(mainAdapter.getButtonStatus() == BUTTON_COMMENT_SUB_CONFIRM) {
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 댓글 작성을 전환 시작");
                mainAdapter.setButtonStatus(BUTTON_COMMENT_CONFIRM);
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 buttonStatus: " + mainAdapter.getButtonStatus());
            } else {
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 : 수정 상태");
            }
            mainAdapter.setSelectedPosition(position);
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
                // 댓글 수정 동작 (버튼 상태 변경)
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 수정 상태로 전환 시작");
                mainAdapter.setButtonStatus(BUTTON_COMMENT_UPDATE);
                mainAdapter.setSelectedPosition(position);
                mainAdapter.setSelectedId(commentItems.get(position).getCommentId());
                Log.d("선택한 댓글", "선택한 댓글 위치: " + position);
                Log.d("댓글 버튼 상태", "댓글 버튼 상태 buttonStatus: " + mainAdapter.getButtonStatus());
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
        TextView writeSubComment;

        RecyclerView subCommentRecyclerview;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageview_item_comment_icon);
            name = itemView.findViewById(R.id.textview_item_comment_username);
            createdTime = itemView.findViewById(R.id.textview_item_comment_datetime);
            content = itemView.findViewById(R.id.textview_item_comment_content);
            showSubComments = itemView.findViewById(R.id.textview_item_comment_visible);
            writeSubComment = itemView.findViewById(R.id.textview_item_comment_add);

            subCommentRecyclerview = itemView.findViewById(R.id.recyclerview_docs_comment_sub);
            subAdapter = new DocsDetailSubCommentAdapter(subCommentItems, (DocsActivity) itemView.getContext(), sectionId, highlightId);
            subCommentRecyclerview.setAdapter(subAdapter);


        }
    }

    public void showBottomSheetDialog(String commentId, int position) {
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
