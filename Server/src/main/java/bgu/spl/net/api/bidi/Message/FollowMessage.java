package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class FollowMessage extends Message {

    private final short opcode=4;
    private boolean follow;
    private List<String> users;
    private SharedData data;


    public FollowMessage(Connections<Message> connections, int connId, boolean follow, List<String> users, SharedData data) {
        super(connections, connId);
        this.follow = follow;
        this.users = users;
        this.data = data;
    }

    @Override
    public boolean execute() {
        String userName=data.getUserName(connId);
        LinkedList<String> toRemoveUsers= new LinkedList<>();
        if(userName==null || !(boolean)data.getUsers_loggedIn().get(userName)) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else {
            if(follow){
               for(String userToFollow:users) {
                   boolean added=data.subscribeFollower(userName,userToFollow);
                   if (!added)
                       toRemoveUsers.add(userToFollow);
               }
            }
            else {
                for(String userToUnFollow:users) {
                    boolean removed=data.unSubscribeFollower(userName,userToUnFollow);
                    if (!removed)
                        toRemoveUsers.add(userToUnFollow);
                }
            }
            users.removeAll(toRemoveUsers);
            if(users.isEmpty()) {
                new ErrorMessage(opcode, connections, connId).execute();
                return false;
            }
            else return new AckFollowMessage(opcode, connections, connId, users).execute();
        }
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        if(follow)
            bytes.add((byte)'\0');
        else
            bytes.add((byte)'\1');
        shortNum = shortToBytes((short)users.size());
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(stringListToBytes(users));

        return byteListToArray(bytes);
    }
}
