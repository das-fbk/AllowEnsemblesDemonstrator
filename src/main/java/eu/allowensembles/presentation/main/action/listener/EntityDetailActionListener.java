package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.utils.DoiBean;
import eu.allowensembles.utils.UserData;

public class EntityDetailActionListener extends MouseAdapter {

    private static final Logger logger = LogManager
	    .getLogger(EntityDetailActionListener.class);
    private MainWindow window;

    public EntityDetailActionListener(MainWindow window) {
	this.window = window;
    }

    @Override
    public void mousePressed(MouseEvent e) {
	// get current user
	DoiBean db = window.getController().getCurrentDoiBean();
	if (db != null) {
	    UserData ud = window.getController().getUserData(db.getName());
	    // for now just check and display data for user, so there must be
	    // preferences
	    if (ud != null && ud.getPreferences() != null) {
		window.displayEntityWindow(true, ud, false);
	    }
	}
    }
}
