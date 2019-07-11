package java.main.id1212.rps.client.view;

public class ThreadSafeStdOut {
    synchronized void print(String output){
        System.out.print(output);
    }

    synchronized void println(String output){
        System.out.println(output);
    }
}
