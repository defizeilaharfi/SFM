package com.example.sfa;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.ResourceBundle;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricalFragment extends Fragment {

//    @NonNull
    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState){

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historical, container, false);

//        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
//    }
//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        //Do something with the date chosen by the user
//        TextView tv = (TextView) getActivity().findViewById(R.id.tv);
//        tv.setText("Date changed...");
//        tv.setText(tv.getText() + "\nYear: " + year);
//        tv.setText(tv.getText() + "\nMonth: " + month);
//        tv.setText(tv.getText() + "\nDay of Month: " + day);
//
//        String stringOfDate = day + "/" + month + "/" + year;
//        tv.setText(tv.getText() + "\n\nFormatted date: " + stringOfDate);
    }

}
