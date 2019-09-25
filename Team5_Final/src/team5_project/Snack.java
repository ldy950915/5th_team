package team5_project;

public class Snack extends Product {
	public Snack() {
		this.name = "과자";
		this.price = 1000;
		count--;
	}

	public Snack(int count) {
		this.name = "과자";
		this.price = 1000;
		this.count = count;
	}

	@Override
	public String toString() {
		return this.name + " 가격: " + this.price + "원";
	}
}