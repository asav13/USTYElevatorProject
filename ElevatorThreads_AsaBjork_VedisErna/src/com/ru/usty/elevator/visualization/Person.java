package com.ru.usty.elevator.visualization;

import com.ru.usty.elevator.ElevatorScene;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Person implements Runnable{

    int src;
    int dest;

    public Person(int src, int dest){
        this.src = src;
        this.dest = dest;
    }
    @Override
    public void run() {



    }
}
