package com.example.jimmy.navigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jimmy on 10/20/2015.
 */
public class fragBills extends Fragment {
    RecyclerView recyclerView;
    private BillsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.lay_fragbills, container, false);
        FloatingActionsMenu actionsMenu = (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        actionsMenu.setVisibility(View.GONE);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleview);
        adapter = new BillsAdapter(getActivity(),getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.jimmy.navigationdrawer.add_bill");
                startActivity(intent);
            }
        });
        return rootView;
    }
    public List<Information> getData(){
        List<Information> data = new ArrayList<>();
        APIConnect apiConnect = new APIConnect();
        String[][] datas = apiConnect.getVector();
        int[] id ={ Integer.parseInt(datas[0][0]),Integer.parseInt(datas[1][0])};
        String[] title ={datas[0][1],datas[1][1]};
        int[] amount = {Integer.parseInt(datas[0][2]),Integer.parseInt(datas[1][2])};
        String[] notes ={datas[0][3],datas[1][3]};
        String[] dataP ={datas[0][4],datas[1][4]};
        String[] payment_type ={datas[0][5],datas[1][5]};
        String imageName = datas[0][6];
        int resID =  getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());
        String imageName2 = datas[1][6];
        int resID2 = getResources().getIdentifier(imageName2, "drawable", getActivity().getPackageName());
        int[] image = {resID,resID2};
        for(int i = 0; i<title.length;i++){
            Information current = new Information();
            current.id = id[i];
            current.title = title[i];
            current.amount = amount[i];
            current.notes = notes[i];
            current.date= dataP[i];
            current.payment_type= payment_type[i];
            current.image = image[i];
            data.add(current);
        }
        return data;
    }
}
