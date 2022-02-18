package team.wo.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import team.wo.chatapp.R;
import team.wo.chatapp.adapter.ThreadAdapter;
import team.wo.chatapp.model.ChatChannel;
import team.wo.chatapp.model.MessageThread;
import team.wo.chatapp.model.User;
import team.wo.chatapp.network.ApiClient;
import team.wo.chatapp.network.ApiService;
import team.wo.chatapp.utilis.AppSharedData;
import team.wo.chatapp.utilis.Constants;
import team.wo.chatapp.utilis.HelperMethods;

public class ChatChannelActivity extends AppCompatActivity {
    private String intentChatId = "";
    private User user;
    private ChatChannel chatChannel;
    private static final String TAG = "chatChannel";
    private ArrayList<MessageThread> msgList = new ArrayList<MessageThread>();
    private RecyclerView rvMsgs;
    private ThreadAdapter threadAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference chatReference = db.collection("chats");
    private ImageView btnSend;
    private EditText mMessage;
    private TextView mTitle;
    private String receiverToken;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channel);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        user = getIntent().getParcelableExtra("user");
        name = getIntent().getStringExtra("name");
        Log.e(TAG, "User  " + user + name);
        if (user != null) {
            receiverToken = user.getToken();
            Log.e(TAG, "token hi " + receiverToken);

        }

        if(chatChannel!=null){
            for(String id : chatChannel.getUsers()){
                if(!AppSharedData.getUserData().getId().equals(id)){

                    db.collection("users")
                            .document(id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    receiverToken = documentSnapshot.getString("token");
                                    Log.e("qqq",receiverToken);
                                }
                            });
                }
            }

        }

        if (getIntent().getParcelableExtra("chat") != null) {
            chatChannel = getIntent().getParcelableExtra("chat");
            Log.e(TAG, "getIntentData: hello chat"+chatChannel);

            intentChatId = chatChannel.getChatId();
            receiverToken=chatChannel.getTokens();
            Log.e(TAG, "chat id: " + intentChatId);
            Log.e(TAG, "token hi " + receiverToken);
            threadListener(intentChatId);

            for(String id : chatChannel.getUsers()){
                if(!AppSharedData.getUserData().getId().equals(id)){

                    db.collection("users")
                            .document(id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    receiverToken = documentSnapshot.getString("token");
                                    Log.e("qqq",receiverToken);
                                }
                            });
                }
            }

        } else {
            checkIfChannel();
        }


    }

    private void threadListener(String intentChatId) {
        Log.e(TAG, "threadListener: ");
        if (!intentChatId.isEmpty()) {
            chatReference.document(intentChatId)
                    .collection("Threads")
                    .orderBy("created", Query.Direction.ASCENDING)
                    .addSnapshotListener((value, error) -> {
                        msgList.clear();
                        if (value.isEmpty()) {
                            Log.e(TAG, "hiiiii: " + value.getDocuments().size());
                        }
                        for (QueryDocumentSnapshot d : value) {
                            Log.e(TAG, "onEvent: " + d.getData());
                            msgList.add(d.toObject(MessageThread.class));

                        }
                        threadAdapter.setDataList(msgList);
                        threadAdapter = new ThreadAdapter(getApplicationContext(), msgList, AppSharedData.getUserData().getId());
                        rvMsgs.setAdapter(threadAdapter);
                        threadAdapter.notifyItemInserted(threadAdapter.getItemCount() - 1);
                        threadAdapter.notifyDataSetChanged();
                    });
        }
    }

    private void initRVChats() {
        threadAdapter = new ThreadAdapter(getApplicationContext(), msgList, AppSharedData.getUserData().getId());
        rvMsgs.setAdapter(threadAdapter);
    }

    private void checkIfChannel() {
        List<String> list = new ArrayList<String>();
        list.add(AppSharedData.getUserData().getId());
        list.add(user.getId());
        chatReference.whereEqualTo("users", list).get().addOnCompleteListener(p0 -> {
            if (p0.isSuccessful()) {
                for (DocumentSnapshot p : p0.getResult()) {
                    Log.e(TAG, "checkIfChannel: success " + p.getData());
                    intentChatId = p.getId();
                    chatChannel = p.toObject(ChatChannel.class);
                    threadListener(intentChatId);
                }
            }
        });

    }

    private void initView() {
        mTitle = findViewById(R.id.tv_title);
        rvMsgs = findViewById(R.id.rv_msgs);
        btnSend = findViewById(R.id.btn_send);
        mMessage = findViewById(R.id.et_message);
//        String name = "";
//        if (user != null) {
//            name = user.getName();
//        }
//        if (chatChannel != null) {
//            name = chatChannel.getReceiverName().toString();
//        }
//        String name= "";
//        if (user!=null){
//            if (!user.getId().equals(AppSharedData.getUserData().getId())){
//                name = user.getName();
//            }
//        }
//        if (chatChannel!=null){
//            Log.d(TAG, "initView() returned: " + chatChannel);
//            if (!chatChannel.getUsers().get(0).equals(AppSharedData.getUserData().getId())){
//                name = chatChannel.getNames().get(chatChannel.getUsers().get(0));
//
//            }
//            if (!chatChannel.getUsers().get(1).equals(AppSharedData.getUserData().getId())){
//                name = chatChannel.getNames().get(chatChannel.getUsers().get(1));
//
//            }
//        }
        if (name==null){
            mTitle.setText(user.getName());
        }else{
            mTitle.setText(name);
        }
        initRVChats();
        initListeners();
    }

    private void initListeners() {
        btnSend.setOnClickListener(view -> {
            if (!mMessage.getText().toString().isEmpty()) {
                sendMessage();
            } else {
                mMessage.setError(getString(R.string.this_field_equired));
                mMessage.requestFocus();
            }
        });
    }

    private void sendMessage() {
        if (intentChatId.isEmpty()) {
            createChannel(
                    new MessageThread(
                            AppSharedData.getUserData().getId().toString(),
                            HelperMethods.AESEncryptionMethod(mMessage.getText().toString()),
                            AppSharedData.getUserData().getName(),
                            "text", Calendar.getInstance().getTime().getTime())
            );
            Log.e(TAG, "initListeners: empty");
        } else {
            sendMsg(new MessageThread(
                    AppSharedData.getUserData().getId().toString(),
                    HelperMethods.AESEncryptionMethod(mMessage.getText().toString()),
                    AppSharedData.getUserData().getName(),
                    "text", Calendar.getInstance().getTime().getTime())
            );
            Log.e(TAG, "initListeners: not empty");
        }
    }

    private void createChannel(MessageThread messageThread) {
        List<String> users = new ArrayList<String>();
        users.add(0, AppSharedData.getUserData().getId());// id sender
        users.add(1, user.getId().toString());// id receiver

        HashMap<String, String> names = new HashMap<String, String>();
        names.put(AppSharedData.getUserData().getId(), AppSharedData.getUserData().getName());

        String name = "";
        if (chatChannel == null) {
            if (!user.getName().isEmpty()) {
                name = user.getName().toString();

            }
            names.put(user.getId(), name);
        }
        DocumentReference chatDocRef = chatReference.document();

        HashMap<String, Object> chatHashMap = new HashMap<String, Object>();
        chatHashMap.put("chatId", chatDocRef.getId());
        chatHashMap.put("users", users);
        chatHashMap.put("receiverName", user.getName().toString());
        chatHashMap.put("lastMessage", messageThread.getContent());
        chatHashMap.put("names", names);
        chatHashMap.put("tokens", user.getToken());

        DocumentReference threadDocRef = chatReference.document(chatDocRef.getId()).collection("Threads").document();
        chatDocRef.set(chatHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    messageThread.setThreadId(threadDocRef.getId());
                    threadDocRef.set(messageThread).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            intentChatId = chatDocRef.getId();
                            threadListener(intentChatId);
                            mMessage.setText("");
                            sendNotification(messageThread);
                        }
                    });
                }
            }
        });


    }

    private void sendNotification(MessageThread messageThread) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            Log.e(TAG, "sendNotification: "+receiverToken );
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("channelId", intentChatId);
            data.put("title", AppSharedData.getUserData().getName());
            data.put("message", messageThread.getContent());
            data.put("senderID", AppSharedData.getUserData().getId());
