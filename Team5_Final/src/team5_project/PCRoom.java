package team5_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PCRoom {
	private Map<String, User> users; // 회원리스트 key:id, value:User
	private List<Product> orders; // 장바구니
	private Admin admin;
	private PCmanagement pcm;
	private Scanner scanner;
	private String userFilenName;
	private int totalSales;
	private Drink drink;
	private Snack snack;
	private List<SalesInfo> salesInfos;

	public PCRoom() {
		userFilenName = "PcUsers.txt";
		scanner = new Scanner(System.in);
		orders = new ArrayList<Product>();
		salesInfos = new ArrayList<SalesInfo>();
		drink = new Drink(100);
		snack = new Snack(100);
		admin = new Admin(scanner);
		pcm = new PCmanagement(scanner);
		users = initUsers();
		// addTestData();
	}

	public void start() {
		while (true) {
			System.out.println("1. 사용자 모드");
			System.out.println("2. 관리자 모드");
			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 2);
			switch (choice) {
			case 1:
				userMode();
				break;
			case 2:
				admin.adminStart(users, salesInfos, drink, snack);
				break;
			}
		}
	}

	private Map<String, User> initUsers() {
		Map<String, User> users = null;
		File file = new File(userFilenName);

		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				users = (HashMap<String, User>) ois.readObject();
			} catch (Exception e) {
				System.out.println("Exception : " + e.getMessage());
			}
		} else
			users = new HashMap<String, User>();

		return users;
	}

	private void userMode() {
		userLoop: while (true) {
			System.out.println("1. 회원가입");
			System.out.println("2. 시간 충전");
			System.out.println("3. PC이용");
			System.out.println("4. 매점이용");
			System.out.println("5. 이전 메뉴로 이동");
			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 5);
			switch (choice) {
			case 1:
				signUp();
				break;
			case 2:
				addTime();
				break;
			case 3:
				pcm.showMenu(users); // 현재 좌석상태 출력
				break;
			case 4:
				order();
				break;
			case 5:
				break userLoop;
			}
		}
	}

	private void signUp() {
		System.out.println("회원가입을 시작합니다.");
		System.out.print("이름 >> ");
		String name = scanner.next().trim();

		if (!ValidataionHelper.checkName(name)) {
			System.out.println("이름 형식이 맞지 않습니다. 초기 메뉴로 돌아갑니다.");
			return;
		}

		System.out.print("ID >> ");
		String id = scanner.next().trim();
		if (!ValidataionHelper.checkId(id)) {
			System.out.println("ID 형식이 맞지 않습니다. 초기 메뉴로 돌아갑니다.");
			return;
		} else if (users.containsKey(id)) {
			System.out.println("ID가 중복되었습니다. 초기 메뉴로 돌아갑니다.");
			return;
		}

		System.out.print("비밀번호 >> ");
		String password = scanner.next().trim();

		System.out.print("휴대폰번호 (- 포함) >> ");
		String phoneNumber = scanner.next().trim();
		if (!ValidataionHelper.checkPhoneNumber(phoneNumber)) {
			System.out.println("휴대폰번호 형식이 맞지 않습니다. 초기 메뉴로 돌아갑니다.");
			return;
		}

		System.out.print("주민등록번호 (- 포함)>> ");
		String securitNumber = scanner.next().trim(); // 앞뒤로 들어오는 공백을 없애기위해 trim()사용
		if (!ValidataionHelper.checkSecurit(securitNumber)) {
			System.out.println("주민등록번호 형식이 맞지 않습니다. 초기 메뉴로 돌아갑니다.");
			return;
		}

		User user = new User(name, phoneNumber, id, password, securitNumber);
		users.put(user.getId(), user);
		System.out.println(user.getName() + "님 회원가입이 완료되었습니다.");
		System.out.println("");
	}

	private void addTime() {
		System.out.print("시간을 추가 할 회원 ID를 입력하세요 : ");
		String id = scanner.next();
		User targetUser = users.get(id);
		if (targetUser != null) {
			showPcMenu();

			System.out.println("번호를 선택해주세요.");
			int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 3);

			System.out.println("시간 추가를 위해 지불할 돈을 입력하세요.");
			int inputMoney = ValidataionHelper.checkChoiceNumber(scanner);

			switch (choice) {
			case 1:
				if (inputMoney >= 1000) {
					targetUser.setTotalTime(1);
					targetUser.setSaveTime(60);
					System.out.println("1시간이 추가 되었습니다.");
					addSalesInfos(targetUser, "Pc 시간 추가", 1000);
					int change = inputMoney - 1000;
					System.out.println("거스름 돈 입니다. " + change + "원 입니다.");
				} else
					System.out.println("금액이 부족합니다.");

				break;
			case 2:
				if (inputMoney >= 5000) {
					targetUser.setTotalTime(6);
					targetUser.setSaveTime(6 * 60);
					System.out.println("6시간이 충전되었습니다.");
					addSalesInfos(targetUser, "Pc 시간 추가", 5000);
					int change = inputMoney - 5000;
					System.out.println("거스름 돈 입니다. " + change + "원 입니다.");
				} else
					System.out.println("금액이 부족합니다.");
				break;
			case 3:
				if (inputMoney >= 10000) {
					targetUser.setTotalTime(15); // 테스트
					targetUser.setSaveTime(15 * 60);
					System.out.println("15시간이 충전되었습니다.");
					addSalesInfos(targetUser, "Pc 시간 추가", 10000);
					int change = inputMoney - 10000;
					System.out.println("거스름 돈은 " + change + "원 입니다.");
				} else
					System.out.println("금액이 부족합니다.");
				break;
			}

		} else {
			System.out.println("ID를 확인해주세요.");
		}
	}

	private void addSalesInfos(User buyer, String productName, int price) {
		salesInfos.add(new SalesInfo(buyer, productName, price));
	}

	private void showPcMenu() {
		System.out.println("[1] 1시간\t가격  : 1000원 ");
		System.out.println("[2] 6시간\t가격  : 5000원 ");
		System.out.println("[3] 15시간\t가격 : 10000원 ");
		System.out.println("1~3번중에 맞는 요금으로 선택해 주세요.");
	}

	private void order() {
		System.out.print("매점 이용 회원 ID를 입력하세요 : ");
		String id = scanner.next();
		User targetUser = users.get(id);
		if (targetUser != null) {
			int choice = 0;
			int drinkCount = 0;
			int snackCount = 0;
			int totalPrice = 0;
			Orderloop: while (choice != 4) {
				showMenu(drinkCount, snackCount);
				choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 4);
				switch (choice) {
				case 1: // 음료수 장바구니에 담음
					if (drink.count - drinkCount > 0) {
						orders.add(new Drink());
						totalPrice += drink.price;
						drinkCount++;
						productsPrint(totalPrice);
					} else
						System.out.println("품절된 상품입니다.");
					break;
				case 2: // 과자 장바구니에 담음
					if (snack.count - snackCount > 0) {
						orders.add(new Snack());
						totalPrice += snack.price;
						snackCount++;
						productsPrint(totalPrice);
					} else
						System.out.println("품절된 상품입니다.");
					break;
				case 3: // 결제
					if (orders.size() > 0) {
						checkChange(totalPrice);
						choice = 0;
						drink.count -= drinkCount;
						snack.count -= snackCount;
						addSalesInfos(targetUser, "매점 이용", 5000);
						break Orderloop;

					} else {
						System.out.println("제품을 선택해주세요.");
						System.out.println(" ");
					}
						
					break;
				case 4: // 주문취소
					System.out.println("주문이 취소되었습니다.");
					totalPrice = 0;
					drinkCount = 0;
					snackCount = 0;
					orders.clear();
					break Orderloop;
				}
			}
		} else
			System.out.println("ID를 확인해주세요.");
	}

	private void showMenu(int orderDrink, int orderSnack) {
		System.out.println("메뉴를 선택해주세요.");
		String isSoldout = drink.count - orderDrink < 1 ? " (품절)" : "";
		System.out.println("[1] " + drink.toString() + isSoldout);

		isSoldout = snack.count - orderSnack < 1 ? " (품절)" : "";
		System.out.println("[2] " + snack.toString() + isSoldout);

		System.out.println("[3] 결제하기");
		System.out.println("[4] 주문취소");
	}

	private void checkChange(int totalPrice) {
		System.out.println("주문확인");

		productsPrint(totalPrice);

		int choice = ValidataionHelper.checkChoiceNumber(scanner, 1, 2);
		switch (choice) {
		case 1:
			System.out.println("지불 금액을 입력해주세요.");
			while (true) {
				int payout = ValidataionHelper.checkChoiceNumber(scanner);
				if (payout < totalPrice) {
					payout = 0;
					System.out.println("금액이 부족합니다.");
					continue;
				} else {
					totalSales += totalPrice;
					System.out.println("거스름돈: " + (payout - totalPrice));
					System.out.println("주문이 완료되었습니다.");
					System.out.println("");
					break;
				}
			}
			break;

		case 2:
			System.out.println("결제가 취소되었습니다.");
			totalPrice = 0;
			orders.clear();
			break;

		}
	}

	private void productsPrint(int totalPrice) {
		System.out.println("===========================");
		System.out.println("주문목록");
		for (Product product : orders) {
			System.out.println(product.name + "\t" + product.price + "원");
		}
		System.out.println("===========================");
		System.out.println("총 금액: " + totalPrice + "원");
		System.out.println("[1]결제    [2]주문취소");
	}

	private void addTestData() {
		users.put("oh", new User("오형남", "010-3642-3603", "oh", "oh", "961220-1111111"));
		users.put("lee", new User("이하연", "010-2108-0124", "lee", "lee", "940124-1111111"));
		users.put("kwon", new User("권태일", "010-3148-1088", "Kwon", "Kwon", "880228-1111111"));
		users.put("park", new User("박성호", "010-5233-3208", "park", "park", "900226-1111111"));
		users.put("ko", new User("코찔이", "010-3148-1088", "ko", "ko", "880228-1111111"));
		users.put("dodo", new User("도도", "010-3642-3603", "dodo", "dodo", "961220-1111111"));
		users.put("goldcow", new User("황소", "010-5233-3208", "goldcow", "goldcow", "890827-1111111"));
		users.put("i7027", new User("임경균", "010-1234-1234", "i7027", "i7027", "010727-3111111"));
		users.put("one", new User("김일겸", "010-4567-4567", "one", "one", "021111-3111111"));

	}
}
