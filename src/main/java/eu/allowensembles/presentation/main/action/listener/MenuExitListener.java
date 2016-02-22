package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuExitListener implements ActionListener {

    private static final Logger logger = LogManager
	    .getLogger(MenuExitListener.class);

    @Override
    public void actionPerformed(ActionEvent e) {
	logger.info("Demonstrator exit");
	System.exit(0);
    }

}
