package team.y2k2.globa.main.notice;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NoticeFragmentAdapter extends FragmentPagerAdapter {

    private String[] images;

    public NoticeFragmentAdapter(FragmentManager fm, String[] images) {
        super(fm);
        this.images = images;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return NoticeFragment.newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}