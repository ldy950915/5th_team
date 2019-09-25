package team5_project;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomCalendar {
	public static Calendar calendar = Calendar.getInstance();

	public static String date() {
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}

	public static String time() {
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
}
