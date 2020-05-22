package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Message.LogoutMessage;
import bgu.spl.net.api.bidi.Message.Message;
import bgu.spl.net.srv.SharedData;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private Connections<Message> connections;
    private int connectionId;
    private boolean shouldTerminate;
    private SharedData<Message> sharedDataObj;
    private boolean initialized=false;

    public BidiMessagingProtocolImpl(SharedData<Message> sharedDataObj) {
       connections=null;
       connectionId=-1;
       shouldTerminate=false;
       this.sharedDataObj = sharedDataObj;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections=connections;
        this.connectionId=connectionId;
        initialized=true;
    }

    @Override
    public void process(Message message) {
        while (!initialized){
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e){

            }
        }
        boolean executed= message.execute();
        if (message instanceof LogoutMessage && executed)
            shouldTerminate=true;
     }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
