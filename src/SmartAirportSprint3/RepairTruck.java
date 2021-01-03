package SmartAirportSprint3;

import java.awt.Image;

//****************************************************************************************
//***                Class that represents the repair team in our airport            ***
//****************************************************************************************
public class RepairTruck {
	int x;
	int y;
	int man_x;
	int man_y;
	int line;
	int remove_truck = 0;
	Image truckImage;
	
	
	public RepairTruck(int x, int y,int line, Image truckImage) {
		this.x = x; // the x and y value represents the location of the truck in the airport
		this.y = y;
		this.line = line;
		this.man_x = x; // the x and y value represents the repair man location
		this.man_y = y;
		this.truckImage = truckImage;
	}
	
	
	public void setImage(Image newtruckImage) {
		this.truckImage = newtruckImage;
	}
}
