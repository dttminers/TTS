package in.tts.model;

import android.content.Context;
import android.os.Trace;

public class User {

    private static Trace mTrace;
    private static transient User user;
    private transient Context context;
    private String email;
    private String fcmToken;
    private String id;
    private String mobile;
    private String name;
    private String name1;
    private String name2;
    private int loginFrom;// 1. Google, 2. Fb, 3. Email
    private String picPath;
    private String token;

    public User(Context context) {
        this.context = context;
    }

    public static User getUser(Context context) {
        if (user == null) {
            user = new User(context);
        }
        return user;
    }

    public void setUser(User user) {
        user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public int getPic() {
        return loginFrom;
    }

    public void setLoginFrom(int pic) {
        this.loginFrom = pic;
    }

    public String getLoginFrom() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
