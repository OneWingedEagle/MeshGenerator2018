package graphics;
import java.awt.Color;

import math.Vect;
import math.util;
import fem.Model;
import javax.media.j3d.TransformGroup;

public class  EdgeField extends TransformGroup{
	

	private TransformGroup Jms;
	private Arrow[] arrow;
	private int nRegEdges,vectMode,dim;
	private ColorBar cBar;
	private Vect[] P;
	private Vect[] V;
	private double scaleFactor=1;
	private double arrowSize=1;;

	public  EdgeField(){	}


	public  EdgeField(Model model, int nr,int mode,int vectMode)
		{
		this.dim=model.dim;
		int edgeNumber;
		this.nRegEdges=0;
		this.vectMode=vectMode;
		boolean[] edgeCounted=new boolean[model.numberOfEdges+1];
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
			for(int j=0;j<12;j++)
			{
				edgeNumber=model.element[i].getEdgeNumb(j);
				if(!edgeCounted[edgeNumber]){
					edgeCounted[edgeNumber]=true;
					this.nRegEdges++;
				}
			}
		

		this.P=new Vect[this.nRegEdges];
		this.V=new Vect[this.nRegEdges];
		

		int ix=0;
		for(int i=1;i<=model.numberOfEdges;i++)
			if(edgeCounted[i])
			{

				this.P[ix]=model.node[model.edge[i].endNodeNumber[0]].getCoord().add(model.node[model.edge[i].endNodeNumber[1]].getCoord()).times(.5);
				if(mode==0)
					this.V[ix]=model.node[model.edge[i].endNodeNumber[1]].getCoord().sub(model.node[model.edge[i].endNodeNumber[0]].getCoord()).normalized().times(model.edge[i].Jms);
					else if(mode==1)
						this.V[ix]=model.node[model.edge[i].endNodeNumber[1]].getCoord().sub(model.node[model.edge[i].endNodeNumber[0]].getCoord()).times(model.edge[i].Au);
				ix++;
			}

		util.pr(model.JmsuMax);

		this.arrow=new Arrow[this.nRegEdges];
		double Vmin=0,Vmax=1;
		if(mode==0) {Vmin=0; Vmax=model.JmsuMax; 	}
		else if(mode==1) {Vmin=0; Vmax=model.AuMax;}

		this.cBar=new ColorBar(Vmin,Vmax);
		
		if(Vmax>0)
			this.scaleFactor=.2/Vmax;
		double magn;
		Color color;

	
		this.Jms=new TransformGroup();
			for(int j=0;j<this.nRegEdges;j++){	

				magn=this.V[j].norm();
				
				this.arrowSize=this.scaleFactor*magn;
				color=this.cBar.getColor(magn);
				this.arrow[j]=new Arrow(dim,this.P[j],this.V[j],this.arrowSize,color,vectMode);
				this.Jms.addChild(this.arrow[j]);

				}
	
	}

	public void rescale(double a){
		if(this.Jms.getParent()!=null){	

				removeChild(this.Jms);
			double Fn=0;
				for(int j=0;j<this.nRegEdges;j++){	
					Fn=this.V[j].norm();
					this.Jms.removeChild(this.arrow[j]);
					this.arrow[j]=new Arrow(dim,this.P[j],this.V[j],a/this.scaleFactor*this.arrow[j].scale,this.cBar.getColor(Fn),this.vectMode);
					this.Jms.addChild(this.arrow[j]);
				
			}
				addChild(this.Jms);
		}
			
		this.scaleFactor=a;

		}


	public void showField(boolean b){
		if(b){

			if(this.Jms.getParent()==null)		
			addChild(this.Jms);
			
		}
		else{

			removeChild(this.Jms);
		}
			
		}
	public double getVectorScale(){
		return this.scaleFactor;
	}
	

}
