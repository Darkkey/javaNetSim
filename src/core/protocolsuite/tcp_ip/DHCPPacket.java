/*
 * DHCPPacket.java
 *
 */

package core.protocolsuite.tcp_ip;

/**
 *
 * @author key
 */
public class DHCPPacket {
    
    public short op;
    public int xid;
    public String ciaddr;
    public String yiaddr;
    public String chaddr;
    public String WantedIP;
    public String SubnetMask;
    public String Gateway;
    public String DHCPServer;
    public int leaseTime;
    public short msgType;
    
    /** Creates a new instance of DHCPPacket */
    public DHCPPacket() {
        WantedIP = "";
        msgType = 0;
        leaseTime = -1;
        SubnetMask = "";
        Gateway = "";
        DHCPServer = "";
        ciaddr = "";
        yiaddr = "";
        chaddr = "";
        op = 0;
        xid = (int) (Math.random() * 100000);
    }
        
    public String toBytes(){
        char[] c = new char[44];
        char[] o = new char[255];
        String res = "";
        String tmp;
        int optIdx = 0;
        
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        //|     op (1)    |   htype (1)   |   hlen (1)    |   hops (1)    |
        c[0] = (char)op;
        c[1] = 1;
        c[2] = 6;
        c[3] = 0;
        //|                            xid (4)                            |
        //c[4-5-6-7]
        c[4] = (char)((xid >> 24) & 0xFF);
        c[5] = (char)((xid >> 16) & 0xFF);
        c[6] = (char)((xid >> 8) & 0xFF);
        c[7] = (char)((xid) & 0xFF);        
        //|           secs (2)            |           flags (2)           |
        c[8] = c[9] = c[10] = c[11] = 0;
        //|                          ciaddr  (4)                          |
        tmp = convertIP(ciaddr);
        c[12] = tmp.charAt(0);
        c[13] = tmp.charAt(1);
        c[14] = tmp.charAt(2);
        c[15] = tmp.charAt(3);
        //|                          yiaddr  (4)                          |
        tmp = convertIP(yiaddr);
        c[16] = tmp.charAt(0);
        c[17] = tmp.charAt(1);
        c[18] = tmp.charAt(2);
        c[19] = tmp.charAt(3);        
        //|                          siaddr  (4)                          |
        c[20] = c[21] = c[22] = c[23] = 0;
        //|                          giaddr  (4)                          |
        c[24] = c[25] = c[26] = c[27] = 0;
        //|                                                               |
        //|                          chaddr  (16)                         |
        //|                                                               |
        //|                                                               |
        
        try{
            tmp = convertMAC(chaddr);
        }catch(Exception e){
            System.out.println(e.toString());            
        }
        c[28] = tmp.charAt(0);
        c[29] = tmp.charAt(1);
        c[30] = tmp.charAt(2);
        c[31] = tmp.charAt(3);
        c[32] = tmp.charAt(4);
        c[33] = tmp.charAt(5);     
        c[34] = c[35] = c[36] = c[37] = c[38] = c[39] = c[40] = c[41] = c[42] = c[43] = 0;
        
        res = String.copyValueOf(c,0,44);               
        
        for(int i = 0; i<192; i++){
            res += " ";
        }
        
        // "magic number"
        o[0] =  99;
        o[1] = 130;
        o[2] = 83;
        o[3] = 99;
        optIdx = 4;
        
        //DHCP Message Type
        if(msgType>0){
            o[optIdx++] = 53;
            o[optIdx++] = 1;
            o[optIdx++] = (char)msgType;
        }
        
        if(WantedIP!=""){
            o[optIdx++] = 50;
            o[optIdx++] = 4;
            tmp = convertIP(WantedIP);
            o[optIdx++] = tmp.charAt(0);
            o[optIdx++] = tmp.charAt(1);
            o[optIdx++] = tmp.charAt(2);
            o[optIdx++] = tmp.charAt(3);  
        }
        
        if(SubnetMask!=""){
            o[optIdx++] = 1;
            o[optIdx++] = 4;
            tmp = convertIP(SubnetMask);
            o[optIdx++] = tmp.charAt(0);
            o[optIdx++] = tmp.charAt(1);
            o[optIdx++] = tmp.charAt(2);
            o[optIdx++] = tmp.charAt(3);  
        }
        
        if(Gateway!=""){
            o[optIdx++] = 3;
            o[optIdx++] = 4;
            tmp = convertIP(Gateway);
            o[optIdx++] = tmp.charAt(0);
            o[optIdx++] = tmp.charAt(1);
            o[optIdx++] = tmp.charAt(2);
            o[optIdx++] = tmp.charAt(3);  
        }
        
        if(leaseTime>=0){
            o[optIdx++] = 51;
            o[optIdx++] = 4;
            //tmp = Long.valueOf(leaseTime). ;
            o[optIdx++] = (char)((leaseTime >> 24) & 0xFF);
            o[optIdx++] = (char)((leaseTime >> 16) & 0xFF);
            o[optIdx++] = (char)((leaseTime >> 8) & 0xFF);
            o[optIdx++] = (char)((leaseTime) & 0xFF);
        }

        if(DHCPServer!=""){
            o[optIdx++] = 54;
            o[optIdx++] = 4;
            tmp = convertIP(DHCPServer);
            o[optIdx++] = tmp.charAt(0);
            o[optIdx++] = tmp.charAt(1);
            o[optIdx++] = tmp.charAt(2);
            o[optIdx++] = tmp.charAt(3);  
        }
        
        res += String.valueOf(o,0,optIdx);      
       
        /*for(int j=0; j<res.length(); j++){
           System.out.println(j + ":\t" + (short) res.charAt(j) );
        }*/
        
        return res;
    }
    
