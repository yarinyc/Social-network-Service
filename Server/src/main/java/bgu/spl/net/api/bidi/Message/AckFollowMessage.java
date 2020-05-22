package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;
import java.util.List;

public class AckFollowMessage extends Message {

    private final short opcode=10;
    private short ackOpcode;
    private List<String> users;

    public AckFollowMessage(short ackOpcode, Connections<Message> connections, int connId, List<String> users) {
        super(connections,connId);
        this.ackOpcode=ackOpcode;
        this.users=users;
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
        shortNum = shortToBytes((short)users.size());
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(stringListToBytes(users));

        return byteListToArray(bytes);
    }
}
