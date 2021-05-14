import java.net.*;

public class client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            //Set up variables to be used
            String[] bestServer = {""};
            String currentMsg = "";
            handshaker.handshake(s);

            // While there are more jobs to be done
            while (!currentMsg.contains("NONE")) {
                // Tells the server that the client is ready for a command and reads it
                msgWriter.sendMsg(s, "REDY\n");
                currentMsg = msgReader.readMsg(s);
                
                // Checks to see if the received command is a new job
                if (currentMsg.contains("JOBN")) {


                    String[] JOBNSplit = currentMsg.split(" ");
                    //Ask what servers are available to run a job with the given data
                    msgWriter.sendMsg(s, "GETS Avail " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                    //Reads the msg saying what data is about to be sent and responds with "OK"
                    currentMsg = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");

                    // Reads the available servers data and responds with "OK"
                    currentMsg = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");
                    //System.out.println(currentMsg);
                    bestServer = bestFitFinder.bestFit(currentMsg, JOBNSplit[4], s);
                   // System.out.println("Outside bestServer");
                    /*if(bestServer.length < 2){
                        currentMsg = msgReader.readMsg(s);
                        msgWriter.sendMsg(s, "GETS Capable " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                        currentMsg = msgReader.readMsg(s);
                        msgWriter.sendMsg(s, "OK\n");
                        currentMsg = msgReader.readMsg(s);
                        msgWriter.sendMsg(s, "OK\n");

                        bestServer = bestFitFinder.closestFit(currentMsg, JOBNSplit[4]);
                    }*/


                    //Reads "." from the server
                    currentMsg = msgReader.readMsg(s);

                    //Schedule the current job to the biggest server (SCHD JobNumber ServerName ServerNumber)
                    msgWriter.sendMsg(s, "SCHD " + JOBNSplit[2] + " " + bestServer[0] + " " + bestServer[1] + "\n");
                  //  System.out.println("SCHD " + JOBNSplit[2] + " " + bestServer[0] + " " + bestServer[1]);
                    //Read the next JOB
                    currentMsg = msgReader.readMsg(s);
                    //System.out.println("SCHD: " + currentMsg);
                }
                else if (currentMsg.contains("DATA")) {
                    msgWriter.sendMsg(s, "OK\n");
                }
            }
            // Sends "Quit" to the server to end the session and then closes the socket
            msgWriter.sendMsg(s, "QUIT\n");
            s.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

