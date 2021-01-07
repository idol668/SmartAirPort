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
	
	// This function moves the plane from its waiting landing area to the runway it is going to use while performing landing
	public static void animateGetPlaneToLandingSpot(int line,boolean landingAllowedFirstLine, boolean landingAllowedSecondLine, Airplane landingPlane) {
		if (landingAllowedFirstLine && landingAllowedSecondLine) {
			Random rand = new Random();
			if (rand.nextBoolean() == true) {
				landingPlane.setPlaneToSecondRunway();
				landingPlane.line = 2*line +1;
				SmartAirport.planesLanding[line]=landingPlane;
				SmartAirport.runwaysLanding[line]=2*line +1;
			} else {
				landingPlane.setPlaneToFirstRunway();
				SmartAirport.planesLanding[line]=landingPlane;
				SmartAirport.runwaysLanding[line]=2*line;
				landingPlane.line = 2*line;
			}
		} else if (landingAllowedFirstLine && !slipperyRunway[2 * line]) {
			landingPlane.setPlaneToFirstRunway();
			SmartAirport.planesLanding[line]=landingPlane;
			SmartAirport.runwaysLanding[line]=2*line;
			landingPlane.line = 2*line;
		} else if (landingAllowedSecondLine && !slipperyRunway[2 * line + 1]) {
			landingPlane.setPlaneToSecondRunway();
			SmartAirport.planesLanding[line]=landingPlane;
			SmartAirport.runwaysLanding[line]=2*line+1;
			landingPlane.line =  2*line +1;
		}
		landingPlane.setShadowUnderPlane();
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
		int cleaningCarSpeedX=15;
		int cleaningCarSpeedY=2;
		for (int i = 0; i < 4; i++) {
			if (slipperyRunway[i]) {
				if (cleaningSensors[i]) {
					if (timeToRemoveDirtOrTruck < 25) {						
						cleaningCars[i].x = cleaningCars[i].x + cleaningCarSpeedX;
					}
					if (timeToRemoveDirtOrTruck > 25 & timeToRemoveDirtOrTruck < 32) {
						stillCleaning[i] = false;
						cleaningCars[i].degree = 90;
						cleaningCars[i].y = cleaningCars[i].y + cleaningCarSpeedY;
					}

					if (timeToRemoveDirtOrTruck > 32) {
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
	public void animatedWaitingForLanding(int iteration, Airplane aircraft)
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
		int repairTruckSpeedX=10;
		int repairTruckSpeedY=20;
		int repairManSpeedY=19;
		int repairManSpeedX =7;
		
		if (loopStep>26)
		{
			if (repairTruck[0] != null) {
				if (repairTruck[0].remove_truck > 2) {
					repairTruck[0].truckImage = AirportImages.repairtruck_r;
					repairTruck[0].x = repairTruck[0].x + repairTruckSpeedX;
				}
				if (repairTruck[0].y > 290) {
					repairTruck[0].y = repairTruck[0].y - repairTruckSpeedY;
					repairTruck[0].man_y = repairTruck[0].man_y - repairManSpeedY;
				} else {
					repairTruck[0].man_x = repairTruck[0].man_x - repairManSpeedX;
					repairTruck[0].remove_truck += 1;
				}
			}
			if (repairTruck[1] != null) {
				if (repairTruck[1].remove_truck > 2) {
					repairTruck[1].truckImage = AirportImages.repairtruck_r;
					repairTruck[1].x = repairTruck[1].x + repairTruckSpeedX;
				}
				if (repairTruck[1].y > 400) {
					repairTruckSpeedY=10;
					repairManSpeedY=10;
					repairManSpeedX=10;
					repairTruck[1].y = repairTruck[1].y - repairTruckSpeedY;
					repairTruck[1].man_y = repairTruck[1].man_y - repairManSpeedY;
				} else {
					repairTruck[1].man_x = repairTruck[1].man_x -repairManSpeedX;
					repairTruck[1].remove_truck += 1;
				}
			}
		}
	}
		
	
	
}
