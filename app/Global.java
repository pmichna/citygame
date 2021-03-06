
import controllers.GameController;
import controllers.routes;
import models.GAME_STATUS;
import models.Game;
import models.User;
import play.*;
import play.libs.F.*;
import play.mvc.Http.*;
import play.mvc.*;
import play.mvc.Controller;
import static play.mvc.Results.*;

public class Global extends GlobalSettings {
	
	public static String orangeUser="48509237274";
	public static String orangePassword="Y7A7HNM3EFF3LF";
	 @Override
	  public void onStart(Application app) {
	    Logger.info("Application has started");
	    for(User u:User.find.all()){
	    	Logger.info("Found user: "+u.alias);
	    	for(Game g:u.games){
	    		Logger.info("Found game: "+g.scenario.name);
	    		if (g.status!=GAME_STATUS.stopped){
	    			GameController.startGame(g);
	    		}
	    	}
	    }
	 }  
	 
	
	 public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
	        return Promise.<SimpleResult>pure(notFound(
	            views.html.notFoundPage.render(request.uri())
	        ));
	    }
	 
	  
}