package drawingPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import math.Vect;
import fem.OrtBlock2D;

public class Canvas2D extends JPanel implements ActionListener {
    public OrtBlock2D[] blk=new OrtBlock2D[100];
    public Image img;
    private double factor=3000;
    public int nBlocks=0;


    public Canvas2D() {
    	setBackground(Color.BLACK);

    }

    public void actionPerformed(ActionEvent e) {
            repaint();
    }

    public void paint(Graphics g) {
    	super.paint(g);

            Graphics2D g2d = (Graphics2D) g;
            for(int i=0;i<nBlocks;i++){
          
            Color color=blk[i].getColor();
            g2d.setColor(color);
 
           blk[i].setPolygon(getPolyVert(i));
            g2d.fill (blk[i].polygon);
        
    	}
          
    }

    public void addBlock(Vect[] P){

    	 nBlocks++;
    	 blk[nBlocks] = new OrtBlock2D(P);
    	repaint();
    	
    }
    
    public void setVert(int i,Vect[] vert){
    	blk[i].blockVert=vert.clone();
    }
    
    public void addSpace(double[] bound){
    	Vect[] vert=new Vect[4];
    	vert[0]=new Vect(-1000,-1000);
    	vert[1]=new Vect(1000,-1000);
    	vert[2]=new Vect(1000,1000);
    	vert[3]=new Vect(-1000,1000);

    	 blk[0] = new OrtBlock2D(vert);
    }
    
    public Vect[] getPolyVert(int nb){
    	int nVert=blk[nb].blockVert.length;
    	Vect[] polyVert=new Vect[nVert];
 
    	Rectangle rc=this.getBounds();
        double DX=rc.getMaxX()-rc.getMinX();
        double DY=rc.getMaxY()-rc.getMinY();
        double Dm=Math.max(DX,DY);
        double xm=DX/2;
        double ym=DY/2;
        double cf=Dm/factor;

        Vect shift=new Vect(xm,ym);
        for(int j=0;j<nVert;j++)
      		polyVert[j]=blk[nb].blockVert[j].times(cf).add(shift);

      	  return polyVert;
    }
    

   
}
/*public class Canvas2D extends JPanel   {
	double width;
	double  height;
	

	public Canvas2D() {
		}
	
	

	 public void paintComponent( Graphics g ) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;

         Line2D line = new Line2D.Double(10, 10, 40, 40);
         g2.setColor(Color.blue);
         g2.setStroke(new BasicStroke(10));
         g2.draw(line);
         
 double x=500,y=400,diameter=150;
		 Ellipse2D.Double circle =
         new Ellipse2D.Double(x, y, diameter, diameter);
		// g2.draw(circle);
         g2.fill(circle);
      }

	
	public static void main ( String[] args){
		JFrame frame =new JFrame();
		frame.setSize(new Dimension(300,300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Canvas2D c2=new Canvas2D();
		frame.add(c2);
		frame.setVisible(true);
	}


}*/