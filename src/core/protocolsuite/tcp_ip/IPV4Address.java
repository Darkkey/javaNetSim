/*
Java Firewall Simulator (jFirewallSim)

Copyright (c) 2004, jFirewallSim development team All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Canberra Institute of Technology nor the names of its
	  contributors may be used to endorse or promote products derived from this software
	  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package core.protocolsuite.tcp_ip;

/**
 * @author luke_hamilton
 * @author bevna_calliess
 * @author robert_hulford
 * @since 1.0 Sep 17, 2004
 * @version v0.20
 */

public class IPV4Address extends IPAddress {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2093944356499761071L;
	// These an the default binary subnet mask address for class A,B and C
	// network
	private static final String CLASS_A_BINARY = "11111111000000000000000000000000";
	private static final String CLASS_B_BINARY = "11111111111111110000000000000000";
	private static final String CLASS_C_BINARY = "11111111111111111111111100000000";

	// These are the class types
	public static final int CLASS_A_TYPE = 1;
	public static final int CLASS_B_TYPE = 2;
	public static final int CLASS_C_TYPE = 3;
	public static final int NO_CLASS = 4;
	
	/**
	 * Creates a new IPV4Address object containing the IP address saved in
	 * binary
	 * 
	 * @author luke_hamilton
	 * @param inDecimal
	 *            - IP address eg: 192.168.0.2
	 * @throws InvalidIPAddressException
	 * @version v0.20
	 */
	public IPV4Address(String inDecimal) throws InvalidIPAddressException {
		setIp(inDecimal);
	}

