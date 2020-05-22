package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class PostMessage extends Message {

    private final short opcode=5;
    private String content;
    private SharedData<Message> data;

    public PostMessage(Connections<Message> connections, int connId, String content, SharedData<Message> data) {
        super(connections, connId);
        this.content = content;
        this.data = data;
    }

    @Override
    public boolean execute() {
        String userName=data.getUserName(connId);
        if(userName==null || !data.isLoggedIn(userName)) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else {
            List<String> sendTo = new LinkedList<>();
            String[] words = content.split(" ");
            if(content.equals(""))
                return true;
            for (String word : words)
                if (!word.equals("@") && word.charAt(0) == '@')
                    sendTo.add(word.substring(1));

           //send post to all followers/@<userName>
            data.getUsers_stat().get(userName).incPosts(1);
            for (String user:data.getUsers_followers().get(userName)) {//maybe need sync for changes while iterating
                int connId=data.getConnId(user);

                new NotificationMessage(connections,connId,data,userName,content,'1').execute();
            }
            data.removeFollowers(sendTo,userName);
            for (String user:sendTo) {
                int connId=data.getConnId(user);
                new NotificationMessage(connections,connId,data,userName,content,'1').execute();
            }
            new AckPostMessage(opcode, connections, connId).execute();
        }
        return true;
    }

    @Override
    public byte[] encode() {
        List<Byte> bytes=new LinkedList<>();
        byte[] shortNum = shortToBytes(opcode);
        bytes.add(shortNum[0]);
        bytes.add(shortNum[1]);
        bytes.addAll(byteArrayToList(content.getBytes()));
        bytes.add((byte)'\0');

        return byteListToArray(bytes);
    }
}
