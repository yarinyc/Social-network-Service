package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.Notifications;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class NotificationMessage extends Message {

    private final short opcode=9;
    private SharedData<Message> data;
    private String sender;
    private String content;
    private char messageType;

    public NotificationMessage(Connections<Message> connections, int connId, SharedData<Message> data, String sender, String content, char messageType) {
        super(connections, connId);
        this.data = data;
        this.sender = sender;
        this.content = content;
        this.messageType=messageType;
    }

    @Override
    public boolean execute() {
        String userToSend = data.getUserName(connId);
        Notifications<Message> userNotificationObj = data.getUsers_notifications().get(userToSend);

        //Add message to the user's Notifications database object
        synchronized (data.getUsers_loggedIn().get(userToSend)) {
            if (data.isLoggedIn(userToSend)) {
                userNotificationObj.addMessage(this);
                //Sends message to user
                connections.send(connId,this);
            }
            else
                userNotificationObj.addUnreadMessage(this);
        }
        return true;
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.add((byte)messageType);
        bytes.addAll(byteArrayToList(sender.getBytes()));
        bytes.add((byte)'\0');
        bytes.addAll(byteArrayToList(content.getBytes()));
        bytes.add((byte)'\0');

        return byteListToArray(bytes);
    }
}
