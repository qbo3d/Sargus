package com.qbo3d.dashboard.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qbo3d.dashboard.Chart.SimpleFragment;
import com.qbo3d.dashboard.Chart.SineCosineFragment;
import com.qbo3d.dashboard.Models.Sensor;
import com.qbo3d.dashboard.R;
import com.qbo3d.dashboard.Serv;
import com.qbo3d.dashboard.Util;
import com.qbo3d.dashboard.Vars;

import java.util.ArrayList;
import java.util.List;

public class ListViewBarChartFragment extends SimpleFragment {

    private Activity act;
    private ListView lv;
    public static ProgressDialog progressDialog;

    @NonNull
    public static Fragment newInstance() {
        return new SineCosineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_listview_chart, container, false);

        act = getActivity();

        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressDialog = new ProgressDialog(act, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        new SensoresValoresPLTask().execute();

        lv = view.findViewById(R.id.listView1);

        return view;
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        private Typeface tfLight;

        ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            tfLight = Typeface.createFromAsset(act.getAssets(), "OpenSans-Light.ttf");

            BarData data = getItem(position);

            ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = convertView.findViewById(R.id.chart);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            if (data != null) {
                data.setValueTypeface(tfLight);
                data.setValueTextColor(Color.BLACK);
            }
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(tfLight);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(tfLight);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setTypeface(tfLight);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);
            holder.chart.setFitBars(true);

            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart.animateY(700);

            return convertView;
        }

        private class ViewHolder {

            BarChart chart;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private BarData generateData(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < Vars.sensoresLista.get(cnt).size(); i++) {
            entries.add(new BarEntry(i + 1, Float.parseFloat(Vars.sensoresLista.get(cnt).get(i).getValue1())));
        }

        BarDataSet d = new BarDataSet(entries, Vars.sensores.get(cnt).getSensor());
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public class SensoresValoresPLTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Vars.isConn = Serv.getSensoresValoresPL(Vars.currentProject);

            boolean flag;

            for (int i = 0; i < Vars.sensores.size(); i++) {

                if (Vars.currentLocation.equals("")) {
                    flag = false;
                } else {
                    flag = Double.parseDouble(Vars.sensores.get(i).getValue1()) < 0 || !Vars.sensores.get(i).getLocation().equals(Vars.currentLocation);
                }

                if (flag) {
                    Vars.sensores.remove(i);
                    i--;
                }
            }

            if (Vars.isConn) {
                Vars.sensoresLista = new ArrayList<>();
                for (Sensor sensor : Vars.sensores) {
                    List<Sensor> res;
                    res = Serv.getSensoresValoresPLS(Vars.currentProject, sensor.getSensor());
                    Vars.sensoresLista.add(res);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressDialog.dismiss();
            if (success && Vars.isConn) {
                if (Vars.sensoresLista != null) {
                    if (Util.isConnect(act, act.getLocalClassName())) {
                        ArrayList<BarData> list = new ArrayList<>();

                        // 20 items
                        for (int i = 0; i < Vars.sensoresLista.size(); i++) {
                            list.add(generateData(i));
                        }

                        ChartDataAdapter cda = new ChartDataAdapter(act.getApplicationContext(), list);
                        lv.setAdapter(cda);
                    }
                }
            } else {
                Util.disconnectMessage(act, act.getLocalClassName());
            }
        }
    }
}