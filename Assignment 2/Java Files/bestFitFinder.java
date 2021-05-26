import java.net.Socket;

public class bestFitFinder {
    public static String[] bestFit(String currentMsg, String cores, Socket s){
        //Set up variables for the algorithm
        String[] servers = currentMsg.split("\n"); 
        String[] runningJobHighCoreServer = {""};
        String[] bestFitServer = servers[0].split(" ");
        int runningJobHighCoreValue = -1;

        //Checking that the current msg from the server is a list of servers and 
        //not "." (ie; null checking - although not null but rather "." checking)
        if(bestFitServer.length == 1){
            return bestFitServer;
        }
        //Set up variables for the algorithm (after checking they are safe to instantiate)
        int currentServerCores = Integer.parseInt(bestFitServer[4]);
        int coreScore = currentServerCores - Integer.parseInt(cores);
        int coreValue = coreScore;
        int jobCount = -1;
        int mostJobs = -1;

        //Checks each available server
        for (int i = 0; i < servers.length; i++){
            if(!servers[i].contains(".")){
                String[] currentServer = servers[i].split(" ");
                if(currentServer.length > 2){
                    //Requests the amount of current running jobs on the server
                    currentServerCores = Integer.parseInt(currentServer[4]);  
                    coreScore = currentServerCores - Integer.parseInt(cores);
                    String msg = "CNTJ " + currentServer[0] + " " + currentServer[1] + " " + 2 + "\n";
                    msgWriter.sendMsg(s, msg);
                    //Saves the amount of jobs running so they can be compared
                    String jobs = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");
                    String[] jobStringCount = jobs.split("\n");
                    if(jobStringCount.length == 2){
                        jobCount = Integer.parseInt(jobStringCount[1]);
                    }
                    else{
                        jobCount = Integer.parseInt(jobStringCount[0]);
                    }
                    //Checks to see if the amount of jobs running is greater than the 
                    //current most jobs running on a single server
                    if(jobCount > 0){
                        if(jobCount > mostJobs){
                            mostJobs = jobCount;
                            bestFitServer = currentServer;
                            coreValue = coreScore; 
                        }


                    }
                    
                }
            }
        }
        //If a server with jobs has been found, return it
        if(bestFitServer.length > 1){
            if(coreValue == 0){
                return bestFitServer;
            }
            else{
                runningJobHighCoreServer = bestFitServer;
                runningJobHighCoreValue = coreValue;
            }
        }

        //Checks each server again (only if no servers are running any jobs)
        for(int i = 0; i < servers.length; i++){
            if(!servers[i].contains(".")){
                String[] currentServer = servers[i].split(" ");
                //Chooses the server with the best fitness value - not taking into account running jobs
                if(currentServer.length > 2){
                    currentServerCores = Integer.parseInt(currentServer[4]);
                    coreScore = currentServerCores - Integer.parseInt(cores);
                    //If the fitness value is 0, return the current server
                    if(coreScore == 0){
                        bestFitServer = currentServer;
                        coreValue = coreScore; 
                        i = servers.length;
                    }
                    else{
                        if(coreScore < coreValue){
                            bestFitServer = currentServer;
                            coreValue = coreScore;
                        }
                    }
                }
            }
        }
        //If no fitness value of 0 is found, return the server with the best fitness value out of the 
        //first server and the server with the smallest fitness value that is not 0
        if(coreValue != 0){
            if(coreValue < runningJobHighCoreValue){
                return bestFitServer;
            }
            else{
                return runningJobHighCoreServer;
            }
        }
        return bestFitServer;
    }

    //Backup in case there are no available servers
    public static String[] closestFit(String currentMsg, String cores){
        String[] servers = currentMsg.split("\n");
        String[] bestFitServer = servers[0].split(" ");
        int coreValue = -1; //Server Core Count - Cores required

        //Checks each server
        for (int i = 0; i < servers.length; i++){
            if(!servers[i].contains(".")){
                String[] currentServer = servers[i].split(" ");
                int currentServerCores = Integer.parseInt(currentServer[4]);
                int coreScore = currentServerCores - Integer.parseInt(cores);
                //Returns the server with the best coreScore (CPU Cores the server has - not nessacarily available)
                if(coreScore > coreValue){
                    bestFitServer = currentServer;
                    coreValue = coreScore; 
                }
            }
        }
        return bestFitServer;

    }
}
