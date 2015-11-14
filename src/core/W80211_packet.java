package core;

public class W80211_packet extends Packet {
	private String srcBSSID;
	private String dstBSSID;
	
	public int Type;
	public int subType;
	public String SSID;
	
	public boolean shared = false;
	public int auth_seq = 0;
	
	public String WEP;
	public int keyNum;
	
	private static long pCnt = 0;
	
	private long pID;
	
	public Long getID(){
		return Long.valueOf(pID);
	}
	
	public void setID(Long nID){
		pID = nID;
	}
	
	public void Cypher(String inWEP, int inkeyNum){
		WEP = inWEP;
		keyNum = inkeyNum;
	}
	
	public void setAuth(boolean inShared, int seq){
		shared = inShared;
		auth_seq = seq;
	}
	
	public W80211_packet(Packet inPacket,  String inSrc, String inDst, int inType, int inSubType, String inSSID)
	{
		dstBSSID = inDst;
		Data = inPacket;
		srcBSSID = inSrc;
		Type = inType;
		subType = inSubType;
		SSID = inSSID;
		WEP = "";
		keyNum = 0;
		
		pID = W80211_packet.pCnt;
		W80211_packet.pCnt++;		
		if(W80211_packet.pCnt > 6500000) W80211_packet.pCnt=0;
	}

	public String getSrcBSSID(){
		return srcBSSID;
	}
	
	public void setDstBSSID(String inDst){
		dstBSSID = inDst;
	}
	
	
	public String getDstBSSID(){
		return dstBSSID;
	}
}
