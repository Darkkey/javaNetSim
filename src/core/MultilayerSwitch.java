package core;

import java.util.Enumeration;
import java.util.Hashtable;

public class MultilayerSwitch extends NetworkLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8045324015387252990L;

	int sz = 0;

	int buffSize = 255;

	Hashtable<String, Hashtable<String, Long>> IntCaches;
	

	public MultilayerSwitch(String inName, boolean inOn) {
		super(inName, 3, inOn); // pass name and protocolstack layer

		IntCaches = new Hashtable<String, Hashtable<String, Long>>();
		
	}


	@Override
	public void addNetworkInterface(String name, int type, boolean active) {
		if (!active) {
			super.addNetworkInterface(name, type, false, 0);
			IntCaches.put(name, new Hashtable<String, Long>());
		}
	}

	@Override
	public void Reset() {
		sz = 0;
		Enumeration it;
		String nic = "";

		it = NetworkInterfacetable.elements();

		while (it.hasMoreElements()) {
			NetworkInterface tempInterface = (NetworkInterface) it
					.nextElement();
			nic = tempInterface.getName();
			Hashtable outInt = IntCaches.get(nic);
			outInt.clear();
		}
        super.Reset();
	}

	@Override
	public int getState() {
		return sz;
	}

	public String getCache() {
		Enumeration it, it2;
		String nic = "";
		String result = "";

		it = NetworkInterfacetable.elements();

		while (it.hasMoreElements()) {
			NetworkInterface tempInterface = (NetworkInterface) it
					.nextElement();
			nic = tempInterface.getName();
			Hashtable outInt = IntCaches.get(nic);

			result = result + nic + ": ";

			it2 = outInt.keys();

			while (it2.hasMoreElements()) {
				String mac = (String) it2.nextElement();
				result = result + mac + "\t";
			}

			result = result + "\n";
		}

		return result;
	}

	/**
	 * This method will recieve a packet from any of the connected links and the
	 * copy the Packet and distribute a copy to each of the other connected
	 * links.
	 * 
	 * @author bevan_calliess
	 * @param inPacket -
	 *            The packet to be transported
	 * @param inLinkName -
	 *            The name of the link that sent the packet eg: eth0
	 */

	@Override
	public void receivePacket(Packet inPacket, String inInterfaceName)
			throws LowLinkException {
		if (sz != 1) {
			Ethernet_packet tempPacket = (Ethernet_packet) inPacket;
			Enumeration it;
			boolean intFound = false;
			String nic = "";

			try {
				Hashtable<String, Long> inInt = IntCaches.get(inInterfaceName);
				inInt.put(tempPacket.getSourceMACAddress(), Long.valueOf(System.currentTimeMillis()));
				

				it = NetworkInterfacetable.elements();
				while (it.hasMoreElements()) {
					NetworkInterface tempInt = (NetworkInterface) it.nextElement();
					
					if(!(tempInt instanceof NetworkInterfacePort)) continue;
					
					NetworkInterfacePort tempInterface = (NetworkInterfacePort) tempInt;
					
					nic = tempInterface.getName();
					Hashtable<String, Long> outInt = IntCaches.get(nic);
					if (outInt.get(tempPacket.getDestinationMACAddress()) != null) {
						intFound = true;
						try {
							if((tempInterface.mode == NetworkInterfacePort.MODE_TRUNK 
									|| tempInterface.vlan == tempPacket.vlan_id) && tempInterface.getConnectedLink()!=null){
								
								if(!tempInterface.getName().equals(inInterfaceName)){
									Ethernet_packet copyPacket = new Ethernet_packet(tempPacket
										.getData(), tempPacket.getDestinationMACAddress(),
										tempPacket.getSourceMACAddress());
									// tag it
									copyPacket.vlan_id = tempPacket.vlan_id;
							
									tempInterface.sendPacket(copyPacket);
								}
							}
						} catch (NullPointerException e) {
							System.out.println("MultilayerSwitch.java: " + e.toString());
						}
					}
				}

				it = NetworkInterfacetable.elements();
				while (it.hasMoreElements() && !intFound) {
					// Test to see if the current Interface is the Interface
					// that sent in the packet
					// if it is skip that interface
					NetworkInterface tempInt = (NetworkInterface) it.nextElement();
					
					if(!(tempInt instanceof NetworkInterfacePort)) continue;
					
					NetworkInterfacePort tempInterface = (NetworkInterfacePort) tempInt;
					
					if (!tempInterface.getName().equals(inInterfaceName)) {

						try {
							if((tempInterface.mode == NetworkInterfacePort.MODE_TRUNK 
									|| tempInterface.vlan == tempPacket.vlan_id) && tempInterface.getConnectedLink()!=null){
								Ethernet_packet copyPacket = new Ethernet_packet(tempPacket
										.getData(), tempPacket.getDestinationMACAddress(),
										tempPacket.getSourceMACAddress());
								// tag it
								copyPacket.vlan_id = tempPacket.vlan_id;
							
								tempInterface.sendPacket(copyPacket);
							}
						} catch (NullPointerException e) {
							System.out.println("Switch.java: " + e.toString());
						}
					}

				}

			} catch (Throwable th) {
				if (th.toString().contains(
						"Packet lost due to physical link problems!")) {
					throw new LowLinkException(th.toString());
				} else {
					sz = 1;
					System.out.println(th.toString());
					throw new LowLinkException(
							"Switch buffer overflow (packet loop flood?).");
				}
			}
		}
	}
	
}
