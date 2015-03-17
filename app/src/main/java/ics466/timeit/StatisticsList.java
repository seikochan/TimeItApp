package ics466.timeit;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Iterator;
import java.util.Map;

public class StatisticsList extends ListActivity {
    List<TimeItActivity> activityArrList = new ArrayList<TimeItActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_popup_main);

        populateStatisticsList();
        populateListView();
    }


    private void populateStatisticsList(){

        Iterator iter = MainActivity.getActivitiesMap().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry pair = (Map.Entry)iter.next();
            List<WeekViewEvent> eventList = (List<WeekViewEvent>) pair.getValue();

            TimeItActivity act = new TimeItActivity((String)pair.getKey(), (long)0);

            for(int i=0;i<eventList.size();i++){
                long totTime = eventList.get(i).getEndTime().getTimeInMillis() - eventList.get(i).getStartTime().getTimeInMillis();
                act.setTimeTotal(act.getTimeTotal() + totTime);
                activityArrList.add(act);
            }

        }
    }

    private void populateListView(){
        ArrayAdapter<TimeItActivity> adapter = new MyListAdapter();
        ListView list = getListView();
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<TimeItActivity> {
        public MyListAdapter() {
            super(StatisticsList.this, R.layout.stats_item, activityArrList);

        }

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
            nameView.setText(stat);



            return (itemView);
        }
    }
}