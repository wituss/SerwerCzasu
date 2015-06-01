package pl.edu.wat.wcy;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

/**
 * Created by wiciu on 30.05.15.
 */
public class SetTimeTask implements Callable<Boolean> {
    BufferedReader in;
    PrintWriter out;
    private long delta;

    public SetTimeTask(BufferedReader in, PrintWriter out, long delta) {
        this.in = in;
        this.out = out;
        this.delta = delta;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("UPDATE_TIME: " + (System.currentTimeMillis() + delta));
        out.println("UPDATE_TIME: " + (System.currentTimeMillis() + delta));
        String isOK = in.readLine();
        if(isOK != null && isOK.equals("OK")){
            System.out.println("OK");
            return true;
        }
        System.out.println("FALSE!!!!");
        return false;
    }
}
