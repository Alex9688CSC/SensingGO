package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.Friend;
import edu.nctu.wirelab.sensinggo.FriendAdapter;
import edu.nctu.wirelab.sensinggo.FriendRequestAdapter;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.UserConfig;


public class SocialFragment extends Fragment {

    private MainActivity mainActivity;

    private ImageView imageViewSuggestedFriend;
    private ImageView imageViewCoinMaster1;
    private ImageView imageViewCoinMaster2;
    private ImageView imageViewCoinMaster3;
    private ImageView imageViewCoinMaster4;
    private TextView textViewFriendRequests;
    private TextView textViewFriends;

    private ImageView imageViewRequestsTriangle;
    private ImageView imageViewFriendsTriangle;

    private RecyclerView recyclerViewRequests;
    private RecyclerView recyclerViewFriends;
    private FriendRequestAdapter friendRequestAdapter;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private List<Friend> requestFriendList;

    private FloatingActionButton floatingActionButtonAddFriend;

    private JsonParser jsonParser = null;
    public void setJsonParser(JsonParser json) {
        jsonParser = json;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        if(UserConfig.myUserName.compareTo("DefaultUser") == 0){
            View view = inflater.inflate(R.layout.textview_nodata, container, false);
            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
            tv.setText(getString(R.string.defaultusernodata));
            return view;
        }

        if(UserConfig.similarityArray == null){

            View view = inflater.inflate(R.layout.textview_nodata, container, false);
            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
            tv.setText(getString(R.string.serverfail));
            return view;
        }

        View view = inflater.inflate(R.layout.fragment_social_new, container, false);


        imageViewSuggestedFriend = view.findViewById(R.id.imageViewSuggestedFriend);
        imageViewCoinMaster1 = view.findViewById(R.id.imageViewCoinMaster1);
        imageViewCoinMaster2 = view.findViewById(R.id.imageViewCoinMaster2);
        imageViewCoinMaster3 = view.findViewById(R.id.imageViewCoinMaster3);
        imageViewCoinMaster4 = view.findViewById(R.id.imageViewCoinMaster4);

        imageViewRequestsTriangle = view.findViewById(R.id.imageViewRequestsTriangle);
        imageViewFriendsTriangle = view.findViewById(R.id.imageViewFriendsTriangle);

        textViewFriendRequests = view.findViewById(R.id.textViewFriendRequests);
        textViewFriends = view.findViewById(R.id.textViewFriends);

        floatingActionButtonAddFriend = view.findViewById(R.id.floatingActionButtonAddFriend);


        recyclerViewRequests = view.findViewById(R.id.recyclerViewRequests);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        recyclerViewRequests.setHasFixedSize(true);

        // 設置格線
//        recyclerViewRequests.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // use a linear layout manager
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        friendRequestAdapter = new FriendRequestAdapter(requestFriendList, getActivity(), this);
        recyclerViewRequests.setAdapter(friendRequestAdapter);


        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendAdapter = new FriendAdapter(friendList, getActivity(), this);
        recyclerViewFriends.setAdapter(friendAdapter);

        textViewFriendRequests.setText(getString(R.string.friend_request_number, requestFriendList.size()));
        textViewFriends.setText(getString(R.string.friend_number, friendList.size()));

        imageViewSuggestedFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserConfig.newfriend_len == 1) {
                    makeFriend(UserConfig.similarityArray[0][0]);
                }
            }
        });

        imageViewCoinMaster1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserConfig.coinMasters != null) {
                    makeFriend(UserConfig.coinMasters[0]);
                }
            }
        });

        imageViewCoinMaster2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserConfig.coinMasters != null) {
                    makeFriend(UserConfig.coinMasters[1]);
                }
            }
        });

        imageViewCoinMaster3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserConfig.coinMasters != null) {
                    makeFriend(UserConfig.coinMasters[2]);
                }
            }
        });

        imageViewCoinMaster4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserConfig.coinMasters != null) {
                    makeFriend(UserConfig.coinMasters[3]);
                }
            }
        });

        imageViewRequestsTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // hide requests list
                if (recyclerViewRequests.getVisibility() == View.VISIBLE) {
//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textViewFriends.getLayoutParams();
//                    params.topToBottom = R.id.textViewFriendRequests;
//                    textViewFriends.requestLayout();

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerViewRequests.getLayoutParams();
                    params.height = 0;
                    recyclerViewRequests.setLayoutParams(params);

                    recyclerViewRequests.setVisibility(View.INVISIBLE);
                    imageViewRequestsTriangle.setImageResource(R.drawable.triangle_down);
                }
                // show requests list
                else {
//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textViewFriends.getLayoutParams();
//                    params.topToBottom = R.id.recyclerViewRequests;
//                    textViewFriends.requestLayout();

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerViewRequests.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    recyclerViewRequests.setLayoutParams(params);

                    recyclerViewRequests.setVisibility(View.VISIBLE);
                    imageViewRequestsTriangle.setImageResource(R.drawable.triangle_up);
                }
            }
        });

        imageViewFriendsTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide friends list
                if (recyclerViewFriends.getVisibility() == View.VISIBLE) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerViewFriends.getLayoutParams();
                    params.height = 0;
                    recyclerViewFriends.setLayoutParams(params);

                    recyclerViewFriends.setVisibility(View.INVISIBLE);
                    imageViewFriendsTriangle.setImageResource(R.drawable.triangle_down);
                }
                // show friends list
                else {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerViewFriends.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    recyclerViewFriends.setLayoutParams(params);

                    recyclerViewFriends.setVisibility(View.VISIBLE);
                    imageViewFriendsTriangle.setImageResource(R.drawable.triangle_up);
                }
            }
        });

        floatingActionButtonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View addFriendView =  inflater.inflate(R.layout.dialog_add_friend, null);
                final EditText usernameEditText = addFriendView.findViewById(R.id.edittext_add_friend_username);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.add_friend))
                        .setView(addFriendView)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                String friendUsername = usernameEditText.getText().toString();
                                if(friendUsername.equals("")){
                                    ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
                                }
                                else{
                                    String info = "username=" + UserConfig.myUserName +
                                            "&friendname=" + friendUsername +
                                            "&fromSimilar=" + 0;
                                    connectServer("POST", "/addFriendByID", info);
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                            }
                        }).show();
            }
        });


        loadImages();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (friendList == null) {
            friendList = new ArrayList<>();
        }

        if (requestFriendList == null) {
            requestFriendList = new ArrayList<>();
        }
    }

    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate( R.menu.menu_social, menu);
        Log.d("qqq","qqqqqqq");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friend_recommend_event) {
            Log.d("qqq","aaaaa");

            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView =  inflater.inflate(R.layout.dialog_friend_recommend_event, null);
            final EditText usernameEditText = dialogView.findViewById(R.id.editText_recommend_friend_username);

            new AlertDialog.Builder(getActivity(), R.style.YellowDialogTheme)
//                    .setTitle(getString(R.string.add_friend))
                    .setView(dialogView)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){

                            String friendUsername = usernameEditText.getText().toString();
                            if(friendUsername.equals("")){
                                ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
                            }
                            else{
                                String Info = "username=" + UserConfig.myUserName +
                                        "&referrerName=" + friendUsername;

                                connectServer("POST", "/newUserPromo", Info);
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                        }
                    }).show();


        }
        return true;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }
    public void setRequestFriendList(List<Friend> friendList) {
        this.requestFriendList = friendList;
    }

    private void loadImages() {
        ColorDrawable cd = new ColorDrawable(0x000000);
        if (UserConfig.newfriend_len == 1) {
            String internetUrl = Setting.HTTPSSERVER + UserConfig.similarityArray[0][0] + ".jpg";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .placeholder(cd)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewSuggestedFriend);
        }

        if (UserConfig.coinMasters != null) {
            String internetUrl = Setting.HTTPSSERVER + UserConfig.coinMasters[0] + ".jpg";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .placeholder(cd)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewCoinMaster1);

            internetUrl = Setting.HTTPSSERVER + UserConfig.coinMasters[1] + ".jpg";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .placeholder(cd)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewCoinMaster2);

            internetUrl = Setting.HTTPSSERVER + UserConfig.coinMasters[2] + ".jpg";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .placeholder(cd)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewCoinMaster3);

            internetUrl = Setting.HTTPSSERVER + UserConfig.coinMasters[3] + ".jpg";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .placeholder(cd)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewCoinMaster4);
        }

    }

    public void onFriendLongClick(int position) {
        // user cannot unfriend "sensinggo"
        if (UserConfig.similarityArray[position + UserConfig.newfriend_len][0].equals("sensinggo")) {
            return;
        }

        final AlertDialog.Builder DeleteFriendDialog = new AlertDialog.Builder(getActivity());
        DeleteFriendDialog.setMessage(getString(R.string.unfriend) + " " + UserConfig.similarityArray[position + UserConfig.newfriend_len][0]+" ?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("enterdelere2","enter");
                                Post_DeletefriendsMessage(UserConfig.similarityArray[position + UserConfig.newfriend_len][0]);

//
//                                // remove the friend on recyclerView
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        friendAdapter.removeItem(position);
//                                        textViewFriends.setText(getString(R.string.friend_number, friendList.size()));
//                                    }
//                                });

                            }
                        }).start();


                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public void onFriendClick(int position) {
        String friendname = UserConfig.similarityArray[position + UserConfig.newfriend_len][0];
        String info = "username=" + UserConfig.myUserName +
                "&friendname=" + friendname;
        Log.d("0614pos", Integer.toString(position) + '\t' + friendname);
        connectServer("POST", "/getFriendInfo", info);
    }

    public void onRequestButtonClick() {

        //requestFriendList.remove(position); //already removed in Adapter
        textViewFriendRequests.setText(getString(R.string.friend_request_number, requestFriendList.size()));

//        if (friend != null) {
//            friendAdapter.addItem(friend);
//            textViewFriends.setText(getString(R.string.friend_number, friendList.size()));
//        } else {
//
//        }
    }

    private void makeFriend(String name) {
        String makeFriendMessage = String.format(getString(R.string.makefriend), name);
        final AlertDialog.Builder AddFriendDialog = new AlertDialog.Builder(getActivity());

        AddFriendDialog.setMessage(makeFriendMessage)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Post_MakefriendsMessage(name);
                            }
                        }).start();

                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    private void Post_MakefriendsMessage(String newfriend){
        //String urlstring = "http://sensinggo.org/addFriend/";
        String Info = "friendname=" + newfriend +
                "&username=" + UserConfig.myUserName +
                "&fromSimilar=" + 1; // make friend from similar
        connectServer("POST", "/addFriendByID", Info);
    }

    private void Post_DeletefriendsMessage(String unfriend){
        //String urlstring = "http://140.113.216.39/unfriend/";
        String Info = "friendname=" + unfriend +
                "&username=" + UserConfig.myUserName;
        connectServer("POST", "/unfriend", Info);

    }

    public void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setFragment(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);
    }
}
