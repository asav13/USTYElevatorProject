package com.ru.usty.elevator;

/**
 * @author  Asa Bjork Valdimarsdottir   - asav13@ru.is
 *          Vedis Erna Eyjolfsdottir    - vedise13@ru.is
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
            int counter = ElevatorScene.scene.getNumberOfPeopleInElevator(this.index);
            // If there is someone in the elevator
            if(counter > 0){
                // And while there is someone in that elevator that wants to get out on this floor
                while(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(this.index)[this.currFloor].hasQueuedThreads()
                        && counter > 0){
                    // Let one out at a time
                    ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(this.index)[this.currFloor].release(1);
                    counter--;
                    elevatorSleep();    // Visualization
                }
            }

            // Let people in to the elevator
            counter = ElevatorScene.scene.getNumberOfPeopleInElevator(index);
            // If we have space and there is someone waiting on the current floor
            if(ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(this.currFloor) > 0
                    && counter < ElevatorScene.scene.ELEVATOR_CAPACITY){
                imLettingIn();
                ElevatorScene.scene.setWhoIsLettingInAtFloor(this.currFloor, this.index);
                // We let people in while the elevator is not full
                while(counter < ElevatorScene.scene.ELEVATOR_CAPACITY){
                    //System.out.println("DEB: I am letting in " + index);
                    ElevatorScene.scene.waitToGetInFromFloor.get(this.currFloor).release(1);

                    counter++;
                    if(counter < 6) { elevatorSleep(); }    // Visualization, no sleep when full
                }
                elevatorSleep(); // viz
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

    private void switchFloor(){
        if(noNeedToMove()){
            return;
        }
        if(this.goingUp && this.currFloor < ElevatorScene.scene.getNumberOfFloors()-1 && needToGoUp()){
            this.currFloor++;
        } else {
            if(this.currFloor == 0){
                this.goingUp = true;
                this.currFloor++;
            } else {
                this.goingUp = false;
                this.currFloor--;
                if(this.currFloor < 0) this.currFloor = 0; // Just safety measures
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
        for(int f = this.currFloor+1; f < ElevatorScene.scene.getNumberOfFloors(); f++){
            if(ElevatorScene.scene.isButtonPushedAtFloor(f)){
                return true;
            }
        }
        for(int f = this.currFloor+1; f < ElevatorScene.scene.getNumberOfFloors(); f++){
            if(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[f].hasQueuedThreads()){
                return true;
            }
        }
        return false;
    }
    private boolean needToGoDown(){
        for(int f = this.currFloor-1; f >= 0; f--){
            if(ElevatorScene.scene.isButtonPushedAtFloor(f)){
                return true;
            }
        }
        for(int f = this.currFloor-1; f >= 0; f--){
            if(ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(index)[f].hasQueuedThreads()){
                return true;
            }
        }
        try {
            Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void imLettingIn(){
        try {
            ElevatorScene.scene.whoIsLettingInMutex.acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
