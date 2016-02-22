package eu.allowensembles.presentation.main.action.listener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.allowensembles.presentation.main.MainWindow;

public class EnsembleListListener implements ListSelectionListener {

    private MainWindow mainWindow;

    public EnsembleListListener(MainWindow mainWindow) {
	this.mainWindow = mainWindow;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
	if (e != null && e.getSource() instanceof DefaultListSelectionModel) {
	    DefaultListSelectionModel lsm = (DefaultListSelectionModel) e
		    .getSource();
	    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
		if (lsm.isSelectedIndex(i)) {
		    selectedEnsemble(mainWindow.getEnsembleInTable(i));
		    break;
		}
	    }
	}
    }

    private void selectedEnsemble(String selectedEnsemble) {
	// highlight selected ensemble
	// TODO
    }

}
