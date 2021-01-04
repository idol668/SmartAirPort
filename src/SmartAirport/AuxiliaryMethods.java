package SmartAirport;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import tau.smlab.syntech.controller.executor.ControllerExecutor;

//****************************************************************************************
//***              This class will contain our auxiliary methods                       ***
//***              This methods are often used during our simulation                   ***
//****************************************************************************************

public class AuxiliaryMethods {
	static int degree_0 = 0;
	static int degree_180 = 180;

	public AuxiliaryMethods() throws IOException {
	}
	
	/*
	 * This function creating planes in the smart airport according To environment variables.
	 */
	public static void creatingPlanesAccordingToExecutor(Map<String, String> envValues) {
		String takeoff_in_first_line  = getTakeOffString(0);
		String takeoff_in_second_line = getTakeOffString(1);
		String landing_in_first_line  = getLandingString(0);
		String landing_in_second_line = getLandingString(1);
				
		if(isAircraftExist(envValues,takeoff_in_first_line)) {
			Airplane plane = putPlanesInWaitingArea("takeoff", 0, envValues.get(takeoff_in_first_line));
			SmartAirport.takeoffPlaneExists[0] = plane;
		}else {
			SmartAirport.takeoffPlaneExists[0] = null;
		}

		if (isAircraftExist(envValues, takeoff_in_second_line)) {
			Airplane plane = putPlanesInWaitingArea("takeoff", 1, envValues.get(takeoff_in_second_line));
			SmartAirport.takeoffPlaneExists[1] = plane;
		} else {
			SmartAirport.takeoffPlaneExists[1] = null;
		}

		if (isAircraftExist(envValues, landing_in_first_line)) {
			Airplane plane = putPlanesInWaitingArea("landing", 0, envValues.get(landing_in_first_line));
			SmartAirport.landingPlaneExists[0] = plane;
		} else {
			SmartAirport.landingPlaneExists[0] = null;
		}
		if (isAircraftExist(envValues, landing_in_second_line)) {
			Airplane plane = putPlanesInWaitingArea("landing", 1,envValues.get(landing_in_second_line));
			SmartAirport.landingPlaneExists[1] = plane;
		} else {
			SmartAirport.landingPlaneExists[1] = null;
		}
	}
	
	/*
	 * This function returns if an aircraft exist in the environment variables
	 */
	public static boolean isAircraftExist(Map<String, String> envValues, String aircraftStr) {
		return !envValues.get(aircraftStr).equals("NONE");
	}
	
	/*
	 * This function returns take off aircraft string according to runway number
	 */
	public static String getTakeOffString(int i) {
		return (String.format("takeoffAircrafts[%d]", i));
	}
	
	/*
	 * This function returns landing aircraft string according to runway number
	 */
	public static String getLandingString(int i) {
		return (String.format("landingAircrafts[%d]", i));
	}
	
	/*
	 * This function returns take off aircraft allowed string according to runway number
	 */
	public static String getTakeOffAllowedString(int i) {
		return (String.format("takeoffAllowed[%d]", i));
	}
	
	/*
	 * This function returns landing aircraft allowed string according to runway number
	 */
	public static String getLandingAllowedString(int i) {
		return (String.format("landingAllowed[%d]", i));
	}
	/*
	 * This function returns mechanical problem string according to runway number
	 */
	public static String getMechanicalProblemString(int i) {
		return (String.format("mechanicalProblem[%d]", i));
	}
	/*
	 * This function returns repair truck string according to runway number
	 */
	public static String getRepairTruckString(int i) {
		return (String.format("repairTruck[%d]", i));
	}
	/*
	 * This function returns rescue team string according to runway number
	 */
	public static String getRescueTeamString(int i) {
		return (String.format("rescueTeam[%d]", i));
	}
	
	/*
	 * This function returns emergency landing aircraft string according to runway number
	 */
	public static String getEmergencyLandingString(int i) {
		return (String.format("emergencyLanding[%d]", i));
	}
	
