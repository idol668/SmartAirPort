package SmartAirport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

//****************************************************************************************
//***            This class will contain methods related to the panel                  ***
//****************************************************************************************

public class AirportPanel {

	static Boolean AddlandingFirstPosition  = false;
	static Boolean AddlandingSecondPosition = false;
	static Boolean AddtakeoffFirstPosition  = false;
	static Boolean AddtakeoffSecondPosition = false;

	public AirportPanel() throws IOException {
	}

	// The function execute when the user requests to add aircrafts.
	public static void performAircraftsAddition(SmartAirport smartairport, String userPlaneType) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || userPlaneType.equals("-----"))
			return;
		
		String planesType = userPlaneType.split(" ")[0].toUpperCase();
		if(AddlandingFirstPosition || AddlandingSecondPosition ||  AddtakeoffFirstPosition || AddtakeoffSecondPosition) {
			SmartAirport.inManualScenario = true;
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() +"Adding Aircrafts:\n");
			if (AddlandingFirstPosition) {
				SmartAirport.envMoves.put(AuxiliaryMethods.getLandingString(0), planesType);
			}
			if (AddlandingSecondPosition) {
				SmartAirport.envMoves.put(AuxiliaryMethods.getLandingString(1), planesType);
			}
			if (AddtakeoffFirstPosition) {
				SmartAirport.envMoves.put(AuxiliaryMethods.getTakeOffString(0), planesType);
			}
			if (AddtakeoffSecondPosition) {
				SmartAirport.envMoves.put(AuxiliaryMethods.getTakeOffString(1), planesType);
			}
		}
		else {
			SmartAirport.outputArea.setText("Missing selection\n");
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "Please Choose Landing or Taking off\n");
		}
	}

	// The function execute when the user requests to add mechanical problem to the smart airport.
	public static void performMechanicalProblem(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() +"Perform Mechnical problem:\n");
		if (aircraft.equals("Takeoff Platform 1")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getMechanicalProblemString(0), aircraft);
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getMechanicalProblemString(1), aircraft);
		}
	}
	
	// The function execute when the user requests to add a dirty runway to the smart airport.
	public static void performSlipperyRunway(SmartAirport smartairport, String runway) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || runway.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "Perform Dirty Runway:\n");
		if (runway.equals("First Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(0), runway);
		} else if (runway.equals("Second Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(1), runway);
		} else if (runway.equals("Third Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(2), runway);
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(3), runway);
		}
	}
	
	// The function execute when the user requests to add an emergency landing to the smart airport.
	public static void performEmergencyLanding(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() +"Perform Emergency Landing:\n");
		if (aircraft.equals("Landing Platform 1")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getEmergencyLandingString(0), aircraft);
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getEmergencyLandingString(1), aircraft);
		}
	}
	
	// This function create the header of the panel.
	public static JPanel createHeadLinePanel() {
		JPanel headPanel = new JPanel();
		JLabel headLineLabel = new JLabel("<html><span style='font-size:20px'>Smart Airport Simulator</span></html>");
		headPanel.add(headLineLabel);
		return headPanel;
	}
	
	// The function creates the part in the panel where planes can be added by the user.
	// The user can choose plane type (cargo, commercial or private), landing or taking off aircraft and the location planes.
	public static JPanel createEventsPanel(SmartAirport smartAirport) {
		JPanel eventsPanel = new JPanel(new BorderLayout());
		JPanel headLinePanel = new JPanel();
		JPanel eventsPanelToggelsLanding = new JPanel(new BorderLayout());
		JPanel eventsPanelToggelsTakeoff = new JPanel(new BorderLayout());
		JPanel eventsPanelToggels = new JPanel();
		JPanel eventsPanelToggelsAndLabel = new JPanel(new BorderLayout());

		JLabel headLineLabel = new JLabel("<html><span style='font-size:14px'>Add Landings & Takeoffs</span></html>");
		headLinePanel.add(headLineLabel);

		JToggleButton landingFirstPosToggleButton = new JToggleButton("Landing Platform 1");
		JToggleButton landingSecPosToggleButton   = new JToggleButton("Landing Platform 2");
		JToggleButton takeoffFirstPosToggleButton = new JToggleButton("Takeoff Platform 1");
		JToggleButton takeoffSecPosToggleButton   = new JToggleButton("Takeoff Platform 2");
		JPanel manualAddbuttonPanel = new JPanel();

		String[] planesType = { "-----", "Cargo (Brown)", "Private (Blue)", "Commercial (White)" };
		JComboBox<String> planesTypeCombo = new JComboBox<>(planesType);

		landingFirstPosToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				if (state == ItemEvent.SELECTED) {
					AddlandingFirstPosition = true;
				} else {
					AddlandingFirstPosition = false;
				}
			}
		});
		landingSecPosToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();

				if (state == ItemEvent.SELECTED) {
					AddlandingSecondPosition = true;
				} else {
					AddlandingSecondPosition = false;
				}
			}
		});

		takeoffFirstPosToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				if (state == ItemEvent.SELECTED) {
					AddtakeoffFirstPosition = true;
				} else {
					AddtakeoffFirstPosition = false;
				}
			}
		});

		takeoffSecPosToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				if (state == ItemEvent.SELECTED) {
					AddtakeoffSecondPosition = true;
				} else {
					AddtakeoffSecondPosition = false;
				}
			}
		});

		JButton manualAddButton = new JButton("Go");
		manualAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMenualControl();
				performAircraftsAddition(smartAirport, planesTypeCombo.getSelectedItem().toString());
			}
		});

		manualAddbuttonPanel.add(planesTypeCombo, BorderLayout.NORTH);
		manualAddbuttonPanel.add(manualAddButton, BorderLayout.SOUTH);

		eventsPanelToggelsLanding.add(landingFirstPosToggleButton, BorderLayout.NORTH);
		eventsPanelToggelsLanding.add(landingSecPosToggleButton, BorderLayout.SOUTH);

		eventsPanelToggelsTakeoff.add(takeoffFirstPosToggleButton, BorderLayout.NORTH);
		eventsPanelToggelsTakeoff.add(takeoffSecPosToggleButton, BorderLayout.SOUTH);

		eventsPanelToggels.add(eventsPanelToggelsLanding, BorderLayout.WEST);
		eventsPanelToggels.add(eventsPanelToggelsTakeoff, BorderLayout.EAST);

		eventsPanelToggelsAndLabel.add(headLinePanel, BorderLayout.NORTH);
		eventsPanelToggelsAndLabel.add(eventsPanelToggels, BorderLayout.CENTER);
		eventsPanelToggelsAndLabel.add(manualAddbuttonPanel, BorderLayout.SOUTH);

		eventsPanel.add(eventsPanelToggelsAndLabel, BorderLayout.NORTH);
		return eventsPanel;
	}
	
	// The function creates the part in the panel where the runway can be dirty by the user.
	static JPanel createDirtyRunwayPanel(SmartAirport smartAirport) {
		JPanel dirtyRunwayLinePanel = new JPanel();
		JPanel dirtyRunwayComboAndButton = new JPanel();
		JPanel eventsPaneldirtyRunway = new JPanel(new BorderLayout());

		String[] runWays = { "-----", "First Runway", "Second Runway", "Third Runway", "Fourth Runway" };
		JComboBox<String> dirtyRunwayCombo = new JComboBox<>(runWays);

		JButton dirtyRunwayButton = new JButton("Go");
		dirtyRunwayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				smartAirport.setStartScenario();
				setMenualControl();
				performSlipperyRunway(smartAirport, dirtyRunwayCombo.getSelectedItem().toString());
			}
		});

		JLabel dirtyRunwayLineLabel = new JLabel("<html><span style='font-size:14px'>Dirty a Runway</span></html>");
		dirtyRunwayLinePanel.add(dirtyRunwayLineLabel);

		dirtyRunwayComboAndButton.add(dirtyRunwayCombo, BorderLayout.WEST);
		dirtyRunwayComboAndButton.add(dirtyRunwayButton, BorderLayout.EAST);

		eventsPaneldirtyRunway.add(dirtyRunwayLinePanel, BorderLayout.NORTH);
		eventsPaneldirtyRunway.add(dirtyRunwayComboAndButton, BorderLayout.CENTER);
		return eventsPaneldirtyRunway;
	}

	//The function creates the part in the panel where emergency landing can be execute by the user.
	static JPanel createEmergencyLandingPanel(SmartAirport smartAirport) {
		JPanel emergencyHeadLine = new JPanel();
		JPanel emergencyComboAndButton = new JPanel();
		JPanel eventsPanelemergency = new JPanel(new BorderLayout());

		JLabel emergencyHeadLineLabel = new JLabel(
				"<html><span style='font-size:14px'>Emergency Landing</span></html>");
		emergencyHeadLine.add(emergencyHeadLineLabel);

		String[] planes = { "-----", "Landing Platform 1", "Landing Platform 2" };
		JComboBox<String> emergencyCombo = new JComboBox<>(planes);

		JButton emergencyButton = new JButton("Go");
		emergencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				smartAirport.setStartScenario();
				setMenualControl();
				performEmergencyLanding(smartAirport, emergencyCombo.getSelectedItem().toString());

			}
		});
		emergencyComboAndButton.add(emergencyCombo, BorderLayout.WEST);
		emergencyComboAndButton.add(emergencyButton, BorderLayout.EAST);

		eventsPanelemergency.add(emergencyHeadLine, BorderLayout.NORTH);
		eventsPanelemergency.add(emergencyComboAndButton, BorderLayout.CENTER);

		return eventsPanelemergency;
	}

	//The function creates the part in the panel where mechanical problem can be execute by the user.
	static JPanel createMechanicalProblemPanel(SmartAirport smartAirport) {

		JPanel MechanicalProblemHeadLine = new JPanel();
		JPanel MechanicalProblemComboAndButton = new JPanel();

		JPanel eventsPanelMechanicalProblem = new JPanel(new BorderLayout());

		JLabel MechanicalProblemHeadLineLabel = new JLabel(
				"<html><span style='font-size:14px'>Mechanical Problem</span></html>");
		MechanicalProblemHeadLine.add(MechanicalProblemHeadLineLabel);

		String[] planes = { "-----", "Takeoff Platform 1", "Takeoff Platform 2" };
		JComboBox<String> MechanicalProblemCombo = new JComboBox<>(planes);

		JButton MechanicalProblemButton = new JButton("Go");
		MechanicalProblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				smartAirport.setStartScenario();
				setMenualControl();
				performMechanicalProblem(smartAirport, MechanicalProblemCombo.getSelectedItem().toString());
			}
		});

		MechanicalProblemComboAndButton.add(MechanicalProblemCombo, BorderLayout.WEST);
		MechanicalProblemComboAndButton.add(MechanicalProblemButton, BorderLayout.EAST);
		eventsPanelMechanicalProblem.add(MechanicalProblemHeadLine, BorderLayout.NORTH);
		eventsPanelMechanicalProblem.add(MechanicalProblemComboAndButton, BorderLayout.CENTER);
		return eventsPanelMechanicalProblem;
	}
	
	//The function creates the part in the panel where the user can choose to run scenario demo. 
	static JPanel createScenariosPanel(SmartAirport smartAirport) {
		JPanel scenariosHeadLine = new JPanel();
		JPanel scenariosComboAndButton = new JPanel();

		JPanel eventsPanelScenarios = new JPanel(new BorderLayout());

		JLabel scenariosHeadLineLabel = new JLabel("<html><span style='font-size:14px'>Scenarios</span></html>");
		scenariosHeadLine.add(scenariosHeadLineLabel);

		String[] scenarios = { "-----", "rush hour", "slippery slope", "being a mechanic is hard", "scared of flying" };
		JComboBox<String> scenariosCombo = new JComboBox<>(scenarios);

		JButton scenariosButton = new JButton("Go");
		scenariosButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				setMenualControl();
				smartAirport.setStartScenario();
				setScenario(smartAirport, scenariosCombo.getSelectedItem().toString());
			}
		});

		scenariosComboAndButton.add(scenariosCombo, BorderLayout.WEST);
		scenariosComboAndButton.add(scenariosButton, BorderLayout.EAST);

		eventsPanelScenarios.add(scenariosHeadLine, BorderLayout.NORTH);
		eventsPanelScenarios.add(scenariosComboAndButton, BorderLayout.CENTER);

		return eventsPanelScenarios;
	}
	
	// This function checks which scenario is selected by the user and defines it at the smart airport.
	protected static void setScenario(SmartAirport smartAirport, String name) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario)
			return;
		SmartAirport.wait = true;
		SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() +"Start scenario: " + name + "\n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		switch (name) {
		case "rush hour":
			setScenarioAndCounter("rush hour", 6);
			break;
		case "slippery slope":
			setScenarioAndCounter("slippery slope", 6);
			break;
		case "being a mechanic is hard":
			setScenarioAndCounter("being a mechanic is hard", 6);
			break;
		case "scared of flying":
			setScenarioAndCounter("scared of flying", 4);
			break;
		}
		SmartAirport.inScenario = true;
		SmartAirport.wait = false;
	}
	
	// The function set the scenario in smart airport.
	private static void setScenarioAndCounter(String scenrioName, int scenarioNumOfSteps) {
		SmartAirport.scenario = scenrioName;
		SmartAirport.scenarioCounter = scenarioNumOfSteps;
	}
	// The function set the control board to appears in manual control.
	private static void setMenualControl() {
		SmartAirport.outputArea.setText("-----------------Manual--Control-----------------\n");
		SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() +"---------------------------------------------------------\n");
	}
	// The function set the control board to appears in automatic control - the take off platform.
	public static void setControlTowerBoardDepartures() {
		if (!SmartAirport.inScenario && !SmartAirport.inManualScenario) {
			SmartAirport.outputArea.setText("-------------Takeoff--Platform---------------\n");
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "Time     Gate        PlaneType      Color\n");
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "-----------------------------------------------------\n");
		}
	}
	// The function set the control board to appears in automatic control - the landing platform.
	public static void setControlTowerBoardArrivel() {
		if (!SmartAirport.inScenario && !SmartAirport.inManualScenario) {
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "-------------Landing--Platform---------------\n");
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "Time     Gate        PlaneType      Color\n");
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + "-----------------------------------------------------\n");
		}
	}
	// The function set the control board to appears in automatic control - add a plane details to the board.
	public static void addPlaneToControlTowerBoard(int gate, Airplane plane) {
		if (!SmartAirport.inScenario && !SmartAirport.inManualScenario) {
			DateFormat timeFormat = new SimpleDateFormat("HH:mm");
			timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
			String curTime = String.format("%-10s",timeFormat.format(new Date()));
			String gatestr;
			if(plane.type.equals("COMMERCIAL")) {
				gatestr = String.format("%-12s",String.valueOf(gate));
			}else {
				gatestr = String.format("%-15s",String.valueOf(gate));
			}
			String plane_type = String.format("%-15s",plane.type.toString().toLowerCase());
			String color = String.format("%5s",plane.color);
			SmartAirport.outputArea.setText(SmartAirport.outputArea.getText() + curTime + gatestr + plane_type + color +"\n");
		}
	}
	
	// set the background color,text color and font of the control tower board.
	public static void controlPanelDesign() {
		SmartAirport.outputArea.setBackground(Color.black);
		SmartAirport.outputArea.setForeground(Color.white);
		Font font = new Font("Calibri", Font.BOLD, 14);
		SmartAirport.outputArea.setFont(font);
	}
}
