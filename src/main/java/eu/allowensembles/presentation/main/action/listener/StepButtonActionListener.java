package eu.allowensembles.presentation.main.action.listener;

import static eu.allowensembles.DemonstratorConstant.STEP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.StepEvent;

public class StepButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case STEP:
	    MainController.post(new StepEvent());
	    break;
	}
    }

}
