
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class client {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            String currentMsg = "";
            boolean end = false;
            handshake(s);
   
            //Tells the server that the client is ready for a job
            //sendMsg(s, "REDY");
            //Get the server state information
         /*   sendMsg(s, "GETS All");
            //Obtains server state info
            currentMsg = readMsg(s);
            System.out.println("Server State Info: " + currentMsg);
            sendMsg(s, "OK");*/
            //While the simulation has not been told to end
            while(end == false){
                //Tells the server that the client is ready for a job
                sendMsg(s, "REDY\n");
                //Obtains  job from the server
                currentMsg = readMsg(s);
                //System.out.println("JOB: " + currentMsg);
                //Checks to see if the received command is "NONE" or "QUIT" which
                //ends the program
                if(currentMsg.contains("NONE") || currentMsg.contains("QUIT")){
                    end = true;
                    break;
                }
                //currentMsg is now a job that needs to be scheduled




                if(currentMsg.contains("JOBN")){
                    String[] JOBNSplit = currentMsg.split(" ");
                    /*
                    String command = JOBN;
                    int jobNumber
                    int submitTime = 0;
                    int jobID = 0;
                    int estRuntime = 0;
                    int core = 0;
                    int memory = 0;
                    int disk = 0;
                    */

                    
                    //See what servers are available
                    sendMsg(s, "GETS Capable " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                    String capableServers = readMsg(s);
                    sendMsg(s, "OK\n");
                    //Read the available servers data
                    currentMsg = readMsg(s);
                    String[] AvailSplit = currentMsg.split("\n");
                    sendMsg(s, "OK\n");
                    if(AvailSplit.length <= 0){
                        break;
                    }
                    sendMsg(s, "OK\n");
                    //See what servers are available
                    sendMsg(s, "GETS Avail " + JOBNSplit[4] + " " + JOBNSplit[5] + " " + JOBNSplit[6] + "\n");
                    
                    sendMsg(s, "OK\n");
                    //Read the available servers data
                    String availServers = readMsg(s);
                    AvailSplit = currentMsg.split("\n");
                    sendMsg(s, "OK\n");
                    if(AvailSplit.length <= 0){
                        break;
                    }



                    String[] firstFitServer = findFirstFitServer(capableServers, availServers);
                    System.out.println("Outside findFirstFitServer");
                    /*String[] chosenServer = currentMsg.split("\n");
                    for(int i = 0; i < chosenServer.length; i++){
                        System.out.println(i + " = " + chosenServer[i]);
                    }
                    for(int i =0; i < AvailSplit.length; i++){
                        String[] individualServerChecker = AvailSplit[i].split(" ");
                        if(individualServerChecker[2].contains("inactive")){
                            chosenServer = AvailSplit[i].split(" ");
                            i = AvailSplit.length;
                        }
                    }
                    
                   // currentMsg = readMsg(s);
                   // System.out.println("After Avail: " + currentMsg);



/*
                    //if(currentMsg == "."){
                      //  TimeUnit.SECONDS.sleep(1);
                    //}
                    //Schedules the job
                    int checker = 0;
                    for(int i = 0; i < AvailSplit.length/9; i++){
                        System.out.println("AvailSplit: " + AvailSplit[2 + (i*9)]);
                        if(!AvailSplit[2 + (i*9)].contains("boot")){
                            checker = i;
                            i = AvailSplit.length;
                        }
                        System.out.println();
                        System.out.println();
                        System.out.println("Booting Found");
                        System.out.println();
                        System.out.println();
                    }
                    System.out.println("Checker: " + checker);
                    System.out.println("AvailSplit[0]: " + AvailSplit[0]);
                    System.out.println("AvailSplit[1]: " + AvailSplit[1]);
                    System.out.println("AvailSplit[2]: " + AvailSplit[2]);
                    System.out.println("AvailSplit[3]: " + AvailSplit[3]);
                    System.out.println("AvailSplit[4]: " + AvailSplit[4]);
                    System.out.println("AvailSplit[5]: " + AvailSplit[5]);
                    System.out.println("AvailSplit[6]: " + AvailSplit[6]);
                    System.out.println("AvailSplit[7]: " + AvailSplit[7]);
                    System.out.println("AvailSplit[8]: " + AvailSplit[8]);
                    System.out.println("AvailSplit[9]: " + AvailSplit[9]);
                    System.out.println("AvailSplit[10]: " + AvailSplit[10]);
*/
                    for(int i = 0; i < firstFitServer.length; i++){
                        System.out.println("firstFitServer[" + i + "] = " + firstFitServer[i]);
                    }
                    System.out.println("End");


                    //SCHD JobNumber ServerName ServerNumber

                    sendMsg(s, "SCHD " + JOBNSplit[2] + " " + firstFitServer[0] + " " + firstFitServer[1] + "\n");
                    currentMsg = readMsg(s);
                   // System.out.println("SCHD: " + currentMsg);
                }

                else if(currentMsg.contains("DATA")){
                    sendMsg(s, "OK\n");
                }

                










            }

            //Sends "Quit" to the server to end the session
            sendMsg(s, "QUIT\n");
            //Closes the socket
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Function used to read a msg from the server
    public static synchronized String readMsg(Socket s){
        String currentMsg = "FAIL";
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            byte[] byteArray = new byte[dis.available()];
            //Reset byteArray to have 0 elements so it is ready to recieve
            //a msg and wait until a msg is recieved
            byteArray = new byte[0];
            while(byteArray.length == 0){
                //Read the bytestream from the server
                byteArray = new byte[dis.available()];
                dis.read(byteArray);
                //Make a string using the recieved bytes and print it
                currentMsg = new String(byteArray, StandardCharsets.UTF_8);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentMsg;
        
    }

    //Function used to send a msg to the server
    public static synchronized void sendMsg(Socket s, String currentMsg){
        try {
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            byte[] byteArray = currentMsg.getBytes();
            dout.write(byteArray);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public static void handshake(Socket s){
        String currentMsg = "";

        //Initiate handshake with server
        sendMsg(s, "HELO\n");
        
        //Check for response from sever for "HELO"
        currentMsg = readMsg(s);
        //System.out.println("RCVD: " + currentMsg);

        //Authenticate with a username (ubuntu)
        sendMsg(s, "AUTH " + System.getProperty("user.name") + "\n");

        //Check to see if sever has ok'd the client's AUTH
        currentMsg = readMsg(s);
        //System.out.println("RCVD: " + currentMsg);
    }

    //Used to find the biggest server available to run the current job
    public static String[] findFirstFitServer(String capableServers, String availServers){
        System.out.println("available set");
        String[] available = availServers.split("\n");
        String[] firstAvailable = {""};
        System.out.println("availServers: " + availServers);
        System.out.println("REEEEEEEEEEEEEEEE");
        System.out.println("available length: " + available.length);
        if(available.length >= 4){
            firstAvailable = available[4].split(" ");
            if(!available[4].contains(".")){
                return firstAvailable;
            }
        }


        System.out.println("capable set");
        String[] capable = capableServers.split("\n");
        String[] firstCapable = {""};
        for(int i = 0; i < capable.length; i++){
            firstCapable = capable[i].split(" ");
            if(firstCapable[2].contains("active")){
                return firstCapable;
            }
        }
        for(int i = 0; i < capable.length; i++){
            firstCapable = capable[i].split(" ");
            if(firstCapable[2].contains("inactive")){
                return firstCapable;
            }
        }
        for(int i = 0; i < capable.length; i++){
            firstCapable = capable[i].split(" ");
            if(firstCapable[2].contains("booting")){
                return firstCapable;
            }
        }
        System.out.println("FAIL");
        return firstCapable;




        /*
        //All the servers sent from the server being split into an array
        String[] capableServersAndInfo = capableServers.split("\n");
        String[] availServersAndInfo = availServers.split("\n");

        //System.out.println("serversAndInfo size: " + serversAndInfo.length);
        
        String currentServerString = "";
        String[] currentServer = {""};
        String[] bestFitServer = {""};
        String[] backupServer = {""};
        String[] secondBackupServer = {""};

        
        for(int i = 0; i < capableServersAndInfo.length; i++){
            currentServerString = capableServersAndInfo[i];
            if(availServers.contains(currentServerString)){
                bestFitServer = currentServerString.split(" ");
                return bestFitServer;
            }
        }

        for(int i = 0; i < capableServersAndInfo.length; i++){
            currentServer = capableServersAndInfo[i].split(" ");
            if(currentServer[2].contains("booting")){
                return currentServer;
            }
        }

        for(int i = 0; i < capableServersAndInfo.length; i++){
            currentServer = capableServersAndInfo[i].split(" ");
            if(currentServer[2].contains("inactive")){
                return currentServer;
            }
        }
        currentServer = capableServersAndInfo[0].split(" ");
        return currentServer;



            //Go through all capable servers and rank them on how many times their name comes up (most times = index 0)
            //Go through all capable servers and look for inactive servers && save into an array of strings
            //if there is a server that is inactive and has the most desirable name, check to see if there are multiple and compare their cpu cores otherwise return it
            //if not, choose the next favourable inactive server
            //if all servers are not inactive, choose the most favourable active server
            //if all servers are not inactive or active, choose the most favourable booting server
            


        
            //Minimization of average turnaround time

            //GETS CAPABLE
            //Check for non-active servers
            //Check for active servers
            //Check for booting servers



        //System.out.println("fastestTurnAroundServer" + fastestTurnAroundServer);
        if(backupServer.length < 2){
            System.out.println("Small");
            bestFitServer = serversAndInfo[0].split(" ");
        }
        return bestFitServer;*/



    }
}
