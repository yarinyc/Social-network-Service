package bgu.spl.net.api;

public class Stat {

    private int postsSent;
    private int numOfFollowing;
    private int numOfFollowers;

    public Stat(){
        postsSent=0;
        numOfFollowers=0;
        numOfFollowing=0;
    }

    public void incPosts(int num) {
        postsSent=postsSent+num;
    }

    public void incFollowing(int num) {
        numOfFollowing=numOfFollowing+num;
    }

    public void incFollowers(int num) {
        numOfFollowers=numOfFollowers+num;
    }

    public int getPostsSent() {
        return postsSent;
    }

    public int getNumOfFollowing() {
        return numOfFollowing;
    }

    public int getNumOfFollowers() {
        return numOfFollowers;
    }
}
