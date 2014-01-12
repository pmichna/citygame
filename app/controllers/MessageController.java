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

	public static Result receiveMsg(String from, String to, String msg) {
		Logger.debug("Received message: " + msg);
		Logger.debug("from: " + from);
		Logger.debug("to: " + to);
		if (User.find.where().eq("phoneNumber", to).findList().size() < 1) {
			Logger.debug("Couldn't find phone number");
			return ok("<response><status>400</status></response>");
		}
		String delims = "[*]";
		String[] tokens = msg.split(delims);
		if (tokens.length < 3) {
			Logger.debug("Message does not have proper format");
			return ok("<response><status>400</status></response>");
		}
		Long checkpointId = Long.parseLong(tokens[1]);
		Long scenarioId = Long.parseLong(tokens[0]);
		if (Scenario.find.where().eq("id", scenarioId).findList().size() < 1) {
			Logger.debug("Scenario does not exist");
			return ok("<response><status>400</status></response>");
		}
		if (Checkpoint.find.where().eq("id", checkpointId).findList().size() < 1) {
			Logger.debug("Checkpoint does not exist");
			return ok("<response><status>400</status></response>");
		}
		GameEvent.createGameEvent(to, tokens[2], scenarioId, checkpointId);
		return ok("<response><status>200</status></response>");
	}

	public static Promise<Result> sendMsg(String to, String from, String msg) {
		String feedUrl = "https://api2.orange.pl/sendsms/";
		msg = msg.replace(' ', '+');

		final play.libs.F.Promise<Result> resultPromise = WS.url(feedUrl)
				.setQueryParameter("from", from).setQueryParameter("tp", to)
				.setQueryParameter("msg", msg)
				.setAuth("48509237274", "Y7A7HNM3EFF3LF").get()
				.map(new Function<WS.Response, Result>() {

					public Result apply(WS.Response response) {

						return ok("Message sent");

					}
				});
		return resultPromise;

	}
}