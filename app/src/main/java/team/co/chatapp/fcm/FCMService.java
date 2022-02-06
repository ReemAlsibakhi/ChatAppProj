package team.co.chatapp.fcm;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import team.co.chatapp.R;
import team.co.chatapp.activity.ChatChannelActivity;
import team.co.chatapp.activity.MainActivity;
import team.co.chatapp.model.ChatChannel;
import team.co.chatapp.utilis.AppSharedData;
import team.co.chatapp.utilis.HelperMethods;


//handle notification

public class FCMService extends FirebaseMessagingService {
    private final String CHANNEL_CHAT_ID_ = "1";

    HelperMethods helperMethods;
    private String title;
    private String message;
    private String body;
    private String channel_id;
    private static final String TAG = "FCMService";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference chatRef = db.collection("chats");
    ChatChannel chat;
    String name;
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived: "+remoteMessage );
        int notificationID = new Random().nextInt(3000);
        helperMethods = new HelperMethods();

        if (!remoteMessage.getData().isEmpty()){
            title=remoteMessage.getData().get("title");
            body=remoteMessage.getData().get("body");
            message=remoteMessage.getData().get("message");
            channel_id=remoteMessage.getData().get("channelId");
        //    token=remoteMessage.getData().get("token");
            Log.e(TAG, "onMessageReceived: ");
            NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager, "CHAT" , CHANNEL_CHAT_ID_);
          }
            getChatById(channel_id);
            if (chat!=null){

                if (!chat.getUsers().get(0).equals(AppSharedData.getUserData().getId())){
                    name = chat.getNames().get(chat.getUsers().get(0));

                }
                if (!chat.getUsers().get(1).equals(AppSharedData.getUserData().getId())){
                    name = chat.getNames().get(chat.getUsers().get(1));

                }
            }
            final Intent intent = new Intent(this, ChatChannelActivity.class);
          //  intent.putExtra("FEED_ID", channel_id);
            intent.putExtra("chat",  chat);
            intent.putExtra("name",name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_CHAT_ID_ )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(notificationSoundUri)
                    .setContentIntent(pendingIntent);

            //Set notification color to match your app color template
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setColor(getResources().getColor(R.color.white));
            }
            notificationManager.notify(notificationID, notificationBuilder.build());

        }



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager, String type, String channelId) {
        CharSequence adminChannelName = type;

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(channelId, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
   public void getChatById(String channel_id){
       db.collection("chats")
               .document(channel_id)
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       chat = documentSnapshot.toObject(ChatChannel.class);
                       Log.e(TAG, "onSuccess: chat"+chat );
                    //   Log.e("qqq",receiverToken);
                   }
               });

   }


}
