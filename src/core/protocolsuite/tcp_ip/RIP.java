package core.protocolsuite.tcp_ip;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;
import core.TransportLayerException;

/* Формат RIP сообщения
 * Команда   1 - Request | 2 - Response/Update
 * Версия   1|2
 * Домен маршрутизации = 0
 * Идентификатор адресной схемы = AFI_INET = 2
 * Метка маршрута = 0
 * -------------------
 * IP адрес
 * Маска подсети		данный фрагмент может повторяться
 * Next hop
 * Метрика
 * -------------------
 */

public class RIP extends Application{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4031443419981254419L;
	private static final int UDP_LISTENING_PORT=520;//прослушиваемый порт на интерфейсе (по стандарту udp 520) 
	private	static final String REQUEST_MESSAGE="1|2|0|2|0||||16";//request-сообщение
	private int UPDATE_TIME=30000;//Интервал посылки update-сообщения в миллисекундах (30000 - 30cек - стандарт)

	//int runs = 0;

	private boolean blockSockets=false;//если в данный момент идет передача через сокет, то таймер не срабатывает

	//Integer - socket, Boolean - получил ли response на request
	//если не получил, то продолжать посылать request
	private Hashtable<Integer,Boolean> appSockets; 

	//Хештаблица - по номеру сокета можно узнать имя интерфейса
	private Hashtable<Integer,String> interfaces;

	/** Creates a new instance of RIP */
	public RIP(ProtocolStack inParentStack, long UID) {
		super(inParentStack, RIP.UDP_LISTENING_PORT, 1, UID);
		clientPort=listenPort;//в принципе от clientPort ничего не зависит, всё равно он равен 3000 :)
		interfaces=new Hashtable<Integer,String>();
		appSockets=new Hashtable<Integer, Boolean>();
	}

	/**
	 * This method is executed when the user hits the ok button in RIPProperties dialog
	 * 
	 * @author igorek
	 *
	 */
	public void initializeRIP(Vector<String> interfaces)throws TransportLayerException {
		//освободить все сокеты
		//очистить хештаблицы!
		this.interfaces.clear();
		Iterator<Integer> i1=appSockets.keySet().iterator();
		while (i1.hasNext()){
			int s=i1.next();
			mParentStack.SL().close(s);
			mParentStack.SL().free(s);
		}
		appSockets.clear();
		//удаляем все динамические маршруты
		String routes[] = mParentStack.getRouteTableEntries();
		Vector<String> networksToDelete=new Vector<String>();
		for(int j=0; j<routes.length - 1; j++){
			Route_entry r = mParentStack.getRouteTableEntry(routes[j]);
			if (r.Type == 1) {
				mParentStack.removeRoute(r.destIP);
			}
		}

		if (interfaces.size()==0){//хотим отключить RIP 

			mParentStack.getParentNode().cancelTimerTask(this);
			LayerInfo protInfo = new LayerInfo(getClass().getName());
			protInfo.setObjectName(mParentStack.getParentNodeName());
			protInfo.setDataType("RIP");
			protInfo.setLayer("Application ");
			protInfo.setDescription("RIP was disabled on " +mParentStack.getParentNodeName());
			Simulation.addLayerInfo(protInfo);
		}

		Iterator<String> i=interfaces.iterator();
		while (i.hasNext()){
			String iface=i.next();
			addInterface(iface);
		}
	}
	public void addInterface(String iface) throws TransportLayerException{    	
		String ifaceIP=mParentStack.getIPAddress(iface);
		if (ifaceIP==null){
			printLayerInfo("RIP", "Error: cannot bind port " + listenPort + " on "+iface+". IP address hadn't been set.");
		}
		else{
			try{	
				Enumeration<String> vals = interfaces.elements();
				boolean found = false;
				while(vals.hasMoreElements() && !found){
					found = vals.nextElement().compareToIgnoreCase(iface)==0;
				}
				if(!found){
					int appSocket=mParentStack.SL().socket(jnSocket.UDP_socket, this);	
					appSockets.put(appSocket,false);	
					this.interfaces.put(appSocket, iface);	
					printLayerInfo("RIP","Binding port " + listenPort + " on "+iface+" ("+ifaceIP+")");	
					mParentStack.SL().bind(appSocket,ifaceIP,listenPort);	
					mParentStack.SL().listen(appSocket);	
					printLayerInfo("RIP", "RIP starts listening in port " + listenPort + ".");	
					mParentStack.getParentNode().startTimerTask(this, UPDATE_TIME);	
				}
			}catch(Exception e){	
				printLayerInfo("RIP", "Error: cannot bind port " + listenPort + ".");	
				throw new TransportLayerException("Cannot bind port " + listenPort + "."); 	
			}
		}
	}
	public boolean removeInterface(String iface) throws TransportLayerException{
		String ifaceIP=mParentStack.getIPAddress(iface);
		boolean result = false;
		if (ifaceIP==null){
			printLayerInfo("RIP", "Error: cannot unbind port " + listenPort + " on "+iface+". IP address hadn't been set.");
		}
		else{
			try{	
				Enumeration<Integer> keys = interfaces.keys();
				boolean found = false;
				Integer key = null;
				while(keys.hasMoreElements() && !found){
					key = keys.nextElement();
					found = interfaces.get(key).compareToIgnoreCase(iface)==0;
				}
				if(found){
					appSockets.remove(key);
					interfaces.remove(key);
					mParentStack.SL().close(key.intValue());
					mParentStack.SL().free(key.intValue());
					result = true;
				}
				else{
					printLayerInfo("RIP", "Error: interface " + iface + " was not listened.");
				}
			}catch(Exception e){
				printLayerInfo("RIP", "Error: cannot unbind port " + listenPort + ".");
				throw new TransportLayerException("Cannot unbind port " + listenPort + "."); 
			}
		}
		return result;
	}


