package SmartAirportSprint3;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.controller.executor.ControllerExecutor;

public class SmartAirport extends JPanel {

	private Image airport;
	private Image shadow_private_0;
	private Image shadow_private_180;
	private Image shadow_commerical_0;
	private Image shadow_commerical_180;
	private Image commercialplane_0;
	private Image commercialplane_180;
	private Image privateplane_0;
	private Image privateplane_180;
	private Image cargoplane_0;
	private Image cargoplane_180;
	private Image shadowcargo_0;
	private Image shadowcargo_180;
	private Image repairman;
	private Image repairtruck_up;
	private Image repairtruck_r;
	private Image rescueteam_up;
	private Image ambuImage_up;
	private Image rescueteam_r;
	private Image rescueteam_down;
	private Image ambuImage_r;
	boolean[] aircraftForLanding = new boolean[2];
	boolean[] aircraftForTakeoff = new boolean[2];
	
	// images for waiting to land
	private Image shadow_private_90;
	private Image shadow_private_270;
	private Image shadowcargo_90;
	private Image shadowcargo_270;
	private Image shadow_commerical_90;
	private Image shadow_commerical_270;
	
	// SlippryRunway variables
	private Image cleaningCar_0;
	private Image cleaningCar_90;
	private Image cleaningCar_180;
	private Image cleaningCar_270;
	private Image oilAndDirt;
	boolean[] cleaningSensors = new boolean[4];
	CleaningTruck[] cleaningCars = new CleaningTruck[4];
	boolean[] stillCleaning = new boolean[4];
	
	//Additional images for takeoff
	private Image shadow_commerical_0_medium;
	private Image shadow_commerical_0_small;
	private Image shadowcargo_0_medium;
	private Image shadowcargo_0_small;
	private Image shadow_private_0_medium;
	private Image shadow_private_0_small;



	int takeOffIteartion;
	
	static boolean inScenario = false;
	static boolean startScenario = false;
	
	boolean[] takeoffAllowed = new boolean[4];
	boolean[] landingAllowed = new boolean[4];

	Airplane[] takeoffPlaneExists = new Airplane[2];
	Airplane[] landingPlaneExists = new Airplane[2];

	ControllerExecutor executor;
	boolean[] emergencyLanding = new boolean[2];
	boolean[] slipperyRunway = new boolean[4];
	static boolean[] mechanicalProblem = new boolean[4];

	RepairTruck[] repairTruck = new RepairTruck[2];

	RescueTeam[] rescueTeams = new RescueTeam[2];
	Ambulance[] ambulances = new Ambulance[2];
	
	static Map<String, String> envMoves = new HashMap<>();

	boolean run = true;
	boolean finished = false;

	static JTextArea outputArea = new JTextArea("Here will be the output for scenarios and events \n", 0, 20);

