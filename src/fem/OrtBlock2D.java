package fem;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import javax.media.j3d.Shape3D;
import javax.swing.JComponent;
import javax.swing.JPanel;

import static java.lang.Math.*;
import math.Mat;
import math.Vect;
import math.util;

public class OrtBlock2D{
	
	public Vect J,M,mur,ofdMu,sigma;
	public double yng,pois,rho;
	public boolean[][] disc;
	public double[][] meshSize;
	public double[][] ratioConstant;
	public Color color;
	public double transp;
	public boolean hasJ,hasM,isConductor,isLinear;
	public String material;
	public GeneralPath polygon;
	public Vect[] polyVert;
	public Vect[] blockVert;
	private boolean selected;
	
	public OrtBlock2D(){}

	public OrtBlock2D(Vect[] vert){
		blockVert=vert.clone();
		disc=new boolean[4][2];
		meshSize=new double[4][2];
		ratioConstant=new double[4][2];
		J=new Vect(2);
		M=new Vect(2);
		sigma=new Vect(2);
		mur=new Vect(1,1);
		J=new Vect(2);
		yng=2e11;
		pois=0.3;
		rho=7850;
		
		for(int j=0;j<4;j++){

			for(int k=0;k<2;k++){
			disc[j][k]=true;
			meshSize[j][k]=100;
			ratioConstant[j][k]=2;
			}
		}
		
		material="iron";
		color=new Color(100+(int)(155*random()),100+(int)(155*random()),100+(int)(155*random()));

	}


	public void setVert(Vect[] vert){
			this.blockVert=vert.clone();
	}
	
	public Vect[] getVert(){
		return this.blockVert.clone();
	}
	
	public Rectangle getRect(){
		double xMin=1e10, xMax=0,yMin=1e10,yMax=0;
		for(int i=0;i<polyVert.length;i++){
			if(polyVert[i].el[0]<xMin) xMin=polyVert[i].el[0];
			if(polyVert[i].el[0]>xMax) xMax=polyVert[i].el[0];
			if(polyVert[i].el[1]<yMin) yMin=polyVert[i].el[1];
			if(polyVert[i].el[1]>yMax) yMax=polyVert[i].el[1];
		}

		Rectangle rect=new Rectangle((int)xMin,(int)yMin,(int)(xMax-xMin)+5,(int)(yMax-yMin)+5);
		return rect;
	}
	
	public void setDisc(boolean[][] disc){
		this.disc=disc.clone();
	}

	public boolean[][] getDisc(){
		return this.disc.clone();
	}
	
	public void setMeshSize(double[][] size){
		this.meshSize=size.clone();
	}

	public double[][] getMeshSize(){
		return this.meshSize.clone();
	}
	
	public void setRatioConstant(double[][] constant){
		this.ratioConstant=constant.clone();
	}

	public double[][]  setRatioConstant(){
		return this.ratioConstant.clone();
	}
	
	public void setJ(Vect J){
		this.J=J;
		this.hasJ=true;
			
	}
	
	public void setM(Vect M){
		this.M=M;
		this.hasM=true;
			
	}

	public void setMur(Vect mu){
		this.mur=mu.deepCopy();
			
	}

	public void setMu(Mat muT){
		ofdMu=new Vect(2);
		this.mur=muT.diagonal();
		ofdMu=new Vect(muT.el[0][1],muT.el[0][2]);
		
			
	}

	
	public void setSigma(Vect sigma){
		this.sigma=sigma;
		if(sigma.norm()>0) this.isConductor=true;
		else
			this.isConductor=false;
	
	}
	
	public void setPois(double  pois){
		this.pois=pois;
		
	}
	public void setYng(double  yng){
		this.yng=yng;
		
	}

	public void setRo(double  rho){
		this.rho=rho;
		
	}
	
	public void setColor(Color  color){
		this.color=color;
		
	}
	public void setSelected(boolean b){
		this.selected=b;
		
	}

	public Color getColor(){
		if(selected) 	return Color.CYAN;
		else 
			return this.color;
		
	}
	
	public void setPolygon(Vect[] vert){
     
		polyVert=vert.clone();
		
		double x1Points[] = {vert[0].el[0], vert[1].el[0],vert[2].el[0], vert[3].el[0]};
		double y1Points[] = {vert[0].el[1], vert[1].el[1],vert[2].el[1], vert[3].el[1]};
		polygon = 
		        new GeneralPath(GeneralPath.WIND_EVEN_ODD,
		                        x1Points.length);
		polygon.moveTo(x1Points[0], y1Points[0]);

		for (int index = 1; index < x1Points.length; index++) {
		        polygon.lineTo(x1Points[index], y1Points[index]);
		};

		polygon.closePath();
			
	}
	
	public Vect getPolyCenter(){
		Vect center=new Vect(2);
		for(int i=0;i<polyVert.length;i++)
			center=center.add(polyVert[i]);
		return center.times(.25);
	}
	
	public boolean contains(Vect P){
		
		Vect v1=polyVert[1].sub(polyVert[0]);
		Vect v2=polyVert[3].sub(polyVert[0]);
		Vect v3=polyVert[1].sub(polyVert[2]);
		Vect v4=polyVert[3].sub(polyVert[2]);
	
		double St=abs(v1.cross(v2).el[2])+abs(v3.cross(v4).el[2]);
		
		Vect vp1=P.sub(polyVert[0]);
		Vect vp2=P.sub(polyVert[2]);

		double s1=abs(v1.cross(vp1).el[2]);
		double s2=abs(v2.cross(vp1).el[2]);
		double s3=abs(v3.cross(vp2).el[2]);
		double s4=abs(v4.cross(vp2).el[2]);
	
		double s=s1+s2+s3+s4;
	
		if(abs(s/St-1)<1e-6)
			return true;
		else
		return false;
	}

}