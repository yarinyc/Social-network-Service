package bgu.spl.net.api.bidi.Message;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.SharedData;

import java.util.LinkedList;
import java.util.List;

public class LogoutMessage extends Message{
    private final short opcode = 3;
    private SharedData<Message> data;

    public LogoutMessage(int connId, SharedData data, Connections<Message> connections) {
        super(connections, connId);
        this.data = data;
    }

    @Override
    public boolean execute() {
        if (!data.logOut(connId)) {
            new ErrorMessage(opcode, connections, connId).execute();
            return false;
        }
        else {
            new AckLogoutMessage(opcode, connections, connId).execute();
            return true;
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

