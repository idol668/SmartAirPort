package SmartAirportSprint3;


//****************************************************************************************
//***                Class that represents the planes in our airport                   ***
//****************************************************************************************

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
		this.x = x; //the x and y value represent the location of the airplane in the airport
		this.y = y;
		this.degree = degree; // the position of the plane in the airport usually 0- take off and 180- for landing  
		this.type = type;
		this.line = line;
		this.ground = ground;
		this.x_shadow=x; // the x and y value of the plane shadow
		this.y_shadow=y;
		this.EnterLandingOrTakeoof =false; //when a plane is about to perform take off or landing the value will be true
	}
	
	public void setPlaneToFirstRunway() {
		this.EnterLandingOrTakeoof = true;
		this.y -= 40;
	}
	
	public void setPlaneToSecondRunway() {
		this.EnterLandingOrTakeoof = true;
		this.y += 62;
	}
	
	// moves the x value of the plane and its shadow while doing some kind of movement 
	public void movingPlaneAndShadow(int num) {
		this.x += num;
		this.x_shadow += num;
	}
	//Sets the plane shadow x and y values to be the same as the plane itself
	public void setShadowUnderPlane() {
		this.x_shadow = this.x;
		this.y_shadow = this.y;
	}

	
}
