
#ifndef CLIENT_INPUTTHREAD_H
#define CLIENT_INPUTTHREAD_H

#include <iostream>
#include <thread>
#include <mutex>
#include <string>
#include <boost/asio.hpp>
#include "connectionHandler.h"

class inputThread  {
private:
    ConnectionHandler &cHandler;
    std::mutex &mutex;

public:
    inputThread(ConnectionHandler &connectionHandler, std::mutex& _mutex);
    void operator()();
};


#endif //CLIENT_INPUTTHREAD_H
