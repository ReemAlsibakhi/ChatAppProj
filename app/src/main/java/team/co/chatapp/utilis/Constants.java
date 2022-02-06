package team.co.chatapp.utilis;

import java.util.HashMap;

public class Constants {
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_FCM_TOKEN = "token";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAnH0xlXo:APA91bFubdypmuHkjvmoIJsBGaK-Xwu-q2IsLGDiLHkcoEpQ33uAf7pbpO1qEgrSJKRF1BbofCXu8EQRP2Nm42rLWzC_QtsuKxeIhxe3phVfDXIkhBuC5ac4flvwtYqVoU9E41aPQsKY");
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }
}
