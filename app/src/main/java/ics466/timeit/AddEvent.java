package ics466.timeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by seikochan on 3/19/15.
 */
public class AddEvent extends Activity {

    private Button btnAddEvent;
    EditText actName;
    Spinner colorSpinner;
    DatePicker startDate;
    TimePicker startTime;
    DatePicker endDate;
    TimePicker endTime;

    int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_picker);

        actName = (EditText) findViewById(R.id.edit_act_name);
        colorSpinner = (Spinner) findViewById(R.id.color_spinner);
        startDate = (DatePicker) findViewById(R.id.startDatePicker);
        startTime = (TimePicker) findViewById(R.id.startTimePicker);
        endDate = (DatePicker) findViewById(R.id.endDatePicker);
        endTime = (TimePicker) findViewById(R.id.endTimePicker);

        // add items to spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(AddEvent.this,R.array.colorArr,R.layout.spinner_item);
        colorSpinner.setAdapter(adapter);

        setUpSpinnerEvents();

        // add button listener
        btnAddEvent = (Button) findViewById(R.id.btn_add);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO error check
                // validate end Date > start Date
                // validate end Time > start Time
                // validate user inputed a activity name

                System.out.println("AddButton Clicked");
                //TODO get this working right
                Intent intent = new Intent();
                String arr[] = {
                        actName.getText().toString(), color+"",
                        startDate.getYear()+"", startDate.getMonth()+"",startDate.getDayOfMonth()+"",startTime.getCurrentHour()+"", startTime.getCurrentMinute()+"",
                        endDate.getYear()+"", endDate.getMonth()+"",endDate.getDayOfMonth()+"",endTime.getCurrentHour()+"", endTime.getCurrentMinute()+""
                };
                System.out.println(Arrays.toString(arr));
                //intent.putExtra("data",arr);
                setResult(RESULT_OK, intent);

                // TODO this is not the right way, fix later
                MainActivity.results = arr;

                finish();
            }


        });



    }

    // helper method to set Listeners on spinner
    private void setUpSpinnerEvents(){
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView txtView = (TextView) view;
                System.out.println("Item " + txtView.getText() + " was selected!!!!!");
                String[] strArr = getResources().getStringArray(R.array.colorArr);
                // case "blue"
                if(txtView.getText().equals(strArr[0])){
                    color = R.color.event_color_01;
                }

                // case
                if(txtView.getText().equals(strArr[1])){
                    color = R.color.event_color_02;
                }

                // case
                if(txtView.getText().equals(strArr[2])){
                    color = R.color.event_color_03;
                }

                // case
                if(txtView.getText().equals(strArr[2])){
                    color = R.color.event_color_04;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
                Toast.makeText(AddEvent.this, "WHEN DOES NOTHING HAPPEN?", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
