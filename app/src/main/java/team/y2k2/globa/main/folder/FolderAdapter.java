package team.y2k2.globa.main.folder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import team.y2k2.globa.R;
import team.y2k2.globa.main.folder.inside.FolderInsideFragment;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.AdapterViewHolder> {
    ArrayList<FolderItem> items;

    public FolderAdapter(ArrayList<FolderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        String title = items.get(position).getTitle();
        String datetime = getDateFormat(items.get(position).getDatetime());

        holder.title.setText(title);
        holder.datetime.setText(datetime);

        holder.layout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("folder_id", items.get(position).getFolderId());
            bundle.putString("folder_title", items.get(position).getTitle());
            FolderInsideFragment fragment = new FolderInsideFragment();
            fragment.setArguments(bundle);

            ((FragmentActivity) holder.layout.getContext()).getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fcv_main, fragment, null)
                    .addToBackStack(null)
                    .commit();
        });


    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView datetime;

        ConstraintLayout layout;


        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_folder_item_title);
            datetime = itemView.findViewById(R.id.textview_folder_item_datetime);
            layout = itemView.findViewById(R.id.constraintlayout_folder_item);
        }
    }

    public String getDateFormat(String datetime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        // 출력 형식
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.KOREA);

        Date date;
        String outputDate = "";

        try {
            // 입력 날짜 문자열을 Date 객체로 파싱
            date = inputFormat.parse(datetime);
            // Date 객체를 원하는 출력 형식의 문자열로 변환
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
    }
}
