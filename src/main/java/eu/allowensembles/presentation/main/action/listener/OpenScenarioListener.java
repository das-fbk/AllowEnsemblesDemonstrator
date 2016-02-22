package eu.allowensembles.presentation.main.action.listener;

import static eu.allowensembles.DemonstratorConstant.SCENARIO1;
import static eu.allowensembles.DemonstratorConstant.SCENARIOREVIEW;
import static eu.allowensembles.DemonstratorConstant.SCENARIOREVIEW_FOLDER;
import static eu.allowensembles.DemonstratorConstant.SCENARIOREVIEW_MAIN_XML;
import static eu.allowensembles.DemonstratorConstant.STORYBOARD1_FOLDER;
import static eu.allowensembles.DemonstratorConstant.STORYBOARD1_MAIN_XML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.utils.ResourceLoader;

public class OpenScenarioListener implements ActionListener {

    private static final Logger logger = LogManager
	    .getLogger(OpenScenarioListener.class);
    private JFileChooser chooser;
    private static final String FILE_SEPARATOR = "/";

    public OpenScenarioListener() {
	chooser = new JFileChooser() {

	    private static final long serialVersionUID = 7489308134784417097L;

	    @Override
	    public void approveSelection() {
		File f = getSelectedFile();
		if (f != null && f.exists()) {
		    logger.info("Selected scenario: " + f.getAbsolutePath());
		    super.approveSelection();
		    try {
			ResourceLoader.loadStoryboard(f);
		    } catch (JAXBException e) {
			logger.error("Error on loading storyboard " + f, e);
			JOptionPane.showMessageDialog(this,
				"Scenario file not valid");
		    }
		} else {
		    logger.error("Selected scenario file null or not exist");
		}
	    }

	};
	FileNameExtensionFilter filter = new FileNameExtensionFilter(
		"Allow Ensembles scenario (.xml)", "xml");
	chooser.setFileFilter(filter);
    }

    private void loadScenario(String folder, String mainXml) {
	File f = null;
	try {
	    URL res = getClass().getResource(
		    FILE_SEPARATOR + folder + FILE_SEPARATOR + mainXml);
	    f = new File(res.toURI());
	    ResourceLoader.loadStoryboard(f);
	} catch (JAXBException | URISyntaxException | IllegalArgumentException e) {
	    logger.warn("Trying to load something inside jar");
	    logger.error("Error loading default storyboard1 " + f.toPath(), e);
	    e.printStackTrace();
	}

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case SCENARIO1:
	    loadScenario(STORYBOARD1_FOLDER, STORYBOARD1_MAIN_XML);
	    break;
	case SCENARIOREVIEW:
	    loadScenario(SCENARIOREVIEW_FOLDER, SCENARIOREVIEW_MAIN_XML);
	    break;
	default:
	    chooser.showOpenDialog(null);
	    break;
	}
    }
}
