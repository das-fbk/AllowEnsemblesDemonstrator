package eu.allowensembles.presentation.main.action.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import eu.allowensembles.controller.MainController;

public class SelectedComboEntityListener implements ActionListener {

    public SelectedComboEntityListener() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	if (e.getSource() != null && e.getSource() instanceof JComboBox<?>) {
	    JComboBox<?> combobox = ((JComboBox<?>) e.getSource());
	    String selected = (String) combobox.getSelectedItem();
	    if (selected != null && !selected.isEmpty()) {
		MainController.post(new DomainObjectDefinitionSelectionByName(
			selected));
	    }

	}
    }
}
