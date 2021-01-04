package SmartAirport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

//****************************************************************************************
//***            This class will contain methods related to the panel                  ***
//****************************************************************************************

public class AirportPanel {

	static Boolean AddlandingFirstPosition = false;
	static Boolean AddlandingSecondPosition = false;
	static Boolean AddtakeoffFirstPosition = false;
	static Boolean AddtakeoffSecondPosition = false;

	public AirportPanel() throws IOException {
	}

	// The function execute when the user requests to add aircrafts.
	public static void performAircraftsAddition(SmartAirport smartairport, String planesType) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || planesType.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText("Adding Aircrafts:\n");
		if (AddlandingFirstPosition == true) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getLandingString(0), planesType.toUpperCase());
		}
		if (AddlandingSecondPosition == true) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getLandingString(1), planesType.toUpperCase());
		}
		if (AddtakeoffFirstPosition == true) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getTakeOffString(0), planesType.toUpperCase());
		}
		if (AddtakeoffSecondPosition == true) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getTakeOffString(1), planesType.toUpperCase());
		}
	}

	// The function execute when the user requests to add mechanical problem to the smart airport.
	public static void performMechanicalProblem(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText("Perform Mechnical problem:\n");
		if (aircraft.equals("Takeoff Position 1")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getMechanicalProblemString(0), String.valueOf(true));
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getMechanicalProblemString(1), String.valueOf(true));
		}
	}
	
	// The function execute when the user requests to add a dirty runway to the smart airport.
	public static void performSlipperyRunway(SmartAirport smartairport, String runway) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || runway.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText("Perform Dirty Runway:\n");
		if (runway.equals("First Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(0), String.valueOf(true));
		} else if (runway.equals("Second Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(1), String.valueOf(true));
		} else if (runway.equals("Third Runway")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(2), String.valueOf(true));
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getSlipperyString(3), String.valueOf(true));
		}
	}
	
	// The function execute when the user requests to add an emergency landing to the smart airport.
	public static void performEmergencyLanding(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || SmartAirport.inManualScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inManualScenario = true;
		SmartAirport.outputArea.setText("Perform Emergency Landing:\n");
		if (aircraft.equals("Landing Position 1")) {
			SmartAirport.envMoves.put(AuxiliaryMethods.getEmergencyLandingString(0), String.valueOf(true));
		} else {
			SmartAirport.envMoves.put(AuxiliaryMethods.getEmergencyLandingString(1), String.valueOf(true));
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

		JToggleButton landingFirstPosToggleButton = new JToggleButton("Landing Position 1");
		JToggleButton landingSecPosToggleButton   = new JToggleButton("Landing Position 2");
		JToggleButton takeoffFirstPosToggleButton = new JToggleButton("Takeoff Position 1");
		JToggleButton takeoffSecPosToggleButton   = new JToggleButton("Takeoff Position 2");
		JPanel manualAddbuttonPanel = new JPanel();

		String[] planesType = { "-----", "Cargo", "Private", "Commercial" };
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

		String[] planes = { "-----", "Landing Position 1", "Landing Position 2" };
		JComboBox<String> emergencyCombo = new JComboBox<>(planes);

		JButton emergencyButton = new JButton("Go");
		emergencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				smartAirport.setStartScenario();
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

		String[] planes = { "-----", "Takeoff Position 1", "Takeoff Position 2" };
		JComboBox<String> MechanicalProblemCombo = new JComboBox<>(planes);

		JButton MechanicalProblemButton = new JButton("Go");
		MechanicalProblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (SmartAirport.startScenario)
					return;
				smartAirport.setStartScenario();
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
		SmartAirport.outputArea.setText("Start scenario: " + name + "\n");
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
}
