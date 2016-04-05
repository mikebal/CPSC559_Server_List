import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
/**
   WorkerRunnable is the class that constitutes as the running thread when a trackerServer connects to the RedirectServer and sends a message.
 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    public ArrayList<Tracker> trackerList;   // The list of servers connected to the tracker server

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<Tracker> trackerList) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.trackerList = trackerList; // List of trackerServers
    }

    public void run() {

        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            String receivedMSG;
            String[] parsedInput;
            ServerManager serverManager;

            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                        true);

                receivedMSG = in.readLine();
                System.out.println("New Connection: " + receivedMSG);

                if(receivedMSG.equals("New Server"))  // if a new tracker is starting up
                {
                    output.write("READY FOR SERVER INFO\n".getBytes());   // requests connection info from the tracker

                    receivedMSG = in.readLine();
                    System.out.println("New server info: " + receivedMSG);
                    while(receivedMSG == null)
                        receivedMSG = in.readLine();

                    Tracker newServer = new Tracker(receivedMSG);      // Create a tracker object for the server ( IP_address / port)
                    serverManager = new ServerManager(trackerList);
                    serverManager.addServerToList(newServer);  // Add tracker to group if possible; else create new group
                }
                else if(receivedMSG.contains("'#"))
                {
                    parsedInput = receivedMSG.split("'#");
                    if(parsedInput[0].equals("update"))     // update indicates that an existing Tracker Server is changing it's Client connection count
                    {
                        serverManager = new ServerManager(trackerList);
                        serverManager.newClientChanged(trackerList, parsedInput[1],parsedInput[2]);  // Add tracker to group if possible; else create new group
                    }
                }

                else // if a client has connected to the redirect server
                {
                    System.out.println("Sending Server list to (" + receivedMSG + ")");
                    String trackerString = getServerListString(trackerList);  // Get a list of all of the tracker servers available
                    out.println(trackerString); // send the list to the requesting client.
                }

            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }
            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    /*
        function: getServerListString
        inputs: ArrayList<Tracker> trackerList = a list of all the active trackers including access infomation
        purpose:  Converts the list of tracker servers to a parsable format for related client program
        format:  ServerName(1), IP(1),Port(1),IP(2),Port(2),...,IP(N),Port(N),...ServerName(M),IP(1),Port(1).....
    */
    private static String getServerListString(ArrayList<Tracker> trackerList)
    {
        String trackerString = "";

        if(trackerList.size() == 0) // when no trackers notify clients
            trackerString = "No servers are currently running";
        else
            for(int i = 0; i < trackerList.size(); i++)  // for every tracker group get every IP/Port access point
                trackerString += trackerList.get(i).getTracker();

        return trackerString;
    }
}