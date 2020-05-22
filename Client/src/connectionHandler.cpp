#include <connectionHandler.h>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using namespace std;

ConnectionHandler::ConnectionHandler(string host, short port, int &shouldTerminate):
        host_(host), port_(port), io_service_(), socket_(io_service_), shouldTerminate_(shouldTerminate){}

ConnectionHandler::~ConnectionHandler() {
    close();
}

//Auxiliary split function
std::vector<std::string> split(const std::string &s, char delimiter) {
    std::vector<std::string> tokens;
    std::string token;
    std::istringstream tokenStream(s);
    while (std::getline(tokenStream, token, delimiter))
    {
        tokens.push_back(token);
    }
    return tokens;
}


short bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {

    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
        do{
            getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    bool result=sendBytes(frame.c_str(),frame.length());
    if(!result) return false;
    return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }


}

void putArrayInVector(int size, const char word[], vector<char> &v){
    for(int i=0;(unsigned)i<size;i++)
        v.push_back(word[i]);
}

//Encoder function
void ConnectionHandler::encode(vector<char> &encodedMsg,const string &msg) {
    std::vector<string> words= split(msg,' ');
    std::string command = words[0];
    char opcode[2];
    createMessage(encodedMsg, words, command, opcode);
}


//Decoder function
std::string ConnectionHandler::decode() {
    char opcodeBytes[2];
    short opcode=0;
    string msg;
    string frame;
    if (getBytes(opcodeBytes, 2)) {
        opcode = bytesToShort(opcodeBytes);
    } else return "";
    if (opcode == 9)
        return decodeNotification(msg, frame);

    if(opcode == 11)
        return decodeError(msg);

    if(opcode==10) {
        char ackOpcode[2];
        short msgOpcode;
        msg.append("ACK ");
        if (getBytes(ackOpcode, 2)) {
            msgOpcode = bytesToShort(ackOpcode);
            msg.append(std::to_string(msgOpcode)+" ");
        } else return "";

        if(msgOpcode==1 | msgOpcode==2 | msgOpcode==3 | msgOpcode==5 | msgOpcode==6 ) {
            msg.resize(msg.length()-1);
            return msg;
        }
        if(msgOpcode==4 | msgOpcode ==7)
            return decodeAck4or7(msg, frame);
        if(msgOpcode==8)
            return decodeAck8(msg);
    }
    return "";
}

int ConnectionHandler::getTerminate() {
    return shouldTerminate_;
}

void ConnectionHandler::setTerminate(int value) {
    shouldTerminate_=value;
}

/// Auxiliary functions:
// create a specific type of message

void ConnectionHandler::createMessage(vector<char> &encodedMsg, vector<string> &words, const string &command, char *opcode) {
    if(command == "REGISTER")
        createRegister(encodedMsg, words, opcode);

    else if(command == "LOGIN")
        createLogin(encodedMsg, words, opcode);

    else if(command == "LOGOUT")
        createLogout(encodedMsg, opcode);

    else if(command == "FOLLOW")
        createFollow(encodedMsg, words, opcode);

    else if(command == "POST")
        createPost(encodedMsg, words, opcode);

    else if(command == "PM")
        createPM(encodedMsg, words, opcode);

    else if(command == "USERLIST")
        createUserlist(encodedMsg, opcode);

    else if(command == "STAT")
        createStat(encodedMsg, words, opcode);
}

