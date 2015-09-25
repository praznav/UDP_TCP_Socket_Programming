import java.io.*;
import java.net.*;

class remotebank_udp
{
   public static void main(String args[]) throws Exception
   {
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
      int portNum = getPort(portStr);




      //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      //InetAddress IPAddress = InetAddress.getByName("localhost");
      InetAddress IPAddress = null;
      try {
          IPAddress= InetAddress.getByAddress(b);
      } catch (UnknownHostException e) {
         System.out.println(e.getMessage());
         System.exit(0);
      }

      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];

      //String sentence = inFromUser.readLine();
      //sendData = sentence.getBytes();
      sendData = "AR".getBytes();

      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNum);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);

      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);

      clientSocket.close();
   }


   // helper methods

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

}
