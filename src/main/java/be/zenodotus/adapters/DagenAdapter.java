package be.zenodotus.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import be.zenodotus.creatie.MaskWatcher;
import be.zenodotus.data.KalenderItems;
import be.zenodotus.data.Rekenen;
import be.zenodotus.data.Totalen;
import be.zenodotus.databank.Feestdag;
import be.zenodotus.databank.FeestdagDao;
import be.zenodotus.databank.Verlof;
import be.zenodotus.databank.VerlofDao;
import be.zenodotus.databank.Verlofsoort;
import be.zenodotus.databank.VerlofsoortDao;
import be.zenodotus.databank.Werkdag;
import be.zenodotus.databank.WerkdagDao;
import be.zenodotus.jvakantie.AddActivity;
import be.zenodotus.jvakantie.MainActivity;
import be.zenodotus.testVakantie.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DagenAdapter<T> extends ArrayAdapter<KalenderItems> {
    private Context context;
    private KalenderItems[] items;
    private int jaar;
    private GregorianCalendar kal;
    private int maand;


    /* renamed from: be.zenodotus.adapters.DagenAdapter.1 */

    class C00821 implements OnClickListener {
        private final /* synthetic */ int val$position;

        C00821(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            int i;
            VerlofDao verlofDao = new VerlofDao(DagenAdapter.this.context);

            for (i = 0; i < DagenAdapter.this.items[this.val$position].getVerlof().size(); i++) {
                Verlof verlof = (Verlof) DagenAdapter.this.items[this.val$position].getVerlof().get(i);
                if (verlof.getVerlofsoort().equals("OU")) {
                    VerlofsoortDao dao = new VerlofsoortDao(DagenAdapter.this.context);
                    dao.open();
                    dao.deleteOveruren(verlof);
                    dao.close();
                }
                verlofDao.open();
                verlofDao.verwijderenVerlof((Verlof) DagenAdapter.this.items[this.val$position].getVerlof().get(i));

                verlofDao.close();
                int grootte = DagenAdapter.this.items[this.val$position].getVerlof().size();
                for (i = 0; i < grootte; i++) {
                    DagenAdapter.this.items[this.val$position].getVerlof().remove(0);
                }
                DagenAdapter.this.notifyDataSetChanged();
            }
        }
    }

    // renamed from: be.zenodotus.adapters.DagenAdapter.2 
    class C00832 implements OnClickListener {
        private  /* synthetic */ int val$position;
        int positie = this.val$position;
        KalenderItems[] lijsten;
        DagenAdapter<KalenderItems> adapter;

        C00832(int i, KalenderItems[] lijsten) {
            this.val$position = i;
            this.lijsten = lijsten;


        }

        public void onClick(View v) {
            boolean weekenddag = false;
            WerkdagDao werkdagDao = new WerkdagDao(DagenAdapter.this.context);
            werkdagDao.open();
            List<Werkdag> weekend = werkdagDao.getWeekend();
            werkdagDao.close();
            for (int i = 0; i < weekend.size(); i++) {
                if (((Werkdag) weekend.get(i)).getDag().equalsIgnoreCase(DagenAdapter.this.items[this.val$position].getWeekdag())) {
                    weekenddag = true;
                }
            }
            //if (!weekenddag) {
            LayoutInflater li = LayoutInflater.from(DagenAdapter.this.context);
            View promptsView = li.inflate(R.layout.activity_add, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    DagenAdapter.this.context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final Spinner spVerlofsoort = (Spinner) promptsView
                    .findViewById(R.id.spVerlofsoort);
            final EditText txtUren = (EditText) promptsView.findViewById(R.id.txtUren);
            txtUren.addTextChangedListener(new MaskWatcher("##:##"));
            final Switch swOveruren = (Switch) promptsView.findViewById(R.id.swOveruren);
            VerlofsoortDao verlofsoortDao = new VerlofsoortDao(DagenAdapter.this.context);
            verlofsoortDao.open();
            ArrayList<Verlofsoort> verlofsoorten = verlofsoortDao.getAlleSoortenPerJaar(jaar);
            ArrayList<String> verlofsoort = new ArrayList<String>();

            for (int i = 0; i < verlofsoorten.size(); i++) {
                verlofsoort.add(verlofsoorten.get(i).getSoort());
            }
            verlofsoortDao.close();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    DagenAdapter.this.context, android.R.layout.simple_spinner_item, verlofsoort);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            swOveruren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (swOveruren.isChecked()) {
                        spVerlofsoort.setVisibility(Spinner.INVISIBLE);
                        //txtUren.setHint("Dit is een test");
                    } else {
                        spVerlofsoort.setVisibility(Spinner.VISIBLE);
                    }
                }
            });
            if (weekenddag) {
                spVerlofsoort.setVisibility(Spinner.INVISIBLE);
                swOveruren.setChecked(true);
                swOveruren.setVisibility(Switch.INVISIBLE);
            }

            spVerlofsoort.setAdapter(adapter);


            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Toevoegen",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    if (!swOveruren.isChecked()) {
                                        boolean isVerlofOk = true;
                                        int dag = val$position;
                                        Verlof verlof = new Verlof();
                                        verlof.setDag(dag);
                                        verlof.setMaand(maand);
                                        verlof.setJaar(jaar);
                                        verlof.setUrental(txtUren.getText().toString());
                                        verlof.setVerlofsoort(spVerlofsoort.getSelectedItem().toString());
                                        List<Rekenen> berekeningen = new Totalen(DagenAdapter.this.context, jaar).berekenUren();
                                        for (int i = 0; i < berekeningen.size(); i++) {
                                            if (berekeningen.get(i).getSoort().equals(verlof.getVerlofsoort())) {
                                                if (!berekeningen.get(i).aftrekken(verlof.getUrental()) || verlof.getUrental().equals("0:00")) {
                                                    Toast.makeText(DagenAdapter.this.context, "Het verlofquota staat niet in de juiste vorm (0:00)", Toast.LENGTH_LONG).show();
                                                    isVerlofOk = false;
                                                    break;
                                                } else {
                                                    if (berekeningen.get(i).getUren() < 0) {
                                                        Toast.makeText(DagenAdapter.this.context, "Het verlofquota is overschreden", Toast.LENGTH_LONG).show();
                                                        isVerlofOk = false;
                                                        break;
                                                    }
                                                }
                                            }

                                        }

                                        if (isVerlofOk) {
                                            VerlofDao verlofDao = new VerlofDao(DagenAdapter.this.context);


                                            verlofDao.open();
                                            verlof.setUrental(Rekenen.transformTime(verlof.getUrental()));
                                            verlofDao.toevoegenVerlof(verlof);
                                            verlofDao.close();
                                            Toast.makeText(DagenAdapter.this.context, "Verlof toegevoegd", Toast.LENGTH_LONG).show();
                                            txtUren.setText("");
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            context.startActivity(intent);


                                        }

                                    } else {
                                        Verlofsoort soort = new Verlofsoort();
                                        soort.setSoort("overuren");
                                        soort.setUren(txtUren.getText().toString());
                                        soort.setJaar(jaar);
                                        VerlofsoortDao dao = new VerlofsoortDao(DagenAdapter.this.context);
                                        boolean verlofok = true;
                                        Rekenen berekening = new Rekenen(txtUren.getText().toString(), "overuren");
                                        //boolean verlofok = true;
                                        if (!berekening.totaal(txtUren.getText().toString()) || txtUren.getText().toString().equals("0:00") || txtUren.getText().toString().equals("0:0"))  {
                                            Toast.makeText(DagenAdapter.this.context, "Het verlofquota staat niet in de juiste vorm (0:00)", Toast.LENGTH_LONG).show();
                                            verlofok = false;

                                        }


                                        if (verlofok) {
                                            dao.open();
                                            Verlofsoort overuren = dao.getOveruren();
                                            if (overuren != null) {
                                                dao.addOveruren(soort);
                                            } else {
                                                dao.toevoegenSoort(soort);
                                            }
                                            dao.close();
                                            VerlofDao verlofDao = new VerlofDao(DagenAdapter.this.context);
                                            verlofDao.open();
                                            int dag = val$position;
                                            Verlof verlof = new Verlof();
                                            verlof.setDag(dag);
                                            verlof.setMaand(maand);
                                            verlof.setJaar(jaar);
                                            verlof.setUrental(Rekenen.transformTime(txtUren.getText().toString()));
                                            verlof.setVerlofsoort("OU");
                                            verlofDao.toevoegenVerlof(verlof);
                                            verlofDao.close();
                                            Toast.makeText(DagenAdapter.this.context, "Overuren toegevoegd", Toast.LENGTH_LONG).show();
                                            txtUren.setText("");
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            context.startActivity(intent);
                                        }
                                    }


                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            //}

        }
    }


    public DagenAdapter(Context context, int resource, KalenderItems[] items, int maand, int jaar) {
        super(context, resource, items);
        this.context = context;
        this.kal = new GregorianCalendar();
        this.items = items;
        this.maand = maand;
        this.jaar = jaar;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtDag;
        TextView txtWeekdag;
        TextView txtVerlof;
        ImageButton btnVerlof;
        int i;
        LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            txtDag = (TextView) convertView.findViewById(R.id.txtKalenderdag);
            txtWeekdag = (TextView) convertView.findViewById(R.id.txtWeekdag);
            txtVerlof = (TextView) convertView.findViewById(R.id.txtVerlof);
            btnVerlof = (ImageButton) convertView.findViewById(R.id.btnVerlof);
        } else {
            txtDag = (TextView) convertView.findViewById(R.id.txtKalenderdag);
            txtWeekdag = (TextView) convertView.findViewById(R.id.txtWeekdag);
            txtVerlof = (TextView) convertView.findViewById(R.id.txtVerlof);
            btnVerlof = (ImageButton) convertView.findViewById(R.id.btnVerlof);
        }
        WerkdagDao werkdagDao = new WerkdagDao(this.context);
        werkdagDao.open();
        List<Werkdag> weekend = werkdagDao.getWeekend();
        werkdagDao.close();
        boolean isWeekend = false;
        txtVerlof.setText("");
        for (i = 0; i < weekend.size(); i++) {
            if (this.items[position].getWeekdag().equalsIgnoreCase(((Werkdag) weekend.get(i)).getDag())) {
                isWeekend = true;
            }
        }
        if (this.items[position].isFeestdag()) {
            convertView.setBackgroundColor(-16711936);
            txtVerlof.append(this.items[position].getFeestdagNaam() + "\n");
        }

        else if (isWeekend) {
            convertView.setBackgroundColor(-3355444);
        } else {
            convertView.setBackgroundColor(-1);
        }
        txtDag.setText(String.valueOf(items[position].getDag()));
        txtWeekdag.setText(this.items[position].getWeekdag());
        for (i = 0; i < this.items[position].getVerlof().size(); i++) {
            txtVerlof.append(new StringBuilder(String.valueOf(((Verlof) this.items[position].getVerlof().get(i)).getVerlofsoort())).append(" ").append(((Verlof) this.items[position].getVerlof().get(i)).getUrental()).append("\n").toString());
        }
        /*for (i = 0; i < this.items[position].getVerlof().size(); i++) {
            txtVerlof.append(new StringBuilder(String.valueOf(((Verlof) this.items[position].getVerlof().get(i)).getVerlofsoort())).append(" ").append(((Verlof) this.items[position].getVerlof().get(i)).getUrental()).append("\n").toString());
        }*/
        if (this.items[position].getVerlof().size() == 0) {
            btnVerlof.setVisibility(100);
        } else {
            btnVerlof.setVisibility(0);
        }
        btnVerlof.setOnClickListener(new C00821(position));
        convertView.setOnClickListener(new C00832(position, items));
        if (this.kal.get(1) == this.jaar && this.kal.get(2) == this.maand && position == this.kal.get(5) - 1) {
            convertView.setBackgroundColor(-256);
        }
        return convertView;
    }
}