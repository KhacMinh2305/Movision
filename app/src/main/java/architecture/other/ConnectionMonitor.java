package architecture.other;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import javax.inject.Inject;
import javax.inject.Singleton;
import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class ConnectionMonitor implements DefaultLifecycleObserver {

    private final ConnectivityManager connManager;
    private final PublishSubject<Boolean> networkChange = PublishSubject.create();
    private boolean hasConnection;

    public PublishSubject<Boolean> getNetworkChange() {
        return networkChange;
    }

    private final NetworkRequest request = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build();

    private final ConnectivityManager.NetworkCallback monitoringCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            assert connManager != null;
            NetworkCapabilities networkCapabilities = connManager.getNetworkCapabilities(network);
            boolean available = networkCapabilities != null
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            // only trigger when the connection is available after offline, not execute each time
            // devices has more network connections option (Wifi, Ethernet, etc.)
            Log.d("Debug", "Co them mang !");
            if(!hasConnection && available) {
                hasConnection = true;
                networkChange.onNext(true);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            assert connManager != null;
            // Only trigger when theres no network anymore
            Log.d("Debug", "Mat 1 mang !");
            if(connManager.getAllNetworks().length == 0) {
                networkChange.onNext(false);
                hasConnection = false;
            }
        }
    };

    public boolean hasInternetConnection() {
        return hasConnection;
    }

    @Inject
    public ConnectionMonitor(@ApplicationContext Context context) {
        connManager = ContextCompat.getSystemService(context, ConnectivityManager.class);
        assert connManager != null;
        hasConnection = connManager.getActiveNetwork() != null;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
        connManager.registerNetworkCallback(request, monitoringCallback);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        connManager.unregisterNetworkCallback(monitoringCallback);
        networkChange.onComplete();
    }
}
