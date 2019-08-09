package be.zenodotus.jvakantie;






import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import be.zenodotus.adapters.DagenAdapter;
import be.zenodotus.creatie.GeneratePDF;
import be.zenodotus.data.BWBelgischeFeestdagen;
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
import be.zenodotus.testVakantie.R;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

	GridView gridView;
	
	private TextView txtMaand;
	private ImageButton btnTerug, btnVerder;
	private int[] dagen = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private String[] weekdagen = {"ZO", "ZO", "MA", "DI", "WO", "DO", "VR", "ZA"};
	private GregorianCalendar kal;
	private String[] strDagen;
	private List<Feestdag> feestdagen;
	private FeestdagDao dao;
	private WerkdagDao werkdagDao;
	private GregorianCalendar nu;
	private int jaar;
	private float x1, x2;
	static final int MIN_DISTANCE = 150;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_bar_main);
		findViewById(R.id.drawer_layout).setOnTouchListener(this);
		kal = new GregorianCalendar();
		nu = new GregorianCalendar();
		dao = new FeestdagDao(this);
		jaar = kal.get(GregorianCalendar.YEAR);
		dao.open();
		feestdagen = dao.getFeestdagenperJaar(kal.get(GregorianCalendar.YEAR));
		if (feestdagen.size() == 0) {
			BWBelgischeFeestdagen maakFeestdagen = new BWBelgischeFeestdagen(kal.get(GregorianCalendar.YEAR));
			List<Feestdag> maken = maakFeestdagen.getFeestdagen();
			for(int i = 0; i < maken.size(); i++) {
				dao.toevoegenFeestdag(maken.get(i));
			}
			dao.close();
			//VerlofsoortDao verlofsoortDao = new VerlofsoortDao(this);
			//verlofsoortDao.open();
			//List<Verlofsoort> verlofsoorten = verlofsoortDao.getAlleSoorten();
			//V//erlofsoort overuren = new Verlofsoort();
			//overuren.setSoort("OU");
			final List<Rekenen> berekeningen = new Totalen(this, jaar - 1).berekenUren();
			boolean heavOveruren = false;
			boolean heavVerlof = false;
			for(int i = 0; i < berekeningen.size(); i++) {
				if(berekeningen.get(i).getSoort().equals("OU")) {
					if(berekeningen.get(i).getUren() > 0) {
						heavOveruren = true;
					}
				} else {
					if(berekeningen.get(i).getUren() > 0) {
						heavVerlof = true;
					}
				}

				}
			//}
			if(heavOveruren) { // || heavVerlof) {
				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.activity_heav, null);

				final Switch swVerlof = (Switch) promptsView.findViewById(R.id.swVerlof);
				final Switch swOveruren = (Switch) promptsView.findViewById(R.id.swOU);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
			alertDialogBuilder.setView(promptsView);
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Toevoegen",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// get user input and set it to result
										// edit text
										VerlofsoortDao verlofsoortDao = new VerlofsoortDao(MainActivity.this);
										verlofsoortDao.open();

										if (swVerlof.isChecked()) {
											for(int i = 0; i < berekeningen.size(); i++) {
												if(!berekeningen.get(i).getSoort().equals("OU")) {
													if(berekeningen.get(i).getUren() >= 0) {
														Verlofsoort verlofsoort = new Verlofsoort();
														verlofsoort.setSoort(berekeningen.get(i).getSoort());
														verlofsoort.setUren(berekeningen.get(i).transform());
														verlofsoort.setJaar(jaar);
														verlofsoortDao.toevoegenSoort(verlofsoort);
													}
												}
											}

											if(swOveruren.isChecked()) {
												for(int i = 0; i < berekeningen.size(); i++) {
													if(berekeningen.get(i).getSoort().equals("OU")) {
														if(berekeningen.get(i).getUren() >= 0) {
															Verlofsoort verlofsoort = new Verlofsoort();
															verlofsoort.setSoort(berekeningen.get(i).getSoort());
															verlofsoort.setUren(berekeningen.get(i).transform());
															verlofsoort.setJaar(jaar);
															verlofsoortDao.toevoegenSoort(verlofsoort);
														}
													}
												}

											}
											verlofsoortDao.close();
											/*boolean isVerlofOk = true;
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


											}*/

										} /*else {
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
											}*/
										//}


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

			}
		}

		vulMaand();
		setKalender();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(MainActivity.this);

		
	}



	private void vulMaand() {
		SimpleDateFormat kalFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
		txtMaand = (TextView) findViewById(R.id.txtMaand);
		txtMaand.setText(kalFormat.format(kal.getTime()));
		btnTerug = (ImageButton) findViewById(R.id.btnTerug);
		btnVerder = (ImageButton) findViewById(R.id.btnVerder);
		
		btnTerug.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setVorigeMaand();
			}
		});
		
		btnVerder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setVolgendeMaand();
				
				
			}
		});
		
	}
	
		
	public void onResume() {
		super.onResume();
		setKalender();
	}
	
	
	public void setVolgendeMaand() {
		if(kal.get(Calendar.MONTH)== kal.getActualMaximum(Calendar.MONTH)) {
			kal.set((kal.get(Calendar.YEAR)+ 1), kal.getActualMinimum(Calendar.MONTH), 1);
			jaar = kal.get(Calendar.YEAR);
		} else {
			kal.set(Calendar.MONTH, kal.get(Calendar.MONTH)+ 1);
			
		}
		setKalender();
		
	}
	
	public void setVorigeMaand() {
		if(kal.get(Calendar.MONTH)== kal.getActualMinimum(Calendar.MONTH)) {
			kal.set((kal.get(Calendar.YEAR)- 1) , kal.getActualMaximum(Calendar.MONTH), 1);
			jaar = kal.get(Calendar.YEAR);
		} else {
			kal.set(Calendar.MONTH, kal.get(Calendar.MONTH) - 1);
		}
		setKalender();
		
	}
	
	public void setKalender() {
		
	int maand = kal.get(Calendar.MONTH);
	jaar = kal.get(Calendar.YEAR);
	Calendar dag = Calendar.getInstance();
	dag.set(jaar, maand, 1);
	
	vulMaand();
	if (kal.isLeapYear(kal.get(GregorianCalendar.YEAR))) {
		dagen[1] = 29;
	}
	final KalenderItems[] items = new KalenderItems[dagen[maand]];
	strDagen = new String[dagen[maand]];
	VerlofDao verlofDao = new VerlofDao(this);
	verlofDao.open();		
	ArrayList<Verlof> verloflijst = verlofDao.getAlleVerlovenPerJaar(jaar, maand);
	verlofDao.close();
	for(int i = 0; i < dagen[maand]; i++) {
		
		dag.set(Calendar.DAY_OF_MONTH, i + 1);
		items[i] = new KalenderItems();
		items[i].setWeekdag(weekdagen[dag.get(Calendar.DAY_OF_WEEK)]);
		items[i].setDag(i + 1);
		List<Verlof> verloven = new ArrayList<Verlof>();
		for(int j = 0; j < verloflijst.size(); j++) {
			if ((verloflijst.get(j).getDag() == i)) {
				verloven.add(verloflijst.get(j));
				
			}
		}
		items[i].setVerlof(verloven);
		dao.open();
		Feestdag feestdag = dao.getFeestdag(jaar, maand, i + 1);
		dao.close();
		if (feestdag != null) {
			items[i].setFeestdag(true);
			items[i].setFeestdagNaam(feestdag.getFeestdag());
		} else {
			items[i].setFeestdag(false);
		}
		
		
	}
	
	
	ListView lst = (ListView) findViewById(R.id.lvkalenderdagen);
	DagenAdapter<String> adapter = new DagenAdapter<String>(this,
			android.R.layout.simple_list_item_1, items, maand, jaar);
	
	lst.setAdapter(adapter);

	adapter.notifyDataSetChanged();
	/*lst.setOnTouchListener(new OnSwipeListener(this) {
		public void onSwipeRight() {
			setVolgendeMaand();
		}
		
		public void onSwipeLeft() {
			setVorigeMaand();
		}
	});*/

	lst.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v,
			int position, long id) {
			boolean weekenddag = false;
			werkdagDao = new WerkdagDao(MainActivity.this);
			werkdagDao.open();
			List<Werkdag> weekend = werkdagDao.getWeekend();
			for(int i = 0; i < weekend.size(); i++) {
				if (weekend.get(i).getDag().equalsIgnoreCase(items[position].getWeekdag())) {
					weekenddag = true;

				}
			}
			if (!weekenddag) {
				Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
				addIntent.putExtra("JAAR", kal.get(Calendar.YEAR));
				addIntent.putExtra("MAAND", kal.get(Calendar.MONTH));
				addIntent.putExtra("DAG", position);
				MainActivity.this.startActivityForResult(addIntent, 2);
			} else {
				Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
				addIntent.putExtra("JAAR", kal.get(Calendar.YEAR));
				addIntent.putExtra("MAAND", kal.get(Calendar.MONTH));
				addIntent.putExtra("DAG", position);
				addIntent.putExtra("WEEKDAG", weekenddag);
				MainActivity.this.startActivityForResult(addIntent, 2);
			}
			
		}
	});
	
	
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {

			
		case R.id.mnuPDF:
			LayoutInflater li = LayoutInflater.from(this);
			View promptsView = li.inflate(R.layout.prompt_jaar, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.txtJaarInput);

			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					// get user input and set it to result
					// edit text
				    	GeneratePDF nieuw = new GeneratePDF();
				    	String bestand = nieuw.vakantieAfdruk(MainActivity.this, "JacquelineVandenbroecke" + Integer.parseInt(userInput.getText().toString()) + ".pdf", Integer.parseInt(userInput.getText().toString()));
				    	Toast.makeText(MainActivity.this, "pdf " + bestand + " aangemaakt", Toast.LENGTH_LONG).show();
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
			break;
		
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			this.startActivity(settingsIntent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}



	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_addSoort) {
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x1 = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				x2 = event.getX();
				float deltaX = x2 - x1;
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					if (x2 < x1) {
						setVorigeMaand();
					}
					else {
						setVolgendeMaand();
					}
				}
		}
		return super.onTouchEvent(event);
	}

}
