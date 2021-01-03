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
	
	public void setPlaneToFirstRunway() {
		this.EnterLandingOrTakeoof = true;
		this.y -= 40;
	}
	
	public void setPlaneToSecondRunway() {
		this.EnterLandingOrTakeoof = true;
		this.y += 62;
	}
	
	public void movingPlaneAndShadow(int num) {
		this.x += num;
		this.x_shadow += num;
	}
	public void setShadowUnderPlane() {
		this.x_shadow = this.x;
		this.y_shadow = this.y;
	}

	
}
