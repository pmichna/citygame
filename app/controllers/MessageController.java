package controllers;

import java.io.StringReader;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import play.Logger;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.F.Function;
import play.mvc.*;
import models.*;

public class MessageController extends Controller {

	private static String orangeUser = play.Play.application().configuration()
			.getString("orange.user");
	private static String orangePass = play.Play.application().configuration()
			.getString("orange.pass");

	public static Result receiveMsg(String from, String to, String msg) {
		Logger.debug("Received message: " + msg);
		Logger.debug("from: " + from);
		Logger.debug("to: " + to);
		if (User.find.where().eq("phoneNumber", from).findList().size() < 1) {
			Logger.error("Couldn't find phone number");
			return ok("<response><status>400</status></response>");
		}
		String delims = "[*]";
		String[] tokens = msg.split(delims);
		if (tokens.length < 3) {
			Logger.error("Message does not have proper format");
			return ok("<response><status>400</status></response>");
		}
		Long checkpointId = Long.parseLong(tokens[1]);
		Long scenarioId = Long.parseLong(tokens[0]);
		if (Scenario.find.where().eq("id", scenarioId).findList().size() < 1) {
			Logger.error("Scenario does not exist");
			return ok("<response><status>400</status></response>");
		}
		if (Checkpoint.find.where().eq("id", checkpointId).findList().size() < 1) {
			Logger.error("Checkpoint does not exist");
			return ok("<response><status>400</status></response>");
		}
		GameEvent.createGameEvent(from, tokens[2], scenarioId, checkpointId);
		Logger.info("Message received");
		return ok("<response><status>200</status></response>");
	}

	public static Promise<Result> sendMsg(String to, String msg) {
		String feedUrl = "https://api2.orange.pl/sendsms/";
		//msg = msg.replace(' ', '+');
		Logger.info("Sending SMS to: "+to+" with msg: "+msg);
		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setQueryParameter("to", to)
				.setQueryParameter("msg", msg)
				.setAuth("orangeUser", "orangePass").get()
				.map(new Function<WS.Response, Result>() {

					public Result apply(WS.Response response) {
						if(response.getBody().toString().indexOf("<deliveryStatus>MessageWaiting</deliveryStatus>")!=-1){
							Logger.info("Message sent");
							return ok("Message sent");
						}else{
							Logger.error("Failed to send message");
							return redirect("Program encountered some problem sending message");
						}

					}
				});
		return resultPromise;

	}
}