package team.y2k2.globa.docs.detail.comment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FocusViewModel extends ViewModel {

    private MutableLiveData<Boolean> commentBtnFocusLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> subCommentBtnFocusLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getCommentBtnFocusLiveData() {
        return commentBtnFocusLiveData;
    }
    public MutableLiveData<Boolean> getSubCommentBtnFocusLiveData() {
        return subCommentBtnFocusLiveData;
    }

    public void setCommentBtnFocusLiveData(boolean hasFocus) {
        commentBtnFocusLiveData.setValue(hasFocus);
    }
    public void setSubCommentBtnFocusLiveData(boolean hasFocus) {
        subCommentBtnFocusLiveData.setValue(hasFocus);
    }

}
