//
// Created by omerdav on 12/31/18.
//

#ifndef CLIENT_OUTPUTTHREAD_H
#define CLIENT_OUTPUTTHREAD_H

#include <iostream>
#include <thread>
#include <mutex>
#include <string>
#include <boost/asio.hpp>
#include "connectionHandler.h"
#include <atomic>

class outputThread {
private:
    ConnectionHandler &cHandler;
    std::mutex &mutex;

public:
    outputThread(ConnectionHandler &connectionHandler, std::mutex& _mutex);
    void operator()();
};


#endif //CLIENT_OUTPUTTHREAD_H
