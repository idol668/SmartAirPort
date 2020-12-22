package SmartAirportSprint3;

import java.util.Map;
import java.util.Random;

public class AuxiliaryMethods {
	
	Airplane[] takeoffPlaneExists;
	Airplane[] landingPlaneExists;
	RepairTruck[] repairTruck;
	CleaningTruck[] cleaningCars;
	RescueTeam[] rescueTeams;

	public  AuxiliaryMethods(Airplane[] takeoffPlaneExists,Airplane[] landingPlaneExists,RepairTruck[] repairTruck,CleaningTruck[] cleaningCars,RescueTeam[] rescueTeams)
	{
		this.takeoffPlaneExists=takeoffPlaneExists;
		this.landingPlaneExists=landingPlaneExists;
		this.repairTruck=repairTruck;
		this.cleaningCars=cleaningCars;
		this.rescueTeams=rescueTeams;
	}
	public static Airplane putPlanesInWaitingArea(String state,int spotInWaitingArea,String planeType)
	{
		Airplane plane=null;
		if (state.equals("takeoff"))
		{
			if (spotInWaitingArea==0)
			{
				 plane = new Airplane(650, 300, 0,planeType,0, true);
			}
			else
			{
				 plane = new Airplane(650, 390, 0, planeType,1, true);
			}
		}
		if (state.equals("landing"))
		{
			if (spotInWaitingArea==0)
			{
				if (planeType.equals("COMMERCIAL"))
				{
					plane = new Airplane(50, 180, 180,planeType,0, false);
				}
				else
				{
					plane = new Airplane(50, 155, 180,planeType,0, false);
				}
				 
			}
			else
			{
				if (planeType.equals("COMMERCIAL"))
				{
					 plane = new Airplane(50, 500, 180,planeType,1, false);
				}	
				else
				{
					 plane = new Airplane(50, 475, 180,planeType,1, false);
				}
			}
		}
		return plane;
	}
	
	public  void updateInputs(Map<String, String> inputs, Map<String, String> sysValues, boolean [] takeoffAllowed,boolean[] landingAllowed,boolean[] mechanicalProblem, boolean[] cleaningSensors,boolean[] slipperyRunway, boolean[] emergencyLanding) {

		for (int i = 0; i < takeoffPlaneExists.length; i++) {

			if (takeoffPlaneExists[i] != null && !takeoffAllowed[2 * i] && !takeoffAllowed[2 * i + 1]){
				inputs.put((String.format("takeoffAircrafts[%d]", i)), takeoffPlaneExists[i].type);
			} else {
				inputs.put((String.format("takeoffAircrafts[%d]", i)), acquireRandomPlane());
			}

			if (landingPlaneExists[i] != null && !landingAllowed[2 * i] && !landingAllowed[2 * i + 1]) {
				inputs.put((String.format("landingAircrafts[%d]", i)), landingPlaneExists[i].type);
			} else {
				inputs.put((String.format("landingAircrafts[%d]", i)), acquireRandomPlane());
			}

			if (takeoffPlaneExists[i] != null && mechanicalProblem[i] && repairTruck[i] == null) {
				inputs.put((String.format("mechanicalProblem[%d]", i)), String.valueOf(true));
				
			} else if (repairTruck[i] != null) {
				inputs.put((String.format("mechanicalProblem[%d]", i)), String.valueOf(false));
				
			} else {
				inputs.put((String.format("mechanicalProblem[%d]", i)), String.valueOf(new Random().nextDouble() <0.15));
			}

		}
		for (int i = 0; i < 4; i++) {
			if (slipperyRunway[i] && !cleaningSensors[i]) {
				inputs.put((String.format("slipperyRunway[%d]", i)), String.valueOf(true));
			} else if (cleaningSensors[i]) {
				inputs.put((String.format("slipperyRunway[%d]", i)), String.valueOf(false));
			} else {
				
				inputs.put((String.format("slipperyRunway[%d]", i)), String.valueOf(new Random().nextDouble() <0.1));
			}

		}
		boolean rescueArrived0 = sysValues.get("rescueTeam[0]").equals("true");
		boolean rescueArrived1 = sysValues.get("rescueTeam[1]").equals("true");
		boolean isntLanding0 = inputs.get("landingAircrafts[0]").equals("NONE");
		boolean isntLanding1 = inputs.get("landingAircrafts[1]").equals("NONE");

		if (isntLanding0) {
			inputs.put("emergencyLanding[0]", String.valueOf(false));
		} else if (isntLanding1) {
			inputs.put("emergencyLanding[1]", String.valueOf(false));
		} else if ((rescueArrived0 && emergencyLanding[0]) || (rescueArrived1 && emergencyLanding[1])) {
			inputs.put("emergencyLanding[0]", String.valueOf(false));
			inputs.put("emergencyLanding[1]", String.valueOf(false));
		} else if (emergencyLanding[0] && !rescueArrived0) {
			inputs.put("emergencyLanding[0]", String.valueOf(true));
			inputs.put("emergencyLanding[1]", String.valueOf(false));
		} else if (emergencyLanding[1] && !rescueArrived1) {
			inputs.put("emergencyLanding[0]", String.valueOf(false));
			inputs.put("emergencyLanding[1]", String.valueOf(true));
		} else {
			double emegencyCHance = new Random().nextDouble();
			if (emegencyCHance <= 0.1) {
				inputs.put("emergencyLanding[0]", String.valueOf(true));
				inputs.put("emergencyLanding[1]", String.valueOf(false));
			} else if (emegencyCHance <= 0.2) {
				inputs.put("emergencyLanding[0]", String.valueOf(false));
				inputs.put("emergencyLanding[1]", String.valueOf(true));
			}

		}
	}
	
	public static String acquireRandomPlane() {
		String planeType = "";
		Random rd = new Random();
		double chance = rd.nextDouble();
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
