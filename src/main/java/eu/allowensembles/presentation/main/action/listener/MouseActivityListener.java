package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.events.SelectedAbstractActivityEvent;

// Handler mouse click on activity
public class MouseActivityListener extends MouseAdapter {

    private static final String UMS_SECURITY_AND_PRIVACY_FILTERING = "UMS_SecurityAndPrivacyFiltering";

    private static final String UMS_UTILITY_RANKING = "UMS_UtilityRanking";

    private static final Logger logger = LogManager
	    .getLogger(MouseActivityListener.class);

    private mxGraphComponent graphComponent;

    private MainWindow window;

    public MouseActivityListener(mxGraphComponent graphComponent,
	    MainWindow window) {
	this.graphComponent = graphComponent;
	this.window = window;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	Object cell = graphComponent.getCellAt(e.getX(), e.getY());

	if (cell != null) {
	    if (cell instanceof mxCell) {
		if (((mxCell) cell).isVertex()) {
		    String label = graphComponent.getGraph().getLabel(cell);
		    // logger.debug("Clicked cell=" + label);
		    String user = window.getController().getCurrentUser();

		    switch (label) {
		    case UMS_UTILITY_RANKING:
			if (window.getController() != null) {
			    window.getUtilityView().setData(
				    window.getController().getUserData(user));
			    window.showUtilityFrame(true);
			}
			break;
		    case UMS_SECURITY_AND_PRIVACY_FILTERING:
			if (window.getController() != null) {
			    window.getPSView().setData(
				    window.getController().getUserData(user));

			    window.showPSFrame(true);
			}
			break;
		    default:
			window.getController().post(
				new SelectedAbstractActivityEvent(label));
			// JOptionPane.showMessageDialog(graphComponent, "cell="
			// + graphComponent.getGraph().getLabel(cell));
			break;
		    }
		}
	    }
	}
    }
}
