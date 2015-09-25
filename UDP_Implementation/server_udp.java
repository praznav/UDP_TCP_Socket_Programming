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

      DatagramSocket sock = new DatagramSocket(portNum);

      while(true) {
          Object[] obj = new Object[3];
          try {
            obj = getData(sock); // obj is as follows:
                                        // obj[0] is message
                                        // obj[1] is IPAddress
                                        // obj[2] is port
          } catch (IOException e) {
              System.out.println (e.getMessage());
              System.out.println ("Something went wrong in recieving the message");
              continue; // not elegant :P
          }

          int isAuth = 2;
          // isAuth = 0 when message is auth request
          // isAuth = 1 when message is includes a hash and username
          // isAuth = 2 when it is unreadable (not in the top 2)

          if (validMessage(obj[0])){
              isAuth = getMessageType(obj[0]);
          } else {
              continue;
          } 
          // I hate casting these objects, but i don't know a better solution
          try {
              if (isAuth == 0){ // AUTH = 0
                  String responseToAuth = "<RandStr>" + String.valueOf(getRandomString())+"<End>";
                  sendData(sock, responseToAuth, (InetAddress) obj[1], (int) obj[2]);
              } else if (isAuth == 1){ // AUTH = 1
		  String[] responses = parseHash((String)obj[0]);	//responses is as follows:
		  			// responses[0] is the randomString used for indexng
		  			// responses[1] is the username
		  			// responses[2] is the hash
		  			// responses[3] is the mode (deposit or withdraw)
		  			// responses[4] is the amount for change
		  String thisPass = getPass(users, responses[1]);



              } else {	// AUTH = 2
                continue;
              }

          } catch (Exception e) {

          }
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
    * getRandomString method
    * @return: char[]   -   64 character string of random letters
    *
    */
   public static char[] getRandomString() {
      int size = 64;
      String chars = "abcdefghijklmnopqrstuvwxyz";
      Random rand = new Random();
      char[] text = new char[size];
      for (int i = 0; i < size; i++) {
          text[i] = chars.charAt(rand.nextInt(26));
      }
      return text;
   }

   /*
    * getData method
    * @param sock  -   DatagramSocket object to receive info over
    * @return Object[]   -   size 3 array with recieved message, 
    *                         IPAddress, and port number
    *
    */
   public static Object[] getData(DatagramSocket sock) throws IOException {
      Object[] obj = new Object[3];

      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      sock.receive(receivePacket);
      String response = new String(receivePacket.getData());
      System.out.println("RECEIVED: " + response);
      InetAddress IPAddress = receivePacket.getAddress();
      int port = receivePacket.getPort();  
      obj[0] = response;
      obj[1] = IPAddress;
      obj[2] = port;
      return obj;
   }


   /*
    * sendData method
    * @param  sock     -    DatagramSock object to send info over
    *         data     -    String of data to send
    *         IPAddress -   ip address to send to
    *         port      -   int of which to send to
    *
    */
   public static void sendData(DatagramSocket sock, String data, InetAddress IPAddress, int port) throws IOException {
      String capitalizedSentence = data.toUpperCase();
      byte[] sendData = new byte[1024];
      sendData = capitalizedSentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
      sock.send(sendPacket);
   }

   public static boolean validMessage(Object a) {
        if ( (a != null) && ( ( (String) a).indexOf("<") != -1) ) {
            return true;
        } else {
            System.out.println("Incorrect message recieved.");
            return false;
        }
   }

   public static int getMessageType(Object a) {
        String in = (String) a;
        if (in.indexOf("<AR>") != -1) {
            return 0;
        } else if (in.indexOf("<EndChar>")!=-1 && in.indexOf("<EndUser>")!=-1
          && in.indexOf("<Mode>")!=-1 && in.indexOf("<Amount>")!=-1 ) {
            return 1;
        }
        System.out.println("Incorrect message recieved.");
        return 2;
   }

   public static String[] parseHash(String in) {
	String [] parts = new String[5];
	parts[0] = in.substring(0,in.indexOf("<EndChar>"));
	parts[1] = in.substring(in.indexOf("<EndChar>")+9,in.indexOf("<EndUser>"));
	parts[2] = in.substring(in.indexOf("<EndUser>")+9,in.indexOf("<Mode>"));
	parts[3] = in.substring(in.indexOf("<Mode>")+6, in.indexOf("<Amount>"));
	parts[4] = in.substring(in.indexOf("<Amount>")+8);
	return parts;
   }

   public static String getPass(User[] allUsers, String user) {
	for (User a : allUsers) {
		if (a.getUser().equals(user)) {
			return a.getPass();
		}
	}
	return "";
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
