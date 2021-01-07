package SmartAirport;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.jit.BasicJitController;
import tau.smlab.syntech.controller.executor.ControllerExecutor;


//*********************************************************************************************
//***              This is our main class and deals with the most important functionality:  ***
//***              Landing and take offs and emergency landing                              ***
//***   Also you can find the drawing function that are used by the thread animation here   ***
//*********************************************************************************************

public class SmartAirport extends JPanel {

	private static final long serialVersionUID = 1L;
	static ControllerExecutor executor;
	
	//****************************************************//
	//***          Initialize variables                ***//
	//****************************************************//
	
	// Landing & take off
	static boolean[] aircraftForLanding  = new boolean[2];
	static boolean[] aircraftForTakeoff  = new boolean[2];
	static boolean[] takeoffAllowed      = new boolean[4];
	static boolean[] landingAllowed      = new boolean[4];
	static Airplane[] takeoffPlaneExists = new Airplane[2];
	static Airplane[] landingPlaneExists = new Airplane[2];
	static int[] runwaysLanding          = new int[2];
	static Airplane[] planesLanding      = new Airplane[2];
	static Airplane[] planesTakeoff = new Airplane[2];
	static int[] runwaysTakeOff = new int[2];

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
	static JTextArea outputArea = new JTextArea("", 10, 27);
	
	static boolean run = true;
	static boolean finished = false;
	
