package com.alonapps.muniapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class XmlFetcher
{

	// String base_xml =
	// "http://webservices.nextbus.com/service/publicXMLFeed?";
	Context appContext;
	DocumentBuilderFactory dbf;
	DocumentBuilder db;
	Document doc;

	public XmlFetcher(Context context)
	{
		appContext = context;

	}

	public List<Route> GetRouteList()
	{

		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_route_list_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);

		Document doc = getDocumentFromXml(command_url);

		NodeList list = doc.getElementsByTagName("route");
		List<Route> routeList = new ArrayList<Route>(list.getLength());

		for (int i = 0; i < list.getLength(); i++)
		{
			NamedNodeMap attrs = list.item(i).getAttributes();
			Route tempRoute = new Route();
			tempRoute.setTag(attrs.getNamedItem("tag").getTextContent());
			tempRoute.setTitle(attrs.getNamedItem("title").getTextContent());
			routeList.add(tempRoute);

		}
		return routeList;
	}

	// public List<String> GetStopsList(String routeName) {
	public List<Stop> GetStopsList(String routeName)
	{

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
		if (doc == null) // some error occurred
			return stopList;
		NodeList listOfStops = doc.getElementsByTagName("route").item(0)
				.getChildNodes();
		for (int i = 0; i < listOfStops.getLength(); i++)
		{
			if (listOfStops.item(i).getNodeName().equalsIgnoreCase("stop") == false)
				continue;

			NamedNodeMap attrs = listOfStops.item(i).getAttributes();
			Stop tempStop = new Stop();
			tempStop.setTag(attrs.getNamedItem("tag").getTextContent());
			tempStop.setTitle(attrs.getNamedItem("title").getTextContent());
			tempStop.setLat(Double.parseDouble(attrs.getNamedItem("lat")
					.getTextContent()));
			tempStop.setLong(Double.parseDouble(attrs.getNamedItem("lon")
					.getTextContent()));
			tempStop.setStopId(Integer.parseInt(attrs.getNamedItem("stopId")
					.getTextContent()));
			stopList.add(tempStop);

		}
		return stopList;
	}

	/*
	 * final private UIHandler handler = this.appContext.new UIHandler(); This
	 * uses DOM approach
	 */
	private Document getDocumentFromXml(String str_url)
	{

		try
		{

			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			// final String encodedUrl = URLEncoder.encode(str_url, "UTF-8");
			URL myUrl = new URL(str_url.replace(" ", "%20"));
			doc = db.parse(myUrl.openStream());

		} catch (Exception e)
		{
			Log.d(getClass().getName(),
					"Some error with XML parking or fetching from URL"
							+ e.getMessage());
			/* error with parding of the xml */
		}

		return doc;
	}

	/* This uses XMLPullParser (SAX Approach) */
	private List parseXML(InputStream input) throws XmlPullParserException,
			IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.NO_NAMESPACE, false);
			parser.setInput(input, null);
			parser.nextTag();
			return readFeed(parser);

		} finally
		{
			input.close();
		}
	}

	private List readFeed(XmlPullParser parser) throws XmlPullParserException,
			IOException
	{
		List<Route> routes = new ArrayList<Route>();

		parser.require(XmlPullParser.START_TAG, null, "body");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			// Look for "route" tag
			if (name.equalsIgnoreCase("route"))
			{
				routes.add(readNewRoute(parser));
			} else
			{
				// good or skipping over unwanted tags...
			}

		}
		return routes;
	}

	private Route readNewRoute(XmlPullParser parser)
	{
		Route singleRoute = new Route();

		
		
		return singleRoute;
	}

}







