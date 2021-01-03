package SmartAirport;

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

import SmartAirport.AuxiliaryMethods;
import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.controller.executor.ControllerExecutor;

public class SmartAirport extends JPanel {

	private static final long serialVersionUID = 1L;
	static ControllerExecutor executor;
	
	//****************************************************//
	//***          Initialize variables                ***//
	//****************************************************//
	
	// Landing & take off
	static boolean[] aircraftForLanding = new boolean[2];
	static boolean[] aircraftForTakeoff = new boolean[2];
	static boolean[] takeoffAllowed = new boolean[4];
	static boolean[] landingAllowed = new boolean[4];
	static Airplane[] takeoffPlaneExists = new Airplane[2];
	static Airplane[] landingPlaneExists = new Airplane[2];

	// Emergency Landing
	static boolean[] emergencyLanding = new boolean[2];
	static RescueTeam[] rescueTeams = new RescueTeam[2];
	
	// Mechanical Problem
	static boolean[] mechanicalProblem = new boolean[2];
	static RepairTruck[] repairTruck = new RepairTruck[2];
	
	// Slippery Runway
	static boolean[] slipperyRunway = new boolean[4];
	static boolean[] cleaningSensors = new boolean[4];
	static boolean[] stillCleaning = new boolean[4];
	static CleaningTruck[] cleaningCars = new CleaningTruck[4];

	// Panel variables
	static boolean inScenario = false;
	static boolean inManualScenario = false;
	static boolean startScenario = false;
	static boolean wait = false;
	static String scenario = "none";
	static int scenarioCounter = -1;
	static Map<String, String> envMoves = new HashMap<>();
	static JTextArea outputArea = new JTextArea("Here will be the output for scenarios and events \n", 0, 20);
	
	static boolean run = true;
	static boolean finished = false;

