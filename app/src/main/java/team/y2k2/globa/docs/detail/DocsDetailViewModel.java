package team.y2k2.globa.docs.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DocsDetailViewModel extends ViewModel {

    private MutableLiveData<Boolean> commentLiveData = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isFirstCommentLiveData = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isAllDeletedLiveData = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getCommentLiveData() {
        return commentLiveData;
    }
    public MutableLiveData<Boolean> getIsFirstCommentLiveData() {
        return isFirstCommentLiveData;
    }
    public MutableLiveData<Boolean> getIsAllDeletedLiveData() {
        return isAllDeletedLiveData;
    }

    public void setCommentLiveData(boolean isReceived) {
        commentLiveData.setValue(isReceived);
    }
    public void setIsFirstCommentLiveData(boolean isFirst) {
        isFirstCommentLiveData.setValue(isFirst);
    }
    public void setIsAllDeletedLiveData(boolean isAllDeleted) {
        isAllDeletedLiveData.setValue(isAllDeleted);
    }

}
