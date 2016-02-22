package eu.allowensembles.robustness.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.StepEvent;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.robustness.controller.RobustnessController;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

public class RobustnessView extends JFrame {

	private static final long serialVersionUID = 4768135707935398796L;

	private JPanel main;
	private workflowView wView;
	private ReplicationView rView;
	private RobustnessController robustnessController;
	private JCheckBox steppingCheckbox;
	private JButton microStepButton;
	private JButton macroStepButton;

	private JLabel[] replicaLabels = new JLabel[3];
	private JPanel ret;

	private JPanel workflowPanel;
	private JPanel replicationPanel;

	private JCheckBox link23;
	private JCheckBox link12;
	private JButton[] replicaButtons = new JButton[3];

	public RobustnessView(RobustnessController robustnessController) {

		this.robustnessController = robustnessController;
		setupGUI();
	}

	private void setupGUI() {
		main = new JPanel();
		setupGraphPanel();

		// Setup JFrame.
		setContentPane(main);
		main.setLayout(new BorderLayout(0, 0));
		main.add(ret);
		ret.setLayout(new BoxLayout(ret, BoxLayout.Y_AXIS));
		ret.setBorder(new EmptyBorder(5, 10, 10, 10));

		workflowPanel = new JPanel();
		ret.add(workflowPanel);
		workflowPanel.setLayout(new BorderLayout(0, 0));
		// Create EvoKnowledge panel.
		wView = new workflowView(robustnessController.getAnnotaionHandler());
		wView.setBorder(new CompoundBorder(new EmptyBorder(5, 0, 0, 0), new LineBorder(new Color(0, 0, 0))));
		workflowPanel.add(wView, BorderLayout.CENTER);
						
								JLabel lblRunningWorkflow = new JLabel("Workflow View");
								workflowPanel.add(lblRunningWorkflow, BorderLayout.NORTH);
								lblRunningWorkflow.setFont(new Font("Tahoma", Font.PLAIN, 18));

		replicationPanel = new JPanel();
		ret.add(replicationPanel);
		replicationPanel.setLayout(new BorderLayout(0, 0));

		JPanel botButtonPanel = new JPanel();
		replicationPanel.add(botButtonPanel, BorderLayout.SOUTH);
		FlowLayout flowLayout = (FlowLayout) botButtonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);

		JCheckBox checkBox = new JCheckBox("Auto-Scroll");
		botButtonPanel.add(checkBox);
		checkBox.setSelected(true);

