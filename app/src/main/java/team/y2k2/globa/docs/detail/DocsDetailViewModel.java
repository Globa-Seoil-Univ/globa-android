package team.y2k2.globa.docs.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DocsDetailViewModel extends ViewModel {

    private MutableLiveData<Boolean> commentLiveData = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getCommentLiveData() {
        return commentLiveData;
    }

    public void setCommentLiveData(boolean isReceived) {
        commentLiveData.setValue(isReceived);
    }
}
