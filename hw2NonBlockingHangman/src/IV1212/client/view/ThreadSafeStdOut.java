package IV1212.client.view;

public class ThreadSafeStdOut {


    synchronized void print(String msg){
        System.out.print(msg);
    }

    synchronized void println(String msg){
        System.out.println(msg);
    }
}
