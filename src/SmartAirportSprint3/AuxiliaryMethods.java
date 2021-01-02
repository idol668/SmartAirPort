package SmartAirportSprint3;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import tau.smlab.syntech.controller.executor.ControllerExecutor;

public class AuxiliaryMethods {

	public AuxiliaryMethods() throws IOException {
	}

	public static Airplane putPlanesInWaitingArea(String state, int spotInWaitingArea, String planeType) {
		int takeoff_pos_x = 650;
		int landing_pos_x = 50;
		int takeoff_pos_north_y = 300;
		int takeoff_pos_south_y = 390;
		int landing_pos_north_y = 165;
		int landing_pos_south_y = 475;
		int landing_pos_commercial_north_y = 185;
		int landing_pos_commercial_south_y = 500;
		int degree_0 = 0;
		int degree_180 = 180;
		Airplane plane = null;
		
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

	public static void updateInputs(Map<String, String> inputs, Map<String, String> sysValues) {
		// take off and landing planes
		for (int i = 0; i < SmartAirport.takeoffPlaneExists.length; i++) {

			if (SmartAirport.takeoffPlaneExists[i] != null
					&& ((!SmartAirport.takeoffAllowed[2 * i] && !SmartAirport.takeoffAllowed[2 * i + 1])
							|| SmartAirport.mechanicalProblem[i])) {
				inputs.put(getTakeOffString(i), SmartAirport.takeoffPlaneExists[i].type);
			} else {
				setInput(inputs, getTakeOffString(i), SmartAirport.scenario);
			}

			if (SmartAirport.landingPlaneExists[i] != null && !SmartAirport.landingAllowed[2 * i]
					&& !SmartAirport.landingAllowed[2 * i + 1]) {
				inputs.put(getLandingString(i), SmartAirport.landingPlaneExists[i].type);
			} else {
				setInput(inputs, getLandingString(i), SmartAirport.scenario);
			}
			// mechanical Problem
			if (SmartAirport.takeoffPlaneExists[i] != null && SmartAirport.mechanicalProblem[i]
					&& SmartAirport.repairTruck[i] == null) {
				inputs.put(getMechanicalProblemString(i), String.valueOf(true));

			} else if (SmartAirport.repairTruck[i] != null) {
				inputs.put(getMechanicalProblemString(i), String.valueOf(false));

			} else {
				setInput(inputs, getMechanicalProblemString(i), SmartAirport.scenario);
			}
		}
		// slippery Runway
		for (int i = 0; i < 4; i++) {
			if (SmartAirport.slipperyRunway[i] && !SmartAirport.cleaningSensors[i]) {
				inputs.put(getSlipperyString(i), String.valueOf(true));
			} else if (SmartAirport.cleaningSensors[i]) {
				inputs.put(getSlipperyString(i), String.valueOf(false));
			} else {
				setInput(inputs, getSlipperyString(i), SmartAirport.scenario);
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
					setInput(inputs, "emergencyLanding[1]", SmartAirport.scenario);
				}
			} else if (isntLanding1) {
				inputs.put("emergencyLanding[1]", String.valueOf(false));
				if ( (rescueArrived0 && SmartAirport.emergencyLanding[0])  || (rescueArrived1 && SmartAirport.emergencyLanding[1] )) {
					inputs.put("emergencyLanding[0]", String.valueOf(false));
				} else if (SmartAirport.emergencyLanding[0] && !rescueArrived0) {
					inputs.put("emergencyLanding[0]", String.valueOf(true));
				} else {
					setInput(inputs, "emergencyLanding[0]", SmartAirport.scenario);
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
				setEmergencyLandingInputs(inputs, SmartAirport.scenario);
			}
		}
		if (SmartAirport.scenarioCounter > -1) {
			SmartAirport.scenarioCounter--;
		}
	}
	
	public static void updatePanelInputs(Map<String, String> inputs, Map<String, String> sysValues) {
		if (SmartAirport.inManualScenario) {
			for (int i = 0; i < 2; i++) {
				String Direction = ((i == 0) ? "north" : "south");
				if (SmartAirport.envMoves.get(String.format("takeoffAircrafts[%d]", i)) != null) {
					String planeType = SmartAirport.envMoves.get(String.format("takeoffAircrafts[%d]", i));
					//if already there is aircraft in this position
					if (SmartAirport.takeoffPlaneExists[i] != null && SmartAirport.takeoffPlaneExists[i].type.equals(planeType)) {
						continue;
					}
					//if there is no aircraft in the position OR the aircraft have no mechanical problem and already took off
					if (SmartAirport.takeoffPlaneExists[i] == null || (!SmartAirport.mechanicalProblem[i] && (SmartAirport.takeoffAllowed[2 * i] || SmartAirport.takeoffAllowed[2 * i + 1]))) {
						SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "- adding " + Direction + " "
								+ planeType.toLowerCase() + " takeoff aircraft\n");
						inputs.put((String.format("takeoffAircrafts[%d]", i)), planeType);
						SmartAirport.envMoves.remove(String.format("takeoffAircrafts[%d]", i));
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
						SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "- adding " + Direction + " "
								+ planeType.toLowerCase() + " land aircraft\n");
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

	private static void setInput(Map<String, String> inputs, String envVar, String scenario) {
		switch (scenario) {
		case "none":
			getInputNoneScenario(inputs, envVar);
			break;
		case "rush hour":
			getInputRushHourScenario(inputs, envVar);
			break;
		case "slippery slope":
			getInputSlipperySlopeScenario(inputs, envVar);
			break;
		case "being a mechanic is hard":
			getInputBeingAMechanicIsHardScenario(inputs, envVar);
			break;
		case "scared of flying":
			getInputScaredOfFlyingScenario(inputs, envVar);
			break;
		}

	}

	private static void setEmergencyLandingInputs(Map<String, String> inputs, String scenario) {
		switch (scenario) {
		case "rush hour":
		case "slippery slope":
		case "being a mechanic is hard":
			inputs.put("emergencyLanding[0]", String.valueOf(false));
			inputs.put("emergencyLanding[1]", String.valueOf(false));
			break;
		case "scared of flying":
			double emegencyChance = new Random().nextDouble();
			if (emegencyChance <= 0.5) {
				inputs.put("emergencyLanding[0]", String.valueOf(true));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(true));
			}
			break;
		case "none":
			double emegencyChanceNone = new Random().nextDouble();
			if (emegencyChanceNone <= 0.1) {
				inputs.put("emergencyLanding[0]", String.valueOf(true));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else if (emegencyChanceNone <= 0.2) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(true));
			} else {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			}
			break;
		}
	}

	private static void getInputNoneScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.15));
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.1));
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.1));
		}
	}

	private static void getInputScaredOfFlyingScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(true));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("emergencyLanding")) {
			Random rd = new Random();
			double chance = rd.nextDouble();
			if (chance <= 0.9) {
				inputs.put(envVar, String.valueOf(true));
			} else {
				inputs.put(envVar, String.valueOf(false));
			}
		}
	}

	private static void getInputBeingAMechanicIsHardScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(true));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("mechanicalProblem")) {
			Random rd = new Random();
			double chance = rd.nextDouble();
			if (chance <= 0.9) {
				inputs.put(envVar, String.valueOf(true));
			} else {
				inputs.put(envVar, String.valueOf(false));
			}
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(false));
		}
	}

	private static void getInputSlipperySlopeScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(false));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("slipperyRunway")) {
			Random rd = new Random();
			double chance = rd.nextDouble();
			if (chance <= 0.9) {
				inputs.put(envVar, String.valueOf(true));
			} else {
				inputs.put(envVar, String.valueOf(false));
			}
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(false));
		}
	}

	private static void getInputRushHourScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(true));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, acquireRandomPlane(true));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(false));
		}
	}

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

	public static void handleEndOfScenario() {
		boolean scenarioOver = (SmartAirport.inScenario && SmartAirport.scenarioCounter == 0)
				|| (SmartAirport.inManualScenario
						&& (SmartAirport.envMoves.isEmpty() || SmartAirport.envMoves == null));
		if (scenarioOver) {
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "Done\n");
			SmartAirport.inScenario = false;
			SmartAirport.inManualScenario = false;
			SmartAirport.startScenario = false;
			SmartAirport.scenario = "none";
		}
	}
	
	public static String getTakeOffString(int i) {
		return (String.format("takeoffAircrafts[%d]", i));
	}
	public static String getLandingString(int i) {
		return (String.format("landingAircrafts[%d]", i));
	}
	public static String getMechanicalProblemString(int i) {
		return (String.format("mechanicalProblem[%d]", i));
	}
	public static String getRescueTeamString(int i) {
		return (String.format("rescueTeam[%d]", i));
	}
	public static String getEmergencyLandingString(int i) {
		return (String.format("emergencyLanding[%d]", i));
	}
	public static String getSlipperyString(int i) {
		return (String.format("slipperyRunway[%d]", i));
	}
	
	public static void getEnvInputs(ControllerExecutor executor)
	{
		Map<String, String> envValues = executor.getCurrInputs();
		for (int i = 0; i < 4; i++) {
			String key = String.format("slipperyRunway[%d]", i);
			SmartAirport.slipperyRunway[i] = envValues.get(key).equals("true") ? true : false;
			if (SmartAirport.slipperyRunway[i]) {
				SmartAirport.stillCleaning[i] = true;
			} else {
				SmartAirport.stillCleaning[i] = false;
			}
		}
		for (int i = 0; i < 2; i++) {
			String key_mech = String.format("mechanicalProblem[%d]", i);
			String key_emerg =String.format("emergencyLanding[%d]", i);
			SmartAirport.mechanicalProblem[i] = envValues.get(key_mech).equals("true") ? true : false;
			SmartAirport.emergencyLanding[i] = envValues.get(key_emerg).equals("true") ? true : false;

			
		}
	}
	
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
				SmartAirport.repairTruck[i] = new RepairTruck(730, 520, i, SmartAirport.repairtruck_up);
			} else {
				SmartAirport.repairTruck[i] = null;
			}
			key = String.format("rescueTeam[%d]", i);
			if (sysValues.get(key).equals("true")) {
				SmartAirport.rescueTeams[i] = new RescueTeam(320, 650, i, SmartAirport.rescueteam_up, SmartAirport.flashlight);
			} else {
				SmartAirport.rescueTeams[i] = null;
			}

			/*
			 * key = String.format("ambulance[%d]", i); if
			 * (sysValues.get(key).equals("true")) { ambulances[i] = new Ambulance(345, 640,
			 * i, ambuImage_up); } else { ambulances[i] = null; }
			 */
		}
	}
	
}