	public SmartAirport() throws IOException {
		initialFields();
		initScene();

		Thread animationThread = new Thread(new Runnable() {
			public void run() {

				Map<String, String> inputs = new HashMap<>();

				try {
					executor = new ControllerExecutor(new BasicJitController(), "out");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				executor.initState(inputs);
				repaint();
				while (run) {

					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Map<String, String> envValues = executor.getCurrInputs();
					for (int i = 0; i < 4; i++) {
						String key = String.format("slipperyRunway[%d]", i);
						slipperyRunway[i] = envValues.get(key).equals("true") ? true : false;
						if (slipperyRunway[i]) {
							stillCleaning[i] = true;
						} else {
							stillCleaning[i] = false;
						}
					}
					mechanicalProblem[0] = envValues.get("mechanicalProblem[0]").equals("true") ? true : false;
					mechanicalProblem[1] = envValues.get("mechanicalProblem[1]").equals("true") ? true : false;

					emergencyLanding[0] = envValues.get("emergencyLanding[0]").equals("true") ? true : false;
					emergencyLanding[1] = envValues.get("emergencyLanding[1]").equals("true") ? true : false;

					//System.out.println(executor.getCurrInputs().toString());
					if (!envValues.get("takeoffAircrafts[0]").equals("NONE")) {
						Airplane plane = new Airplane(650, 170, 0, executor.getCurrInputs().get("takeoffAircrafts[0]"),
								0, true);
						takeoffPlaneExists[0] = plane;
					} else {
						takeoffPlaneExists[0] = null;
					}

					if (!envValues.get("takeoffAircrafts[1]").equals("NONE")) {
						Airplane plane = new Airplane(650, 470, 0, executor.getCurrInputs().get("takeoffAircrafts[1]"),
								1, true);
						takeoffPlaneExists[1] = plane;
					} else {
						takeoffPlaneExists[1] = null;
					}

					if (!envValues.get("landingAircrafts[0]").equals("NONE")) {
						Airplane plane = new Airplane(50, 165, 180, executor.getCurrInputs().get("landingAircrafts[0]"),
								0, false);
						landingPlaneExists[0] = plane;
					} else {
						landingPlaneExists[0] = null;
					}
					if (!envValues.get("landingAircrafts[1]").equals("NONE")) {
						Airplane plane = new Airplane(50, 465, 180, executor.getCurrInputs().get("landingAircrafts[1]"),
								0, false);
						landingPlaneExists[1] = plane;
					} else {
						landingPlaneExists[1] = null;
					}

					Map<String, String> sysValues = executor.getCurrOutputs();
					String key = "";
					for (int i = 0; i < 4; i++) {
						key = String.format("takeoffAllowed[%d]", i);
						takeoffAllowed[i] = sysValues.get(key).equals("true") ? true : false;
						key = String.format("landingAllowed[%d]", i);
						landingAllowed[i] = sysValues.get(key).equals("true") ? true : false;
						key = String.format("cleanTruck[%d]", i);
						cleaningSensors[i] = sysValues.get(key).equals("true") ? true : false;
					}
					for (int i = 0; i < 2; i++) {
						key = String.format("repairTruck[%d]", i);
						if (sysValues.get(key).equals("true")) {
							repairTruck[i] = new RepairTruck(730, 520, i, repairtruck_up);
						} else {
							repairTruck[i] = null;
						}
						key = String.format("rescueTeam[%d]", i);
						if (sysValues.get(key).equals("true")) {
							rescueTeams[i] = new RescueTeam(310, 650, i, rescueteam_up);
						} else {
							rescueTeams[i] = null;
						}

						/*
						 * key = String.format("ambulance[%d]", i); if
						 * (sysValues.get(key).equals("true")) { ambulances[i] = new Ambulance(345, 640,
						 * i, ambuImage_up); } else { ambulances[i] = null; }
						 */
					}

					// Create new CleaningCars
					if (cleaningSensors[0]) {
						cleaningCars[0] = new CleaningTruck(10, 155, 180, 450, 175);
					}

					if (cleaningSensors[1]) {
						cleaningCars[1] = new CleaningTruck(10, 255, 180, 450, 275);
					}

					if (cleaningSensors[2]) {
						cleaningCars[2] = new CleaningTruck(10, 455, 180, 450, 475);
					}

					if (cleaningSensors[3]) {
						cleaningCars[3] = new CleaningTruck(10, 555, 180, 450, 575);
					}

					System.out.println(sysValues.toString());
					repaint();
					animateEmergencyLanding();
					animateLandingAndTakeoff();
					
					updateInputs(inputs, sysValues);
					if(inScenario){
						if(envMoves.isEmpty() || envMoves==null)
						{
							outputArea.setText(outputArea.getText()+"Done\n");
//							System.out.println("Done");
							inScenario = false;
							startScenario = false;
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							for (int i = 0; i < 2; i++) {
								if(envMoves.get(String.format("mechanicalProblem[%d]", i))!= null){
									inputs.put((String.format("mechanicalProblem[%d]", i)),"true");
									envMoves.remove(String.format("mechanicalProblem[%d]", i));
								}
							}
							for (int i = 0; i < 4; i++) {
								if(envMoves.get(String.format("slipperyRunway[%d]", i))!= null){
									inputs.put((String.format("slipperyRunway[%d]", i)),"true");
									envMoves.remove(String.format("slipperyRunway[%d]", i));
								}
							}
						}
						
					}
					System.out.println(inputs.toString());
					
					
					try {
						executor.updateState(inputs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//executor.updateState(inputs);

					repaint();
					envMoves.clear();
				}

				finished = true;
			}
		});
		animationThread.start();
		repaint();
	}

	public void updateInputs(Map<String, String> inputs, Map<String, String> sysValues) {

		for (int i = 0; i < takeoffPlaneExists.length; i++) {

			if (takeoffPlaneExists[i] != null && !takeoffAllowed[2 * i] && !takeoffAllowed[2 * i + 1]) {
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

	public String acquireRandomPlane() {
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

	public void animateEmergencyLanding() {
		int landingLine = 0;
		for (int i = 0; i < 2; i++) {
			if (emergencyLanding[i] && landingPlaneExists[i] != null) {
				if (landingAllowed[2 * i] == true && landingAllowed[2 * i + 1] == true) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						landingPlaneExists[i].y = landingPlaneExists[i].y + 60;
						landingLine = i * 2 + 1;
					} else {
						landingPlaneExists[i].y = landingPlaneExists[i].y - 40;
						landingLine = i * 2;
					}
				} else if (landingAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					landingPlaneExists[i].y = landingPlaneExists[i].y - 40;
					landingLine = i * 2;
				} else if (landingAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					landingPlaneExists[i].y = landingPlaneExists[i].y + 60;
					landingLine = i * 2 + 1;
				}
				landingPlaneExists[i].x_shadow =landingPlaneExists[i].x;
				landingPlaneExists[i].y_shadow =landingPlaneExists[i].y;
				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof
						&& rescueTeams[i] != null) {
					
					for (int j = 0; j < 23; j++) {
						
						landingPlaneExists[i].x += 15;
						landingPlaneExists[i].x_shadow += 15;
						if (j == 9) {
							landingPlaneExists[i].ground = true;
						}
						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					landingPlaneExists[i].EnterLandingOrTakeoof = false;
					for (int j = 0; j < 50; j++) {
						if (landingPlaneExists[i]!=null && !landingPlaneExists[i].EnterLandingOrTakeoof & !emergencyLanding[i]){
							animatedWaitingForLanding(j,landingPlaneExists[i]);
						}
						if (landingLine == 0) {
							if (j < 42) {
								rescueTeams[i].y -= 12;
							} else {
								try {
									TimeUnit.MICROSECONDS.sleep(250);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else if (landingLine == 1) {
							if (j < 35) {
								rescueTeams[i].y -= 12;
							} else {
								try {
									TimeUnit.MICROSECONDS.sleep(150);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						if (landingLine == 2) {
							if (j < 12) {
								rescueTeams[i].y -= 12;
							}
							if (j < 14) {
								rescueTeams[i].y -= 5;
							} else {
								rescueTeams[i].rescueteamImage = rescueteam_r;
								try {
									TimeUnit.MICROSECONDS.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						if (landingLine == 3) {
							if (j < 6) {
								rescueTeams[i].y -= 12;
							} else {
								rescueTeams[i].rescueteamImage = rescueteam_r;
								try {
									TimeUnit.MICROSECONDS.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					for (int j = 0; j < 35; j++) {
						landingPlaneExists[i].x += 15;
						landingPlaneExists[i].x_shadow += 15;
						if (landingLine == 2 || landingLine == 3) {
							if (j < 2) {
								rescueTeams[i].x += 1;
							}
							rescueTeams[i].rescueteamImage = rescueteam_down;
							if (rescueTeams[i].y < 660) {
								rescueTeams[i].y += 12;
							}
						} else {
							rescueTeams[i].y -= 12;
						}

						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void animateRepairTruck(int i) {
		if (i>12)
		{
			if (repairTruck[0] != null) {
				if (repairTruck[0].remove_truck > 2) {
					repairTruck[0].truckImage = repairtruck_r;
					repairTruck[0].x = repairTruck[0].x + 10;
				}
				if (repairTruck[0].y > 170) {
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
				if (repairTruck[1].y > 460) {
					repairTruck[1].y = repairTruck[1].y - 5;
					repairTruck[1].man_y = repairTruck[1].man_y - 5;
				} else {
					repairTruck[1].man_x = repairTruck[1].man_x - 5;
					repairTruck[1].remove_truck += 1;
				}
			}
		}
		
	}

	public void animateLandingAndTakeoff() {
		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && !emergencyLanding[i]) {
				if (landingAllowed[2 * i] == true && landingAllowed[2 * i + 1] == true) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						landingPlaneExists[i].y = landingPlaneExists[i].y + 65;
					} else {
						landingPlaneExists[i].y = landingPlaneExists[i].y - 40;
					}
				} else if (landingAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					landingPlaneExists[i].y = landingPlaneExists[i].y - 40;
				} else if (landingAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					landingPlaneExists[i].EnterLandingOrTakeoof = true;
					landingPlaneExists[i].y = landingPlaneExists[i].y + 65;
				}
				landingPlaneExists[i].x_shadow =landingPlaneExists[i].x;
				landingPlaneExists[i].y_shadow =landingPlaneExists[i].y;
			}
			if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == false) {
				if (takeoffAllowed[2 * i] == true && takeoffAllowed[2 * i + 1] == true) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						takeoffPlaneExists[i].y = takeoffPlaneExists[i].y + 64;
						takeoffPlaneExists[i].y_shadow = takeoffPlaneExists[i].y_shadow + 64;
					} else {
						takeoffPlaneExists[i].y = takeoffPlaneExists[i].y - 46;
						takeoffPlaneExists[i].y_shadow = takeoffPlaneExists[i].y_shadow - 46;

					}
				} else if (takeoffAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					takeoffPlaneExists[i].y = takeoffPlaneExists[i].y - 46;
					takeoffPlaneExists[i].y_shadow = takeoffPlaneExists[i].y_shadow - 46;

				} else if (takeoffAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					takeoffPlaneExists[i].y = takeoffPlaneExists[i].y + 64;
					takeoffPlaneExists[i].y_shadow = takeoffPlaneExists[i].y_shadow + 64;
				} else {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
				}
			} else if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == true) {
				takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
			}
		}

		boolean planeOrTruckMooving = false;
		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof) {
				planeOrTruckMooving = true;
			}
			if (takeoffPlaneExists[i] != null && takeoffPlaneExists[i].EnterLandingOrTakeoof) {
				planeOrTruckMooving = true;
			}
			if (repairTruck[i] != null && mechanicalProblem[i] == true) {
				planeOrTruckMooving = true;
			}
			if (cleaningCars[2 * i] != null && slipperyRunway[2 * i] == true) {
				planeOrTruckMooving = true;
			}
			if (cleaningCars[2 * i + 1] != null && slipperyRunway[2 * i + 1] == true) {
				planeOrTruckMooving = true;
			}

		}

		if (!planeOrTruckMooving) {
			return;
		}

		repaint();

		for (int j = 0; j < 50; j++) {
			takeOffIteartion=j;
			for (int i = 0; i < 2; i++) {
				
				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof && j>=9) {
					if (j<21)
					{
						landingPlaneExists[i].x_shadow +=18;
						landingPlaneExists[i].x += 20;
						
					}
					
					else if (j == 21) {
						landingPlaneExists[i].ground = true;
					}
					else
					{
						if(j<34)
						{
							landingPlaneExists[i].x += 20;
							landingPlaneExists[i].x_shadow +=22;
						}
						else
						{
							if (j>33 && j<40)
							{
								landingPlaneExists[i].x += 15;
							}
							else
							{
								landingPlaneExists[i].x += 10;
							}
								
							landingPlaneExists[i].x_shadow =landingPlaneExists[i].x;
							landingPlaneExists[i].y_shadow =landingPlaneExists[i].y;
							
						}
							
					}
				}
				
				if (landingPlaneExists[i]!=null && !landingPlaneExists[i].EnterLandingOrTakeoof ){
					animatedWaitingForLanding(j,landingPlaneExists[i]);
				}
				if (takeoffPlaneExists[i] != null && takeoffPlaneExists[i].EnterLandingOrTakeoof & j>=22) {

					takeoffPlaneExists[i].x -= 20;
					takeoffPlaneExists[i].x_shadow = (int) Math.round(takeoffPlaneExists[i].x_shadow - 20);
					takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow - 1);
					if (j == 31) {
						takeoffPlaneExists[i].ground = false;
					}
				}
			}
			animatedCleanTruck(j);
			animateRepairTruck(j);
			repaint();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof) {
				landingPlaneExists[i] = null;
			}
			if (takeoffPlaneExists[i] != null && takeoffPlaneExists[i].EnterLandingOrTakeoof) {
				takeoffPlaneExists[i] = null;
			}
		}
	}

	private void drawRescueTeam(Graphics g, RescueTeam rescue) {
		g.drawImage(rescue.rescueteamImage, rescue.x, rescue.y, this);
	}

	private void drawAmbulance(Graphics g, Ambulance ambu) {
		g.drawImage(ambu.ambuImage, ambu.x, ambu.y, this);
	}

	private void drawTruck(Graphics g, RepairTruck truck) {
		if (truck.line == 0) {
			g.drawImage(repairman, truck.man_x + 40, truck.man_y + 40, this);
		} else {
			g.drawImage(repairman, truck.man_x + 40, truck.man_y + 15, this);
		}
		g.drawImage(truck.truckImage, truck.x, truck.y, this);
	}

	private void drawPlane(Graphics g, Airplane plane) {
		if (plane.degree == 0) {
			if (plane.ground == false) {
				if (plane.type.equals("COMMERCIAL")) {
						g.drawImage(shadow_commerical_0, plane.x_shadow, plane.y_shadow, this);
				} else if (plane.type.equals("PRIVATE")) {
						g.drawImage(shadow_private_0, plane.x_shadow, plane.y_shadow, this);	
				} else if (plane.type.equals("CARGO")) {
						g.drawImage(shadowcargo_0, plane.x_shadow, plane.y_shadow, this);	
				}
			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_0, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_0, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_0, plane.x, plane.y, this);

				}
			}
		}
		if (plane.degree == 180) {
			if (plane.ground == false) {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_180, plane.x_shadow, plane.y_shadow, this);
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_180, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_180, plane.x, plane.y, this);

				}
			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_180, plane.x, plane.y, this);
				
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_180, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_180, plane.x, plane.y, this);

				}

			}
		}

		if (plane.degree==90) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_90, plane.x_shadow, plane.y_shadow, this);
				
				}
				else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_90,plane.x_shadow, plane.y_shadow, this);
				
				}
				 else if (plane.type.equals("CARGO")) {
						g.drawImage(shadowcargo_90, plane.x_shadow, plane.y_shadow, this);

				}
			}
		}
		if (plane.degree==270) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_270, plane.x_shadow, plane.y_shadow, this);
				
				}
				else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_270,plane.x_shadow, plane.y_shadow, this);
				
				}
				
			 else if (plane.type.equals("CARGO")) {
				g.drawImage(shadowcargo_270, plane.x_shadow, plane.y_shadow, this);

			 	}
				
			}
		}
		return;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(airport, 0, 0, this);

		if (repairTruck[0] != null) {
			drawTruck(g, repairTruck[0]);
		}
		if (repairTruck[1] != null) {
			drawTruck(g, repairTruck[1]);
		}
		if (takeoffPlaneExists[0] != null) {
			drawPlane(g, takeoffPlaneExists[0]);
		}
		if (takeoffPlaneExists[1] != null) {
			drawPlane(g, takeoffPlaneExists[1]);
		}
		if (landingPlaneExists[0] != null) {
			drawPlane(g, landingPlaneExists[0]);
		}
		if (landingPlaneExists[1] != null) {
			drawPlane(g, landingPlaneExists[1]);
		}

		for (int i = 0; i < 4; i++) {
			if (slipperyRunway[i] == true) {
				if (stillCleaning[i]) {
					drawOil(g, i);
				}
				if (cleaningSensors[i] == true && cleaningCars[i] != null) {
					drawCleaningTruck(g, cleaningCars[i]);
				}
			}
		}
		if (rescueTeams[0] != null) {
			drawRescueTeam(g, rescueTeams[0]);
		}
		if (rescueTeams[1] != null) {
			drawRescueTeam(g, rescueTeams[1]);
		}
		if (ambulances[0] != null) {
			drawAmbulance(g, ambulances[0]);
		}
		if (ambulances[1] != null) {
			drawAmbulance(g, ambulances[1]);
		}

	}

	private void initScene() {
		Random rand = new Random();

		for (int i = 0; i < aircraftForLanding.length; i++) {
			aircraftForLanding[i] = rand.nextBoolean();
		}
		for (int i = 0; i < aircraftForTakeoff.length; i++) {
			aircraftForTakeoff[i] = rand.nextBoolean();
		}

	};

	private void initialFields() throws IOException {
		airport = ImageIO.read(new File("img/airport.png"));
		commercialplane_0 = ImageIO.read(new File("img/commercialplane_0.png"));
		privateplane_0 = ImageIO.read(new File("img/privateplane_0.png"));
		cargoplane_0 = ImageIO.read(new File("img/cargoplane_0.png"));
		commercialplane_180 = ImageIO.read(new File("img/commercialplane_180.png"));
		privateplane_180 = ImageIO.read(new File("img/privateplane_180.png"));
		cargoplane_180 = ImageIO.read(new File("img/cargoplane_180.png"));
		shadowcargo_0 = ImageIO.read(new File("img/shadowcargo_0.png"));
		shadow_private_0 = ImageIO.read(new File("img/shadow_private_0.png"));
		shadow_commerical_0 = ImageIO.read(new File("img/shadow_commerical_0.png"));
		shadow_private_180 = ImageIO.read(new File("img/shadow_private_180.png"));
		shadow_commerical_180 = ImageIO.read(new File("img/shadow_commerical_180.png"));
		shadowcargo_180 = ImageIO.read(new File("img/shadowcargo_180.png"));
		repairman = ImageIO.read(new File("img/man1.png"));
		repairtruck_up = ImageIO.read(new File("img/repairtruck_up.png"));
		repairtruck_r = ImageIO.read(new File("img/repairtruck_right.png"));
		
		//Images for waiting to land
		
		shadowcargo_90 = ImageIO.read(new File("img/shadowcargo_90.png"));
		shadowcargo_270 = ImageIO.read(new File("img/shadowcargo_270.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270.png"));
		shadow_private_90 = ImageIO.read(new File("img/shadow_private_90.png"));
		shadow_private_270 = ImageIO.read(new File("img/shadow_private_270.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270.png"));

		//Additional Images for takeoff
		shadow_commerical_0_medium = ImageIO.read(new File("img/shadow_commerical_0_medium.png"));
		shadow_commerical_0_small = ImageIO.read(new File("img/shadow_commerical_0_small.png"));
		shadowcargo_0_medium=ImageIO.read(new File("img/shadowcargo_0_medium.png"));
		shadowcargo_0_small=ImageIO.read(new File("img/shadowcargo_0_small.png"));
		shadow_private_0_medium=ImageIO.read(new File("img/shadow_private_0_medium.png"));
		shadow_private_0_small=ImageIO.read(new File("img/shadow_private_0_small.png"));
		
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

		for (int i = 0; i < 4; i++) {
			takeoffAllowed[i] = false;
		}
	}

	public static JPanel createControlPanel(SmartAirport smartAirport) {
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel controlPanelHeadAndEvents = new JPanel(new BorderLayout());
		JPanel controlPanelEvents = new JPanel(new BorderLayout());
		JPanel controlPanelScenarionsAndOutput = new JPanel(new BorderLayout());
		JPanel outputPanelAndLabel = new JPanel(new BorderLayout());
		JPanel headPanel = AirportPanel.createHeadLinePanel();
		JPanel eventsPanel = AirportPanel.createEventsPanel(smartAirport);
		JPanel scenariosPanel = AirportPanel.createScenariosPanel(smartAirport);
		JPanel dirtyRunway = AirportPanel.createDirtyRunwayPanel(smartAirport);
		JPanel EmergencyPanel = AirportPanel.createEmergencyLandingPanel(smartAirport);
		JPanel MechanicalPanel = AirportPanel.createMechanicalProblemPanel(smartAirport);
		JPanel outputPanel = new JPanel();
		JPanel outputLabelPanel = new JPanel();

		JLabel outputLabel = new JLabel("<html><span style='font-size:14px'>Output</span></html>");

		outputLabelPanel.add(outputLabel);
		outputPanel.add(outputArea);
		outputPanelAndLabel.add(outputLabelPanel, BorderLayout.NORTH);
		outputPanelAndLabel.add(outputPanel, BorderLayout.CENTER);
		
		controlPanelEvents.add(EmergencyPanel, BorderLayout.NORTH);
		controlPanelEvents.add(dirtyRunway,BorderLayout.CENTER);
		controlPanelEvents.add(MechanicalPanel, BorderLayout.SOUTH);
		
		controlPanelHeadAndEvents.add(headPanel, BorderLayout.NORTH);
		controlPanelHeadAndEvents.add(eventsPanel, BorderLayout.CENTER);
		controlPanelHeadAndEvents.add(controlPanelEvents,  BorderLayout.SOUTH);
		
		controlPanelScenarionsAndOutput.add(scenariosPanel, BorderLayout.NORTH);
		controlPanelScenarionsAndOutput.add(outputPanelAndLabel, BorderLayout.CENTER);

		controlPanel.add(controlPanelHeadAndEvents, BorderLayout.NORTH);
		controlPanel.add(controlPanelScenarionsAndOutput, BorderLayout.CENTER);

		return controlPanel;
	}

	public static JSplitPane createMainPanel(SmartAirport smartAirport) {
		JPanel controlPanel = createControlPanel(smartAirport);
		JSplitPane splitPanel = new JSplitPane(SwingConstants.VERTICAL, smartAirport, controlPanel);
		splitPanel.setDividerLocation(800);
		return splitPanel;
	}

	public static void main(String[] args) throws IOException {

		JFrame SmartAirport = new JFrame("Airprot Simulator");
		SmartAirport smartAirport = new SmartAirport();

		SmartAirport.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		SmartAirport.setSize(1150, 800);
		SmartAirport.setContentPane(createMainPanel(smartAirport));
		SmartAirport.setVisible(true);
	}

	private void drawOil(Graphics g, int i) {
		if (i == 0) {
			g.drawImage(oilAndDirt, 320, 175, this);
		}

		if (i == 1) {
			g.drawImage(oilAndDirt, 320, 275, this);
		}

		if (i == 2) {
			g.drawImage(oilAndDirt, 320, 475, this);// 1
		}

		if (i == 3) {
			g.drawImage(oilAndDirt, 320, 575, this);// 1
		}

	}

	private void drawCleaningTruck(Graphics g, CleaningTruck cleanCar) {
		if (cleanCar.degree == 0) {
			g.drawImage(cleaningCar_0, cleanCar.x, cleanCar.y, this);
		}

		if (cleanCar.degree == 180) {
			g.drawImage(cleaningCar_180, cleanCar.x, cleanCar.y, this);
		}
		if (cleanCar.degree == 90) {
			g.drawImage(cleaningCar_90, cleanCar.x, cleanCar.y, this);
		}
		if (cleanCar.degree == 270) {
			g.drawImage(cleaningCar_270, cleanCar.x, cleanCar.y, this);
		}
	}

	private void animatedCleanTruck(int timeToRemoveDirtOrTruck) {

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
		}
		
		if (iteration>9 & iteration <20)
		{
			aircraft.degree=90;
			aircraft.y_shadow += 3;
		}
		
		if (iteration>19 & iteration <30)
		{
			aircraft.degree=0;
			aircraft.x_shadow -= 4;
		}
		
		if (iteration>29 & iteration <38)
		{
			aircraft.degree=270;
			aircraft.y_shadow -= 3;
		}
		
		if (iteration==39)
		{
			aircraft.degree=180;
		}
		
		
	}

	public void setStartScenario() {
		startScenario = true;
		
	}
	

}