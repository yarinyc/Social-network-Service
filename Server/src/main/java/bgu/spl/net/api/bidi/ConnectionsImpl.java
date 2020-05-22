package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private HashMap<Integer, ConnectionHandler<T>> connections;

    public ConnectionsImpl(){
        connections=new HashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        try {
            ConnectionHandler<T> handler = connections.get(connectionId);
            if(handler==null)
                return false;
            else {
                synchronized (handler) {
                    handler.send(msg);
                    return true;
                }
            }
        }
        catch (IllegalStateException e){
            return false;
        }

    }

    @Override
    public void broadcast(T msg) {
        for(HashMap.Entry<Integer, ConnectionHandler<T>> entry : connections.entrySet())
            entry.getValue().send(msg);
    }

    @Override
    public void disconnect(int connectionId) {
        synchronized (connections.get(connectionId)) {
            connections.remove(connectionId);
        }
    }

    public void addConnection(int connId,ConnectionHandler<T> handler){
        connections.put(connId,handler);
    }
}
