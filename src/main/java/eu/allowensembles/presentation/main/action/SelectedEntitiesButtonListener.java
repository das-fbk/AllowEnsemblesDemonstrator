package eu.allowensembles.presentation.main.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.allowensembles.presentation.main.MainWindow;

public class SelectedEntitiesButtonListener implements ActionListener {

    public final static String PREVIOUS = "PREVIOUS_ENTITY";
    public final static String NEXT = "NEXT_ENTITY";
    private MainWindow window;

    public SelectedEntitiesButtonListener(MainWindow window) {
	this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case PREVIOUS:
	    window.getController().selectPreviousEntity();
	    break;
	case NEXT:
	    window.getController().selectNextEntity();
	    break;
	}

    }
}
