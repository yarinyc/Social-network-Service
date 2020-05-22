package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;
import java.util.List;


public abstract class Message {

    protected Connections<Message> connections;
    protected int connId;

    public Message(Connections<Message> connections, int connId) {
        this.connections = connections;
        this.connId = connId;
    }

    public abstract boolean execute();

    public abstract byte[] encode();

    static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    static List<Byte> stringListToBytes(List<String> list){
        List<Byte> bytes =new LinkedList<>();
        for (String s : list){
            bytes.addAll(byteArrayToList(s.getBytes()));
            bytes.add((byte)'\0');
        }
        return bytes;
    }
    static List<Byte> byteArrayToList(byte[] arr){
        List<Byte> list = new LinkedList<>();
        for (byte b:arr) {
            list.add(b);
        }
        return list;
    }
    static byte[] byteListToArray(List<Byte> list){
        byte[] array = new byte[list.size()];
        int i=0;
        for (Byte b:list) {
            array[i]=b;
            i++;
        }
        return array;
    }

}
