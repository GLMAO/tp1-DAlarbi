package org.emp.gl.core.launcher;

import org.emp.gl.timer.service.impl.DummyTimeServiceImpl;
import org.emp.gl.timer.service.TimerService;
import org.emp.gl.clients.Horloge;

public class App {

    public static void main(String[] args) {
        testDuTimeService();
    }

    private static void testDuTimeService() {
        // 1. Créer le TimerService
        TimerService service = new DummyTimeServiceImpl();

        // 2. Injecter le service dans la ou les Horloges
        Horloge horloge1 = new Horloge("Num 1", service);
        Horloge horloge2 = new Horloge("Num 2", service);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
