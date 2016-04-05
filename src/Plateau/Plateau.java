package Plateau;

import java.awt.Point;

public class Plateau {
	private Point cible;
	private Color color;
	private Point robotR;
	private Point robotV;
	private Point robotJ;
	private Point robotB;
	private Case[][] plateau;

	public Plateau() {
		plateau = new Case[16][16];
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				plateau[i][j] = new Case(i, j);
				if(i==0)
					plateau[i][j].setTop(true);
				if(i==15)
					plateau[i][j].setBottom(true);
				if(j==0)
					plateau[i][j].setLeft(true);
				if(j==15)
					plateau[i][j].setRight(true);

			}
		}
	}
	public boolean noRobot(int x ,int y){
		boolean ans=true;
		if(robotR.x==x && robotR.y==y)
			ans=false;
		if(robotV.x==x && robotV.y==y)
			ans=false;
		if(robotB.x==x && robotB.y==y)
			ans=false;
		if(robotJ.x==x && robotJ.y==y)
			ans=false;
		return ans;
	}
	public boolean canMove(String s) {
		char color = s.charAt(0);
		char move = s.charAt(1);
		boolean answer = false;
		Point robot = null;
		switch (color) {
		case 'R':
			robot=robotR;
			break;
		case 'B':
			robot=robotB;
			break;
		case 'V':
			robot=robotV;
			break;
		case 'J':
			robot=robotJ;
			break;
		}
		switch (move) {
		case 'H':
			if(!plateau[robot.x][robot.y].getTop() && noRobot((robot.x)-1, robot.y)){
				answer=true;
				robot.setLocation((robot.x)-1, robot.y);
			}
			break;
		case 'B':
			if(!plateau[robot.x][robot.y].getBottom() && noRobot((robot.x)+1, robot.y) ){
				answer=true;
				robot.setLocation((robot.x)+1, robot.y);
			}
			break;
		case 'G':
			if(!plateau[robot.x][robot.y].getLeft() && noRobot(robot.x, (robot.y)-1) ){
				answer=true;
				robot.setLocation((robot.x), (robot.y)-1);
			}
			break;
		case 'D':
			if(!plateau[robot.x][robot.y].getRight() && noRobot(robot.x, (robot.y)+1) ){
				answer=true;
				robot.setLocation((robot.x), (robot.y)+1);
			}
			break;
		}
		// switch(color){
		// case 'R':
		// xrobot=robotR.x;
		// yrobot=robotR.y;
		// switch (move) {
		// case 'H':
		// if(!plateau[xrobot][yrobot].getTop()){
		// answer=true;
		// robotR.setLocation(xrobot-1, yrobot);
		// }
		// break;
		// case 'B':
		// if(!plateau[xrobot][yrobot].getTop()){
		// answer=true;
		// robotR.setLocation(xrobot-1, yrobot);
		// }
		// break;
		// case 'G':
		// if(!plateau[xrobot][yrobot].getTop()){
		// answer=true;
		// robotR.setLocation(xrobot-1, yrobot);
		// }
		// break;
		// case 'D':
		// if(!plateau[xrobot][yrobot].getTop()){
		// answer=true;
		// robotR.setLocation(xrobot-1, yrobot);
		// }
		// break;
		//
		// }
		// return true;
		// case 'V':
		// return true;
		// case 'B':
		// return true;
		// case 'J':
		// return true;
		//
		// }
		return answer;
	}

	// SETTERS AND GETTERS
	public Case getCase(int i, int j) {
		return plateau[i][j];
	}

	public Point getCible() {
		return cible;
	}

	public void setCible(Point cible) {
		this.cible = cible;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Point getRobotR() {
		return robotR;
	}

	public void setRobotR(Point robotR) {
		this.robotR = robotR;
	}

	public Point getRobotV() {
		return robotV;
	}

	public void setRobotV(Point robotV) {
		this.robotV = robotV;
	}

	public Point getRobotJ() {
		return robotJ;
	}

	public void setRobotJ(Point robotJ) {
		this.robotJ = robotJ;
	}

	public Point getRobotB() {
		return robotB;
	}

	public void setRobotB(Point robotB) {
		this.robotB = robotB;
	}

	public Color getColor(String s) {
		Color color = null;
		switch (s) {
		case "R":
			color = Color.R;
			break;
		case "B":
			color = Color.B;
			break;
		case "J":
			color = Color.J;
			break;
		case "V":
			color = Color.V;
			break;
		}
		return color;
	}

	public enum Color {
		R, B, J, V
	};

}
