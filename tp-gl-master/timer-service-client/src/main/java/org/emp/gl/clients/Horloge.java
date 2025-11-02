package org.emp.gl.clients;

import org.emp.gl.timer.service.TimerService;
import org.emp.gl.timer.service.TimerChangeListener;
import org.emp.gl.timer.service.impl.DummyTimeServiceImpl;

public class Horloge implements TimerChangeListener {

    private String name;
    private TimerService timerService;

    public Horloge(String name, TimerService timerService) {
        this.name = name;
        this.timerService = timerService;

        // S’enregistrer auprès du service pour recevoir les notifications
        if (timerService instanceof DummyTimeServiceImpl) {
            ((DummyTimeServiceImpl) timerService).addTimeChangeListener(this);
        }

        System.out.println("Horloge " + name + " initialisée et abonnée !");
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        // N’affiche l’heure que quand les secondes changent
        if (TimerChangeListener.SECONDE_PROP.equals(evt.getPropertyName())) {
            afficherHeure();
        }
    }

    private void afficherHeure() {
        System.out.println(name + " → " +
                String.format("%02d:%02d:%02d",
                        timerService.getHeures(),
                        timerService.getMinutes(),
                        timerService.getSecondes()));
    }
}
