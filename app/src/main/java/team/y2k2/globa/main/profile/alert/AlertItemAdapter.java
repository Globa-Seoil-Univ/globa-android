package team.y2k2.globa.main.profile.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class AlertItemAdapter extends RecyclerView.Adapter<AlertItemAdapter.AdapterViewHolder> {
    ArrayList<AlertItem> items;
    AlertActivity activity;

    private boolean newUploadNofi, newShareNofi, newEventNofi;

    public AlertItemAdapter(ArrayList<AlertItem> items, AlertActivity activity) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {

        AlertItem item = items.get(position);

        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getDescription());
        holder.toggle.setChecked(item.isChecked());

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switch (position) {
                case 0:
                    activity.setNewUploadNofi(isChecked);
                    if(isChecked) FirebaseMessaging.getInstance().subscribeToTopic("notification");
                    else FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
                    break;
                case 1:
                    activity.setNewShareNofi(isChecked);
                    if(isChecked) FirebaseMessaging.getInstance().subscribeToTopic("notification");
                    else FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
                    break;
                case 2:
                    activity.setNewEventNofi(isChecked);
                    if(isChecked) FirebaseMessaging.getInstance().subscribeToTopic("event");
                    else FirebaseMessaging.getInstance().unsubscribeFromTopic("event");
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        Switch toggle;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_item_alert_title);
            description = itemView.findViewById(R.id.textview_item_alert_description);
            toggle = itemView.findViewById(R.id.switch_alert);

        }
    }
}