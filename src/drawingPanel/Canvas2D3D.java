package drawingPanel;

import java.awt.Color;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;


	public class Canvas2D3D extends Canvas3D
	{
	public Canvas2D3D (GraphicsConfiguration config)
	{
	super(config);
	
	}
	
	public void postRender()
	{
	J3DGraphics2D draw;
	draw = this.getGraphics2D();
	draw.setColor(Color.red);
	draw.drawLine(100,100,800,100);
	draw.drawLine(800,100,800,600);
	draw.drawLine(800,600,100,600);
	draw.drawLine(100,600,100,100);
	draw.flush(true);
	}

public void postRender(String s, int i, int j)
{
	J3DGraphics2D draw;
	draw = this.getGraphics2D();
	draw.setColor(Color.red);
	draw.drawLine(300,200,500,400);
	draw.drawString(s,i,j);
	draw.flush(true);
}


}
