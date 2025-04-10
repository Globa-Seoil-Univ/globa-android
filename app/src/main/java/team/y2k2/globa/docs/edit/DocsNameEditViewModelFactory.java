package team.y2k2.globa.docs.edit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.api.clients.RecordApiClient;

public class DocsNameEditViewModelFactory implements ViewModelProvider.Factory {

    private final String initialTitle;
    private final String recordId;
    private final String folderId;
    private final RecordApiClient recordApiClient;

    public DocsNameEditViewModelFactory(String initialTitle, String recordId, String folderId, RecordApiClient recordApiClient) {
        this.initialTitle = initialTitle;
        this.recordId = recordId;
        this.folderId = folderId;
        this.recordApiClient = recordApiClient;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked") // Suppress warning for cast
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DocsNameEditViewModel.class)) {
            return (T) new DocsNameEditViewModel(initialTitle, recordId, folderId, recordApiClient);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}