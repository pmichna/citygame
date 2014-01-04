package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;

import com.avaje.ebean.*;

import play.data.validation.Constraints.*;

@Entity
public class User extends Model {

    @Id
    public Long Id;
    
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

    public static Finder<String,User> find = new Finder<String,User>(
        String.class, User.class
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
    
    public static User editUserData(String email, String newPasswordNotHash, String newAlias, String newPhoneNumber){
    	User user = find.where().eq("email", email).findUnique();
    	if(user == null){
    		return null;
    	}
    	
    	user.alias = newAlias;
    	user.passwordHash = BCrypt.hashpw(newPasswordNotHash, BCrypt.gensalt());
    	user.phoneNumber = newPhoneNumber;
    	user.save();
    	
    	return user;
    }
    
    public static User editUserEmail(String oldEmail, String newEmail){
    	User user = find.where().eq("email", oldEmail).findUnique();
    	if(user == null){
    		return null;
    	}
    	user.email = newEmail;
    	user.update();
    	
    	return user;
    }
}
