package team5_project;

public class Drink extends Product {
	public Drink() {
		this.name = "음료수";
		this.price = 1000;
		this.count--;
	}

	public Drink(int count) {
		this.name = "음료수";
		this.price = 1000;
		this.count = count;
	}

	@Override
	public String toString() {
		return this.name + " 가격: " + this.price + "원";
	}
}