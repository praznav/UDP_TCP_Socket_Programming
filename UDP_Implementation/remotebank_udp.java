import java.io.*;
import java.net.*;
import java.security.*;

class remotebank_udp {
  public static void main(String args[]) throws Exception {
    // checks to see if there are 5 arguments
    checkArgNum(args);

    // creating variables for each of the arguments
    String ipPort = args[0];
    String username = args[1];
    String password = args[2];
    String action = args[3];
    String amount = args[4];
    String debugStr = args[args.length-1];
    boolean isDebugMode = (debugStr.equals("-d"));
    System.out.println("Debug mode set to: " + isDebugMode);

    // handling of incorrect IP and port
    checkColon(ipPort);

    // get ip address
    String ip = ipPort.substring(0,ipPort.indexOf(":"));     
    String portStr = ipPort.substring(ipPort.indexOf(":") + 1); // get port number

    // parsing the IP address
    byte[] b = splitIP(ip);
    int portNum = getPort(portStr);   // convert string to int


    DatagramSocket clientSocket = new DatagramSocket();   // open socket
    clientSocket.setSoTimeout(10000);     // set timeout to 10 seconds
    // make IPAddress object
    InetAddress IPAddress = null;
    try {
      IPAddress= InetAddress.getByAddress(b);
    } catch (UnknownHostException e) {
      System.out.println("Improper IP input.");
      System.out.println(e.getMessage());
      System.exit(0);
    }

    boolean notDone = true;   // control boolean
    while (notDone) {
      try {
        sendData(clientSocket,"<ar>", IPAddress, portNum);    // send Authentication request
        if (isDebugMode)
          System.out.println("Sending authentication request to server " + ipPort);

        String newIn;   // string for input 
        try {
          newIn = getData(clientSocket);
          if (isDebugMode)
            System.out.println("Recieved response to authentication.");
        } catch (SocketTimeoutException e) {
          System.out.println("Timeout on response. Starting over");
          continue;
        }

        // make sure that the response is correct form
        if (!validateHash(newIn)) {
            System.out.println("Improper response from server. Ending.");
            System.exit(0);
        }
        newIn = newIn.substring(newIn.indexOf("<randstr>")+9,newIn.indexOf("<end>")); //  parse input to get random string.
        String hashedString = hash(username, password, newIn);  // get hash
        // create response
        String modifiedSentence = newIn + "<endchar>" + username + "<enduser>" + hashedString + "<mode>" +  action + "<amount>" + amount + "<end>";
        sendData(clientSocket, modifiedSentence, IPAddress, portNum); // send response
        if (isDebugMode)
          System.out.println("Sending username " + username + " and hash " + hashedString + " to server");

        String lastIn;  // last input
        try {
          lastIn = getData(clientSocket);   // get input
          if (isDebugMode)
            System.out.println("Recieved confirmation or rejection from server.");
        } catch (SocketTimeoutException e) {
          System.out.println("Timeout on response. Starting over");
          continue;
        }

        // check if the last input is a success
        if (isSuccess(lastIn)) {    // SUCCESS
          System.out.println("Success!");
          System.out.println("New Balance is: " + getBalance(lastIn));
        } else {    // FAILURE
          System.out.println("Failure.");
        }
        notDone = false;    // exit the loop
      } catch (Exception e) {     // HANDLES ALL UNEXPECTED ERRORS
        System.out.println(e.getMessage());
        System.out.println("There was an error; starting over.");
        continue;
      }
    }
    clientSocket.close();   // end
  }



  // helper methods


  /*
* sendData method
* @param  sock     -    DatagramSock object to send info over
*         data     -    String of data to send
*         IPAddress -   ip address to send to
*         port      -   int of which to send to
*
*/
  public static void sendData(DatagramSocket sock, String data, InetAddress IPAddress, int port) throws IOException {
    byte[] sendData = new byte[1024];
    sendData = data.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
    sock.send(sendPacket);
  }

  /*
* getData method
* @param sock  -   DatagramSocket object to receive info over
* @return String    -   recieved message
*
*/
  public static String getData(DatagramSocket sock) throws IOException {

    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    sock.receive(receivePacket);
    String response = new String(receivePacket.getData());
    return response;
  }

