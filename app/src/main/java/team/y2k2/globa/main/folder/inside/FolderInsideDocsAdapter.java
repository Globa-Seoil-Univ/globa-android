package team.y2k2.globa.main.folder.inside;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;
import team.y2k2.globa.main.docs.keyword.DocsKeywordAdapter;
import team.y2k2.globa.main.docs.keyword.DocsKeywordModel;
import team.y2k2.globa.main.docs.list.DocsListItemAdapter;

public class FolderInsideDocsAdapter extends RecyclerView.Adapter<FolderInsideDocsAdapter.AdapterViewHolder> {
    ArrayList<FolderInsideDocsItem> items;

    public FolderInsideDocsAdapter(ArrayList<FolderInsideDocsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FolderInsideDocsAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new FolderInsideDocsAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderInsideDocsAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());

        BottomSheetDialog moreBottomSheet = new BottomSheetDialog(holder.more.getContext());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(moreBottomSheet.getContext());

        moreBottomSheet.setContentView(R.layout.dialog_more_docs);

        bottomSheetDialog.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = bottomSheetDialog.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = bottomSheetDialog.findViewById(R.id.textview_delete_docs_cancel);


        holder.more.setOnClickListener(v -> {

            // 버튼 클릭 리스너를 별도의 메서드로 분리
            confirm.setOnClickListener(d2 -> {
                bottomSheetDialog.dismiss();
            });
            cancel.setOnClickListener(d2 -> {
                bottomSheetDialog.dismiss();
                moreBottomSheet.show();
            });

            holder.more.setOnClickListener(view -> {
                moreBottomSheet.show();

                RelativeLayout rename = moreBottomSheet.findViewById(R.id.relativelayout_more_rename);
                rename.setOnClickListener(d1 -> {
                    moreBottomSheet.dismiss();
                    Intent intent = new Intent(holder.itemView.getContext(), DocsNameEditActivity.class);
                    intent.putExtra("recordId", items.get(position).getRecordId());
                    intent.putExtra("folderId", items.get(position).getFolderId());
                    intent.putExtra("title", items.get(position).getTitle());
                    holder.itemView.getContext().startActivity(intent);
                });

                RelativeLayout move = moreBottomSheet.findViewById(R.id.relativelayout_more_move);
                move.setOnClickListener(d1 -> {
                    moreBottomSheet.dismiss();
                    Intent intent = new Intent(holder.itemView.getContext(), DocsMoveActivity.class);
                    intent.putExtra("recordId", items.get(position).getRecordId());
                    intent.putExtra("folderId", items.get(position).getFolderId());
                    intent.putExtra("title", items.get(position).getTitle());
                    holder.itemView.getContext().startActivity(intent);
                });


                RelativeLayout delete = moreBottomSheet.findViewById(R.id.relativelayout_more_delete);
                delete.setOnClickListener(d1 -> {
                    moreBottomSheet.dismiss();
                    bottomSheetDialog.show();
                });
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

        ImageView more;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_document_title);
            datetime = itemView.findViewById(R.id.textview_document_datetime);
            more = itemView.findViewById(R.id.imageview_document_more);
        }
    }


}