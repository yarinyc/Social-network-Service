CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: Client
	g++ -o bin/BGSclient bin/connectionHandler.o bin/BGSclient.o bin/inputThread.o bin/outputThread.o $(LDFLAGS) 

Client: bin/connectionHandler.o bin/BGSclient.o bin/inputThread.o bin/outputThread.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp
	

bin/inputThread.o: src/inputThread.cpp
	g++ $(CFLAGS) -o bin/inputThread.o src/inputThread.cpp


bin/outputThread.o: src/outputThread.cpp
	g++ $(CFLAGS) -o bin/outputThread.o src/outputThread.cpp

.PHONY: clean
clean:
	rm -f bin/*
