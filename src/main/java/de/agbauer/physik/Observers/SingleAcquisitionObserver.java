package de.agbauer.physik.Observers;

import ij.ImagePlus;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by dennis on 17/02/2017.
 */
public class SingleAcquisitionObserver implements Observer{

    private SingleAcquisition[] listeners;

    public SingleAcquisitionObserver(SingleAcquisition[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ImagePlus) {
            ImagePlus image = (ImagePlus) arg;
            for (SingleAcquisition listener: listeners) {
                listener.acquiredImage(image);
            }
        }
    }
}
