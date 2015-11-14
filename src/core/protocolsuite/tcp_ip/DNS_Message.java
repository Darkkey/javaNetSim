/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core.protocolsuite.tcp_ip;

import java.util.Vector;

/**
 *
 * @author Gek
 */
public class DNS_Message
{
    private static int Counter = 1;
    //header
    private int id;
    private String flags; //length = 16
    //content
    
    private Vector<Query> queries;
    private Vector<Answer> answers;
    private Vector<Object> accesses;	//for future realizations
    private Vector<Object> additionals;	//for future realizations
    public static final int TTL_DEFAULT = 86400;	// 86400s = 24h
    public static final int CLASS_IN = 1;	//class of request = 1
    public static final int A_QUERY_TYPE = 1;
    public static final int CNAME_QUERY_TYPE = 5;
    public static final int PTR_QUERY_TYPE = 12;
    public static final int HINFO_QUERY_TYPE = 13;
    public static final int MX_QUERY_TYPE = 15;

    
    public DNS_Message(int new_id, int QR, int opcode, int AA, int TC, int RD, int RA, int rcode, Vector<Query> query, Vector<Answer> answer, Vector<Object> access, Vector<Object> addit)
    {
        //create DNS-message with known simple parameters
        if(new_id==0){
        	if (DNS_Message.Counter == 16777216)
                DNS_Message.Counter = 0;
	        DNS_Message.Counter++;
	        id = DNS_Message.Counter;
        }
        else{
        	id = new_id;
        }
        
        //convert flags to 2-bytes string
        flags=IntToStr2((QR << 15)+(opcode << 11) + (AA << 10) +
                (TC << 9) + (RD << 8) + (RA << 7) + (rcode));
        
        queries = query;
        if ((QR==1) && (rcode != 3))
        {
            answers = answer;
        }
        else
        {
            answers = new Vector<Answer>(0);
        }
        //May be parse access and form like answers
        if(access==null)
        	this.accesses = new Vector<Object>(0);
        //May be parse additional and form like answers
        if(addit==null)
        	this.additionals = new Vector<Object>(0);
    }
    
    public DNS_Message(String mess)
    {
        //create object DNS-message with known header and data in string
        //parse input string and set fields of object
        id = Str2ToInt(mess.substring(0, 2));
        flags = mess.substring(2, 4);
        int countOfQuery = Str2ToInt(mess.substring(4, 6));
        int countOfAnswers = Str2ToInt(mess.substring(6, 8));
//        int countOfAccesses = Str2ToInt(mess.substring(8, 10));
//        int countOfAddons = Str2ToInt(mess.substring(10, 12));

        queries = new Vector<Query>(1);
        answers = new Vector<Answer>(0);
	    accesses = new Vector<Object>(0);
	    additionals = new Vector<Object>(0);
        
        int begin = 12;
        for(int i=0; i<countOfQuery; i++){
	        Query qu = parseQuery(mess, begin); 
	        begin += qu.length;
	        queries.add(qu);
        }
        for(int i=0; i<countOfAnswers; i++){
	        Answer ans = parseAnswer(mess, begin); 
	        begin += ans.length;
	        answers.add(ans);
        }
//        for(int i=0; i<countOfAccesses; i++){
//        	Access acc = parseAccess(mess, begin); 
//	        begin += acc.length;
//	        accesses.add(ans);
//        }
//        for(int i=0; i<countOfAddons; i++){
//	        Addon add = parseAddon(mess, begin); 
//	        begin += add.length;
//	        additionals.add(add);
//        }
    }

    
    @Override
	public String toString()
    {
        String resStr = IntToStr2(id) + flags + IntToStr2(queries.size()) + IntToStr2(answers.size()) + IntToStr2(accesses.size()) + IntToStr2(additionals.size());
        resStr += createQueries() + createAnswers(); // + creareAccess() + createAdditional(); 
        return resStr;
    }
    
    public int getID()
    {
        return id;
    }
    
    public int getRCode()
    {
        return (flags.codePointAt(1) & 15);
    }
    
