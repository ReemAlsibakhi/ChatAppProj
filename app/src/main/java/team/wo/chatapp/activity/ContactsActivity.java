package team.wo.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import team.wo.chatapp.R;
import team.wo.chatapp.adapter.ContactAdapter;
import team.wo.chatapp.model.User;

public class ContactsActivity extends AppCompatActivity {
    private TextView mTitle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");
    private static final String TAG = "ContactsActivity";
    private ArrayList<User> userList=new ArrayList<User>();
    private RecyclerView rvContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mTitle = findViewById(R.id.tv_title);
        rvContacts=findViewById(R.id.rv_contacts);
        mTitle.setText("Contacts");
        initRVContacts();
        getContactsList();
    }

    private void initRVContacts() {
        ContactAdapter contactAdapter = new ContactAdapter(getApplicationContext(), userList);
        rvContacts.setAdapter(contactAdapter);
        contactAdapter.setClickListener(new ContactAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {
                Intent intent=new Intent(ContactsActivity.this,ChatChannelActivity.class);
                Log.e(TAG, "onItemClick: "+user );
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }

    public void getContactsList() {
        userRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "error: " + error.getMessage().toString());
            }
            if (value != null && !value.getDocuments().isEmpty()) {
                userList.clear();
                for (QueryDocumentSnapshot d : value) {
                    Log.e(TAG, "getChatList: " + d.getData());
                    User users = d.toObject(User.class);
                  //  chatChannel.chatId = d.id
                    userList.add(users);
                    Log.e(TAG, "getContactsList: "+userList );
                    initRVContacts();
                }

            }
        });
    }


}