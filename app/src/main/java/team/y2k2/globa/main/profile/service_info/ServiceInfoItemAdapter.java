package team.y2k2.globa.main.profile.service_info;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class ServiceInfoItemAdapter extends RecyclerView.Adapter<ServiceInfoItemAdapter.AdapterViewHolder> {
    ArrayList<ServiceInfoItem> items;

    public ServiceInfoItemAdapter(ArrayList<ServiceInfoItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ServiceInfoItemAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_info,parent,false);
        return new ServiceInfoItemAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceInfoItemAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getDescription());

        if(items.get(position).getActivity() == null)
            return;

//        holder.layout.setOnClickListener(view -> {
//            Intent intent = new Intent(holder.itemView.getContext(), items.get(position).getActivity().getClass());
//            holder.itemView.getContext().startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        ConstraintLayout layout;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_service_info_title);
            description = itemView.findViewById(R.id.textview_item_service_info_description);
//            layout = itemView.findViewById(R.id.item_profile_items);
        }
    }
}