    public Vector<Query> getQuery()
    {
        return queries;
    }
    
    public Vector<Answer> getAnswer()
    {
        return answers;
    }
    
    private String createQueries()
    {
    	String resQuery = "";
        for(int i=0; i<queries.size(); i++){
            resQuery += createQuery(queries.get(i));
        }
        return resQuery;
    }
    
    private String createAnswers()
    {
    	String resAnswer = "";
        for (int i = 0; i<answers.size(); i++){
            resAnswer += createAnswer(answers.get(i));
        }
        return resAnswer;
    }
    
    private String createQuery(Query qu) 
    {
    	String resQuery = createDomainName(qu.name);
        resQuery += IntToStr2(qu.type);
        resQuery += IntToStr2(DNS_Message.CLASS_IN);
        return resQuery;
    }
    
    private String createAnswer(Answer ans)
    {
    	String resAnswer = createQuery(ans);
        resAnswer += IntToStr4(DNS_Message.TTL_DEFAULT);
        switch(ans.type){
            case A_QUERY_TYPE: {
            	String[] ip = ans.resource.split("\\.");
            	resAnswer += IntToStr2(4) + (char)Integer.parseInt(ip[0])
	            	+ (char)Integer.parseInt(ip[1])
	            	+ (char)Integer.parseInt(ip[2])
	            	+ (char)Integer.parseInt(ip[3]);
            	break;
            }
            case PTR_QUERY_TYPE: 
            case CNAME_QUERY_TYPE: {
            	String server = createDomainName(ans.resource);
            	String len = IntToStr2(server.length());
            	resAnswer += len + server;
            	break;
            }
            case HINFO_QUERY_TYPE: {
            	String hinfo = ans.resource.substring(0,Math.min(ans.resource.length(), 255));
            	String server = (char)hinfo.length() + hinfo;
            	String len = IntToStr2(server.length());
            	resAnswer += len + server;
            	break;
            }
            case MX_QUERY_TYPE: {
                String priority = IntToStr2(ans.priority);
                String server = createDomainName(ans.resource);
                String len = IntToStr2(server.length()+2);
                resAnswer += len + priority + server;
            	break;
            }
        }
        return resAnswer;
    }
    
    public String createDomainName(String in){
    	String out = "";
        String[] domains = in.split("\\u002E");	// u002E aka .(dot)
        for (int i = 0; i < domains.length; i++) 
        {
        	String dname = domains[i].substring(0,Math.min(domains[i].length(),63));	// max length of domain is 63 symbols
            out += (char)dname.length() + dname;
        }
        out += '\u0000';
        return out;
    }
    
    public DName parseDomainName(String str, int begin){
    	int cur = begin;
        String qName = new String();
        int k = str.codePointAt(cur);
        while(k!=0)
        {
        	int oldBegin = cur+1;
        	cur += k+1;
            k = str.codePointAt(cur);
            qName += str.substring(oldBegin, cur) + (k==0?"":".");
        }
        return new DName(qName,cur+1-begin);
    }
    
    /**
     * This method extract from input string the queries string
     */
    private Query parseQuery(String str, int begin)
    {
    	int cur = begin;
    	DName dname = parseDomainName(str, cur);
    	cur += dname.length;
        Integer qType = new Integer(Str2ToInt(str.substring(cur,cur+2)));
        return new Query(dname.name,qType,cur+4-begin);
    }
    
