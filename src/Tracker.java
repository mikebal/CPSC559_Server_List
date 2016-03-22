import java.util.ArrayList;

/**
 * Created by Michael on 2/23/2016.
 */
public class Tracker {
    public ArrayList<AddressPortObject> addressPort = new ArrayList<>();
    private String name;
    private String free_slots;
    private String max_slots;
    private static final int TRACKER_ARGUMENTS = 5;
    private int roundRobinIndex = 0;

    public Tracker(String input){
        String[] strAray = input.split("'#");
        if(strAray.length == TRACKER_ARGUMENTS)
        {
            addressPort.add(new AddressPortObject(strAray[0], strAray[2]));
            name = strAray[1];
            free_slots = strAray[3];
            max_slots = strAray[4];
        }
    }

    public AddressPortObject getAddressPort(){ return addressPort.get(0);}

    public void addNewServerAddress(AddressPortObject newServerInfo)
    {
        this.addressPort.add(newServerInfo);
    }

    public void updateFreeSlots(String newFreeSlots){
        free_slots = newFreeSlots;
    }

    public String getTracker(){
        String concatenatedTrackerObject = "";
        int index = roundRobinIndex;

        //inspect
        concatenatedTrackerObject += name + "'#";
        concatenatedTrackerObject += String.valueOf(addressPort.size()) + "'#";
        for(int i = 0; i < addressPort.size(); i++) {
            concatenatedTrackerObject += addressPort.get(index).getAddressPort();
            index++;
            if(index == addressPort.size())
                index = 0;
        }

        addressRoundRobin();

        return concatenatedTrackerObject;
    }

    public void addressRoundRobin(){
        roundRobinIndex++;
        if(roundRobinIndex == addressPort.size())
            roundRobinIndex = 0;
    }

    public String getTrackerName(){return this.name;}
}
