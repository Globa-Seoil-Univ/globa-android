package team.y2k2.globa.main.notice;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import team.y2k2.globa.R;

public class NoticeFragment extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // FirebaseStorage 인스턴스 생성

    // 'images/image.jpg' 파일에 대한 참조 생성
    StorageReference storageRef;
    private String imageURL;
    private boolean isDownloadFailed = false; // 다운로드 실패 여부를 추적하는 플래그


    public NoticeFragment() {
        // Required empty public constructor
    }

    public static NoticeFragment newInstance(String imageURL) {
        NoticeFragment fragment = new NoticeFragment();
        Bundle args = new Bundle();
        args.putString("imageURL", imageURL);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        ImageView imageView = view.findViewById(R.id.imageview_notice_image);

        if (isDownloadFailed) {
            Log.w("NOTICE", "이미 다운로드에 실패하여 재시도하지 않음.");
            return view;
        }

        storageRef = storage.getReference().child(imageURL);

        // 다운로드 URL 가져오기
        storageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // 성공적으로 다운로드 URL을 가져왔을 때 처리
                        String downloadedImageUrl = uri.toString();
                        Glide.with(inflater.getContext())
                                .load(downloadedImageUrl) // 임시로 로드
                                .into(imageView);

                        Log.e("NOTICE_SUCCESS", imageURL + ":" + downloadedImageUrl);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 다운로드 URL을 가져오는 데 실패했을 때 처리
                        Log.e("NOTICE_ERROR", "다운로드 URL 가져오기 실패", exception);

                        (getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("에러 발생")
                                        .setMessage("Firebase RDB 에러 : " + exception.getMessage())
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        });


                        isDownloadFailed = true; // 실패 플래그 설정
                    }
                });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageURL = getArguments().getString("imageURL");
        }
    }
}
