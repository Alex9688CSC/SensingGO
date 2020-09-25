package edu.nctu.wirelab.sensinggo;

import android.graphics.Bitmap;

public class Friend {
    private String username;
    private String nickname;
//    private int image = R.drawable.icon_manb;
//    private Bitmap bitmap;
//    private boolean isBitmapSet;

    public Friend() {
        super();
    }

    public Friend(String username) {
        super();
        this.username = username;
    }

    public Friend(String username, String nickname) {
        super();
        this.username = username;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
