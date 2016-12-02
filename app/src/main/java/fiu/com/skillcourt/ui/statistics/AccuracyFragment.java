package fiu.com.skillcourt.ui.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fiu.com.skillcourt.R;
import fiu.com.skillcourt.ui.base.BaseFragment;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * Created by April Perry
 */

public class AccuracyFragment extends BaseFragment {

    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;

    ArrayList<Long> datesList = new ArrayList<Long>();
    ArrayList<Long> accuracyList = new ArrayList<Long>();

    public static AccuracyFragment newInstance() {
        return new AccuracyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accuracy, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        datesList.clear();
        accuracyList.clear();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users/" + user.getUid()).child("games");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) postSnapshot.getValue();

                    Object date = map.get((Object) "date");
                    datesList.add((Long) date);

                    Object accuracy = map.get((Object) "accuracy");
                    accuracyList.add((Long) accuracy);

                }
                createLineGraph(accuracyList, datesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        
    }

    protected void createLineGraph(ArrayList<Long> accuracyList, ArrayList<Long> datesList)
    {

        if ( (datesList.size() == 0) || (accuracyList.size() == 0) ) {
            return;
        }
        LineChart chart = (LineChart) getView().findViewById(R.id.chart);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // Entry = x axis, y axis
        ArrayList<Entry> vals = new ArrayList();
        for (int i=0; i < accuracyList.size(); i++) {
            float f1 = (float) i;
            if (accuracyList.get(i) != 0) {
                vals.add(new Entry(f1, Float.parseFloat(accuracyList.get(i).toString())));
            } else {
                vals.add(new Entry(f1, 0));
            }
        }

        // create data set out of these values
            LineDataSet dataSet = new LineDataSet(vals, "Accuracy");
        LineData lineData = new LineData(dataSet);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // do some styling/formatting
        dataSet.setFillAlpha(80);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.GRAY);
        dataSet.setFillColor(Color.GREEN);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);

        // set data and create chart
        chart.setData(lineData);

        // styling of lines and grid
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        xAxis.setGranularity(1f);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // set bottom axis values
        String[] values = new String[datesList.size()];
        for (int j=0; j < datesList.size(); j++) {
                Date date = new Date(datesList.get(j));
                values[j] = simpleDateFormat.format(date).toString().substring(5,7) +"/" + simpleDateFormat.format(date).toString().substring(8,10);
        }

        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        chart.animateXY(3000, 3000);
        chart.invalidate(); // refresh
    }


    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
