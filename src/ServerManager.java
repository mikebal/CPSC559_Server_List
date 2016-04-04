import java.util.ArrayList;

/**
 * Created by Michael on 3/6/2016.
 *
 * The purpose of this class is to manage the lists associated keeping track of the TrackerServers
 */
public class ServerManager
{
    private ArrayList<Tracker> serverList;

    public ServerManager(ArrayList<Tracker> serverList)
    {
        this.serverList = serverList;
    }

    public void addServerToList(Tracker newTracker)
    {
        int index = getServerIndex(serverList, newTracker.getTrackerName());
        if(index == -1)                 //  If the server name(category) is unique add it to the list
            serverList.add(newTracker);
        else                            // If the server name(category) exists add it to that groups list
        {
            AddressPortObject accessInfo = newTracker.getAddressPort(); // Get the IP/Port of the server being added
            serverList.get(index).addNewServerAddress(accessInfo);      // Add it to the list
        }
    }

    /*
        function: getServerIndex
        inputs:
                ArrayList<Tracker> serverList = list of active servers who have advertised to the tracker
                String serverName = the name(category) of the new server that is being added to the server list

        purpose: this function searches the server list for an occurrences of the serverName(groupName) to determine if
                 the new server should be added to an existing group or create a new group.

        Returns: (int) the location within the list of the pre-existing group
                  DEFAULT = -1  indicates new group must be created.
     */
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
}
