package eu.allowensembles.presentation.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.DemonstratorConstant;

/**
 * A dialog for user preferences for Allow Ensembles Demonstrator
 */

public class PreferencesDialog extends JDialog {

    private static final long serialVersionUID = -1651600634146771350L;

    private static final Logger logger = LogManager
	    .getLogger(PreferencesDialog.class);

    private final JPanel contentPanel = new JPanel();
    private JTextField textField;

    /**
     * Create the dialog.
     */
    public PreferencesDialog() {
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentShown(ComponentEvent e) {
		textField.setText("" + DemonstratorConstant.getStepTime());
	    }
	});
	setResizable(false);
	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	setTitle("Allow Ensembles Demonstrator");
	setBounds(100, 100, 307, 205);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(null);

	JLabel lblSettings = new JLabel("Settings");
	lblSettings.setBounds(10, 11, 46, 14);
	contentPanel.add(lblSettings);

	JLabel lblStepTime = new JLabel("Step time (ms)");
	lblStepTime.setBounds(21, 53, 100, 14);
	contentPanel.add(lblStepTime);

	textField = new JTextField();
	textField
		.setToolTipText("specify step duration for demonstrator in play mode");
	textField.setBounds(173, 50, 86, 20);

	contentPanel.add(textField);
	textField.setColumns(10);

	JPanel panel = new JPanel();
	panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
	panel.setBounds(10, 36, 281, 96);
	contentPanel.add(panel);

	JPanel buttonPane = new JPanel();
	buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	getContentPane().add(buttonPane, BorderLayout.SOUTH);
	{
	    JButton okButton = new JButton("OK");
	    okButton.addMouseListener(new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
		    // save settings
		    if (textField != null && textField.getText() != null) {
			try {
			    int milliseconds = Integer.valueOf(textField
				    .getText());
			    if (milliseconds >= 0
				    && milliseconds <= DemonstratorConstant.STEP_TIME_MAX) {
				DemonstratorConstant.setStepTime(milliseconds);
			    } else {
				displayValidationError(panel);
			    }
			} catch (NumberFormatException nfe) {
			    logger.error(nfe.getMessage(), nfe);
			    displayValidationError(panel);
			}
		    }
		    setVisible(false);
		}

		private void displayValidationError(JPanel panel) {
		    JOptionPane.showMessageDialog(panel,
			    "Step time must be a number more than 0 and less than "
				    + DemonstratorConstant.STEP_TIME_MAX,
			    "Error", JOptionPane.ERROR_MESSAGE);
		    DemonstratorConstant
			    .setStepTime(DemonstratorConstant.STEP_TIME_DEFAULT);
		}
	    });
	    okButton.setActionCommand("OK");
	    buttonPane.add(okButton);
	    getRootPane().setDefaultButton(okButton);
	}

	// dialog disappear when user hit ESC
	getRootPane().registerKeyboardAction(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
	    }
	}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
		JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
