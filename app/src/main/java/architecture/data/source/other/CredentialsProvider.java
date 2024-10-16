package architecture.data.source.other;
import android.content.Context;
import android.os.CancellationSignal;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.AuthCredential;

import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.other.AppConstant;

@Singleton
public class CredentialsProvider {

    private final CredentialManager credentialManager;

    @Inject
    public CredentialsProvider(CredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

    private GetCredentialRequest getCredentialRequest() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(AppConstant.WEB_CLIENT_ID).build();
        return new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();
    }

    public void requestGoogleCredential(Context activityContext, CredentialManagerCallback<GetCredentialResponse, GetCredentialException> callback) {
        GetCredentialRequest request = getCredentialRequest();
        credentialManager.getCredentialAsync(activityContext,
                request, new CancellationSignal(), Executors.newSingleThreadExecutor(), callback);
    }
}
