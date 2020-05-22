package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;
import java.util.List;

public class AckUserListMessage extends Message {

    private final short opcode=10;
    private short ackOpcode;
    private int numOfUsers;
    private LinkedList<String> userList;

    public AckUserListMessage(short ackOpcode, Connections<Message> connections, int connId, int numOfUsers, LinkedList<String> userList) {
        super(connections,connId);
        this.ackOpcode=ackOpcode;
        this.numOfUsers=numOfUsers;
        this.userList=userList;
    }


    @Override
    public boolean execute() {
        connections.send(connId,this);
        return true;
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
        shortNum = shortToBytes((short)userList.size());
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(stringListToBytes(userList));

        return byteListToArray(bytes);
    }
}
