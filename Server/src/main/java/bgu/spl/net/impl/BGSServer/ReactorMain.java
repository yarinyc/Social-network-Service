package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.Message.Message;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.SharedData;

public class ReactorMain {
    public static void main(String[] args) {

        SharedData<Message> sharedDataObj = new SharedData<>();//one shared object
        ConnectionsImpl<Message> connections = new ConnectionsImpl<>();
        Integer port = new Integer(args[0]);
        Integer numberOfThreads = new Integer(args[1]);


        Server.reactor(
                numberOfThreads,
                port,//port
                ()->new BidiMessagingProtocolImpl(sharedDataObj), //protocol factory
                ()->new MessageEncoderDecoderImpl(Reactor.connectionIdCounter,sharedDataObj,connections),//message encoder decoder factory
                connections
        ).serve();

    }
}
