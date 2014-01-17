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
import models.*;

public class LocationController extends Controller {
	
	private static String orangeUser = play.Play.application().configuration()
			.getString("application.orangeUser");
	private static String orangePass = play.Play.application().configuration()
			.getString("application.orangePass");

	public static play.libs.F.Promise<Result> locationControllerGET(final String number) {

		String feedUrl = "https://api2.orange.pl/terminallocation/";
		
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setQueryParameter("msisdn",number)
				.setAuth(orangeUser, orangePass).get()
				.map(new Function<WS.Response, Result>() {

					NodeList longitude = null;
					NodeList latitude = null;

					public Result apply(WS.Response response) {
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
						if(latitude.getLength() == 0 || longitude.getLength() == 0) {
							Logger.error("Failed to get coordinates");
							if(response.getBody().indexOf("<description>msisdn not allowed</description>") != -1){
								Logger.info("Msisdn not allowed for:" + number);
								User.setUserLocation(number, false);
							}
							if(response.getBody().indexOf("<description>getLocation limit reached</description>") != -1){
								Logger.info("Msisdn limit reached for: " + number);
								
							}
							return ok("Failed to get coordinates");
						}
						double longitudeDouble = Double.parseDouble(longitude.item(0).getTextContent());
						double latitudeDouble = Double.parseDouble(latitude.item(0).getTextContent());
						Logger.info("Received location of number: " + number + " Longitude: " + longitudeDouble
						+" Latitude: "+ latitudeDouble);
						User.setUserPosition(number, longitudeDouble, latitudeDouble);
						User.setUserLocation(number, true);						
						return ok(latitude.item(0).getTextContent() + " " + longitude.item(0).getTextContent());
					}
				});
		return resultPromise;
	}

}