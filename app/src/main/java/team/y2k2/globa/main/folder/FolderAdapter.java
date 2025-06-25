package team.y2k2.globa.main.folder;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.main.folder.inside.FolderInsideFragment;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.AdapterViewHolder> {
    private final ArrayList<FolderModel.FolderItem> items;
    private final Activity activity;

    // AlertDialog 및 Toast 메시지 텍스트 상수화 (테스트 코드와 일치시키기 위해)
    private static final String ALERT_DIALOG_TITLE_FOLDER_DELETE = "폴더 삭제";
    private static final String ALERT_DIALOG_BUTTON_YES = "예";
    private static final String ALERT_DIALOG_BUTTON_NO = "아니오";
    private static final String TOAST_TEXT_FOLDER_DELETED = "폴더를 삭제했습니다";

    public FolderAdapter(ArrayList<FolderModel.FolderItem> items, Activity activity) {
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
        FolderModel.FolderItem currentItem = items.get(position); // 명확성을 위해 변수 사용
        String title = currentItem.getTitle();
        String datetime = getDateFormat(currentItem.getDatetime());

        holder.title.setText(title);
        holder.datetime.setText(datetime);

        holder.layout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            // getAdapterPosition()을 사용하여 클릭 시점의 정확한 위치 사용 권장 (데이터 변경에 안전)
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                FolderModel.FolderItem clickedItem = items.get(clickedPosition);
                bundle.putInt("folderId", clickedItem.getFolderId());
                bundle.putString("folderTitle", clickedItem.getTitle());
                bundle.putString("folderDatetime", clickedItem.getDatetime());
                FolderInsideFragment fragment = new FolderInsideFragment();
                fragment.setArguments(bundle);

                ((FragmentActivity) holder.layout.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view_main, fragment, null) // MainActivity의 FragmentContainerView ID 사용
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.layout.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(ALERT_DIALOG_TITLE_FOLDER_DELETE);
            builder.setMessage("폴더를 삭제하시겠습니까?"); // 이 메시지도 필요시 상수로 관리 가능

            builder.setPositiveButton(ALERT_DIALOG_BUTTON_YES, (dialog, which) -> {
                int currentPosition = holder.getAdapterPosition(); // 삭제 시점의 정확한 위치
                if (currentPosition != RecyclerView.NO_POSITION) {
                    FolderModel.FolderItem itemToDelete = items.get(currentPosition);
                    int folderIdToDelete = itemToDelete.getFolderId();

                    // 1. 로컬 리스트에서 아이템 제거 (UI 즉시 업데이트를 위해)
                    items.remove(currentPosition);
                    // 2. 어댑터에 아이템 제거 알림
                    notifyItemRemoved(currentPosition);
                    // 3. 제거된 아이템 이후의 아이템들의 위치가 변경되었음을 알림 (애니메이션 및 안정성)
                    //    items.size()는 이미 1 감소된 상태임
                    if (currentPosition < items.size()) { // 삭제 후에도 해당 위치부터 아이템이 남아 있다면
                        notifyItemRangeChanged(currentPosition, items.size() - currentPosition);
                    }
                    // 만약 마지막 아이템이었고 currentPosition == items.size() 였다면 (삭제 후)
                    // notifyItemRangeChanged는 호출할 필요 없음 (또는 itemCount 0으로 호출).
                    // notifyItemRemoved가 대부분의 경우를 처리함.

                    // 4. 서버에 삭제 요청 API 호출
                    FolderApiClient client = new FolderApiClient();
                    client.requestDeleteFolder(folderIdToDelete);
                    Log.d("FolderAdapter", "요청됨: 폴더 ID " + folderIdToDelete + " 삭제 (UI에서는 즉시 제거됨).");
                }
                Toast.makeText(activity, TOAST_TEXT_FOLDER_DELETED, Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton(ALERT_DIALOG_BUTTON_NO, null);
            builder.show();
            return true; // 롱클릭 이벤트 소비됨
        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    // 이 메서드는 이제 onBindViewHolder에서 직접 API를 호출하므로, 필요 없을 수 있습니다.
    // 만약 다른 곳에서 사용된다면 folderId를 받도록 수정하거나, 현재 로직을 유지합니다.
    // public void deleteFolder(int position) {
    //     FolderApiClient client = new FolderApiClient();
    //     client.requestDeleteFolder(items.get(position).getFolderId());
    // }

    public String getDateFormat(String datetime) {
        // 기존 getDateFormat 로직 유지
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.KOREA);
        Date date;
        String outputDate;
        try {
            date = inputFormat.parse(datetime);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("FolderAdapter", "날짜 형식 변환 실패: " + datetime, e);
            return datetime; // 파싱 실패 시 원본 반환 또는 기본값 설정
        }
        return outputDate;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView datetime;
        private final ConstraintLayout layout;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_folder_item_title);
            datetime = itemView.findViewById(R.id.textview_folder_item_datetime);
            layout = itemView.findViewById(R.id.constraintlayout_folder_item);
        }
    }
}