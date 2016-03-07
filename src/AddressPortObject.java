/**
 * Created by Michael on 3/6/2016.
 */
public class AddressPortObject {
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
}
