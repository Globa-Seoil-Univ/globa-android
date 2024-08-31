package team.y2k2.globa.docs.detail.comment.subcomment;

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

    public DocsDetailSubCommentAdapter(List<DocsDetailSubCommentItem> subCommentItems, DocsActivity activity) {
        this.subCommentItems = subCommentItems;
        this.activity = activity;
        this.folderId = activity.getFolderId();
        this.recordId = activity.getRecordId();
        this.sectionId = mainAdapter.getDocsSectionId();
        this.highlightId = mainAdapter.getDocsHighlightId();
    }

    public void updateAllData(List<DocsDetailSubCommentItem> subCommentItemList) {
        this.subCommentItems = subCommentItemList;
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
