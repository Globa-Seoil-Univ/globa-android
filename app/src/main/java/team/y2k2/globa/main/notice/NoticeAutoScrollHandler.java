package team.y2k2.globa.main.notice;

import android.os.Handler;

import androidx.viewpager.widget.ViewPager;

public class NoticeAutoScrollHandler {
    private static final long AUTO_SCROLL_DELAY = 5000; // 5초마다 자동 스크롤

    private Handler handler;
    private Runnable runnable;



    public NoticeAutoScrollHandler(ViewPager viewPager) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // 다음 페이지로 이동하는 로직을 호출
                // ViewPager의 setCurrentItem 메서드 등을 사용하여 페이지 이동
                // 예시: viewPager.setCurrentItem(nextPosition);

                // 다음으로 넘길 페이지의 위치 계산
                int nextPosition = viewPager.getCurrentItem() + 1;
                if (nextPosition >= viewPager.getAdapter().getCount()) {
                    nextPosition = 0; // 처음 페이지로 이동
                }

                viewPager.setCurrentItem(nextPosition, true); // 자연스럽게 스크롤
                startAutoScroll(); // 다음 자동 스크롤 시작
            }
        };
    }

    public void startAutoScroll() {
        handler.postDelayed(runnable, AUTO_SCROLL_DELAY);
    }

    public void stopAutoScroll() {
        handler.removeCallbacks(runnable);
    }
}
