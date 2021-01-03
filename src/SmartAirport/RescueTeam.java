package SmartAirport;

import java.awt.Image;

//****************************************************************************************
//***                Class that represents the rescue team in our airport            ***
//****************************************************************************************
public class RescueTeam {
	int x;
	int y;
	int line;
	boolean is_flashlight_on;
	Image rescueteamImage;
	Image flashlight_on;
	Image flashlight;
	
	
	public RescueTeam(int x, int y,	int line,Image rescueteamImage, Image flashlight) {
		this.x = x; // the x and y value represents the location of the truck in the airport
		this.y = y;
		this.line =line;
		this.is_flashlight_on = true; // when emergency landing performed the airport flashlights are showcased 
		this.rescueteamImage = rescueteamImage;
		this.flashlight_on = flashlight;
		this.flashlight = flashlight;
	}
	
	//This function turns the flashlights on and off in order to emphasize that this is an emergency landing
	public void TurnFlashLight() {
		if(is_flashlight_on == true) {
			this.flashlight = null;
			is_flashlight_on = false;
		} else {
			this.flashlight = this.flashlight_on;
			is_flashlight_on = true;
		}
	}
}
