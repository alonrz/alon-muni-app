package com.alonapps.muniapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
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

	public List<String> GetRouteList() {
		List<String> routeList = new ArrayList<String>();

		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_route_list_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);

		Document doc = getDocumentFromXml(command_url);

		NodeList list = doc.getElementsByTagName("route");
		for (int i = 0; i < doc.getElementsByTagName("route").getLength(); i++) {
			routeList.add(list.item(i).getAttributes().getNamedItem("title")
					.getTextContent());

		}
		return routeList;
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
