package com.example.jimmy.navigationdrawer.Fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jimmy.navigationdrawer.Activity.manage.manage_bill;
import com.example.jimmy.navigationdrawer.Activity.manage.manage_income;
import com.example.jimmy.navigationdrawer.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

/**
 * Created by Jimmy on 2/4/2016.
 */
public class fragShare extends Fragment {
    Button btnShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.lay_frag_share, container, false);
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
        btnShare = (Button) rootView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlToShare = "http://play.google.com/store/search?q=pub:{publisher_name}";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

                // See if official Facebook app is found
                boolean facebookAppFound = false;
                List<ResolveInfo> matches =
                        getActivity().getPackageManager().queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                        intent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }

                // As fallback, launch sharer.php in a browser
                if (!facebookAppFound) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }
                startActivity(intent);
            }
        });
        return rootView;
    }
}
