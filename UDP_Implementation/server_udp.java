import java.io.*;
import java.net.*;
import java.util.Random;

class server_udp
{
   public static void main(String args[]) throws Exception {

      checkArgNum(args);

      int portNum = getPort(args[0]);
      String debugStr = args[args.length-1];
      boolean isDebugMode = (debugStr.equals("-d"));

      User[] users = initUsers();

      DatagramSocket serverSocket = new DatagramSocket(portNum);
      byte[] receiveData = new byte[1024];
      byte[] sendData = new byte[1024];


      while(true)
         {
            receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
            new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
         }

   }

   // helper methods

   /*
    * checkArgNum method
    * @param: args   -  the arguments of the program when it was run
    *
    * error message and exits program if the argument length isn't 5
    */
   public static void checkArgNum(String args[]) {
      if (args.length != 1 && args.length != 2) { // check number of arguments
         System.out.println("Wrong number of inputs. 1 input are required. Input the port number.");
         System.exit(0);                           // exit program if incorrect number of arguments passed
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
    *
    *
    *
    */
   public static char[] getRandomString() {
      int size = 64;
      String chars = "abcdefghijklmnopqrstuvwxyz";
      Random rand = new Random();
      char[] text = new char[size];
      for (int i = 0; i < size; i++) {
          text[i] = chars.charAt(rand.nextInt(size));
      }
      return text;
   }

   // Inner class for User
   public static User[] initUsers() {
      User[] users = new User[3];
      User jack = new server_udp().new User("Jack", "123", 10);
      User sam = new server_udp().new User("Sam", "abc", 20);
      User amy = new server_udp().new User("Amy", "pass", 30);
      users[0] = jack;
      users[1] = sam;
      users[2] = amy;
      return users;
   }

   class User {
      String user;
      String pass;
      int balance;
      public User (String u, String p, int bal) {
          user = u;
          pass = p;
          balance = bal;
      }

      public void addMoney (int add) {
          balance += add;
      }

      public String getUser() {
          return user;
      }

      public String getPass() {
          return pass;
      }

      public String toString() {
        return (user + "\n" + pass + "\n" + balance);
      }
   }
}