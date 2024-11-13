package architecture.ui.state;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;

public class VerificationCodeUiState {

    private static final int COUNT_DOWN_TIME = 60;
    private final MutableLiveData<Boolean> sheetState = new MutableLiveData<>();
    private final MutableLiveData<Integer> waitingTimeState = new MutableLiveData<>();
    private final MutableLiveData<String> emailState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> codeCheckingResult = new MutableLiveData<>();
    private final MutableLiveData<String> clearCodeState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    public boolean isCountingDown = false;
    public int countDownRemain = COUNT_DOWN_TIME;
    private final Handler handler;

    public MutableLiveData<Boolean> getSheetState() { return sheetState; }
    public MutableLiveData<Integer> getWaitingTimeState() { return waitingTimeState; }
    public MutableLiveData<String> getEmailState() { return emailState; }
    public MutableLiveData<Boolean> getCodeCheckingResult() { return codeCheckingResult; }
    public MutableLiveData<String> getClearCodeState() { return clearCodeState; }
    public MutableLiveData<Boolean> getLoadingState() { return loadingState; }

    public void setSheetState(boolean state) { sheetState.setValue(state); }
    public void setEmailState(String gmail) { emailState.setValue(gmail); }
    public void setLoadingState(boolean state) { loadingState.setValue(state); }
    public void clearCode() { clearCodeState.setValue(""); }

    public VerificationCodeUiState() {
        handler = new Handler(Looper.getMainLooper());
    }

    private final Runnable countDown = new Runnable() {
        @Override
        public void run() {
            boolean counting = onCountingDown(this);
            if(!counting) { onStopCountingDown(this); }
        }
    };

    private boolean onCountingDown(Runnable runnable) {
        if (countDownRemain >= 0 && isCountingDown) {
            waitingTimeState.setValue(countDownRemain--);
            handler.postDelayed(runnable, 1000);
            return true;
        }
        return false;
    }

    private void onStopCountingDown(Runnable runnable) {
        countDownRemain = COUNT_DOWN_TIME;
        isCountingDown = false;
        handler.removeCallbacks(runnable);
    }

    public void countDown() {
        isCountingDown = true;
        handler.post(countDown);
    }
}
