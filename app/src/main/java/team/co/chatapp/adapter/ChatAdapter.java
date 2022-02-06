package team.co.chatapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.co.chatapp.R;
import team.co.chatapp.model.ChatChannel;
import team.co.chatapp.utilis.AppSharedData;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatChannel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private static final String TAG = "chatAdapter";

    // data is passed into the constructor
    public ChatAdapter(Context context, List<ChatChannel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatChannel data = mData.get(position);

        String index= "";
        if (!data.getUsers().get(0).equals(AppSharedData.getUserData().getId())){

            index = data.getUsers().get(0);
            Log.e(TAG, "index: "+index );
        }
        if (!data.getUsers().get(1).equals(AppSharedData.getUserData().getId())){
            index = data.getUsers().get(1);
            Log.e(TAG, "index: "+index );

        }
        holder.mName.setText(data.getNames().get(index));

        holder.mLastMsg.setText(data.getLastMessage());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mName;
        TextView mLastMsg;

        ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tv_name);
            mLastMsg = itemView.findViewById(R.id.tv_lastMessage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                Log.e(TAG, "onClick: hello" );
                mClickListener.onItemClick(view, mData.get(getAdapterPosition()));
            }
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, ChatChannel chatChannel);
    }
}
