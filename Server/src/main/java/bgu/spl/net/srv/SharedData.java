package bgu.spl.net.srv;

import bgu.spl.net.api.Notifications;
import bgu.spl.net.api.Stat;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SharedData<Message> {

    private ConcurrentHashMap<String, Notifications<Message>> users_notifications;
    private ConcurrentHashMap<String,Integer> users_Id;
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> users_followers;
    private ConcurrentHashMap<String,String> users_passwords;
    private ConcurrentHashMap<String,Boolean> users_loggedIn;
    private ConcurrentHashMap<String, Stat> users_stat;

    public SharedData() {
        users_notifications=new ConcurrentHashMap<>();
        users_Id=new ConcurrentHashMap<>();
        users_passwords=new ConcurrentHashMap<>();
        users_loggedIn=new ConcurrentHashMap<>();
        users_stat=new ConcurrentHashMap<>();
        users_followers=new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Notifications<Message>> getUsers_notifications() {
        return users_notifications;
    }

    public ConcurrentHashMap<String, Integer> getUsers_Id() {
        return users_Id;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> getUsers_followers() {
        return users_followers;
    }

    public ConcurrentHashMap<String, String> getUsers_passwords() {
        return users_passwords;
    }

    public ConcurrentHashMap<String, Boolean> getUsers_loggedIn() {
        return users_loggedIn;
    }

    public ConcurrentHashMap<String, Stat> getUsers_stat() {
        return users_stat;
    }

    public void addNewUser(String userName,String password, int connId){
        users_Id.put(userName, connId);
        users_notifications.put(userName, new Notifications<Message>());
        users_followers.put(userName, new ConcurrentLinkedDeque<String>());
        users_passwords.put(userName, password);
        users_loggedIn.put(userName, false);
        users_stat.put(userName,new Stat());
    }

    //returns username by corresponding connId
    public String getUserName(int connId){
        for(ConcurrentHashMap.Entry<String,Integer> entry: users_Id.entrySet())
            if(entry.getValue()==connId)
                return entry.getKey();
        return null;
    }
    
    public Integer getConnId(String userName){
        return users_Id.get(userName);
    }
    
    public boolean isLoggedIn(String userName){
        return users_loggedIn.get(userName);
    }

    public boolean logIn(int currentConnId, String userName,String password){
        if(users_loggedIn.get(userName)==null)
            return false;
        synchronized (users_loggedIn.get(userName)) {
            if (!users_passwords.get(userName).equals(password) || users_loggedIn.get(userName))
                return false;
            else {
                users_loggedIn.replace(userName, true);
                users_Id.replace(userName,currentConnId);
            }
            return true;
        }
    }

    public boolean logOut(int connId){
        String userName=getUserName(connId);
        if (userName==null)
            return false;
        synchronized (users_loggedIn.get(userName)) {
            if (!users_loggedIn.get(userName))
                return false;
            else users_loggedIn.replace(userName, false);
            return true;
        }
    }

    public boolean subscribeFollower(String userName, String userToFollow) {//assumes userName exists
        ConcurrentLinkedDeque<String> userToFollowList=users_followers.get(userToFollow);
        if(userToFollowList==null)
            return false;
        synchronized (userToFollowList){ //sync to prevent multiple changes at once
            if (users_followers.get(userToFollow).contains(userName))
                return false;
            users_followers.get(userToFollow).add(userName);
            users_stat.get(userToFollow).incFollowers(1);
            users_stat.get(userName).incFollowing(1);
            return true;
        }
    }

    public boolean unSubscribeFollower(String userName, String userToUnFollow) {//assumes userName exists
        ConcurrentLinkedDeque<String> userToUnFollowList=users_followers.get(userToUnFollow);
        if(userToUnFollowList==null)
            return false;
        synchronized (userToUnFollowList){ //sync to prevent multiple changes at once
            if (!users_followers.get(userToUnFollow).contains(userName))
                return false;
            users_followers.get(userToUnFollow).remove(userName);
            users_stat.get(userToUnFollow).incFollowers(-1);
            users_stat.get(userName).incFollowing(-1);
            return true;
        }
    }

    //removes all the current followers of userName from given list
    public void removeFollowers(List<String> sendTo, String userName) {
        synchronized (users_followers.get(userName)) {
            sendTo.removeAll(users_followers.get(userName));
        }
    }

}
