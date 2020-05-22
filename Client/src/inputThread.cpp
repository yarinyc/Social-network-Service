
#include "inputThread.h"
using namespace std;

inputThread::inputThread(ConnectionHandler &connectionHandler, std::mutex &_mutex):
        cHandler(connectionHandler), mutex(_mutex){
}

void inputThread::operator()() {
    const short bufsize = 1024;
    char buf[bufsize];
    bool exit= false;
    while(!exit){
        std::cin.getline(buf, bufsize); // get input from user
        std::string line(buf);// create string of message for user
        vector<char> bytes;

        if(!line.empty()) {
            cHandler.encode(bytes, line);
            if (bytes.size() != 0)
                if (!cHandler.sendBytes(bytes.data(), (unsigned int) bytes.size()))
                    break;
            if (line == "LOGOUT") {
                while (cHandler.getTerminate() == 0) {
                    //wait
                }
                if (cHandler.getTerminate() == 1) {
                    exit = true;
                }
                cHandler.setTerminate(0);
            }
        }
    }
}
