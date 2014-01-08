package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import play.data.validation.Constraints.*;

@Entity
public class User extends Model {

    @Id
    public Long id;
    
    @Column(length=254, unique=true, nullable=false)
    @Required
    public String email;

    @Column(length=20,nullable=false,unique=true)
    @Required
    public String alias;

    @Column(length=9,nullable=false,unique=true)
    @Required
    public String phoneNumber;

    @Column(length=60,nullable=false)
    public String passwordHash;

    @ManyToMany(mappedBy = "members")
    public List<Scenario> scenarios = new ArrayList<Scenario>();

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    public USER_PRIVILEGE privilege;

    @OneToMany
    public List<Game> games = new ArrayList<Game>();
    
    
    public User(String email, String alias, String password, String phoneNumber, USER_PRIVILEGE privilege) {
      this.email = email;
      this.alias = alias;
      this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
      this.phoneNumber = phoneNumber;
      this.privilege = privilege;
    }

    public static Finder<Long, User> find = new Finder<Long,User>(
        Long.class, User.class
    );
    
    
    public static User authenticate(String email, String passwordNotHash) {
    	User user = find.where().eq("email", email).findUnique();
    	if(user == null)
    	{
    		return null;
    	}
    	if(BCrypt.checkpw(passwordNotHash, user.passwordHash))
    	{
    		return user;
    	}
    	return null;
    }
    
    public static User editUser(String oldEmail, String newEmail, String newPasswordNotHash, String newAlias, String newPhoneNumber) {
    	User user = find.where().eq("email", oldEmail).findUnique();
    	if(user == null) {
    		return null;
    	}
    	if(newAlias != null && !newAlias.equals(user.alias)) {
    		user.alias = newAlias;
    	}
    	if(newPasswordNotHash != null && !newPasswordNotHash.equals("")) {
    		user.passwordHash = BCrypt.hashpw(newPasswordNotHash, BCrypt.gensalt());
    	}
    	if(newPhoneNumber != null && !newPhoneNumber.equals(user.phoneNumber)) {
    		user.phoneNumber = newPhoneNumber;
    	}
    	if(newEmail != null && !newEmail.equals(user.email)) {
    		user.email = newEmail;
    	}
    	user.save();
    	
    	return user;
    }
    
    
}
