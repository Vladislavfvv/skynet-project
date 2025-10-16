package org.example;

import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        CyclicBarrier barrierStart = new CyclicBarrier(3);
        CyclicBarrier barrierEnd = new CyclicBarrier(3);
        Factory factory = new Factory(barrierStart, barrierEnd);
        Fraction world = new Fraction("World", factory, barrierStart, barrierEnd);
        Fraction wednesday = new Fraction("Wednesday", factory,  barrierStart, barrierEnd);

        Thread threadFactory = new Thread(factory);
        Thread threadWorld = new Thread(world);
        Thread threadWednesday = new Thread(wednesday);

        threadFactory.start();
        threadWorld.start();
        threadWednesday.start();

        try {
            threadFactory.join();
            threadWorld.join();
            threadWednesday.join();

            int robotsWorld = world.getRobots();
            int robotsWednesday = wednesday.getRobots();

            System.out.println("World has " + robotsWorld + " robots / Wednesday has " + robotsWednesday + " robots");

            if (robotsWorld > robotsWednesday) {
                System.out.println("World WON with robots: " + robotsWorld);
            } else if (robotsWorld < robotsWednesday) {
                System.out.println("Wednesday WON with robots: " + robotsWednesday);
            } else System.out.println("TIE: " + robotsWednesday + " have everyone");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}