package edu.nctu.wirelab.sensinggo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;

public class FriendRequestAdapter_old extends RecyclerView.Adapter<FriendRequestAdapter_old.MyViewHolder> {
    private List<Friend> friendList;
    private WeakReference<Context> activityContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewUsername;
        public ImageView imageViewUser;
        public Button buttonAccept;
        public Button buttonReject;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            imageViewUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
//            buttonAccept = (Button) itemView.findViewById(R.id.buttonAccept);
//            buttonReject = (Button) itemView.findViewById(R.id.buttonReject);

            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String info = "username=" + UserConfig.myUserName +
                            "&friendname=" + friendList.get(getAdapterPosition()).getUsername() +
                            "&choice=" + 1;
                    connectServer("POST", "/confirmAddFriend", info, activityContext.get());
                    removeItem(getAdapterPosition());
                    // Toast.makeText(view.getContext(), "click " +getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });

            buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String info = "username=" + UserConfig.myUserName +
                            "&friendname=" + friendList.get(getAdapterPosition()).getUsername() +
                            "&choice=" + 0;
                    connectServer("POST", "/confirmAddFriend", info, activityContext.get());
                    removeItem(getAdapterPosition());
                    // Toast.makeText(view.getContext(), "click " +getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendRequestAdapter_old(List<Friend> friendList, Context context) {
        this.friendList = friendList;
        activityContext = new WeakReference<>(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendRequestAdapter_old.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Log.d("9913test", "onBindViewHolder");
        final Friend friend = friendList.get(position);
        holder.textViewUsername.setText(friend.getUsername());

        /* old inefficient way for photo */
//        if (friend.getIsBitmapSet()) {
//            holder.imageViewUser.setImageBitmap(friend.getBitmap());
//            Log.d("9913test", "bitmap position:" + position);
//        }
//        else {
//            holder.imageViewUser.setImageResource(friend.getImage());
//            Log.d("9913test", "Image position:" + position);
//        }

        String internetUrl = Setting.HTTPSSERVER + friend.getUsername() + ".jpg";
        ColorDrawable cd = new ColorDrawable(0x000000);
        
        Glide.with(activityContext.get())
                .load(internetUrl)
                .placeholder(cd)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imageViewUser);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void removeItem(int position) {
        friendList.remove(position);
        notifyItemRemoved(position);
    }

    public void connectServer(String method, String path, String info, Context context) {
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(context);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);
    }
}