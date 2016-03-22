import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
/**

 */

class TrackerTuple{
    public String ip;
    public String port;

    public TrackerTuple(String ip, String port){
        this.ip = ip;
        this.port = port;
    }

}

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    public ArrayList<Tracker> trackerList;

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<Tracker> trackerList) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.trackerList = trackerList;
    }

    public void run() {

        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            String receivedMSG = "";

            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                        true);

                receivedMSG = in.readLine();
                System.out.println("New Connection: " + receivedMSG);

                if(receivedMSG.equals("New Server"))
                {
                    output.write("READY FOR SERVER INFO\n".getBytes());

                    receivedMSG = in.readLine();
                    System.out.println("New server info: " + receivedMSG);
                    while(receivedMSG == null)
                        receivedMSG = in.readLine();

                    Tracker newServer = new Tracker(receivedMSG);
                    ServerManager serverManager = new ServerManager(trackerList);
                    serverManager.addServerToList(newServer);

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

                }
                else
                {
                    System.out.println("Sending Server list to (" + receivedMSG + ")");
                    String trackerString = getServerListString(trackerList);
                    out.println(trackerString);
                }

            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }
            long time = System.currentTimeMillis();

            output.close();
            input.close();
           // System.out.println( recevedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    private static String getServerListString(ArrayList<Tracker> trackerList)
    {
        String trackerString = "";

        if(trackerList.size() == 0)
            trackerString = "No servers are currently running";
        else
            for(int i = 0; i < trackerList.size(); i++)
                trackerString += trackerList.get(i).getTracker();

        return trackerString;
    }
}

