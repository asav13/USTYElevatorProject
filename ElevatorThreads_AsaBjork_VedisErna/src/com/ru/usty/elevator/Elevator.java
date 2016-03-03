package com.ru.usty.elevator;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Elevator implements Runnable{

    private int     currFloor;
    private int     index;
    private boolean goingUp;

    public Elevator(int index) {
        this.index      = index;
        this.currFloor  = 0;    // Starts on first floor
        this.goingUp    = true; // Starts by going up
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
                // We let people in while the elevator is not full
                while(counter < ElevatorScene.scene.ELEVATOR_CAPACITY){
                    ElevatorScene.scene.waitToGetInFromFloor.get(currFloor).release(1);
                    counter++;
                    if(counter < 6) { elevatorSleep(); }    // Visualization, no sleep when full
                }
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

    private void switchFloor(){
        if(goingUp && currFloor < ElevatorScene.scene.getNumberOfFloors()-1){
            currFloor++;
        } else {
            if(currFloor == 0){
                goingUp = true;
                currFloor++;
            } else {
                goingUp = false;
                currFloor--;
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
}
