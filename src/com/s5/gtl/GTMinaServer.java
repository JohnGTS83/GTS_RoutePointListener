package com.s5.gtl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import com.s5.common.Loger;
import com.s5.common.db.QueryHelper;

public class GTMinaServer {
	
	static GTConfig config;
	private static IoAcceptor acceptor;
	
	private int port;
	
	private GTMinaServer(int port) throws IOException {
		this.port = port;
		this.init(port);
	}

	public void init(int port) throws IOException {
		/*
		TimerTask timerTask = new TimerTask(){
			public void run() {
				List<RouteCustomAlartDTO> dtos = RouteCustomAlartDAO.getAllAlert();
		    	  if(dtos.size()>0){
		    		  ExecutorService oldExecutor = Executors.newFixedThreadPool(10);
		    		  for (RouteCustomAlartDTO reportDetailsDTO : dtos) {
		    			  CustomRouteAlarmProcess process = new CustomRouteAlarmProcess(reportDetailsDTO);
				    	  try {
							oldExecutor.submit(process);
				    	  } catch (Exception e) {
							e.printStackTrace();
				    	  }
		    		  }
			    	  oldExecutor.shutdown();
		    	  }
				 System.out.println("Getting custom route alarms");
		      }
		 };
		 Timer timer = new Timer();
		 timer.schedule(timerTask, 0L, 1000*60*1L);
		 */
		
//		EmailUtil.sendReportMail("Route Alert", "This is a route alert", "aawte.umesh@avibha.com,aawte.umesh@gmail.com");
		//yyyy/MM/dd HH:mm:ss
//		new GMSDispatcher("861074027436776|43.584022999999995|-80.553105|25633247|116|2021-01-25 07:38:53").run();
//		new GMSDispatcher("862951023206536|43.69536603567597|-79.50106366688523|25633247|26|2021-01-25 13:18:53").run();
		
		//TO avoide multiple connection pool creation in threads.
		try {
			QueryHelper helper = new QueryHelper();
			helper.closeConnection();
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
		try {
	    	acceptor = new NioDatagramAcceptor();
			acceptor.getFilterChain().addLast( "logger", new LoggingFilter());
	        acceptor.setHandler(new MessageServerHandler());
	        DatagramSessionConfig dcfg =  (DatagramSessionConfig) acceptor.getSessionConfig();
	        dcfg.setReadBufferSize(1024);
	        acceptor.bind(new InetSocketAddress(port));
	        Loger.log(Loger.INFO,"UDP alert server listening on port " + port);
		} catch(Exception e) {
    	   e.printStackTrace();
		}
	}

	public static IoAcceptor getAcceptor() {
		if(acceptor != null)
			return acceptor;
		else
			return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		try{
			GTMinaServer.config = GTConfig.getInstance();
			if(config == null) {
				errorAndExit("GT Config is not loaded! - Listeners are not started");
			}
			try {
				 int port = Integer.parseInt(config.getAlertListener());
				 server = new GTMinaServer(port);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
//			promtKeybord();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void errorAndExit(String errorMessage){
		Loger.log(Loger.ERROR, " Error : " + errorMessage);
		System.exit(0);
	}
	
	public static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//	public static Map<Integer,GTMinaServer> serverList = new HashMap<Integer, GTMinaServer>();
	private static GTMinaServer server;

//	public static synchronized void addServerInMap(int port, GTMinaServer server){
//		serverList.put(port, server);
//	}
	public static void unbindAll(){
		try{
//			Collection<GTMinaServer> serversList = serverList.values();
//			for(GTMinaServer server:serversList){
				server.unbind();
//			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void bindAll(){
		try{
//			Collection<GTMinaServer> serversList = serverList.values();
//			for(GTMinaServer server:serversList){
				server.bind();
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
	 }
		
	 private void unbind(){
		 acceptor.unbind();
		 System.out.println("Connection unbind: " + this.port);
	 }
	 
	 public void bind(){
		try {
			acceptor.bind(new InetSocketAddress(this.port));
			System.out.println("Connection bind: " + this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 public static void promtKeybord() throws IOException{
		 while(true) {
			System.out.println("Enter Commands - bind|unbind|exit ");
			String console = keyboard.readLine();
			if (console.equals("unbind")) {
				unbindAll();
			} else if(console.equals("exit")) {
				System.out.println("Closing");
				unbindAll();
				System.exit(0);
			} else if (console.equals("bind")) {
				bindAll();
			}
		 }
	 }
}