package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.allowensembles.presentation.main.MainWindow;


public class RobustnessButtonActionListener implements ActionListener {

    private MainWindow window;

    public RobustnessButtonActionListener(MainWindow window) {
    	this.window = window;
    }

    public static final String OPEN = "open";

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
        case OPEN:
           window.showRobustnessView();
        }
    }
}
