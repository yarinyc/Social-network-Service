package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class PmMessage extends Message{

    private final short opcode=6;
    private String content;
    private String userName;
    private SharedData<Message> data;

    public PmMessage(Connections<Message> connections, int connId, String content, SharedData<Message> data, String userName) {
        super(connections, connId);
        this.content = content;
        this.data = data;
        this.userName=userName;
    }

    @Override
    public boolean execute(){
        String sender=data.getUserName(connId);
        Integer toSend = data.getConnId(userName);
        if(sender==null || toSend==null || !data.isLoggedIn(sender)) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else{
            new NotificationMessage(connections,toSend,data,sender,content, '0').execute();
            return new AckPmMessage(opcode, connections, connId).execute();
        }
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(byteArrayToList(userName.getBytes()));
        bytes.add((byte)'\0');
        bytes.addAll(byteArrayToList(content.getBytes()));
        bytes.add((byte)'\0');

        return byteListToArray(bytes);
    }
}