void ConnectionHandler::createStat(vector<char> &encodedMsg,  vector<string> &words, char *opcode) {
    shortToBytes(8, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    putArrayInVector((int)words[1].size(),words[1].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
}

void ConnectionHandler::createPM(vector<char> &encodedMsg,  vector<string> &words, char *opcode) {
    shortToBytes(6, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    putArrayInVector((int)words[1].size(),words[1].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
    for(int i=2; i<words.size();i++) {
        words[i].append(" ");
        putArrayInVector((int) words[i].size(), words[i].c_str(), encodedMsg);
    }
    encodedMsg.push_back('\0');
}

void ConnectionHandler::createPost(vector<char> &encodedMsg, vector<string> &words, char *opcode) {
    shortToBytes(5, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    for(int i=1; i<words.size();i++) {
        words[i].append(" ");
        putArrayInVector((int) words[i].size(), words[i].c_str(), encodedMsg);
    }
    encodedMsg.push_back('\0');
}

void ConnectionHandler::createFollow(vector<char> &encodedMsg,  vector<string> &words, char *opcode) {
    char numOfUsers[2];
    shortToBytes(4, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    if (words[1] == "0")
        encodedMsg.push_back('\0');
    else
        encodedMsg.push_back('\1');
    shortToBytes((short) stoi(words[2]), numOfUsers);
    encodedMsg.push_back(numOfUsers[0]);
    encodedMsg.push_back(numOfUsers[1]);
    for (int i = 3; i < bytesToShort(numOfUsers) + 3; i++) {
        putArrayInVector((int) words[i].size(), words[i].c_str(), encodedMsg);
        encodedMsg.push_back('\0');
    }
}

void ConnectionHandler::createLogin(vector<char> &encodedMsg,  vector<string> &words, char *opcode) {
    shortToBytes(2, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    putArrayInVector((int)words[1].size(),words[1].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
    putArrayInVector((int)words[2].size(),words[2].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
}

void ConnectionHandler::createRegister(vector<char> &encodedMsg,  vector<string> &words, char *opcode) {
    shortToBytes(1, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
    putArrayInVector((int)words[1].size(),words[1].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
    putArrayInVector((int)words[2].size(),words[2].c_str(),encodedMsg);
    encodedMsg.push_back('\0');
}

void ConnectionHandler::createLogout(vector<char> &encodedMsg, char *opcode) {
    shortToBytes(3, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
}

void ConnectionHandler::createUserlist(vector<char> &encodedMsg, char *opcode) {
    shortToBytes(7, opcode);
    encodedMsg.push_back(opcode[0]);
    encodedMsg.push_back(opcode[1]);
}

//decodes a specific type of message
string ConnectionHandler::decodeAck8(string &msg) {
    char numData[2];
    for(int i=0; i<3;i++){
        if (getBytes(numData, 2)) {
            msg.append(to_string(bytesToShort(numData))+" ");
        } else return "";
    }
    msg.resize(msg.length()-1);
    return msg;
}

string ConnectionHandler::decodeAck4or7(string &msg, string &frame) {
    char numOfUsersBytes[2];
    short numOfUsers;
    if (getBytes(numOfUsersBytes, 2)) {
        numOfUsers=bytesToShort(numOfUsersBytes);
        msg.append(to_string(numOfUsers)+" ");
    } else return "";
    for(int i=0; i<numOfUsers;i++){
        if (getFrameAscii(frame, '\0')) {
            frame.resize(frame.length()-1);
            msg.append(frame + " ");
            frame = "";
        } else return "";
    }
    msg.resize(msg.length()-1);
    return msg;
}

string ConnectionHandler::decodeError(string &msg) {
    char ackOpcode[2];
    msg.append("ERROR ");
    if (getBytes(ackOpcode, 2)) {
        short msgOpcode=0;
        msgOpcode = bytesToShort(ackOpcode);
        msg.append(to_string(msgOpcode));
        return msg;
    } else return "";
}

string ConnectionHandler::decodeNotification(string &msg, string &frame) {
    char byte;
    msg.append("NOTIFICATION ");
    if (getBytes(&byte, 1)) {
        if (byte == '0')
            msg.append("PM ");
        else msg.append("Public ");
    } else return "";
    for (int i = 0; (unsigned) i < 2; i++)
        if (getFrameAscii(frame, '\0')) {
            frame.resize(frame.length()-1);
            msg.append(frame + " ");
            frame = "";
        } else return "";
    msg.resize(msg.length()-1);
    return msg;
}