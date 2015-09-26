import java.io.*;
import java.net.*;
import java.util.Random;
import java.security.*;

class server_udp {
  public static boolean isDebugMode;
  public static void main(String args[]) throws Exception {

    checkArgNum(args);

    int portNum = getPort(args[0]);
    String debugStr = args[args.length-1];
    isDebugMode = (debugStr.equals("-d"));
    System.out.println("Debug mode set to: " + isDebugMode);

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

      if (validMessage(obj[0])){    // checks if valid
        isAuth = getMessageType(obj[0]);    // gets what type of message it is
      } else {
        continue;
      } 
      // I hate casting these objects, but i don't know a better solution
      try {
        if (isAuth == 0){ // AUTH = 0
          String responseToAuth = "<randstr>" + String.valueOf(getRandomString())+"<end>";  // creates a response with a random string of characters
          if (isDebugMode) {
            System.out.println("Sending random string to client\t" + responseToAuth);
          }
          sendData(sock, responseToAuth, (InetAddress) obj[1], (int) obj[2]);   //  sends the response
        } else if (isAuth == 1){ // AUTH = 1
          String[] responses = parseHash((String)obj[0]); //responses is as follows:
          // responses[0] is the randomString used for indexng
          // responses[1] is the username
          // responses[2] is the hash
          // responses[3] is the mode (deposit or withdraw)
          // responses[4] is the amount for change
          String thisPass = getPass(users, responses[1]);       // gets password of user
          String challenge = hash(responses[1],thisPass,responses[0]);    // hashes the response
          boolean passes = challenge.equals(responses[2]);      // checks if the hash returned is correct
          String responseToChall = "<bal>";
          if (passes) {     // correct hash
            if (!verifyInputs(responses[3],responses[4])) {   // checks if mode and amount are correct
              responseToChall = "Improper mode" + responseToChall + "<end>";   // adds improper mode
              sendData(sock, "responseToChall", (InetAddress) obj[1], (int) obj[2] );   //  sends the final confimration
              if (isDebugMode)
                System.out.println("Sending rejection to client \t" + responseToChall);
              continue;
            }
            responseToChall = "yes" + responseToChall;    // adds yes
            int balance = getNewBalance(users, responses[1], responses[3], responses[4]);   //gets new balance
            responseToChall = responseToChall + balance + "<end>";      // adds balance and <end>
            if (isDebugMode)
              System.out.println("Sending confirmation to client \t" + responseToChall);
            sendData(sock, responseToChall, (InetAddress) obj[1], (int) obj[2] );   //  sends the final confimration
          } else {      // inccorect hash
            responseToChall = "no" + responseToChall + "<end>";   // adds no
            if (isDebugMode)
              System.out.println("Sending rejection to client \t" + responseToChall);
            sendData(sock, responseToChall, (InetAddress) obj[1], (int) obj[2]);    // sends no
          }

        } else {  // AUTH = 2
          continue;   // invalid input
        }

      } catch (Exception e) {
            // messed up start over
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
    byte[] sendData = new byte[1024];
    sendData = data.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
    sock.send(sendPacket);
  }

  /*
  * validMessage method
  * @param  object a  -   a string
  * @return boolean   -   true if the string has <
  *
  */
  public static boolean validMessage(Object a) {
    if ( (a != null) && ( ( (String) a).indexOf("<") != -1) ) {
      return true;
    } else {
      System.out.println("Incorrect message recieved.");
      return false;
    }
  }

  /*
  * getMessageType method
  * @param  a   -   a string
  * @return int   -   0 is auth request, 1 if challresponse, 2 otherwise
  *
  */
  public static int getMessageType(Object a) {
    String in = (String) a;
    if (in.indexOf("<ar>") != -1) {
      if (isDebugMode)
        System.out.println ("Authentication message recieved");
      return 0;
    } else if (in.indexOf("<endchar>")!=-1 && in.indexOf("<enduser>")!=-1
        && in.indexOf("<mode>")!=-1 && in.indexOf("<amount>")!=-1 ) {
      if (isDebugMode)
        System.out.println("Challenge response message recieved.");
      return 1;
    }
    System.out.println("Incorrect message recieved.");
    return 2;
  }

  /*
  * parseHash method
  * @param  in  -   the challengeResponse that needs to be parsed
  * @return String[]  -   each of the parts of the response in an array
  *
  */
  public static String[] parseHash(String in) {
    String [] parts = new String[5];
    parts[0] = in.substring(0,in.indexOf("<endchar>"));
    parts[1] = in.substring(in.indexOf("<endchar>")+9,in.indexOf("<enduser>"));
    parts[2] = in.substring(in.indexOf("<enduser>")+9,in.indexOf("<mode>"));
    parts[3] = in.substring(in.indexOf("<mode>")+6, in.indexOf("<amount>"));
    parts[4] = in.substring(in.indexOf("<amount>")+8, in.indexOf("<end>"));
    return parts;
  }

  /*
  * getPass method
  * @param  allUsers  -  an array of all users
  *         user    -   a string with the username of hte one we want to find
  * @return String   -    the password of user
  */
  public static String getPass(User[] allUsers, String user) {
    for (User a : allUsers) {
      if (a.getUser().equals(user)) {
        return a.getPass();
      }
    }
    return "";
  }

  /*
  * verifyInputs method
  * @param  a   -   String that is mode
  * @param  b   -   String that is amount
  * @return true  -   if inputs are valid
  *         false -   otherwise
  */
  public static boolean verifyInputs(String a, String b) {
    if (!(a.equalsIgnoreCase("Deposit") || a.equalsIgnoreCase("Withdraw"))) {
      System.out.println("Improper mode");
      return false;
    }
    try {
      Integer.parseInt(b);
    } catch (NumberFormatException e) {
      System.out.println("amount entered is not a number");
      return false;
    }
    return true;
  }

  /*
  * getNewBalance method
  * @param  allUsers  - array of all users
  *         name      - username of specific user
  *         a         - mode
  *         b         - amount
  * @return   int   -   new balance of user
  */
  public static int getNewBalance (User[] allUsers, String name, String a, String b) {
    int amount = Integer.parseInt(b);
    if (a.equalsIgnoreCase("deposit")) {
      for (User user : allUsers) {
        if (user.getUser().equals(name)) {
          user.addMoney(amount);
          return user.getBalance();
        }
      }
    } else {
      for (User user : allUsers) {
        if (user.getUser().equals(name)) {
          user.addMoney(-amount);
          return user.getBalance();
        }
      }
    }
    return 0;
  }

  /*
  * hash method
  * @param  user    -     String of username
  *         pass    -     string of password
  *         challenge -   string of random characters
  * @return String    -   MD5 hash of all parameters
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
  * initUsers method
  * @return   User[] - an array of users including
  *                     Jack, Sam, and Amy
  *                     with passwords and balances
  */
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

  // Inner class user
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

    public int getBalance() {
      return balance;
    }

    public String toString() {
      return (user + "\n" + pass + "\n" + balance);
    }
  }
}
