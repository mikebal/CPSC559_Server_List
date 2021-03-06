import java.io.Serializable;

/**
 * Created by Michael on 3/6/2016.
 *
 * AddressPortObject is a simple object to hold the structure of the required connection information within our server lists.
 */
public class AddressPortObject implements Serializable{
    private String IP_address;
    private String port;
    private static final String PARSABLE_CHARACTER = "'#";

    public AddressPortObject(String ip, String port)
    {
        this.IP_address = ip;
        this.port = port;
    }

    public String get_ip_address(){return IP_address;}

    public String get_port(){return port;}

    public String getAddressPort(){
        return IP_address + PARSABLE_CHARACTER + port + PARSABLE_CHARACTER;
    }

    @Override

    public boolean equals(Object object){
        if(object != null && object instanceof AddressPortObject)
        {
            AddressPortObject addr = (AddressPortObject)object;
            return addr.getAddressPort().equals(this.getAddressPort());
        }
        else
            return false;

    }
}
