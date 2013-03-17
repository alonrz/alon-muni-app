package com.alonapps.muniapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class XmlFetcher {

	// String base_xml =
	// "http://webservices.nextbus.com/service/publicXMLFeed?";
	Context appContext;
	DocumentBuilderFactory dbf;
	DocumentBuilder db;
	Document doc;

	public XmlFetcher(Context context) {
		appContext = context;

	}

	public List<Route> GetRouteList() {
		List<Route> routeList = new ArrayList<Route>();

		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_route_list_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);

		Document doc = getDocumentFromXml(command_url);

		NodeList list = doc.getElementsByTagName("route");
		for (int i = 0; i < list.getLength(); i++) {
			NamedNodeMap attrs = list.item(i).getAttributes();
			Route tempRoute = new Route();
			tempRoute.setTag(attrs.getNamedItem("tag").getTextContent());
			tempRoute.setTitle(attrs.getNamedItem("title").getTextContent());
			routeList.add(tempRoute);

		}
		return routeList;
	}

	//public List<String> GetStopsList(String routeName) {
	public List<Stop> GetStopsList(String routeName) {
		// TODO Auto-generated method stub
		//List<String> stopsList2 = new ArrayList<String>();
		List<Stop> stopList = new ArrayList<Stop>();
		
		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_stops_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_route_tag);
		command_url += "=";
		command_url += routeName;
		
		Document doc = getDocumentFromXml(command_url);

		NodeList listOfStops = doc.getElementsByTagName("route").item(0).getChildNodes();
		for (int i = 0; i < listOfStops.getLength(); i++) {
			if(listOfStops.item(i).getNodeName().equalsIgnoreCase("stop") == false)
				continue;
			
			NamedNodeMap attrs = listOfStops.item(i).getAttributes();
			Stop tempStop = new Stop();
			tempStop.setTag(attrs.getNamedItem("tag").getTextContent());
			tempStop.setTitle(attrs.getNamedItem("title").getTextContent());
			tempStop.setLat(Double.parseDouble(attrs.getNamedItem("lat").getTextContent()));
			tempStop.setLong(Double.parseDouble(attrs.getNamedItem("lon").getTextContent()));
			tempStop.setStopId(Integer.parseInt(attrs.getNamedItem("stopId").getTextContent()));
			stopList.add(tempStop);
			
//			stopsList2.add(listOfStops.item(i).getAttributes().getNamedItem("title")
//					.getTextContent());

		}
		return stopList;
	}
	
	// final private UIHandler handler = this.appContext.new UIHandler();
	private Document getDocumentFromXml(String str_url) {

		try {

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(new URL(str_url).openStream());
		} catch (Exception e) {
			Log.d(getClass().getName(),
					"Some error with XML parking or fetching from URL"
							+ e.getMessage());
			/* error with parding of the xml */
		}

		return doc;
	}



}
