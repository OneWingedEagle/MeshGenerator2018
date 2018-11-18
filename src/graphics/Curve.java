package graphics;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.math.plot.Plot2DPanel;

import math.Vect;
import math.util;
public class Curve extends JPanel{

	JFrame frame;

	
	public Curve(double[][] array, int width, int height){

		Plot2DPanel plot = new Plot2DPanel();
		double[] x=new double[array.length];
		int L=array.length;
		int N=array[0].length;
		
		for(int i=0;i<L;i++)
			x[i]=array[i][0];
	
		double[] y=new double[array.length];
		for(int n=1;n<N;n++){
		for(int i=0;i<L;i++)
			y[i]=array[i][n];
		
			plot.addLinePlot("y(x)",x, y);
		
		}
		
		plot.setAxisLabel(0,"x");
		plot.setAxisLabel(1,"y");
		setFrame("y-x plot",width,height);
		frame.add(plot);

	}
	
	

	private void setFrame(String name,int width, int height){
		frame=new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width,height);
	}

	public void show(boolean b){
		frame.setVisible(b);
	}

}