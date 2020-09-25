package edu.nctu.wirelab.sensinggo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.Fragment.SocialFragment;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {
    private List<Friend> friendList;
    private WeakReference<Context> activityContext;
    private SocialFragment socialFragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewUsername;
        public ImageView imageViewUser;
        public ImageButton imageButtonAccept;
        public ImageButton imageButtonReject;
        public View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            imageViewUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
            imageButtonAccept = (ImageButton) itemView.findViewById(R.id.imageButtonAccept);
            imageButtonReject = (ImageButton) itemView.findViewById(R.id.imageButtonReject);
            this.itemView = itemView;

            imageButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Friend friend = friendList.get(getAdapterPosition());
                    String info = "username=" + UserConfig.myUserName +
                            "&friendname=" + friend.getUsername() +
                            "&choice=" + 1;
                    connectServer("POST", "/confirmAddFriend", info, activityContext.get());

                    removeItem(getAdapterPosition());
//                    socialFragment.onRequestButtonClick();
                    // Toast.makeText(view.getContext(), "click " +getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });

            imageButtonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Friend friend = friendList.get(getAdapterPosition());
                    String info = "username=" + UserConfig.myUserName +
                            "&friendname=" + friend.getUsername() +
                            "&choice=" + 0;
                    connectServer("POST", "/confirmAddFriend", info, activityContext.get());

                    removeItem(getAdapterPosition());
//                    socialFragment.onRequestButtonClick();
                    // Toast.makeText(view.getContext(), "click " +getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendRequestAdapter(List<Friend> friendList, Context context, SocialFragment socialFragment) {
        this.friendList = friendList;
        activityContext = new WeakReference<>(context);
        this.socialFragment = socialFragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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


        String internetUrl = Setting.HTTPSSERVER + friend.getUsername() + ".jpg";
        ColorDrawable cd = new ColorDrawable(0x000000);
        
        Glide.with(activityContext.get())
                .load(internetUrl)
                .placeholder(cd)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imageViewUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        httpsConnection.setActivity(socialFragment.getActivity());
        httpsConnection.execute(path);
    }
}