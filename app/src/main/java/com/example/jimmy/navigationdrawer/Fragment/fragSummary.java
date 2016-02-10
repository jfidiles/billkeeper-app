package com.example.jimmy.navigationdrawer.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.jimmy.navigationdrawer.APIConnect;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_income;
import com.example.jimmy.navigationdrawer.Authorisation;
import com.example.jimmy.navigationdrawer.Interfaces.onReportTask;
import com.example.jimmy.navigationdrawer.R;
import com.example.jimmy.navigationdrawer.Response.ReportTaskResponse;
import com.example.jimmy.navigationdrawer.StatusCode;
import com.example.jimmy.navigationdrawer.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

/**
 * Created by Jimmy on 12/12/2015.
 */
public class fragSummary extends Fragment implements onReportTask {
    JSONArray jsonArray;
    JSONObject today, oneMonth, threeMonths, sixMonths, year;
    private PieChart pieChart;
    private float[] pieItemDimension = {1, 5, 12, 24, 48};
    private final String[] pieItemName = {"Today", "One month", "Three months", "Six months", "Last Year"};
    FrameLayout mainLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.summary_bar_chart, container, false);
        android.support.design.widget.FloatingActionButton fabFrag;
        fabFrag = (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (fabFrag.getVisibility() == View.VISIBLE) {
            fabFrag.setVisibility(View.GONE);
        }
        //Show custom FAB
        FloatingActionsMenu fab = (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        fab.setVisibility(View.VISIBLE);
        FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.action_a);
        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.action_b);
        //btn1
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                startActivity(intent);
            }
        });
        //btn2
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_income.class);
                startActivity(intent);
            }
        });

        mainLayout = (FrameLayout) rootView.findViewById(R.id.PieChart);
        pieChart = new PieChart(getActivity());
        loadSummary();
        //new GetSummaryTask().execute();

        return rootView;
    }

    private void loadSummary() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetSummaryTask getSummaryTask = new APIConnect.GetSummaryTask();
        getSummaryTask.delegate = this;
        getSummaryTask.execute();
    }

    @Override
    public void getSummary(ReportTaskResponse summaryRespone) {
        HttpStatus code = summaryRespone.code;
        String error = summaryRespone.error;
        if (StatusCode.isOk(code)) {
            showPieChart(summaryRespone.body);
        } else if (StatusCode.isUnauthorised(code)) {
            Authorisation.UpdateToken(getActivity());
            loadSummary();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPieChart(String details) {
        try {
            jsonArray = new JSONArray(details);

            today = jsonArray.getJSONObject(0);
            oneMonth = jsonArray.getJSONObject(1);
            threeMonths = jsonArray.getJSONObject(2);
            sixMonths = jsonArray.getJSONObject(3);
            year = jsonArray.getJSONObject(4);


            mainLayout.addView(pieChart);
            mainLayout.setBackgroundColor(Color.parseColor("#f1f3f4"));

            //set
            pieChart.setUsePercentValues(false);
            pieChart.setDescription("Summary");

            //
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColorTransparent(true);
            pieChart.setHoleRadius(12);
            pieChart.setTransparentCircleRadius(10);

            //enable rotation of chart
            pieChart.setRotationAngle(0);
            pieChart.setRotationEnabled(true);

            //set a chart value selected listener
            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, int i, Highlight highlight) {
                    if (entry == null)
                        return;
                    String output = "";
                    switch ((int) entry.getXIndex()) {
                        case 0:
                            output = getSummaryFromJson(today);
                            break;
                        case 1:
                            output = getSummaryFromJson(oneMonth);
                            break;
                        case 2:
                            output = getSummaryFromJson(threeMonths);
                            break;
                        case 3:
                            output = getSummaryFromJson(sixMonths);
                            break;
                        case 4:
                            output = getSummaryFromJson(year);
                            break;
                    }
                    Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });

            // add data
            addData();

            //customize legend

            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(3);
            l.setYEntrySpace(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getSummaryFromJson(JSONObject json) {
        String output = "";
        try {
            output = "Paid: " + json.getString("paid") + "\n" +
                    "Payable: " + json.getString("payable") + "\n" +
                    "Overdue: " + json.getString("overdue") + "\n" +
                    "Income: " + json.get("income");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    private void addData() {
        ArrayList<Entry> yVal = new ArrayList<Entry>();

        for (int i = 0; i < pieItemDimension.length; i++)
            yVal.add(new Entry(pieItemDimension[i], i));

        ArrayList<String> xVal = new ArrayList<String>();
        for (int i = 0; i < pieItemName.length; i++)
            xVal.add(pieItemName[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVal,"Legend");
        dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(3);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int i : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(i);
        for (int i : ColorTemplate.JOYFUL_COLORS)
            colors.add(i);
        for (int i : ColorTemplate.COLORFUL_COLORS)
            colors.add(i);
        for (int i : ColorTemplate.LIBERTY_COLORS)
            colors.add(i);
        for (int i : ColorTemplate.PASTEL_COLORS)
            colors.add(i);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object
        PieData data = new PieData(xVal, dataSet);
//        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0.1f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

        //undo all highlights
        pieChart.highlightValues(null);

        //update pie chart
        pieChart.invalidate();
    }
}
