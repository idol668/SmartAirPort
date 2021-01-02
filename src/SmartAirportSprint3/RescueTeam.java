package SmartAirportSprint3;

import java.awt.Image;

public class RescueTeam {
	int x;
	int y;
	int line;
	boolean is_flashlight_on;
	Image rescueteamImage;
	Image flashlight_on;
	Image flashlight;
	
	
	public RescueTeam(int x, int y,	int line,Image rescueteamImage, Image flashlight) {
		this.x = x;
		this.y = y;
		this.line =line;
		this.is_flashlight_on = true;
		this.rescueteamImage = rescueteamImage;
		this.flashlight_on = flashlight;
		this.flashlight = flashlight;
	}
	
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
