package market.coin.simple.simplecoinmarket.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

import market.coin.simple.simplecoinmarket.R;
import retrofit2.HttpException;

public class ErrorUtil {
    private ErrorUtil() {
    }

    /**
     * Get error message from throwable
     *
     * @param context
     * @param throwable represent the error which return by Retrofit or others request api library
     * @return String indicates the message.
     */
    public static String getErrorMessage(Context context, Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof HttpException) {
            JSONObject jObjError;
            try {
                String jsonError = ((HttpException) throwable)
                        .response().errorBody().string();
                jObjError = new JSONObject(jsonError);
                return jObjError.getString("message");
            } catch (JSONException | IOException | NullPointerException e) {
                e.printStackTrace();
                return context.getString(R.string.error_common);
            }
        } else if (throwable instanceof NoSuchElementException) {
            return context.getString(R.string.error_no_data);
        } else if (throwable instanceof UnknownHostException) {
            return context.getString(R.string.error_unknown_host);
        } else if (throwable instanceof SocketTimeoutException) {
            return context.getString(R.string.error_socket_timeout);
        }

        return context.getString(R.string.error_common);
    }
}
