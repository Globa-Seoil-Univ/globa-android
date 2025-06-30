package team.y2k2.globa.main.profile.withdraw; // 본인의 패키지 경로로 수정

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import team.y2k2.globa.api.clients.UserApiClient;

public class WithdrawViewModelFactory implements ViewModelProvider.Factory {

    private final UserApiClient userApiClient;
    private final FirebaseMessaging firebaseMessaging;

    public WithdrawViewModelFactory() {
        this.userApiClient = new UserApiClient();
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    public WithdrawViewModelFactory(UserApiClient userApiClient, FirebaseMessaging firebaseMessaging) {
        this.userApiClient = userApiClient;
        this.firebaseMessaging = firebaseMessaging;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WithdrawViewModel.class)) {
            return (T) new WithdrawViewModel(userApiClient, firebaseMessaging);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}