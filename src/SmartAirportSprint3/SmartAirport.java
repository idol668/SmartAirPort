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

import SmartAirportSprint3.AuxiliaryMethods;

import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.controller.executor.ControllerExecutor;

public class SmartAirport extends JPanel {

	private static final long serialVersionUID = 1L;
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
	static Image repairtruck_up;
	static Image repairtruck_r;
	static Image rescueteam_up;
	static Image rescueteam_r;
	private Image rescueteam_down;
	private Image ambuImage_up;
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
	static Image oilAndDirt;
	static boolean[] cleaningSensors = new boolean[4];
	static CleaningTruck[] cleaningCars = new CleaningTruck[4];
	static boolean[] stillCleaning = new boolean[4];

	// Additional images for takeoff
	private Image privateplane_90;
	private Image privateplane_270;
	private Image commercialplane_90;
	private Image commercialplane_270;
	private Image cargoplane_90;
	private Image cargoplane_270;
	static Image flashlight;
	int takeOffIteartion;

	static boolean[] takeoffAllowed = new boolean[4];
	static boolean[] landingAllowed = new boolean[4];

	static Airplane[] takeoffPlaneExists = new Airplane[2];
	static Airplane[] landingPlaneExists = new Airplane[2];

	static ControllerExecutor executor;
	static boolean[] emergencyLanding = new boolean[2];
	static boolean[] slipperyRunway = new boolean[4];
	static boolean[] mechanicalProblem = new boolean[2];

	static RepairTruck[] repairTruck = new RepairTruck[2];

	static RescueTeam[] rescueTeams = new RescueTeam[2];
	static Ambulance[] ambulances = new Ambulance[2];

	static Map<String, String> envMoves = new HashMap<>();

	static boolean inScenario = false;
	static boolean inManualScenario = false;
	static boolean startScenario = false;
	static boolean wait = false;
	static String scenario = "none";
	static int scenarioCounter = -1;

