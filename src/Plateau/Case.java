package Plateau;

public class Case {
	private int x, y;

	public enum SIDE {
		H, B, D, G
	};
	private Boolean left,right,top,bottom;


	public Case(int x, int y) {
		this.x = x;
		this.y = y;
		left=right=top=bottom=false;
		}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Boolean getLeft() {
		return left;
	}

	public void setLeft(Boolean left) {
		this.left = left;
	}

	public Boolean getRight() {
		return right;
	}

	public void setRight(Boolean right) {
		this.right = right;
	}

	public Boolean getTop() {
		return top;
	}

	public void setTop(Boolean top) {
		this.top = top;
	}

	public Boolean getBottom() {
		return bottom;
	}

	public void setBottom(Boolean bottom) {
		this.bottom = bottom;
	}

	public static SIDE getSide(String s) {
		SIDE facing=null;
		switch (s) {
		case "H":
			facing=SIDE.H;
			break;
		case "B":
			facing=SIDE.B;
			break;
		case "D":
			facing=SIDE.D;
			break;
		case "G":
			facing=SIDE.G;
			break;
		}
		return facing;
	}
	public String toString(){
		return "Je suis la case "+x+" et "+y;
	}
}
