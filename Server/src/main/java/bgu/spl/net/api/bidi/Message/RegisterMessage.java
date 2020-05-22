package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;


public class RegisterMessage extends Message{

    private SharedData<Message> data;
    private final short opcode=1;
    private String userName;
    private String password;

    public RegisterMessage(String userName,String password,int connId, SharedData<Message> data,Connections<Message> connections) {
        super(connections,connId);
        this.userName=userName;
        this.password=password;
        this.data=data;
    }

    @Override
    public boolean execute(){
        synchronized (data.getUsers_Id()){
            if (!data.getUsers_Id().containsKey(userName) && !data.getUsers_Id().values().contains(connId)) {
                data.addNewUser(userName, password, connId);
                return new AckRegisterMessage(opcode, connections, connId).execute();
            } else {
                new ErrorMessage(opcode, connections, connId).execute();
                return false;
            }
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
        bytes.addAll(byteArrayToList(password.getBytes()));
        bytes.add((byte)'\0');

        return byteListToArray(bytes);
    }
}