	static boolean run = true;
	static boolean finished = false;

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
					e1.printStackTrace();
				}
				executor.initState(inputs);
				System.out.println("init input" + executor.getCurrInputs().toString());
				repaint();
				while (run) {

					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Map<String, String> envValues = executor.getCurrInputs();
					AuxiliaryMethods.getEnvInputs(executor);
					
					/*for (int i = 0; i < 4; i++) {
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
					emergencyLanding[1] = envValues.get("emergencyLanding[1]").equals("true") ? true : false;*/

					// System.out.println(executor.getCurrInputs().toString());
					if (!envValues.get("takeoffAircrafts[0]").equals("NONE")) {
						Airplane plane = AuxiliaryMethods.putPlanesInWaitingArea("takeoff", 0,
								executor.getCurrInputs().get("takeoffAircrafts[0]"));
						takeoffPlaneExists[0] = plane;
					} else {
						takeoffPlaneExists[0] = null;
					}

					if (!envValues.get("takeoffAircrafts[1]").equals("NONE")) {
						Airplane plane = AuxiliaryMethods.putPlanesInWaitingArea("takeoff", 1,
								executor.getCurrInputs().get("takeoffAircrafts[1]"));
						takeoffPlaneExists[1] = plane;
					} else {
						takeoffPlaneExists[1] = null;
					}

					if (!envValues.get("landingAircrafts[0]").equals("NONE")) {
						Airplane plane = AuxiliaryMethods.putPlanesInWaitingArea("landing", 0,
								executor.getCurrInputs().get("landingAircrafts[0]"));
						landingPlaneExists[0] = plane;
					} else {
						landingPlaneExists[0] = null;
					}
					if (!envValues.get("landingAircrafts[1]").equals("NONE")) {
						Airplane plane = AuxiliaryMethods.putPlanesInWaitingArea("landing", 1,
								executor.getCurrInputs().get("landingAircrafts[1]"));
						landingPlaneExists[1] = plane;
					} else {
						landingPlaneExists[1] = null;
					}

					Map<String, String> sysValues = executor.getCurrOutputs();
					AuxiliaryMethods.getSysInputs(executor);
					/*String key = "";	
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
					}*/
						/*
						 * key = String.format("ambulance[%d]", i); if
						 * (sysValues.get(key).equals("true")) { ambulances[i] = new Ambulance(345, 640,
						 * i, ambuImage_up); } else { ambulances[i] = null; }
						 */
					

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
					System.out.println("System Values:"+sysValues.toString());
					System.out.println("Env Values:"+envValues.toString());
					repaint();
					animateEmergencyLanding();
					animateLandingAndTakeoff();
					while (wait) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					AuxiliaryMethods.handleEndOfScenario();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					AuxiliaryMethods.updateInputs(inputs, sysValues);
					AuxiliaryMethods.updatePanelInputs(inputs, sysValues);
					try {
						executor.updateState(inputs);
					} catch (Exception e) {
						e.printStackTrace();
					}
					repaint();
				}

				finished = true;
			}
		});
		animationThread.start();
		repaint();
	}

	public void animateEmergencyLanding() {
		for (int i = 0; i < 2; i++) {
			if (emergencyLanding[i] && landingPlaneExists[i] != null) {
				SecondaryAnimation.setLandingPlaneInPositionLine(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof
						&& rescueTeams[i] != null) {

					for (int j = 0; j < 22; j++) {
						landingPlaneExists[i].movingPlaneAndShadow(15);
						rescueTeams[i].TurnFlashLight();
						if (j == 9) {
							landingPlaneExists[i].ground = true;
						}
						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					landingPlaneExists[i].movingPlaneAndShadow(4);
					landingPlaneExists[i].EnterLandingOrTakeoof = false;
					
					for (int j = 0; j < 50; j++) {
						SecondaryAnimation.movingRescueTeamToLandingLine(landingPlaneExists[i].line, j, rescueTeams[i]);
						repaint();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					for (int j = 0; j < 40; j++) {
						landingPlaneExists[i].movingPlaneAndShadow(10);
						if (landingPlaneExists[i].line == 2 || landingPlaneExists[i].line == 3) {
							if (j < 2) {
								rescueTeams[i].x += 1;
							}
							if (rescueTeams[i] != null) {
								rescueTeams[i].rescueteamImage = rescueteam_down;
							}

							if (rescueTeams[i] != null && rescueTeams[i].y < 660) {
								rescueTeams[i].y += 12;
							}
							if (rescueTeams[i] != null && rescueTeams[i].y >= 660) {
								rescueTeams[i] = null;
							}
						} else {
							if (rescueTeams[i] != null) {
								rescueTeams[i].rescueteamImage = rescueteam_down;
								rescueTeams[i].y += 13;
							}
						}
						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			rescueTeams[i] = null;
		}
	}

	public void animateLandingAndTakeoff() {
		SecondaryAnimation secondarySimulator = new SecondaryAnimation(cleaningSensors, cleaningCars, stillCleaning,
				slipperyRunway, repairTruck, mechanicalProblem);
		Airplane[] planes = new Airplane[2];
		int[] runwaysTakeOff = new int[2];
		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && !emergencyLanding[i]) {
				SecondaryAnimation.setLandingPlaneInPositionLine(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
			}
			if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == false) {

				if (takeoffAllowed[2 * i] == true && takeoffAllowed[2 * i + 1] == true) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						planes[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i + 1;
					} else {
						planes[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i;
					}
				} else if (takeoffAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					planes[i] = takeoffPlaneExists[i];
					runwaysTakeOff[i] = 2 * i;

				} else if (takeoffAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					planes[i] = takeoffPlaneExists[i];
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					runwaysTakeOff[i] = 2 * i + 1;
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
		animateGetToTakeOffSpot(runwaysTakeOff, planes);
		for (int j = 0; j < 50; j++) {
			takeOffIteartion = j;
			for (int i = 0; i < 2; i++) {

				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof && j >= 9) {
					if (j < 21) {
						landingPlaneExists[i].x_shadow += 18;
						landingPlaneExists[i].x += 20;
						if (j>15 && j<21){
							landingPlaneExists[i].y -=1; 
						}	
					}

					else if (j == 21) {
						landingPlaneExists[i].ground = true;
					} else {
						if (j < 34) {
							landingPlaneExists[i].x += 20;
							landingPlaneExists[i].x_shadow += 22;
						} else {
							if (j > 33 && j < 40) {
								if (landingPlaneExists[i].y > 165 && landingPlaneExists[i].y < 400) {
									landingPlaneExists[i].y -= 1;
								}
								if (landingPlaneExists[i].y > 400 && landingPlaneExists[i].y < 500) {
									landingPlaneExists[i].y += 2;
								}
								landingPlaneExists[i].x += 15;
							} else {
								if (landingPlaneExists[i].y > 165 && landingPlaneExists[i].y < 400) {
									landingPlaneExists[i].y -= 1;
								}
								if (landingPlaneExists[i].y > 400 && landingPlaneExists[i].y < 500) {
									landingPlaneExists[i].y += 1;
								}
								
								landingPlaneExists[i].x += 10;
							}

							landingPlaneExists[i].x_shadow = landingPlaneExists[i].x;
							landingPlaneExists[i].y_shadow = landingPlaneExists[i].y;

						}
					}
				}

				if (landingPlaneExists[i] != null && !landingPlaneExists[i].EnterLandingOrTakeoof) {
					secondarySimulator.animatedWaitingForLanding(j, landingPlaneExists[i]);
				}
				if (takeoffPlaneExists[i] != null && takeoffPlaneExists[i].EnterLandingOrTakeoof & j >= 20) {
					if (j<29)
					{
						takeoffPlaneExists[i].x -= 20;
						takeoffPlaneExists[i].x_shadow = (int) Math.round(takeoffPlaneExists[i].x_shadow - 20);
						takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow - 1);
					}
					
					if (j == 29) {
						takeoffPlaneExists[i].ground = false;
					}
					if (j>29)
					{
						if (takeoffPlaneExists[i].y > 500 && takeoffPlaneExists[i].y < 650) {
							takeoffPlaneExists[i].y_shadow += 2;
							takeoffPlaneExists[i].y+=2; 

						}
						if (takeoffPlaneExists[i].y > 230 && takeoffPlaneExists[i].y < 330) {
							takeoffPlaneExists[i].y_shadow += 1;
							takeoffPlaneExists[i].y+=1; 
							
						}
						if (takeoffPlaneExists[i].y > 400 && takeoffPlaneExists[i].y < 500) {
							takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow -2);
							takeoffPlaneExists[i].y= (int) Math.round(takeoffPlaneExists[i].y -2);
							
						}
						else
						{
							takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow -1);
							takeoffPlaneExists[i].y= (int) Math.round(takeoffPlaneExists[i].y -1);

						}
						takeoffPlaneExists[i].x -=23;
						takeoffPlaneExists[i].x_shadow = (int) Math.round(takeoffPlaneExists[i].x_shadow - 23);
					}
					
				}
			}
			secondarySimulator.animatedCleanTruck(j);
			secondarySimulator.animateRepairTruck(j);
			repaint();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
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

	private void drawRescueTeam(Graphics g, RescueTeam rescue) 
	{
		g.drawImage(rescue.rescueteamImage, rescue.x, rescue.y, this);
		g.drawImage(rescue.flashlight, 175, 135, this);
		g.drawImage(rescue.flashlight, 175, 205, this);
		g.drawImage(rescue.flashlight, 175, 225, this);
		g.drawImage(rescue.flashlight, 175, 300, this);
		g.drawImage(rescue.flashlight, 175, 435, this);
		g.drawImage(rescue.flashlight, 175, 510, this);
		g.drawImage(rescue.flashlight, 175, 535, this);
		g.drawImage(rescue.flashlight, 175, 610, this);
		g.drawImage(rescue.flashlight, 600, 135, this);
		g.drawImage(rescue.flashlight, 600, 205, this);
		g.drawImage(rescue.flashlight, 600, 225, this);
		g.drawImage(rescue.flashlight, 600, 300, this);
		g.drawImage(rescue.flashlight, 600, 435, this);
		g.drawImage(rescue.flashlight, 600, 510, this);
		g.drawImage(rescue.flashlight, 600, 535, this);
		g.drawImage(rescue.flashlight, 600, 610, this);



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
					g.drawImage(commercialplane_0, plane.x+10, plane.y+8, this);
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_0,  plane.x+10, plane.y+8, this);

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_0,  plane.x+10, plane.y+8, this);

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
					g.drawImage(commercialplane_180, plane.x+10, plane.y+8, this);
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_180,  plane.x+10, plane.y+8, this);

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_180, plane.x+10, plane.y+8, this);


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

		if (plane.degree == 90) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_90,  plane.x+10, plane.y+8, this);


				} else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_90,  plane.x+10, plane.y+8, this);
					

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_90, plane.x+10, plane.y+8, this);

				}
			}

			else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_90, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_90, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_90, plane.x, plane.y, this);
				}
			}
		}

		if (plane.degree == 270) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_270, plane.x+10, plane.y+8, this);


				} else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_270,  plane.x+10, plane.y+8, this);
				}

				else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_270,  plane.x+10, plane.y+8, this);

				}

			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(shadow_commerical_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(commercialplane_270, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(shadow_private_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(privateplane_270, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(shadowcargo_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(cargoplane_270, plane.x, plane.y, this);
				}
			}
		}
		return;
	}

	public  void   paintComponent(Graphics g) {
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
		for (int i = 0; i < 4; i++) {
			takeoffAllowed[i] = false;
		}
	};

	private void initialFields() throws IOException {
		//airport = ImageIO.read(new File("img/airport.png"));
		airport = ImageIO.read(new File("img/airport_v2.png"));
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
		flashlight =ImageIO.read(new File("img/flashlight.png"));

		// Images for waiting to land

		shadowcargo_90 = ImageIO.read(new File("img/shadowcargo_90.png"));
		shadowcargo_270 = ImageIO.read(new File("img/shadowcargo_270.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270.png"));
		shadow_private_90 = ImageIO.read(new File("img/shadow_private_90.png"));
		shadow_private_270 = ImageIO.read(new File("img/shadow_private_270.png"));
		shadow_commerical_90 = ImageIO.read(new File("img/shadow_commerical_90.png"));
		shadow_commerical_270 = ImageIO.read(new File("img/shadow_commerical_270.png"));

		// Additional Images for takeoff
		cargoplane_90 = ImageIO.read(new File("img/cargoplane_90.png"));
		cargoplane_270 = ImageIO.read(new File("img/cargoplane_270.png"));
		privateplane_90 = ImageIO.read(new File("img/privateplane_90.png"));
		privateplane_270 = ImageIO.read(new File("img/privateplane_270.png"));
		commercialplane_90 = ImageIO.read(new File("img/commercialplane_90.png"));
		commercialplane_270 = ImageIO.read(new File("img/commercialplane_270.png"));

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
		controlPanelEvents.add(dirtyRunway, BorderLayout.CENTER);
		controlPanelEvents.add(MechanicalPanel, BorderLayout.SOUTH);

		controlPanelHeadAndEvents.add(headPanel, BorderLayout.NORTH);
		controlPanelHeadAndEvents.add(eventsPanel, BorderLayout.CENTER);
		controlPanelHeadAndEvents.add(controlPanelEvents, BorderLayout.SOUTH);

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

	public void setStartScenario() {
		startScenario = true;

	}

	public void animateGetToTakeOffSpot(int[] runway, Airplane[] planes) {
		Map<Airplane, Boolean> gotToTakeOffSpot = new HashMap<Airplane, Boolean>();
		Map<Airplane, Integer> runwayToPlane = new HashMap<Airplane, Integer>();

		for (int i = 0; i < planes.length; i++) {
			if (planes[i] != null) {
				gotToTakeOffSpot.put(planes[i], false);
				runwayToPlane.put(planes[i], runway[i]);
			}
		}
		for (Airplane plane : runwayToPlane.keySet()) {
			for (int j = 0; j < 16; j++) {
				int runwayPlane = runwayToPlane.get(plane);

				if (j == 15) {
					plane.degree = 0;
					break;
				}
				if (runwayPlane == 0) {
					plane.degree = 270;
					plane.y -= 10;
					plane.y_shadow -= 10;
				}
				if (runwayPlane == 1) {
					plane.degree = 270;
					plane.y -= 4;
					plane.y_shadow -= 4;
				}
				if (runwayPlane == 2) {
					plane.degree = 90;
					plane.y += 4;
					plane.y_shadow += 4;
				}
				if (runwayPlane == 3) {
					plane.degree = 90;
					plane.y += 11;
					plane.y_shadow += 11;
				}

				repaint();

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}