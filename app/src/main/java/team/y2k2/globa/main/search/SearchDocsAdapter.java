package team.y2k2.globa.main.search;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;

public class SearchDocsAdapter extends RecyclerView.Adapter<SearchDocsAdapter.AdapterViewHolder> {
    ArrayList<SearchDocsItem> items;
    public SearchDocsAdapter(ArrayList<SearchDocsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());

        holder.itemView.setOnClickListener(v -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // 저장소 참조
            StorageReference storageRef = storage.getReference();

            Log.d(getClass().getName(), items.get(position).getFolderId());

            // 저장된 음악 파일 경로
            String filePath = "folders/" + items.get(position).getFolderId()+"/" + "1724947418.ogg";

            // 해당 파일의 참조
            StorageReference audioRef = storageRef.child(filePath);

            // Firebase Storage에서 MP3 파일 다운로드 및 준비
            // 다운로드 URL 가져오기
            audioRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String audioUrl = uri.toString();
                        // 이제 downloadUrl을 사용하여 음악 파일을 재생하거나 처리할 수 있습니다.
                        Intent intent = new Intent(holder.itemView.getContext(), DocsActivity.class);

                        intent.putExtra("title", items.get(position).getTitle());
                        intent.putExtra("audioUrl", audioUrl);
                        intent.putExtra("folderId", items.get(position).getFolderId());
                        intent.putExtra("recordId", items.get(position).getRecordId());

                        holder.itemView.getContext().startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "다운로드 URL 가져오기 실패", e);
                    });
        });
    }


    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView datetime;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_document_title);
            datetime = itemView.findViewById(R.id.textview_document_datetime);
        }
    }


}