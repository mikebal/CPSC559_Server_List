import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Michael on 2/23/2016.
 */
public class Main {
    public static void main(String[] args)
    {
        ArrayList<Tracker> trackerList = new ArrayList<>();         // List of server who have advertised to Redirect Server
        String ipAddress = getIPaddress();

        MultiThreadedServer server = new MultiThreadedServer(9000, trackerList);    // initialize the server for to be run on its own thread
        new Thread(server).start();

        try {
            Thread.sleep(1000);  // sleep - required as some machines would not lock the port immediately.
            System.out.println("Redirect Server Running at: " + ipAddress);
            while(true);             // Keep the server running forever
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }

    /*
        function: getIPaddress
        input: none
        purpose: aquire the ip address of the machine the server is running on
        returns: (String) ip address
     */
    public static String getIPaddress(){
        String IPaddress = "";
        try {
            InetAddress thisIp = InetAddress.getLocalHost();
            IPaddress = thisIp.getHostAddress();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return IPaddress;
    }

}
