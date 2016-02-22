package eu.allowensembles.presentation.main.action.listener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.DomainObjectInstanceSelection;

public class EntityTableSelectionListener implements ListSelectionListener {

    private JTable table;

    public EntityTableSelectionListener(JTable generalTable) {
	this.table = generalTable;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
	if (e.getSource() instanceof DefaultListSelectionModel) {
	    DefaultListSelectionModel lsm = (DefaultListSelectionModel) e
		    .getSource();
	    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
		if (lsm.isSelectedIndex(i)) {
		    String name = (String) table.getValueAt(i, 1);
		    MainController
			    .post(new DomainObjectInstanceSelection(name));
		    break;
		}
	    }

	}

    }

}
