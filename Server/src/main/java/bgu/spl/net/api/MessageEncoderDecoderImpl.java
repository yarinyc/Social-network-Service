package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.Message.*;
import bgu.spl.net.srv.SharedData;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private List<Byte> bytes;
    private short opcode=0;
    private int zeroByteCounter;
    private int connId;
    private SharedData<Message> data;
    private Connections<Message> connections;
    private List<String> msgData = new LinkedList<>();
    private boolean isTrue=false;
    private int startIndex=-1;

    public MessageEncoderDecoderImpl(int connId, SharedData<Message> data, Connections<Message> connections) {
        this.connId = connId;
        this.data = data;
        this.connections = connections;
        this.bytes = new LinkedList<>();
        this.zeroByteCounter = -1;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        bytes.add(nextByte);
        if (bytes.size() == 2) {
            opcode = bytesToShort(byteListToArray(bytes));
            if (opcode == 3 | opcode == 7)
                return createMessage();
        }
        else if(bytes.size()>=3 & zeroByteCounter==-1){
            switch (opcode){
                case 1:
                case 2:
                case 6:
                    zeroByteCounter=2;
                    startIndex=2;
                    isTrue=true;
                    break;
                case 4:
                    zeroByteCounter=-1;
                    startIndex=5;
                    break;
                case 5:
                case 8:
                    zeroByteCounter=1;
                    startIndex=2;
                    isTrue=true;
                    break;
            }
            if(!isTrue){
                if(nextByte=='\0' & msgData.isEmpty())
                    msgData.add("follow");
                else if(msgData.isEmpty())
                    msgData.add("unfollow");
                else if(bytes.size()==5){
                    byte[] numOfUsers=new byte[2];
                    numOfUsers[0]=bytes.get(3);
                    numOfUsers[1]=bytes.get(4);
                    short num =bytesToShort(numOfUsers);
                    zeroByteCounter=num;
                    isTrue=true;
                }
            }
        }
        if(isTrue & nextByte=='\0')
            zeroByteCounter--;
        if(zeroByteCounter==0) {
            completeMsgData(startIndex);
            return createMessage();
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        return message.encode();
    }



    // Auxiliary functions
    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] byteListToArray(List<Byte> list){
        byte[] array = new byte[list.size()];
        int i=0;
        for (Byte b:list) {
            array[i]=b;
            i++;
        }
        return array;
    }

    private Message createMessage(){
        Message msgToSend=null;
        switch (opcode){
            case 3:
                msgToSend = new LogoutMessage(connId,data,connections);
                break;
            case 7:
                msgToSend = new UserListMessage(connections,connId,data);
                break;
            case 1:
                msgToSend = new RegisterMessage(msgData.get(0),msgData.get(1),connId,data,connections);
                break;
            case 2:
                msgToSend = new LoginMessage(msgData.get(0),msgData.get(1),connId,data,connections);
                break;
            case 4:
                boolean follow=false;
                if(msgData.remove(0).equals("follow"))
                    follow=true;
                msgToSend = new FollowMessage(connections,connId,follow,new LinkedList<>(msgData),data);
                break;
            case 5:
                msgToSend = new PostMessage(connections,connId,msgData.get(0),data);
                break;
            case 6:
                msgToSend = new PmMessage(connections,connId,msgData.get(1),data,msgData.get(0));
                break;
            case 8:
                msgToSend = new StatMessage(connections,connId,data,msgData.get(0));
                break;
        }
        opcode=0;
        zeroByteCounter=-1;
        isTrue=false;
        startIndex=-1;
        msgData.clear();
        bytes.clear();
        return msgToSend;
    }


    private void completeMsgData(int start){
        int index=start;
        byte[] array =byteListToArray(bytes);
        for(int i=start; i<array.length; i++)
            if(array[i]=='\0'){
                msgData.add(new String(array,index,i-index, StandardCharsets.UTF_8));
                index=i+1;
            }
    }
}


