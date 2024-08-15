package team.y2k2.globa.docs.detail.comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    ArrayList<DocsDetailSubCommentItem> subCommentItems = new ArrayList<>();

    private DocsDetailAdapter mainAdapter;
    DocsDetailSubCommentAdapter subAdapter;

    private final int BUTTON_COMMENT_CONFIRM = 0;
    private final int BUTTON_COMMENT_SUB_CONFIRM = 1;
    private final int BUTTON_COMMENT_UPDATE = 2;
    private final int BUTTON_COMMENT_SUB_UPDATE = 3;

    public DocsDetailCommentAdapter(ArrayList<DocsDetailCommentItem> commentItems, DocsActivity activity) {
        this.commentItems = commentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = mainAdapter.getDocsSectionId();
        this.highlightId = mainAdapter.getDocsHighlightId();
    }

    public void addItemToSubCommentRecyclerView(int position, DocsDetailSubCommentItem item) {
        commentItems.get(position).getSubCommentItemList().add(item);
        notifyItemChanged(position);
    }

    public void addNewItem(DocsDetailCommentItem newItem) {
        commentItems.add(newItem);
        notifyItemInserted(commentItems.size() - 1);
    }

    public void updateItem(String text, int position) {
        commentItems.get(position).setContent(text);
        notifyItemChanged(position);
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

        String newProfile;
        if(profile != null) {
            profileImageRef = storage.getReference().child(profile);
            newProfile = convertGsToHttps(String.valueOf(profileImageRef));
        } else {
            newProfile = null;
        }

        Glide.with(activity).load(newProfile).error(R.mipmap.ic_launcher).into(holder.profileImage);
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
                // 대댓글 호출
                ApiClient apiClient = new ApiClient(activity);
                List<SubComment> subCommentList = apiClient.getSubComments(folderId, recordId, sectionId, highlightId, commentId, 1, 10).getSubComments();
                for(SubComment subComment : subCommentList) {
                    String subProfile = subComment.getUser().getProfile();
                    String subName = subComment.getUser().getName();
                    String subCreatedTime = subComment.getCreatedTime();
                    String subContent = subComment.getContent();
                    subCommentItems.add(new DocsDetailSubCommentItem(subProfile, subName, subCreatedTime, subContent));
                }

                subAdapter.updateData(subCommentItems);

            } else {
                holder.subCommentRecyclerview.setVisibility(View.GONE);
            }

        });
        holder.writeSubComment.setOnClickListener(v -> {
            // 답글 달기 클릭 이벤트 (대댓글 달기 활성화)
            if(mainAdapter.getButtonStatus() == BUTTON_COMMENT_CONFIRM) {
                mainAdapter.setButtonStatus(BUTTON_COMMENT_SUB_CONFIRM);
            } else if(mainAdapter.getButtonStatus() == BUTTON_COMMENT_SUB_CONFIRM) {
                mainAdapter.setButtonStatus(BUTTON_COMMENT_CONFIRM);
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
                // 댓글 수정 동작

                return true;
            });
            menu.findItem(R.id.action_comment_delete).setOnMenuItemClickListener(menuItem -> {
               // 댓글 삭제 동작

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
            subAdapter = new DocsDetailSubCommentAdapter(subCommentItems, (DocsActivity) itemView.getContext());
            subCommentRecyclerview.setAdapter(subAdapter);


        }
    }

    public static String convertGsToHttps(String gsUrl) {
        if (!gsUrl.startsWith("gs://")) {
            throw new IllegalArgumentException("Invalid gs:// URL");
        }

        // Extract bucket name and path
        int bucketEndIndex = gsUrl.indexOf("/", 5); // Find the end of bucket name
        if (bucketEndIndex == -1 || bucketEndIndex == gsUrl.length() - 1) {
            throw new IllegalArgumentException("Invalid gs:// URL format");
        }

        String bucketName = gsUrl.substring(5, bucketEndIndex);
        String filePath = gsUrl.substring(bucketEndIndex + 1);

        // URL encode the file path
        String encodedFilePath;
        try {
            encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL encoding failed", e);
        }

        // Construct the HTTPS URL
        String httpsUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName +
                "/o/" + encodedFilePath + "?alt=media";

        return httpsUrl;
    }
}
