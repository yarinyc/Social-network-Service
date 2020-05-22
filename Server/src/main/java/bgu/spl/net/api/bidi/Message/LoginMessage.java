package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class LoginMessage extends Message{
    private final short opcode = 2;
    private SharedData<Message> data;
    private String userName;
    private String password;


    public LoginMessage(String userName, String password, int connId, SharedData data, Connections<Message> connections) {
        super(connections, connId);
        this.userName = userName;
        this.password = password;
        this.data = data;
    }

    @Override
    public boolean execute() {
        boolean b=data.logIn(connId,userName,password);
        if (!b) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else {
            synchronized (data.getUsers_notifications().get(userName)) { //sync notifications: if user gets any new login msgs while login executes
                List<Message> sentMsgs = new LinkedList<>();
                for (Message msg : data.getUsers_notifications().get(userName).getUnreadMsg()) {
                    if(connections.send(connId, msg))
                        sentMsgs.add(msg);
                }
                data.getUsers_notifications().get(userName).clearUnread(sentMsgs);
            }
           return new AckLoginMessage(opcode, connections, connId).execute();
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
