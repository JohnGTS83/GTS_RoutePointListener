package com.s5.gtl;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GTConfig {

	private String IpServer;
	private String SqlServerName;
	private String DataBase;
	private String PasswordDB;
	private String UserDB;
	private String PortListener;
	private String Company;
	private String TimeZoneHourId;
	private String CompileType;
	private String ServiceName;
	private boolean devMode;
	private String PortHttp;
	private String LivePasswordDB;
	private String LiveUserDB;
	private String LiveDataBase;
	private String AlertListener;

	private static GTConfig instance = null;

	public static GTConfig getInstance() throws ParserConfigurationException, SAXException, IOException {

		if (instance != null) {
			return instance;
		} else {
			instance = new GTConfig(new File("GTL.xml"));
			return instance;
		}
	}

	public GTConfig(File configFile) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = (Document) dBuilder.parse(configFile);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName("Config");
		Node node = nodes.item(0);

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;

			IpServer = getValue("IpServer", element);
			SqlServerName = getValue("SqlServerName", element);
			DataBase = getValue("DataBase", element);
			PasswordDB = getValue("PasswordDB", element);
			UserDB = getValue("UserDB", element);
			Company = getValue("Company", element);
			TimeZoneHourId = getValue("TimeZoneHourId", element);
			String devemode = getValue("DevMode", element);
			devMode = Boolean.parseBoolean(devemode);
			LiveUserDB = getValue("LiveUserDB", element);  //live db connection details
			LivePasswordDB = getValue("LivePasswordDB", element);
			LiveDataBase = getValue("LiveDataBase", element);
			AlertListener = getValue("AlertListener", element);
		}
	}

	private String getValue(String tag, Element element) {
//		System.out.println("Tag >>>"+tag+"   element >>>>"+element);
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		System.out.println("Tag >>>"+tag+"   node >>>>"+node.getNodeValue());
		return node.getNodeValue();
	}

	public String getIpServer() {
		return IpServer;
	}

	public void setIpServer(String ipServer) {
		IpServer = ipServer;
	}

	public String getSqlServerName() {
		return SqlServerName;
	}

	public void setSqlServerName(String sqlServerName) {
		SqlServerName = sqlServerName;
	}

	public String getDataBase() {
		return DataBase;
	}

	public void setDataBase(String dataBase) {
		DataBase = dataBase;
	}

	public String getPasswordDB() {
		return PasswordDB;
	}

	public void setPasswordDB(String passwordDB) {
		PasswordDB = passwordDB;
	}

	public String getUserDB() {
		return UserDB;
	}

	public void setUserDB(String userDB) {
		UserDB = userDB;
	}

	public String getPortListener() {
		return PortListener;
	}

	public void setPortListener(String portListener) {
		PortListener = portListener;
	}

	public String getCompany() {
		return Company;
	}

	public void setCompany(String company) {
		Company = company;
	}

	public String getTimeZoneHourId() {
		return TimeZoneHourId;
	}

	public void setTimeZoneHourId(String timeZoneHourId) {
		TimeZoneHourId = timeZoneHourId;
	}

	public String getCompileType() {
		return CompileType;
	}

	public void setCompileType(String compileType) {
		CompileType = compileType;
	}

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}

	public String getPortHttp() {
		return PortHttp;
	}

	public void setPortHttp(String portHttp) {
		PortHttp = portHttp;
	}

	public String getLivePasswordDB() {
		return LivePasswordDB;
	}

	public void setLivePasswordDB(String livePasswordDB) {
		LivePasswordDB = livePasswordDB;
	}

	public String getLiveUserDB() {
		return LiveUserDB;
	}

	public void setLiveUserDB(String liveUserDB) {
		LiveUserDB = liveUserDB;
	}

	public String getLiveDataBase() {
		return LiveDataBase;
	}

	public void setLiveDataBase(String liveDataBase) {
		LiveDataBase = liveDataBase;
	}

	public String getAlertListener() {
		return AlertListener;
	}

	public void setAlertListener(String alertListener) {
		AlertListener = alertListener;
	}

}
