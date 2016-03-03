package com.ru.usty.elevator;

/**
 * @author Asa Bjork Valdimarsdottir - asav13@ru.is
 * @since 01-Mar-16.
 */
public class Person implements Runnable{

    private int src;
    private int dest;

    public Person(int src, int dest){
        this.src = src;
        this.dest = dest;
    }
    @Override
    public void run() {
        // Increment count of people waiting on source floor
        incWaiting();

        // Wait for a spot in an elevator
        try {
            ElevatorScene.scene.waitToGetInFromFloor.get(this.src).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // When we are here it means we've gotten inside an elevator
        // Increment people in elevator and decrement people waiting on floor
        incInElevator();
        decWaiting();

        // Wait to get out
        //  TODO fix HARDCOEDED ELEVATOR index!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        try {
            ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(0)[dest].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // When we're here it means we've gotten out of the elevator to our floor
        // Decrement people in elevator and increment people outside of it
        decInElevator();
        incOutside();

        // Then we need to kill the thread...right?
    }

    /* Helper functions for readability, too many "try catch" blocks */

    private void incWaiting(){
        try {
            ElevatorScene.scene.incNumberOfPeopleWaitingAtFloor(src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void decWaiting(){
        try {
            ElevatorScene.scene.decNumberOfPeopleWaitingAtFloor(src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void incInElevator(){
        try {
            ElevatorScene.scene.incNumberOfPeopleInElevator(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void decInElevator(){
        try {
            ElevatorScene.scene.decNumberOfPeopleInElevator(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void incOutside(){
        ElevatorScene.scene.personExitsAtFloor(dest);
    }
}
