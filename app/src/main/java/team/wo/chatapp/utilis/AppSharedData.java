package team.wo.chatapp.utilis;

import static android.content.Context.MODE_PRIVATE;

import com.google.gson.Gson;

import team.wo.chatapp.model.User;


public class AppSharedData {

    public static final String SHARED_APP_DATA = "app_data";
    public static final String SHARED_USER_DATA = "user";
    public static final String SHARED_DEVICE_TOKEN = "token";
    public static final String SHARED_TOKEN = "token_";
    public static final String SHARED_SETTINGS = "settings";
    private static final String SHARED_IS_USER_LOGIN = "is_user_login";
    private static final String SHARED_FCM_TOKEN = "fcmToken";
    private static Gson gson = new Gson();


    public static boolean isUserLogin() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getBoolean(SHARED_IS_USER_LOGIN, false);
    }

    public static void setUserLogin(boolean login) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putBoolean(SHARED_IS_USER_LOGIN, login).apply();
    }
    public static void setUserData(User user) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .edit().putString(SHARED_USER_DATA, gson.toJson(user)).apply();
    }

    public static User getUserData() {
        User mUser = gson.fromJson(APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_USER_DATA, null), User.class);
        return mUser;
    }
    public static void setDeviceToken(String token) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putString(SHARED_DEVICE_TOKEN, token).apply();

    }

    public static String getDeviceToken() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_DEVICE_TOKEN, "");
    }
    public static void setToken(String token) {
        APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putString(SHARED_TOKEN, token).apply();

    }

    public static String getToken() {
        return APP.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_TOKEN, "");
    }


}
