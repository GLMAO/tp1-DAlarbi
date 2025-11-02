package org.emp.gl.core.launcher;

import org.emp.gl.timer.service.impl.DummyTimeServiceImpl;
import org.emp.gl.timer.service.TimerService;
import org.emp.gl.clients.CompteARebours;
import org.emp.gl.clients.Horloge;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        testDuTimeService();
    }

    private static void testDuTimeService() {
        // 1. Créer le TimerService (une seule fois)
        TimerService service = new DummyTimeServiceImpl();

        // 2. Créer des horloges
        Horloge horloge1 = new Horloge("Num 1", service);
        Horloge horloge2 = new Horloge("Num 2", service);

        // 3. Créer un compte à rebours de 5 secondes
        CompteARebours c = new CompteARebours("CD-5", 5, service);

        // 4. Créer plusieurs comptes à rebours aléatoires
        Random rnd = new Random();
        for (int i = 1; i <= 10; i++) {
            int seconds = 10 + rnd.nextInt(11); // entre 10 et 20
            new CompteARebours("CD-" + i, seconds, service);
        }

        // 5. Si ton service doit être démarré manuellement :
        // ((DummyTimeServiceImpl) service).start(); // décommente si nécessaire
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
