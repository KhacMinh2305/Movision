package architecture.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangeAvatarViewModel extends ViewModel {

    @Inject
    public ChangeAvatarViewModel() {}
}
