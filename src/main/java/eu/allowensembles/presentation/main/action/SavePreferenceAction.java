package eu.allowensembles.presentation.main.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.AddNewEntityWindow;
import eu.allowensembles.presentation.main.events.UpdateUserPreferenceEvent;
import eu.allowensembles.utility.controller.Preferences;

public class SavePreferenceAction implements ActionListener {

    private static final Logger logger = LogManager
	    .getLogger(SavePreferenceAction.class);

    private AddNewEntityWindow window;

    public SavePreferenceAction(AddNewEntityWindow addNewEntityWindow) {
	this.window = addNewEntityWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (window.getUserName() == null || window.getUserName().isEmpty()) {
	    logger.warn("Impossible to save entity without a name");
	    return;
	}
	// read from window user preferences and build Preference Object
	Preferences userPreference = new Preferences();
	userPreference.setCmax(window.getCmax());
	userPreference.setCweight(window.getCweight());
	userPreference.setNCweight(window.getNCweight());
	userPreference.setNoCmax(window.getNoCmax());
	userPreference.setRCweight(window.getRCweight());
	userPreference.setTmax(window.getTmax());
	userPreference.setTTweight(window.getTTweight());
	userPreference.setUSPweight(window.getUSPweight());
	userPreference.setWDweight(window.getWDweight());
	userPreference.setWmax(window.getWmax());
	userPreference.setWSDweight(window.getWSDweight());

	userPreference.setTtCar(window.getTtCar());
	userPreference.setTtFlexibus(window.getTtFlexibus());
	userPreference.setTtWalk(window.getTtWalk());
	userPreference.setTtCarpooling(window.getTtCarpooling());

	userPreference.setPmCash(window.getPmCash());
	userPreference.setPmCreditCard(window.getPmCreditCard());
	userPreference.setPmPaypal(window.getPmPaypal());
	userPreference.setPmServiceCard(window.getPmServiceCard());

	userPreference.setNamesens(window.getNamesens());
	userPreference.setEmailsens(window.getEmailsens());
	userPreference.setPhonesens(window.getPhonesens());
	userPreference.setGpssens(window.getGpssens());

	// do actual save. Note: is not possible right now to change user name
	window.save(window.getUserName(), userPreference);

	// post event
	MainController.post(new UpdateUserPreferenceEvent(userPreference));

	// hide window
	window.displayWindow(false, false);
    }
}
