package com.alonapps.muniapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alonapps.muniapp.Route.Direction;
import android.content.Context;
import android.graphics.Color;
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

	public List<Route> loadAllRoutesWithDirections()
	{

		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_routeconfig_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_terse_command);

		/*** SAX ***/
		return getRoutesListFromXml(command_url);

		/*** DOM ***/
		// Document doc = getDocumentFromXml(command_url);
		//
		// NodeList list = doc.getElementsByTagName("route");
		// List<Route> routeList = new ArrayList<Route>(list.getLength());
		//
		// for (int i = 0; i < list.getLength(); i++)
		// {
		// NamedNodeMap attrs = list.item(i).getAttributes();
		// Route tempRoute = new Route();
		// tempRoute.setTag(attrs.getNamedItem("tag").getTextContent());
		// tempRoute.setTitle(attrs.getNamedItem("title").getTextContent());
		// routeList.add(tempRoute);
		//
		// }
		// return routeList;

	}

	/*
	 * This uses DOM approach
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

	/*
	 * This uses XmlPullParser (SAX) approach
	 */
	private List<Route> getRoutesListFromXml(String str_url)
	{
		List<Route> routes = new ArrayList<Route>();
		try
		{
			URL myUrl = new URL(str_url.replace(" ", "%20"));
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream input = conn.getInputStream();
			/*** testing - print to log ****/
			/*
			 * byte[] mybytes = new byte[500]; String str = new String();
			 * while(input.read(mybytes) >=0) { str += new String(mybytes); }
			 * Log.i("TEST", str); return routes;
			 */
			/*******************************/
			routes = parseXML(input);

		} catch (Exception e)
		{
			Log.d(getClass().getName(),
					"Some error with XML parking or fetching from URL"
							+ e.getMessage());
			/* error with parding of the xml */
		}

		return routes;
	}

	/* This uses XMLPullParser (SAX Approach) */
	private List<Route> parseXML(InputStream input)
			throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			// XmlPullParser parser =
			// XmlPullParserFactory.newInstance().newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			parser.nextTag();

			return readFeed(parser);

		} catch (Exception e)
		{
			Log.i("Error with XmlPullParser", e.getMessage());
			return null;
		} finally
		{
			input.close();
		}
	}

	private List<Route> readFeed(XmlPullParser parser)
	{
		List<Route> routes = new ArrayList<Route>();

		try
		{
			parser.require(XmlPullParser.START_TAG, null, "body");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{

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

				eventType = parser.next();
			}
		} catch (Exception e)
		{
			Log.e("readFeed func", e.getMessage());
		}
		return routes;
	}

	private Route readNewRoute(XmlPullParser parser)
	{
		Route singleRoute = new Route();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "route");

			// Process Attributes
			singleRoute.setTag(parser.getAttributeValue(null, "tag"));
			singleRoute.setTitle(parser.getAttributeValue(null, "title"));
			final String colorString = parser.getAttributeValue(null, "color");
			singleRoute.setColor(Color.parseColor("#" + colorString));
			singleRoute.setMinLat(Double.parseDouble(parser.getAttributeValue(
					null, "latMin")));
			singleRoute.setMinLon(Double.parseDouble(parser.getAttributeValue(
					null, "lonMin")));
			singleRoute.setMaxLat(Double.parseDouble(parser.getAttributeValue(
					null, "latMax")));
			singleRoute.setMaxLon(Double.parseDouble(parser.getAttributeValue(
					null, "lonMax")));

			int eventType = parser.next();
			while (eventType != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					if (name.equalsIgnoreCase("stop"))
					{
						singleRoute.addStop(readStop(parser, singleRoute));
					} else if (name.equalsIgnoreCase("direction"))
					{
						singleRoute.addDirection(readDirection(parser,
								singleRoute));
					} else
					// take care of the <path> element
					{
						skip(parser);
					}
				}
				eventType = parser.next(); // This is instead of placing this
											// inside
											// the while loop. easy debugging.
			}
		} catch (Exception e)
		{
			Log.e("readNewRoute func", e.getMessage());
		}
		return singleRoute;
	}

	private Direction readDirection(XmlPullParser parser, Route r)
	{
		Route.Direction mydir = r.new Direction();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "direction");
			mydir.setTag(parser.getAttributeValue(null, "tag"));
			mydir.setTitle(parser.getAttributeValue(null, "title"));
			mydir.setName(parser.getAttributeValue(null, "name"));

			int eventType = parser.next();
			while (eventType != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					if (name.equalsIgnoreCase("stop"))
					{
						String strTag = parser.getAttributeValue(null, "tag");
						int nTag = Integer.parseInt(strTag);
						mydir.addStopTag(nTag);
						parser.nextTag();
					} else
					{
						// take care of the <path> element
						skip(parser);
					}
				}
				eventType = parser.next();
			}
		} catch (Exception e)
		{
			Log.e("readDirection func", e.getMessage());
		}
		return mydir;
	}

	private Route.Stop readStop(XmlPullParser parser, Route r)
	{
		Route.Stop mystop = r.new Stop();

		try
		{
			parser.require(XmlPullParser.START_TAG, null, "stop");

			mystop.setTag(Integer.parseInt(parser
					.getAttributeValue(null, "tag")));
			mystop.setTitle(parser.getAttributeValue(null, "title"));
			mystop.setStopID(parser.getAttributeValue(null, "stopId"));
			mystop.setLat(Double.parseDouble(parser.getAttributeValue(null,
					"lat")));
			mystop.setLon(Double.parseDouble(parser.getAttributeValue(null,
					"lon")));
			parser.next(); // to take care of the implied closing tag
		} catch (Exception e)
		{
			Log.e("readStop func", e.getMessage());
		}
		return mystop;
	}

	private void skip(XmlPullParser parser)
	{
		try
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				throw new IllegalStateException();
			}
			int depth = 1;
			while (depth != 0)
			{
				switch (parser.next())
				{
					case XmlPullParser.END_TAG:
						depth--;
						break;
					case XmlPullParser.START_TAG:
						depth++;
						break;
				}
			}
		} catch (Exception e)
		{
			Log.e("skip func", e.getMessage());
		}
	}

}
