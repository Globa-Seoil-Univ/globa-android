package team.y2k2.globa;

import androidx.test.espresso.idling.CountingIdlingResource;

/**
 * Espresso 테스트 중 비동기 작업을 동기화하기 위한 IdlingResource 클래스입니다.
 * 네트워크 요청과 같은 백그라운드 작업이 완료될 때까지 테스트 실행을 일시 중지시킵니다.
 */
public class EspressoIdlingResource {

    // 리소스 이름을 정의합니다. 디버깅 시 유용합니다.
    private static final String RESOURCE = "GLOBAL";

    // 실제 IdlingResource 인스턴스입니다.
    // CountingIdlingResource는 내부적으로 카운터를 사용하여 작업이 진행 중인지 여부를 추적합니다.
    private static final CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE);

    /**
     * 비동기 작업이 시작될 때 호출합니다.
     * 내부 카운터를 증가시켜 Espresso에게 작업이 진행 중임을 알립니다.
     */
    public static void increment() {
        countingIdlingResource.increment();
    }

    /**
     * 비동기 작업이 완료될 때 호출합니다.
     * 내부 카운터를 감소시킵니다. 카운터가 0이 되면 Espresso는 테스트를 계속 진행합니다.
     * 카운터가 이미 0이거나 음수가 되는 것을 방지하기 위해 isIdleNow() 체크를 추가할 수 있습니다.
     */
    public static void decrement() {
        // countingIdlingResource가 유휴 상태가 아닐 때만 decrement를 호출하여 음수 카운터를 방지합니다.
        if (!countingIdlingResource.isIdleNow()) {
            countingIdlingResource.decrement();
        }
    }

    /**
     * Espresso 테스트 프레임워크에 등록할 IdlingResource 인스턴스를 반환합니다.
     * @return CountingIdlingResource 인스턴스
     */
    public static CountingIdlingResource getIdlingResource() {
        return countingIdlingResource;
    }
}
