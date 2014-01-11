package controllers;

import play.mvc.*;
import models.*;

public class MessageController extends Controller {
	
	public static Result receiveMsg(String from, String to, String msg) {		
		new User(from + "_" + "to" + "_" + to + "_" + msg + "@dupa.pl", "alias", "assword", "000000000", USER_PRIVILEGE.regular).save();
		return ok("<response><status>200</status></response>");
	}
}