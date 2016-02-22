package eu.allowensembles.presentation.main.action;

import static eu.allowensembles.DemonstratorConstant.UMS_PREFIX;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.utils.DoiBean;

public class SelectUmsAction extends AbstractAction {

    private static final Logger logger = LogManager
	    .getLogger(SelectUmsAction.class);

    private static final long serialVersionUID = -4445170764939294838L;

    private MainWindow window;

    public SelectUmsAction(MainWindow window) {
	this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	List<DoiBean> instances = window.getController()
		.getProcessEngineFacade().getDomainObjectInstances();
	if (instances == null) {
	    logger.warn("domainObjectInstances null");
	    return;
	}
	// if there is at least one ums instance, then update interface
	// according to it
	for (DoiBean db : instances) {
	    if (db.getName().startsWith(UMS_PREFIX)) {
		window.getController().selectDomainObject();
		break;
	    }
	}
    }

}
