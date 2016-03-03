package com.ru.usty.elevator;

/**
 * @author  Asa Bjork Valdimarsdottir   - asav13@ru.is
 *          Vedis Erna Eyjolfsdottir    - vedise13@ru.is
 * @since 01-Mar-16.
 */
public class Person implements Runnable{

    private int     src;
    private int     dest;
    private int     elevatorIndex;

    public Person(int src, int dest){
        this.src            = src;
        this.dest           = dest;
        this.elevatorIndex  = 0;
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
        findWhoLetMeIn();

        // Increment people in elevator and decrement people waiting on floor
        incInElevator();
        decWaiting();

        // Wait to get out
        try {
            ElevatorScene.scene.waitToGetOutOfElevatorToFloor.get(this.elevatorIndex)[this.dest].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // When we're here it means we've gotten out of the elevator to our floor
        // Decrement people in elevator and increment people outside of it
        decInElevator();
        incOutside();
    }

    /* Helper functions for readability, too many "try catch" blocks */

    private void incWaiting(){
        try {
            ElevatorScene.scene.incNumberOfPeopleWaitingAtFloor(this.src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void decWaiting(){
        try {
            ElevatorScene.scene.decNumberOfPeopleWaitingAtFloor(this.src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void incInElevator(){
        try {
            ElevatorScene.scene.incNumberOfPeopleInElevator(this.elevatorIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void decInElevator(){
        try {
            ElevatorScene.scene.decNumberOfPeopleInElevator(this.elevatorIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void incOutside(){
        ElevatorScene.scene.personExitsAtFloor(this.dest);
    }

    private void findWhoLetMeIn() {
        while(true) {
            try {
                this.elevatorIndex = ElevatorScene.scene.getWhoIsLettingInAtFloor(this.src);
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
