import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by Michael on 2/23/2016.
 *
 * Tracker is an object used to hold vital connection information relating to the access points of a tracker group
 */
public class Tracker implements Serializable {
    public ArrayList<AddressPortObject> addressPort = new ArrayList<>();
    private String name;         // The name of the server or 'category'
    private String free_slots;   // The number of available open connections to servers within the group
    private String max_slots;    // The maximum available number of serverr sockets on the group
    private static final int TRACKER_ARGUMENTS = 5;
    private int roundRobinIndex = 0;  // Index of the last 'first priority' server

    public Tracker(String input){
        String[] strArray = input.split("'#");
        if(strArray.length == TRACKER_ARGUMENTS)
        {
            addressPort.add(new AddressPortObject(strArray[0], strArray[2]));
            name = strArray[1];
            free_slots = strArray[3];
            max_slots = strArray[4];
        }
    }

    public AddressPortObject getAddressPort(){ return addressPort.get(0);}

    public void addNewServerAddress(AddressPortObject newServerInfo)
    {
        this.addressPort.add(newServerInfo);
    }

    /**
     * function: updateFreeSlots
     * purpose: incrment/decrement the active peer count up or down depending on input
     * @param newFreeSlots
     */
    public void updateFreeSlots(String newFreeSlots)
    {
        int currentFreeSlots = Integer.valueOf(free_slots);

        currentFreeSlots += Integer.valueOf(newFreeSlots);
        free_slots = String.valueOf(currentFreeSlots);
    }

    public void updateMaxSlots(String newSlots){
        int currentMazSlots = Integer.valueOf(max_slots);
        currentMazSlots += Integer.valueOf(newSlots);
        max_slots = String.valueOf(currentMazSlots);
    }

    public String getMax_slots(){return max_slots;}

    public String getFree_slots(){return free_slots;}

    /*
        function:  getTracker
        purpose: To retrieve a string containing the connection information of a tracker in round-robin order.
        returns string in format:  SERVER_NAME, (N)UMBER_OF_SERVERS, IP(1), Port(1),IP(2),Port(2),...,IP(N),Port(N)
     */
    public String getTracker(){
        String concatenatedTrackerObject = "";
        int index = roundRobinIndex; // start position

        concatenatedTrackerObject += name + "\t" + getFree_slots() + "/" + getMax_slots() + "'#";                               //Get the name of the server
        concatenatedTrackerObject += String.valueOf(addressPort.size()) + "'#"; // Get the number of servers
        for(int i = 0; i < addressPort.size(); i++) {
            concatenatedTrackerObject += addressPort.get(index).getAddressPort(); // For all the servers get their IP and Port.
            index++;
            if(index == addressPort.size())
                index = 0;
        }

        addressRoundRobin();

        return concatenatedTrackerObject;
    }

    /*
        function: addressRoundRobin
        purpose: Increment the roundRobin index to the next server with rollover.
     */
    public void addressRoundRobin(){
        roundRobinIndex++;
        if(roundRobinIndex == addressPort.size())
            roundRobinIndex = 0;
    }

    public String getTrackerName(){return this.name;}

}
