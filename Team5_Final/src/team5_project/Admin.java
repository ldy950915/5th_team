package team5_project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Admin {
	private final String ADMIN_ID = "admin";
	private final String ADMIN_PW = "123";
	private final int PRODUCT_MAX_COUNT = 100; // 음료, 과자는 초기값 100개가 있는 상태이다.
	private boolean isAdmin;
	private Scanner scanner;
	private String userFilenName;
	private Map<String, User> users;
	private List<SalesInfo> salesInfos;
	private Drink drink;
	private Snack snack;

	public Admin(Scanner scanner) {
		userFilenName = "PcUsers.txt";
		this.scanner = scanner;
	}

	public void adminStart(Map<String, User> users, List<SalesInfo> salesInfos, Drink drink, Snack snack) {
		this.users = users;
		this.salesInfos = salesInfos;
		this.drink = drink;
		this.snack = snack;

		adminLogin();
	}

	private void adminLogin() {
		System.out.println("관리자 로그인을 시작합니다.");

		isAdmin = checkLoginCount(3);
		if (isAdmin) {
			System.out.println("관리자 로그인 성공!\n");
			showAdminMenu();
		} else
			System.out.println("관리자 로그인에 실패했습니다. 초기화면으로 이동합니다.");
	}

	private boolean checkLoginCount(int tryCount) {
		boolean result = false;
		String id = "";
		String password = "";
		for (int i = tryCount; i > 0; i--) {
			System.out.print("ID : ");
			id = scanner.next();
			System.out.print("Password : ");
			password = scanner.next();

			if (id.equals(ADMIN_ID) && password.equals(ADMIN_PW)) {
				result = true;
				break;
			} else if (tryCount > 1) {
				System.out.println("입력하신 정보가 일치하지 않습니다.");
				System.out.println("재시도 기회 : " + (i - 1) + "/" + tryCount);
			}
		}

		return result;
	}

	private void adminLogout() {
		isAdmin = false;
		System.out.println("관리자 모드를 종료합니다.");
		System.out.println();
	}

	private void showAdminMenu() {
		adminLoop: while (true) {
			System.out.println("1. 회원 조회");
			System.out.println("2. 파일 저장");
			System.out.println("3. 발주");
			System.out.println("4. 관리자 로그아웃");
			System.out.println("5. 프로그램 종료");

			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 5);
			switch (choice) {
			case 1:
				showSearchMenu();
				break;
			case 2:
				showFileMenu();
				break;
			case 3:
				stockManagement();
				break;
			case 4:
				adminLogout();
				break adminLoop;
			case 5:
				exitPCmanagement();
				break;
			}
		}
	}

	private void exitPCmanagement() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFilenName))) {
			oos.writeObject(users);
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}

		System.out.println("프로그램을 종료합니다.");
		System.exit(0);
	}

	private void showSearchMenu() {
		searchLoop: while (true) {
			System.out.println("1. 전체 조회");
			System.out.println("2. ID 조회");
			System.out.println("3. 이름 조회");
			System.out.println("4. 이전 메뉴로 돌아가기");

			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 4);
			switch (choice) {
			case 1:
				searchAllUser();
				break;
			case 2:
				searchId();
				break;
			case 3:
				searchName();
				break;
			case 4:
				break searchLoop;
			}
		}
	}

	private void searchAllUser() {
		List<User> targets = new ArrayList<User>(users.values());
		showSearchUsers(targets);
	}

	private void searchId() {
		System.out.print("검색 ID를 입력하세요 : ");
		String searchName = scanner.next();
		List<User> targets = new ArrayList<>();
		User target = users.get(searchName);
		if (target != null)
			targets.add(target);

		showSearchUsers(targets);
	}

	private void searchName() {
		System.out.print("검색 이름을 입력하세요 : ");
		String searchName = scanner.next();
		List<User> targets = new ArrayList<>();
		for (User user : users.values()) {
			if (user.getName().contains(searchName))
				targets.add(user);
		}

		showSearchUsers(targets);
	}

	private void showSearchUsers(List<User> users) {
		if (users.size() > 0) {
			System.out.println("번호\t이름\t아이디\t\t전화번호\t\t나이\t주민 번호");
			System.out.println("───────────────────────────────────────────────────────────────");
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				System.out.printf("%02d\t%s\t%s\t\t%s\t%d\t%s\n", (i + 1), user.getName(), user.getId(),
						user.getPhoneNumber(), user.getAge(), user.getSecuritNumber().split("-")[0]);
			}
		} else
			System.out.println("검색 결과가 없습니다.");

		System.out.println();
	}

	private void showFileMenu() {
		while (true) {
			System.out.println("1. 회원 파일 저장");
			System.out.println("2. 매출 파일 저장");
			System.out.println("3. 이전 메뉴로 돌아가기");
			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 3);
			String savePath = "";
			switch (choice) {
			case 1:
				savePath = saveUserInfoFile();
				break;
			case 2:
				savePath = saveSalesInfoFile();
				break;
			case 3:
				break;
			}

			System.out.println("파일 저장을 완료했습니다.");
			System.out.println("파일 경로 : " + savePath);
			System.out.println();
		}
	}

	private String saveUserInfoFile() {
		File file = new File("usersInfo.csv");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "EUC-KR");
			bw = new BufferedWriter(osw);

			bw.write("번호,이름,아이디,핸드폰 번호,나이,주민번호,잔여 시간,누적 시간,가입일,가입 시간");
			bw.newLine();
			int index = 0;
			for (User user : users.values()) {
				String[] date = user.getJoinDay().split(" ");
				int hours = (int) (user.getSaveTime() / 3600);
				int minutes = (int) ((user.getSaveTime() % 3600) / 60);
				int seconds = (int) (user.getSaveTime() % 60);
				String saveTime = hours + ":" + minutes + ":" + seconds;
				bw.write(++index + "," + user.getName() + "," + user.getId() + "," + user.getPhoneNumber() + ","
						+ user.getAge() + "," + user.getSecuritNumber() + "," + saveTime + "," + user.getTotalTime()
						+ "," + date[0] + "," + date[1]);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		} finally {
			try {
				bw.close();
				osw.close();
				fos.close();
			} catch (Exception e2) {
				System.out.println("Exception : " + e2.getMessage());
			}
		}

		return file.getAbsolutePath();
	}

	private String saveSalesInfoFile() {
		File file = new File("salesInfos.csv");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "EUC-KR");
			bw = new BufferedWriter(osw);

			bw.write("번호,제품명,구매자,구매자 아이디,가격,구매 날짜,구매 시간");
			bw.newLine();
			int index = 0;
			for (SalesInfo salesInfo : salesInfos) {
				String[] date = salesInfo.getDate().split(" ");
				bw.write(++index + "," + salesInfo.getProductName() + "," + salesInfo.getBuyer().getName() + ","
						+ salesInfo.getBuyer().getId() + "," + salesInfo.getPrice() + "," + date[0] + "," + date[1]);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		} finally {
			try {
				bw.close();
				osw.close();
				fos.close();
			} catch (Exception e2) {
				System.out.println("Exception : " + e2.getMessage());
			}
		}

		return file.getAbsolutePath();
	}

	private void stockManagement() { // 음료,과자 재고관리
		System.out.println("관리할 품목을 선택해주세요.");
		System.out.println("번호\t품명\t개수");
		System.out.printf("1\t%s\t%s\n", drink.name, drink.count);
		System.out.printf("2\t%s\t%s\n", snack.name, snack.count);

		int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 2);

		Product selectProduct = null;
		if (choice == 1)
			selectProduct = drink;
		else
			selectProduct = snack;

		System.out.println("1. 발주\t2. 반품");
		choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 2);

		if (choice == 1) {// 발주
			System.out.println("몇 개를 발주넣으겠습니까?");
			int addCount = ValidataionHelper.checkChoiceNumber(scanner);
			if (addCount + selectProduct.count > PRODUCT_MAX_COUNT) {
				System.out.println("재고 최대 갯수는 100개 입니다. 다시 입력해 주세요.");
				stockManagement();
			} else {
				drink.count += addCount;
				System.out.println("현재 " + selectProduct.name + " 재고" + selectProduct.count + "개 있습니다.");
			}
		} else if (choice == 2) {// 반품
			System.out.println("몇 개를 반품하시겠습니까?");
			int subCount = ValidataionHelper.checkChoiceNumber(scanner);
			if (selectProduct.count - subCount >= 0) {
				selectProduct.count -= subCount;
				System.out.println("현재 " + selectProduct.name + " 재고" + selectProduct.count + "개 있습니다.");
			} else {
				System.out.println("갯수가 부족합니다. 다시 입력해주세요.");
				stockManagement();
			}
		}

		System.out.println();
	}
}
