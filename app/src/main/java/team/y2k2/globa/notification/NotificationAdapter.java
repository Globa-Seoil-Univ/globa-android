package team.y2k2.globa.notification;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.entity.Inquiry;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.entity.Share;
import team.y2k2.globa.api.model.entity.User;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.notification.inquiry.InquiryDetailActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.AdapterViewHolder> {
    private static final int TYPE_NOTIFICATION = 1;
    private static final int TYPE_SHARE_INVITE = 2;
    private static final int TYPE_SHARED_FOLDER_ADD_RECORD = 3;
    private static final int TYPE_SHARED_FOLDER_ADD_USER = 4;
    private static final int TYPE_SHARED_FOLDER_ADD_COMMENT = 5;
    private static final int TYPE_UPLOAD_SUCCESS = 6;
    private static final int TYPE_UPLOAD_FAILED = 7;
    private static final int TYPE_INQUIRY_ANSWERED = 8;

    NotificationModel items;

    private int createViewHolderCount = 0; // 호출 횟수를 추적하는 변수

    public NotificationAdapter(NotificationModel items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NotificationAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(getClass().getName(), "onCreateViewHolder 시작" + viewType);

        Notification notification = items.getResponse().getNotifications().get(createViewHolderCount);

        int type = Integer.valueOf(notification.getType());
        Log.d(getClass().getName(),  "onCreateViewHolder | " + createViewHolderCount +" | " + type);
        createViewHolderCount++;

        View view;
        switch (type) {
            case TYPE_NOTIFICATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_inquiry, parent, false);
                return new NotificationAdapter.AdapterViewHolder(view, TYPE_NOTIFICATION);

            case TYPE_SHARE_INVITE:
                break;

            case TYPE_SHARED_FOLDER_ADD_RECORD:
                break;

            case TYPE_SHARED_FOLDER_ADD_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_share, parent, false);
                return new NotificationAdapter.AdapterViewHolder(view, TYPE_SHARED_FOLDER_ADD_USER);
            case TYPE_SHARED_FOLDER_ADD_COMMENT:
                break;

            case TYPE_UPLOAD_SUCCESS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_upload_success, parent, false);
                return new NotificationAdapter.AdapterViewHolder(view, TYPE_UPLOAD_SUCCESS);
            case TYPE_UPLOAD_FAILED:
                break;

            case TYPE_INQUIRY_ANSWERED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_inquiry, parent, false);
                return new NotificationAdapter.AdapterViewHolder(view, TYPE_INQUIRY_ANSWERED);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.AdapterViewHolder holder, int position) {
        Log.d(getClass().getName(),  "onBindViewHolder 시작 ");

        switch (holder.type) {
            case TYPE_NOTIFICATION:
                holder.inquiryTitle.setText("공지사항 알림 준비중입니다.");
                break;
            case TYPE_SHARE_INVITE:
                break;

            case TYPE_SHARED_FOLDER_ADD_RECORD:
                break;

            case TYPE_SHARED_FOLDER_ADD_USER:
                User user = items.getNotifications().get(position).getUser();
                Share share = items.getNotifications().get(position).getShare();
                Folder folder = items.getNotifications().get(position).getFolder();
                holder.shareUserTitle.setText(user.getName() + "님이" + folder.getTitle() +" 폴더 공유 초대를 보냈습니다.");

                holder.shareUserAccess.setOnClickListener(v -> {
                    ApiClient apiClient = new ApiClient(holder.itemView.getContext());

                    Response<Void> response = apiClient.requestAcceptShareInvite(folder.getFolderId(), share.getShareId());

                    Log.d("초대 수락", "초대 수락 성공 : " + response.code());
                    holder.shareUserAccess.setVisibility(View.INVISIBLE);
                    holder.shareUserDenied.setVisibility(View.INVISIBLE);
                    Toast.makeText(holder.itemView.getContext(), "수락했습니다.", Toast.LENGTH_LONG);
                });
                break;

            case TYPE_SHARED_FOLDER_ADD_COMMENT:
                break;

            case TYPE_UPLOAD_SUCCESS:
                try {
                    Record record = items.getNotifications().get(position).getRecord();
                    Log.d(getClass().getName(),  record.getTitle());

                    holder.uploadSuccessTitle.setText(record.getTitle() + " 문서가 업로드 되었습니다.");
                    items.getNotifications().get(position);
                } catch (NullPointerException e) {
                    holder.uploadSuccessTitle.setText("오류 발생 : " + e.getMessage());
                }
                break;

            case TYPE_UPLOAD_FAILED:
//                holder.inquiryTitle.setText("이 외 형식입니다.");
                break;

            case TYPE_INQUIRY_ANSWERED:
                Inquiry inquiry = items.getNotifications().get(position).getInquiry();
                holder.inquiryTitle.setText(inquiry.getTitle());
                holder.inquiryTitle.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), InquiryDetailActivity.class);
                    intent.putExtra("inquiryId",inquiry.getInquiryId());
                    holder.itemView.getContext().startActivity(intent);

                });
//                holder.description.setText(items.get(position).getContent());
                break;

            default:
                if(holder.inquiryTitle == null) {
                    holder.uploadSuccessTitle.setText("이 외 형식입니다.");
                }
                else {
                    holder.inquiryTitle.setText("이 외 형식입니다.");
                }
                break;

        }



//        holder.itemView.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.getNotifications().size() : 0);
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView inquiryTitle;
        TextView uploadSuccessTitle;

        TextView shareUserTitle;

        Button shareUserAccess;
        Button shareUserDenied;

        int type;


//        TextView description;

        public AdapterViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            Log.d(getClass().getName(), "AdapterViewHolder 시작" + viewType);

            type = viewType;

            switch (viewType) {
                case TYPE_NOTIFICATION:
                    inquiryTitle = itemView.findViewById(R.id.textview_item_notification_inquiry_title);
                    break;

                case TYPE_SHARE_INVITE:
                    break;

                case TYPE_SHARED_FOLDER_ADD_RECORD:
                    break;

                case TYPE_SHARED_FOLDER_ADD_USER:
                    shareUserTitle = itemView.findViewById(R.id.textview_item_notification_share_title);
                    shareUserAccess = itemView.findViewById(R.id.button_item_notification_share_access);
                    shareUserDenied = itemView.findViewById(R.id.button_item_notification_share_denied);

                    break;

                case TYPE_SHARED_FOLDER_ADD_COMMENT:
                    break;

                case TYPE_UPLOAD_SUCCESS:
                    uploadSuccessTitle = itemView.findViewById(R.id.textview_item_notification_upload_success_title);
                    break;
                case TYPE_UPLOAD_FAILED:
                    break;

                case TYPE_INQUIRY_ANSWERED:
                    inquiryTitle = itemView.findViewById(R.id.textview_item_notification_inquiry_title);
                    break;

//            description = itemView.findViewById(R.id.textview_item_notification_inquiry_description);
                default:
                    inquiryTitle = itemView.findViewById(R.id.textview_item_notification_inquiry_title);
                    break;
            }
        }
    }
}