  /*
* SplitIP method
* @param: ip    -   the full ip address
* @return: byte[]   -   the ip address with each section split into bytes array
*/
  public static byte[] splitIP(String ip) {

    String part1 = ip.substring(0,ip.indexOf("."));
    if (ip.length() > 0)
    ip = ip.substring(ip.indexOf(".") + 1);
    else {
      System.out.println("incorrect IP");
      System.exit(0);
    }
    String part2 = ip.substring(0,ip.indexOf("."));
    if (ip.length() > 0)
    ip = ip.substring(ip.indexOf(".") + 1);
    else {
      System.out.println("incorrect IP");
      System.exit(0);
    }
    String part3 = ip.substring(0,ip.indexOf("."));
    if (ip.length() > 0)
    ip = ip.substring(ip.indexOf(".") + 1);
    else {
      System.out.println("incorrect IP");
      System.exit(0);
    }
    String part4 = ip;
    try {
      byte[] b = {(byte)Integer.parseInt(part1),(byte)Integer.parseInt(part2),
        (byte)Integer.parseInt(part3),(byte)Integer.parseInt(part4)};
      return b;
    } catch (NumberFormatException e) {
      System.out.println(e.getMessage());
      System.out.println("IP not completely numerical.");
      System.exit(0);
    }

    return null;
  }

  /*
* checkArgNum method
* @param: args   -  the arguments of the program when it was run
*
* error message and exits program if the argument length isn't 5
*/
  public static void checkArgNum(String args[]) {
    if (args.length != 5 && args.length != 6) { // check number of arguments
      System.out.println("Wrong number of inputs. 5 inputs are required.");
      System.out.println("Here is an example:\nIPAddress:Port username password action amount");
      System.out.println("Add a -d at end to enter debug mode. The -d must be the last argument.");
      System.exit(0);                           // exit program if incorrect number of arguments passed
    }
  }

  /*
* checkColon method
* @param: ipPort    -  the ip address and port seperated with a :
*
* checks if there is a : and checks if there is some sort of port number
* if not, prints error message and exits
*/
  public static void checkColon(String ipPort) {
    // colon exists for IP and port
    if (ipPort.indexOf(":") == -1) {
      System.out.println("You have not specified a port number.");
      System.out.println("The first argument must be an IP number colon Port number");
      System.out.println("Here is an example: 127.0.0.1:8591");
      System.exit(0);
    }

    // error handling for missing port number
    if (ipPort.indexOf(":") == ipPort.length()) {
      System.out.println("There is no Port Number.");
      System.out.println("Here is an example: 127.0.0.1:8591");
      System.exit(0);
    }

  }

  /*
* getPort method
* @param: port is the port number in string form
* @return: integer form of port num
* error message and exits if there is a problem.
*/
  public static int getPort(String port) {
    try {
      return Integer.parseInt(port);
    } catch (NumberFormatException e) {
      System.out.println(e.getMessage());
      System.out.println("Port number not completely numerical.");
      System.exit(0);
    }
    return 0;
  }

  /*
* hash method
* @param   user  -   username
*          pass  -   password
*          challenge   -   random string
* @return    String  -   MD5 hash of parameters
*/
  public static String hash(String user, String pass, String challenge) {
    user = user + pass + challenge;
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (Exception e) {   // never gonna throw exception

    }
    byte[] theDigest = md.digest(user.getBytes());
    return new String(theDigest);
  }

  /*
* isSuccess method
* @param     in  -   String input from socket
* @return    true  -   if the input contains <bal>, <end>, and yes
*            false -   otherwise
*/
  public static boolean isSuccess(String in) {
    if (in != null && in.length()!=0 && in.indexOf("<bal>")!=-1 && in.indexOf("<end>")!=-1 && (in.substring(0,in.indexOf("<bal>")).equals("yes") ) ) {
      return true;
    }
    return false;
  }

  /*
* getBalance method
* @param       in    -   input from socket
* @return      String  -   parsed balance
*
*/
  public static String getBalance(String in) {
    return in.substring(in.indexOf("<bal>")+5,in.indexOf("<end>"));
  }

/*
* validateHash method
* @param       in    -   input from socket
* @return      boolean  -   true if contains randstr and end
*                           false otherwise
*/
  public static boolean validateHash(String in) {
    return (in.indexOf("<randstr>") != -1 && in.indexOf("<end>") != -1);
  }
}