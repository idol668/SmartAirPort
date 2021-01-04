package SmartAirport;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

//****************************************************************************************
//***              This class will contain all the airport images                      ***
//***              All the images are used during our simulation                       ***
//****************************************************************************************

public class AirportImages {
	
	static Image airport;
	static Image shadow_private_0;
	static Image shadow_private_180;
	static Image shadow_commerical_0;
	static Image shadow_commerical_180;
	static Image commercialplane_0;
	static Image commercialplane_180;
	static Image privateplane_0;
	static Image privateplane_180;
	static Image cargoplane_0;
	static Image cargoplane_180;
	static Image shadowcargo_0;
	static Image shadowcargo_180;
	static Image repairman;
	static Image repairtruck_up;
	static Image repairtruck_r;
	static Image rescueteam_up;
	static Image rescueteam_r;
	static Image rescueteam_down;
	static Image ambuImage_up;
	static Image ambuImage_r;
	static Image cleaningCar_0;
	static Image cleaningCar_90;
	static Image cleaningCar_180;
	static Image cleaningCar_270;
	static Image oilAndDirt;
	static Image shadow_private_90;
	static Image shadow_private_270;
	static Image shadowcargo_90;
	static Image shadowcargo_270;
	static Image shadow_commerical_90;
	static Image shadow_commerical_270;
	
	static Image privateplane_90;
	static Image privateplane_270;
	static Image commercialplane_90;
	static Image commercialplane_270;
	static Image cargoplane_90;
	static Image cargoplane_270;
	static Image flashlight;

	
	// This function sets all the images to their respective values
	public static void initialFields() throws IOException {
		//airport = ImageIO.read(new File("img/airport.png"));
		airport = ImageIO.read(new File("img/airport_v4.png"));
		commercialplane_0 = ImageIO.read(new File("img/commercialplane_0.png"));
		privateplane_0 = ImageIO.read(new File("img/privateplane_0.png"));
		cargoplane_0 = ImageIO.read(new File("img/cargoplane_0.png"));
		commercialplane_180 = ImageIO.read(new File("img/commercialplane_180.png"));
		privateplane_180 = ImageIO.read(new File("img/privateplane_180.png"));
		cargoplane_180 = ImageIO.read(new File("img/cargoplane_180.png"));
		shadowcargo_0 = ImageIO.read(new File("img/shadowcargo_0_t.png"));
		shadow_private_0 = ImageIO.read(new File("img/shadow_private_0.png"));
		shadow_commerical_0 = ImageIO.read(new File("img/shadow_commerical_0_t.png"));
		shadow_private_180 = ImageIO.read(new File("img/shadow_private_180.png"));
		shadow_commerical_180 = ImageIO.read(new File("img/shadow_commerical_180_t.png"));
		shadowcargo_180 = ImageIO.read(new File("img/shadowcargo_180_t.png"));
		repairman = ImageIO.read(new File("img/man1.png"));
		repairtruck_up = ImageIO.read(new File("img/repairtruck_up.png"));
		repairtruck_r = ImageIO.read(new File("img/repairtruck_right.png"));
		flashlight =ImageIO.read(new File("img/flashlight.png"));

		// Images for waiting to land

		shadowcargo_90 = ImageIO.read(new File("img/shadowcargo_90_t.png"));
		shadowcargo_270 = ImageIO.read(new File("img/shadowcargo_270_t.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90_t.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270_t.png"));
		shadow_private_90 = ImageIO.read(new File("img/shadow_private_90.png"));
		shadow_private_270 = ImageIO.read(new File("img/shadow_private_270.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90_t.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270_t.png"));

		// Additional Images for takeoff
		cargoplane_90 = ImageIO.read(new File("img/cargoplane_90.png"));
		cargoplane_270 = ImageIO.read(new File("img/cargoplane_270.png"));
		privateplane_90 = ImageIO.read(new File("img/privateplane_90.png"));
		privateplane_270 = ImageIO.read(new File("img/privateplane_270.png"));
		commercialplane_90 = ImageIO.read(new File("img/commercialplane_90.png"));
		commercialplane_270 = ImageIO.read(new File("img/commercialplane_270.png"));

		// images for slippery runway
		cleaningCar_0 = ImageIO.read(new File("img/cleaningCar_0.png"));
		cleaningCar_180 = ImageIO.read(new File("img/cleaningCar_180.png"));
		cleaningCar_90 = ImageIO.read(new File("img/cleaningCar_90.png"));
		cleaningCar_270 = ImageIO.read(new File("img/cleaningCar_270.png"));
		oilAndDirt = ImageIO.read(new File("img/oil.png"));

		// images for Emergency landing
		ambuImage_up = ImageIO.read(new File("img/ambulance_up.png"));
		rescueteam_up = ImageIO.read(new File("img/rescueTeam_up.png"));
		ambuImage_r = ImageIO.read(new File("img/ambulance_to_right.png"));
		rescueteam_r = ImageIO.read(new File("img/rescueTeam_to_right.png"));
		rescueteam_down = ImageIO.read(new File("img/rescueTeam_down.png"));

	}
	

}
