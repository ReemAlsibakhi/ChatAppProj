package team.wo.chatapp.utilis;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;




public class APP extends Application {

    public static APP mInstance;
    public static final String TAG = APP.class.getSimpleName();
    public static Context mContext;

    public static synchronized APP getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mInstance = this;
        mContext = getApplicationContext();

    }



    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }




}
