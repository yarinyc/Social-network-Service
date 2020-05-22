package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;
import java.util.List;

public class AckLoginMessage extends Message {
    private final short opcode=10;
    private short ackOpcode;

    public AckLoginMessage(short ackOpcode, Connections<Message> connections, int connId) {
        super(connections,connId);
        this.ackOpcode=ackOpcode;
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

        return byteListToArray(bytes);
    }
}
