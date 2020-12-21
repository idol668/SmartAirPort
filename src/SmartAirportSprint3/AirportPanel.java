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

public class AirportPanel extends SmartAirport{
	
	static Boolean AddlandingNorth = false;
	static Boolean AddlandingSouth = false;
	static Boolean AddtakeoffNorth = false;
	static Boolean AddtakeoffSouth = false;
	
	public AirportPanel() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static void performAddAirfarts(SmartAirport smartairport, String planesType) {
		if(inScenario  || planesType.equals("-----"))
			return;
		inScenario = true;
		outputArea.setText("Adding Aircrafts:\n");
		if(AddlandingNorth == true) {
			envMoves.put("landingAircrafts[0]",planesType.toUpperCase());
		}
		if(AddlandingSouth == true) {
			envMoves.put("landingAircrafts[1]",planesType.toUpperCase());
		}
		if(AddtakeoffNorth == true) {
			envMoves.put("takeoffAircrafts[0]",planesType.toUpperCase());
		}
		if(AddtakeoffSouth == true) {
			envMoves.put("takeoffAircrafts[1]",planesType.toUpperCase());
		}	
	}
	
	public static void performMechanicalProblem(SmartAirport smartairport, String aircraft) {
		if(inScenario || aircraft.equals("-----"))
			return;
		inScenario = true;
		outputArea.setText("Perform Mechnical problem:\n");
		if(aircraft.equals("Takeoff North")) {
			envMoves.put("mechanicalProblem[0]",String.valueOf(true));
			
		}
		else {
			envMoves.put("mechanicalProblem[1]",String.valueOf(true));
		}
	}
	
	public static void performSlipperyRunway(SmartAirport smartairport, String runway) {
		if(inScenario || runway.equals("-----"))
			return;
		inScenario = true;
		outputArea.setText("perform Dirty runway:\n");
		if(runway.equals("First Runway")) {
			envMoves.put("slipperyRunway[0]",String.valueOf(true));	
		}
		else if(runway.equals("Second Runway")) {
			envMoves.put("slipperyRunway[1]",String.valueOf(true));	
		}
		else if(runway.equals("Third Runway")) {
			envMoves.put("slipperyRunway[2]",String.valueOf(true));	
		}
		else {
			envMoves.put("slipperyRunway[3]",String.valueOf(true));
		}
	}
	
	public static void performEmergencyLanding(SmartAirport smartairport, String aircraft) {
		if(inScenario || aircraft.equals("-----"))
			return;
		inScenario = true;
		outputArea.setText("Perform Emergency Landing:\n");
		if(aircraft.equals("Landing North")) {
			envMoves.put("emergencyLanding[0]",String.valueOf(true));
			
		}
		else {
			envMoves.put("emergencyLanding[1]",String.valueOf(true));
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
		
		String[] planesType = { "-----", "Cargo", "Private", "Commercial"};
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
				performAddAirfarts(smartAirport, planesTypeCombo.getSelectedItem().toString());
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
				if(startScenario)
	    			return;
				smartAirport.setStartScenario();
	    		performSlipperyRunway(smartAirport,dirtyRunwayCombo.getSelectedItem().toString());
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

		JLabel emergencyHeadLineLabel = new JLabel("<html><span style='font-size:14px'>Emergency Landing</span></html>");
		emergencyHeadLine.add(emergencyHeadLineLabel);

		String[] planes = { "-----", "Landing North", "Landing South"};
		JComboBox<String> emergencyCombo = new JComboBox<>(planes);

		JButton emergencyButton = new JButton("Go");
		emergencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(startScenario)
        			return;
				smartAirport.setStartScenario();
				performEmergencyLanding(smartAirport,emergencyCombo.getSelectedItem().toString());
				
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

		JLabel MechanicalProblemHeadLineLabel = new JLabel("<html><span style='font-size:14px'>Mechanical Problem</span></html>");
		MechanicalProblemHeadLine.add(MechanicalProblemHeadLineLabel);

		String[] planes = { "-----", "Takeoff North", "Takeoff South" };
		JComboBox<String> MechanicalProblemCombo = new JComboBox<>(planes);

		JButton MechanicalProblemButton = new JButton("Go");
		MechanicalProblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(startScenario)
        			return;
				smartAirport.setStartScenario();
        		performMechanicalProblem(smartAirport,MechanicalProblemCombo.getSelectedItem().toString());
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

		String[] scenarios = { "-----", "Scenario 1", "Scenario 1", "Scenario 3" };
		JComboBox<String> scenariosCombo = new JComboBox<>(scenarios);

		JButton scenariosButton = new JButton("Go");
		scenariosButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// implement
			}
		});

		scenariosComboAndButton.add(scenariosCombo, BorderLayout.WEST);
		scenariosComboAndButton.add(scenariosButton, BorderLayout.EAST);

		eventsPanelScenarios.add(scenariosHeadLine, BorderLayout.NORTH);
		eventsPanelScenarios.add(scenariosComboAndButton, BorderLayout.CENTER);

		return eventsPanelScenarios;
	}
}
