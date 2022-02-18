package team.wo.chatapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

public class ChatChannel implements Parcelable {
    private String chatId;
    private List<String> users;
    private String receiverName;
    private String lastMessage;
    private String tokens;
    private HashMap<String, String> names;

    public ChatChannel() {

    }



    protected ChatChannel(Parcel in) {
        chatId = in.readString();
        users = in.createStringArrayList();
        receiverName = in.readString();
        lastMessage = in.readString();
        tokens = in.readString();

    }

    public ChatChannel(String channel_id, String title) {
        this.chatId=channel_id;
        this.lastMessage=title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeStringList(users);
        dest.writeString(receiverName);
        dest.writeString(lastMessage);
        dest.writeString(tokens);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatChannel> CREATOR = new Creator<ChatChannel>() {
        @Override
        public ChatChannel createFromParcel(Parcel in) {
            return new ChatChannel(in);
        }

        @Override
        public ChatChannel[] newArray(int size) {
            return new ChatChannel[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public HashMap<String, String> getNames() {
        return names;
    }

    public void setNames(HashMap<String, String> names) {
        this.names = names;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public static Creator<ChatChannel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "ChatChannel{" +
                "chatId='" + chatId + '\'' +
                ", users=" + users +
                ", receiverName='" + receiverName + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", token=" + tokens +
                ", names=" + names +
                '}';
    }
}
