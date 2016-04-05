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
    public ArrayList<Tracker> update;
    private boolean isBackup;

    public WorkerRunnable(Socket clientSocket, String serverText, ArrayList<Tracker> trackerList, boolean isBackup) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.trackerList = trackerList;
        this.isBackup = isBackup;
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
                //System.out.println(stringBuffer.toString());

                //receivedMSG = in.readLine();
                System.out.println("New Connection: " + receivedMSG);

                if(receivedMSG.equals("New Server"))
                {
                    in.readLine();
                    output.write("READY FOR SERVER INFO\n".getBytes());

                    receivedMSG = in.readLine();
                    System.out.println("New server info: " + receivedMSG);
                    while(receivedMSG == null)
                        receivedMSG = in.readLine();

                    Tracker newServer = new Tracker(receivedMSG);
                    ServerManager serverManager = new ServerManager(trackerList);
                    serverManager.addServerToList(newServer);
                    notifyTrackers();

                }
                else if(receivedMSG.equals("update")){
                    ObjectInputStream objectInputStream = open(clientSocket);
                    update = (ArrayList<Tracker>)objectInputStream.readObject();
                    ListIterator<Tracker> itr = update.listIterator();
                    trackerList.clear();
                    trackerList.addAll(update);
                }
                else
                {
                    System.out.println("Sending Server list to (" + receivedMSG + ")");
                    String trackerString = getServerListString(trackerList);
                    out.println(trackerString);
                    System.out.println(trackerString);
                }

            } catch (IOException e) {
                System.out.println("Read failed");
                e.printStackTrace();
                System.exit(-1);
            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            long time = System.currentTimeMillis();

            output.close();
            input.close();
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

