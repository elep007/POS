package com.example.rexpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class SelectDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);

        final CalendarPickerView cal = findViewById(R.id.calendar_view);
        cal.init(
                DateTime.now(DateTimeZone.UTC).minusYears(1).toDate(),
                DateTime.now(DateTimeZone.UTC).plusDays(4).toDate()
        )
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(DateTime.now(DateTimeZone.UTC).toDate());

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Date> temp=cal.getSelectedDates();
                //Log.d("first",temp.get(0).toString());
                //Log.d("last",temp.get(temp.size()-1).toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String startDate = dateFormat.format(temp.get(0));
                String endDate=dateFormat.format(temp.get(temp.size()-1));

                Intent intent=new Intent().putExtra("startDate",startDate)
                                        .putExtra("endDate",endDate);
                setResult(102,intent);
                finish();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(400);
                finish();
            }
        });
    }
}
