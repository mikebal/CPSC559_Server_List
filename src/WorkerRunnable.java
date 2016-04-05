import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
/**
   WorkerRunnable is the class that constitutes as the running thread when a trackerServer connects to the RedirectServer and sends a message.
 */

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    public ArrayList<Tracker> trackerList;   // The list of servers connected to the tracker server
    public ArrayList<Tracker> update;

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

                InputStream inputStream = clientSocket.getInputStream();
                BufferedInputStream  buff = new BufferedInputStream (clientSocket.getInputStream());
                while(buff.available() == 0);
                int ch = inputStream.read();
                StringBuffer stringBuffer = new StringBuffer();
                
                while((char)ch != '\n' && (char)ch != '\r')
                {
                    stringBuffer.append((char)ch);
                    ch = inputStream.read();
                }
                receivedMSG = stringBuffer.toString();

                System.out.println("New Connection: " + receivedMSG);

                if(receivedMSG.equals("New Server"))  // if a new tracker is starting up
                {
                    in.readLine();
                    output.write("READY FOR SERVER INFO\n".getBytes());

                    receivedMSG = in.readLine();
                    System.out.println("New server info: " + receivedMSG);
                    while(receivedMSG == null)
                        receivedMSG = in.readLine();

                    Tracker newServer = new Tracker(receivedMSG);      // Create a tracker object for the server ( IP_address / port)
                    serverManager = new ServerManager(trackerList);
                    serverManager.addServerToList(newServer);  // Add tracker to group if possible; else create new group
                    notifyTrackers();

                }
                else if(receivedMSG.equals("update")){
                    ObjectInputStream objectInputStream = open(clientSocket);
                    update = (ArrayList<Tracker>)objectInputStream.readObject();
                    ListIterator<Tracker> itr = update.listIterator();
                    trackerList.clear();
                    trackerList.addAll(update);

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
                e.printStackTrace();
                System.exit(-1);
            }

            catch(ClassNotFoundException e){
                e.printStackTrace();
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

    private ObjectInputStream open(Socket s) throws IOException{
        try{
            ObjectInputStream o = new ObjectInputStream(s.getInputStream());
            return o;
        }
        catch(Exception e){
            try{

                InputStream inputStream= s.getInputStream();
                int ch = inputStream.read();
                System.out.println("read a byte");
                if (ch != -1)
                    open(s);
                else{
                    System.out.println("reached end of stream");
                }
            }
            catch(Exception ee){
                ee.printStackTrace();
            }
        }
        System.out.println("null");
        return null;
    }

    /**
    * Notifies trackers when there is a new tracker in their group
    */
    public void notifyTrackers(){
        try{
        ListIterator<Tracker> itr = trackerList.listIterator();
        while(itr.hasNext())
        {
            Tracker tracker = itr.next();
            boolean hasSiblingTrackers = false;
            ListIterator<AddressPortObject> addrItr= tracker.addressPort.listIterator();
            while(addrItr.hasNext())
            {
                AddressPortObject addr = addrItr.next();
                Socket sock = new Socket(addr.get_ip_address(), Integer.parseInt(addr.get_port()));
                PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
                String str = new String();

                ListIterator<AddressPortObject> addrItr2 = tracker.addressPort.listIterator();

                str = str + (tracker.getTrackerName() + "'#");
                while(addrItr2.hasNext())
                {
                    AddressPortObject addr2 = addrItr2.next();
                    if(!addr2.equals(addr))
                    {
                        str += addr2.getAddressPort();
                        hasSiblingTrackers = true;
                    }

                }
                if(hasSiblingTrackers)
                    pw.println("new-sibling-trackers" + "'#" + str);
                            sock.close();
                        }
                    }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

