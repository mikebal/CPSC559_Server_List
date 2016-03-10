import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Michael on 2/23/2016.
 */
public class Main {
    public static void main(String[] args)
    {
        ArrayList<Tracker> trackerList = new ArrayList<>();
        String ipAddress = getIPaddress();

        MultiThreadedServer server = new MultiThreadedServer(9000, trackerList);
        new Thread(server).start();

        try {
            Thread.sleep(1000);
            System.out.println("Redirect Server Running at: " + ipAddress);
            while(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }

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
