package team.y2k2.globa.main.profile.service_info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class ServiceInfoViewModel extends ViewModel {

    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private final ArrayList<ServiceInfoItem> serviceInfoItems = new ArrayList<>();

    public ServiceInfoViewModel() {
        loadServiceInfoItems();
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public ArrayList<ServiceInfoItem> getServiceInfoItems() {
        return serviceInfoItems;
    }

    public void onBackClick() {
        finishActivity.setValue(true);
    }

    private void loadServiceInfoItems() {
        serviceInfoItems.add(new ServiceInfoItem(R.string.profile_service_info_1_title, R.string.profile_service_info_1_description, "https://joyous-coelurus-23b.notion.site/175478173550410c87d6cc6854e944e4?pvs=4"));
        serviceInfoItems.add(new ServiceInfoItem(R.string.profile_service_info_2_title, R.string.profile_service_info_2_description, "https://joyous-coelurus-23b.notion.site/5e8ff23d34d14c128e21e30c71a2b0bc?pvs=4"));
        serviceInfoItems.add(new ServiceInfoItem(R.string.profile_service_info_3_title, R.string.profile_service_info_3_description, null));
        serviceInfoItems.add(new ServiceInfoItem(R.string.profile_service_info_4_title, R.string.profile_service_info_4_description, "https://joyous-coelurus-23b.notion.site/eb062dceb56e4750be51b25690e1bd91?pvs=4"));
    }
}
