#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <mutex>

using boost::asio::ip::tcp;

class ConnectionHandler {
private:
	const std::string host_;
	const short port_;
	boost::asio::io_service io_service_;   // Provides core I/O functionality
	tcp::socket socket_;
    int shouldTerminate_;

 
public:
    ConnectionHandler(std::string host, short port, int &shouldTerminate);
    virtual ~ConnectionHandler();
 
    // Connect to the remote machine
    bool connect();
 
    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);
 
	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);
	
    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);
	
	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(std::string& line);
 
    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(std::string& frame, char delimiter);
 
    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const std::string& frame, char delimiter);
	
    // Close down the connection properly.
    void close();

    //Encoder function
    void encode(std::vector<char> &encodedMsg,const std::string &msg);

    //Decoder function
    std::string decode();

    int getTerminate();

    void setTerminate(int value);

    void createRegister(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode);

    void createLogin(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode);

    void createFollow(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode);

    void createPost(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode) ;

    void createPM(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode) ;

    void createStat(std::vector<char> &encodedMsg,  std::vector<std::string> &words, char *opcode) ;

    void createUserlist(std::vector<char> &encodedMsg, char *opcode) ;

    void createLogout(std::vector<char> &encodedMsg, char *opcode) ;

    void createMessage(std::vector<char> &encodedMsg, std::vector<std::string> &words, const std::string &command, char *opcode);

    std::string decodeNotification(std::string &msg, std::string &frame);

    std::string decodeError(std::string &msg);

    std::string decodeAck4or7(std::string &msg, std::string &frame);

    std::string decodeAck8(std::string &msg);

}; //class ConnectionHandler
 
#endif