	/*
	 * This function returns slippery runway string according to runway number
	 */
	public static String getSlipperyString(int i) {
		return (String.format("slipperyRunway[%d]", i));
	}
	
	/*
	 * This function returns clean truck string according to runway number
	 */
	public static String getCleanTruckString(int i) {
		return (String.format("cleanTruck[%d]", i));
	}

	/*
	 *  This function sets the planes in their respective waiting area using by changing its x and y values
	 *  The waiting area is a spot in the airport where the planes are waiting to get their landing or take off permissions
	 */
	public static Airplane putPlanesInWaitingArea(String state, int spotInWaitingArea, String planeType) {
		int takeoff_pos_x 		= 650; // the x value of the planes that are waiting for take off 
		int landing_pos_x 		= 50;  // the x value of the planes that are waiting for landing 
		int takeoff_pos_north_y = 300; // the y value of the planes that are waiting for takeoff in north
		int takeoff_pos_south_y = 390; // the y value of the planes that are waiting for for takeoff in south 
		int landing_pos_north_y = 165; // the y value of the planes that are waiting for landing in north
		int landing_pos_south_y = 475; // the y value of the planes that are waiting for landing in south
		int landing_pos_commercial_north_y = 185;
		int landing_pos_commercial_south_y = 500;

		Airplane plane = null;
		
		//An Airplane object is created according to its state take off or landing
		// If its take off we will pass the correct x and y values, else we will pass the landing x and y value
		if (state.equals("takeoff")) {
			if (spotInWaitingArea == 0) {
				plane = new Airplane(takeoff_pos_x, takeoff_pos_north_y, degree_0, planeType, 0, true);
			} else {
				plane = new Airplane(takeoff_pos_x, takeoff_pos_south_y, degree_0, planeType, 1, true);
			}
		}
		if (state.equals("landing")) {
			if (spotInWaitingArea == 0) {
				if (planeType.equals("COMMERCIAL")) {
					plane = new Airplane(landing_pos_x, landing_pos_commercial_north_y, degree_180, planeType, 0, false);
				} else {
					plane = new Airplane(landing_pos_x, landing_pos_north_y, degree_180, planeType, 0, false);
				}

			} else {
				if (planeType.equals("COMMERCIAL")) {
					plane = new Airplane(landing_pos_x, landing_pos_commercial_south_y, degree_180, planeType, 1, false);
				} else {
					plane = new Airplane(landing_pos_x, landing_pos_south_y, degree_180, planeType, 1, false);
				}
			}
		}
		return plane;
	}

