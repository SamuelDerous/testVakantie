package be.zenodotus.jvakantie;

import java.util.ArrayList;
import java.util.List;

import be.zenodotus.adapters.DagenAdapter;
import be.zenodotus.data.Rekenen;
import be.zenodotus.data.Totalen;
import be.zenodotus.databank.Verlof;
import be.zenodotus.databank.VerlofDao;
import be.zenodotus.databank.Verlofsoort;
import be.zenodotus.databank.VerlofsoortDao;
import be.zenodotus.testVakantie.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

public class SoortenActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	
	List<EditText> txtSoorten, txtUren;
	List<ImageButton> btnVerwerken;
	List<ImageButton> btnVerwijderen;
	List<Verlofsoort> verlofsoorten;
	Button btnToevoegen;
	GridLayout grid;
	VerlofsoortDao dao;
	int jaar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_bar_soorten);
		Bundle datum = getIntent().getExtras();
		jaar = datum.getInt("JAAR");
		grid = (GridLayout) findViewById(R.id.gridSoorten);
		txtSoorten = new ArrayList<EditText>();
		txtUren = new ArrayList<EditText>();
		btnVerwerken = new ArrayList<ImageButton>();
		btnVerwijderen = new ArrayList<ImageButton>();

		maakViews();
		vulGrid();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		
	}
	
	private void maakViews() {
		dao = new VerlofsoortDao(this);
		dao.open();
		verlofsoorten = dao.getAlleSoortenPerJaar(jaar);
		dao.close();
		int i = 0;
		int j = 0;
		while(j < verlofsoorten.size()) {
			final Verlofsoort verlofsoort = verlofsoorten.get(j);
			if(!verlofsoort.getSoort().equalsIgnoreCase("overuren")) {
			txtSoorten.add(new EditText(SoortenActivity.this));
			txtUren.add(new EditText(this));
			btnVerwerken.add(new ImageButton(this));
			btnVerwijderen.add(new ImageButton(this));
			txtSoorten.get(i).setText(verlofsoort.getSoort());
			txtUren.get(i).setText(verlofsoort.getUren());


				btnVerwerken.get(i).setImageResource(R.drawable.bewerken);
				btnVerwijderen.get(i).setImageResource(R.drawable.recycle_bin);
				final int teller = i;
				btnVerwerken.get(i).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						verlofsoort.setSoort(txtSoorten.get(teller).getText().toString());
						verlofsoort.setUren(txtUren.get(teller).getText().toString());

						dao.open();
						dao.updateSoort(verlofsoort);
						dao.close();
						Toast.makeText(SoortenActivity.this, "Verlofsoort aangepast", Toast.LENGTH_LONG).show();

					}

				});
				btnVerwijderen.get(i).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						verlofsoort.setSoort(txtSoorten.get(teller).getText().toString());

						dao.open();
						dao.verwijderSoort(verlofsoort);
						dao.close();
						finish();
						startActivity(getIntent());

						Toast.makeText(SoortenActivity.this, "Verlofsoort is verwijderd", Toast.LENGTH_LONG).show();
					}

				});
				i++;
			}
			j++;
			
		}
	}
	
	private void vulGrid() {
		int i = 0;
		for(; i < txtSoorten.size(); i++) {
			GridLayout.LayoutParams gridLayoutParams1 = new GridLayout.LayoutParams();
			GridLayout.LayoutParams gridLayoutParams2 = new GridLayout.LayoutParams();
			GridLayout.LayoutParams gridLayoutParams3 = new GridLayout.LayoutParams();
			GridLayout.LayoutParams gridLayoutParams4 = new GridLayout.LayoutParams();
			gridLayoutParams1.rowSpec = GridLayout.spec(i);
			gridLayoutParams1.columnSpec = GridLayout.spec(0);
			txtSoorten.get(i).setLayoutParams(gridLayoutParams1);
			grid.addView(txtSoorten.get(i));
			gridLayoutParams2.rowSpec = GridLayout.spec(i);
			gridLayoutParams2.columnSpec = GridLayout.spec(1);
			txtUren.get(i).setLayoutParams(gridLayoutParams2);
			grid.addView(txtUren.get(i));
			gridLayoutParams3.columnSpec = GridLayout.spec(2);
			gridLayoutParams3.rowSpec = GridLayout.spec(i);
			btnVerwerken.get(i).setLayoutParams(gridLayoutParams3);
			grid.addView(btnVerwerken.get(i));
			gridLayoutParams4.columnSpec = GridLayout.spec(2);
			gridLayoutParams3.rowSpec = GridLayout.spec(i);
			btnVerwerken.get(i).setLayoutParams(gridLayoutParams4);
			grid.addView(btnVerwijderen.get(i));
		}

		btnToevoegen = new Button(this);
		GridLayout.LayoutParams gridLayoutParams5 = new GridLayout.LayoutParams();
		gridLayoutParams5.rowSpec = GridLayout.spec(++i);
		gridLayoutParams5.columnSpec = GridLayout.spec(0, 4);
		gridLayoutParams5.width = LayoutParams.MATCH_PARENT;
		btnToevoegen.setLayoutParams(gridLayoutParams5);
		btnToevoegen.setText("Soort toevoegen");
		btnToevoegen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showToevoegen();
				
			}
			
		});
		grid.addView(btnToevoegen);
		
	}
	
	private void showToevoegen() {
	
	LayoutInflater li = LayoutInflater.from(this);
	View promptsView = li.inflate(R.layout.add_soort_prompt, null);

	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			this);

	// set prompts.xml to alertdialog builder
	alertDialogBuilder.setView(promptsView);

	final EditText txtVerlofsoort = (EditText) promptsView
			.findViewById(R.id.txtPrSoort);
	final EditText txtVerlofUren = (EditText) promptsView.findViewById(R.id.txtPrUren);
	

	// set dialog message
	alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("Toevoegen",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				if (!txtVerlofsoort.getText().equals("overuren") && !txtVerlofsoort.getText().equals("OU")) {
					Verlofsoort verlofsoort = new Verlofsoort();

					verlofsoort.setSoort(txtVerlofsoort.getText().toString());
					verlofsoort.setUren(txtVerlofUren.getText().toString());
					verlofsoort.setJaar(jaar);
					VerlofsoortDao verlofsoortDao = new VerlofsoortDao(SoortenActivity.this);
					verlofsoortDao.open();
					verlofsoortDao.toevoegenSoort(verlofsoort);
					verlofsoortDao.close();
					finish();
					startActivity(getIntent());

					Toast.makeText(SoortenActivity.this, "Verlofsoort is Toegevoegd", Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(SoortenActivity.this, "Dit woordt is gereserveerd", Toast.LENGTH_LONG).show();
				}
			}
				
			
		  })
		.setNegativeButton("Cancel",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) {
			dialog.cancel();
		    }
		  });

	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();

	// show it
	alertDialog.show();

}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		if(id == R.id.nav_main) {
			Intent addIntent = new Intent(this, MainActivity.class);
			addIntent.putExtra("JAAR", jaar);
			this.startActivity(addIntent);
		}
		else if (id == R.id.nav_addSoort) {
			Intent addIntent = new Intent(this, AddSoort.class);
			addIntent.putExtra("JAAR", jaar);
			this.startActivity(addIntent);
		} else if (id == R.id.nav_soort) {
			Intent soortenIntent = new Intent(this, SoortenActivity.class);
			soortenIntent.putExtra("JAAR", jaar);
			this.startActivity(soortenIntent);
		} else if (id == R.id.nav_berekeningen) {
			Intent berekeningenIntent = new Intent(this, BerekeningenActivity.class);
			berekeningenIntent.putExtra("JAAR", jaar);
			this.startActivity(berekeningenIntent);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}


}
	
	

