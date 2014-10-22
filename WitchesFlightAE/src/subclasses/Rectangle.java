package subclasses;

public class Rectangle {

	public int x;
	public int y;
	public int width;
	public int height;
	
	public Rectangle(int x, int y, int width, int height){
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(float x, float y, int width, int height){
		
		this.x = (int)x;
		this.y = (int)y;
		this.width = width;
		this.height = height;
	}
}
