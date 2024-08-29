package team.y2k2.globa.main.folder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.main.folder.inside.FolderInsideFragment;
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.AdapterViewHolder> {
    ArrayList<FolderItem> items;
    Activity activity;
    public FolderAdapter(ArrayList<FolderItem> items, Activity activity) {
        this.items = items;
        this.activity = activity;
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
            bundle.putInt("folderId", items.get(position).getFolderId());
            bundle.putString("folderTitle", items.get(position).getTitle());
            FolderInsideFragment fragment = new FolderInsideFragment();
            fragment.setArguments(bundle);

            ((FragmentActivity) holder.layout.getContext()).getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fcv_main, fragment, null)
                    .addToBackStack(null)
                    .commit();
        });

        holder.layout.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("폴더 삭제");
            builder.setMessage("폴더를 삭제하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteFolder(position);
                    Toast.makeText(activity, "폴더를 삭제했습니다", Toast.LENGTH_LONG);
                }
            });

            return true;
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

    public void deleteFolder(int position) {
        ApiClient client = new ApiClient(activity);
        client.requestDeleteFolder(items.get(position).getFolderId());
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
