package de.agbauer.physik.Observers;

import de.agbauer.physik.Generic.ActivatableForm;
import de.agbauer.physik.MainWindow;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by dennis on 10/02/2017.
 */
public class AcquisitionObserver implements Observer {

    private final ActivatableForm[] forms;

    public AcquisitionObserver(ActivatableForm[] forms) {
        this.forms = forms;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof  String) {
            String argStr = (String) arg;
            switch (argStr) {
                case "started-acquisition":
                    for (ActivatableForm form: forms) {
                        if (form != o) {
                            form.setEnabledState(false);
                        }
                    }
                    break;
                case "finished-acquisition":
                    for (ActivatableForm form: forms) {
                        form.setEnabledState(true);
                    }
                    break;
            }
        }
    }
}
