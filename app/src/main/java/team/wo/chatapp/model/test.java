package team.wo.chatapp.model;

import android.os.Parcel;
import android.os.Parcelable;


public class test implements Parcelable {
    private String threadId;
    private String senderId;
    private byte[] content;
    private String chatId;
    private String sendName;
    private String type;
    private Long created;

    protected test(Parcel in) {
        threadId = in.readString();
        senderId = in.readString();
        content = in.createByteArray();
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
        dest.writeByteArray(content);
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

    public static final Creator<test> CREATOR = new Creator<test>() {
        @Override
        public test createFromParcel(Parcel in) {
            return new test(in);
        }

        @Override
        public test[] newArray(int size) {
            return new test[size];
        }
    };
}