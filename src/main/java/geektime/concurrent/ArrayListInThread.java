package geektime.concurrent;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;

public class ArrayListInThread implements Runnable {
    List<String> testList = new ArrayList<String>();// not thread safe
//    List<String> testList = Collections.synchronizedList(new ArrayList<String>());// thread safe

    public void run() {
        try {
            Thread.sleep((int)(Math.random() * 2));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        testList.add(Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group = new ThreadGroup("testgroup");
        ArrayListInThread t = new ArrayListInThread();
        for (int i = 0; i < 10000; i++) {
            Thread th = new Thread(group, t, String.valueOf(i));
            th.start();
        }
      
        while (group.activeCount() > 0) {
            Thread.sleep(10);
        }
        System.out.println();
        System.out.println(t.testList.size()); // it should be 10000 if thread safe collection is used.
    }
}
