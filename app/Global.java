import controllers.GameController;
import controllers.UserAccountController;
import models.GAME_STATUS;
import models.Game;
import models.User;
import play.*;

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
	    	UserAccountController.startUserThread(u.email);
	    }
	 }  
}