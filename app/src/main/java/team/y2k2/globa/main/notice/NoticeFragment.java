package team.y2k2.globa.main.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import team.y2k2.globa.R;

public class NoticeFragment extends Fragment {

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

        Glide.with(inflater.getContext())
                .load(imageURL) // 임시로 로드
                .into(imageView);

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