	// This is where everything happens- the thread animation is activated here
	// We use the thread to draw and simulate the airtport condition using the controller system and environment inputs 
	public SmartAirport() throws IOException {
		AirportImages.initialFields(); // initialize the images we gonna use in the airport
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
					// Creating planes according To environment variables
					AuxiliaryMethods.creatingPlanesAccordingToExecutor(envValues);

					Map<String, String> sysValues = executor.getCurrOutputs();
					AuxiliaryMethods.getSysInputs(executor);
					
					// Create cleaning cars according to cleaning sensor
					for(int runwayline=0; runwayline<4; runwayline++) {
						AuxiliaryMethods.createCleaningCars(runwayline ,cleaningSensors[runwayline]);
					}
					
					System.out.println("System Values:"+sysValues.toString());
					System.out.println("Env Values:"+envValues.toString());
					repaint();
					animateSetInWaitingTakeOffSpot();
					animateEmergencyLanding();
					animateLandingAndTakeoff();
					while (wait) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					ScenarioFunctions.handleEndOfScenario();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					// Here we update the inputs of the system and environment in the controller and the panel
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

	//*********************************************************************************************
	//***            Take off ,Landing and Emergency landing functions                          ***
	//*********************************************************************************************
	// One of our main functions - this function deals with the animation of the take offs and landing that occur in the airport
	// Also we call here some of our secondary animation functions here like the slippery runway and mechanical problem
	public void animateLandingAndTakeoff() {
		SecondaryAnimation secondarySimulator = new SecondaryAnimation(cleaningSensors, cleaningCars, stillCleaning,
				slipperyRunway, repairTruck, mechanicalProblem);
		planesTakeoff = new Airplane[2];
		runwaysTakeOff = new int[2];
		runwaysLanding = new int[2];
		planesLanding = new Airplane[2];
		setLandingAndTakeOffRunwaysStep1(secondarySimulator,planesTakeoff,runwaysTakeOff);
		/*
		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && !emergencyLanding[i]) {
				//here we move our planes from the landing waiting area to the correct runway they gonna use in their landings
				SecondaryAnimation.animateGetPlaneToLandingSpot(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
			}
			//Here we set the correct runway for the take off
			//Note: in case of two possible runways we randomly choose one
			if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == false) {

				if (takeoffAllowed[2 * i] == true && takeoffAllowed[2 * i + 1] == true) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						planesTakeoff[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i + 1;
					} else {
						planesTakeoff[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i;
					}
				} else if (takeoffAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					planesTakeoff[i] = takeoffPlaneExists[i];
					runwaysTakeOff[i] = 2 * i;

				} else if (takeoffAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					planesTakeoff[i] = takeoffPlaneExists[i];
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					runwaysTakeOff[i] = 2 * i + 1;
				} else {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
				}
			} else if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == true) {
				takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
			}

		}*/

		boolean planeOrTruckMooving = false;
		planeOrTruckMooving=checkIfMovementInAirportStep2 (planeOrTruckMooving);
		/*
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
		}*/

		if (!planeOrTruckMooving) {
			return;
		}
		// This function moves the planes from the take off waiting area to the correct take off lane
		animateGetToTakeOffSpot(runwaysTakeOff, planesTakeoff);
		animateGetToLandingSpot(runwaysLanding, planesLanding); 
		for (int j = 0; j < 55; j++) {
			int takeOffPlaneSpeed=20;
			int takeOffPlaneSpeedY=1;

			int landinShadowgPlaneSpeed =18;
			int planeSpeedY=1;
			int landinPlaneSpeed =20;
			for (int i = 0; i < 2; i++) {

				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof && j >= 9) {
					if (j < 21) {
						landingPlaneExists[i].x_shadow += landinShadowgPlaneSpeed;
						landingPlaneExists[i].x += landinPlaneSpeed;
						if (j>15 && j<21){
							landingPlaneExists[i].y -=planeSpeedY; 
						}	
					}

					else if (j == 21) {
						landingPlaneExists[i].ground = true;
					} else {
						if (j < 34) {
							landinShadowgPlaneSpeed=22;
							landingPlaneExists[i].x += landinPlaneSpeed;
							landingPlaneExists[i].x_shadow += landinShadowgPlaneSpeed;
						} else {
							if (j > 33 && j < 40) {
								if (landingPlaneExists[i].y > 165 && landingPlaneExists[i].y < 400) {
									landingPlaneExists[i].y -= planeSpeedY;
								}
								if (landingPlaneExists[i].y > 400 && landingPlaneExists[i].y < 500) {
									planeSpeedY=2;
									landingPlaneExists[i].y += planeSpeedY;
								}
								landinPlaneSpeed=15;
								landingPlaneExists[i].x += landinPlaneSpeed;
							} else {
								if (landingPlaneExists[i].y > 165 && landingPlaneExists[i].y < 400) {
									planeSpeedY=1;
									landingPlaneExists[i].y -= planeSpeedY;
								}
								if (landingPlaneExists[i].y > 400 && landingPlaneExists[i].y < 500) {
									planeSpeedY=2;
									landingPlaneExists[i].y += planeSpeedY;
								}
								landinPlaneSpeed=10;
								landingPlaneExists[i].x += landinPlaneSpeed;
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
						takeoffPlaneExists[i].x -= takeOffPlaneSpeed;
						takeoffPlaneExists[i].x_shadow = (int) Math.round(takeoffPlaneExists[i].x_shadow - takeOffPlaneSpeed);
						takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow - takeOffPlaneSpeedY);
					}
					
					if (j == 29) {
						takeoffPlaneExists[i].ground = false;
					}
					if (j>29)
					{
						if (takeoffPlaneExists[i].y > 500 && takeoffPlaneExists[i].y < 650) {
							takeOffPlaneSpeedY=3;
							takeoffPlaneExists[i].y_shadow += takeOffPlaneSpeedY;
							takeoffPlaneExists[i].y+=takeOffPlaneSpeedY; 

						}
						if (takeoffPlaneExists[i].y > 230 && takeoffPlaneExists[i].y < 330) {
							takeOffPlaneSpeedY=1;
							takeoffPlaneExists[i].y_shadow += takeOffPlaneSpeedY;
							takeoffPlaneExists[i].y+=takeOffPlaneSpeedY; 
							
						}
						if (takeoffPlaneExists[i].y > 400 && takeoffPlaneExists[i].y < 500) {
							takeOffPlaneSpeedY=2;
							takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow -takeOffPlaneSpeedY);
							takeoffPlaneExists[i].y= (int) Math.round(takeoffPlaneExists[i].y -takeOffPlaneSpeedY);
							
						}
						else
						{
							takeOffPlaneSpeedY=1;
							takeoffPlaneExists[i].y_shadow = (int) Math.round(takeoffPlaneExists[i].y_shadow -takeOffPlaneSpeedY);
							takeoffPlaneExists[i].y= (int) Math.round(takeoffPlaneExists[i].y -takeOffPlaneSpeedY);

						}
						takeOffPlaneSpeed=23;
						takeoffPlaneExists[i].x -=takeOffPlaneSpeed;
						takeoffPlaneExists[i].x_shadow = (int) Math.round(takeoffPlaneExists[i].x_shadow - takeOffPlaneSpeed);
					}
					
				}
			}
			//Here we deal with the mechanical problem and the slippery runway issue
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
	//The Function Sets the planes runways for take off or landing
	public void setLandingAndTakeOffRunwaysStep1(SecondaryAnimation secondarySimulator,Airplane[] planesTakeoff,int[] runwaysTakeOff ) {
		for (int i = 0; i < 2; i++) {
			if (landingPlaneExists[i] != null && !emergencyLanding[i]) {
				//here we move our planes from the landing waiting area to the correct runway they gonna use in their landings
				SecondaryAnimation.animateGetPlaneToLandingSpot(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
			}
			//Here we set the correct runway for the take off
			//Note: in case of two possible runways we randomly choose one
			if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == false) {

				if (takeoffAllowed[2 * i] == true && takeoffAllowed[2 * i + 1] == true) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					Random rand = new Random();
					if (rand.nextBoolean() == true) {
						planesTakeoff[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i + 1;
					} else {
						planesTakeoff[i] = takeoffPlaneExists[i];
						runwaysTakeOff[i] = 2 * i;
					}
				} else if (takeoffAllowed[2 * i] == true && !slipperyRunway[2 * i]) {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					planesTakeoff[i] = takeoffPlaneExists[i];
					runwaysTakeOff[i] = 2 * i;

				} else if (takeoffAllowed[2 * i + 1] == true && !slipperyRunway[2 * i + 1]) {
					planesTakeoff[i] = takeoffPlaneExists[i];
					takeoffPlaneExists[i].EnterLandingOrTakeoof = true;
					runwaysTakeOff[i] = 2 * i + 1;
				} else {
					takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
				}
			} else if (takeoffPlaneExists[i] != null && mechanicalProblem[i] == true) {
				takeoffPlaneExists[i].EnterLandingOrTakeoof = false;
			}
		}
	}
	//The function checks if there's any movement that going to occur in the airport 
	public boolean checkIfMovementInAirportStep2 (boolean planeOrTruckMooving) {	
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
		return planeOrTruckMooving;
	}
	
	// Animate enter of the take off planes
	public void animateSetInWaitingTakeOffSpot() {
		int planeSpeed=10;
		int waitingTakeOffPosition= 650;
		for (int j=0;j<2;j++) {
			if(takeoffPlaneExists[j]!=null) {
				while(takeoffPlaneExists[j].x > waitingTakeOffPosition){
					takeoffPlaneExists[j].x-=planeSpeed;
					takeoffPlaneExists[j].x_shadow-=planeSpeed;
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
	
	// Animate Emergency Landing Step 1: the plane landing in the runway and emergency flash light is on.
	public void animateEmergencyLandingLandingStage(int i) {
		int plane_landing_speed         = 15;
		int plane_arrived_to_the_ground = 340;
		int plane_landing_spot          = 390;
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
	}
	
	// Animate Emergency Landing Step 2: rescue team arrives to the plane position
	// The plane position is determined by the runway used by the plane while performing the emergency landing.
	public void animateEmergencyLandingRescueTeamArrivesStage(int i) {
		int rescue_team_speed         = 12;
		boolean is_rescueteam_arrives = false;
		int planePosition = AuxiliaryMethods.planePositionByRunwayNumber(landingPlaneExists[i].line);
		while(!is_rescueteam_arrives) {
			if(rescueTeams[i].y > planePosition) {
				rescueTeams[i].y -= rescue_team_speed;
			}
			else {
				is_rescueteam_arrives = true;
				rescueTeams[i].rescueteamImage = AirportImages.rescueteam_r;
			}
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Animate Emergency Landing Step 3: rescue team returns back to the MDA building and plane drive to the arrival spot.
	public void animateEmergencyLandingOverStage(int i) {
		int rescue_team_turn               = 10;
		int rescue_team_speed              = 12;
		int plane_driving_speed            = 12;
		int rescue_team_out_of_sight_y_pos = 660;
		int plane_out_of_sight_x_pos       = 800;
		boolean landingPlaneOutOfSight     = false;
		
		//rescue team turn back to the MDA building.
		rescueTeams[i].rescueteamImage = AirportImages.rescueteam_down;	
		rescueTeams[i].x += rescue_team_turn;
		repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(rescueTeams[i] != null || !landingPlaneOutOfSight) {
			if(landingPlaneExists[i]!=null) {
				if(landingPlaneExists[i].x < plane_out_of_sight_x_pos) {
					landingPlaneExists[i].movingPlaneAndShadow(plane_driving_speed);
				}
				else {
					landingPlaneOutOfSight = true;
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
	
	// Animate Emergency Landing : union all stages of the animation.
	public void animateEmergencyLanding() {
		runwaysLanding = new int[2];
		planesLanding = new Airplane[2];
		for (int i = 0; i < 2; i++) {
			if (emergencyLanding[i] && landingPlaneExists[i] != null) {
				SecondaryAnimation.animateGetPlaneToLandingSpot(i,landingAllowed[2 * i], landingAllowed[2 * i + 1], landingPlaneExists[i]);
				if (landingPlaneExists[i] != null && landingPlaneExists[i].EnterLandingOrTakeoof && rescueTeams[i] != null) {
					animateGetToLandingSpot(runwaysLanding,planesLanding);
					animateEmergencyLandingLandingStage(i);
					landingPlaneExists[i].EnterLandingOrTakeoof = false;
					
					// keep the flash light on
					if(rescueTeams[i].flashlight == null) {
						rescueTeams[i].TurnFlashLight();
					}
					animateEmergencyLandingRescueTeamArrivesStage(i);
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
					
					animateEmergencyLandingOverStage(i);
				}
			}
			rescueTeams[i] = null;
		}
	}

	//*********************************************************************************************
	//***                              Drawing Functions                                        ***
	//*********************************************************************************************
	// This function draws the rescue team and the flashlights that are used during the  emergency landing
	private void drawRescueTeam(Graphics g, RescueTeam rescue) {
		g.drawImage(rescue.rescueteamImage, rescue.x, rescue.y, this);
		int waitingArea1X =175;
		int waitingArea2X=600;
		g.drawImage(rescue.flashlight, waitingArea1X, 135, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 205, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 225, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 305, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 435, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 510, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 535, this);
		g.drawImage(rescue.flashlight, waitingArea1X, 610, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 135, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 205, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 225, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 305, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 435, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 510, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 535, this);
		g.drawImage(rescue.flashlight, waitingArea2X, 610, this);
	}

	private void drawTruck(Graphics g, RepairTruck truck) {
		if (truck.line == 0) {
			g.drawImage(AirportImages.repairman, truck.man_x + 40, truck.man_y + 40, this);
		} else {
			g.drawImage(AirportImages.repairman, truck.man_x + 40, truck.man_y + 15, this);
		}
		g.drawImage(truck.truckImage, truck.x, truck.y, this);
	}
	// This function draws all our planes and their shadows depending on their type and angles
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
	
	// This function draws the oil on the lane when we are in slippery runway sitatuion 
		private void drawOil(Graphics g, int i) {
			int lane_number =0;
			int oilY=175;
			int oilX =320;
			if (i == lane_number) {
				g.drawImage(AirportImages.oilAndDirt, oilX, oilY, this);
			}
			lane_number+=1;
			oilY+=100;
			if (i == lane_number) {
				g.drawImage(AirportImages.oilAndDirt, oilX, oilY, this);
			}
			lane_number+=1;
			oilY=475;
			if (i == lane_number) {
				g.drawImage(AirportImages.oilAndDirt, oilX, oilY, this);// 1
			}
			lane_number+=1;
			oilY+=100;
			if (i == lane_number) {
				g.drawImage(AirportImages.oilAndDirt, oilX, oilY, this);// 1
			}
		}
	//This function draws the cleaning team that handles the slippery runway situation 
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
	
//This method calls for all the paint methods in the simulator
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
//Stimulates the controller in the beginning of the run by getting the planes values 
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

// This functions creates all the Basic elements in the panel which is used in the simulator 
	public static JSplitPane createMainPanel(SmartAirport smartAirport) {
		JPanel controlPanel = AirportPanel.createControlPanel(smartAirport);
		JSplitPane splitPanel = new JSplitPane(SwingConstants.VERTICAL, smartAirport, controlPanel);
		splitPanel.setDividerLocation(800);
		return splitPanel;
	}

	public static void main(String[] args) throws IOException {
		JFrame SmartAirport = new JFrame("Airprot Simulator");
		SmartAirport smartAirport = new SmartAirport();
		SmartAirport.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		SmartAirport.setSize(1150, 850);
		SmartAirport.setContentPane(createMainPanel(smartAirport));
		SmartAirport.setVisible(true);
	}
	public void setStartScenario() {
		startScenario = true;
	}
	//This function is used to et the planes that are waiting in the take off waiting area to their correct lane 
	public void animateGetToTakeOffSpot(int[] runway, Airplane[] planes) {
		Map<Airplane, Boolean> gotToTakeOffSpot = new HashMap<Airplane, Boolean>();
		Map<Airplane, Integer> runwayToPlane = new HashMap<Airplane, Integer>();
		for (int i = 0; i < planes.length; i++) {
			if (planes[i] != null) {
				gotToTakeOffSpot.put(planes[i], false);
				runwayToPlane.put(planes[i], runway[i]);
			}
		}
		int planeSpeedY=10;
		for (Airplane plane : runwayToPlane.keySet()) {
			for (int j = 0; j < 16; j++) {
				int runwayPlane = runwayToPlane.get(plane);

				if (j == 15) {
					plane.degree = 0;
					break;
				}
				if (runwayPlane == 0) {
					plane.degree = 270;
					plane.y -= planeSpeedY;
					plane.y_shadow -= planeSpeedY;
				}
				if (runwayPlane == 1) {
					planeSpeedY=4;
					plane.degree = 270;
					plane.y -= planeSpeedY;
					plane.y_shadow -= planeSpeedY;
				}
				if (runwayPlane == 2) {
					planeSpeedY=4;
					plane.degree = 90;
					plane.y += planeSpeedY;
					plane.y_shadow += planeSpeedY;
				}
				if (runwayPlane == 3) {
					planeSpeedY=11;
					plane.degree = 90;
					plane.y += planeSpeedY;
					plane.y_shadow += planeSpeedY;
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
	//This function sets the landing planes into there landing lane
	public void animateGetToLandingSpot(int[] runway, Airplane[] planes) {
		Map<Airplane, Boolean> gotToTakeOffSpot = new HashMap<Airplane, Boolean>();
		Map<Airplane, Integer> runwayToPlane = new HashMap<Airplane, Integer>();
		for (int i = 0; i < planes.length; i++) {
			if (planes[i] != null) {
				gotToTakeOffSpot.put(planes[i], false);
				runwayToPlane.put(planes[i], runway[i]);
			}
		}
		int planeSpeedY=2;
		for (Airplane plane : runwayToPlane.keySet()) {
			for (int j = 0; j < 10; j++) {
				int runwayPlane = runwayToPlane.get(plane);
				if (j == 9) {
					plane.degree = 180;
					break;
				}
				if (runwayPlane == 0) {
					plane.degree = 270;
					plane.y -= planeSpeedY;
					plane.y_shadow -= planeSpeedY;
				}
				if (runwayPlane == 1) {
					planeSpeedY=7;
					plane.degree = 90;
					plane.y += planeSpeedY;
					plane.y_shadow += planeSpeedY;
				}
				if (runwayPlane == 2) {
					planeSpeedY=3;
					plane.degree = 270;
					plane.y -= planeSpeedY;
					plane.y_shadow -= planeSpeedY;
				}
				if (runwayPlane == 3) {
					planeSpeedY=6;
					plane.degree = 90;
					plane.y += planeSpeedY;
					plane.y_shadow += planeSpeedY;
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
