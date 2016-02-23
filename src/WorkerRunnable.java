import java.io.*;
import java.net.Socket;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
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
            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }


            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println( recevedMSG + "      " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}