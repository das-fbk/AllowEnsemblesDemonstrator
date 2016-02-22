package eu.allowensembles.presentation.main.action.listener;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.utils.DoiBean;

public class SelectInstanceListener implements ListSelectionListener {

    private MainWindow window;

    public SelectInstanceListener(MainWindow mainWindow) {
	this.window = mainWindow;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
	if (e != null && e.getSource() instanceof JList<?>) {
	    JList<?> lsm = (JList<?>) e.getSource();
	    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
		if (lsm.isSelectedIndex(i)) {
		    setCurrentProcessWithName(lsm.getSelectedValue());
		    break;
		}
	    }
	}
    }

    public void setCurrentProcessWithName(Object selectedValue) {
	if (selectedValue instanceof String) {
	    String v = (String) selectedValue;
	    for (DoiBean db : window.getController().getProcessEngineFacade()
		    .getDomainObjectInstances()) {
		if (db.getName().equals(v)) {
		    window.getController().updateInterface(db);
		    break;
		}
	    }
	}
    }
}
