package team.y2k2.globa.folder.currently;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.folder.inside.FolderInsideFragment;

public class FolderCurrentlyAdapter extends RecyclerView.Adapter<FolderCurrentlyAdapter.AdapterViewHolder> {
    ArrayList<FolderCurrentlyItem> items;

    public FolderCurrentlyAdapter(ArrayList<FolderCurrentlyItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FolderCurrentlyAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_currently, parent, false);
        return new FolderCurrentlyAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderCurrentlyAdapter.AdapterViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.datetime.setText(items.get(position).getDatetime());

        holder.layout.setOnClickListener(v -> {
            ((FragmentActivity) holder.layout.getContext()).getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fcv_main, FolderInsideFragment.class, null)
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

            title = itemView.findViewById(R.id.textview_folder_item_currently_title);
            datetime = itemView.findViewById(R.id.textview_folder_item_currently_datetime);
            layout = itemView.findViewById(R.id.constraintlayout_folder_item_currently);

        }
    }
}
