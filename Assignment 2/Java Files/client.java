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
                if (currentMsg.contains("JOBN") || currentMsg.contains("JOBP")) {


                    String[] JOBNSplit = currentMsg.split(" ");
                    
                    //Ask what servers are available to run a job with the given data
                    msgWriter.sendMsg(s, "GETS Avail " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");


                    //Reads the msg saying what data is about to be sent and responds with "OK"
                    currentMsg = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");

                    // Reads the available servers data and responds with "OK"
                    currentMsg = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");
                    
                    //Requests for the best server to be found (using the bestFitFinder class)
                    bestServer = bestFitFinder.bestFit(currentMsg, JOBNSplit[4], s);

                    //Reads "." from the server
                    currentMsg = msgReader.readMsg(s);

                    //Checks to see if there is a valid server to be scheduled to
                    if(bestServer.length > 1){
                        //Schedule the current job to the biggest server (SCHD JobNumber ServerName ServerNumber)
                        msgWriter.sendMsg(s, "SCHD " + JOBNSplit[2] + " " + bestServer[0] + " " + bestServer[1] + "\n");
                    


                        //Read the next JOB
                        currentMsg = msgReader.readMsg(s);
                    }
                    //If the best Server has not been found (there are no available servers)
                    else{
                        //Ask what servers are available to run a job with the given data
                        msgWriter.sendMsg(s, "GETS Capable " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");

                        //Reads the msg saying what data is about to be sent and responds with "OK"
                        currentMsg = msgReader.readMsg(s);
                        msgWriter.sendMsg(s, "OK\n");

                        // Reads the available servers data and responds with "OK"
                        currentMsg = msgReader.readMsg(s);
                        msgWriter.sendMsg(s, "OK\n");

                        bestServer = bestFitFinder.bestFit(currentMsg, JOBNSplit[4], s);

                        //Schedule the current job to the biggest server (SCHD JobNumber ServerName ServerNumber)
                        msgWriter.sendMsg(s, "SCHD " + JOBNSplit[2] + " " + bestServer[0] + " " + bestServer[1] + "\n");
                    
                        //Read the next JOB
                        currentMsg = msgReader.readMsg(s);
                    }
                    

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

