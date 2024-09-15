package team.y2k2.globa.docs.detail.comment.subcomment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;

public class DocsDetailSubCommentAdapter extends RecyclerView.Adapter<DocsDetailSubCommentAdapter.AdapterViewHolder> {

    List<DocsDetailSubCommentItem> subCommentItems;

    DocsActivity activity;
    String folderId;
    String recordId;
    String sectionId;
    String highlightId;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;

    private DocsDetailAdapter mainAdapter;

    public DocsDetailSubCommentAdapter(List<DocsDetailSubCommentItem> subCommentItems, DocsActivity activity, String sectionId, String highlightId) {
        this.subCommentItems = subCommentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = sectionId;
        this.highlightId = highlightId;
    }

    public void updateAllData(List<DocsDetailSubCommentItem> subCommentItemList) {
        Log.d("대댓글 어댑터", "대댓글 어댑터 updateAllData() 시작");
        this.subCommentItems = subCommentItemList;
        Log.d("대댓글 어댑터", "대댓글 아이템: " + this.subCommentItems.get(0).getContent());

        notifyDataSetChanged();
    }

    public void updateData(List<DocsDetailSubCommentItem> subCommentItems, String text, int position) {
        subCommentItems.get(position).setContent(text);
        notifyItemChanged(position);
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
            Glide.with(activity).load(profile).error(R.mipmap.ic_launcher).into(holder.profileImage);
        } else {
            Glide.with(activity).load(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.profileImage);
        }


        holder.name.setText(name);
        holder.createdTime.setText(createdTime);
        holder.content.setText(content);
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

            profileImage = itemView.findViewById(R.id.imageview_item_comment_icon);
            name = itemView.findViewById(R.id.textview_item_comment_username);
            createdTime = itemView.findViewById(R.id.textview_item_comment_datetime);
            content = itemView.findViewById(R.id.textview_item_comment_content);
        }
    }

}
