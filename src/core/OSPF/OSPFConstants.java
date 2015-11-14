package core.OSPF;

/**
 * @author Anatoly Chekh
 * 
 *         Class contains different OSPF constants
 */
public class OSPFConstants {

	public static final String PACKET_DELIMETER = "|";

	public static final int OSPF_VERSION = 2;
	
	public static final String All_SPF_ROUTERS_IP_ADRESS = "224.0.0.5";
	
	public static final String All_DR_ROUTERS_IP_ADRESS = "224.0.0.6";
	
	public static final int ROUTER_DEAD_INTERVAL = 40 * 1000;

	public static final int MAX_AREA_NUMBER = 256 * 256 - 1; // 0.0.255.255

	public static final String OSPF_DATA_TYPE = "OSPF";
	
	public static final String OSPF_LAYER = "Network";

	public static final String OSPF_PACKET_HEADER = "{0}|{1}|{2}|{3}|{4}|{5}|{6}|{7}"; 
	
	/* Hello message constants */

	public static final String HELLO_PACKET_TYPE_NAME = "Hello packet";

	public static final int HELLO_PACKET_TYPE = 1;

	public static final int HELLO_INTERVAL = 10;

	public static final String HELLO_MESSAGE = "{0}|{1}|{2}|{3}|{4}|{5}|{6}|{7}|{8}";

	/* LSA Update constants */

	public static final String LSA_UPDATE_PACKET_TYPE_NAME = "LSA-Update";

	public static final int LSA_UPDATE_PACKET_TYPE = 4;

	public static final int LSA_UPDATE_INTERVAL = 30;
	
	public static final String LSA_UPDATE_MESSAGE = "{0}|{1}|{2}";
	
	/* LSA */
	
	public static final int ROUTER_LSA_TYPE = 1;
	
	public static final int EXTERNAL_LSA_TYPE = 5;

	public static final int LSA_MAX_AGE = 1000;
	
	public static final String LSA_HEADER = "{0}|{1}|{2}|{3}|{4}|{5}|{6}|{7}";
	
	public static final String ROUTER_LSA_LINK_MESSAGE = "{0}|{1}|{2}";
	
	public static final String ROUTER_LSA_MESSAGE = "{0}|{1}|{2}";
	
	public static final String EXTERNAL_LSA_MESSAGE = "{0}|{1}|{2}";
	
}