	public boolean isBroadcast() {
		// binaryIpAddress
		String[] ip = getDecimalIp().split("\\.");
		Integer firstoctet = new Integer(ip[0].trim());

		if (firstoctet.intValue() == 255) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidIp(final String ip) {
		return ip.matches("^[\\s]*[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}[\\s]*$");
	}

	/**
	 * This method returns the binary subnetmask in decimal
	 * 
	 * @author luke_hamilton
	 * @return String - Decimal version of SubnetMask eg: 192.168.0.2
	 * @version v0.20
	 */
	public String getSubnetMask() {
		return IPV4Address.toDecimalString(binarySubnetMask);
	}

	/**
	 * This method will return a decimal string of a binary ip address or binary
	 * subnetmask. This method is static as it will need to be used by other
	 * classes
	 * 
	 * @author luke_hamilton
	 * @param inBinaryIpAddress
	 *            - IP address in Binary eg: 11000000101010000000000000000001
	 * @return String - Decimal version of IP V4 address eg: 192.168.0.1
	 * @version v0.20
	 */
	public static String toDecimalString(String inBinaryIpAddress) {
		String decimalip = new String();
		String[] binary = new String[4];

		for (int i = 0, c = 0; i < 32; i = i + 8, c++) {
			binary[c] = inBinaryIpAddress.substring(i, i + 8);
			int octet = Integer.parseInt(binary[c], 2);
			decimalip = decimalip + Integer.toString(octet);
			if (c < 3) {
				decimalip = decimalip + ".";
			}
		}
		return decimalip;
	}

	public static String networkAddressByIPandMask(String ipAddress, String mask) {
		if (ipAddress == null) {
			return "";
		}
		try {
			return IPV4Address.toDecimalString(IPV4Address.IPandMask(IPV4Address.toBinaryString(ipAddress), IPV4Address
					.toBinaryString(mask)));
		} catch (InvalidIPAddressException e) {
			return "";
		}
	}

	public static boolean IPEqLower(String IP1, String IP2) {

		String IPOct1[] = IP1.split("\\.");
		String IPOct2[] = IP2.split("\\.");

		for (int i = 0; i < 4; i++) {
			if (Integer.valueOf(IPOct1[i]) - Integer.valueOf(IPOct2[i]) < 0)
				return false;
		}

		return true;
	}

	/**
	 * This method will return the binary version of an IPV4 address or subnet
	 * mask This method is static as it will need to be used by IPV4 class and
	 * possible others. If it doesn't it will throw and error.
	 * 
	 * @author luke_hamilton
	 * @param inDecimalIp
	 *            - IP address of node eg: 192.168.0.1
	 * @throws InvalidIPAddressException
	 * @return String - Binary version of IPV4 address
	 * @version v0.20
	 */
	public static String toBinaryString(String inDecimalIp) throws InvalidIPAddressException {
		String[] ip = inDecimalIp.split("\\."); // Split string into array
		String binaryAddress = new String();

		for (int i = 0; i < ip.length; i++) {
			Integer octet = new Integer(ip[i].trim());

			// This tests every octet within the inDecimalIp
			// to make sure the user hasnt entered a Negative value.
			// This code fixs bug #1056000
			if (octet.intValue() < 0) {
				throw new InvalidIPAddressException("Invalid IP Address, cant use Negative IP address");
			}

			if (octet.intValue() <= 255) {

				StringBuffer temp = new StringBuffer(Integer.toBinaryString(octet.intValue()));
				if (temp.length() != 8) {

					while (temp.length() != 8) {
						temp.insert(0, 0);
					}
				}
				binaryAddress = binaryAddress + temp;
			} else
				throw new InvalidIPAddressException("Invalid IP Address");
		}

		return binaryAddress;
	}

	/**
	 * This class is called when an ip address is set. It then set the default
	 * subnet type mask for that IP. And returns the binary string of that
	 * subnet mask
	 * 
	 * @author luke_hamilton
	 * @param inDeciamlIp
	 *            - IP address of node eg: 192.168.0.2
	 * @return String - Return class SubnetMask in Binary
	 * @version v0.20
	 */
	private String setDefaultSubnetMask(String inDeciamlIp) {
		/*
		 * if(getDefaultSubnetMaskClassType(inDeciamlIp) == CLASS_A_TYPE){
		 * ClassType = CLASS_A_TYPE; return CLASS_A_BINARY; } else
		 * if(getDefaultSubnetMaskClassType(inDeciamlIp) == CLASS_B_TYPE){
		 * ClassType = CLASS_B_TYPE; return CLASS_B_BINARY; } else
		 * if(getDefaultSubnetMaskClassType(inDeciamlIp) == CLASS_C_TYPE){
		 * ClassType = CLASS_C_TYPE; return CLASS_C_BINARY; }
		 */
		return "00000000000000000000000000000000";

	}

	/**
	 * This method returns the default subnet mask type that an IP Address
	 * belong too.
	 * 
	 * @author luke_hamilton
	 * @param inDecimalIP
	 *            - IP address of node eg: 192.168.0.1
	 * @return int - Return class type eg: 1, 2 or 3
	 * @version v0.20
	 */
	public static int getDefaultSubnetMaskClassType(String inDecimalIP) {
		String[] ip = inDecimalIP.split("\\.");
		Integer firstoctet = new Integer(ip[0].trim());

		if (firstoctet.intValue() >= 1 && firstoctet.intValue() <= 127) { // test
																			// for
																			// class
																			// A
			return IPV4Address.CLASS_A_TYPE;
		} else if (firstoctet.intValue() >= 128 && firstoctet.intValue() <= 191) {// test
																					// for
																					// class
																					// B
			return IPV4Address.CLASS_B_TYPE;
		} else if (firstoctet.intValue() >= 192 && firstoctet.intValue() <= 223) {// test
																					// for
																					// class
																					// C
			return IPV4Address.CLASS_C_TYPE;
		} else {
			return IPV4Address.NO_CLASS;
		}
		// return 0; UGLY FIX
	}

	/**
	 * This method will return a Decimal Subnet Mask. This is being used mainly
	 * by the GUI in the process of set ip address details.
	 * 
	 * @param inDecimalIp
	 *            - IP address of node eg: 192.168.0.1
	 * @return string - Default SubnetMask eg: 255.255.255.0
	 * @version v0.20
	 */
	public static String getDefaultSubnetMask(String inDecimalIp) {

		if (IPV4Address.getDefaultSubnetMaskClassType(inDecimalIp) == IPV4Address.CLASS_A_TYPE)
			return IPV4Address.toDecimalString(IPV4Address.CLASS_A_BINARY);
		else if (IPV4Address.getDefaultSubnetMaskClassType(inDecimalIp) == IPV4Address.CLASS_B_TYPE)
			return IPV4Address.toDecimalString(IPV4Address.CLASS_B_BINARY);
		else if (IPV4Address.getDefaultSubnetMaskClassType(inDecimalIp) == IPV4Address.CLASS_C_TYPE)
			return IPV4Address.toDecimalString(IPV4Address.CLASS_C_BINARY);
		return null; // This should never happen
	}

	/**
	 * This method set the ip address in binary form. It then sets the default
	 * subnet mask based on the ip address. It will also test that the ip passed
	 * to it is valid. Eg 127.0.0.1, 0.0.0.0 and subnetMask 255.255.255.0 cannot
	 * be set.
	 * 
	 * @author luke_hamilton
	 * @param inDecimalIp
	 *            - A decimal IP address eg: 192.168.0.2
	 * @throws InvalidIPAddressException
	 * @version v0.20
	 */
	public final void setIp(String inDecimalIp) throws InvalidIPAddressException {

		if (inDecimalIp.equalsIgnoreCase("0.0.0.0")) { // test for 0.0.0.0
			throw new InvalidIPAddressException("Invalid IP Address");
		} else if (inDecimalIp == "127.0.0.1") { // test for loopback 127.0.0.1
			throw new InvalidIPAddressException("Invalid IP Address, this address is reserved for the loopback device");
		}

		String[] ip = inDecimalIp.split("\\."); // split string into an array

		for (int i = 0; i < ip.length; i++) {
			try {
				Integer octet = new Integer(ip[i].trim());
				// if (octet.intValue() == 255){ //test all octet's for 255
				// throw new
				// InvalidIPAddressException("Invalid IP Address, cant use Subnet Mask as IP address");
				// }
				// FIXME

				if (i == 0) { // test first octect for 0 eg 0.1.2.3 is an
								// invalid address
					if (octet.intValue() == 0) {
						throw new InvalidIPAddressException("Invalid IP Address");
					}
				}
			} catch (NumberFormatException e) {
				throw new InvalidIPAddressException("Invalid characters entered for IP address");
			}
		}

		if (IPV4Address.checkHostId(setDefaultSubnetMask(inDecimalIp), IPV4Address.toBinaryString(inDecimalIp))) { // test
																													// if
																													// the
																													// hostID
																													// is
																													// valid
			binaryIpAddress = IPV4Address.toBinaryString(inDecimalIp);
			binarySubnetMask = setDefaultSubnetMask(inDecimalIp);
		} else
			throw new InvalidIPAddressException("Invalid IP Address, invalid hostID");
	}

	/**
	 * This method returns the number of bits used for the network ID of an IP
	 * Address Return an int of eight for a class A network, with a subnet of
	 * 255.0.0.0
	 * 
	 * @author luke_hamilton
	 * @param inBinarySubnetMask
	 *            - Binary version of a SubnetMask eg:
	 *            11111111000000000000000000000000
	 * @return boolean
	 */
	public static int getNetworkIDBits(String inBinarySubnetMask) {
		return inBinarySubnetMask.lastIndexOf("1") + 1;
	}

	/**
	 * This method check to see if the host ID of an IP address is valid eg: The
	 * class A address 1.1.1.0 is an invalid address, but 1.0.0.1 is a valid
	 * class A address. Return true if valid. Else, return false.
	 * 
	 * @author luke_hamilton
	 * @param inBinarySubnetMask
	 *            - SubnetMask in Binary
	 * @param inBinaryIPAddress
	 *            - IP address in Binary
	 * @throws InvalidIPAddressException
	 * @return boolean
	 * @version v0.20
	 */
	// This method test the binary hostID part of an IP address, it only needs
	// to contain a "1"
	// in any part of its string to be valid.
	private static boolean checkHostId(String inBinarySubnetMask, String inBinaryIPAddress)
			throws InvalidIPAddressException {
		int SubnetBits = IPV4Address.getNetworkIDBits(inBinarySubnetMask);
		if (inBinaryIPAddress.length() == 32) {
			String temp = inBinaryIPAddress.substring(SubnetBits, 32);
			if (temp.indexOf("1") != -1) {
				return true; // the address is valid
			}
			return false;
		}
		throw new InvalidIPAddressException("Invalid ip address");
	}

	/**
	 * This method returns the number of bits used in the network ID of an IP
	 * Address
	 * 
	 * @author luke_hamilton
	 * @return String - Binary version of SubnetMask
	 * @version v0.20
	 */
	private int getNetworkIDBits() {
		return binarySubnetMask.indexOf("0"); // -1
	}

	/**
	 * This method returns a decimal string containing the ip address
	 * 
	 * @author luke_hamilton
	 * @return String - Decimal version of IP address eg: 192.168.0.2
	 * @version v0.20
	 */
	public String getDecimalIp() {
		return IPV4Address.toDecimalString(binaryIpAddress);
	}

	/**
	 * This method overrides the toString method. Please note that this is for
	 * testing! Please leave.
	 * 
	 * @author luke_hamilton
	 * @return String - Decimal version of IP address eg: 192.168.0.2
	 * @version v0.20
	 */
	// TODO: Finish overriding to string method with more details about the
	// object.
	@Override
	public String toString() {
		return IPV4Address.toDecimalString(binaryIpAddress);
	}

	/**
	 * This method returns the binary string of the IP address
	 * 
	 * @author luke_hamilton
	 * @return String - Binary version of IP address eg:
	 *         11000000101010000000000000000001
	 * @version v0.20
	 */
	public String getBinaryIpAddress() {
		return binaryIpAddress;
	}

	/**
	 * This method sets a custom subnetMask it will check if the current address
	 * is a class A B or C and then check that the passed in Subnet meets the
	 * minimum requirements for that class. If it passes that test it will then
	 * call the validateSubSting method to check that the remainder of the
	 * Subnet mask is valid.
	 * 
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inDecimalSubnetIp
	 *            - IP address in decimal eg: 192.168.0.2
	 * @throws InvalidSubnetMaskException
	 * @version v0.20
	 */
	public void setCustomSubnetMask(String inDecimalSubnetIp) throws InvalidSubnetMaskException {
		try {
			String binSubnetMask = IPV4Address.toBinaryString(inDecimalSubnetIp);
			binarySubnetMask = binSubnetMask;
		} catch (Exception e) {
		}
		/*
		 * try{ String binSubnetMask = toBinaryString(inDecimalSubnetIp);
		 * 
		 * if(ClassType == CLASS_A_TYPE){
		 * if(binSubnetMask.substring(0,8).equals("11111111")){
		 * if(validateSubString(binSubnetMask.substring(8,32))){
		 * binarySubnetMask = binSubnetMask; }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }else
		 * if(ClassType == CLASS_B_TYPE){
		 * if(binSubnetMask.substring(0,16).equals("1111111111111111")){
		 * if(validateSubString(binSubnetMask.substring(16,32))){
		 * binarySubnetMask = binSubnetMask; }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }else{
		 * if(binSubnetMask.substring(0,24).equals("111111111111111111111111")){
		 * if(validateSubString(binSubnetMask.substring(24,32))){
		 * binarySubnetMask = binSubnetMask; }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }else{ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); } }
		 * }catch(InvalidIPAddressException e){ throw new
		 * InvalidSubnetMaskException("Invalid Subnet Mask"); }
		 */
	}

	/**
	 * This method takes a substring of the Subnet mask and validates that it is
	 * a valid subnet mask. If Mask is correct return true. Else, return false
	 * 
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inMask
	 *            - A string that will be validated
	 * @return boolean
	 * @version v0.20
	 */
	private static boolean validateSubString(String inMask) {
		if (inMask.substring(inMask.length() - 2, inMask.length()).equals("00")) {
			int indexOfZero = inMask.indexOf('0');
			String hosts = inMask.substring(indexOfZero);
			int indexOfOne = hosts.indexOf('1');
			if (indexOfOne != -1) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * This method returns a binary string of the subnet mask
	 * 
	 * @author luke_hamilton
	 * @return String - IP address in binary
	 * @version v0.20
	 **/
	public String getBinarySubnetMask() {
		return binarySubnetMask;
	}

	/**
	 * This method returns the subnet mask in a decmial string
	 * 
	 * @author luke_hamilton
	 * @since v0.20
	 * @return String - IP address in decimal
	 */
	public String getDecmialSubnetMask() {
		return IPV4Address.toDecimalString(binarySubnetMask);
	}

	/**
	 * This is a static method that will validate the IP Address passed in to
	 * see if it as a valid IPV4 address
	 * 
	 * @author bevan_calliess
	 * @param inIPAddress
	 *            - The IP address to be tested eg: 192.168.0.2
	 * @return boolean
	 * @version v0.20
	 */
	public static boolean validateDecIP(String inDecIPAddress) {
		inDecIPAddress = inDecIPAddress.trim();
		String[] ip = inDecIPAddress.split("\\."); // split string into an array
		if (ip.length != 4) { // ip address must have 4 octets
			return false;
		}

		if (inDecIPAddress.contains("255.255.255.255")) {
			return true;
		}

		
		
		for (int i = 0; i < ip.length; i++) {
			try {
				Integer octet = new Integer(ip[i].trim());
				// if this is the first octect test to see if it is a valid
				// a,b or address in the range of 1 to 223
				if (i == 0) {
					if (octet.intValue() < 1 || octet.intValue() > 223) {
						return false;
					}
				} else {
					if (octet.intValue() < 0 || octet.intValue() > 255) {
						return false;
					}
				}
			} catch (NumberFormatException e) {
				return false;
			}

			try {

				// This code check to make sure the host id of an ipaddress is
				// also valid.
				boolean x = IPV4Address.checkHostId(IPV4Address.toBinaryString(IPV4Address
						.getDefaultSubnetMask(inDecIPAddress)), IPV4Address.toBinaryString(inDecIPAddress));

				if (x) {
					return true;
				}
				return false;

			} catch (Exception e) {
			} // This should never happen! (fingers crossed)
		}
		return true; // if it has passed all of these tests return true
	}

	/**
	 * This is a static method that will sanitize the IP Address valid IPV4 address or null
	 * 
	 * @author QweR
	 * @param inIPAddress
	 *            - The IP address to be tested eg: 192.168.0.2
	 * @return String 
	 * @version v0.20
	 */
	public static String sanitizeDecIP(String inDecIPAddress) {
		if(inDecIPAddress==null) return null;
		inDecIPAddress = inDecIPAddress.trim();
		String ipaddr = ""; 
		String[] ip = inDecIPAddress.split("\\."); // split string into an array
		if (ip.length != 4) { // ip address must have 4 octets
			return null;
		}

		if (inDecIPAddress.contains("255.255.255.255")) {
			return inDecIPAddress;
		}

		for (int i = 0; i < ip.length; i++) {
			try {
				Integer octet = new Integer(ip[i].trim());
				// if this is the first octect test to see if it is a valid
				// a,b or address in the range of 1 to 223
				if (i == 0) {
					if (octet.intValue() < 1 || octet.intValue() > 223) {
						return null;
					}
					ipaddr = octet.toString();
				} else {
					if (octet.intValue() < 0 || octet.intValue() > 255) {
						return null;
					}
					ipaddr += "."+octet.toString();
				}
			} catch (NumberFormatException e) {
				return null;
			}
			try {
				// This code check to make sure the host id of an ipaddress is
				// also valid.
				boolean x = IPV4Address.checkHostId(IPV4Address.toBinaryString(IPV4Address
						.getDefaultSubnetMask(inDecIPAddress)), IPV4Address.toBinaryString(inDecIPAddress));
				if (!x) {
					return null;
				}
			} catch (Exception e) {
				return null;
			} // This should never happen! (fingers crossed)
		}
		return ipaddr; // if it has passed all of these tests return true
	}
	
	/**
	 * This is a static method that will sanitize the IP Address valid IPV4 address or null
	 * 
	 * @author QweR
	 * @param inIPAddress
	 *            - The IP address to be tested eg: 192.168.0.2
	 * @return String 
	 * @version v0.20
	 */
	public static String sanitizeDecIPorName(String inDecIPAddress) {
		String ip = sanitizeDecIP(inDecIPAddress);
		if(ip==null) return inDecIPAddress;
		else return ip;
	}
	
	/**
	 * This static method will validate a subnet mask against the passed in IP
	 * Addres. If found as a Class A, B, C and passes substring test return
	 * true. Else, return false.
	 * 
	 * @author luke_hamilton
	 * @param inDecimalSubnetIp
	 *            - Subnet IP address in decimal
	 * @return boolean
	 * @version v0.20
	 */
	public static boolean validateDecSubnetMask(String inDecimalSubnetIp) {
		String[] ipn = inDecimalSubnetIp.split("\\.");
		try {
			if (ipn.length == 4) {
				for (int i = 0; i < 4; i++) {
					int num = Integer.parseInt(ipn[i].trim());
					if (num < 0 || num > 255)
						throw new NumberFormatException();
				}
				return true;
			}
		} catch (NumberFormatException e) {
		}
		;
		return false;
		/*
		 * try { String binSubnetMask = toBinaryString(inDecimalSubnetIp);
		 * 
		 * if(getDefaultSubnetMaskClassType(inDecIPAddress) == CLASS_A_TYPE){
		 * if(binSubnetMask.substring(0,8).equals("11111111")){
		 * if(validateSubString(binSubnetMask.substring(8,32))){ return true; }
		 * return false; } return false; } else
		 * if(getDefaultSubnetMaskClassType(inDecIPAddress) == CLASS_B_TYPE){
		 * if(binSubnetMask.substring(0,16).equals("1111111111111111")){
		 * if(validateSubString(binSubnetMask.substring(16,32))){ return true; }
		 * return false; } return false;
		 * 
		 * }else{
		 * if(binSubnetMask.substring(0,24).equals("111111111111111111111111")){
		 * if(validateSubString(binSubnetMask.substring(24,32))){ return true; }
		 * return false; } return false; } } catch (Exception e) { return false;
		 * }
		 */
	}

	public static String IPandMask(String binIP, String binMask) {
		String b1, b2, b3;
		String result = "";
		for (int i = 0; i < 32; i++) {
			b1 = binIP.substring(i, i + 1);
			b2 = binMask.substring(i, i + 1);
			if (b1.contains("1") && b2.contains("1"))
				b3 = "1";
			else
				b3 = "0";
			result = result + b3;
		}
		return result;
	}

	/**
	 * This method will test the passed in ipaddress to see if its on the same
	 * network/Subnet as this IP address. It will return true if on the same
	 * network. Else, false.
	 * 
	 * @author luke_hamilton
	 * @param inIPAddress
	 *            - IP address of node eg: 192.168.0.2
	 * @return boolean
	 * @version v0.20
	 */
	public boolean compareToSubnet(String inIPAddress) {
		String subIP; // Substring of the current ip address
		String subInIP; // substring of the passed in ip address

		try {
			int i = getNetworkIDBits();

			subIP = IPV4Address.IPandMask(binaryIpAddress, binarySubnetMask); // Create
																				// substring
																				// containing
																				// only
																				// the
																				// network
																				// bits
																				// of
																				// the
																				// binary
																				// address

			subInIP = IPV4Address.IPandMask(IPV4Address.toBinaryString(inIPAddress), binarySubnetMask);

			if (subIP.equals(subInIP)) { // Compare the two substrings
				return true;
			}
			return false;
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * int i = getNetworkIDBits();
	 * 
	 * subIP = binaryIpAddress.substring(0,i); //Create substring containing
	 * only the network bits of the binary address subInIP =
	 * IPV4Address.toBinaryString(inIPAddress).substring(0,getNetworkIDBits());
	 * 
	 * if(subIP.equals(subInIP)){ //Compare the two substrings return true; }
	 */

	public static long IPString2Number(String sip) {
		long nip = 0;
		String[] asip = sip.split("\\.");
		if (asip.length == 4) {
			for (int i = 0; i < 4; i++) {
				try {
					int val = Integer.parseInt(asip[i].trim());
					if (val >= 0 && val <= 255) {
						nip = (nip << 8) + val;
					} else
						return -1;
				} catch (NumberFormatException e) {
					return -1;
				}
			}
		} else
			return -1;
		return nip;
	}

	public static boolean isBetween(String ip, String low, String high) {
		boolean result = false;
		long nip = IPString2Number(ip);
		long nlow = IPString2Number(low);
		long nhigh = IPString2Number(high);
		if (nip > -1 && nlow > -1 && nhigh > -1) {
			result = (nip >= nlow && nip <= nhigh);
		}
		return result;
	}

}// EOF
