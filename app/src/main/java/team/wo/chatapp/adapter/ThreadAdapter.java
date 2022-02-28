package team.wo.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import team.wo.chatapp.R;
import team.wo.chatapp.model.MessageThread;
import team.wo.chatapp.utilis.HelperMethods;


public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.AudioItemsViewHolder>{

    private List<MessageThread> dataList;
    private final String userId;
    private Context mContext;
    private static final String TAG = "ThreadAdapter";
    private static final Integer SELF=1;
    private static final Integer OTHER=2;

    public ThreadAdapter(Context context, List<MessageThread> audioItems, String  selfId
    ) {
        this.dataList = audioItems;
        this.mContext = context;
        this.userId = selfId;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getSenderId().equals(userId))
            return SELF;
        else
            return OTHER;

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public AudioItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SELF)
            return new AudioItemsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_item, parent, false));
        else
            return new AudioItemsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_message_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AudioItemsViewHolder holder, int position) {
        HelperMethods helperMethod=new HelperMethods();
//        holder.tvTime.setText(TimeAgo.getTimeAgo(dataList.get(position).getCreated()));
        holder.tvName.setText(dataList.get(position).getSendName());
        switch (dataList.get(position).getType()){
            case "text": {
                holder.tvMsg.setText(helperMethod.decode(dataList.get(position).getContent()));
                holder.tvMsg.setVisibility(View.VISIBLE);
                break;
            }

            default:
                break;
        }


    }


    public void setDataList(@NotNull ArrayList<MessageThread> msgList) {
        this.dataList = msgList;
    }

    // Interaction listeners e.g. click, seekBarChange etc are handled in the view holder itself. This eliminates
    // need for anonymous allocations.
    static class AudioItemsViewHolder extends RecyclerView.ViewHolder  {
        TextView tvName,tvMsg;

        AudioItemsViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_message);
           // tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }


    }




}