    private String convertMAC(String MAC){
      char[] mac =  new char[6];
      int i = 0;      
      
      for(int j = 0; j<6; j++){
          mac[j] = (char)Integer.parseInt(MAC.substring(i, i+2),16);  
          i+=3;
      }
            
      return String.copyValueOf(mac,0,6);
    }
    
    private String convertIP(char o1, char o2, char o3, char o4){
        String res;
        byte[] ip = new byte[4];
        ip[0] = (byte)o1;
        ip[1] = (byte)o2;
        ip[2] = (byte)o3;
        ip[3] = (byte)o4;
        try{ 
            res = java.net.InetAddress.getByAddress(ip).getHostAddress();
        }catch(Exception e){ 
            e.printStackTrace();
            res = ""; 
        }
        
        return res;
    }
    
    private String convertIP(String IP){
      String res = "    ";  
      
      if (IP.length()<4){
          return res;
      }
        
      try{  
        byte b[] = java.net.InetAddress.getByName(IP).getAddress();
        
        res = "";
        
        res = res + (char)b[0] + (char)b[1] + (char)b[2] + (char)b[3];
        
      }catch(Exception e){ }         
      
      return res;
    }
    
    public void fromBytes(String bytes){
        String tmp;
        int optIdx = 0;
        try{
        
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        //|     op (1)    |   htype (1)   |   hlen (1)    |   hops (1)    |
        op = (short)bytes.charAt(0);
        //|                            xid (4)                            |
        xid = 0;
        xid = xid | (((bytes.charAt(4)) << 24 ) & 0xFF000000);
        xid = xid | (((bytes.charAt(5)) << 16 ) & 0x00FF0000);
        xid = xid | (((bytes.charAt(6)) << 8 ) & 0x0000FF00);
        xid = xid | (bytes.charAt(7) & 0x000000FF);
        //|           secs (2)            |           flags (2)           |
        //|                          ciaddr  (4)                          |
        ciaddr = convertIP(bytes.charAt(12),bytes.charAt(13),bytes.charAt(14),bytes.charAt(15));
        //|                          yiaddr  (4)                          |
        yiaddr = convertIP(bytes.charAt(16),bytes.charAt(17),bytes.charAt(18),bytes.charAt(19));
        //|                          siaddr  (4)                          |
        //|                          giaddr  (4)                          |
        //|                                                               |
        //|                          chaddr  (16)                         |
        //|                                                               |
        //|                      
                
        chaddr = "";
        for(int j = 28; j<34; j++){            
            tmp = Integer.toHexString(Integer.valueOf((short)bytes.charAt(j))).toUpperCase();
            if(tmp.trim().length() < 2){
                tmp = "0" + tmp;
            }        
            chaddr += tmp;
            if(j!=33){
                chaddr += ":";
            }
        }
        
        //+10 tail of HWAddr
        //+192 Null Bytes
                      
        // "magic number"
        //[236] =  99;
        //[237] = 130;
        //[238] = 83;
        //[239] = 99;
        
        optIdx = 240;
        
        while(optIdx < bytes.length()){
            switch(bytes.charAt(optIdx)){
                case 53:
                    optIdx+=2;
                    msgType = (short)bytes.charAt(optIdx++);
                    break;
                case 50:
                    optIdx+=2;
                    WantedIP = convertIP(bytes.charAt(optIdx),bytes.charAt(optIdx+1),bytes.charAt(optIdx+2),bytes.charAt(optIdx+3));
                    optIdx+=4;
                    break;
                case 1:
                    optIdx+=2;
                    SubnetMask = convertIP(bytes.charAt(optIdx),bytes.charAt(optIdx+1),bytes.charAt(optIdx+2),bytes.charAt(optIdx+3));
                    optIdx+=4;
                    break;
                case 3:
                    optIdx+=2;
                    Gateway = convertIP(bytes.charAt(optIdx),bytes.charAt(optIdx+1),bytes.charAt(optIdx+2),bytes.charAt(optIdx+3));
                    optIdx+=4;
                    break; 
                case 54:
                    optIdx+=2;
                    DHCPServer = convertIP(bytes.charAt(optIdx),bytes.charAt(optIdx+1),bytes.charAt(optIdx+2),bytes.charAt(optIdx+3));
                    optIdx+=4;
                    break;    
                case 51:
                    optIdx+=2;
                    leaseTime = 0;
                    leaseTime = leaseTime | (((bytes.charAt(optIdx++)) << 24 ) & 0xFF000000);
                    leaseTime = leaseTime | (((bytes.charAt(optIdx++)) << 16 ) & 0x00FF0000);
                    leaseTime = leaseTime | (((bytes.charAt(optIdx++)) << 8 ) & 0x0000FF00);
                    leaseTime = leaseTime | (bytes.charAt(optIdx++) & 0x000000FF);
                    break;
            }
        }
        
        }catch(Exception e){ op = -1; }
    }
}
