package be.zenodotus.data;



public class Rekenen {
	
	private int uren;
	private int minuten;
	private String soort;
	private String strUren, strMinuten;
	
	public Rekenen(String tijd, String soort) {
		totaal(tijd);
		
		this.soort = soort;
	}
	
	public boolean totaal(String tijd) {
		
		String[] uurMinuut = tijd.split(":");
		if(uurMinuut.length == 2 && isNumeric(uurMinuut[0]) && isNumeric(uurMinuut[1]) && Integer.parseInt(uurMinuut[1]) < 60) {
			uren = Integer.parseInt(uurMinuut[0]);
			minuten = Integer.parseInt(uurMinuut[1]);
			strUren = getStrUren();
			strMinuten = getStrMinuten();
			if(((uren == 0) && minuten > 0) || (uren > 0)) {
				return true;
			}
			
		}
		return false;
		
		
	}

	public String transform() {
		return strUren + ":" + strMinuten;
	}

	static public String transformTime(String tijd) {
		String[] uurMinuut = tijd.split(":");
		if(uurMinuut.length == 2 && isNumericst(uurMinuut[0]) && isNumericst(uurMinuut[1]) && Integer.parseInt(uurMinuut[1]) < 60) {
			String time = getStrUrenst(Integer.parseInt(uurMinuut[0])) + ":" + getStrMinutenst(Integer.parseInt(uurMinuut[1]));
			return time;

		}
		return "0:00";



	}
	
	public boolean aftrekken(String tijd) {
		String[] uurMinuut = tijd.split(":");
		if(uurMinuut.length == 2 && isNumeric(uurMinuut[0]) && isNumeric(uurMinuut[1])) {
			uren -= Integer.parseInt(uurMinuut[0]);
			//minuten -= (60 + Integer.parseInt(uurMinuut[1]));
			if ((Integer.parseInt(uurMinuut[1])) > minuten) {
				uren--;
				minuten = (minuten + 60) - (Integer.parseInt(uurMinuut[1])); 
			} else {
				minuten -= Integer.parseInt(uurMinuut[1]);
			}
			strUren = getStrUren();
			strMinuten = getStrMinuten();
			if((Integer.parseInt(uurMinuut[0]) <= 0 && Integer.parseInt(uurMinuut[1]) > 0) || (Integer.parseInt(uurMinuut[0]) > 0))
			{
				return true;
			}
			
		}
		return false;
	}

	public static String optellenOveruren(String tijd1, String tijd2) {
		String[] uurMinuut1 = tijd1.split(":");
		String[] uurMinuut2 = tijd2.split(":");
		int uren, minuten;
		int aantalUren = 0;
		String totaal = "00:00";
		if((uurMinuut1.length == 2 && isNumericst(uurMinuut1[0]) && isNumericst(uurMinuut1[1])) && (uurMinuut2.length == 2 && isNumericst(uurMinuut2[0]) && isNumericst(uurMinuut2[1]))) {
			System.out.println("in berekening");
			minuten = Integer.parseInt(uurMinuut1[1]) + Integer.parseInt(uurMinuut2[1]);
			if (minuten >= 60) {
				aantalUren = minuten / 60;
				minuten = minuten % 60;
			}
				uren = Integer.parseInt(uurMinuut1[0]) + Integer.parseInt(uurMinuut2[0]) + aantalUren;
				totaal = getStrUrenst(uren) + ":" + getStrMinutenst(minuten);
				System.out.println("" + totaal);


		}
		return totaal;
	}

	public static String aftrekkenOveruren(String tijd1, String tijd2) {
		String[] uurMinuut1 = tijd1.split(":");
		String[] uurMinuut2 = tijd2.split(":");
		int uren, minuten;
		//int aantalUren = 0;
		String totaal = "00:00";
		if((uurMinuut1.length == 2 && isNumericst(uurMinuut1[0]) && isNumericst(uurMinuut1[1])) && (uurMinuut2.length == 2 && isNumericst(uurMinuut2[0]) && isNumericst(uurMinuut2[1]))) {
			uren = Integer.parseInt(uurMinuut1[0]) - Integer.parseInt(uurMinuut2[0]);
			//minuten -= (60 + Integer.parseInt(uurMinuut[1]));
			minuten = Integer.parseInt(uurMinuut1[1]);
			if ((Integer.parseInt(uurMinuut2[1])) > Integer.parseInt(uurMinuut1[1])) {
				uren--;
				minuten = (minuten + 60) - (Integer.parseInt(uurMinuut2[1]));
			} else {
				minuten -= Integer.parseInt(uurMinuut2[1]);
			}

			totaal = getStrUrenst(uren) + ":" + getStrMinutenst(minuten);



		}
		return totaal;
	}
	
	public String totaal() {
		return strUren + ":" + strMinuten;
	}
	
	
	private boolean isNumeric(String number) {
		try {
			int test = Integer.parseInt(number);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private static boolean isNumericst(String number) {
		try {
			int test = Integer.parseInt(number);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public int getUren() {
		return uren;
	}

	public void setUren(int uren) {
		this.uren = uren;
	}

	public int getMinuten() {
		return minuten;
	}

	public void setMinuten(int minuten) {
		this.minuten = minuten;
	}

	public String getSoort() {
		return soort;
	}

	public void setSoort(String soort) {
		this.soort = soort;
	}
	
	public String getStrMinuten() {
		if(minuten < 10) {
			return "0" + minuten;
		} else {
			return "" + minuten;
		}
	}
	
	public String getStrUren() {
		if(uren < 10) {
			return "0" + uren;
		} else {
			return "" + uren;
		}
	}

	public static String getStrMinutenst(int minuten) {
		if(minuten < 10) {
			return "0" + minuten;
		} else {
			return "" + minuten;
		}
	}

	public static String getStrUrenst(int uren) {
		if(uren < 10) {
			return "0" + uren;
		} else {
			return "" + uren;
		}
	}
	
	
	

}
