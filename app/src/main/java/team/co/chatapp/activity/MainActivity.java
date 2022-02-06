package team.co.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import team.co.chatapp.R;
import team.co.chatapp.adapter.ChatAdapter;
import team.co.chatapp.adapter.ContactAdapter;
import team.co.chatapp.model.ChatChannel;
import team.co.chatapp.model.User;
import team.co.chatapp.utilis.AppSharedData;
import team.co.chatapp.utilis.Constants;
//
public class MainActivity extends AppCompatActivity {
    private TextView mLogout;
    private TextView mTitle;
    private FloatingActionButton fab;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference chatRef = db.collection("chats");
    private static final String TAG = "MainActivity";
    private ArrayList<ChatChannel> chatList = new ArrayList<ChatChannel>();
    private RecyclerView rvChats;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogout = findViewById(R.id.tv_logout);
        mTitle = findViewById(R.id.tv_title);
        fab = findViewById(R.id.fab);
        rvChats = findViewById(R.id.rv_chat);
        mLogout.setVisibility(View.VISIBLE);
        initView();
        getChatsList();
        getToken();
    }

    private void initView() {
        mTitle.setText("Hello, " + AppSharedData.getUserData().getName());
        Log.e(TAG, "id user: "+AppSharedData.getUserData().getId() );
        initRVChat();
        initListeners();
    }

    private void initRVChat() {
        chatAdapter = new ChatAdapter(getApplicationContext(), chatList);
        rvChats.setAdapter(chatAdapter);
        chatAdapter.setClickListener(new ChatAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, ChatChannel chatChannel) {
                String name="";
                    Log.d(TAG, "initView() returned: " + chatChannel);
                    if (!chatChannel.getUsers().get(0).equals(AppSharedData.getUserData().getId())){
                        name = chatChannel.getNames().get(chatChannel.getUsers().get(0));

                    }
                    if (!chatChannel.getUsers().get(1).equals(AppSharedData.getUserData().getId())){
                        name = chatChannel.getNames().get(chatChannel.getUsers().get(1));

                    }
                Intent intent = new Intent(MainActivity.this, ChatChannelActivity.class);
                intent.putExtra("chat", chatChannel);
                intent.putExtra("name",name);
                Log.e(TAG, "chat channel: "+chatChannel );
                startActivity(intent);
            }
        });
    }

    private void getChatsList() {
        chatRef.whereArrayContains("users", AppSharedData.getUserData().getId().toString())
        .addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "error: " + error.getMessage().toString());
            }
            if (value != null && !value.getDocuments().isEmpty()) {
                chatList.clear();
                for (QueryDocumentSnapshot d : value) {
                    Log.e(TAG, "getChatList: " + d.getData());
                    ChatChannel chat = d.toObject(ChatChannel.class);
                    //  chatChannel.chatId = d.id
                    chatList.add(chat);
                    Log.e(TAG, "initRVChat: "+chatList );
                    initRVChat();
                }

            }
        });
    }

    private void initListeners() {
        mLogout.setOnClickListener(view -> {
            AppSharedData.setUserLogin(false);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);

        });


    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            // Toast.makeText(SplashScreenActivity.this, "Fetching FCM registration token failed ," +
                            //         " Try Login again", Toast.LENGTH_SHORT).show();
                            AppSharedData.setUserLogin(false);
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
//                        preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
//                        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
                        AppSharedData.setDeviceToken(token);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(AppSharedData.getUserData().getId())
                                .update(Constants.KEY_FCM_TOKEN, token, "platform", "android");
                        Log.e(TAG, "onComplete: token "+AppSharedData.getDeviceToken());
                        //  startActivity(new Intent(getApplicationContext(), HomeNavActivity.class));
                        //  finish();

                        //Toast.makeText(SplashScreenActivity.this, "Token Updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}