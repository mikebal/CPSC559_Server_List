import java.util.*;
import java.net.*;
import java.io.*;

public class Updater extends Thread
{
	public int updateInterval;
	public ArrayList<Tracker> update;
	public ObjectOutputStream outObj = null;
	String ip;
	int port;

	public Updater(int updateInterval, String ip, int port, ArrayList<Tracker> update){
		this.updateInterval = updateInterval;
		this.ip = ip;
		this.port = port;
		this.update = update;
	}

	public void run(){
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				sendRecurringUpdate();
			}
		}, updateInterval, updateInterval);
	}

	
	public void sendRecurringUpdate()  
	{
		//System.out.println("in sendRecurringUpdate()");
		try{
		
				Socket sock = new Socket(InetAddress.getByName(ip), port);
				//System.out.println("sending update to: " + ip + " " + port);
				PrintWriter printWriter = new PrintWriter(sock.getOutputStream(), true);
				printWriter.print("update\n");
				printWriter.flush();
				outObj = new ObjectOutputStream(sock.getOutputStream());	
				outObj.writeObject(update);
				outObj.flush();
				sock.close();		
		}
		catch(IOException e){
			//e.printStackTrace();
		}
	}
}