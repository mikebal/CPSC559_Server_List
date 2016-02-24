import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**

 */
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
            String recevedMSG = "";//input.read();

            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                        true);
                recevedMSG = in.readLine();
                System.out.println(recevedMSG);

                if(recevedMSG.equals("New Server"))
                {
                    System.out.println("In server");
                    output.write("READY FOR SERVER INFO\n".getBytes());

                    recevedMSG = in.readLine();
                    while(recevedMSG == null)
                        recevedMSG = in.readLine();

                    Tracker newServer = new Tracker(recevedMSG);
                    trackerList.add(newServer);

                        System.out.println(recevedMSG);

                }
                else{
                    String trackerString = getServerListString(trackerList);
                    output.write((trackerString).getBytes());
                }
                System.out.println("Out of server");



            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }
            long time = System.currentTimeMillis();

            output.close();
            input.close();
            System.out.println( recevedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    private static String getServerListString(ArrayList<Tracker> trackerList)
    {
        String trackerString = "";

        for(int i = 0; i < trackerList.size(); i++)
            trackerString += trackerList.get(i).getTracker();

        return trackerString;
    }
}