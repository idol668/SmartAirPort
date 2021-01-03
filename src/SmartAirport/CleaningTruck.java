package SmartAirport;


//****************************************************************************************
//***                Class that represents the cleaning team in our airport            ***
//****************************************************************************************

public class CleaningTruck {
	
	int x;
	int y;
	int degree;
	int x_oil;
	int y_oil;
	
	
	public CleaningTruck(int x, int y,int degree, int x_oil, int y_oil) {
		this.x = x; // the x and y values represents the location of the cleaning team in the airport 
		this.y = y;
		this.degree = degree; // which direction is the cleaning team moving - you can see the directions in the images itself 
		this.x_oil=x_oil; //the x and y values of the oil in the runway (the reason for the slippery runway)
		this.y_oil=y_oil;
	}

}
