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

      String ip = ipPort.substring(0,ipPort.indexOf(":")); // get ip address
      // error handling for missing port number
      if (ipPort.indexOf(":") == ipPort.length()) {
          System.out.println("There is no Port Number.");
	  System.exit(0);
      }
      String port = ipPort.substring(ipPort.indexOf(":")); // get port number
      // parsing the IP address
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
