package team.wo.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import team.wo.chatapp.R;
import team.wo.chatapp.utilis.Aes256Class;
import team.wo.chatapp.utilis.AppSharedData;
import team.wo.chatapp.utilis.HelperMethods;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       HelperMethods helperMethods=new HelperMethods();
//
//        Log.e(TAG, "encode text: "+ helperMethods.encode("hi reem"));
//
//        Log.e(TAG, "decode text: "+helperMethods.decode(helperMethods.encode("hi reem")) );


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppSharedData.isUserLogin()) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();

            }
        }, 4000);
    }


}