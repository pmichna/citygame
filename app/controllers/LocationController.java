package controllers;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import play.Logger;
import play.libs.F.Function;
import play.libs.WS;
import play.mvc.*;
import sun.net.www.protocol.http.AuthScheme;
import models.*;

public class LocationController extends Controller {

	public static play.libs.F.Promise<Result> locationControllerGET(
			final String number) {

		String feedUrl = "https://api2.orange.pl/terminallocation/";
		
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setQueryParameter("msisdn",number)
				.setAuth("48509237274", "Y7A7HNM3EFF3LF").get()
				.map(new Function<WS.Response, Result>() {

					NodeList longitude = null;
					NodeList latitude = null;

					public Result apply(WS.Response response) {
						//response.body.asXml();
						DocumentBuilderFactory dbf = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder db = null;
						try {
							db = dbf.newDocumentBuilder();
							InputSource is = new InputSource();
							is.setCharacterStream(new StringReader(response
									.getBody().toString()));

							Document doc = db.parse(is);
							longitude = doc.getElementsByTagName("longitude");
							latitude = doc.getElementsByTagName("latitude");

						} catch (Exception e) {
							Logger.error("Failed to properly parse location file");
						}
						if(latitude.getLength()==0 || longitude.getLength()==0){
							Logger.error("Failed to get coordinates");
							if(response.getBody().indexOf("<description>msisdn not allowed</description>")!=-1){
								Logger.error("Msisdn not allowed for:"+number);
								GameEvent.createGameEvent(number, "msisdn not allowed", GAME_EVENT_TYPE.msisdnError);
							}
							if(response.getBody().indexOf("<description>getLocation limit reached</description>")!=-1){
								Logger.error("Msisdn limit reached for: "+number);
								GameEvent.createGameEvent(number, "msisdn limit reached", GAME_EVENT_TYPE.msisdnError);
							}
							return ok("Failed to get coordinates");
						}
						
						//Logger.debug("Latitude:"+latitude.item(0).getTextContent());
						double longitudeDouble=Double.parseDouble(longitude.item(0).getTextContent());
						double latitudeDouble=Double.parseDouble(latitude.item(0).getTextContent());
						Logger.info("Received location of number: " +number
						+" Longitude: "+ longitudeDouble
						+" Latitude: "+ latitudeDouble);
						User.setUserPosition(number,longitudeDouble,latitudeDouble);
						GameEvent.createGameEvent(longitudeDouble,latitudeDouble,number);
						
						return ok(latitude.item(0).getTextContent()+" "+longitude.item(0).getTextContent());
					}
				});
		return resultPromise;
	}

}