	/*
	 * This function updates the state of the inputs in the controller
	 * We update the environment values according to the guarantees we defined in the specification 
	 */
	public static void updateInputs(Map<String, String> inputs, Map<String, String> sysValues) {
		
		// take off and landing planes - we update their state hence if they did perform take off or landing
		// if they didn't perform the landing or takeoff we will tell the controller that they are still waiting for the permission 
		for (int i = 0; i < SmartAirport.takeoffPlaneExists.length; i++) {

			if (SmartAirport.takeoffPlaneExists[i] != null
					&& ((!SmartAirport.takeoffAllowed[2 * i] && !SmartAirport.takeoffAllowed[2 * i + 1])
							|| SmartAirport.mechanicalProblem[i])) {
				inputs.put(getTakeOffString(i), SmartAirport.takeoffPlaneExists[i].type);
			} else {
				ScenarioFunctions.setInput(inputs, getTakeOffString(i), SmartAirport.scenario);
			}

			if (SmartAirport.landingPlaneExists[i] != null && !SmartAirport.landingAllowed[2 * i]
					&& !SmartAirport.landingAllowed[2 * i + 1]) {
				inputs.put(getLandingString(i), SmartAirport.landingPlaneExists[i].type);
			} else {
				ScenarioFunctions.setInput(inputs, getLandingString(i), SmartAirport.scenario);
			}
			// mechanical Problem - we update the mechanical issue in the plane according to if a repair truck arrived and fixed the issue or not
			if (SmartAirport.takeoffPlaneExists[i] != null && SmartAirport.mechanicalProblem[i]
					&& SmartAirport.repairTruck[i] == null) {
				inputs.put(getMechanicalProblemString(i), String.valueOf(true));

			} else if (SmartAirport.repairTruck[i] != null) {
				inputs.put(getMechanicalProblemString(i), String.valueOf(false));

			} else {
				ScenarioFunctions.setInput(inputs, getMechanicalProblemString(i), SmartAirport.scenario);
			}
		}
		// slippery runway - we update the controller according to the road condition- is it still slippery or cleaned by the cleaning truck
		for (int i = 0; i < 4; i++) {
			if (SmartAirport.slipperyRunway[i] && !SmartAirport.cleaningSensors[i]) {
				inputs.put(getSlipperyString(i), String.valueOf(true));
			} else if (SmartAirport.cleaningSensors[i]) {
				inputs.put(getSlipperyString(i), String.valueOf(false));
			} else {
				ScenarioFunctions.setInput(inputs, getSlipperyString(i), SmartAirport.scenario);
			}
		}
		// emergency Landing
		boolean rescueArrived0 = sysValues.get("rescueTeam[0]").equals("true");
		boolean rescueArrived1 = sysValues.get("rescueTeam[1]").equals("true");
		boolean isntLanding0 = inputs.get("landingAircrafts[0]").equals("NONE");
		boolean isntLanding1 = inputs.get("landingAircrafts[1]").equals("NONE");

		if (isntLanding0 || isntLanding1) {
			if (isntLanding0 && isntLanding1) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else if (isntLanding0) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				if ( (rescueArrived0 && SmartAirport.emergencyLanding[0])  || (rescueArrived1 && SmartAirport.emergencyLanding[1] )) {
					inputs.put("emergencyLanding[1]", String.valueOf(false));
				} else if (SmartAirport.emergencyLanding[1] && !rescueArrived1) {
					inputs.put("emergencyLanding[1]", String.valueOf(true));
				} else {
					ScenarioFunctions.setInput(inputs, "emergencyLanding[1]", SmartAirport.scenario);
				}
			} else if (isntLanding1) {
				inputs.put("emergencyLanding[1]", String.valueOf(false));
				if ( (rescueArrived0 && SmartAirport.emergencyLanding[0])  || (rescueArrived1 && SmartAirport.emergencyLanding[1] )) {
					inputs.put("emergencyLanding[0]", String.valueOf(false));
				} else if (SmartAirport.emergencyLanding[0] && !rescueArrived0) {
					inputs.put("emergencyLanding[0]", String.valueOf(true));
				} else {
					ScenarioFunctions.setInput(inputs, "emergencyLanding[0]", SmartAirport.scenario);
				}
			}
		} else {
			if ((rescueArrived0 && SmartAirport.emergencyLanding[0])
					|| (rescueArrived1 && SmartAirport.emergencyLanding[1])) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else if (SmartAirport.emergencyLanding[0] && !rescueArrived0) {
				inputs.put("emergencyLanding[0]", String.valueOf(true));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else if (SmartAirport.emergencyLanding[1] && !rescueArrived1) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(true));
			} else {
				ScenarioFunctions.setEmergencyLandingInputs(inputs, SmartAirport.scenario);
			}
		}
		if (SmartAirport.scenarioCounter > -1) {
			SmartAirport.scenarioCounter--;
		}
	}
	
	// This function updates the panel inputs - which is caused by an event when the user clicks on one of the buttons
	public static void updatePanelInputs(Map<String, String> inputs, Map<String, String> sysValues) {
		if (SmartAirport.inManualScenario) {
			for (int i = 0; i < 2; i++) {
				String Position = ((i == 0) ? "first" : "second");
				if (SmartAirport.envMoves.get(String.format("takeoffAircrafts[%d]", i)) != null) {
					String planeType = SmartAirport.envMoves.get(getTakeOffString(i));
					//if already there is aircraft in this position
					if (SmartAirport.takeoffPlaneExists[i] != null && SmartAirport.takeoffPlaneExists[i].type.equals(planeType)) {
						continue;
					}
					//if there is no aircraft in the position OR the aircraft have no mechanical problem and already took off
					if (SmartAirport.takeoffPlaneExists[i] == null || (!SmartAirport.mechanicalProblem[i] && (SmartAirport.takeoffAllowed[2 * i] || SmartAirport.takeoffAllowed[2 * i + 1]))) {
						SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "- adding " + planeType.toLowerCase() + " takeoff aircraft in "+ Position + " position\n");
						inputs.put(getTakeOffString(i), planeType);
						SmartAirport.envMoves.remove(getTakeOffString(i));
					}
				}
				if (SmartAirport.envMoves.get(getLandingString(i)) != null) {
					String planeType = SmartAirport.envMoves.get(getLandingString(i));
					//if already there is aircraft in this position
					if (SmartAirport.landingPlaneExists[i] != null && SmartAirport.landingPlaneExists[i].type.equals(planeType)) {
						continue;
					}
					//if there is no aircraft in the position OR the aircraft already landed
					if (SmartAirport.landingPlaneExists[i] == null || (SmartAirport.landingAllowed[2 * i] || SmartAirport.landingAllowed[2 * i + 1])) {
						SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "- adding " + planeType.toLowerCase() + " landing aircraft in "+ Position + " position\n");
						inputs.put(getLandingString(i), planeType);
						SmartAirport.envMoves.remove(getLandingString(i));
					}
				}

				if (SmartAirport.envMoves.get(getMechanicalProblemString(i)) != null) {
					//if this aircraft already fixed
					if (SmartAirport.repairTruck[i] != null) {
						continue;
					}
					inputs.put(getMechanicalProblemString(i), "true");
					SmartAirport.envMoves.remove(getMechanicalProblemString(i));
				}
				if (SmartAirport.envMoves.get(getEmergencyLandingString(i)) != null) {
					int j = ((i == 0) ? 1 : 0);
					boolean rescueArrived = sysValues.get(getRescueTeamString(i)).equals("true");
					boolean isAircaftLanding = !(inputs.get(getLandingString(i)).equals("NONE"));
					//if aircraft in the parallel runway is landed in emergency
					if (SmartAirport.emergencyLanding[j]) {
						continue;
					}
					//if there is a aircraft landing and is not already handled by rescue team.
					if (isAircaftLanding && !(SmartAirport.emergencyLanding[i] && rescueArrived)) {
						inputs.put(getEmergencyLandingString(i), String.valueOf(true));
						inputs.put(getEmergencyLandingString(j), String.valueOf(false));
						SmartAirport.envMoves.remove(getEmergencyLandingString(i));
					}
				}
			}
			for (int i = 0; i < 4; i++) {
				if (SmartAirport.envMoves.get(getSlipperyString(i)) != null) {
					//if this runway already slippery OR just cleaned;
					if(SmartAirport.slipperyRunway[i] || SmartAirport.cleaningSensors[i]) {
						continue;
					}
					inputs.put(getSlipperyString(i), "true");
					SmartAirport.envMoves.remove(getSlipperyString(i));
				}
			}
		}
	}
	

	// This function is used to for the initial run in our simulator and in some of the scenarios 
	// This function gets a random plane type 
	public static String acquireRandomPlane(boolean onlyAirCraft) {
		String planeType = "";
		Random rd = new Random();
		double chance;
		if (onlyAirCraft) {
			chance = rd.nextDouble() * 0.75 + 0.26;
		} else {
			chance = rd.nextDouble();
		}
		if (chance <= 0.25) {
			planeType = "NONE";
		} else if (chance <= 0.5) {
			planeType = "COMMERCIAL";
		} else if (chance <= 0.75) {
			planeType = "PRIVATE";
		} else {
			planeType = "CARGO";
		}
		return planeType;
	}

	
	// This function is used in order to get the environment inputs from the controller
	// This inputs are used to correctly draw the state of the airport
	public static void getEnvInputs(ControllerExecutor executor)
	{
		Map<String, String> envValues = executor.getCurrInputs();
		for (int i = 0; i < 4; i++) {
			SmartAirport.slipperyRunway[i] = envValues.get( getSlipperyString(i)).equals("true") ? true : false;
			if (SmartAirport.slipperyRunway[i]) {
				SmartAirport.stillCleaning[i] = true;
			} else {
				SmartAirport.stillCleaning[i] = false;
			}
		}
		for (int i = 0; i < 2; i++) {
			SmartAirport.mechanicalProblem[i] = envValues.get(getMechanicalProblemString(i)).equals("true") ? true : false;
			SmartAirport.emergencyLanding[i] = envValues.get(getEmergencyLandingString(i)).equals("true") ? true : false;		
		}
	}
	
	// This function is used in order to get the system inputs from the controller
	// This inputs are used to correctly draw the state of the airport
	public static void getSysInputs(ControllerExecutor executor)
	{
		Map<String, String> sysValues = executor.getCurrOutputs();
		String key = "";
		for (int i = 0; i < 4; i++) {
			key = String.format("takeoffAllowed[%d]", i);
			SmartAirport.takeoffAllowed[i] = sysValues.get(key).equals("true") ? true : false;
			key = String.format("landingAllowed[%d]", i);
			SmartAirport.landingAllowed[i] = sysValues.get(key).equals("true") ? true : false;
			key = String.format("cleanTruck[%d]", i);
			SmartAirport.cleaningSensors[i] = sysValues.get(key).equals("true") ? true : false;
		}
		
		for (int i = 0; i < 2; i++) {
			key = String.format("repairTruck[%d]", i);
			if (sysValues.get(key).equals("true")) {
				SmartAirport.repairTruck[i] = new RepairTruck(720, 600, i, AirportImages.repairtruck_up);
			} else {
				SmartAirport.repairTruck[i] = null;
			}
			key = String.format("rescueTeam[%d]", i);
			if (sysValues.get(key).equals("true")) {
				SmartAirport.rescueTeams[i] = new RescueTeam(320, 650, i, AirportImages.rescueteam_up, AirportImages.flashlight);
			} else {
				SmartAirport.rescueTeams[i] = null;
			}
		}
	}
	
	/*
	 * This function determines the plane position according to the chosen runway.
	 */
	public static int planePositionByRunwayNumber(int runwayLine) {
		int planePosition;
		switch (runwayLine) {
			case 0:
				planePosition = 155;
				break;
			case 1:
				planePosition = 255;
				break;
			case 2:
				planePosition = 455;
				break;
			default: // in case of the last runway line.
				planePosition = 555;
		}
		return planePosition;
	}
	
	/*
	 * This function is used for creating cleaning car in case the cleaning sensor is on 
	 */
	public static void createCleaningCars(int runwayLine, boolean isCleaningSensorsOn) {
		if(isCleaningSensorsOn) {
			// the x position of the cleaning truck and the oil is permanent in all runway lines
			int cleantruck_x_pos = 10;
			int oil_x_pos 		 = 450;
			// the x position of the cleaning truck and the oil changed according to runway line
			int cleantruck_y_pos;
			int oil_y_pos;
			switch (runwayLine) {
				case 0:
					cleantruck_y_pos = 155;
					oil_y_pos = 175;
					break;
				case 1:
					cleantruck_y_pos = 255;
					oil_y_pos = 275;
					break;
				case 2:
					cleantruck_y_pos = 455;
					oil_y_pos = 475;
					break;
				default: // in case of the last runway line.
					cleantruck_y_pos = 555;
					oil_y_pos = 575;	
			}
			SmartAirport.cleaningCars[runwayLine] = new CleaningTruck(cleantruck_x_pos, cleantruck_y_pos, degree_180, oil_x_pos, oil_y_pos);
		}
	}
	
}
