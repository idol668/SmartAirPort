package SmartAirport;

import java.util.Random;
import java.util.concurrent.TimeUnit;

//*********************************************************************************************
//***              This class will contain some of our animation methods                    ***
//*** This methods are used for the features such as mechanical and slippery runway problem ***
//*** Note: They are secondary animation since the main functionality is of the             ***
//***  landing and takeoff that performed by the planes                                      *** 
//*********************************************************************************************

public class SecondaryAnimation {
	
	static boolean[] cleaningSensors;
	static CleaningTruck [] cleaningCars;
	static boolean[] stillCleaning;
	static boolean[] slipperyRunway;
	static RepairTruck[] repairTruck;
	static boolean[] mechanicalProblem;
	
	public SecondaryAnimation (boolean[] cleanSensors,CleaningTruck [] cleanCars,boolean[] stillCleaning,boolean[] slipperyRunway,RepairTruck[] repairTruck,boolean[] mechanicalProblem)
	{
		SecondaryAnimation.cleaningSensors=cleanSensors;
		SecondaryAnimation.cleaningCars=cleanCars;
		SecondaryAnimation.stillCleaning=stillCleaning;
		SecondaryAnimation.repairTruck=repairTruck;
		SecondaryAnimation.mechanicalProblem=mechanicalProblem;
		SecondaryAnimation.slipperyRunway=slipperyRunway;

	}
	
	// This function moves the plane from its waiting landing area to the runway it is going to use while performing landing/takeoff
	// Note: in case of two possible runways we randomly choose one
	public static void animateMovePlaneToRunway(int line, boolean islanding, boolean UsingFirstLineAllowed, boolean UsingSecondLineAllowed, Airplane Plane) {
		if((UsingFirstLineAllowed && !slipperyRunway[2 * line]) || (UsingSecondLineAllowed&& !slipperyRunway[2 * line + 1])) {
			Plane.EnterLandingOrTakeoof = true;
			if (UsingFirstLineAllowed && UsingSecondLineAllowed && !slipperyRunway[2 * line] && !slipperyRunway[2 * line + 1]) {
				Random rand = new Random();
				if (rand.nextBoolean() == true) {
					Plane.line = 2*line+1;
				} else {
					Plane.line = 2*line;
				}
			} else if (UsingFirstLineAllowed && !slipperyRunway[2 * line]) {
				Plane.line = 2*line;
			} else if (UsingSecondLineAllowed && !slipperyRunway[2 * line + 1]) {
				Plane.line = 2*line+1;
			}
			
			if(islanding==true) {
				SmartAirport.planesLanding[line] = Plane;
				SmartAirport.runwaysLanding[line]= Plane.line;
			}
			else {
				SmartAirport.planesTakeoff[line] = Plane;
				SmartAirport.runwaysTakeOff[line] = Plane.line;
			}
			
		} else {
			Plane.EnterLandingOrTakeoof = false;
		}
	}
	
