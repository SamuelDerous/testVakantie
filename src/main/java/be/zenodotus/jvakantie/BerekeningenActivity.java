package be.zenodotus.jvakantie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import be.zenodotus.data.Rekenen;
import be.zenodotus.data.Totalen;
import be.zenodotus.databank.Verlof;
import be.zenodotus.databank.VerlofDao;
import be.zenodotus.databank.Verlofsoort;
import be.zenodotus.databank.VerlofsoortDao;
import be.zenodotus.testVakantie.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;

public class BerekeningenActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	
	private List<TextView> txtSoorten;
	private List<TextView> txtUren;
	private GridLayout grid;
	private int jaar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_bar_berekeningen);
		grid = (GridLayout) findViewById(R.id.gridHoeveelheid);
		txtUren = new ArrayList<TextView>();
		txtSoorten = new ArrayList<TextView>();
		Bundle datum = getIntent().getExtras();
		jaar = datum.getInt("JAAR");
		berekenUren();
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
	
	
	
	public void vulGrid() {
		for(int i = 0; i < txtSoorten.size(); i++) {
			GridLayout.LayoutParams gridLayoutParams1 = new GridLayout.LayoutParams();
			GridLayout.LayoutParams gridLayoutParams2 = new GridLayout.LayoutParams();
			gridLayoutParams1.rowSpec = GridLayout.spec(i);
			gridLayoutParams1.width = 200;
			gridLayoutParams1.columnSpec = GridLayout.spec(0);
			txtSoorten.get(i).setLayoutParams(gridLayoutParams1);
			grid.addView(txtSoorten.get(i));
			gridLayoutParams2.rowSpec = GridLayout.spec(i);
			gridLayoutParams2.columnSpec = GridLayout.spec(1);
			txtUren.get(i).setLayoutParams(gridLayoutParams2);
			grid.addView(txtUren.get(i));
			
		}
	}
	
		
	public void berekenUren() {
		
		List<Rekenen> berekening = new Totalen(this, jaar).berekenUren();
		for(int i = 0; i < berekening.size(); i++) {
			//if(!berekening.get(i).getSoort().equals("overuren"))
			txtUren.add(new TextView(this));
			txtUren.get(i).setTextSize(24);
			txtUren.get(i).setText(berekening.get(i).totaal());
			txtSoorten.add(new TextView(this));
			txtSoorten.get(i).setTextSize(24);
			txtSoorten.get(i).setText(berekening.get(i).getSoort());
			
			
		}
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
