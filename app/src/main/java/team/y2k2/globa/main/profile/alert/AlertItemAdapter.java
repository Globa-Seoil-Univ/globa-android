package team.y2k2.globa.main.profile.alert;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.List;
import team.y2k2.globa.R;

public class AlertItemAdapter extends RecyclerView.Adapter<AlertItemAdapter.AlertViewHolder> {

    public interface OnToggleStateChangedListener {
        void onToggleChanged();
    }

    private List<AlertItem> items;
    private final OnToggleStateChangedListener listener;

    public AlertItemAdapter(List<AlertItem> items, OnToggleStateChangedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<AlertItem> newItems) {
        if (newItems != null) {
            this.items = newItems;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertItem item = items.get(position);
        holder.bind(item, listener, position);
    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    private void updateFirebaseMessaging(int position, boolean isChecked) {
        String topic;
        switch (position) {
            case 0:
                topic = "primary";
                break;
            case 1:
                topic = "upload";
                break;
            case 2:
                topic = "share";
                break;
            case 3:
                topic = "event";
                break;
            default:
                return;
        }

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        if (isChecked) {
            fm.subscribeToTopic(topic).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d("FCM", topic + " 구독 성공");
                } else {
                    Log.e("FCM", topic + " 구독 실패", task.getException());
                }
            });
        } else {
            fm.unsubscribeFromTopic(topic).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d("FCM", topic + " 구독 취소");
                } else {
                    Log.e("FCM", topic + " 구독 취소 실패", task.getException());
                }
            });
        }
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final SwitchCompat toggle;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_item_alert_title);
            description = itemView.findViewById(R.id.textview_item_alert_description);
            toggle = itemView.findViewById(R.id.switch_alert);
        }

        public void bind(AlertItem item, OnToggleStateChangedListener listener, int position) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());

            toggle.setOnCheckedChangeListener(null);
            toggle.setChecked(item.isChecked());

            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                if (listener != null) {
                    listener.onToggleChanged();
                }
                // getBindingAdapter()를 통해 Adapter 인스턴스에 안전하게 접근
                if(getBindingAdapter() instanceof AlertItemAdapter) {
                    ((AlertItemAdapter) getBindingAdapter()).updateFirebaseMessaging(position, isChecked);
                }
            });
        }
    }
}