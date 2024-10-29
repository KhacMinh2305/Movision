package architecture.data.network.api;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class SessionAuthenticator implements Authenticator {

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, Response response) throws IOException {
        Request request = response.request();
        if(Objects.equals(request.header("Content-Type"), "application/json;charset=utf-8")) {
            //get guess session then retry

        }
        return null;
    }
}
