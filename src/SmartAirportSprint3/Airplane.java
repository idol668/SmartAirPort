package SmartAirportSprint3;

public class Airplane {
	int x;
	int y;
	int x_shadow;
	int y_shadow;
	int degree;
	String type;
	boolean ground;
	int line;
	boolean EnterLandingOrTakeoof;
	
	
	public Airplane(int x, int y,int degree, String type, int line, boolean ground) {
		this.x = x;
		this.y = y;
		this.degree = degree;
		this.type = type;
		this.line = line;
		this.ground = ground;
		this.x_shadow=x;
		this.y_shadow=y;
		this.EnterLandingOrTakeoof =false;
	}
	
}
