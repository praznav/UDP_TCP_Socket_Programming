# UDP_TCP_Socket_Programming
CS 3251 Programming Assignment 1. UDP and TCP socket in java.


CS 3251 - Computer Networks I
 
Sockets Programming Assignment 1 
 
 
TCP and UDP Applications Programming
 
For this assignment you will design and write your own application program using network sockets. You will implement two different versions of the application. One version will be based on TCP. The other will use UDP. You will implement both the client and server portions of this application. You may choose Java, Python or C/C++ for your implementation.
 
The application you are designing is a simple banking application that allows you to record deposits and withdrawals to an account. Because authentication is important for such an application, you will include a basic online authentication service. The authentication service is based on a shared secret "password" and a challenge response protocol. The client interface will allow users to provide a username and password that will be used to determine whether or not they are granted access and then perform the requested account adjustment. The client command should be called "remotebank" and it should read the username, password, and account request from the command line. The command should report the transaction information and new account balance on success. If it does not succeed, it should provide appropriate diagnostic feedback to the user. For instance, if you provide an invalid password it should print "User authorization failed." as output.
 
You must allow the host name (or IP address) and port number of the server to be specified on the command line of the client in addition to the username and password. 
(Of course, you would never implement a real security system that specifies username and password on the command line. In such cases another user running the "ps" command would quickly learn your password!) 
 
For instance, the command:
remotebank 127.0.0.1:8591 “DrEvil” “minime123” deposit 27.50
might produce the output:
Welcome DrEvil. 
Your deposit of 27.50 is successfully recorded.
Your new account balance is 394.50
Thank you for banking with us.

 
This access query would have been sent to the server application running on the local host (same system), listening on port 8591. Note: The command line must work exactly this way to make sure the TA can easily test your program.
 
Your server should support at least three different users. You can keep this simple and use a static list of usernames, passwords, and initial balances. When you start the server, you should specify the port number on which it will be listening.
 
bank-server 8591
 
This would start the server on port 8591. Normally, the server should run forever, processing multiple clients. 
 
Challenge Response Algorithm
 
The access algorithm you are implementing is an extremely simplistic form of the Digest Authentication scheme widely used by web servers and many other services including VoIP. You should start by reading about these in Chapter 8 of your text and also in the Wikipedia pages on Digest Authentication and MD5 Hash Function.
Your access application will require the exchange of four messages.
Client sends "authentication request" message.
Server responds with a one time use, challenge value in the form of a random 64 character string. (You get to decide how this random string is generated.)
Client computes a MD5 hash of the string formed by concatenating the username, password and  the random string sent by the server. Hash = MD5("username","password","challenge")
Client sends the clear text "username" and the resulting "Hash" to the server.
The server takes the username, finds the corresponding password on file, and performs the same MD5 calculation. It then compares the calculated Hash to the one sent by the client. 
If they match, the user has successfully authenticated. If no match, then authentication fails.
 
You will need to develop your own "protocol" for the communication between the client and the server. While you should use TCP or UDP to transfer messages back and forth, you must determine exactly what messages will be sent, what they mean and when they will be sent (syntax, semantics and timing). Be sure to document your protocol completely in the program writeup.
 
Your server application should listen for requests from bank clients, process the client and return the result. After handling a client the server should then listen for more requests. The server application should be designed so that it will never exit as a result of a client user action. You should also be prepared for multiple requests arriving from different clients at the same time.
 
Your server will "store" the password along with the username. I'm asking you to keep it simple. Use a simple, in memory record format. You do not need to use a database.
 
I'm asking you to implement the MD5 hash. However, you don't have to write the code. There are numerous versions already implemented that you should use. For instance, the RFC has one. You can also find example code here. However you choose to implement it, you must include a citation in your source code indicating where your MD5 came from. Note that you don't have to fully understand the MD5 algorithms to be able to use this in your program! You just need a function that will calculate the hash from your string.
 
Focus on limited functionality that is fully implemented. Practice defensive programming as you parse the data arriving from the other end. Again, don't focus on a powerful database or a fancy GUI, focus on the protocol and data exchange and make sure that you deal gracefully with whatever you or the TAs might throw at it.

Grading

The grading will be divided as follows:
20% Documentation
40% TCP Implementation
40% UDP Implementation
 
Notes:
You are implementing two complete, separate versions of this assignment: a client and server using TCP and another client and server using UDP. Your final submission should include 4 separate executables. For simplicity in grading, let's call them remotebank-tcp, server-tcp, remotebank-udp and server-udp. 
Your TCP and UDP versions will share much of the same code for command line parsing, password storage and user interaction. However, your UDP implementation will have to deal with lost request messages. Make sure you consider what happens when a message is lost. You will need to handle this in some way such as using a timer to retransmit your request. A good way to test your client in this situation is to run it without the server running. Does your client handle this gracefully?
You should include a "-d" command line option that enables debugging messages to be printed. Such as the client printing "Sending authentication request to server <IP> <port>", "Retransmitting request after <timeout>", "Sending username <username> and hash <hash> to server",  "Sending random string <string> to client" etc.
A portion of your grade will come from the TA running your program on one of the sytems available  in the CoC.  You must clearly indicate in your README and Makefile on which platform you want to be graded!
Make sure that you do sufficient error handling such that a user can't crash your server. For instance, what will you do if a user provides invalid input?
You must test your program and convince me it works! Provide me with a sample output (you can use script or cut-and-paste) that shows all of your functions working. If you fail to demonstrate a capability of the program I will assume that feature does not work. 
Program Submission Instructions:
You will turn in your well-documented source code, a README file and a sample output file calledSample.txt. The README file must contain:
Your Name and email address
Class name, section, date and assignment title
Names and descriptions of all files submitted
Detailed instructions for compiling and running your client and server programs including a Makefile.
A description of your application protocol (1/2 to 1 page) with sufficient detail such that somebody else could implement your protocol
Any known bugs, limitations of your design or program
You will be graded on the correctness of the code and its readability and structure.  As you write the documentation, imagine that I intend to implement your protocol in my own client that will interoperate with your server. Make sure I don't have to read your source code to figure out how to do this.
 
By 11:55 PM on the due date, you must submit your program files online.
Create a ZIP archive of your entire submission.
Use T-Square to submit your complete submission package as an attachment.
