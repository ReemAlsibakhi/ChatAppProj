package team.wo.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import team.wo.chatapp.R;
import team.wo.chatapp.model.User;
import team.wo.chatapp.utilis.AppSharedData;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone;
    private CountryCodePicker codePicker;
    private Button btnRegister;
    private String token;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.et_userName);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        codePicker = findViewById(R.id.country);
        btnRegister = findViewById(R.id.btn_register);
        getToken();

        btnRegister.setOnClickListener(view -> {
            if (isValid())
                checkIfFound(codePicker.getSelectedCountryCodeWithPlus() + etPhone.getText().toString());


        });

    }

    private void checkIfFound(String mobile) {

        usersRef.whereEqualTo("phone",mobile).get().addOnCompleteListener(p0->{
            if (p0.isSuccessful()) {
                Log.e(TAG, "checkIfChannel: success "+p0.getResult().size());
                 if (p0.getResult().size()==0){
                     Log.e(TAG, "checkIfFound:  not exists" );
                     navigate();
                 }else{
                     for (DocumentSnapshot p : p0.getResult()) {
                         Log.e(TAG, "checkIfChannel: success " + p.getData());
                         if (p.exists()){
                             Toast.makeText(getApplicationContext(),"user already exists",Toast.LENGTH_LONG).show();
                             Log.e(TAG, "checkIfFound:  exists" );
                         }else {
                             Log.e(TAG, "checkIfFound:  not exists" );
                         }
                     }
                 }

            }else{
                Toast.makeText(this,""+p0.getException(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void navigate() {
        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
        User user = new User(etName.getText().toString(), etEmail.getText().toString(), codePicker.getSelectedCountryCodeWithPlus() + etPhone.getText().toString(),token);
        intent.putExtra("user", user);
        intent.putExtra("viaFrom", "register");
        startActivity(intent);


    }

    public Boolean isValid() {
        if (etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.field_required));
            etName.requestFocus();
            return false;
        } else if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError(getString(R.string.field_required));
            etEmail.requestFocus();
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError(getString(R.string.email_not_valid));
            etEmail.requestFocus();
            return false;
        } else if (etPhone.getText().toString().isEmpty()) {
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
//                            // Toast.makeText(SplashScreenActivity.this, "Fetching FCM registration token failed ," +
//                            //         " Try Login again", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                            finish();
//                            return;
//                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        Log.e(TAG, "onComplete: "+token );
                        AppSharedData.setDeviceToken(token);
                    }
                });
    }

}