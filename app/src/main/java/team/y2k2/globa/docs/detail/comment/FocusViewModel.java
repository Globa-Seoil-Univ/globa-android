package team.y2k2.globa.docs.detail.comment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FocusViewModel extends ViewModel {
    private final MutableLiveData<Boolean> commentFocusLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> subCommentFocusLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getCommentFocusLiveData() {
        return commentFocusLiveData;
    }

    public void setCommentFocusLiveData(boolean hasFocus) {
        commentFocusLiveData.setValue(hasFocus);
    }

    public MutableLiveData<Boolean> getSubCommentFocusLiveData() {
        return subCommentFocusLiveData;
    }

    public void setSubCommentFocusLiveData(boolean hasFocus) {
        subCommentFocusLiveData.setValue(hasFocus);
    }
}
