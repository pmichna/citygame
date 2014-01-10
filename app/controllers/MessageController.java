package controllers;

import play.mvc.*;
import models.*;

public class MessageController extends Controller {
	
	public static Result receiveMsg(String from, String to, String msg) {		
		new User("ppp@ppp.pl", from + " " + to + " " + msg, "assword", "000000000", USER_PRIVILEGE.regular).save();
		return ok("<response><status>200</status></response>");
	}
}