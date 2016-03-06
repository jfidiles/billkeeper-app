package com.work.jfidiles.BillKeeper.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.APIConnect;
import com.work.jfidiles.BillKeeper.Activity.manage.manage_bill;
import com.work.jfidiles.BillKeeper.Activity.manage.manage_income;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Interfaces.MarkAsOverdue;
import com.work.jfidiles.BillKeeper.Interfaces.onReportTask;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;
import com.work.jfidiles.BillKeeper.Response.ReportTaskResponse;
import com.work.jfidiles.BillKeeper.StatusCode;
import com.work.jfidiles.BillKeeper.Utilities;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class fragSummary extends Fragment implements onReportTask, MarkAsOverdue {
    JSONArray jsonArray;
    JSONObject today, oneMonth, threeMonths, sixMonths, year;
    private PieChart pieChart;
    private float[] pieItemDimension = AppConfig.PIE_DIMENSION;
    private final String[] pieItemName = AppConfig.PIE_ITEMS;
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

        FloatingActionsMenu fab = (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        fab.setVisibility(View.VISIBLE);
        FloatingActionButton billFAB = (FloatingActionButton) getActivity().findViewById(R.id.action_a);
        FloatingActionButton incomeFAB = (FloatingActionButton) getActivity().findViewById(R.id.action_b);

        billFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_bill.class);
                startActivity(intent);
            }
        });

        incomeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), manage_income.class);
                startActivity(intent);
            }
        });

        mainLayout = (FrameLayout) rootView.findViewById(R.id.PieChart);
        pieChart = new PieChart(getActivity());
        loadSummary();

        checkAndSetBillsToOverdue();
        return rootView;
    }

    private void checkAndSetBillsToOverdue() {
        APIConnect.UpdatePayableToOverdue updateToOverdue = new APIConnect.UpdatePayableToOverdue();
        updateToOverdue.delegate = this;
        updateToOverdue.execute();
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
            APIConnect.UpdateToken(getActivity());
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

            pieChart.setUsePercentValues(false);
            pieChart.setDescription("Summary");

            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColorTransparent(true);
            pieChart.setHoleRadius(12);
            pieChart.setTransparentCircleRadius(10);

            pieChart.setRotationAngle(0);
            pieChart.setRotationEnabled(true);

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
                public void onNothingSelected() {}
            });

            addData();

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
            output = "Paid: " + json.getString(AppConfig.PAID) + "\n" +
                    "Payable: " + json.getString(AppConfig.PAYABLE) + "\n" +
                    "Overdue: " + json.getString(AppConfig.OVERDUE) + "\n" +
                    "Income: " + json.get(AppConfig.INCOME);
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
        PieDataSet dataSet = new PieDataSet(yVal, AppConfig.LEGEND);
        dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(3);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<>();

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
        data.setValueTextSize(0.1f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        //update pie chart
        pieChart.invalidate();
    }

    @Override
    public void setBillToOverdue(BillTaskResponse updateToOverdue) {
        HttpStatus code = updateToOverdue.code;
        String error = updateToOverdue.error;
        if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getActivity());
            checkAndSetBillsToOverdue();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }
}
