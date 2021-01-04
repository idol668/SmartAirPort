package SmartAirport;

import java.util.Map;
import java.util.Random;

//****************************************************************************************
//***              This class will contain our scenario methods                       ***
//****************************************************************************************

public class ScenarioFunctions {
	/*
	 * This function is used to describe the state of our simulator
	 * If the simulator is on manual or automatic state we will give it the value none
	 * Else if we chose a scenario, we will update the state of our simulator to the right scenario value
	 */
	public static void setInput(Map<String, String> inputs, String envVar, String scenario) {
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

	/*
	 *  This function sets the emergency landing input in each scenario
	 *  In case we are in none (automatic mode)- we would like to stimulate the simulator in the first state and get values
	 *  for the emergency landing 
	 */
	public static void setEmergencyLandingInputs(Map<String, String> inputs, String scenario) {
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
	
	// This function is used when we are not in a scenario and in the first state in our simulator
	// When we are in automatic state, we would like to stimulate the controller by giving the first environment values
	public static void getInputNoneScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, AuxiliaryMethods.acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, AuxiliaryMethods.acquireRandomPlane(false));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.15));
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.1));
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(new Random().nextDouble() < 0.1));
		}
	}

	// This function is used to get the inputs on the being scared of flying scenario
	public static void getInputScaredOfFlyingScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, AuxiliaryMethods.acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar, AuxiliaryMethods.acquireRandomPlane(true));
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

	// This function is used to get the inputs on the being mechanic is hard scenario
	public static void getInputBeingAMechanicIsHardScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar, AuxiliaryMethods.acquireRandomPlane(true));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar,  AuxiliaryMethods.acquireRandomPlane(false));
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

	// This function is used to get the inputs on the slippery slope scenario 
	public static void getInputSlipperySlopeScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar,  AuxiliaryMethods.acquireRandomPlane(false));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar,  AuxiliaryMethods.acquireRandomPlane(false));
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

	// This function is used to get the inputs in the rush hour scenario 
	public static void getInputRushHourScenario(Map<String, String> inputs, String envVar) {
		if (envVar.startsWith("takeoffAircrafts")) {
			inputs.put(envVar,  AuxiliaryMethods.acquireRandomPlane(true));
		} else if (envVar.startsWith("landingAircrafts")) {
			inputs.put(envVar,  AuxiliaryMethods.acquireRandomPlane(true));
		} else if (envVar.startsWith("mechanicalProblem")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("slipperyRunway")) {
			inputs.put(envVar, String.valueOf(false));
		} else if (envVar.startsWith("emergencyLanding")) {
			inputs.put(envVar, String.valueOf(false));
		}
	}
	
// This function handles the end of the scenario by setting all the correct values back to false 
// And printing Done to the user on the output screen 
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

}
