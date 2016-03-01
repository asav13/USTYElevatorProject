package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Elevator implements Runnable{

    public int floor;
    public Semaphore freeSpace;
    private ElevatorScene scene;

    public Elevator() {
        int floor = 0;
        freeSpace = new Semaphore(6);
    }

    @Override
    public void run() {
        System.out.println("Hello i am an evleflelve");

    }
}
