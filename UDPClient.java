import java.io.*;
import java.net.*;

class UDPClient
{
   public static void main(String args[]) throws Exception
   {
      if (args.length != 5) { // check number of arguments
         System.out.println("Wrong number of inputs. 5 inputs are required.");
         System.out.println("Here is an example:\nIPAddress:Port username password action amount");
         System.exit(0);                           // exit program if incorrect number of arguments passed
      }

      // creating variables for each of the arguments
      String ipPort = args[0];
      String username = args[1];
      String password = args[2];
      String action = args[3];
      String amount = args[4];

      // handling of incorrect IP and port
      if (ipPort.indexOf(":") == -1) {
         System.out.println("You have not specified a port number.");
         System.out.println("The first argument must be an IP number colon Port number");
         System.out.println("Here is an example: 127.0.0.1:8591");
         System.exit(0);
      }

      // parsing the IP address
      String hi = "127.0.0.1";
      String part1 = hi.substring(0,hi.indexOf("."));
      if (hi.length() > 0)
         hi = hi.substring(hi.indexOf(".") + 1);
      else {
         System.out.println("incorrect IP");
         System.exit(0);
      }
      String part2 = hi.substring(0,hi.indexOf("."));
      if (hi.length() > 0)
         hi = hi.substring(hi.indexOf(".") + 1);
      else {
         System.out.println("incorrect IP");
         System.exit(0);
      }
      String part3 = hi.substring(0,hi.indexOf("."));
      if (hi.length() > 0)
         hi = hi.substring(hi.indexOf(".") + 1);
      else {
         System.out.println("incorrect IP");
         System.exit(0);
      }
      String part4 = hi;
      byte[] b = {(byte)Integer.parseInt(part1),(byte)Integer.parseInt(part2),
         (byte)Integer.parseInt(part3),(byte)Integer.parseInt(part4)};

      BufferedReader inFromUser =
         new BufferedReader(new InputStreamReader(System.in));
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
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);
      clientSocket.close();
   }
}
