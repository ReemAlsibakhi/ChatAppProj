package team.co.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import team.co.chatapp.R;
import team.co.chatapp.model.User;
import team.co.chatapp.utilis.AppSharedData;

public class LoginActivity extends AppCompatActivity {
    private TextView tvCreateAccount;
    private Button btnLogin;
    private CountryCodePicker codePicker;
    private EditText etPhone;
    private String token;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvCreateAccount = findViewById(R.id.tv_newAccount);
        btnLogin = findViewById(R.id.btn_login);
        codePicker = findViewById(R.id.country);
        etPhone = findViewById(R.id.et_phone);
        getToken();

        btnLogin.setOnClickListener(view -> {
            if (isValid()) {

                checkIfUser(codePicker.getSelectedCountryCodeWithPlus().toString() + etPhone.getText().toString());
            }
        });

        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void checkIfUser(String mobile) {
        userRef.whereEqualTo("phone", mobile).get().addOnCompleteListener(p0 -> {
            if (p0.isSuccessful()) {
                Log.e(TAG, "checkIfChannel: success " + p0.getResult().size());
                if (p0.getResult().size() == 0) {
                    Log.e(TAG, "checkIfFound:  not exists");
                    Toast.makeText(getApplicationContext(), "user not exists", Toast.LENGTH_LONG).show();
                } else {
                    for (DocumentSnapshot p : p0.getResult()) {
                        Log.e(TAG, "checkIfChannel: success " + p.getData());
                        AppSharedData.setUserData(p.toObject(User.class));
                        if (p.exists()) {
                            navigate();
                            Toast.makeText(getApplicationContext(), "user already exists", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "checkIfFound:  exists");
                        } else {
                            Log.e(TAG, "checkIfFound:  not exists");
                        }
                    }
                }

            } else {
                Toast.makeText(this, "" + p0.getException(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void navigate() {
        Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
        User user = new User("", "", codePicker.getSelectedCountryCodeWithPlus() + etPhone.getText().toString(),token);
        intent.putExtra("user", user);
        intent.putExtra("viaFrom", "login");
        startActivity(intent);
        finish();

    }

    public Boolean isValid() {
        if (etPhone.getText().toString().isEmpty()) {
            etPhone.setError(getString(R.string.field_required));
            etPhone.requestFocus();
            return false;
        } else if (etPhone.getText().toString().length() < 9) {
            etPhone.setError(getString(R.string.phone_valid));
            etPhone.requestFocus();
            return false;
        }
        return true;
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                             Toast.makeText(LoginActivity.this, "Fetching FCM registration token failed ," + " Try Login again", Toast.LENGTH_SHORT).show();
////                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
////                            finish();
//                            return;
//                        }
                        if (task.isSuccessful()){
                            // Get new FCM registration token
                            token = task.getResult();
                            Log.e(TAG, "onComplete: "+token );
                            AppSharedData.setDeviceToken(token);
                        }

                    }
                });
    }


}