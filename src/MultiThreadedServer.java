import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
/*
    MultiThreadedServer is a Class that is instantiated when a connection top the server is made and handles incoming
    communications.
 */
public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = 9000;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    public ArrayList<Tracker> trackerList;

    public MultiThreadedServer(int port, ArrayList<Tracker> trackerList){
        this.serverPort = port;
        this.trackerList = trackerList;
    }
    // When a connection to the server is made
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();      // Attempt to accept the connection
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            // If the connection has been established successfully pass the connection to WorkerRunnable
            new Thread(
                    new WorkerRunnable(
                            clientSocket, "Multithreaded Server", trackerList)
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 9000", e);
        }
    }

}