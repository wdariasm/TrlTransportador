package com.giocni.trasnportador.Utiles;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Gilmar Ocampo Nieves on 23/04/2016.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public String fecha;
    public EditText txtFecha;

    public DatePicker(){
        fecha = "";
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c= Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,anio,mes,dia);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String me = String.valueOf((monthOfYear + 1));
        String di = String.valueOf(dayOfMonth);
        if((monthOfYear + 1) < 10){
            me = "0" +  String.valueOf((monthOfYear + 1));
        }
        if((dayOfMonth + 1) < 10){
            di = "0" +  String.valueOf(dayOfMonth);
        }
        fecha = year + "-" + me + "-" + di;
        txtFecha.setText(fecha);
    }
}
