
#include "outputThread.h"

using namespace std;

outputThread::outputThread(ConnectionHandler &connectionHandler, std::mutex &_mutex) :
cHandler(connectionHandler), mutex(_mutex) {
}

void outputThread::operator()() {
    while (true) {
        string msgToPrint;

            msgToPrint = cHandler.decode();
        if (msgToPrint=="ACK 3") {
            std::cout << "ACK 3" <<  std::endl;
            cHandler.setTerminate(1);
            break;
        }
        if(!msgToPrint.empty()) {
            std::cout << msgToPrint  << endl;
            if(msgToPrint=="ERROR 3") {
                cHandler.setTerminate(-1);
            }
        }
    }
}

