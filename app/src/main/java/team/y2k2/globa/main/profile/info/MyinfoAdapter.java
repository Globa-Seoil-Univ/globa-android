package team.y2k2.globa.main.profile.info;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.intro.IntroActivity;

public class MyinfoAdapter extends RecyclerView.Adapter<MyinfoAdapter.MyViewHolder> {
    private final List<MyInfoItem> itemList;
    private final ActivityResultLauncher<Intent> nicknameEditLauncher;
    private final MyInfoActivity activity;

    private Context context; // onCreateViewHolder에서 초기화

    public MyinfoAdapter(List<MyInfoItem> itemList, ActivityResultLauncher<Intent> nicknameEditLauncher, MyInfoActivity activity) {
        this.itemList = itemList;
        this.nicknameEditLauncher = nicknameEditLauncher;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyInfoItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getName());
        holder.image.setImageResource(item.getImage());

        holder.layout.setOnClickListener(v -> {
            // ViewModel에서 title을 context.getString(R.string.xxx)으로 설정했으므로,
            // item.getTitle()은 이미 현재 로케일에 맞는 문자열입니다.
            // 따라서 비교할 때도 동일하게 getString()을 사용해야 합니다.
            String titleNameFromResource = context.getString(R.string.name); // "이름"에 해당하는 리소스
            String titleUserCodeFromResource = context.getString(R.string.profile_account_code);
            String titleSignOutFromResource = context.getString(R.string.sign_out);
            String titleWithdrawFromResource = context.getString(R.string.withdraw);

            if (item.getActivity() != null) {
                // *** 중요 수정: 문자열 리소스를 사용하여 비교 ***
                if (item.getTitle().equals(titleNameFromResource)) { // "이름" 항목일 경우
                    Intent intent = new Intent(context, item.getActivity().getClass());
                    intent.putExtra("current_name", item.getName());
                    intent.putExtra("userId", activity.getUserId());
                    if (nicknameEditLauncher != null) {
                        nicknameEditLauncher.launch(intent);
                    } else {
                        Log.e("MyinfoAdapter", "nicknameEditLauncher is null");
                    }
                } else if (item.getTitle().equals(titleWithdrawFromResource)) { // "회원탈퇴" 항목일 경우
                    Intent intent = new Intent(context, item.getActivity().getClass());
                    activity.startActivity(intent);
                }
            } else {
                if (item.getTitle().equals(titleUserCodeFromResource)) {
                    copyToClipboard(context, item.getName());
                    Toast.makeText(context, "코드 복사 완료", Toast.LENGTH_SHORT).show(); // 문자열 리소스 사용 권장
                    Log.d(getClass().getSimpleName(), "클립보드 복사 완료: " + item.getName());
                } else if (item.getTitle().equals(titleSignOutFromResource)) {
                    Log.d(getClass().getSimpleName(), "프리퍼런스 리셋 시작 (로그아웃)");
                    SharedPreferences preferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Log.d(getClass().getSimpleName(), "프리퍼런스 리셋 완료");

                    Intent logoutIntent = new Intent(context, IntroActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(logoutIntent);
                    if (activity != null) {
                        activity.finish();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("userCode", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    // LiveData로부터 새 아이템 리스트를 받아 어댑터 내부 리스트를 갱신하고 UI에 알리는 메소드
    public void updateItems(List<MyInfoItem> newItems) {
        this.itemList.clear();
        this.itemList.addAll(newItems);
        notifyDataSetChanged(); // 또는 DiffUtil을 사용하여 더 효율적인 업데이트 가능
        Log.d("MyinfoAdapter", "아이템 리스트 업데이트 및 notifyDataSetChanged 호출됨. 새 아이템 개수: " + newItems.size());
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView name;
        private final ImageView image;
        private final ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_my_info_item_list_title);
            name = itemView.findViewById(R.id.textview_my_info_item_list_name);
            image = itemView.findViewById(R.id.imageview_my_info_item_list_next);
            layout = itemView.findViewById(R.id.constraintlayout_my_info_item_list);
        }
    }
}