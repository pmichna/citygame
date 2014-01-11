package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;
import models.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDataController extends Controller {
	public static Result loadTestData() {
		for (int i = 10; i < 30; i++) {
			User tempUser = new User("testUser" + i + "@citygame.com",
					"testUser" + i, "test", "1111111" + i,
					USER_PRIVILEGE.regular);
			if (tempUser != null){
				if (User.find.where().eq("email", tempUser.email).findUnique() == null
						&& User.find.where().eq("alias", tempUser.alias)
								.findUnique() == null
						&& User.find.where()
								.eq("phoneNumber", tempUser.phoneNumber)
								.findUnique() == null) {
					tempUser.save();
				}
				String scenarioName="public scenario " + tempUser.alias;
				if(Scenario.find.where().eq("name",scenarioName).findList().size()<1){
					Scenario.create(scenarioName, true,
							null, tempUser.email,true);
				}
				
		}
		}
		return ok(index.render(null));
	}
}
