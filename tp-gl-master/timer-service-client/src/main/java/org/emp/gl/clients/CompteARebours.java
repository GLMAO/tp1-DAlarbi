package org.emp.gl.clients;

import org.emp.gl.timer.service.TimerService;
import org.emp.gl.timer.service.TimerChangeListener;

/**
 * CompteARebours : similaire à Horloge, mais décrémente une valeur entière chaque seconde.
 */
public class CompteARebours implements TimerChangeListener {
    private final String name;
    private final TimerService timerService;
    private int remaining;             // nombre de secondes restantes
    private boolean active = true;     // utilisé si on préfère marquer inactif avant suppression

    public CompteARebours(String name, int initialSeconds, TimerService timerService) {
        this.name = name;
        this.remaining = initialSeconds;
        this.timerService = timerService;

        // S'enregistrer auprès du service
        this.timerService.addTimeChangeListener(this);
        System.out.println(name + " démarré avec " + initialSeconds + "s");
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        // ici on suppose que SECONDE_PROP est le nom envoyé chaque seconde
        if (!active) return;

        if (TimerChangeListener.SECONDE_PROP.equals(evt.getPropertyName())) {
            tick();
        }
    }

    private synchronized void tick() {
        if (!active) return;
        if (remaining <= 0) return;

        remaining--;
        System.out.println(name + " -> " + remaining + "s");

        if (remaining <= 0) {
            // Option 1 : se désinscrire immédiatement
            timerService.removeTimeChangeListener(this);
            active = false;
            System.out.println(name + " : terminé et désinscrit.");
        }
    }

    // méthode utilitaire si on veut forcer l'arrêt
    public synchronized void stop() {
        if (!active) return;
        active = false;
        timerService.removeTimeChangeListener(this);
        System.out.println(name + " : arrêté manuellement et désinscrit.");
    }
}
