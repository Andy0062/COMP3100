import java.net.Socket;

public class handshaker {
    public static void handshake(Socket s) {
        String currentMsg = "";

        // Initiate handshake with server
        msgWriter.sendMsg(s, "HELO\n");

        // Check for response from sever for "HELO"
        currentMsg = msgReader.readMsg(s);
        System.out.println("RCVD: " + currentMsg);

        // Authenticate with a username (ubuntu)
        msgWriter.sendMsg(s, "AUTH " + System.getProperty("user.name") + "\n");

        // Check to see if sever has ok'd the client's AUTH
        currentMsg = msgReader.readMsg(s);
        System.out.println("RCVD: " + currentMsg);
    }
}
