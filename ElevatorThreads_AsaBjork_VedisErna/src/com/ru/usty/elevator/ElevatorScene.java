package com.ru.usty.elevator;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {
    public static final int VISUALIZATION_WAIT_TIME = 10;  // milliseconds
    public static final int ELEVATOR_CAPACITY = 6;    // max six people per elevator

    public static ElevatorScene scene;
    // State variables
    private int numberOfFloors;
    private int numberOfElevators;

    // Elevators and waiting semaphores
    public ArrayList<Thread> elevators;
    public static ArrayList<Semaphore> waitToGetInFromFloor;
    public static ArrayList<Semaphore[]> waitToGetOutOfElevatorToFloor;
    public ArrayList<Integer> currFloor;
    public List<Integer> whoIsLettingInAtFloor;

    // People counters
    ArrayList<Integer> peopleInElevator;
    ArrayList<Integer> peopleWaitingOnFloor;
    public ArrayList<Integer> exitedCount;

    /* MUTEXES */
    public static Semaphore currFloorMutex;
    public static Semaphore exitedCountMutex;
    public static Semaphore peopleInElevatorMutex;
    public static Semaphore peopleWaitingOnFloorMutex;
    public static Semaphore whoIsLettingInMutex;

    public ElevatorScene() {
        this.scene = this;

        this.elevators = new ArrayList<>();
        this.waitToGetInFromFloor = new ArrayList<>();
        this.waitToGetOutOfElevatorToFloor = new ArrayList<Semaphore[]>();
        this.currFloor = new ArrayList<>();
        this.whoIsLettingInAtFloor = new ArrayList<>();

        this.peopleInElevator = new ArrayList<Integer>();
        this.exitedCount = new ArrayList<Integer>();
        this.peopleWaitingOnFloor = new ArrayList<Integer>();
    }

    //Base function: definition must not change
    //Necessary to add your code in this one
    public void restartScene(int numberOfFloors, int numberOfElevators) {

        for (Thread thread : elevators) {
            if (thread != null) {
                if (thread.isAlive()) {
                    thread.stop();
                }
            }
        }

		/* State variables*/
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;

		/* Elevtor threads and information */
        this.elevators.clear();
        this.peopleInElevator.clear();
        this.currFloor.clear();
        for (int i = 0; i < numberOfElevators; i++) {
            this.elevators.add(new Thread(new Elevator(i)));
            this.elevators.get(i).start();

            this.peopleInElevator.add(0);
            this.currFloor.add(0);
        }

		/* Semaphores for waiting for an elevator */
        this.waitToGetInFromFloor.clear();
        this.whoIsLettingInAtFloor.clear();
        for (int i = 0; i < numberOfFloors; i++) {
            this.waitToGetInFromFloor.add(new Semaphore(0));
            this.whoIsLettingInAtFloor.add(0);// ?!?!?!?!? TODO
        }

		/* Semaphores for waiting to get out of  an elevator */
        this.waitToGetOutOfElevatorToFloor.clear();
        for (int i = 0; i < numberOfElevators; i++) {
            Semaphore[] curr = new Semaphore[numberOfFloors];
            for (int j = 0; j < numberOfFloors; j++) {
                curr[j] = new Semaphore(0);
            }
            this.waitToGetOutOfElevatorToFloor.add(curr);
        }

		/* MUTEXES */
        this.exitedCountMutex = new Semaphore(1);
        this.currFloorMutex = new Semaphore(1);
        this.peopleInElevatorMutex = new Semaphore(1);
        this.peopleWaitingOnFloorMutex = new Semaphore(1);
        this.whoIsLettingInMutex = new Semaphore(1);

		/* nr of people on currFloor*/
        this.peopleWaitingOnFloor.clear();
        this.exitedCount.clear();
        for (int i = 0; i < numberOfFloors; i++) {
            this.peopleWaitingOnFloor.add(0);
            this.exitedCount.add(0);
        }

    }

    //Base function: definition must not change
    //Necessary to add your code in this one
    public Thread addPerson(int sourceFloor, int destinationFloor) {

        Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
        thread.start();
        // The Person class takes care of the rest
        return thread;
    }

    //Base function: definition must not change, but add your code
    public int getCurrentFloorForElevator(int elevator) {
        return currFloor.get(elevator);
    }

    public void setCurrentFloorForElevator(int elevator, int floor) throws InterruptedException {
        currFloorMutex.acquire();
            currFloor.set(elevator, floor);
        currFloorMutex.release();
    }

    //Base function: definition must not change, but add your code
    public int getNumberOfPeopleInElevator(int elevator) {
        int r = 0;
        if (peopleInElevatorMutex == null) {
            peopleInElevatorMutex = new Semaphore(1);
        }
        try {
            peopleInElevatorMutex.acquire();
            r = peopleInElevator.get(elevator);
            peopleInElevatorMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return r;
    }

    public void incNumberOfPeopleInElevator(int elevator) throws InterruptedException {
        peopleInElevatorMutex.acquire();
            peopleInElevator.set(elevator, (peopleInElevator.get(elevator) + 1));
            int r = peopleInElevator.get(elevator);
        peopleInElevatorMutex.release();
        if(r > 6) System.out.println("ERROR: People in elevator " + r);
    }

    public void decNumberOfPeopleInElevator(int elevator) throws InterruptedException {
        peopleInElevatorMutex.acquire();
            peopleInElevator.set(elevator, (peopleInElevator.get(elevator) - 1));
        peopleInElevatorMutex.release();
    }

    //Base function: definition must not change, but add your code
    public int getNumberOfPeopleWaitingAtFloor(int floor) {
        int r = -1;
        if (peopleWaitingOnFloorMutex == null) {
            peopleInElevatorMutex = new Semaphore(1);
        }
        if (!(floor < peopleWaitingOnFloor.size())) {
            return 0;
        }
        try {
            peopleWaitingOnFloorMutex.acquire();
            if (floor > peopleWaitingOnFloor.size()) {
                return 0;
            }
            r = peopleWaitingOnFloor.get(floor);
            peopleWaitingOnFloorMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return r;
    }

    public void incNumberOfPeopleWaitingAtFloor(int floor) throws InterruptedException {
        peopleWaitingOnFloorMutex.acquire();
            peopleWaitingOnFloor.set(floor, (peopleWaitingOnFloor.get(floor) + 1));
        peopleWaitingOnFloorMutex.release();
    }

    public void decNumberOfPeopleWaitingAtFloor(int floor) throws InterruptedException {
        peopleWaitingOnFloorMutex.acquire();
           peopleWaitingOnFloor.set(floor, (peopleWaitingOnFloor.get(floor) - 1));
        peopleWaitingOnFloorMutex.release();
    }

    //Base function: definition must not change, but add your code if needed
    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    //Base function: definition must not change, but add your code if needed
    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    //Base function: definition must not change, but add your code if needed
    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    //Base function: definition must not change, but add your code if needed
    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    //Base function: no need to change unless you choose
    //				 not to "open the doors" sometimes
    //				 even though there are people there
    public boolean isElevatorOpen(int elevator) {
        return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
    }

    //Base function: no need to change, just for visualization
    //Feel free to use it though, if it helps
    public boolean isButtonPushedAtFloor(int floor) {
        return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
    }

    public void personExitsAtFloor(int floor) {
        try {
            exitedCountMutex.acquire();
                exitedCount.set(floor, (exitedCount.get(floor) + 1));
            exitedCountMutex.release();
        } catch (InterruptedException e) {
            System.out.println("MUTEX ERROR: While exiting floor.");
            e.printStackTrace();
        }
    }

    public int getExitedCountAtFloor(int floor) {
        if (floor < getNumberOfFloors()) {
            return exitedCount.get(floor);
        } else {
            return 0;
        }
    }

    public int getWhoIsLettingInAtFloor(int floor) throws Exception {
        return whoIsLettingInAtFloor.get(floor);
    }

    // The caller needs to take care of the MUTEX here, so he can choose
    // when to release it, needs to hold on to it while letting ppl in
    public void setWhoIsLettingInAtFloor( int floor, int index) {
        whoIsLettingInAtFloor.set(floor, index);
    }

}