		microStepButton = new JButton("Micro Step");
		microStepButton.setIcon(new ImageIcon(MainWindow.class.getResource("/images/knob_micro_walk.png")));
		microStepButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				robustnessController.execute();
				if (robustnessController.isFinished()) {
					setMacroStepButton();
				}
			}
		});
		microStepButton.setVisible(false);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		optionsPanel.setSize(100, 500);
		replicationPanel.add(optionsPanel, BorderLayout.WEST);
		GridBagLayout gbl_optionsPanel = new GridBagLayout();
		gbl_optionsPanel.columnWidths = new int[] { 83 };
		gbl_optionsPanel.rowHeights = new int[] {0, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34};
		gbl_optionsPanel.columnWeights = new double[] { 0.0 };
		gbl_optionsPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		optionsPanel.setLayout(gbl_optionsPanel);
		JButton replica1 = new JButton("R1");
		replica1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rView.failReplica(0);
				if (robustnessController.toggleReplicasAvailability(0)) {
					replica1.setBackground(Color.GREEN);
				} else {
					replica1.setBackground(Color.RED);
				}
			}
		});
		replica1.setBackground(Color.GREEN);
		replica1.setToolTipText("Press to simulate failure.");
		replicaButtons[0] = replica1;
		GridBagConstraints gbc_replica1 = new GridBagConstraints();
		gbc_replica1.fill = GridBagConstraints.BOTH;
		gbc_replica1.insets = new Insets(0, 0, 5, 0);
		gbc_replica1.gridx = 0;
		gbc_replica1.gridy = 1;
		optionsPanel.add(replica1, gbc_replica1);

		JLabel replicaLabel1 = new JLabel("");
		GridBagConstraints gbc_replicaLabel1 = new GridBagConstraints();
		gbc_replicaLabel1.fill = GridBagConstraints.BOTH;
		gbc_replicaLabel1.insets = new Insets(0, 0, 5, 0);
		gbc_replicaLabel1.gridx = 0;
		gbc_replicaLabel1.gridy = 2;
		optionsPanel.add(replicaLabel1, gbc_replicaLabel1);
		replicaLabel1.setForeground(Color.RED);
		replicaLabels[0] = replicaLabel1;

		link12 = new JCheckBox("Link R1 / R2");
		GridBagConstraints gbc_link12 = new GridBagConstraints();
		gbc_link12.fill = GridBagConstraints.BOTH;
		gbc_link12.insets = new Insets(0, 0, 5, 0);
		gbc_link12.gridx = 0;
		gbc_link12.gridy = 3;
		optionsPanel.add(link12, gbc_link12);
		link12.setSelected(true);
		link12.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rView.cutLink(0, 100, 0);
				if (!link12.isSelected()) {
					robustnessController.addFailure(RobustnessController.Failure.LINK_12_FAILED);
				} else {
					robustnessController.removeFailure(RobustnessController.Failure.LINK_12_FAILED);
				}
			}
		});

		JButton replica3 = new JButton("R3");
		replica3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rView.failReplica(2);
				if (robustnessController.toggleReplicasAvailability(2)) {
					replica3.setBackground(Color.GREEN);
				} else {
					replica3.setBackground(Color.RED);
				}
			}
		});

		JButton replica2 = new JButton("R2");
		replica2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rView.failReplica(1);
				if (robustnessController.toggleReplicasAvailability(1)) {
					replica2.setBackground(Color.GREEN);
				} else {
					replica2.setBackground(Color.RED);
				}
			}
		});
		replica2.setToolTipText("Press to simulate failure.");
		replica2.setBackground(Color.GREEN);
		replicaButtons[1] = replica2;
		GridBagConstraints gbc_replica2 = new GridBagConstraints();
		gbc_replica2.fill = GridBagConstraints.BOTH;
		gbc_replica2.insets = new Insets(0, 0, 5, 0);
		gbc_replica2.gridx = 0;
		gbc_replica2.gridy = 4;
		optionsPanel.add(replica2, gbc_replica2);

		JLabel replicaLabel2 = new JLabel("");
		GridBagConstraints gbc_replicaLabel2 = new GridBagConstraints();
		gbc_replicaLabel2.insets = new Insets(0, 0, 5, 0);
		gbc_replicaLabel2.gridx = 0;
		gbc_replicaLabel2.gridy = 5;
		replicaLabel2.setForeground(Color.RED);
		replicaLabels[1] = replicaLabel2;
		optionsPanel.add(replicaLabel2, gbc_replicaLabel2);

		link23 = new JCheckBox("Link R2 / R3");
		GridBagConstraints gbc_link23 = new GridBagConstraints();
		gbc_link23.insets = new Insets(0, 0, 5, 0);
		gbc_link23.fill = GridBagConstraints.BOTH;
		gbc_link23.gridx = 0;
		gbc_link23.gridy = 7;
		optionsPanel.add(link23, gbc_link23);
		link23.setSelected(true);
		link23.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rView.cutLink(0, 100, 1);
				if (!link23.isSelected()) {
					robustnessController.addFailure(RobustnessController.Failure.LINK_23_FAILED);
				} else {
					robustnessController.removeFailure(RobustnessController.Failure.LINK_23_FAILED);
				}
			}
		});
		replica3.setBackground(Color.GREEN);
		replica3.setToolTipText("Press to simulate failure.");
		replicaButtons[2] = replica3;
		GridBagConstraints gbc_replica3 = new GridBagConstraints();
		gbc_replica3.fill = GridBagConstraints.BOTH;
		gbc_replica3.insets = new Insets(0, 0, 5, 0);
		gbc_replica3.gridx = 0;
		gbc_replica3.gridy = 8;
		optionsPanel.add(replica3, gbc_replica3);

		JLabel replicaLabel3 = new JLabel("");
		replicaLabel3.setForeground(Color.RED);
		GridBagConstraints gbc_replicaLabel3 = new GridBagConstraints();
		gbc_replicaLabel3.insets = new Insets(0, 0, 5, 0);
		gbc_replicaLabel3.fill = GridBagConstraints.BOTH;
		gbc_replicaLabel3.gridx = 0;
		gbc_replicaLabel3.gridy = 9;
		optionsPanel.add(replicaLabel3, gbc_replicaLabel3);
		replicaLabels[2] = replicaLabel3;

		macroStepButton = new JButton("Macro Step");
		GridBagConstraints macroButtonConstraints = new GridBagConstraints();
		macroButtonConstraints.fill = GridBagConstraints.BOTH;
		macroButtonConstraints.gridx = 0;
		macroButtonConstraints.gridy = 12;
		GridBagConstraints microButtonConstraints = new GridBagConstraints();
		microButtonConstraints.fill = GridBagConstraints.BOTH;
		microButtonConstraints.gridx = 0;
		microButtonConstraints.gridy = 12;
		
				steppingCheckbox = new JCheckBox("Enable Stepping");
				GridBagConstraints gbc_steppingCheckbox = new GridBagConstraints();
				gbc_steppingCheckbox.anchor = GridBagConstraints.WEST;
				gbc_steppingCheckbox.insets = new Insets(0, 0, 5, 0);
				gbc_steppingCheckbox.gridx = 0;
				gbc_steppingCheckbox.gridy = 11;
				optionsPanel.add(steppingCheckbox, gbc_steppingCheckbox);
				steppingCheckbox.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						robustnessController.setStepping(steppingCheckbox.isSelected());
					}
				});
		optionsPanel.add(macroStepButton, macroButtonConstraints);
		optionsPanel.add(microStepButton, microButtonConstraints);
		macroStepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.post(new StepEvent());
			}
		});
		macroStepButton.setIcon(new ImageIcon(MainWindow.class.getResource("/images/knob_walk.png")));
		rView = new ReplicationView();
		replicationPanel.add(rView, BorderLayout.CENTER);
				
						JPanel workflowButtonPanel = new JPanel();
						replicationPanel.add(workflowButtonPanel, BorderLayout.NORTH);
						workflowButtonPanel.setLayout(new BorderLayout(0, 0));
						
						JPanel leftPanel = new JPanel();
						workflowButtonPanel.add(leftPanel, BorderLayout.WEST);
						
								JLabel lblReplicationView = new JLabel("Replication View");
								leftPanel.add(lblReplicationView);
								lblReplicationView.setFont(new Font("Tahoma", Font.PLAIN, 18));
								
								JPanel rightPanel = new JPanel();
								workflowButtonPanel.add(rightPanel, BorderLayout.EAST);
								
										JCheckBox chckbxAutoscroll = new JCheckBox("Auto-Scroll");
										rightPanel.add(chckbxAutoscroll);
										chckbxAutoscroll.setSelected(true);
				chckbxAutoscroll.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						wView.setAutoScroll(chckbxAutoscroll.isSelected());
					}
				});
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rView.setAutoScroll(checkBox.isSelected());
			}
		});

		setTitle("Allow Ensembles Demonstrator");
		setSize(1080, 763);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void fixSizes() {
		workflowPanel.setMinimumSize(new Dimension(0, workflowPanel.getHeight() + 50));
		workflowPanel.setMaximumSize(new Dimension(2000, workflowPanel.getHeight() + 50));
		replicationPanel.setMaximumSize(new Dimension(2000, replicationPanel.getHeight()));
	}

	private JPanel setupGraphPanel() {
		ret = new JPanel();

		return ret;
	}

	public void setNewMaster(int replica) {
		for (int i = 0; i < 3; i++) {
			if (i == replica) {
				replicaLabels[i].setText("Master");
			} else {
				replicaLabels[i].setText("");
			}
		}
	}

	/**
	 * This method is used to disable and later re-enable the inputs used to
	 * simulate failures
	 * 
	 * @param allow
	 */
	public void allowFailureChanges(boolean allow) {
		link12.setEnabled(allow);
		link23.setEnabled(allow);
		for (JButton btn : replicaButtons) {
			btn.setEnabled(allow);
		}
	}

	public ReplicationView getReplicationView() {
		return rView;
	}
	
	private void setMacroStepButton() {
		microStepButton.setVisible(false);
		macroStepButton.setVisible(true);
	}
	
	private void setMicroStepButton() {
		macroStepButton.setVisible(false);
		microStepButton.setVisible(true);
	}

	public workflowView getWorkflowView() {
		return wView;
	}

	public void enableStepButtoon() {
		setMicroStepButton();
	}
}
