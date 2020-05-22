package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class StatMessage extends Message {

    private final short opcode=8;
    private String userName;
    private SharedData<Message> data;


    public StatMessage(Connections<Message> connections, int connId, SharedData<Message> data, String userName) {
        super(connections, connId);
        this.data=data;
        this.userName = userName;
    }

    @Override
    public boolean execute() {
        String userName = data.getUserName(connId);
        if(userName==null || !data.isLoggedIn(userName)){
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        return new AckStatMessage(connections,connId,opcode,data,userName).execute();
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(byteArrayToList(userName.getBytes()));
        bytes.add((byte)'\0');

        return byteListToArray(bytes);
    }
}
