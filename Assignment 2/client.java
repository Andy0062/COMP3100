
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            //Set up variables to be used
            String[] biggestServer = {""};
            String[] serverCost = {""};
            boolean biggestFound = false;
            String currentMsg = "";
            handshake(s);

            //Read first job (Which can be re-read) and gets all to put all servers in cost order
       /*     sendMsg(s, "REDY\n");
            currentMsg = readMsg(s);
            sendMsg(s, "GETS All\n");
            sendMsg(s, "OK\n");
            currentMsg = readMsg(s);
            System.out.println("currentMsg: " + currentMsg);
            serverCost = costOrder(currentMsg);
            sendMsg(s, "OK\n");*/


            // While there are more jobs to be done
            while (!currentMsg.contains("NONE")) {
                // Tells the server that the client is ready for a command and reads it
                sendMsg(s, "REDY\n");
                currentMsg = readMsg(s);
                
                // Checks to see if the received command is a new job
                if (currentMsg.contains("JOBN")) {


                    String[] JOBNSplit = currentMsg.split(" ");
                    //Ask what servers are available to run a job with the given data
                    sendMsg(s, "GETS Avail " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                    //Reads the msg saying what data is about to be sent and responds with "OK"
                    currentMsg = readMsg(s);
                    sendMsg(s, "OK\n");

                    // Reads the available servers data and responds with "OK"
                    currentMsg = readMsg(s);
                    sendMsg(s, "OK\n");









                    //Checks to see if the biggest Server has been found
                    //Used as a flag to see that it was found on the first round
                   /* if(biggestFound == false){
                        biggestServer = findBiggestServer(currentMsg);
                        biggestFound = true;
                    }*/

                    biggestServer = bestFit(currentMsg, JOBNSplit[4]);
                    if(biggestServer.length < 2){
                        currentMsg = readMsg(s);
                        sendMsg(s, "GETS Capable " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                        currentMsg = readMsg(s);
                        sendMsg(s, "OK\n");
                        currentMsg = readMsg(s);
                        sendMsg(s, "OK\n");

                        biggestServer = bestFit(currentMsg, JOBNSplit[4]);
                    }






                    //Reads "." from the server
                    currentMsg = readMsg(s);

                    //Schedule the current job to the biggest server (SCHD JobNumber ServerName ServerNumber)
                    sendMsg(s, "SCHD " + JOBNSplit[2] + " " + biggestServer[0] + " " + biggestServer[1] + "\n");

                    //Read the next JOB
                    currentMsg = readMsg(s);
                    System.out.println("SCHD: " + currentMsg);
                }
                else if (currentMsg.contains("DATA")) {
                    sendMsg(s, "OK\n");
                }
            }
            // Sends "Quit" to the server to end the session and then closes the socket
            sendMsg(s, "QUIT\n");
            s.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // Function used to read a msg from the server
    public static synchronized String readMsg(Socket s) {
        String currentMsg = "FAIL";
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            byte[] byteArray = new byte[dis.available()];
            // Reset byteArray to have 0 elements so it is ready to recieve
            // a msg and wait until a msg is recieved
            byteArray = new byte[0];
            while (byteArray.length == 0) {
                // Read the bytestream from the server
                byteArray = new byte[dis.available()];
                dis.read(byteArray);
                // Make a string using the recieved bytes and print it
                currentMsg = new String(byteArray, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Return the msg just recieved from the server
        return currentMsg;
    }

    // Function used to send a msg to the server
    public static synchronized void sendMsg(Socket s, String currentMsg) {
        try {
            //Converts the String msg to an array of bytes and sends them to the server
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            byte[] byteArray = currentMsg.getBytes();
            dout.write(byteArray);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handshake(Socket s) {
        String currentMsg = "";

        // Initiate handshake with server
        sendMsg(s, "HELO\n");

        // Check for response from sever for "HELO"
        currentMsg = readMsg(s);
        System.out.println("RCVD: " + currentMsg);

        // Authenticate with a username (ubuntu)
        sendMsg(s, "AUTH " + System.getProperty("user.name") + "\n");

        // Check to see if sever has ok'd the client's AUTH
        currentMsg = readMsg(s);
        System.out.println("RCVD: " + currentMsg);
    }

    // Used to find the biggest server available to run the current job
    public static String[] findBiggestServer(String currentMsg) {
        // All the servers in the currentMsg being split into an array
        String[] serversAndInfo = currentMsg.split("\n");
        //Sets up variables
        int mostCores = 0;
        String[] currentServer = {""};
        //Searches for the most cores a server holds in the given available servers
        for (int i = 0; i < serversAndInfo.length; i++) {
            currentServer = serversAndInfo[i].split(" ");
            int currentCores = Integer.valueOf(currentServer[4]);
            if (currentCores > mostCores) {
                mostCores = currentCores;
            }
        }
        //Finds and returns the biggest server (The one with the most cores)
        for (int i = 0; i < serversAndInfo.length; i++) {
            currentServer = serversAndInfo[i].split(" ");
            int currentCores = Integer.valueOf(currentServer[4]);
            if (currentCores == mostCores) {
                return currentServer;
            }
        }
        return currentServer;

    }

    public static String[] costOrder(String currentMsg){
        String[] servers = currentMsg.split("\n");
        String[] cheapestServer = {""};


        for (int i = 0; i < servers.length; i++){
            String[] currentServer = servers[i].split(" ");
            for(int j = 0; j < currentServer.length; j++){
                System.out.println(currentServer[i]);
            }
            System.out.println("Name: " + currentServer[0]);
            //System.out.println("Hourly Rate: " + currentServer[5]);
            System.out.println(" ");
        }
        return servers;
    }

    public static String[] bestFit(String currentMsg, String cores){
        String[] bestFitServer = {""};
        String[] servers = currentMsg.split("\n");
        int coreValue = -1; //Server Core Count - Cores required
        for (int i = 0; i < servers.length; i++){
            if(!servers[i].contains(".")){
                System.out.println("currentServer: " + servers[i]);
                String[] currentServer = servers[i].split(" ");
                int currentServerCores = Integer.parseInt(currentServer[4]);
                int coreScore = currentServerCores - Integer.parseInt(cores);
                if(coreValue == -1){
                    bestFitServer = currentServer;
                    coreValue = coreScore; 
                }
                else{
                    if(coreScore < coreValue){
                        bestFitServer = currentServer;
                        coreValue = coreScore; 
                    }
                }
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("YEET");
        System.out.println();
        System.out.println("bestFitServer: " + bestFitServer[0]);
        System.out.println();
        System.out.println();

        return bestFitServer;

    }




}

