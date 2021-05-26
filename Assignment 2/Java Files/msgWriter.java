import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class msgWriter {
    // Function used to send a msg to the server
    public static synchronized void sendMsg(Socket s, String currentMsg) {
        try {
            //Converts the String msg to an array of bytes and sends them to the server
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            byte[] byteArray = currentMsg.getBytes();
            dout.write(byteArray);
            //Empties the ouput stream so it is ready to send something else
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
