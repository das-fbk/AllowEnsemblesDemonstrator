package eu.allowensembles.presentation.main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = -1651600634146771350L;

    private static final Logger logger = LogManager
	    .getLogger(AboutDialog.class);

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public AboutDialog() {
	setResizable(false);
	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	setTitle("Allow Ensembles Demonstrator");
	setBounds(100, 100, 680, 320);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(null);
	try {
	    URL resource = getClass().getResource("/images/aboutImage.png");
	    BufferedImage image = ImageIO.read(resource);
	    JLabel picLabel = new JLabel(new ImageIcon(image));
	    picLabel.setBorder(null);
	    picLabel.setBounds(10, 11, 644, 226);
	    contentPanel.add(picLabel);
	    contentPanel.repaint();
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	JPanel buttonPane = new JPanel();
	buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	getContentPane().add(buttonPane, BorderLayout.SOUTH);

	JLabel lblNewLabel = new JLabel("http://www.allow-ensembles.eu/");
	lblNewLabel.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {

		try {
		    Desktop.getDesktop().browse(
			    new URL(lblNewLabel.getText()).toURI());
		} catch (Exception ex) {
		    logger.error(ex.getMessage(), ex);
		}

	    }
	});
	buttonPane.add(lblNewLabel);
	{
	    JButton okButton = new JButton("OK");
	    okButton.addMouseListener(new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
		    setVisible(false);
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
