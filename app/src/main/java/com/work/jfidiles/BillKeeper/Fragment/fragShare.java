package com.work.jfidiles.BillKeeper.Fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.work.jfidiles.BillKeeper.Activity.manage.manage_bill;
import com.work.jfidiles.BillKeeper.Activity.manage.manage_income;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

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

        btnShare = (Button) rootView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlToShare = AppConfig.URL_TO_SHARE;
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
                    String sharerUrl = AppConfig.URL_BROWSER_FACEBOOK + urlToShare;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }
                startActivity(intent);
            }
        });
        return rootView;
    }
}