import java.net.Socket;

public class bestFitFinder {
    public static String[] bestFit(String currentMsg, String cores, Socket s){
        
        //If server has coreScore 0 and has a running job, do it
        //if none fit, look for first with score of 0


        String[] servers = currentMsg.split("\n");
        String[] bestFitServer = servers[0].split(" ");
        int currentServerCores = Integer.parseInt(bestFitServer[4]);
        int coreScore = currentServerCores - Integer.parseInt(cores);
        int coreValue = coreScore;
        int jobCount = -1;

       

        



        for (int i = 0; i < servers.length; i++){
            //System.out.println("NEXT");
            if(!servers[i].contains(".")){
                String[] currentServer = servers[i].split(" ");
                if(currentServer.length > 2){

                    currentServerCores = Integer.parseInt(currentServer[4]);
                    coreScore = currentServerCores - Integer.parseInt(cores);
                    String msg = "CNTJ " + currentServer[0] + " " + currentServer[1] + " " + 2 + "\n";
                    msgWriter.sendMsg(s, msg);

                    String jobs = msgReader.readMsg(s);
                    msgWriter.sendMsg(s, "OK\n");
                    String[] jobStringCount = jobs.split("\n");
                    if(jobStringCount.length == 2){
                        jobCount = Integer.parseInt(jobStringCount[1]);
                    }
                    else{
                        jobCount = Integer.parseInt(jobStringCount[0]);
                    }
                    if(jobCount > 0){
                        if(coreScore == 0){
                            bestFitServer = currentServer;
                            coreValue = coreScore; 
                            i = servers.length;
                        }
                    }
                }
            }
        }


        if(coreValue != 0){
            for(int i = 0; i < servers.length; i++){
                if(!servers[i].contains(".")){
                    String[] currentServer = servers[i].split(" ");
                    currentServerCores = Integer.parseInt(currentServer[4]);
                    coreScore = currentServerCores - Integer.parseInt(cores);
                    if(coreScore == 0){
                        bestFitServer = currentServer;
                        coreValue = coreScore; 
                        i = servers.length;
                    }
                    else{
                        if(coreScore < coreValue && coreScore > 0){
                            bestFitServer = currentServer;
                            coreValue = coreScore; 
                        }
                    }
                }
            }
        }
        System.out.println("coreValue: " + coreValue);
        System.out.println("coreScore: " + coreScore);
        return bestFitServer;

    }









    public static String[] closestFit(String currentMsg, String cores){
        String[] servers = currentMsg.split("\n");
        String[] bestFitServer = servers[0].split(" ");
        int coreValue = -1; //Server Core Count - Cores required

        for (int i = 0; i < servers.length; i++){
            if(!servers[i].contains(".")){
               // System.out.println("currentServer closestFit: " + servers[i]);
                String[] currentServer = servers[i].split(" ");
                int currentServerCores = Integer.parseInt(currentServer[4]);
                int coreScore = currentServerCores - Integer.parseInt(cores);
                if(coreScore > coreValue){
                    bestFitServer = currentServer;
                    coreValue = coreScore; 
                }
            }
        }

        return bestFitServer;

    }
}
