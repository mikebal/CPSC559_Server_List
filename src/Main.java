import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Michael on 2/23/2016.
 */
public class Main {
    public static void main(String[] args)
    {
        ArrayList<Tracker> trackerList = new ArrayList<>();         // List of server who have advertised to Redirect Server
        boolean isBackup = false;
        String backupIP = "localhost";
        int backupPort = 7000;
        String ipAddress = getIPaddress();
        int port = 9000;


        if(args.length == 0) //start a primary server with updates sent to the default ip and port
        {
            Updater updater = new Updater(5000, backupIP, backupPort, trackerList);
            updater.start();
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("Backup")) //start a backup server on the default port
        {
            System.out.println("Redirect backup server starting");
            isBackup = true;
            port = 7000;
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("Backup")) //start a backup server on the command-line specified port
        {
            System.out.println("Redirect backup server starting");
            isBackup = true;
            port = Integer.parseInt(args[1]);
        }
        else if(args.length == 2) //start a primary server with updates sent to the command-line specified ip and port
        {
            backupIP = args[0];
            backupPort = Integer.parseInt(args[1]);
            Updater updater = new Updater(5000, backupIP, backupPort, trackerList);
            updater.start();
        }
        else
        {
            System.out.println("Incorrect argument format");
            System.exit(0);
        }


        MultiThreadedServer server = new MultiThreadedServer(port, trackerList);

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
