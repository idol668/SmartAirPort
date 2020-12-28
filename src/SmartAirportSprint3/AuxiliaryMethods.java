package SmartAirportSprint3;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class AuxiliaryMethods  {

	public AuxiliaryMethods() throws IOException {
	}

	public static Airplane putPlanesInWaitingArea(String state, int spotInWaitingArea, String planeType) {
		Airplane plane = null;
		if (state.equals("takeoff")) {
			if (spotInWaitingArea == 0) {
				plane = new Airplane(650, 300, 0, planeType, 0, true);
			} else {
				plane = new Airplane(650, 390, 0, planeType, 1, true);
			}
		}
		if (state.equals("landing")) {
			if (spotInWaitingArea == 0) {
				if (planeType.equals("COMMERCIAL")) {
					plane = new Airplane(50, 180, 180, planeType, 0, false);
				} else {
					plane = new Airplane(50, 155, 180, planeType, 0, false);
				}

			} else {
				if (planeType.equals("COMMERCIAL")) {
					plane = new Airplane(50, 500, 180, planeType, 1, false);
				} else {
					plane = new Airplane(50, 475, 180, planeType, 1, false);
				}
			}
		}
		return plane;
	}

	public void updateInputs(Map<String, String> inputs, Map<String, String> sysValues) {
		// take off and landing planes
		for (int i = 0; i < SmartAirport.takeoffPlaneExists.length; i++) {

			if (SmartAirport.takeoffPlaneExists[i] != null && !SmartAirport.takeoffAllowed[2 * i] && !SmartAirport.takeoffAllowed[2 * i + 1]) {
				inputs.put((String.format("takeoffAircrafts[%d]", i)), SmartAirport.takeoffPlaneExists[i].type);
			} else {
				setInput(inputs, String.format("takeoffAircrafts[%d]", i), SmartAirport.scenario);
			}

			if (SmartAirport.landingPlaneExists[i] != null && !SmartAirport.landingAllowed[2 * i] && !SmartAirport.landingAllowed[2 * i + 1]) {
				inputs.put((String.format("landingAircrafts[%d]", i)), SmartAirport.landingPlaneExists[i].type);
			} else {
				setInput(inputs, String.format("landingAircrafts[%d]", i), SmartAirport.scenario);
			}
			// mechanical Problem
			if (SmartAirport.takeoffPlaneExists[i] != null && SmartAirport.mechanicalProblem[i] && SmartAirport.repairTruck[i] == null) {
				inputs.put((String.format("mechanicalProblem[%d]", i)), String.valueOf(true));

			} else if (SmartAirport.repairTruck[i] != null) {
				inputs.put((String.format("mechanicalProblem[%d]", i)), String.valueOf(false));

			} else {
				setInput(inputs, String.format("mechanicalProblem[%d]", i), SmartAirport.scenario);
			}
		}
		// slippery Runway
		for (int i = 0; i < 4; i++) {
			if (SmartAirport.slipperyRunway[i] && !SmartAirport.cleaningSensors[i]) {
				inputs.put((String.format("slipperyRunway[%d]", i)), String.valueOf(true));
			} else if (SmartAirport.cleaningSensors[i]) {
				inputs.put((String.format("slipperyRunway[%d]", i)), String.valueOf(false));
			} else {
				setInput(inputs, String.format("slipperyRunway[%d]", i), SmartAirport.scenario);
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
				if (rescueArrived1 && SmartAirport.emergencyLanding[1]) {
					inputs.put("emergencyLanding[1]", String.valueOf(false));
				} else if (SmartAirport.emergencyLanding[1] && !rescueArrived1) {
					inputs.put("emergencyLanding[1]", String.valueOf(true));
				} else {
					setInput(inputs, "emergencyLanding[1]", SmartAirport.scenario);
				}
			} else if (isntLanding1) {
				inputs.put("emergencyLanding[1]", String.valueOf(false));
				if (rescueArrived0 && SmartAirport.emergencyLanding[0]) {
					inputs.put("emergencyLanding[0]", String.valueOf(false));
				} else if (SmartAirport.emergencyLanding[0] && !rescueArrived0) {
					inputs.put("emergencyLanding[0]", String.valueOf(true));
				} else {
					setInput(inputs, "emergencyLanding[0]", SmartAirport.scenario);
				}
			}
		} else {
			if ((rescueArrived0 && SmartAirport.emergencyLanding[0]) || (rescueArrived1 && SmartAirport.emergencyLanding[1])) {
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
		SmartAirport.scenarioCounter--;
		if(SmartAirport.scenarioCounter==0) {
			SmartAirport.scenario= "none";
			SmartAirport.inScenario =false;
			SmartAirport.startScenario = false;
		}
	}

	private void setInput(Map<String, String> inputs, String envVar, String scenario) {
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

	private void setEmergencyLandingInputs(Map<String, String> inputs, String scenario) {
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
			} else  {
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

	private void getInputNoneScenario(Map<String, String> inputs, String envVar) {
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

	private void getInputScaredOfFlyingScenario(Map<String, String> inputs, String envVar) {
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

	private void getInputBeingAMechanicIsHardScenario(Map<String, String> inputs, String envVar) {
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

	private void getInputSlipperySlopeScenario(Map<String, String> inputs, String envVar) {
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

	private void getInputRushHourScenario(Map<String, String> inputs, String envVar) {
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
}
