package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class UserListMessage extends Message {

    private final short opcode=7;
    private SharedData<Message> data;


    public UserListMessage(Connections<Message>connections,int connId, SharedData<Message> data){
        super(connections,connId);
        this.data=data;
    }


    @Override
    public boolean execute(){
        if(data.getUserName(connId)==null) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        if(!data.isLoggedIn(data.getUserName(connId))) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else {
            synchronized (data.getUsers_Id()) {
                LinkedList<String> userList = new LinkedList<>(data.getUsers_Id().keySet());
                return new AckUserListMessage(opcode, connections, connId, userList.size(), userList).execute();
            }
        }
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);

        return byteListToArray(bytes);
    }
}
