package be.zenodotus.jvakantie;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSoort extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	
	EditText txtSoort, txtUren;
	Button btnSoort;
	Verlofsoort soort;
	VerlofsoortDao dao;
	int jaar;

	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.app_bar_addsoort);
			Bundle datum = getIntent().getExtras();
			jaar = datum.getInt("JAAR");
			btnSoort = (Button)findViewById(R.id.btnToevoegenSoort);
			btnSoort.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					toevoegenSoort();
					Toast.makeText(AddSoort.this, "Nieuwe Soort toegevoegd", Toast.LENGTH_LONG).show();
					
				}
				
			});
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

	private void toevoegenSoort() {
		txtSoort = (EditText) findViewById(R.id.txtSoort);
		txtUren = (EditText) findViewById(R.id.txtUren);
		soort = new Verlofsoort();
		soort.setSoort(txtSoort.getText().toString());
		soort.setUren(txtUren.getText().toString());
		soort.setJaar(jaar);
		dao = new VerlofsoortDao(this);
		dao.open();
		dao.toevoegenSoort(soort);
		dao.close();
		txtSoort.setText("");
		txtUren.setText("");
		
		
		
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_main) {
			Intent addIntent = new Intent(this, MainActivity.class);
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
