package team.y2k2.globa.main.docs.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.main.docs.keyword.DocsKeywordAdapter;
import team.y2k2.globa.main.docs.keyword.DocsKeywordModel;

public class DocsListItemAdapter extends RecyclerView.Adapter<DocsListItemAdapter.AdapterViewHolder> {
    ArrayList<DocsListItem> items;


    public DocsListItemAdapter(ArrayList<DocsListItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DocsListItemAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_document, parent, false);
        return new DocsListItemAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocsListItemAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());

        holder.user_1.setImageResource(items.get(position).getImage_1());
        holder.user_2.setImageResource(items.get(position).getImage_2());
        holder.user_3.setImageResource(items.get(position).getImage_3());

        BottomSheetDialog moreBottomSheet = new BottomSheetDialog(holder.more.getContext());
        moreBottomSheet.setContentView(R.layout.dialog_more_docs);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(moreBottomSheet.getContext());
        bottomSheetDialog.setContentView(R.layout.dialog_delete_docs);

        TextView confirm = bottomSheetDialog.findViewById(R.id.textview_delete_docs_confirm);
        TextView cancel = bottomSheetDialog.findViewById(R.id.textview_delete_docs_cancel);


        holder.layout.setOnClickListener(v -> {
            // 여기에 문서 상세로 이동
            Intent intent = new Intent(holder.itemView.getContext(), DocsActivity.class);
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

        holder.more.setOnClickListener(view -> {
            moreBottomSheet.show();

//            RelativeLayout rename = moreBottomSheet.findViewById(R.id.relativelayout_more_rename);
//            RelativeLayout move = moreBottomSheet.findViewById(R.id.relativelayout_more_move);
            RelativeLayout delete = moreBottomSheet.findViewById(R.id.relativelayout_more_delete);
            delete.setOnClickListener(d1 -> {
                moreBottomSheet.dismiss();
                bottomSheetDialog.show();
            });
        });

        // 키워드 어뎁터
        DocsKeywordModel keywordModel = new DocsKeywordModel(items.get(position).getKeywords());
        DocsKeywordAdapter adapter = new DocsKeywordAdapter(keywordModel.getItems());

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

        ImageView more;

        ImageView user_1;
        ImageView user_2;
        ImageView user_3;

//        ConstraintLayout layout;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_document_main_docs_name);
            datetime = itemView.findViewById(R.id.textview_item_document_docs_time);

            layout = itemView.findViewById(R.id.constraintlayout_item_main_document);

            keywordRecyclerView = itemView.findViewById(R.id.recyclerview_document_keyword);
            more = itemView.findViewById(R.id.imageview_document_more);

            user_1 = itemView.findViewById(R.id.imageview_document_user_1);
            user_2 = itemView.findViewById(R.id.imageview_document_user_2);
            user_3 = itemView.findViewById(R.id.imageview_document_user_3);
        }
    }
}