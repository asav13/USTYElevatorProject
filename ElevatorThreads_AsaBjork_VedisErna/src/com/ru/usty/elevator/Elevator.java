package com.ru.usty.elevator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Elevator implements Runnable{

    private int     currFloor;
    private int     index;
    private boolean goingUp;

    private Set<Integer> letPeopleInAt;

    public Elevator(int index) {
        this.index      = index;
        this.currFloor  = 0;    // Starts on first floor
        this.goingUp    = true; // Starts by going up
        this.letPeopleInAt = new HashSet<>();
    }

    @Override
    public void run() {

        while(true){
            elevatorSleep();    // Visualization

            // Let people out
            int counter = ElevatorScene.scene.getNumberOfPeopleInElevator(index);
            // If there is someone in the elevator
            if(counter > 0){
                // And while there is someone in that elevator that wants to get out on this floor

                while(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[currFloor].hasQueuedThreads()
                        && counter > 0){
                    // Let one out at a time
                    ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[currFloor].release(1);
                    counter--;
                    elevatorSleep();    // Visualization
                }
            }

            // Let people in to the elevator
            counter = ElevatorScene.scene.getNumberOfPeopleInElevator(index);
            // If we have space and there is someone waiting on the current floor
            if(ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor) > 0 && counter < ElevatorScene.scene.ELEVATOR_CAPACITY){
                imLettingIn();
                // We let people in while the elevator is not full
                while(counter < ElevatorScene.scene.ELEVATOR_CAPACITY){
                    ElevatorScene.scene.waitToGetInFromFloor.get(currFloor).release(1);
                    counter++;
                    letPeopleInAt.add(currFloor);
                    if(counter < 6) { elevatorSleep(); }    // Visualization, no sleep when full
                }
                ElevatorScene.scene.whoIsLettingInMutex.release();

            }

            switchFloor();      // Move elevator
        }
    }

    /* Helper functions for readability, too many "try catch" blocks */

    private void setFloorInScene(){
        try {
            ElevatorScene.scene.setCurrentFloorForElevator(index,currFloor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void switchTo(int f){
        currFloor = f;
        setFloorInScene();
    }

    private void switchFloor(){
        if(noNeedToMove()){
            return;
        }
        if(goingUp && currFloor < ElevatorScene.scene.getNumberOfFloors()-1 && needToGoUp()){
            currFloor++;
        } else {
            if(currFloor == 0){
                goingUp = true;
                currFloor++;
            } else /*if(needToGoDown())*/ {
                goingUp = false;
                if(needToGoDown()) {
                    currFloor--;
                }
            }
        }
        // SetFloor
        setFloorInScene();
    }

    private void elevatorSleep(){
        try {
            Thread.sleep(ElevatorScene.scene.VISUALIZATION_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean noNeedToMove(){
        for(int f = 0; f < ElevatorScene.scene.getNumberOfFloors(); f++){
            if(ElevatorScene.scene.isButtonPushedAtFloor(f)){
                return false;
            }
        }
        if(ElevatorScene.scene.getNumberOfPeopleInElevator(index) > 0){
            return false;
        }
        return true;
    }

    private boolean needToGoUp(){
        for(int f = currFloor+1; f < ElevatorScene.scene.getNumberOfFloors(); f++){
            if(ElevatorScene.scene.isButtonPushedAtFloor(f)){
                return true;
            }
        }
        for(int f = currFloor+1; f < ElevatorScene.scene.getNumberOfFloors(); f++){
            if(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[f].hasQueuedThreads()){
                return true;
            }
        }
        return false;
    }
    private boolean needToGoDown(){
        for(int f = currFloor-1; f >= 0; f--){
            if(ElevatorScene.scene.isButtonPushedAtFloor(f)){
                return true;
            }
        }
        for(int f = currFloor-1; f >= 0; f--){
            if(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[f].hasQueuedThreads()){
                return true;
            }
        }
        return false;
    }

    private void imLettingIn(){
        try {
            ElevatorScene.scene.whoIsLettingInMutex.acquire();
            ElevatorScene.scene.setWhoIsLettingIn(index);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
