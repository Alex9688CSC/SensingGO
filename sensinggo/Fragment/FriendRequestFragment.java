package edu.nctu.wirelab.sensinggo.Fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.nctu.wirelab.sensinggo.Friend;
import edu.nctu.wirelab.sensinggo.FriendRequestAdapter_old;
import edu.nctu.wirelab.sensinggo.R;


public class FriendRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendRequestAdapter_old mAdapter;
    private List<Friend> friendList;

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // 設置格線
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        mAdapter = new FriendRequestAdapter_old(friendList, getActivity());
        recyclerView.setAdapter(mAdapter);

        /* old inefficient way for photo */
        //downloadFriendRequestPhoto();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (friendList == null) {
            friendList = new ArrayList<>();
        }
//        friendList.add(new Friend("Test000001", R.drawable.icon_many));
//        friendList.add(new Friend("Test000002", R.drawable.icon_many));
//        friendList.add(new Friend("Test000003", R.drawable.icon_manb));
//        friendList.add(new Friend("Test000004", R.drawable.icon_manb));
//        friendList.add(new Friend("Test000005", R.drawable.icon_mang));
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    /* old inefficient way for photo */
//    public void downloadFriendRequestPhoto() {
//        String info = "username=" + UserConfig.myUserName;
//        connectServer("POST", "/downloadToBeConfirmdFriendProfilePhoto", info);
//    }
//
//    public void downloadFriendRequestPhotoCallback(JSONArray result) {
//        try {
//            JSONObject jsonObject = result.getJSONObject(0);
//            JSONArray friends = jsonObject.getJSONArray("friends photo");
//
//            for (int i = 0; i < friends.length(); i++) {
//                JSONObject friend = friends.getJSONObject(i);
//                String name = friend.getString("name");
//                String photo = friend.getString("profile photo");
//                Log.d("9913test", "name: " + name + ", photo: " + photo);
//
//                if (photo.isEmpty()) {
//                    continue;
//                }
//
//                byte[] decodedString = Base64.decode(photo, Base64.URL_SAFE|Base64.NO_WRAP);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                for (int j = 0; j < friendList.size(); j++) {
//                    Log.d("9913test", "j1: " + j);
//                    if (friendList.get(j).getUsername().equals(name)) {
//                        Log.d("9913test", "j2: " + j);
//                        friendList.get(j).setBitmap(bitmap);
//                        friendList.get(j).setIsBitmapSet(true);
//                        mAdapter.notifyItemChanged(j);
//                    }
//                }
//            }
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void connectServer(String method, String path, String info){
//        Log.d("0612conn", info);
//        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//        httpsConnection.setActivity(getActivity());
//        httpsConnection.setFragment(this);
//        httpsConnection.setMethod(method, info);
//        httpsConnection.execute(path);
//
//    }

}
