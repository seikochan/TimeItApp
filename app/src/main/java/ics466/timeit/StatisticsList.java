package ics466.timeit;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.alamkanak.weekview.WeekViewEvent;

        import org.w3c.dom.Text;

        import java.math.BigInteger;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Timer;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.concurrent.TimeUnit;

public class StatisticsList extends ListActivity{
    List<TimeItActivity> activityArrList = new ArrayList<TimeItActivity>();
    Button btnStatsClose;
    Spinner timeSpinner;
    TimeMeas timeMeasurement;

    enum TimeMeas  {
        LAST_DAY,
        LAST_WEEK,
        LAST_MONTH
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_popup_main);

        // add button listener
        btnStatsClose = (Button) findViewById(R.id.btn_stats_close);
        btnStatsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // add items to spinner
        timeSpinner = (Spinner) findViewById(R.id.time_unit_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.timeMeasurement,R.layout.spinner_item);
        timeSpinner.setAdapter(adapter);
        setUpSpinnerEvents();


        timeMeasurement = TimeMeas.LAST_DAY;

        populateStatisticsList();
        populateListView();
    }


    private void populateStatisticsList() {

        Iterator iter = MainActivity.getActivityMap().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            System.out.print("FOUND: " + pair.getKey() + "-> ");
            List<Event> eventList = (List<Event>) pair.getValue();

            // add up all the times of same events
            // ie.  if there are 5 "Sleeping" events, add up the total times
            long totTime = 0;
            for (int i = 0; i < eventList.size(); i++) {
                System.out.print(eventList.get(i).getId() + " - ");
                long eventTime = eventList.get(i).getEndTime() - eventList.get(i).getStartTime();
                totTime = totTime + eventTime;
            }
            TimeItActivity act = new TimeItActivity((String) pair.getKey(), totTime);
            activityArrList.add(act);
            System.out.println("!!!!!!!!!!!!!!!!!!!");
        }
    }

    private void populateListView() {

        ListAdapter adapter = new ArrayAdapter<TimeItActivity>(
                StatisticsList.this, R.layout.stats_item, activityArrList) {

            public View getView(int position, View convertView, ViewGroup parent) {
                //View itemView = convertView;
                final ViewHolder mHolder;

                TextView nameView;
                TextView statView;
                TextView totTimeView;
                TextView totUnitTimeView;

                //Ensure we have a view to work with
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.stats_item, parent, false);

                    ////                            //TODO prevent multiple instances of same item in list
                    //                            mHolder = new ViewHolder();
                    //
                    //                            mHolder.mText = (TextView) convertView.findViewById(R.id.actName);
                    //                            mHolder.mStat = (TextView) convertView.findViewById(R.id.intStatVal);

                    //                            convertView.setTag(mHolder);
                }
                //                        else{
                //                            mHolder = (ViewHolder) convertView.getTag();
                //                        }


                //TODO switch on day, week, month, etc

                //Populate list
                //Find activity to work with
                TimeItActivity currAct = activityArrList.get(position);

                //Fill the view
                nameView = (TextView) convertView.findViewById(R.id.actName);
                nameView.setText(currAct.getActName());
                //mHolder.mText.setText(currAct.getActName());

                statView = (TextView) convertView.findViewById(R.id.intStatVal);
                //calculate statistics
            System.out.println("I GOT HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEee");

                long totTime = calculateTotalTimeInTimeSpan(currAct.getActName());
                double timeSpanMillis = 0;
                switch(timeMeasurement){
                    // last 24hours
                    case LAST_DAY:
                        // 86400000secs = 24 hrs
                        timeSpanMillis = 86400000;
                        break;

                    //last 7 days
                    case LAST_WEEK:
                        // 6.048e+8secs = 7 days
                        timeSpanMillis = 6.048e+8;
                        break;

                    //lst 30 days
                    case LAST_MONTH:
                        // 2.63e+9secs = 30 days
                        timeSpanMillis = 2.63e+9;
                        break;

                    default:
                        System.out.println("This should not occur. Error in switch statement of getView().");
                        System.exit(1);
                }

                totUnitTimeView = (TextView) findViewById(R.id.txt_tot_time);
                totUnitTimeView.setText(prettyTimeString((long)timeSpanMillis));

                int stat = (int) ((totTime / timeSpanMillis) * 100);
                statView.setText(Integer.toString(stat) + "%");
                //                        mHolder.mStat.setText(Integer.toString(stat) + "%");

                totTimeView = (TextView) convertView.findViewById(R.id.totTimeVal);
                totTimeView.setText(prettyTimeString(totTime));

                //convertView.setClickable(false);
                //TODO make the listView items unclickable (this no work :( )

                return (convertView);
            }

            // helper method to return a time in the form "D day, H hour, M min, S sec"
            private String prettyTimeString(long ms){
                String prettyTime = String.format("%d day, %d hr, %d min, %d sec",
                        TimeUnit.MILLISECONDS.toDays(ms),
                        TimeUnit.MILLISECONDS.toHours(ms) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(ms)),
                        TimeUnit.MILLISECONDS.toMinutes(ms) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                        TimeUnit.MILLISECONDS.toSeconds(ms) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))
                );
                return (prettyTime);
            }

        };

        ListView list = getListView();
        list.setAdapter(adapter);
    }

    // helper method to set Listeners on spinner
    private void setUpSpinnerEvents(){
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView txtView = (TextView) view;
                System.out.println("Item " + txtView.getText() + " was selected!!!!!");
                String[] strArr = getResources().getStringArray(R.array.timeMeasurement);
                // case "last 24 hours"
                if(txtView.getText().equals(strArr[0])){
                    System.out.println("LAST 24 HRS selected!!!!!!");
                    timeMeasurement = TimeMeas.LAST_DAY;
                }

                // case "last 7 days"
                if(txtView.getText().equals(strArr[1])){
                    System.out.println("LAST 7 days selected!!!!!!");
                    timeMeasurement = TimeMeas.LAST_WEEK;
                }

                // case "last 30 days"
                if(txtView.getText().equals(strArr[2])){
                    System.out.println("LAST 30 DAYS selected!!!!!!");
                    timeMeasurement = TimeMeas.LAST_MONTH;
                }

                // need to update list with new statistics based on user selection
                populateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
                Toast.makeText(StatisticsList.this, "WHEN DOES NOTHING HAPPEN?",Toast.LENGTH_SHORT).show();
            }
        });
    }



    // helper method to calculate the total time for each activity within a certain timespan form given day
    private long calculateTotalTimeInTimeSpan(String actName){
        Calendar today = Calendar.getInstance();
        long totTimeForSpan = 0;

        // returns a list of all events of this activity (ie. returns a list of all instances of a "Sleeping" activity
        List<Event> actEvents = MainActivity.getActivityMap().get(actName);
        Iterator iter = actEvents.iterator();

        // go through each event and see if it is within the time frame we are getting statistics for
        // remember, time frame is relative to when this method was called
        while(iter.hasNext()){
            Event actEvent = (Event) iter.next();

            Calendar fromDate = (Calendar) today.clone();
            switch(timeMeasurement){
                // last 24hours
                case LAST_DAY:
                    fromDate.add(Calendar.HOUR,-24);
                    break;

                case LAST_WEEK:
                    fromDate.add(Calendar.DAY_OF_MONTH,-7);
                    break;

                case LAST_MONTH:
                    fromDate.add(Calendar.MONTH,-1);
                    break;

                default:
                    System.out.println("This should not occur. Error in switch statment of calculateTotalTimeInTimeSpan().");
                    System.exit(1);
            }

            // if event startTime is between todays date and a 24hrs/7days/month ago, add it

            if( (actEvent.getStartTime() > fromDate.getTimeInMillis()) && (actEvent.getStartTime() < today.getTimeInMillis()) ) {
                Calendar thisEvent = Calendar.getInstance();
                thisEvent.setTimeInMillis(actEvent.getStartTime());


                System.out.println("Event on " +  thisEvent.toString() + " is between: " + fromDate.toString() + " and " + today.toString());
                totTimeForSpan = totTimeForSpan + actEvent.getDuration();
            }
        }

        return (totTimeForSpan);
    }

    //helper class and cache mechanism that stores Views
    private  class ViewHolder {
        private TextView mText;
        private TextView mStat;

    }
}
