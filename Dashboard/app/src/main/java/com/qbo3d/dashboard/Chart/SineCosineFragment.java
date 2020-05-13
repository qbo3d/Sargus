package com.qbo3d.dashboard.Chart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qbo3d.dashboard.Models.Sensor;
import com.qbo3d.dashboard.R;
import com.qbo3d.dashboard.Serv;
import com.qbo3d.dashboard.Util;
import com.qbo3d.dashboard.Vars;

import java.util.List;


public class SineCosineFragment extends SimpleFragment {

    @NonNull
    public static Fragment newInstance() {
        return new SineCosineFragment();
    }

    private Activity act;

    @SuppressWarnings("FieldCanBeLocal")
    private LineChart chart;
    private Typeface tfLight;
    private SensoresPromedioTask task = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_simple_line, container, false);

        act = getActivity();

//        chart = v.findViewById(R.id.lineChart1);
//
//        chart.getDescription().setEnabled(false);
//
//        chart.setDrawGridBackground(false);
//
//        chart.setData(generateLineData());
//        chart.animateX(3000);
//
//        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
//
//        Legend l = chart.getLegend();
//        l.setTypeface(tf);
//
//        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tf);
//        leftAxis.setAxisMaximum(1.2f);
//        leftAxis.setAxisMinimum(-1.2f);
//
//        chart.getAxisRight().setEnabled(false);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setEnabled(false);

        tfLight = Typeface.createFromAsset(act.getAssets(), "OpenSans-Light.ttf");

        chart = view.findViewById(R.id.lineChart1);
//        chart.setOnChartValueSelectedListener(this);

        // enable description text
        chart.getDescription().setEnabled(true);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLUE);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = chart.getXAxis();
        xl.setTypeface(tfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        task = new SensoresPromedioTask();
        task.execute();

        return view;
    }

    private void addEntry(List<Sensor> sensores) {

        double promedio = 0;
        boolean flag;

        for (int i = 0; i < sensores.size(); i++) {

            if (Vars.currentLocation.equals("")) {
                flag = false;
            } else {
                flag = Double.parseDouble(sensores.get(i).getValue1()) < 0 || !sensores.get(i).getLocation().equals(Vars.currentLocation);
            }

            if (flag) {
                sensores.remove(i);
                i--;
            } else {
                promedio += Double.parseDouble(sensores.get(i).getValue1());
            }
        }

        promedio = promedio / sensores.size();

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
//            data.addEntry(new Entry(set.getEntryCount(), (float) (promedio + Math.random() * 5)), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float) (promedio)), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(10);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, Vars.currentLocation);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

//    @Override
//    public void onValueSelected(Entry e, Highlight h) {
//        Log.i("Entry selected", e.toString());
//    }
//
//    @Override
//    public void onNothingSelected() {
//        Log.i("Nothing selected", "Nothing selected.");
//    }

    public class SensoresPromedioTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("Sargus", Vars.currentLocation);
            Vars.isConn = Serv.getSensoresValoresPL(Vars.currentProject);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success && Vars.isConn) {
                if (Vars.sensores != null) {
                    if (Util.isConnect(act, act.getLocalClassName())) {
                        try {
                            Thread.sleep(1000);
                            task = new SensoresPromedioTask();
                            task.execute();
                            addEntry(Vars.sensores);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Util.disconnectMessage(act, act.getLocalClassName());

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        task.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (task.isCancelled()) {
            task = new SensoresPromedioTask();
            task.execute();
        }
    }
}
