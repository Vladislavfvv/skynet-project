package org.example;

import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Fraction implements Runnable {
    private final String name;
    private final Factory factory;
    private final CyclicBarrier barrierStart;
    private final CyclicBarrier barrierEnd;
    private final Map<PartType, Integer> inventory = new EnumMap<>(PartType.class);
    private int robots = 0;

    public Fraction(String name, Factory factory, CyclicBarrier barrierStart, CyclicBarrier barrierEnd) {
        this.name = name;
        this.factory = factory;
        this.barrierStart = barrierStart;
        this.barrierEnd = barrierEnd;
        for (PartType part : PartType.values()) {
            inventory.put(part, 0);//начальная инициализация склада потребителя
        }
    }

    private void collectAndBuild() throws InterruptedException {
        List<PartType> parts = factory.collectParts(5);
        System.out.println(name + " fraction gathered parts " + parts);

        for (PartType part : parts) {
            inventory.put(part, inventory.get(part) + 1);
        }

        int startRobot = robots;

        while (canBuildRobot()) {
            buildRobot();
        }
        System.out.println(name + " built " + (robots - startRobot) + " robots in this day");
        System.out.println(name + " has after night: " + inventory + " and " + robots + " robots");

        try {
            barrierEnd.await();//сигнал фабрике о завершении ночи и цикла
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean canBuildRobot() {
        return inventory.get(PartType.HEAD) >= 1 &&
                inventory.get(PartType.TORSO) >= 1 &&
                inventory.get(PartType.HAND) >= 2 &&
                inventory.get(PartType.FEET) >= 2;
    }

    private void buildRobot() {
        inventory.put(PartType.HEAD, inventory.get(PartType.HEAD) - 1);
        inventory.put(PartType.TORSO, inventory.get(PartType.TORSO) - 1);
        inventory.put(PartType.HAND, inventory.get(PartType.HAND) - 2);
        inventory.put(PartType.FEET, inventory.get(PartType.FEET) - 2);
        robots++;
    }

    @Override
    public void run() {
        for (int day = 1; day <= 100; day++) {
            try {
                barrierStart.await();//ожидание фабрики(наступления ночи)
                collectAndBuild();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getRobots() {
        return robots;
    }
}