	public SmartAirport() throws IOException {
		AirportImages.initialFields();
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
					
					// Create cleaning cars according to cleaning sensor
					for(int runwayline=0; runwayline<4; runwayline++) {
						AuxiliaryMethods.createCleaningCars(runwayline ,cleaningSensors[runwayline]);
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
		int rescue_team_speed = 12;
		int plane_landing_speed = 15;
		int plane_driving_speed = 12;
		int plane_landing_spot = 390;
		int plane_arrived_to_the_ground = 340;
		int rescue_team_out_of_sight_y_pos = 660;
		int plane_out_of_sight_x_pos = 750;
		
		for (int i = 0; i < 2; i++) {
			if (emergencyLanding[i] && landingPlaneExists[i] != null) {
				SecondaryAnimation.animateGetPlaneToLandingSpot(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
				landingPlaneExists[i].EnterLandingOrTakeoof = false;
				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof && rescueTeams[i] != null) {
					// plane landing and emergency flash light is on.
					boolean planeWaitingForRescueTeam = false;
					while(!planeWaitingForRescueTeam) {
						rescueTeams[i].TurnFlashLight();
						if(landingPlaneExists[i].x < plane_landing_spot) {
							landingPlaneExists[i].movingPlaneAndShadow(plane_landing_speed);
							if(landingPlaneExists[i].x > plane_arrived_to_the_ground) {
								landingPlaneExists[i].ground = true;
							}
						}
						else {
							planeWaitingForRescueTeam = true;
						}
						repaint();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// keep the flash light on
					if(rescueTeams[i].flashlight == null) {
						rescueTeams[i].TurnFlashLight();
					}

					// rescue team moves to the plane position
					// The plane position is determined by the runway used by the plane while performing the emergency landing
					boolean rescueTeamArrivedToThePlane = false;
					int planePosition = AuxiliaryMethods.planePositionByRunwayNumber(landingPlaneExists[i].line);
					while(!rescueTeamArrivedToThePlane) {
						if(rescueTeams[i].y > planePosition) {
							rescueTeams[i].y -= rescue_team_speed;
						}
						else {
							rescueTeamArrivedToThePlane = true;
							rescueTeams[i].rescueteamImage = AirportImages.rescueteam_r;
						}
						repaint();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					// the rescue team arrived to the plane and handle the situation
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// rescue team return back to the control tower and plane drive to the arrival spot.
					rescueTeams[i].rescueteamImage = AirportImages.rescueteam_down;
					while(rescueTeams[i] != null || landingPlaneExists[i]!=null) {
						if(landingPlaneExists[i]!=null) {
							if(landingPlaneExists[i].x < plane_out_of_sight_x_pos) {
								landingPlaneExists[i].movingPlaneAndShadow(plane_driving_speed);
							}
							else {
								landingPlaneExists[i] = null;
							}
						}
						if(rescueTeams[i]!=null) {
							if(rescueTeams[i].y < rescue_team_out_of_sight_y_pos) {
								rescueTeams[i].y += rescue_team_speed;
							}
							else {
								rescueTeams[i] = null;
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
				SecondaryAnimation.animateGetPlaneToLandingSpot(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
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
			g.drawImage(AirportImages.repairman, truck.man_x + 40, truck.man_y + 40, this);
		} else {
			g.drawImage(AirportImages.repairman, truck.man_x + 40, truck.man_y + 15, this);
		}
		g.drawImage(truck.truckImage, truck.x, truck.y, this);
	}

	private void drawPlane(Graphics g, Airplane plane) {
		if (plane.degree == 0) {
			if (plane.ground == false) {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_0, plane.x+10, plane.y+8, this);
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_0,  plane.x+10, plane.y+8, this);

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_0,  plane.x+10, plane.y+8, this);

				}
			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_0, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_0, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_0, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_0, plane.x, plane.y, this);

				}
			}
		}
		if (plane.degree == 180) {
			if (plane.ground == false) {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_180, plane.x+10, plane.y+8, this);
				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_180,  plane.x+10, plane.y+8, this);

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_180, plane.x+10, plane.y+8, this);


				}
			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_180, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_180, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_180, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_180, plane.x, plane.y, this);
				}
			}
		}

		if (plane.degree == 90) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_90,  plane.x+10, plane.y+8, this);


				} else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_90,  plane.x+10, plane.y+8, this);
					

				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_90, plane.x+10, plane.y+8, this);

				}
			}

			else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_90, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_90, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_90, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_90, plane.x, plane.y, this);
				}
			}
		}

		if (plane.degree == 270) {
			if (plane.ground == false) {
				if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_270, plane.x+10, plane.y+8, this);


				} else if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_270,  plane.x+10, plane.y+8, this);
				}

				else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_270,  plane.x+10, plane.y+8, this);

				}

			} else {
				if (plane.type.equals("COMMERCIAL")) {
					g.drawImage(AirportImages.shadow_commerical_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.commercialplane_270, plane.x, plane.y, this);

				} else if (plane.type.equals("PRIVATE")) {
					g.drawImage(AirportImages.shadow_private_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.privateplane_270, plane.x, plane.y, this);
				} else if (plane.type.equals("CARGO")) {
					g.drawImage(AirportImages.shadowcargo_270, plane.x_shadow, plane.y_shadow, this);
					g.drawImage(AirportImages.cargoplane_270, plane.x, plane.y, this);
				}
			}
		}
		return;
	}

	public  void   paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(AirportImages.airport, 0, 0, this);

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
			g.drawImage(AirportImages.oilAndDirt, 320, 175, this);
		}

		if (i == 1) {
			g.drawImage(AirportImages.oilAndDirt, 320, 275, this);
		}

		if (i == 2) {
			g.drawImage(AirportImages.oilAndDirt, 320, 475, this);// 1
		}

		if (i == 3) {
			g.drawImage(AirportImages.oilAndDirt, 320, 575, this);// 1
		}
	}

	private void drawCleaningTruck(Graphics g, CleaningTruck cleanCar) {
		if (cleanCar.degree == 0) {
			g.drawImage(AirportImages.cleaningCar_0, cleanCar.x, cleanCar.y, this);
		}

		if (cleanCar.degree == 180) {
			g.drawImage(AirportImages.cleaningCar_180, cleanCar.x, cleanCar.y, this);
		}
		if (cleanCar.degree == 90) {
			g.drawImage(AirportImages.cleaningCar_90, cleanCar.x, cleanCar.y, this);
		}
		if (cleanCar.degree == 270) {
			g.drawImage(AirportImages.cleaningCar_270, cleanCar.x, cleanCar.y, this);
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