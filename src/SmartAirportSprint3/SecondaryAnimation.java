package SmartAirportSprint3;

import java.awt.Image;

public class SecondaryAnimation {
	
	/*--------------- This class will deal with the animation of our features such as : Slippery runway and Mechanical Problem--------------------*/
	
	boolean[] cleaningSensors;
	CleaningTruck [] cleaningCars;
	boolean[] stillCleaning;
	boolean[] slipperyRunway;
	RepairTruck[] repairTruck;
	boolean[] mechanicalProblem;
	private Image repairtruck_r;
	
	public SecondaryAnimation (boolean[] cleanSensors,CleaningTruck [] cleanCars,boolean[] stillCleaning,boolean[] slipperyRunway,RepairTruck[] repairTruck,boolean[] mechanicalProblem,Image repairtruck_r)
	{
		this.cleaningSensors=cleanSensors;
		this.cleaningCars=cleanCars;
		this.stillCleaning=stillCleaning;
		this.slipperyRunway=slipperyRunway;
		this.repairTruck=repairTruck;
		this.mechanicalProblem=mechanicalProblem;
		this.repairtruck_r=repairtruck_r;

	}
	
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
	
	public void animateRepairTruck(int i) {
		if (i>22)
		{
			if (repairTruck[0] != null) {
				if (repairTruck[0].remove_truck > 2) {
					repairTruck[0].truckImage = repairtruck_r;
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
					repairTruck[1].truckImage = repairtruck_r;
					repairTruck[1].x = repairTruck[1].x + 10;
				}
				if (repairTruck[1].y > 400) {
					repairTruck[1].y = repairTruck[1].y - 5;
					repairTruck[1].man_y = repairTruck[1].man_y - 5;
				} else {
					repairTruck[1].man_x = repairTruck[1].man_x - 10;
					repairTruck[1].remove_truck += 1;
				}
			}
		}
		
	}
	


}
