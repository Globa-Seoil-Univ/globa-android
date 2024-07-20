package team.y2k2.globa.main.notice;


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
