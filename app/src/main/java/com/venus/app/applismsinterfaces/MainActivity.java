package com.venus.app.applismsinterfaces;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.venus.app.Adapters.ViewPagerAdapater;
import com.venus.app.IO.Asyncable;
import com.venus.app.IO.FetchOnlineAsc;
import com.venus.app.IO.SendToServerAsc;
import com.venus.app.Modele.*;
import com.venus.app.Utils.SimpleMessageDialog;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Asyncable {
    private static final String FOA_EXPORT = "export";
    private static final String FOA_IMPORT = "import";
    public static int fragmentToUpdate = -1;
    private ViewPager viewPager;
    private ViewPagerAdapater adapter;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapater(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fab_listener);

        progress = new ProgressDialog(this);
    }

    private View.OnClickListener fab_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // redirige vers l'activite de creation d'un nouveau message/groupe
            if (viewPager.getCurrentItem() == 0) startActivity(new Intent(MainActivity.this, NewMessageActivity.class));
            else startActivity(new Intent(MainActivity.this, NewGroupeActivity.class));
        }
    };

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("on resume");
        if (fragmentToUpdate != -1) {
            //((ListeFragment) ((FragmentStatePagerAdapter) viewPager.getAdapter()).getItem(fragmentToUpdate)).update();
            fragmentToUpdate = -1;
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Exporter");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ab_operateurs) {
            new OperatorsDialog().show(getSupportFragmentManager(), "operators");
        } else if (item.getItemId() == R.id.ab_export) {
            ImportExportDialogFragment.newInstance("export").show(getSupportFragmentManager(), "export");
        } else if (item.getItemId() == R.id.ab_import) {
            ImportExportDialogFragment.newInstance("import").show(getSupportFragmentManager(), "import");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchOnlineResult(Object result, String code) {
        progress.dismiss();
        if (code.equals(FOA_EXPORT)) {
            if (result != null) {
                try {
                    JSONObject res = new JSONObject(result.toString());
                    if (res.getInt("success") == 1)
                        SimpleMessageDialog.newInstance("Exportation réussie").show(getSupportFragmentManager(), "success");
                    else SimpleMessageDialog.newInstance("Une erreur est surevenue").show(getSupportFragmentManager(), "failed");
                } catch (Exception e) {
                    SimpleMessageDialog.newInstance("Une erreur est surevenue").show(getSupportFragmentManager(), "failed");
                }

            }
        } else if (code.equals(FOA_IMPORT)) {
            //
            JSONArray jArray;
            try {
                jArray = ((JSONObject) result).getJSONArray("contacts");
                for (int i = 0; i < jArray.length(); i++) {
                    Contact c = new Contact(jArray.getJSONObject(i).getString("nom"),
                            jArray.getJSONObject(i).getString("numero"),
                            jArray.getJSONObject(i).getString("operateur"));
                    c.save();
                }
                jArray = ((JSONObject) result).getJSONArray("groupes");
                for (int i = 0; i < jArray.length(); i++) {
                    Groupe g = new Groupe(jArray.getJSONObject(i).getString("nom"));
                    g.save();
                }
                // message
                // groupecontact
                // groupemessage
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ImportExportDialogFragment extends AppCompatDialogFragment {
        // TODO: Ajouter les demandes de permission
        private static final String ARG_OP = "operation";
        private AppCompatEditText editText;

        public static ImportExportDialogFragment newInstance(String op) {

            Bundle args = new Bundle();
            args.putString(ARG_OP, op);
            ImportExportDialogFragment fragment = new ImportExportDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private void exporter(String url) {
            ((MainActivity) getActivity()).progress.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contacts", Contact.listAll(Contact.class));
                jsonObject.put("groupes", Groupe.listAll(Groupe.class));
                jsonObject.put("messages", Message.listAll(Message.class));
                jsonObject.put("groupemessages", GroupeMessage.listAll(GroupeMessage.class));
                jsonObject.put("groupecontact", GroupeContact.listAll(GroupeContact.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("json = " + jsonObject);
            new SendToServerAsc((MainActivity) getActivity(), url, FOA_EXPORT).execute(jsonObject.toString());
            dismiss();
        }

        private void importer(String url) {
            ((MainActivity) getActivity()).progress.show();
            new FetchOnlineAsc((MainActivity) getActivity(), url, FOA_IMPORT).execute();
            dismiss();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View v = new LinearLayout(getContext());
            ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);
            editText = new AppCompatEditText(getContext());
            ((LinearLayout) v).addView(editText);

            AlertDialog dialog = null;
            if (getArguments().containsKey(ARG_OP))
                if (getArguments().getString(ARG_OP).equals("export")) {
                    dialog =  new AlertDialog.Builder(getContext())
                            .setView(v)
                            .setTitle("Entrez l'adresse du serveur")
                            .setPositiveButton("Exporter", null)
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Utils.isGoodStringValue(editText.getText().toString()))
                                        exporter(editText.getText().toString());
                                    else editText.setError("valeur incorrecte");
                                }
                            });
                        }
                    });
                } else if (getArguments().getString(ARG_OP).equals("import")) {
                    dialog =  new AlertDialog.Builder(getContext())
                            .setView(v)
                            .setTitle("Entrez l'adresse du serveur")
                            .setPositiveButton("Importer", null)
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Utils.isGoodStringValue(editText.getText().toString()))
                                        importer(editText.getText().toString());
                                    else editText.setError("valeur incorrecte");
                                }
                            });
                        }
                    });
                }

            if (dialog == null) dialog = new AlertDialog.Builder(getContext()).create();
            return dialog;
        }
    }
}
