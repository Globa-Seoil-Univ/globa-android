package team.y2k2.globa.main.profile.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class AlertItemAdapter extends RecyclerView.Adapter<AlertItemAdapter.AdapterViewHolder> {
    private final ArrayList<AlertItem> items;
    private final AlertActivity activity;

    public AlertItemAdapter(ArrayList<AlertItem> items, AlertActivity activity) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        AlertItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.toggle.setChecked(item.isChecked());

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            updateFirebaseMessaging(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    private void updateFirebaseMessaging(int position, boolean isChecked) {
        String topic = "notification";
        if (position == 2) {
            topic = "event";
        }

        if (isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        }
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final SwitchCompat toggle;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_alert_title);
            description = itemView.findViewById(R.id.textview_item_alert_description);
            toggle = itemView.findViewById(R.id.switch_alert);
        }
    }
}