	// This function moves the rescue team to the correct spot
	// The correct spot is determined by the runway that is used by the plane while performing the emergency landing
	public static void movingRescueTeamToLandingLine(int landingLine, int loopStep, RescueTeam rescueTeam) {
		switch (landingLine) {
			case 0:
				if (loopStep < 42) {
					rescueTeam.y -= 11;
				} else {
					rescueTeam.rescueteamImage = AirportImages.rescueteam_r;
					try {
						TimeUnit.MICROSECONDS.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				if (loopStep < 35) {
					rescueTeam.y -= 11;
				} else {
					rescueTeam.rescueteamImage = AirportImages.rescueteam_r;
					try {
						TimeUnit.MICROSECONDS.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				if (loopStep < 12) {
					rescueTeam.y -= 12;
				}
				if (loopStep < 13) {
					rescueTeam.y -= 5;
				} else {
					rescueTeam.rescueteamImage = AirportImages.rescueteam_r;
					try {
						TimeUnit.MICROSECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				if (loopStep < 6) {
					rescueTeam.y -= 12;
				} else {
					rescueTeam.rescueteamImage =AirportImages.rescueteam_r;
					try {
						TimeUnit.MICROSECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
		}
	}

	/*This Function will simulate the dirty runway functionality that was described in the specification */
	public void animatedCleanTruck(int timeToRemoveDirtOrTruck) {
		int cleaningCarSpeedX=17;
		int cleaningCarSpeedY=2;
		for (int i = 0; i < 4; i++) {
			if (slipperyRunway[i]) {
				if (cleaningSensors[i]) {
					if (timeToRemoveDirtOrTruck < 23) {						
						cleaningCars[i].x = cleaningCars[i].x + cleaningCarSpeedX;
					}
					if (timeToRemoveDirtOrTruck > 23 & timeToRemoveDirtOrTruck < 30) {
						stillCleaning[i] = false;
						cleaningCars[i].degree = 90;
						cleaningCars[i].y = cleaningCars[i].y + cleaningCarSpeedY;
					}

					if (timeToRemoveDirtOrTruck > 30) {
						cleaningCars[i].degree = 0;
						cleaningCars[i].x = cleaningCars[i].x - cleaningCarSpeedX;
						if (timeToRemoveDirtOrTruck == 54) {
							cleaningCars[i] = null;
						}
					}
				}
			}
		}
	}
	
	
	
	/*The Function will make the planes that are waiting to land to go in circles until they'll get the chance to land */
	public static void animatedWaitingForLanding(int iteration, Airplane aircraft)
	{
		int planeSpeedX=4;
		int planeSpeedY=3;
		if (iteration <10)
		{
			aircraft.x_shadow +=planeSpeedX;
			aircraft.x += planeSpeedX;
		}
		
		if (iteration>9 & iteration <20)
		{
			aircraft.degree=90;
			aircraft.y_shadow += planeSpeedY;
			aircraft.y +=planeSpeedY;

		}
		
		if (iteration>19 & iteration <30)
		{
			aircraft.degree=0;
			aircraft.x_shadow -= planeSpeedX;
			aircraft.x -= planeSpeedX;

		}
		
		if (iteration>29 & iteration <38)
		{
			aircraft.degree=270;
			aircraft.y_shadow -= planeSpeedY;
			aircraft.y -= planeSpeedY;

		}
		
		if (iteration==39)
		{
			aircraft.degree=180;
		}
	}
	
	//This function will simulate the mechanical problem feature that was described in the specification
	//It will move the repair truck to the correct spot
	//The correct spot is determined by the plane's takeoff waiting area
	public void animateRepairTruck(int loopStep) {
		int repairTruckSpeedX = 10;
		int repairTruckSpeedY = 20;
		int repairManSpeedY   = 20;
		int repairManSpeedX   = 7;
		int plane_waiting_platform_1_y_pos = 290;
		int plane_waiting_platform_2_y_pos = 400;
		int plane_waiting_x_pos = 670;
		int truck_step_to_leave = 2;
		int[] planeWaitingPosition = {plane_waiting_platform_1_y_pos,plane_waiting_platform_2_y_pos};
		if (loopStep>26){
			for(int i=0; i<2;i++) {
				if (repairTruck[i] != null) {
					if(repairTruck[i].remove_truck_step > truck_step_to_leave) {
						repairTruck[i].truckImage = AirportImages.repairtruck_r;
						repairTruck[i].x+= repairTruckSpeedX;				
					}
					if (repairTruck[i].y > planeWaitingPosition[i]) {
						repairTruck[i].y -= repairTruckSpeedY;
						repairTruck[i].man_y -= repairManSpeedY;
					}
					else if(repairTruck[i].man_x > plane_waiting_x_pos) {
						repairTruck[i].man_x -= repairManSpeedX;
						repairTruck[i].remove_truck_step += 1;
					}
				}
			}		
		}
	}
	
	// This function will simulate the landing of an airplane.
	// The airplane moves on the runway and changes its speed according to the position on the runway.
	public static void animatePlaneLanding(int loopStep,Airplane plane) {
		int landinPlaneSpeed = 20;
		int landinShadowgPlaneSpeed = 18;
		int planeSpeedY = 1;
		int min_upper_runway_y = 165;
		int max_upper_runway_y = 400;
		int max_lower_runway_y = 500;
		
		if (plane != null && plane.EnterLandingOrTakeoof && loopStep >= 9) {
			if (loopStep < 21) {
				plane.x_shadow += landinShadowgPlaneSpeed;
				plane.x += landinPlaneSpeed;
				if (loopStep>15 && loopStep<21){
					plane.y -=planeSpeedY; 
				}	
			}
			else if (loopStep == 21) {
				plane.ground = true;
			} else {
				if (loopStep < 34) {
					landinShadowgPlaneSpeed=22;
					plane.x += landinPlaneSpeed;
					plane.x_shadow += landinShadowgPlaneSpeed;
				} else {

					if (loopStep > 33 && loopStep < 40) {
						if (plane.y > min_upper_runway_y && plane.y < max_upper_runway_y) {
							plane.y -= planeSpeedY;
						}
						if (plane.y > max_upper_runway_y && plane.y < max_lower_runway_y) {
							planeSpeedY=2;
							plane.y += planeSpeedY;
						}
						landinPlaneSpeed=15;
						plane.x += landinPlaneSpeed;
					} else {
						if (plane.y > min_upper_runway_y && plane.y < max_upper_runway_y) {
							planeSpeedY=1;
							plane.y -= planeSpeedY;
						}
						if (plane.y > max_upper_runway_y && plane.y < max_lower_runway_y) {
							planeSpeedY=2;
							plane.y += planeSpeedY;
						}
						landinPlaneSpeed=10;
						plane.x += landinPlaneSpeed;
					}
					plane.setShadowUnderPlane();
				}
			}
		}
		if (plane != null && !plane.EnterLandingOrTakeoof) {
			animatedWaitingForLanding(loopStep, plane);
		}
	}
	
	// This function will simulate the take-off of an airplane.
	// The airplane moves on the runway and changes its speed according to the position on the runway.
	public static void animatePlaneTakingOff(int loopStep,Airplane plane) {
		int takeOffPlaneSpeed = 20;
		int takeOffPlaneSpeedY = 1;
		
		if (plane != null && plane.EnterLandingOrTakeoof & loopStep >= 20) {
			if (loopStep<29){
				plane.x -= takeOffPlaneSpeed;
				plane.x_shadow = (int) Math.round(plane.x_shadow - takeOffPlaneSpeed);
				plane.y_shadow = (int) Math.round(plane.y_shadow - takeOffPlaneSpeedY);
			} else if (loopStep == 29) {
				plane.ground = false;
			} else {
				if (plane.y > 500 && plane.y < 650) {
					takeOffPlaneSpeedY=3;
					plane.y_shadow += takeOffPlaneSpeedY;
					plane.y+=takeOffPlaneSpeedY; 
	
				}
				if (plane.y > 230 && plane.y < 330) {
					takeOffPlaneSpeedY=1;
					plane.y_shadow += takeOffPlaneSpeedY;
					plane.y+=takeOffPlaneSpeedY; 
					
				}
				if (plane.y > 400 && plane.y < 500) {
					takeOffPlaneSpeedY=2;
					plane.y_shadow = (int) Math.round(plane.y_shadow -takeOffPlaneSpeedY);
					plane.y= (int) Math.round(plane.y -takeOffPlaneSpeedY);
					
				} else {
					takeOffPlaneSpeedY=1;
					plane.y_shadow = (int) Math.round(plane.y_shadow -takeOffPlaneSpeedY);
					plane.y= (int) Math.round(plane.y -takeOffPlaneSpeedY);
	
				}
				takeOffPlaneSpeed=23;
				plane.x -=takeOffPlaneSpeed;
				plane.x_shadow = (int) Math.round(plane.x_shadow - takeOffPlaneSpeed);
			}
		}
	}
	
}
