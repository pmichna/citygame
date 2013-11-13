package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

@Entity
public class User extends Model {

    @Id
    public String email;
    public String alias;
    public String passwordHashed;
    
    public User(String email, String alias, String password) {
      this.email = email;
      this.alias = alias;
      this.passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static Finder<String,User> find = new Finder<String,User>(
        String.class, User.class
    );
    
    public static User authenticate(String email, String passwordNotHashed) {
    	User user = find.where().eq("email", email).findUnique();
    	if(user == null)
    	{
    		return null;
    	}
    	if(BCrypt.checkpw(passwordNotHashed, user.passwordHashed))
    	{
    		return user;
    	}
    	return null;
    }
}