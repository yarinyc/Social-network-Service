package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;
import bgu.spl.net.api.Stat;

import java.util.LinkedList;
import java.util.List;

public class AckStatMessage extends Message {

    private final Short opcode=10;
    private short ackOpcode;
    private SharedData<Message> data;
    private String userName;
    private int numOfPosts=0;
    private int numOfFollowers=0;
    private int numOfFollowing=0;

    public AckStatMessage(Connections<Message> connections, int connId, short ackOpcode, SharedData<Message> data, String userName) {
        super(connections, connId);
        this.ackOpcode = ackOpcode;
        this.data = data;
        this.userName = userName;
    }

    @Override
    public boolean execute() {
        Stat userStat = data.getUsers_stat().get(userName);
        if(userStat==null) {
            new ErrorMessage(ackOpcode, connections, connId).execute();
            return false;
        }
        else {
            numOfPosts = userStat.getPostsSent();
            numOfFollowers = userStat.getNumOfFollowers();
            numOfFollowing = userStat.getNumOfFollowing();
            connections.send(connId, this);
            return true;
        }
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        shortNum = shortToBytes(ackOpcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        shortNum = shortToBytes((short) numOfPosts);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        shortNum = shortToBytes((short) numOfFollowers);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        shortNum = shortToBytes((short) numOfFollowing);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);

        return byteListToArray(bytes);

    }
}
