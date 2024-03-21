package team.y2k2.globa.profile;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.AdapterViewHolder> {
    ArrayList<SettingItem> items;

    public SettingItemAdapter(ArrayList<SettingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.icon.setImageResource(items.get(position).getIcon());
        holder.title.setText(items.get(position).getName());

        if(items.get(position).getActivity() == null)
            return;

        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), items.get(position).getActivity().getClass());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        ConstraintLayout layout;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.imageview_item_setting_icon);
            title = itemView.findViewById(R.id.textview_item_setting_title);
            layout = itemView.findViewById(R.id.item_profile_items);
        }
    }
}
