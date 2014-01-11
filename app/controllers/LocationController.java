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
			String number) {

		String feedUrl = "https://api2.orange.pl/terminallocation/?msisdn="
				+ number;
		
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setAuth("48509237274", "Y7A7HNM3EFF3LF", com.ning.http.client.Realm.AuthScheme.BASIC).get()
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
							Logger.debug("Failed to properly pase location file");
						}
						if(latitude.getLength()==0 || longitude.getLength()==0)
							return ok("");
						return ok(latitude.item(0).getTextContent()+" "+longitude.item(0).getTextContent());
					}
				});
		return resultPromise;
	}

}