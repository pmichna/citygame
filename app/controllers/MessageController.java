package controllers;

import java.util.Arrays;

import play.Logger;
import play.mvc.*;
import models.*;

public class MessageController extends Controller {
	
	public static Result receiveMsg(String from,String to, String msg) {
		Logger.debug("Received message: "+msg);
		Logger.debug("from: "+from);
		Logger.debug("to: "+to);
		if(User.find.where().eq("phoneNumber",to).findList().size()<1){
			Logger.debug("Couldn't find phone number");
			return ok("<response><status>400</status></response>");
		}
		String delims = "[*]";
		String[] tokens = msg.split(delims);
		if(tokens.length<3){
			Logger.debug("Message does not have proper format");
			return ok("<response><status>400</status></response>");
		}
		Long checkpointId=Long.parseLong(tokens[1]);
		Long scenarioId=Long.parseLong(tokens[0]);
		if(Scenario.find.where().eq("id",scenarioId).findList().size()<1){
			Logger.debug("Scenario does not exist");
			return ok("<response><status>400</status></response>");
		}
		if(Checkpoint.find.where().eq("id",checkpointId).findList().size()<1){
			Logger.debug("Checkpoint does not exist");
			return ok("<response><status>400</status></response>");
		}
		GameEvent.createGameEvent(to, tokens[2], scenarioId, checkpointId);
		return ok("<response><status>200</status></response>");
	}
	
	public static Result sendMsg(String to, String msg){
		return ok("<response><status>200</status></response>");
		
	}
}