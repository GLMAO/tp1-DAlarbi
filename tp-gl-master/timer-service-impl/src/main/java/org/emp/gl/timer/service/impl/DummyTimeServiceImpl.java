package org.emp.gl.timer.service.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import org.emp.gl.timer.service.TimerChangeListener;
import org.emp.gl.timer.service.TimerService;

/**
 * Implémentation du TimerService avec PropertyChangeSupport
 * pour gérer la concurrence de manière thread-safe
 */
public class DummyTimeServiceImpl implements TimerService {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    // Variables de temps
    int dixiemeDeSeconde;
    int minutes;
    int secondes;  
    int heures;

    /**
     * Constructeur : crée un timer qui déclenche toutes les 100ms
     */
    public DummyTimeServiceImpl() {
        setTimeValues();
        
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeChanged();
            }
        };
        timer.scheduleAtFixedRate(task, 100, 100);
    }

    private void setTimeValues() {
        LocalTime localTime = LocalTime.now();
        setSecondes(localTime.getSecond());
        setMinutes(localTime.getMinute());
        setHeures(localTime.getHour());
        setDixiemeDeSeconde(localTime.getNano() / 100000000);
    }

    @Override
    public void addTimeChangeListener(TimerChangeListener pl) {
        // Déléguer à PropertyChangeSupport (thread-safe)
        support.addPropertyChangeListener(pl);
    }

    @Override
    public void removeTimeChangeListener(TimerChangeListener pl) {
        // Déléguer à PropertyChangeSupport (thread-safe)
        support.removePropertyChangeListener(pl);
    }

    private void timeChanged() {
        setTimeValues();
    }

    // === DIXIÈMES DE SECONDE ===
    public void setDixiemeDeSeconde(int newDixiemeDeSeconde) {
        if (dixiemeDeSeconde == newDixiemeDeSeconde)
            return;

        int oldValue = dixiemeDeSeconde;
        dixiemeDeSeconde = newDixiemeDeSeconde;

        // PropertyChangeSupport gère la notification thread-safe
        support.firePropertyChange(
            TimerChangeListener.DIXEME_DE_SECONDE_PROP,
            oldValue, 
            dixiemeDeSeconde
        );
    }

    // === SECONDES ===
    public void setSecondes(int newSecondes) {
        if (secondes == newSecondes)
            return;

        int oldValue = secondes;
        secondes = newSecondes;

        // PropertyChangeSupport gère la notification thread-safe
        support.firePropertyChange(
            TimerChangeListener.SECONDE_PROP,
            oldValue, 
            secondes
        );
    }

    // === MINUTES ===
    public void setMinutes(int newMinutes) {
        if (minutes == newMinutes)
            return;

        int oldValue = minutes;
        minutes = newMinutes;

        // PropertyChangeSupport gère la notification thread-safe
        // CORRECTION : envoyer 'minutes' pas 'secondes' !
        support.firePropertyChange(
            TimerChangeListener.MINUTE_PROP,
            oldValue, 
            minutes
        );
    }

    // === HEURES ===
    public void setHeures(int newHeures) {
        if (heures == newHeures)
            return;

        int oldValue = heures;
        heures = newHeures;

        // PropertyChangeSupport gère la notification thread-safe
        // CORRECTION : envoyer 'heures' pas 'secondes' !
        support.firePropertyChange(
            TimerChangeListener.HEURE_PROP,
            oldValue, 
            heures
        );
    }

    // === GETTERS ===
    @Override
    public int getDixiemeDeSeconde() {
        return dixiemeDeSeconde;
    }

    @Override
    public int getHeures() {
        return heures;
    }

    @Override
    public int getMinutes() {
        return minutes;
    }

    @Override
    public int getSecondes() {
        return secondes;
    }
}