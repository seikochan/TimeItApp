package ics466.timeit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener{

    private final Context CONTEXT = this;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    private Button btnStatsClose;
    private Button btnStatsOpen;
    private PopupWindow pwStats;

    private List<TimeItActivity> activityArrList;
    private static HashMap<String, List<WeekViewEvent>> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    System.out.println("CREATED MAIN!!!!!!!!!");


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        activityMap = new HashMap<>();

            setStatsDialogBox();
    }

    public void setStatsDialogBox(){

        btnStatsOpen = (Button) findViewById(R.id.btnStatsOpen);

        // add button listener
        btnStatsOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((CONTEXT));
                activityArrList = new ArrayList<TimeItActivity>();

                // create a close button
                builder.setNegativeButton(R.string.close,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create a list adapter for TimeItActivities
                //populate Statistics List
                Iterator iter = MainActivity.getActivitiesMap().entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry pair = (Map.Entry) iter.next();
                    List<WeekViewEvent> eventList = (List<WeekViewEvent>) pair.getValue();

                    TimeItActivity act = new TimeItActivity((String) pair.getKey(), (long) 0);

                    for (int i = 0; i < eventList.size(); i++) {
                        long totTime = eventList.get(i).getEndTime().getTimeInMillis() - eventList.get(i).getStartTime().getTimeInMillis();
                        act.setTimeTotal(act.getTimeTotal() + totTime);
                        activityArrList.add(act);
                    }
                }
                // populate ListView
                ListAdapter adapter = new ArrayAdapter<TimeItActivity>(
                        MainActivity.this, R.layout.stats_item, activityArrList ) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View itemView = convertView;

                        //Ensure we have a view to work with
                        if(itemView == null) {
                            itemView = getLayoutInflater().inflate(R.layout.stats_item, parent, false);
                        }


                        //TODO switch on day, week, month, etc

                        //Populate list
                        //Find activity to work with
                        TimeItActivity currAct = activityArrList.get(position);

                        //Fill the view
                        TextView nameView = (TextView) itemView.findViewById(R.id.actName);
                        nameView.setText(currAct.getActName());

                        TextView statView = (TextView) itemView.findViewById(R.id.intStatVal);
                        //calculate statistics
                        //right now based on 24hrs (86400000 ms)
                        int stat = (int)currAct.getTimeTotal()/86400000;
                        statView.setText(Integer.toString(stat));

                        itemView.setClickable(false);
                        //TODO make the listView items unclickable (this no work :( )

                        return (itemView);
                    }
                };

                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });

                AlertDialog alertDialog = builder.create();

                alertDialog.setTitle("Statistics:");

                alertDialog.show();
            }



        });
    }


        //TODO try to get working with a menu item?
//    private void getPopupWindow(){
//        try{
//            context = StatisticsList.getApplicationContext();
//            //need to somehow pass in appContext from StsisticsList to here....
//
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View layout = inflater.inflate(R.layout.stats_popup_main,(ViewGroup)findViewById(R.id.stats_popup_element));
//            pwStats = new PopupWindow(layout, 700, 700, true);
//            pwStats.showAtLocation(layout, Gravity.CENTER, 0, 0);
//
//        System.out.println("POPUP WINDOW CREATED!!!!!!!!!!!");
//
//            //System.out.println(activityArrList.toString());
//
//            btnStatsClose = (Button) layout.findViewById(R.id.btn_stats_close);
//
//            btnStatsClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pwStats.dismiss();
//                }
//            });
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
//            case R.id.statistics:
//                System.out.println("STATISTICS CLICKED!!!!!!!!!");
//                getPopupWindow();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    //TODO remove once made a userinput interface
    private String[] getUserInput(int index){
        //1 startHrs
        // 2 startMins
        // 3 startMonth
        // 4 int startYear
        // 5 int endHrs
        // 6 endMins
        // 7 endMonth
        // 8 endYear
        // 9 activity Name
        // 10 color

        String[] arr;

        switch(index){
            case 0:
                arr = new String[] {"3","0","0","0","1","0","0","0","1", "Sleep",Integer.toString(R.color.event_color_01)};
                return(arr);
            case 1:
                arr = new String[] {"3","30","0","0","4","30","0","0", "2", "Exercise",Integer.toString(R.color.event_color_02)};
                return(arr);
            case 2:
                arr = new String[] {"4","20","0","0","5","0","0","0", "3", "Socialize",Integer.toString(R.color.event_color_03)};
                return(arr);
            case 3:
                arr = new String[] {"5","0","0","0","3","0","0","0", "3", "Socialize",Integer.toString(R.color.event_color_03)};
                return(arr);
            default:
                System.out.println("SHOULD NOT OCCUR: getUserInput()");
                return null;

        }
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> allEvents = new ArrayList<WeekViewEvent>();

        for(int i=0;i<4;i++){
            String[] userInputArr = getUserInput(i);
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(userInputArr[0]));
            startTime.set(Calendar.MINUTE, Integer.parseInt(userInputArr[1]));
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, Integer.parseInt(userInputArr[4]));
            endTime.set(Calendar.MONTH, newMonth-1);
            WeekViewEvent event = new WeekViewEvent(Integer.parseInt(userInputArr[8]), userInputArr[9], startTime, endTime);
            event.setColor(getResources().getColor(Integer.parseInt(userInputArr[10])));

            if(activityMap.containsKey(userInputArr[9])){
                activityMap.get(userInputArr[9]).add(event);
            }else{
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                events.add(event);
                activityMap.put(userInputArr[9],events);
            }

            allEvents.add(event);
        }
        return allEvents;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    protected static HashMap<String,List<WeekViewEvent>> getActivitiesMap(){
        return activityMap;
    }



// / THIS IS TO START A NEW ACTIVITY, NOT THE SAME AS A POPUP WINDOW
//    /**
//     * Called when user presses "Statistics" button
//     * @param view
//     */
//    public void openStatsWindow(View view) {
//        Intent intent = new Intent(this, StatisticsList.class);
//        startActivity(intent);
//    }
}