	/**
	 * This method sends broadcast REQUEST-message
	 * 
	 * @author igorek
	 *
	 */
	public void sendRequest(int localSocket){
		try{
			blockSockets=true;
			LayerInfo protInfo = new LayerInfo(getClass().getName());
			protInfo.setObjectName(mParentStack.getParentNodeName());
			protInfo.setDataType("RIP Data");
			protInfo.setLayer("Application ");
			protInfo.setDescription("Sending RIP broadcast request message '" +RIP.REQUEST_MESSAGE);
			Simulation.addLayerInfo(protInfo);
			//TODO 255.255.255.255 исправить на ОДНУ подсеть (192.168.0.255) 
			mParentStack.SL().writeTo(localSocket,RIP.REQUEST_MESSAGE, "255.255.255.255",listenPort);
			blockSockets=false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This method generate network address by IP and SubnetMask
	 * 192.168.0.1 255.255.255.0 -> 192.168.0.0
	 * 
	 * @author igorek
	 *
	 */
	public String networkAddressByIPandMask(String ipAddress,String mask){   	

		if(ipAddress == null) return "";

		try{
			return IPV4Address.toDecimalString(IPV4Address.IPandMask(
					IPV4Address.toBinaryString(ipAddress),
					IPV4Address.toBinaryString(mask)));
		}catch(InvalidIPAddressException e){
			return "";
		}	

	}

	/**
	 * This method generates UPDATE-message
	 * сообщение состовляется из непосредственно подключенных к интерфейсам сетей + таблица маршрутизации
	 * @author igorek
	 *
	 */
	public String createMessageWithAllRoutes(String outIFace){
		String s = "2|2|0|2|0";

		//находим все поднятые интерфейсы(учитываем только 10T,100FX,Wireless) у роутера
		ArrayList<String> ifaces=mParentStack.getParentNode().getAllInterfacesNames();
		Iterator iterator=ifaces.iterator();
		while(iterator.hasNext()){
			String iface=(String)iterator.next();
			try{
				if (mParentStack.getParentNode().isActiveInterface(iface)){
					int ifType=mParentStack.getParentNode().getIntType(iface);
					if (((ifType== core.NetworkInterface.Ethernet10T)||
							(ifType== core.NetworkInterface.Ethernet100FX)||
							(ifType== core.NetworkInterface.Serial)||
							(ifType== core.NetworkInterface.Wireless))
							&&
							outIFace != iface        			
					){
						String netwAddress=networkAddressByIPandMask(mParentStack.getIPAddress(iface), mParentStack.getSubnetMask(iface));

						if(netwAddress == "") continue;

						s+="|"+netwAddress+"|"+mParentStack.getSubnetMask(iface)+"| |0";
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//если у роутера была таблица маршрутизации, то добавляем её в сообщение
		String routes[] = mParentStack.getRouteTableEntries();        
		for(int i=0; i<routes.length - 1; i++){
			Route_entry r = mParentStack.getRouteTableEntry(routes[i]);
			if(r.iFace != outIFace && r.metric<16){
				s+="|"+routes[i] + "|" + r.genMask+"|"+ r.gateway;
				if (r.Type == 1) {
					s+="|" + r.metric;
				}else{
					s+="|1"; 
				}
			}

		}
		return s;
	}

	/**
	 * This method is разбирает пришедшее UPDATE-сообщение и, если необходимо,
	 * изменяет таблицу маршрутизации(удаляет/добавляет маршруты)
	 * 
	 * @author igorek
	 *
	 */
	public void parseMessageAndUpdateRouteTable(int localSocket,String data,String gateway){
		try{
			StringTokenizer datas=new StringTokenizer(data,"|");
			String msg[]=new String[datas.countTokens()];
			for (int i=0;i<msg.length;i++)
				msg[i]=datas.nextToken();


			for (int i=5;i<msg.length;i+=4){

				String localIP=mParentStack.getIPAddress(interfaces.get(localSocket));
				String localMask=mParentStack.getSubnetMask(interfaces.get(localSocket));

				String networkAddress=msg[i];
				String mask=msg[i+1];
				String metric=msg[i+3];

				if (networkAddressByIPandMask(localIP,localMask).equals(networkAddress) || networkAddressByIPandMask(localIP,localMask) == "")continue;

				//если пакет пришел от устройства из другой подсети
				//if (!networkAddressByIPandMask(localIP,localMask).equals(networkAddressByIPandMask(gateway,localMask)))continue;

				appSockets.put(localSocket, true);

				boolean use=true;//используем данный адрес сети или пропускаем??

				//находим все поднятые интерфейсы(учитываем только 10T,100FX,Wireless) у роутера
				//и сравниваем адреса подключенных к роутеру сетей с адресом сети, пришедшим в пакете
				//если они совпадают, то пропускаем данный адрес сети и читаем следующий
				ArrayList<String> ifaces=mParentStack.getParentNode().getAllInterfacesNames();
				Iterator iterator=ifaces.iterator();
				while(iterator.hasNext()){
					String iface=(String)iterator.next();
					try{
						if (mParentStack.getParentNode().isActiveInterface(iface)){
							int ifType=mParentStack.getParentNode().getIntType(iface);
							if ((ifType== core.NetworkInterface.Ethernet10T)||
									(ifType== core.NetworkInterface.Ethernet100FX)||
									(ifType== core.NetworkInterface.Serial)||
									(ifType== core.NetworkInterface.Wireless)){
								String netwAddressOfIface=networkAddressByIPandMask(mParentStack.getIPAddress(iface), mParentStack.getSubnetMask(iface));                		
								if (networkAddress.equals(netwAddressOfIface)){use=false;break;}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				if (!use)continue;

				Route_entry old_r=mParentStack.getRouteTableEntry(networkAddress);
				if (old_r==null){
					if (Integer.parseInt(metric)>=16)return;
					//добавляем в таблицу маршрутизации
					LayerInfo protInfo = new LayerInfo(getClass().getName());
					protInfo.setObjectName(mParentStack.getParentNodeName());
					protInfo.setDataType("RIP");
					protInfo.setLayer("Application ");
					protInfo.setDescription("Updating routing table... creating route to "+networkAddress);
					Simulation.addLayerInfo(protInfo);

					mParentStack.addRoute(new Route_entry(networkAddress,gateway,mask,mParentStack.router(gateway),1,(Integer.parseInt(metric))+1,System.currentTimeMillis()));
				}else{
					if (old_r.Type == 1) {

						if (Integer.parseInt(metric)+1<old_r.metric){
							//добавляем в таблицу маршрутизации новый маршрут(с улучшенной метрикой)
							LayerInfo protInfo = new LayerInfo(getClass().getName());
							protInfo.setObjectName(mParentStack.getParentNodeName());
							protInfo.setDataType("RIP");
							protInfo.setLayer("Application ");
							protInfo.setDescription("Updating routing table... creating route to "+networkAddress);
							Simulation.addLayerInfo(protInfo);

							mParentStack.addRoute(new Route_entry(networkAddress,gateway,mask,interfaces.get(localSocket),1,(Integer.parseInt(metric))+1,System.currentTimeMillis()));
						}

						if (Integer.parseInt(metric)+1==old_r.metric){
							old_r.createdTime=System.currentTimeMillis();
						}

						if (Integer.parseInt(metric)==16){
							mParentStack.addRoute(new Route_entry(networkAddress,gateway,mask,interfaces.get(localSocket),1,16,System.currentTimeMillis()));

							//механизм Split Horizont и Triggered Update - принудительно отправляем обновление
							Iterator<Integer> iter=appSockets.keySet().iterator();
							while(iter.hasNext()){
								int sock=iter.next();
								if (localSocket!=sock)//механизм Split Horizont
									sendUpdate(sock);//Triggered Update
							}
							//удаляем маршрут, т.к. он недостижим
							LayerInfo protInfo = new LayerInfo(getClass().getName());
							protInfo.setObjectName(mParentStack.getParentNodeName());
							protInfo.setDataType("RIP");
							protInfo.setLayer("Application ");
							protInfo.setDescription("Updating routing table... removing route to "+networkAddress);
							Simulation.addLayerInfo(protInfo);
							mParentStack.removeRoute(networkAddress);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}

	}

	/**
	 * This method is отправляет ответ на конкретный IP(не broadcast-ом)
	 * 
	 * @author igorek
	 *
	 */
	public void sendResponseTo(int localSocket,String dstIP){
		try{
			blockSockets=true;


			String data=createMessageWithAllRoutes(mParentStack.router(dstIP));

			LayerInfo protInfo = new LayerInfo(getClass().getName());
			protInfo.setObjectName(mParentStack.getParentNodeName());
			protInfo.setDataType("RIP Data");
			protInfo.setLayer("Application ");
			protInfo.setDescription("Sending RIP response message '" +data+ "' to " + dstIP + ":" + listenPort);
			Simulation.addLayerInfo(protInfo);

			if (Connect(dstIP, RIP.UDP_LISTENING_PORT))
				mParentStack.SL().writeTo(localSocket,data, dstIP,listenPort);
			blockSockets=false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * This method is отправляет broadcast-ом UPDATE-сообщение
	 * 
	 * @author igorek
	 *
	 */
	public void sendUpdate(int localSocket){
		try{
			blockSockets=true;

			String data=createMessageWithAllRoutes(mParentStack.router(mParentStack.SL().get_socket(localSocket).src_IP));
			LayerInfo protInfo = new LayerInfo(getClass().getName());
			protInfo.setObjectName(mParentStack.getParentNodeName());
			protInfo.setDataType("RIP Data");
			protInfo.setLayer("Application ");
			protInfo.setDescription("Sending RIP broadcast update message '" +data);
			Simulation.addLayerInfo(protInfo);
			//TODO 255.255.255.255 исправить на ОДНУ подсеть (192.168.0.255) 
			mParentStack.SL().writeTo(localSocket,data, "255.255.255.255",listenPort);
			blockSockets=false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This method is срабатывает каждые UPDATE_TIME*1000 секунды
	 * Если интерфейс еще не получил ответа на REQUEST сообщение, то
	 * посылается новое REQUEST-сообщение, иначе отправляется UPDATE-сообщение
	 * 
	 * @author igorek
	 *
	 */
	@Override
	public void Timer(int code){
		//runs++;
		//if(runs>4) return;

		if (blockSockets)return;
		Set<Integer> sockets=appSockets.keySet();
		Iterator<Integer> i =sockets.iterator();
		while (i.hasNext()){
			int socket=i.next();
			if (!appSockets.get(socket)){
				sendRequest(socket);
			}
			else{
				//check TTL of routes
				String routes[] = mParentStack.getRouteTableEntries();
				Vector<String> networksToDelete=new Vector<String>();
				for(int j=0; j<routes.length - 1; j++){
					Route_entry r = mParentStack.getRouteTableEntry(routes[j]);
					if (r.Type == 1) {
						if (System.currentTimeMillis()-r.createdTime>=6*UPDATE_TIME){
							networksToDelete.add(r.destIP);
							r.metric=16;
						}
					}
				}
				//send Update
				sendUpdate(socket);
				Iterator<String> it=networksToDelete.iterator();
				while(it.hasNext()){
					String netwAddress=it.next();
					mParentStack.removeRoute(netwAddress);
				}
			}
		}
	}

	/**
	 * Overriden methods
	 * 
	 * @author igorek
	 *
	 */    
	@Override
	public void Listen() throws TransportLayerException {}
	@Override
	public void Accept(int listenSock, int sessionSock) {}
	@Override
	public void OnConnect(int sock){}
	@Override
	public void OnDisconnect(int sock){}
	@Override
	public void OnError(int sock, int error){}
	@Override
	public void Close() throws TransportLayerException{}
	@Override
	public void Free() throws TransportLayerException{}



	/**
	 * This method connects to server on the other side (imaginations from the other side.... :+)
	 * This is "fake" method cos of UDP
	 * @author key
	 * @param Host - hostname or ip of server.
	 * @param port - server's port
	 * @version v0.01
	 */
	@Override
	public boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException {      
		return mParentStack.SL().connect(appSock, Host, port);
	}   


	/**
	 * This method sends data to the other side.
	 * @param data to send
	 * @author key
	 * @version v0.01
	 */

	@Override
	public void SendData(int sock, String Data) throws LowLinkException, TransportLayerException, CommunicationException{
		blockSockets=true;
		String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
		int sdPort = mParentStack.SL().get_socket(sock).dst_port;
		mParentStack.SL().writeTo(sock, Data, sdHost, sdPort);
		blockSockets=false;
	}

	/**
	 * This method recieves data from the other side.
	 * @param data to recv
	 * @author key
	 * @version v0.01
	 */
	@Override
	public void RecvData(int localSocket, String Data) throws LowLinkException, TransportLayerException {

		{
			LayerInfo protInfo = new LayerInfo(getClass().getName());
			protInfo.setObjectName(mParentStack.getParentNodeName());
			protInfo.setDataType("RIP");
			protInfo.setLayer("Application ");
			protInfo.setDescription("RIP message recieved...");
			Simulation.addLayerInfo(protInfo);
		}
		try{
			blockSockets=true;
			StringTokenizer m=new StringTokenizer(Data,"|");
			String msg[]=new String[m.countTokens()];
			for (int i=0;i<m.countTokens();i++)
				msg[i]=m.nextToken();

			String sdHost = mParentStack.SL().get_socket(localSocket).dst_IP;
			int sdPort = mParentStack.SL().get_socket(localSocket).dst_port;
			switch(Integer.parseInt(msg[0])){
			case 1:{//Recieving request msg
				//если пакет пришел от устройства из другой подсети, то выход
				String localIP=mParentStack.getIPAddress(interfaces.get(localSocket));
				String localMask=mParentStack.getSubnetMask(interfaces.get(localSocket));
				//if (!networkAddressByIPandMask(localIP,localMask).equals(networkAddressByIPandMask(sdHost,localMask)) && !sdHost.equals("255.255.255.255"))break;
				//иначе send response
				LayerInfo protInfo = new LayerInfo(getClass().getName());
				protInfo.setObjectName(mParentStack.getParentNodeName());
				protInfo.setDataType("RIP Data");
				protInfo.setLayer("Application ");
				protInfo.setDescription("Recieving RIP request message '" + Data + "' from " + sdHost + ":" + sdPort);
				Simulation.addLayerInfo(protInfo);
				sendResponseTo(localSocket,sdHost );
				blockSockets=false;
				break;
			}
			case 2:{//Recieving RESPONSE or UPDATE message
				LayerInfo protInfo = new LayerInfo(getClass().getName());
				protInfo.setObjectName(mParentStack.getParentNodeName());
				protInfo.setDataType("RIP Data");
				protInfo.setLayer("Application ");
				protInfo.setDescription("Recieving RIP update message '" + Data + "' from " + sdHost + ":" + sdPort);
				Simulation.addLayerInfo(protInfo);
				parseMessageAndUpdateRouteTable(localSocket,Data,sdHost);
				blockSockets=false;
				break;
			}

			}
		}catch(Exception e){
			System.out.println(e.toString());
		}

	}
	public Hashtable<Integer, String> getInterfaces() {
		return interfaces;
	}

	public Hashtable<Integer, Boolean> getAppSockets() {
		return appSockets;
	}
}
