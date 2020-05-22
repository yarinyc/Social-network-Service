package bgu.spl.net.api;

import java.util.LinkedList;
import java.util.List;

public class Notifications<Message> {

    private List<Message> messages;
    private List<Message> unreadMessages;

    public Notifications(){
        messages = new LinkedList<>();
        unreadMessages = new LinkedList<>();
    }

    public void addMessage(Message msg) {
        messages.add(msg);
    }



    public void addUnreadMessage(Message unreadMessage) {
        unreadMessages.add(unreadMessage);
    }



    public void clearUnread(List<Message> sentMsgs){
        messages.addAll(sentMsgs);
        unreadMessages.removeAll(sentMsgs);
    }



    public LinkedList<Message> getUnreadMsg(){
        return new LinkedList<>(unreadMessages);
    }
}
