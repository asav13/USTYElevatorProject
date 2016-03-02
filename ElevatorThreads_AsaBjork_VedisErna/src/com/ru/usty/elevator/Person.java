package com.ru.usty.elevator;

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

        // H�kkar fj�ldann � r��inni
        try {
            ElevatorScene.scene.incNumberOfPeopleWaitingAtFloor(src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // Bidur � r�� � h��inni

            (ElevatorScene.scene.waitingQueue.get(this.src)).acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // eg fekk waitingQueue[src] semaf�runa

        try {
            ElevatorScene.scene.decNumberOfPeopleWaitingAtFloor(src);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ElevatorScene.scene.incNumberOfPeopleInElevator(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ((ElevatorScene.scene.waitInElevator.get(0))[dest]).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // i am inside elevator
        System.out.println("I AM INSIDE ");

        /*Védís*/
                    try {
                        ElevatorScene.scene.getOutOfElevator.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
        /*Védís*/

        // wait to get out
        System.out.println("I WAS LET OUT ");

        try {
            ElevatorScene.scene.decNumberOfPeopleInElevator(0);
            ElevatorScene.scene.personExitsAtFloor(dest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // After that we know we're in it, Do something about us being inside the
        // elevator, ++

        // acquire some semaphore for waiting in the elev.

        // then we know we're outside it

    }
}
