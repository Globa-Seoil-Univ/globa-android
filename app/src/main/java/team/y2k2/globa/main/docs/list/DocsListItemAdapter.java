package team.y2k2.globa.main.docs.list;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.main.docs.keyword.DocsKeywordAdapter;
import team.y2k2.globa.main.docs.keyword.DocsKeywordModel;
import team.y2k2.globa.sql.RecordDB;

public class DocsListItemAdapter extends RecyclerView.Adapter<DocsListItemAdapter.AdapterViewHolder> {
    private final ArrayList<DocsListItem> items;
    private final Activity activity;

    public DocsListItemAdapter(ArrayList<DocsListItem> items, Activity activity) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (items.isEmpty() || items.get(0).getRecordId() == null || items.get(0).getRecordId().isEmpty()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_document_null, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_document, parent, false);
        }
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        if (items.isEmpty() || items.get(0).getRecordId() == null || items.get(0).getRecordId().isEmpty()) {
            return;
        }

        DocsListItem currentItem = items.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.datetime.setText(getDateFormat(currentItem.getDatetime()));

        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DocsActivity.class);
            intent.putExtra("title", currentItem.getTitle());
            intent.putExtra("folderId", currentItem.getFolderId());
            intent.putExtra("recordId", currentItem.getRecordId());

            SharedPreferences preferences = holder.itemView.getContext().getSharedPreferences("record_" + currentItem.getRecordId(), Activity.MODE_PRIVATE);
            preferences.edit().putInt("count", preferences.getInt("count", 0) + 1).apply();

            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            showMoreDialog(holder, currentItem, position);
            return true;
        });

        if (currentItem.getKeywords() != null) {
            DocsKeywordModel keywordModel = new DocsKeywordModel(currentItem.getKeywords());
            DocsKeywordAdapter adapter = new DocsKeywordAdapter(keywordModel.getItems());

            if (!currentItem.getKeywords().isEmpty()) {
                holder.processing.setVisibility(View.GONE);
                holder.lottieAnimationView.setVisibility(View.GONE);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            holder.keywordRecyclerView.setLayoutManager(layoutManager);
            holder.keywordRecyclerView.setAdapter(adapter);
        }
    }

    private void showMoreDialog(AdapterViewHolder holder, DocsListItem item, int position) {
        BottomSheetDialog moreBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
        moreBottomSheet.setContentView(R.layout.dialog_more_docs);

        TextView moreTitle = moreBottomSheet.findViewById(R.id.textview_more_docs_title);
        TextView moreDatetime = moreBottomSheet.findViewById(R.id.textview_more_docs_description);
        RelativeLayout rename = moreBottomSheet.findViewById(R.id.relativelayout_more_rename);
        RelativeLayout delete = moreBottomSheet.findViewById(R.id.relativelayout_more_delete);

        if (moreTitle != null) moreTitle.setText(item.getTitle());
        if (moreDatetime != null) moreDatetime.setText(getDateFormat(item.getDatetime()));

        if (rename != null) {
            rename.setOnClickListener(v -> {
                moreBottomSheet.dismiss();
                Intent intent = new Intent(holder.itemView.getContext(), DocsNameEditActivity.class);
                intent.putExtra("recordId", item.getRecordId());
                intent.putExtra("folderId", item.getFolderId());
                intent.putExtra("title", item.getTitle());
                holder.itemView.getContext().startActivity(intent);
            });
        }

        if (delete != null) {
            delete.setOnClickListener(v -> {
                moreBottomSheet.dismiss();
                showDeleteConfirmationDialog(item, position);
            });
        }

        moreBottomSheet.show();
    }

    private void showDeleteConfirmationDialog(DocsListItem item, int position) {
        BottomSheetDialog deleteBottomSheet = new BottomSheetDialog(activity);
        deleteBottomSheet.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = deleteBottomSheet.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = deleteBottomSheet.findViewById(R.id.textview_delete_docs_cancel);

        if (confirm != null) {
            confirm.setOnClickListener(v -> {
                deleteBottomSheet.dismiss();
                deleteDocs(item.getFolderId(), item.getRecordId(), position);
            });
        }

        if (cancel != null) {
            cancel.setOnClickListener(v -> deleteBottomSheet.dismiss());
        }

        deleteBottomSheet.show();
    }

    public void deleteDocs(String folderId, String recordId, int position) {
        RecordApiClient apiClient = new RecordApiClient();
        Response<Void> response = apiClient.deleteRecord(folderId, recordId);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "문서 삭제 성공 (API): " + response.code());

            RecordDB recordDB = new RecordDB(activity);
            recordDB.deleteRecordById(recordId);
            Log.d(getClass().getName(), "문서 삭제 성공 (Local DB)");

            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());

            Toast.makeText(activity, "문서가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(getClass().getName(), "문서 삭제 실패: " + response.code() + ", " + response.message());
            Toast.makeText(activity, "문서 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public String getDateFormat(String datetime) {
        if (datetime == null) return "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.KOREA);

        try {
            Date date = inputFormat.parse(datetime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return datetime; // 파싱 실패 시 원본 반환
        }
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView datetime;
        final RecyclerView keywordRecyclerView;
        final ConstraintLayout layout;
        final TextView processing;
        final LottieAnimationView lottieAnimationView;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_document_main_docs_name);
            datetime = itemView.findViewById(R.id.textview_item_document_docs_time);
            layout = itemView.findViewById(R.id.constraintlayout_item_main_document);
            processing = itemView.findViewById(R.id.textview_main_document_processing);
            keywordRecyclerView = itemView.findViewById(R.id.recyclerview_document_keyword);
            lottieAnimationView = itemView.findViewById(R.id.lottie_main_document_record);
        }
    }
}