package team.y2k2.globa.main.profile.info;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.y2k2.globa.R;

public class MyinfoAdapter extends RecyclerView.Adapter<MyinfoAdapter.MyViewHolder>{

    private List<MyinfoItem> itemList;
    private Context context;

    public MyinfoAdapter(List<MyinfoItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyinfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.myinfo_itemlist, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyinfoAdapter.MyViewHolder holder, int position) {
        MyinfoItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getName());
        holder.image.setImageResource(item.getImage());

        /*
        if(item.getActivity() == null) {
            // 임시 | null 값은 로그아웃으로 처리합니다.
            return;
        }
         */

        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), item.getActivity().getClass());
            if(item.getActivity() != null) {
                if(item.getTitle().toString().equals("이름")) {
                    // 임시 코드
                    intent.putExtra("name", item.getName().toString());
                }
                holder.itemView.getContext().startActivity(intent);
            } else {
                if(item.getTitle().toString().equals("계정코드")) {
                    copyToClipboard(holder.itemView.getContext(), item.getName().toString());
                    Toast.makeText(holder.itemView.getContext(), "복사완료!", Toast.LENGTH_SHORT).show();
                } else if(item.getTitle().toString().equals("로그아웃")) {
                    // 로그아웃 로직
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
