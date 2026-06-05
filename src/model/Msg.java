package model;

public class Msg{
    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    String t;
    long time;
    public Msg(String t, long time) {
        this.t = t;
        this.time = time;
    }
}