package com.dst.dt203657.sharearide;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dst.dt203657.sharearide.com.dst.sharearide.representations.OfferARide;
import com.dst.dt203657.sharearide.com.dst.sharearide.representations.OptARide;
import com.dst.dt203657.sharearide.com.dst.sharearide.representations.RestResponse;

public class RideShareOffersListView extends ListFragment implements OpterDialogFragment.OpterDialogListener {
    private static final int REQ_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        // Calling Rest service to get list of users who offers ride share

        String pickupLoc = ListActivity.pickupLocation;
        String dropLoc = ListActivity.dropLocation;

        new HttpRequestTask().execute(new String[]{pickupLoc,dropLoc});

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        RideOfferInfo clickedDetail = (RideOfferInfo)l.getItemAtPosition(position);
        OptARide optARide = new OptARide();
        optARide.setOptedDropLocation(clickedDetail.getDropLocation());
        optARide.setOptedMeetingPoint(clickedDetail.getMeetingPoint());
        optARide.setOptedPickUpLocation(clickedDetail.getPickupLocation());
        optARide.setOptedRideId(clickedDetail.getRideId());
        optARide.setOptedStartTime(clickedDetail.getStartTime());
        optARide.setOpterMobileNumber(BigInteger.valueOf(1234567890));
        optARide.setOpterName("");
        optARide.setPreferredViaRroute("");

      /*  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(View v) {
                OpterDialogFragment dialog = new OpterDialogFragment();
                dialog.setTargetFragment(RideShareOffersListView.this, REQ_CODE);
                dialog.show(getFragmentManager(), "dialog");
            }
        }*/
        onDialogPositiveClick(new OpterDialogFragment(),new OptARide(),optARide);
        new SaveOptedRideTask().execute(optARide);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, OptARide sourceOptARide,OptARide targetOptARide){
        targetOptARide.setOpterMobileNumber(sourceOptARide.getOpterMobileNumber());
        targetOptARide.setOpterName(sourceOptARide.getOpterName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getActivity(), "Result: " + resultCode,
                Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class HttpRequestTask extends AsyncTask<String, Void, List<RideOfferInfo>> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = ProgressDialog.show(ListActivity.this, "Please wait", "Loading rides..", true, false);
        }


        @Override
        protected List<RideOfferInfo> doInBackground(String... params) {
            try {
                //final String url = "http://10.161.233.73:8080/share-a-ride/searchRide?pickupLocation="+params[0]+"&dropLocation="+params[1];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                RiderInfoResponse isSuccess = restTemplate.getForObject(ListActivity.urlRequestedFor,RiderInfoResponse.class);
                Log.i(" HttpRequestTask ","isSuccess::"+isSuccess.toString());
                return isSuccess.getData();
            } catch (Exception e) {
                Log.e("ListActivity", e.getMessage(), e);
            }

            return new ArrayList<RideOfferInfo>();
        }

        @Override
        protected void onPostExecute(List<RideOfferInfo> rideOfferList) {
            setListAdapter(new RiderItemAdapter(getActivity(), rideOfferList));
        }

    }


    private class SaveOptedRideTask extends AsyncTask<OptARide, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "Please wait", "Opting...", true, false);
        }


        @Override
        protected Boolean doInBackground(OptARide... params) {
            Assert.notNull(params[0],"Opting data required.");
            try {
                final String url = "http://49.205.0.117:8080/share-a-ride/saveOptedRide";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                RestResponse isSuccess = restTemplate.postForObject(url,params[0],RestResponse.class);
                Log.i(" SaveOptedRideTask ","isSuccess::"+isSuccess.toString());
                return (Boolean)isSuccess.getData();
            } catch (Exception e) {
                Log.e("ListActivity", e.getMessage(), e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean optSaveStatus) {
            progressDialog.dismiss();
        }

    }
}