    /**
     * This method extract from input string the answers string
     */
    private Answer parseAnswer(String str, int begin)
    {
    	int cur = begin;
        Query qu = parseQuery(str, cur);
        cur += qu.length;
        int ttl = Str4ToInt(str.substring(cur,cur+4));
        cur += 4;
        int len = Str2ToInt(str.substring(cur,cur+2));
        cur += 2;
        String aRes = "";
        int priority = 0;
        switch(qu.type){
	        case A_QUERY_TYPE:{
	        	if(len==4){
	        		aRes = Integer.toString(str.codePointAt(cur))+"."+Integer.toString(str.codePointAt(cur+1))+"."+Integer.toString(str.codePointAt(cur+2))+"."+Integer.toString(str.codePointAt(cur+3));
	        	}
	        	break;
	        }
	        case PTR_QUERY_TYPE:
	        case CNAME_QUERY_TYPE:{
	        	DName dname = parseDomainName(str, cur);
	        	aRes = dname.name;
	        	break;
	        }
	        case HINFO_QUERY_TYPE:{
	        	int hilen = str.codePointAt(cur);
	        	aRes = str.substring(cur+1,cur+hilen+1);
	        	break;
	        }
	        case MX_QUERY_TYPE:{
	        	priority = Str2ToInt(str.substring(cur,cur+2));
	        	DName dname = parseDomainName(str, cur+2);
	        	aRes = dname.name;
	        	break;
	        }
        }
        cur += len;        
        return new Answer(qu.name,qu.type,ttl,aRes,priority,cur-begin);
    }
    
    /**
    * This method return string with two elements that contain each of byte of value.
    * @param value to convert
    * @author Gek
    * @version v0.01
    */
    private String IntToStr2(int value)
    {
        char[] ch = new char[2];
        ch[0] = (char)((value>>8) & 0xFF);
        ch[1] = (char)(value & 0xFF);
        return String.copyValueOf(ch);
    }    
    
    private int Str2ToInt(String value)
    {
        return (value.codePointAt(0)<<8)+value.codePointAt(1);
    }

    private String IntToStr4(int value)
    {
        char[] ch = new char[4];
        ch[0] = (char)((value>>24) & 0xFF);
        ch[1] = (char)((value>>16) & 0xFF);
        ch[2] = (char)((value>>8) & 0xFF);
        ch[3] = (char)(value & 0xFF);
        return String.copyValueOf(ch);
    }  
    
    private int Str4ToInt(String value)
    {
        return (((((value.codePointAt(0)<<8)+value.codePointAt(1))<<8)+value.codePointAt(2))<<8)+value.codePointAt(3);
    }
    
    static public int getTypeInt(String t){
    	if(t.equalsIgnoreCase("a")){
    		return DNS_Message.A_QUERY_TYPE;
    	}
    	else if(t.equalsIgnoreCase("ptr")){
    		return DNS_Message.PTR_QUERY_TYPE;
    	}
    	else if(t.equalsIgnoreCase("cname")){
    		return DNS_Message.CNAME_QUERY_TYPE;
    	}
    	else if(t.equalsIgnoreCase("hinfo")){
    		return DNS_Message.HINFO_QUERY_TYPE;
    	}
    	else if(t.equalsIgnoreCase("mx")){
    		return DNS_Message.MX_QUERY_TYPE;
    	}
    	return 0;
    }
 
    static public String getTypeString(int t){
    	if(t == DNS_Message.A_QUERY_TYPE){
    		return "A";
    	}
    	else if(t == DNS_Message.PTR_QUERY_TYPE){
    		return "PTR";
    	}
    	else if(t == DNS_Message.CNAME_QUERY_TYPE){
    		return "CNAME";
    	}
    	else if(t == DNS_Message.HINFO_QUERY_TYPE){
    		return "HINFO";
    	}
    	else if(t == DNS_Message.MX_QUERY_TYPE){
    		return "MX";
    	}
    	return "";
    }
    
    static public class DName {
    	public String name;
    	public int length;
    	public DName(String m_name, int m_length){
    		name = m_name;
    		length = m_length;
    	}
    }
    
    static public class Query extends DName{
    	public int	type;
    	public Query(String m_name, int m_type, int m_length){
    		super(m_name, m_length);
    		type = m_type;
    	}
    }
    
    static public class Answer extends Query {
    	public int ttl;
    	public String resource;
    	public int priority; //MX only
    	public Answer(String m_name, int m_type, int m_ttl, String m_resource, int m_priority, int m_length){
    		super(m_name, m_type, m_length);
    		ttl = m_ttl;
    		resource = m_resource;
    		priority = m_priority;
    	}
    }
};
