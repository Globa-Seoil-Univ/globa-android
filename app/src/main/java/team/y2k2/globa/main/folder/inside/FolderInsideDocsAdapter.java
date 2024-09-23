package team.y2k2.globa.main.folder.inside;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;

public class FolderInsideDocsAdapter extends RecyclerView.Adapter<FolderInsideDocsAdapter.AdapterViewHolder> {
    ArrayList<FolderInsideDocsItem> items;

    BottomSheetDialog bottomSheetDialog;
    BottomSheetDialog moreBottomSheet;

    Context context;
    FolderInsideFragment fragment;

    public FolderInsideDocsAdapter(ArrayList<FolderInsideDocsItem> items, FolderInsideFragment fragment) {
        this.items = items;
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (items.get(0).getFolderId().equals(""))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs_null, parent, false);
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        }

        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        if(items.get(0).getFolderId().equals("")) {
            holder.title.setText("문서를 찾을 수 없습니다.");
            return;
        }


        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());

        moreBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
        bottomSheetDialog = new BottomSheetDialog(moreBottomSheet.getContext());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DocsActivity.class);

            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("folderId", items.get(position).getFolderId());
            intent.putExtra("recordId", items.get(position).getRecordId());

            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            viewRecordMore(holder, position);
            return false;
        });
    }

    public void viewRecordMore(AdapterViewHolder holder, int position) {
        moreBottomSheet.setContentView(R.layout.dialog_more_docs);
        bottomSheetDialog.setContentView(R.layout.dialog_delete_docs);

        moreBottomSheet.show();

        TextView title = moreBottomSheet.findViewById(R.id.textview_more_docs_title);
        TextView datetime = moreBottomSheet.findViewById(R.id.textview_more_docs_description);

        title.setText(items.get(position).getTitle());
        datetime.setText(items.get(position).getDatetime());

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

        TextView confirm = bottomSheetDialog.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = bottomSheetDialog.findViewById(R.id.textview_delete_docs_cancel);

        // 버튼 클릭 리스너를 별도의 메서드로 분리
        confirm.setOnClickListener(d2 -> {
            bottomSheetDialog.dismiss();
            ApiClient client = new ApiClient(context);
            String folderId = items.get(position).getFolderId();
            String recordId = items.get(position).getRecordId();
            client.deleteRecord(folderId,recordId);


            fragment.loadFolderInside();
        });
        cancel.setOnClickListener(d2 -> {
            bottomSheetDialog.dismiss();
            moreBottomSheet.show();
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