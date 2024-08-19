package team.y2k2.globa.main.docs.list;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.main.docs.keyword.DocsKeywordAdapter;
import team.y2k2.globa.main.docs.keyword.DocsKeywordModel;

public class DocsListItemAdapter extends RecyclerView.Adapter<DocsListItemAdapter.AdapterViewHolder> {
    ArrayList<DocsListItem> items;

    public DocsListItemAdapter(ArrayList<DocsListItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_document, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        String title = items.get(position).getTitle();
        String datetime = getDateFormat(items.get(position).getDatetime());

        holder.title.setText(title);
        holder.datetime.setText(datetime);

        BottomSheetDialog moreBottomSheet = new BottomSheetDialog(holder.itemView.getContext());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(moreBottomSheet.getContext());

        moreBottomSheet.setContentView(R.layout.dialog_more_docs);
        bottomSheetDialog.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = bottomSheetDialog.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = bottomSheetDialog.findViewById(R.id.textview_delete_docs_cancel);

        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DocsActivity.class);
            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("folderId", items.get(position).getFolderId());
            intent.putExtra("recordId", items.get(position).getRecordId());

            SharedPreferences preferences = holder.itemView.getContext().getSharedPreferences("record_" + items.get(position).getRecordId(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("count", preferences.getInt("count", 0) + 1);
            editor.commit();

            holder.itemView.getContext().startActivity(intent);
        });

        // 버튼 클릭 리스너를 별도의 메서드로 분리
        confirm.setOnClickListener(d2 -> {
            bottomSheetDialog.dismiss();
        });
        cancel.setOnClickListener(d2 -> {
            bottomSheetDialog.dismiss();
            moreBottomSheet.show();
        });

        holder.itemView.setOnLongClickListener(view -> {
            moreBottomSheet.show();

            RelativeLayout rename = moreBottomSheet.findViewById(R.id.relativelayout_more_rename);
            TextView moreTitle = moreBottomSheet.findViewById(R.id.textview_more_docs_title);

            moreTitle.setText(title);


            rename.setOnClickListener(d1 -> {
                moreBottomSheet.dismiss();
                Intent intent = new Intent(holder.itemView.getContext(), DocsNameEditActivity.class);
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
            return true;
        });

        // 키워드 어뎁터
        DocsKeywordModel keywordModel = new DocsKeywordModel(items.get(position).getKeywords());
        DocsKeywordAdapter adapter = new DocsKeywordAdapter(keywordModel.getItems());


        if(items.get(position).getKeywords().size() == 0) {
            // 아직 STT 트렌젝션이 완료되지 않았을 때.
            holder.user_layout_1.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            holder.user_layout_2.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            holder.user_layout_3.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        else {
            holder.processing.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            holder.lottieAnimationView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.keywordRecyclerView.setLayoutManager(layoutManager);
        holder.keywordRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView datetime;
        RecyclerView keywordRecyclerView;
        ConstraintLayout layout;
        TextView processing;
        LottieAnimationView lottieAnimationView;

        LinearLayout user_layout_1;
        LinearLayout user_layout_2;
        LinearLayout user_layout_3;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_document_main_docs_name);
            datetime = itemView.findViewById(R.id.textview_item_document_docs_time);

            layout = itemView.findViewById(R.id.constraintlayout_item_main_document);

            keywordRecyclerView = itemView.findViewById(R.id.recyclerview_document_keyword);

            processing = itemView.findViewById(R.id.textview_main_document_processing);
            lottieAnimationView = itemView.findViewById(R.id.lottie_main_document_record);
        }
    }


    public String getDateFormat(String datetime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        // 출력 형식
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.KOREA);

        Date date;
        String outputDate = "";

        try {
            date = inputFormat.parse(datetime);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
    }
}