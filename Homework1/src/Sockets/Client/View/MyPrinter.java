package Sockets.Client.View;

/**
 * class for handling thread output
 */
class MyPrinter {
    synchronized void print(String output){
        System.out.print(output);
    }
    synchronized void println(String output){
        System.out.println(output);
    }
}
