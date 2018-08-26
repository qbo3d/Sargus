package com.qbo3d.sargus.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qbo3d.sargus.Objects.Ticket;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Vars;

import java.util.LinkedList;

public class TicketFragment extends Fragment {

    public static ListView lv_ft_ticket;
    private static ProgressDialog progressDialog;
    public static LinkedList<Ticket> data = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        lv_ft_ticket = view.findViewById(R.id.lv_ft_ticket);

        progressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (Vars.allTicket != null) {
            Util.cargarAllTickets(getActivity(), lv_ft_ticket, R.layout.itemlist_ticket, Util.ticketListToData());
        }

        return view;
    }

}
