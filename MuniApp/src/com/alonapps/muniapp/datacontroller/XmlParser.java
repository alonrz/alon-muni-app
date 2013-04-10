package com.alonapps.muniapp.datacontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.R.string;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Predictions.Prediction;
import com.alonapps.muniapp.datacontroller.Route.Direction;
import com.alonapps.muniapp.datacontroller.Route.Stop;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Xml;

public class XmlParser
{
	Context appContext;

	// DocumentBuilderFactory dbf;
	// DocumentBuilder db;
	// Document doc;

	public XmlParser(Context context)
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
	}

	public List<Predictions> loadPredictions(String stopId)
	{
		String command_url = appContext.getString(R.string.xml_base_command);
		command_url += appContext.getString(R.string.xml_prediction_command);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_agency_name_tag);
		command_url += "=";
		command_url += appContext.getString(R.string.xml_agency_sfmuni);
		command_url += "&";
		command_url += appContext.getString(R.string.xml_stopid_command);
		command_url += "=";
		command_url += stopId;
		command_url += "&";
		command_url += appContext.getString(R.string.xml_useShortTitles_command);
		Log.i("Command_Url", command_url);
		/*** SAX ***/
		return getPredictionFromXml(command_url);
	}

	/*
	 * This uses XmlPullParser (SAX) approach
	 */
	private List<Route> getRoutesListFromXml(String cmd_url)
	{
		List<Route> routes = new ArrayList<Route>();
		try
		{
			InputStream input = initConnection(cmd_url);
			/*** testing - print to log ****/
			/*
			 * byte[] mybytes = new byte[500]; String str = new String();
			 * while(input.read(mybytes) >=0) { str += new String(mybytes); }
			 * Log.i("TEST", str); return routes;
			 */
			/*******************************/
			routes = parseFeedForRoutes(input);
			input.close();

		} catch (Exception e)
		{
			Log.d(getClass().getName(),
					"Some error with XML parsing [getRoutesListFromXml]" + e.getMessage());
			/* error with parding of the xml */
		}

		return routes;
	}

	private List<Predictions> getPredictionFromXml(String cmd_url)
	{
		List<Predictions> predictions = new ArrayList<Predictions>();
		try
		{
			InputStream input = initConnection(cmd_url);
			predictions = parseFeedForPredictions(input);
			input.close();
		} catch (Exception e)
		{
			Log.d(getClass().getName(), "Some error with XML parsing [getPredictionFromXml]"
					+ e.getClass().toString());
		}
		return predictions;
	}

	/**
	 * Creates a connection object with a given URL and returns the input stream
	 * 
	 * @param command_url
	 *            url to send as a request
	 * @return InputStream to the connection
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private InputStream initConnection(String command_url) throws MalformedURLException,
			IOException, ProtocolException
	{
		URL myUrl = new URL(command_url.replace(" ", "%20"));
		HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		InputStream input = conn.getInputStream();
		return input;
	}

	private List<Predictions> parseFeedForPredictions(InputStream input) throws IOException
	{
		List<Predictions> predictions = new ArrayList<Predictions>();

		try
		{
			XmlPullParser parser = prepareXmlPullParser(input);

			parser.require(XmlPullParser.START_TAG, null, "body");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					// Look for "route" tag
					if (name.equalsIgnoreCase("predictions"))
					{
						predictions.add(readPredictions(parser));
					} else
					{
						// good or skipping over unwanted tags...
					}
				}

				eventType = parser.next();
			}
		} catch (Exception e)
		{
			Log.e("Error with XmlPullParser [parseFeedForPredictions]", e.getMessage());
			return null;
		} finally
		{
			input.close();
		}
		return predictions;
	}

	/* This uses XMLPullParser (SAX Approach) */
	private List<Route> parseFeedForRoutes(InputStream input) throws IOException
	{
		List<Route> routes = new ArrayList<Route>();

		try
		{
			XmlPullParser parser = prepareXmlPullParser(input);

			// Start parsing
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
						routes.add(readRoute(parser));
					} else
					{
						// good or skipping over unwanted tags...
					}
				}

				eventType = parser.next();
			}
		} catch (Exception e)
		{
			Log.e("Error with XmlPullParser [parseFeedForRoutes]", e.getMessage());
			return null;

		} finally
		{
			input.close();
		}
		return routes;
	}

	private XmlPullParser prepareXmlPullParser(InputStream input) throws XmlPullParserException,
			IOException
	{
		// init PullParser
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(input, null);
		parser.nextTag();
		return parser;
	}

	private Predictions readPredictions(XmlPullParser parser)
	{
		Predictions pred = new Predictions();
		boolean hasPredictions = true;
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "predictions");

			// Process attrs
			pred.setAgencyTitle(parser.getAttributeValue(null, "agencyTitle"));
			pred.setRouteTitle(parser.getAttributeValue(null, "routeTitle"));
			pred.setRouteTag(parser.getAttributeValue(null, "routeTag"));
			pred.setStopTitle(parser.getAttributeValue(null, "stopTitle"));
			pred.setStopTag(parser.getAttributeValue(null, "stopTag"));
			pred.setAgencyTitle(parser.getAttributeValue(null, "agencyTitle"));
			if (parser.getAttributeValue(null, "dirTitleBecauseNoPredictions") != null)
			{
				pred.setDirTitleBecauseNoPredictions(parser.getAttributeValue(null,
						"dirTitleBecauseNoPredictions"));
				hasPredictions = false;
				//return pred;
			}
			
			int eventType = parser.next();
			while (parser.getEventType() != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					if (name.equalsIgnoreCase("direction"))
					{
						pred.addDirection(readDirectionFromPrediction(parser, pred));
					} else if (name.equalsIgnoreCase("message"))
					{
						pred.addMessage(parser.getAttributeValue(null, "text"));
					} else
					{
						skip(parser);
					}
				}
				eventType = parser.next();
			}

		} catch (Exception e)
		{
			Log.e("Error with XmlPullParser [readPredictions]", e.getMessage());

		}
		return pred;
	}

	private Predictions.Direction readDirectionFromPrediction(XmlPullParser parser, Predictions p)
	{
		Predictions.Direction dir = p.new Direction();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "direction");

			// Process attrs
			dir.setDirectionTitle(parser.getAttributeValue(null, "title"));

			int eventType = parser.next();
			while (parser.getEventType() != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					if (name.equalsIgnoreCase("prediction"))
					{
						dir.addSinglePrediction(readSinglePrediction(parser, p));
					}
				}
				eventType = parser.next();
			}
		} catch (Exception e)
		{
			Log.e("Error with XmlPullParser [readDirectionFromPrediction]", e.getMessage());

		}
		return dir;
	}

	private Prediction readSinglePrediction(XmlPullParser parser, Predictions p)
	{
		Predictions.Prediction singlePred = p.new Prediction();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "prediction");

			// Process attrs
			singlePred.setEpochTime(Long.parseLong(parser.getAttributeValue(null, "epochTime")));
			singlePred.setSeconds(Integer.parseInt(parser.getAttributeValue(null, "seconds")));
			singlePred.setMinutes(Integer.parseInt(parser.getAttributeValue(null, "minutes")));
			singlePred.setIsDeparture(Boolean.parseBoolean(parser.getAttributeValue(null,
					"isDeparture")));
			singlePred.setDirTag(parser.getAttributeValue(null, "dirTag"));
			singlePred
					.setVehicleNumber(Integer.parseInt(parser.getAttributeValue(null, "vehicle")));
			singlePred.setBlock(Integer.parseInt(parser.getAttributeValue(null, "block")));
			singlePred.setTripTag(Integer.parseInt(parser.getAttributeValue(null, "tripTag")));
			String strAffectedByLayover = parser.getAttributeValue(null, "affectedByLayover");
			if (strAffectedByLayover != null)
				singlePred.setIsAffectedByLayover(Boolean.parseBoolean(strAffectedByLayover));
			// TODO add more optional attributes here

			parser.next();

		} catch (Exception e)
		{
			Log.e("Error with XmlPullParser [readSinglePrediction]", e.getMessage());

		}
		return singlePred;
	}

	private Route readRoute(XmlPullParser parser)
	{
		Route singleRoute = new Route();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "route");

			// Process Attributes
			singleRoute.setTag(parser.getAttributeValue(null, "tag"));
			singleRoute.setTitle(parser.getAttributeValue(null, "title"));
			final String colorString = parser.getAttributeValue(null, "color");
			String colorStringWithHash = "#" + colorString;
			singleRoute.setColor(Color.parseColor(colorStringWithHash));
			final String oppColorString = parser.getAttributeValue(null, "oppositeColor");
			String oppColorStringWithHash = "#" + oppColorString;
			singleRoute.setOppColor(Color.parseColor(oppColorStringWithHash));

			singleRoute.setMinLat(Double.parseDouble(parser.getAttributeValue(null, "latMin")));
			singleRoute.setMinLon(Double.parseDouble(parser.getAttributeValue(null, "lonMin")));
			singleRoute.setMaxLat(Double.parseDouble(parser.getAttributeValue(null, "latMax")));
			singleRoute.setMaxLon(Double.parseDouble(parser.getAttributeValue(null, "lonMax")));

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
						singleRoute.addDirection(readDirectionFromRoutes(parser, singleRoute));
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
			Log.e("Error in XML parser [readRoute]", e.getMessage());
		}
		return singleRoute;
	}

	private Direction readDirectionFromRoutes(XmlPullParser parser, Route r)
	{
		Route.Direction mydir = r.new Direction();
		try
		{
			parser.require(XmlPullParser.START_TAG, null, "direction");
			mydir.setTag(parser.getAttributeValue(null, "tag"));
			mydir.setTitle(parser.getAttributeValue(null, "title"));
			String directionName = parser.getAttributeValue(null, "name");
			if (directionName.equalsIgnoreCase(DIRECTION.Outbound.name()))
				mydir.setDirection(DIRECTION.Outbound);
			else
				mydir.setDirection(DIRECTION.Inbound);

			int eventType = parser.next();
			while (eventType != XmlPullParser.END_TAG)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					if (name.equalsIgnoreCase("stop"))
					{
						String strTag = parser.getAttributeValue(null, "tag");

						mydir.addStopTag(strTag);
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
			Log.e("Error in XML parser [readDirectionFromRoutes]", e.getMessage());
		}
		return mydir;
	}

	private Route.Stop readStop(XmlPullParser parser, Route r)
	{
		Route.Stop mystop = r.new Stop();

		try
		{
			parser.require(XmlPullParser.START_TAG, null, "stop");

			mystop.setTag(parser.getAttributeValue(null, "tag"));
			mystop.setTitle(parser.getAttributeValue(null, "title"));
			mystop.setStopID(parser.getAttributeValue(null, "stopId"));
			mystop.setLat(Double.parseDouble(parser.getAttributeValue(null, "lat")));
			mystop.setLon(Double.parseDouble(parser.getAttributeValue(null, "lon")));
			parser.next(); // to take care of the implied closing tag
		} catch (Exception e)
		{
			Log.e("Error in XML parser [readStop]", e.getMessage());
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
