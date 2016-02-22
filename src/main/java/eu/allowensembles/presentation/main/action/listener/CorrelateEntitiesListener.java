package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.events.DomainObjectInstanceSelectionByName;

public class CorrelateEntitiesListener implements MouseListener {

    public CorrelateEntitiesListener() {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	JList<String> list = ((JList<String>) e.getSource());
	if (e.getClickCount() == 2 && list.getModel() != null) {

	    // Double-click detected
	    int index = list.locationToIndex(e.getPoint());
	    if (index >= 0) {
		MainController.post(new DomainObjectInstanceSelectionByName(
			list.getModel().getElementAt(index)));
	    }
	}
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
