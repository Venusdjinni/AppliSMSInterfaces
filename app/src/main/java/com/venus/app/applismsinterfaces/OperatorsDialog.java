package com.venus.app.applismsinterfaces;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import com.venus.app.Modele.Sim;

import java.util.ArrayList;
import java.util.List;

public class OperatorsDialog extends AppCompatDialogFragment {
    private static String[] operateurs = new String[]{"MTN", "Orange", "Nexttel", "Camtel"};
    private List<Sim> sims = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_operators, null, false);
        LinearLayout ll = v.findViewById(R.id.operators_ll);
        // Construction de la vue
        // Pour chaque sim on ajoute un item

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            SubscriptionManager sMan = getContext().getSystemService(SubscriptionManager.class);
            List<SubscriptionInfo> infos = sMan.getActiveSubscriptionInfoList();
            for (SubscriptionInfo info : infos) {
                View vi = LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_operators, null, false);
                ((AppCompatTextView) vi.findViewById(R.id.opi_sim)).setText("Sim : " + info.getNumber());
                AppCompatSpinner spinner = vi.findViewById(R.id.opi_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, operateurs);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                ll.addView(vi);
                //
                final Sim s = new Sim();
                sims.add(s);
                s.setNumero(info.getNumber());
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        s.setOperateur(operateurs[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }

        return new AlertDialog.Builder(getContext())
                .setView(v)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Sim s : sims)
                            s.save();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
