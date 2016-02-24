/**
 * Created by Michael on 2/23/2016.
 */
public class Tracker {
    private String address;
    private String name;
    private String port;
    private String free_slots;
    private String max_slots;
    private static final int TRACKER_ARGUMENTS = 5;

    public Tracker(String input){
        String[] strAray = input.split("'#");
        if(strAray.length == TRACKER_ARGUMENTS)
        {
            address = strAray[0];
            name = strAray[1];
            port = strAray[2];
            free_slots = strAray[3];
            max_slots = strAray[4];
        }
    }

    public void updateFreeSlots(String newFreeSlots){
        free_slots = newFreeSlots;
    }

    public String getTracker(){
        String concatonatedTrackerObject = "";
        concatonatedTrackerObject += address + "\n";
        concatonatedTrackerObject += name + "\n";
        concatonatedTrackerObject += port + "\n";
        concatonatedTrackerObject += free_slots + "\n";
        concatonatedTrackerObject += max_slots + "\n";

        return concatonatedTrackerObject;
    }
}
