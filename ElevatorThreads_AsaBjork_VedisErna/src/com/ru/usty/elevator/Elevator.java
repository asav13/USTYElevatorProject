package com.ru.usty.elevator;

import javax.lang.model.element.ElementVisitor;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Elevator implements Runnable{

    public int floor;
    private ElevatorScene scene;
    public int index;
    private boolean goingUp;

    public Elevator(int index) {
        this.floor = 0;
        this.index = index;

    }

    @Override
    public void run() {
        //h��a integer stj�rna l�ppu

        int counter = 0;
        while(counter < 1) {

            int i = 0;

            //for (int i = 0; i < ElevatorScene.scene.getNumberOfFloors(); i++) {
            while(true){
                // SET
                try {
                    ElevatorScene.scene.setCurrentFloorForElevator(index,i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //SLEEP
                try {
                    Thread.sleep(ElevatorScene.scene.VISUALIZATION_WAIT_TIME + index * 250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // LET PPL OUT //....
                while(ElevatorScene.scene.waitInElevator.get(index)[i].availablePermits() < 6){
                    //then someone in the elevator wants to get out
                    System.out.println("Releasing on floor " + i);
                    ElevatorScene.scene.waitInElevator.get(index)[i].release();
                    /*Védís*/    ElevatorScene.scene.getOutOfElevator.release();
                }

                // LET PPL IN
                // for each floor if it has ppl
                if (ElevatorScene.scene.keepOpen(index, i)) {
                //we release queueu semaphore
                    while(ElevatorScene.scene.getNumberOfPeopleInElevator(index) < 6 &&
                            ElevatorScene.scene.keepOpen(index, i)){
                        ElevatorScene.scene.waitingQueue.get(i).release();
                    }
                }

                if(goingUp){
                    if(i == ElevatorScene.scene.getNumberOfFloors()-1){
                        goingUp = false;
                        i--;
                    }else {
                        i++;
                    }
                }else{
                    if(i == 0){
                        goingUp = true;
                        i++;
                    }else {
                        i--;
                    }
                }
            }
        }
    }
}
