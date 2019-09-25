package team5_project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class Computer {
	private int number;
	private boolean isUse;
	private User user;
	private Timer timer;
	private String root = "ComputerInfo";

	public Computer(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public boolean getIsUse() {
		return isUse;
	}

	public void setIsUse(boolean isUse) {
		this.isUse = isUse;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void powerOn(User user) {
		this.user = user;
		this.user.setLogin(true);
		isUse = true;
		writeInfo("Login", false, "");
		start();
	}

	private void start() {
		TimerTask timeThread = new TimerTask() {
			@Override
			public void run() {
				if (ValidataionHelper.ageCheck(user)) {
					long currentSaveTime = user.getSaveTime();
					if (currentSaveTime > 0) {
						long result = (currentSaveTime - 1);
						user.setSaveTime(result);
					} else {
						if (isUse)
							powerOff(true, "");
					}
				} else {
					powerOff(true, "청소년 보호법으로 사용을 종료합니다.");
				}
			};
		};

		timer = new Timer();
		timer.schedule(timeThread, 0, 1000); // timeThread 작업을 delay 1초 후 없이 1초씩 반복한다.
	}

	public void powerOff(boolean isAuto, String memo) {
		if (user.getId() != null) {
			isUse = false;
			timer.cancel();
			user.setLogin(false);
			if (!isAuto)
				System.out.println(user.getName() + "님 사용을 종료합니다.");
			writeInfo("Logout", isAuto, memo);
			user = null;
			// computer.showSe0at(); 시간 끝날때마다 자리 출력. . .
		} else {
			System.out.println("미 사용중인 컴퓨터입니다.");
			isUse = false;
		}
	}

	private void writeInfo(String status, boolean isAuto, String memo) {
		String auto = isAuto ? "시스템 자동" : "사용자";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(root + File.separator + number + ".txt", true))) {
			bw.write(user.getName() + "님 " + auto + " " + status + " " + CustomCalendar.date() + " "
					+ CustomCalendar.time() + " " + memo);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
