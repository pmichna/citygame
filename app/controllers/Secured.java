package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
		ctx.flash().put("error", "Login to proceed");
        return redirect(routes.Application.loginGET());
    }
    
    public static boolean isMemberOf(Long scenarioId) {
        return Scenario.isMember(
            scenarioId,
            Context.current().request().username()
        );
    }

}