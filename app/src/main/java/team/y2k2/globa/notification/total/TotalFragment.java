package team.y2k2.globa.notification.total;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.FragmentTotalBinding;
import team.y2k2.globa.notification.NotificationActivity;

public class TotalFragment extends Fragment {

    FragmentTotalBinding binding;
    private TotalFragmentViewModel totalFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTotalBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalFragmentViewModel = new ViewModelProvider(this).get(TotalFragmentViewModel.class);

    }
}