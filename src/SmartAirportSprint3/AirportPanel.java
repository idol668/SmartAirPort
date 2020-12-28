package SmartAirportSprint3;

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



public class AirportPanel  {

	static Boolean AddlandingNorth = false;
	static Boolean AddlandingSouth = false;
	static Boolean AddtakeoffNorth = false;
	static Boolean AddtakeoffSouth = false;

	public AirportPanel() throws IOException {
		
	}

	public static void performAircraftsAddition(SmartAirport smartairport, String planesType) {
		if (SmartAirport.inScenario || planesType.equals("-----"))
			return;
		SmartAirport.inScenario = true;
		SmartAirport.outputArea.setText("Adding Aircrafts:\n");
		if (AddlandingNorth == true) {
			SmartAirport.envMoves.put("landingAircrafts[0]", planesType.toUpperCase());
		}
		if (AddlandingSouth == true) {
			SmartAirport.envMoves.put("landingAircrafts[1]", planesType.toUpperCase());
		}
		if (AddtakeoffNorth == true) {
			SmartAirport.envMoves.put("takeoffAircrafts[0]", planesType.toUpperCase());
		}
		if (AddtakeoffSouth == true) {
			SmartAirport.envMoves.put("takeoffAircrafts[1]", planesType.toUpperCase());
		}
	}

	public static void performMechanicalProblem(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inScenario = true;
		SmartAirport.outputArea.setText("Perform Mechnical problem:\n");
		if (aircraft.equals("Takeoff North")) {
			SmartAirport.envMoves.put("mechanicalProblem[0]", String.valueOf(true));
		} else {
			SmartAirport.envMoves.put("mechanicalProblem[1]", String.valueOf(true));
		}
	}

	public static void performSlipperyRunway(SmartAirport smartairport, String runway) {
		if (SmartAirport.inScenario || runway.equals("-----"))
			return;
		SmartAirport.inScenario = true;
		SmartAirport.outputArea.setText("perform Dirty runway:\n");
		if (runway.equals("First Runway")) {
			SmartAirport.envMoves.put("slipperyRunway[0]", String.valueOf(true));
		} else if (runway.equals("Second Runway")) {
			SmartAirport.envMoves.put("slipperyRunway[1]", String.valueOf(true));
		} else if (runway.equals("Third Runway")) {
			SmartAirport.envMoves.put("slipperyRunway[2]", String.valueOf(true));
		} else {
			SmartAirport.envMoves.put("slipperyRunway[3]", String.valueOf(true));
		}
	}

	public static void performEmergencyLanding(SmartAirport smartairport, String aircraft) {
		if (SmartAirport.inScenario || aircraft.equals("-----"))
			return;
		SmartAirport.inScenario = true;
		SmartAirport.outputArea.setText("Perform Emergency Landing:\n");
		if (aircraft.equals("Landing North")) {
			SmartAirport.envMoves.put("emergencyLanding[0]", String.valueOf(true));
		} else {
			SmartAirport.envMoves.put("emergencyLanding[1]", String.valueOf(true));
		}
	}

	public static JPanel createHeadLinePanel() {
		JPanel headPanel = new JPanel();
		JLabel headLineLabel = new JLabel("<html><span style='font-size:20px'>Smart Airport Simulator</span></html>");
		headPanel.add(headLineLabel);
		return headPanel;
	}

