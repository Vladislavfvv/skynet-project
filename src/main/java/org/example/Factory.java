package org.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Factory implements Runnable {
    private final Queue<PartType> queue = new ConcurrentLinkedQueue<>();
    private final Random random = new Random();

    private final CyclicBarrier barrierStart;
    private final CyclicBarrier barrierEnd;

    public Factory(CyclicBarrier barrierStart, CyclicBarrier barrierEnd) {
        this.barrierStart = barrierStart;
        this.barrierEnd = barrierEnd;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            System.out.println("\n" + "********* " + i + " day started ***********");
            produceParts(i);
            System.out.println("Night started");

            try {
                barrierStart.await();//ожидание фракции (начала ночи)
                barrierEnd.await();//ожидание завершения полного цикла(т.е. ночи)
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void produceParts(int day) {
        queue.clear(); // очищаем склад перед новым днём
        int newNumberOfParts = random.nextInt(11); //до 10 деталей

        for (int i = 0; i < newNumberOfParts; i++) {
            PartType newPart = PartType.values()[random.nextInt(PartType.values().length)];
            queue.offer(newPart);
        }

        System.out.println("Factory build " + newNumberOfParts + " parts / all in storage: " + queue.size());
    }

    public List<PartType> collectParts(int countParts) {
       List<PartType> shuffled = new ArrayList<>(queue);
       Collections.shuffle(shuffled, random);

        List<PartType> partTypeList = new ArrayList<>();
        for (int i = 0; i < countParts && i < shuffled.size(); i++) {
            PartType newPart = shuffled.get(i);
            if(queue.remove(newPart))
                partTypeList.add(newPart);
        }
        return partTypeList;
    }
}
