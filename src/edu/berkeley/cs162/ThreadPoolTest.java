package edu.berkeley.cs162;

import static org.junit.Assert.*;
import java.io.Serializable;
import org.junit.Test;

public class ThreadPoolTest {
    
    public static void runTest() {
	ThreadPool test = new ThreadPool(10);
	Runnable r = new Runnable() {
		public void run() {
		    System.out.println("t");
		}
	    };
	    Runnable r2 = new Runnable() {
		    public void run() {
			System.out.println("h");
		    }
		};
	    Runnable r3 = new Runnable() {
		    public void run() {
			System.out.println("e");
		    }
		};
	    try {
		test.addToQueue(r);
	    } catch (InterruptedException exception) {
		System.out.println(exception);
	    }
	    try { 
		test.addToQueue(r2);
	    } catch (InterruptedException exception) {
		System.out.println(exception);
	    }
	    try {
		test.addToQueue(r3);
	    } catch (InterruptedException exception) {
		System.out.println(exception);
	    }
	test.threads[0].run();
	test.threads[1].run();
	test.threads[2].run();
	assertTrue(test.queueOfTasks.size() == 0);
    }

    public static void main(String args[]) {
	runTest();
    }
}
