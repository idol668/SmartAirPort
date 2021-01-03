package SmartAirportSprint3;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SecondaryAnimation {
	
	/*--------------- This class will deal with the animation and drawing functions of our features such as : Slippery runway and Mechanical Problem--------------------*/
	
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
	
	public static void animateGetPlaneToLandingSpot(int line,boolean landingAllowedFirstLine, boolean landingAllowedSecondLine, Airplane landingPlane) {
		if (landingAllowedFirstLine && landingAllowedSecondLine) {
			Random rand = new Random();
			if (rand.nextBoolean() == true) {
				landingPlane.setPlaneToSecondRunway();
				landingPlane.line = 2*line +1;
			} else {
				landingPlane.setPlaneToFirstRunway();
				landingPlane.line = 2*line;
			}
		} else if (landingAllowedFirstLine && !slipperyRunway[2 * line]) {
			landingPlane.setPlaneToFirstRunway();
			landingPlane.line = 2*line;
		} else if (landingAllowedSecondLine && !slipperyRunway[2 * line + 1]) {
			landingPlane.setPlaneToSecondRunway();
			landingPlane.line =  2*line +1;
		}
		landingPlane.setShadowUnderPlane();
	}
	
	public static void movingRescueTeamToLandingLine(int landingLine, int loopStep, RescueTeam rescueTeam) {
		switch (landingLine) {
			case 0:
				if (loopStep < 42) {
					rescueTeam.y -= 11;
				} else {
					rescueTeam.rescueteamImage = SmartAirport.rescueteam_r;
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
					rescueTeam.rescueteamImage = SmartAirport.rescueteam_r;
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
					rescueTeam.rescueteamImage = SmartAirport.rescueteam_r;
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
					rescueTeam.rescueteamImage = SmartAirport.rescueteam_r;
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

		for (int i = 0; i < 4; i++) {
			if (slipperyRunway[i]) {
				if (cleaningSensors[i]) {
					if (timeToRemoveDirtOrTruck < 20) {
						
						cleaningCars[i].x = cleaningCars[i].x + 15;

					}
					if (timeToRemoveDirtOrTruck > 20 & timeToRemoveDirtOrTruck < 27) {
						stillCleaning[i] = false;
						cleaningCars[i].degree = 90;
						cleaningCars[i].y = cleaningCars[i].y + 2;
					}

					if (timeToRemoveDirtOrTruck > 27) {
						cleaningCars[i].degree = 0;
						cleaningCars[i].x = cleaningCars[i].x - 15;
						if (timeToRemoveDirtOrTruck == 49) {
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
		
		if (iteration <10)
		{
			aircraft.x_shadow += 4;
			aircraft.x += 4;
		}
		
		if (iteration>9 & iteration <20)
		{
			aircraft.degree=90;
			aircraft.y_shadow += 3;
			aircraft.y += 3;

		}
		
		if (iteration>19 & iteration <30)
		{
			aircraft.degree=0;
			aircraft.x_shadow -= 4;
			aircraft.x -= 4;

		}
		
		if (iteration>29 & iteration <38)
		{
			aircraft.degree=270;
			aircraft.y_shadow -= 3;
			aircraft.y -= 3;

		}
		
		if (iteration==39)
		{
			aircraft.degree=180;
		}
	}
	
	/*This function will simulate the mechanical problem feature that was described in the specification*/
	public void animateRepairTruck(int i) {
		if (i>22)
		{
			if (repairTruck[0] != null) {
				if (repairTruck[0].remove_truck > 2) {
					repairTruck[0].truckImage = SmartAirport.repairtruck_r;
					repairTruck[0].x = repairTruck[0].x + 10;
				}
				if (repairTruck[0].y > 290) {
					repairTruck[0].y = repairTruck[0].y - 20;
					repairTruck[0].man_y = repairTruck[0].man_y - 19;
				} else {
					repairTruck[0].man_x = repairTruck[0].man_x - 7;
					repairTruck[0].remove_truck += 1;
				}
			}
			if (repairTruck[1] != null) {
				if (repairTruck[1].remove_truck > 2) {
					repairTruck[1].truckImage = SmartAirport.repairtruck_r;
					repairTruck[1].x = repairTruck[1].x + 10;
				}
				if (repairTruck[1].y > 400) {
					repairTruck[1].y = repairTruck[1].y - 7;
					repairTruck[1].man_y = repairTruck[1].man_y - 6;
				} else {
					repairTruck[1].man_x = repairTruck[1].man_x - 10;
					repairTruck[1].remove_truck += 1;
				}
			}
		}
	}
		
	
	
}
