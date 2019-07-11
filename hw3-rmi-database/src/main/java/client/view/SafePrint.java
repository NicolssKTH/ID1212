package client.view;

public class SafePrint {

    synchronized void print(String out){
        System.out.print(out);
    }

    synchronized void println(String out){
        System.out.println(out);
    }
}
