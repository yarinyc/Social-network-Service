#include <stdlib.h>
#include <connectionHandler.h>
#include <inputThread.h>
#include <outputThread.h>


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    int shouldTerminate(0);
    //shouldTerminate.store(1);
    ConnectionHandler connectionHandler(host, port, shouldTerminate);
    std::mutex _mutex;

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    inputThread inputTask(connectionHandler,_mutex);
    outputThread outputTask(connectionHandler,_mutex);

    //Starting input+output threads
    std::thread inThread(std::ref(inputTask));
    std::thread outThread(std::ref(outputTask));

    //Joining threads
    outThread.join();
    inThread.join();
    connectionHandler.close();
    return 0;
}
