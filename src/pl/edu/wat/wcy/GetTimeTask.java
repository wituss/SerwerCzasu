package pl.edu.wat.wcy;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

/**
 * Created by wiciu on 30.05.15.
 */
class GetTimeTask implements Callable<RespondObject> {
    BufferedReader in;
    PrintWriter out;
    String clientName;

    public GetTimeTask(BufferedReader in, PrintWriter out, String name) {
        this.in = in;
        this.out = out;
        this.clientName = name;
    }

    /**
     * Metoda poro
     * @return
     * @throws Exception
     */
    public RespondObject call() throws Exception {
        out.println("GET_TIME");
        long localTime = System.currentTimeMillis();
        String time = in.readLine();
        long ping = System.currentTimeMillis() - localTime;
        long remoteTime = Long.valueOf(time,10);
        remoteTime -= ping / 2;
        long delta = remoteTime - localTime;
        System.out.println("Client [" + clientName + "] respond [" + remoteTime + "] in " + ping + "ms");
        return new RespondObject(delta, remoteTime);
    }
}
