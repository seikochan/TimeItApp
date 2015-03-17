/**
 * Created by seikochan on 3/16/15.
 */
public class Activity {
    private String actName;
    private long timeStart;
    private long timeEnd;

    public Activity(){
        actName = "";
        timeStart= -1;
        timeEnd = -1;
    }

    public Activity(String name, long start, long end){
        //error checking for start before end?

    }

    public void setActName(String name){
        actName = name;
    }

    public String getActName(){
        return actName;
    }

    public void setTimeStart(long start){
        timeStart = start;
    }

    public void setTimeEnd(long end){
        timeEnd = end;
    }

    public long getTimeStart(){
        return timeStart;
    }

    public long getTimeEnd(){
        return timeEnd;
    }
}
