package graphics;
import java.awt.Color;

import math.Vect;
import math.util;
import fem.Model;
import javax.media.j3d.TransformGroup;

public class  NodalField extends TransformGroup{
	

	private TransformGroup field;
	private Arrow[] arrow;
	private int nRegNodes,vectMode,dim;
	private Vect[] P;
	private Vect[] V;
	private double scaleFactor=1;
	private double arrowSize=1;;

	public NodalField(){	}

	public NodalField(Model model, ColorBar cBar,int[][] nodeNumber,int mode,int vectMode){
		
		this.vectMode=vectMode;

		this.dim=model.dim;
		boolean[] counted=new boolean[model.numberOfNodes+1];
		for(int i=0;i<nodeNumber.length;i++)
			for(int j=0;j<nodeNumber[0].length;j++)
				counted[nodeNumber[i][j]]=true;
		
		
		
		nRegNodes=0;
		for(int i=1;i<counted.length;i++)
			if(counted[i]) nRegNodes++;
		
		
		P=new Vect[nRegNodes];
		V=new Vect[nRegNodes];
		
		int ix=0;

		for(int i=1;i<counted.length;i++)	
			if(counted[i])
			{
			
			
				P[ix]=model.node[i].getCoord();
				V[ix]=model.node[i].getNodalVect(mode).v3();
				ix++;
			}
		
		arrow=new Arrow[nRegNodes];
		double Vmax=1;
		if(mode==-1) {Vmax=model.uMax; 	}
		else if(mode==1) { Vmax=model.FreluctMax;}
		else if(mode==2) { Vmax=model.FmsMax;}
		
		//cBar=new ColorBar(Vmin,Vmax);
		
		if(Vmax>0)
			scaleFactor=.2/Vmax;
		double magn;
		Color color;
		
		
		field=new TransformGroup();
			for(int j=0;j<nRegNodes;j++){	
				
				magn=V[j].norm();

				arrowSize=scaleFactor*magn;
				color=cBar.getColor(magn);
				arrow[j]=new Arrow(this.dim,P[j],V[j],arrowSize,color,vectMode);
				field.addChild(arrow[j]);

				}
	
	}

	public NodalField(Model model,ColorBar cBar, int nr,int mode,int vectMode)
		{
		this.dim=model.dim;
		int nodeNumber;
		nRegNodes=0;
		this.vectMode=vectMode;
		boolean[] nodeCounted=new boolean[model.numberOfNodes+1];
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
		{
		for(int j=0;j<model.nElVert;j++)
			{

			nodeNumber=model.element[i].getVertNumb(j);
				if(!nodeCounted[nodeNumber]){
						if(model.node[nodeNumber].getNodalVect(mode)==null) continue;
						if(model.node[nodeNumber].getNodalVect(mode).norm()==0 ) continue;
					nodeCounted[nodeNumber]=true;
					nRegNodes++;
				}
			}
		}
		
		P=new Vect[nRegNodes];
		V=new Vect[nRegNodes];
		

		int ix=0;
	
		for(int i=1;i<=model.numberOfNodes;i++)
			if(nodeCounted[i])
			{

				P[ix]=model.node[i].getCoord();
				V[ix]=model.node[i].getNodalVect(mode);
				ix++;
				
				
			}


		arrow=new Arrow[nRegNodes];
		double Vmax=1;
		if(mode==-1) {Vmax=model.uMax; 	}
		else if(mode==1) {Vmax=model.FreluctMax;}
		else if(mode==2) { Vmax=model.FmsMax;}

		if(Vmax>0)
			scaleFactor=4*model.maxEdgeLength/Vmax;
		double magn;
		Color color;

	
		field=new TransformGroup();
			for(int j=0;j<nRegNodes;j++){	

				if(V[j]==null)
					V[j]=new Vect(3);
				magn=V[j].norm();
				arrowSize=scaleFactor*magn;
				color=cBar.getColor(magn);
				arrow[j]=new Arrow(this.dim,P[j],V[j],arrowSize,color,vectMode);
				field.addChild(arrow[j]);

				}
	
	}

	public void rescale(ColorBar cBar,double a){
		showField(false);
				field=new TransformGroup();
			double Fn=0;
				for(int j=0;j<this.nRegNodes;j++){	
					Fn=V[j].norm();
					field.removeChild(arrow[j]);
					
					arrow[j]=new Arrow(this.dim,P[j],V[j],a/scaleFactor*arrow[j].scale,cBar.getColor(Fn),this.vectMode);
					field.addChild(arrow[j]);
			}
				addChild(field);
		
			
		scaleFactor=a;
		
		showField(true);

		}

	public void showField(boolean b){
		if(b){

			if(field.getParent()==null)		
			addChild(field);
			
		}
		else{

			removeChild(field);
		}
			
		}
	public double getVectorScale(){
		return this.scaleFactor;
	}
	

}