//            data.put("token", receiverToken);
            body.put("registration_ids", tokens);
            body.put(Constants.REMOTE_MSG_DATA, data);

            Log.e(TAG, "onSuccess:body " + body);
            sendRemoteMessage(body.toString());


        } catch (Exception exception) {
            Toast.makeText(ChatChannelActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendMsg(MessageThread messageThread) {

        String index = null;
        if (chatChannel != null) {
            if (!chatChannel.getUsers().get(0).equals(AppSharedData.getUserData().getId())) {
                index = chatChannel.getUsers().get(0);
            }
            if (!chatChannel.getUsers().get(1).equals(AppSharedData.getUserData().getId())) {
                index = chatChannel.getUsers().get(1);
            }
        }

        HashMap<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("lastMessage", messageThread.getContent());

        Log.e(TAG, "sendMsg: " + index);
        chatReference.document(intentChatId).update(userMap).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG, "reem success");
            }
        });

        DocumentReference threadDocRef = chatReference.document(intentChatId).collection("Threads").document();
        messageThread.setThreadId(threadDocRef.getId());
        String finalIndex = index;
        threadDocRef.set(messageThread).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mMessage.setText("");
                LinearLayoutManager layoutManager = (LinearLayoutManager) rvMsgs.getLayoutManager();
                layoutManager.smoothScrollToPosition(rvMsgs, null, threadAdapter.getItemCount());
                sendNotification(messageThread);

            }
        });


    }

    private void sendRemoteMessage(String remoteMessageBody) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG + " response0:", "response success");

                } else {
                    Log.e(TAG + " response:", response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG + " failure:", t.getMessage());

            }
        });
    }
}
//{
//        "message":{
//        "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
//        "data":{
//        "Nick" : "Mario",
//        "body" : "great match!",
//        "Room" : "PortugalVSDenmark"
//        }
//        }
//        }
