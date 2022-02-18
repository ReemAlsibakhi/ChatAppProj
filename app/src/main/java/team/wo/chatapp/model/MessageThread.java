package team.wo.chatapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import team.wo.chatapp.utilis.HelperMethods;

public class MessageThread implements  Parcelable {
    private String threadId;
    private String senderId;
    private String content;
    private String chatId;
    private String sendName;
    private String type;
    private Long created;

    public MessageThread() {
    }

    public MessageThread(String senderId, String content, String sendName, String type, Long created) {
        this.senderId = senderId;
        this.content = content;
        this.sendName = sendName;
        this.created = created;
        this.type = type;
    }

    protected MessageThread(Parcel in) {
        threadId = in.readString();
        senderId = in.readString();
        content = in.readString();
        chatId = in.readString();
        sendName = in.readString();
        type = in.readString();
        if (in.readByte() == 0) {
            created = null;
        } else {
            created = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(threadId);
        dest.writeString(senderId);
        dest.writeString(content);
        dest.writeString(chatId);
        dest.writeString(sendName);
        dest.writeString(type);
        if (created == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(created);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageThread> CREATOR = new Creator<MessageThread>() {
        @Override
        public MessageThread createFromParcel(Parcel in) {
            return new MessageThread(in);
        }

        @Override
        public MessageThread[] newArray(int size) {
            return new MessageThread[size];
        }
    };

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return HelperMethods.AESDecryptionMethod(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}