import java.util.ArrayList;

/**
 * Created by Michael on 3/6/2016.
 */
public class ServerManager {
    private ArrayList<Tracker> serverList;

    public ServerManager(ArrayList<Tracker> serverList)
    {
        this.serverList = serverList;
    }

    public void addServerToList(Tracker newTracker)
    {
        int index = getServerIndex(serverList, newTracker.getTrackerName());
        if(index == -1)
            serverList.add(newTracker);
        else
        {
            AddressPortObject accessInfo = newTracker.getAddressPort();
            serverList.get(index).addNewServerAddress(accessInfo);
        }
        displayActiveServers();
    }

    private int getServerIndex(ArrayList<Tracker> serverList, String serverName)
    {
        int index = -1;
        for(int i = 0; i < serverList.size(); i++)
        {
            if(serverList.get(i).getTrackerName().equals(serverName))
            {
                index = i;
                break;
            }
        }
        return index;
    }
    private void displayActiveServers(){
        Tracker tracker = serverList.get(0);
        ArrayList<AddressPortObject> listToDisplay =  tracker.addressPort;
        //DO NOT DELETE
       /* for(int i = 0; i < listToDisplay.size(); i++)
        {
            System.out.print("Connection " + i + ": ");
            System.out.println(listToDisplay.get(i).get_ip_address() + "    " + listToDisplay.get(i).get_port());
        }*/

    }
}
