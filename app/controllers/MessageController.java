package controllers;

import play.mvc.*;

public class MessageController extends Controller {
	
	public static Result receiveMsg(String from, String to, String msg){		
		return ok("<response><status>200</status></response>");
	}
}