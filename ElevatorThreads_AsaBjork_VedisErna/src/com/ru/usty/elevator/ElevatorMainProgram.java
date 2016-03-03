package com.ru.usty.elevator;

import com.ru.usty.elevator.visualization.TestSuite;

public class ElevatorMainProgram {
	public static void main(String[] args) throws InterruptedException {

		try {

			TestSuite.startVisualization();

/***EXPERIMENT HERE BUT THIS WILL BE CHANGED DURING GRADING***/

			Thread.sleep(1000);

			//TestSuite.runTest(4);

			Thread.sleep(2000);

			for(int i = 0; i <= 9; i++) {
				TestSuite.runTest(i);
			Thread.sleep(1500);
	}

/*************************************************************/

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR in main program.");
			e.printStackTrace();
		}
		
		//System.exit(0);
	}
}
