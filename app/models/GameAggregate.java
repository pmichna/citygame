package models;

public class GameAggregate {
	
	public int points;
	
	public String alias;
	
	public GameAggregate(String alias, int points) {
		this.alias = alias;
		this.points = points;
	}

}
