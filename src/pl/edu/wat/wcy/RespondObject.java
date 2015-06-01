package pl.edu.wat.wcy;

/**
 * Created by wiciu on 30.05.15.
 */
class RespondObject {
    private long delta;
    private long time;

    public RespondObject(long delta, long time) {
        this.delta = delta;
        this.time = time;
    }

    public long getDelta() {
        return delta;
    }

    public long getTime() {
        return time;
    }
}
