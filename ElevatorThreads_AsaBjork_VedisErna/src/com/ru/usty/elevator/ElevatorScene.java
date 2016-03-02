package com.ru.usty.elevator;

import sun.plugin2.applet.SecurityManagerHelper;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {
	public static final int VISUALIZATION_WAIT_TIME = 1500;  //milliseconds

	public static ElevatorScene scene;
	private int numberOfFloors;
	private int numberOfElevators;

	public ArrayList<Semaphore[]> waitInElevator; // nr index, for floor number secondInde

	public ArrayList<Semaphore> waitingQueue;

    public ArrayList<Integer> currFloor;

	ArrayList<Thread> elevators = new ArrayList<>();
	ArrayList<Integer> exitedCount = null;
	public static Semaphore exitedCountMutex;
    public static Semaphore currFloorMutex;
    public static Semaphore peopleInElevatorMutex;
    public static Semaphore peopleWaitingOnFloorMutex;

	public Semaphore getOutOfElevator;


	ArrayList<Integer> peopleInElevator;
	ArrayList<Integer> peopleWaitingOnFloor;

	public ElevatorScene() {
		waitInElevator = new ArrayList<Semaphore[]>();
		waitingQueue = new ArrayList<Semaphore>();
		scene = this;
        currFloor = new ArrayList<>();
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		/**
		 * Important to add code here to make new threads that run your elevator-runnables
		 * Also add any other code that initializes your system for a new run
		 * If you can, tell any currently running elevator threads to stop
		 */

		for(int i = 0; i < numberOfElevators; i++){
			elevators.add(new Thread(new Elevator(i)));
			elevators.get(i).start();
		}
		waitInElevator.clear();
		for(int i = 0;i < numberOfFloors; i++){
			// TODO ATH MED AD NYTA PLASS
			Semaphore[] semForDestFloor = new Semaphore[numberOfFloors];
			for(int j = 0; j < numberOfFloors; j++){
				semForDestFloor[j] = new Semaphore(6);
			}
			waitInElevator.add(semForDestFloor);
		}


		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		peopleWaitingOnFloor = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.peopleWaitingOnFloor.add(0);
			//this.waitingQueue.add(new Semaphore(0));
            this.waitingQueue.add(new Semaphore(1));
		}


		peopleInElevator = new ArrayList<Integer>();
		for(int i = 0; i < numberOfElevators; i++) {
			this.peopleInElevator.add(0);
            this.currFloor.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex            = new Semaphore(1);
        currFloorMutex              = new Semaphore(1);
        peopleInElevatorMutex       = new Semaphore(1);
        peopleWaitingOnFloorMutex   = new Semaphore(1);

		/*Védís*/	getOutOfElevator = new Semaphore(0);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
		thread.start();
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */

		//dumb code, replace it!
		//	peopleWaitingOnFloor.set(sourceFloor, peopleWaitingOnFloor.get(sourceFloor) + 1);
		return thread;  //this means that the testSuite will not wait for the threads to
		// finish

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
        try {
            peopleInElevatorMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int r = peopleInElevator.get(elevator);
        peopleInElevatorMutex.release();
        return r;
	}

	public void incNumberOfPeopleInElevator(int elevator) throws InterruptedException {
        peopleInElevatorMutex.acquire();
		    peopleInElevator.set(elevator, (peopleInElevator.get(elevator) + 1));
        peopleInElevatorMutex.release();
	}

	public void decNumberOfPeopleInElevator(int elevator) throws InterruptedException {
        peopleInElevatorMutex.acquire();
		    peopleInElevator.set(elevator,(peopleInElevator.get(elevator)-1));
        peopleInElevatorMutex.release();
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {
        try {
            peopleWaitingOnFloorMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        peopleWaitingOnFloorMutex.release();
        return peopleWaitingOnFloor.get(floor);
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

	public boolean keepOpen(int elevator, int floor){
		return getNumberOfPeopleWaitingAtFloor(floor) > 0;
	}


    public void personExitsAtFloor(int floor) {
        try {

            exitedCountMutex.acquire();
            exitedCount.set(floor, (exitedCount.get(floor) + 1));
            exitedCountMutex.release();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getExitedCountAtFloor(int floor) {
        if(floor < getNumberOfFloors()) {
            return exitedCount.get(floor);
        }
        else {
            return 0;
        }
    }

}
