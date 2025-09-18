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

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.move.DocsMoveActivity;

public class FolderInsideDocsAdapter extends RecyclerView.Adapter<FolderInsideDocsAdapter.AdapterViewHolder> {
    private final FolderInsideFragment fragment;
    private final Context context;
    private List<FolderInsideRecord> items;

    public FolderInsideDocsAdapter(List<FolderInsideRecord> items, FolderInsideFragment fragment) {
        this.items = items;
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    public void setItems(List<FolderInsideRecord> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        FolderInsideRecord item = items.get(position);
        holder.title.setText(item.getTitle());
        String datetime = item.getCreatedTime().replace("T", "  ");
        holder.datetime.setText(datetime);

        holder.itemView.setOnClickListener(v -> navigateToDocsActivity(item));
        holder.itemView.setOnLongClickListener(v -> {
            showRecordMoreDialog(holder, item);
            return true;
        });
    }

    private void navigateToDocsActivity(FolderInsideRecord item) {
        Intent intent = new Intent(context, DocsActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("folderId", String.valueOf(fragment.getFolderId()));
        intent.putExtra("recordId", item.getRecordId());
        context.startActivity(intent);
    }

    private void showRecordMoreDialog(AdapterViewHolder holder, FolderInsideRecord item) {
        BottomSheetDialog moreBottomSheet = new BottomSheetDialog(context);
        moreBottomSheet.setContentView(R.layout.dialog_more_docs);

        TextView title = moreBottomSheet.findViewById(R.id.textview_more_docs_title);
        TextView datetime = moreBottomSheet.findViewById(R.id.textview_more_docs_description);

        if (title != null) title.setText(item.getTitle());
        if (datetime != null) datetime.setText(item.getCreatedTime().replace("T", " "));

        RelativeLayout rename = moreBottomSheet.findViewById(R.id.relativelayout_more_rename);
        if (rename != null) rename.setOnClickListener(v -> {
            moreBottomSheet.dismiss();
            navigateToDocsNameEditActivity(item);
        });

        RelativeLayout move = moreBottomSheet.findViewById(R.id.relativelayout_more_move);
        if (move != null) move.setOnClickListener(v -> {
            moreBottomSheet.dismiss();
            navigateToDocsMoveActivity(item);
        });

        RelativeLayout delete = moreBottomSheet.findViewById(R.id.relativelayout_more_delete);
        if (delete != null) delete.setOnClickListener(v -> {
            moreBottomSheet.dismiss();
            showDeleteConfirmationDialog(item);
        });

        moreBottomSheet.show();
    }

    private void navigateToDocsNameEditActivity(FolderInsideRecord item) {
        Intent intent = new Intent(context, DocsNameEditActivity.class);
        intent.putExtra("recordId", item.getRecordId());
        intent.putExtra("folderId", String.valueOf(fragment.getFolderId()));
        intent.putExtra("title", item.getTitle());
        context.startActivity(intent);
    }

    private void navigateToDocsMoveActivity(FolderInsideRecord item) {
        Intent intent = new Intent(context, DocsMoveActivity.class);
        intent.putExtra("recordId", item.getRecordId());
        intent.putExtra("folderId", String.valueOf(fragment.getFolderId()));
        intent.putExtra("title", item.getTitle());
        context.startActivity(intent);
    }

    private void showDeleteConfirmationDialog(FolderInsideRecord item) {
        BottomSheetDialog deleteBottomSheet = new BottomSheetDialog(context);
        deleteBottomSheet.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = deleteBottomSheet.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = deleteBottomSheet.findViewById(R.id.textview_delete_docs_cancel);

        if (confirm != null) confirm.setOnClickListener(v -> {
            deleteBottomSheet.dismiss();
            fragment.getViewModel().deleteDocs(String.valueOf(fragment.getFolderId()), item.getRecordId());
        });

        if (cancel != null) cancel.setOnClickListener(v -> deleteBottomSheet.dismiss());

        deleteBottomSheet.show();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView datetime;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_document_title);
            datetime = itemView.findViewById(R.id.textview_document_datetime);
        }
    }
}