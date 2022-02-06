package team.co.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import team.co.chatapp.R;
import team.co.chatapp.model.User;
import team.co.chatapp.utilis.AppSharedData;
import team.co.chatapp.utilis.Constants;

public class OtpVerificationActivity extends AppCompatActivity {
    private User user;
    private OtpView otpFields;
    private TextView resendCode,timer;
    private static final String TAG = "OtpVerificationActivity";
    private CountDownTimer countDownTimer;
    String VR_ID="";
    String viaFrom="";
    String token="";
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        getArgumentsData();
        initView();
        getToken();
    }

    private void getArgumentsData() {
        user=getIntent().getParcelableExtra("user");
        viaFrom=getIntent().getStringExtra("viaFrom");
        Log.e(TAG, "getArgumentsData: "+user );
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        otpFields=findViewById(R.id.otpFields);
        resendCode=findViewById(R.id.resendCode);
        timer=findViewById(R.id.timer);

        initListeners();
        sendCodeToPhone();
    }

    private void initListeners() {
        resendCode.setOnClickListener(v -> {
            if (timer.getVisibility() == View.GONE) {
                sendCodeToPhone();
            }
        });

        otpFields.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String code) {
                if (code.length() == 6) {
                    if (!VR_ID.equals("")) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VR_ID, code);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.please_wait_while_send_code, Toast.LENGTH_LONG).show();

                    }

                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
               // mViewModel. progressbarObservable.setValue(false);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d("onVerificationFailed", e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d("onVerificationFailed", e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
             //   mViewModel.progressbarObservable.setValue(false);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                VR_ID = verificationId;
                Log.d("onCodeSent", verificationId);
                callTimer();
              //  mViewModel.progressbarObservable.setValue(false);

            }
        };

    }

    void sendCodeToPhone() {
      //  mViewModel.progressbarObservable.setValue(true);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(user.getPhone())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's informationVerification code is invalid
                        Toast.makeText(getApplicationContext(), R.string.code_verified, Toast.LENGTH_SHORT).show();

                       if (viaFrom.equals("login"))
                           navigateToMain();
                       else
                           storeUser();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.verification_code_is_invalid, Toast.LENGTH_SHORT).show();
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    private void navigateToMain() {
        AppSharedData.setUserLogin(true);
        Intent intent=new Intent(OtpVerificationActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void storeUser() {
        DocumentReference collRef = db.collection("users").document();
        user.setId(collRef.getId());
        collRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                navigateToMain();
                AppSharedData.setUserData(new User(collRef.getId(), user.getName()));
            }
        });
    }


    private void callTimer() {
        countDownTimer = new CountDownTimer(60*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setVisibility(View.VISIBLE);
                 timer.setText(MillisUntilToTime(millisUntilFinished));
            }

            public void onFinish() {
                timer.setVisibility(View.GONE);
            }
        };
        countDownTimer.start();

    }

    public String MillisUntilToTime(Long millisUntilFinished) {
        long minutes = millisUntilFinished / 1000 / 60;
        long seconds = (millisUntilFinished / 1000 % 60);
        Log.d("timier", "seconds remaining: " + new DecimalFormat("00").format(minutes) + "." + new DecimalFormat("00").format(seconds));
        return new DecimalFormat("00").format(minutes) + "." + new DecimalFormat("00").format(seconds);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (countDownTimer != null) {
            callTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            // Toast.makeText(SplashScreenActivity.this, "Fetching FCM registration token failed ," +
                            //         " Try Login again", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        AppSharedData.setDeviceToken(token);
                    }
                });
    }
}