	public static JPanel createEventsPanel(SmartAirport smartAirport) {
		JPanel eventsPanel = new JPanel(new BorderLayout());
		JPanel headLinePanel = new JPanel();
		JPanel eventsPanelToggelsLanding = new JPanel(new BorderLayout());
		JPanel eventsPanelToggelsTakeoff = new JPanel(new BorderLayout());
		JPanel eventsPanelToggels = new JPanel();
		JPanel eventsPanelToggelsAndLabel = new JPanel(new BorderLayout());

		JLabel headLineLabel = new JLabel("<html><span style='font-size:14px'>Add landings & takeoffs</span></html>");
		headLinePanel.add(headLineLabel);

		JToggleButton landingNorthToggleButton = new JToggleButton("Landing North");
		JToggleButton landingSouthToggleButton = new JToggleButton("Landing South");
		JToggleButton takeoffNorthToggleButton = new JToggleButton("Takeoff North");
		JToggleButton takeoffSouthToggleButton = new JToggleButton("Takeoff South");
		JPanel manualAddbuttonPanel = new JPanel();

		String[] planesType = { "-----", "Cargo", "Private", "Commercial" };
		JComboBox<String> planesTypeCombo = new JComboBox<>(planesType);

		landingNorthToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();

				if (state == ItemEvent.SELECTED) {
					AddlandingNorth = true;
				} else {
					AddlandingNorth = false;
				}
			}
		});
		landingSouthToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();

				if (state == ItemEvent.SELECTED) {
					AddlandingSouth = true;
				} else {
					AddlandingSouth = false;
				}
			}
		});

		takeoffNorthToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();

				if (state == ItemEvent.SELECTED) {
					AddtakeoffNorth = true;
				} else {
					AddtakeoffNorth = false;
				}
			}
		});

		takeoffSouthToggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();

				if (state == ItemEvent.SELECTED) {
					AddtakeoffSouth = true;
				} else {
					AddtakeoffSouth = false;
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

		eventsPanelToggelsLanding.add(landingNorthToggleButton, BorderLayout.NORTH);
		eventsPanelToggelsLanding.add(landingSouthToggleButton, BorderLayout.SOUTH);

		eventsPanelToggelsTakeoff.add(takeoffNorthToggleButton, BorderLayout.NORTH);
		eventsPanelToggelsTakeoff.add(takeoffSouthToggleButton, BorderLayout.SOUTH);

		eventsPanelToggels.add(eventsPanelToggelsLanding, BorderLayout.WEST);
		eventsPanelToggels.add(eventsPanelToggelsTakeoff, BorderLayout.EAST);

		eventsPanelToggelsAndLabel.add(headLinePanel, BorderLayout.NORTH);
		eventsPanelToggelsAndLabel.add(eventsPanelToggels, BorderLayout.CENTER);
		eventsPanelToggelsAndLabel.add(manualAddbuttonPanel, BorderLayout.SOUTH);

		eventsPanel.add(eventsPanelToggelsAndLabel, BorderLayout.NORTH);
		return eventsPanel;
	}

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

	static JPanel createEmergencyLandingPanel(SmartAirport smartAirport) {

		JPanel emergencyHeadLine = new JPanel();
		JPanel emergencyComboAndButton = new JPanel();
		JPanel eventsPanelemergency = new JPanel(new BorderLayout());

		JLabel emergencyHeadLineLabel = new JLabel(
				"<html><span style='font-size:14px'>Emergency Landing</span></html>");
		emergencyHeadLine.add(emergencyHeadLineLabel);

		String[] planes = { "-----", "Landing North", "Landing South" };
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

	static JPanel createMechanicalProblemPanel(SmartAirport smartAirport) {

		JPanel MechanicalProblemHeadLine = new JPanel();
		JPanel MechanicalProblemComboAndButton = new JPanel();

		JPanel eventsPanelMechanicalProblem = new JPanel(new BorderLayout());

		JLabel MechanicalProblemHeadLineLabel = new JLabel(
				"<html><span style='font-size:14px'>Mechanical Problem</span></html>");
		MechanicalProblemHeadLine.add(MechanicalProblemHeadLineLabel);

		String[] planes = { "-----", "Takeoff North", "Takeoff South" };
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

	static JPanel createScenariosPanel(SmartAirport smartAirport) {

		JPanel scenariosHeadLine = new JPanel();
		JPanel scenariosComboAndButton = new JPanel();

		JPanel eventsPanelScenarios = new JPanel(new BorderLayout());

		JLabel scenariosHeadLineLabel = new JLabel("<html><span style='font-size:14px'>Scenarios</span></html>");
		scenariosHeadLine.add(scenariosHeadLineLabel);

		String[] scenarios = { "-----", "rush hour", "slippery slope", "being a mechanic is hard","scared of flying"};
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

	protected static void setScenario(SmartAirport smartAirport, String name) {
		if(SmartAirport.inScenario)
			return;
		SmartAirport.wait = true;
		SmartAirport.outputArea.setText("Start scenario: " + name + "\n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		switch(name)
		{
			case "rush hour":
				setScenarioAndCounter("rush hour",6);
                break;
			case "slippery slope":
				setScenarioAndCounter("slippery slope",6);
                break;
			case "being a mechanic is hard":
				setScenarioAndCounter("being a mechanic is hard",6);
                break;
			case "scared of flying":
				setScenarioAndCounter("scared of flying",4);
                break;
		}
		
		SmartAirport.inScenario = true;
		SmartAirport.wait = false;
		
	}

	private static void setScenarioAndCounter(String scenrioName, int scenarioNumOfSteps) {
		SmartAirport.scenario = scenrioName;
		SmartAirport.scenarioCounter = scenarioNumOfSteps;
	}
}
