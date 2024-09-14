package team.y2k2.globa.main.profile.info;

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

public class MyinfoAdapter extends RecyclerView.Adapter<MyinfoAdapter.MyViewHolder>{

    private List<MyinfoItem> itemList;
    private Context context;
    private ActivityResultLauncher<Intent> nicknameEditLauncher;
    private MyinfoActivity activity;

    public MyinfoAdapter(List<MyinfoItem> itemList, ActivityResultLauncher<Intent> nicknameEditLauncher, MyinfoActivity activity) {
        this.itemList = itemList;
        this.nicknameEditLauncher = nicknameEditLauncher;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_myinfo, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyinfoItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getName());
        holder.image.setImageResource(item.getImage());

        holder.layout.setOnClickListener(v -> {
            if(item.getActivity() != null) {

                if(item.getTitle().toString().equals("이름")) {
                    Intent intent = new Intent(context, item.getActivity().getClass());
                    intent.putExtra("current_name", item.getName().toString());
                    intent.putExtra("userId", activity.getUserId());
                    nicknameEditLauncher.launch(intent);
                } else if(item.getTitle().toString().equals("회원탈퇴")) {
                    Intent intent = new Intent(context, item.getActivity().getClass());
                    holder.itemView.getContext().startActivity(intent);
                }

            } else {

                if(item.getTitle().toString().equals("계정 코드")) {
                    // 계정코드 클립보드 복사
                    copyToClipboard(context, item.getName().toString());
                    Toast.makeText(context, "코드 복사완료!", Toast.LENGTH_SHORT).show();
                    Log.d(getClass().getName(), "클립보드 복사 완료");
                } else if(item.getTitle().toString().equals("로그아웃")) {
                    // 로그아웃 로직
                    Log.d(getClass().getName(), "프리퍼런스 리셋 시작");
                    SharedPreferences preferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Log.d(getClass().getName(), "프리퍼런스 리셋 완료 : " + preferences.getString("accessToken", ""));
                    Intent logoutIntent = new Intent(context, IntroActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(logoutIntent);
                }

            }

        });



    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView name;
        ImageView image;

        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_myinfoItemlist_title);
            name = itemView.findViewById(R.id.textview_myinfoItemlist_name);
            image = itemView.findViewById(R.id.imageview_myinfoItemlist_next);
            layout = itemView.findViewById(R.id.constraintlayout_myinfoItemList);
        }

    }

    // 클립보드 복사 메소드
    public void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard != null) {
            ClipData clip = ClipData.newPlainText("code", text);
            clipboard.setPrimaryClip(clip);
        }
    }


}
