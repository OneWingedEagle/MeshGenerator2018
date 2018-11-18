package graphics;
import java.awt.Color;
import math.Vect;
import math.util;
import fem.Model;
import javax.media.j3d.TransformGroup;
import javax.swing.JOptionPane;

import static java.lang.Math.*;

public class  VectField extends TransformGroup{
	

	private TransformGroup flux;
	private TransformGroup current=new TransformGroup();
	private PyramidArrow[] arrowB;
	private PyramidArrowX[] arrowX;
	private PyramidArrow[] arrowJ;
	private Arrow[] arrow;
	private int nReg,nElements,nFirst,nVer=8,vectMode,dim,arrowMode;
	//private ColorBar cBar;
	private Vect[] P;
	private Vect[] V;
	private double scaleFactor=1;
	private double arrowSize=1;
	private boolean isH;
	public  VectField(int nr,int elFirst,int elLast,Color color,int vectMode,int arrowMode){	
	
		 this.nReg=nr;
	
		 this.nFirst=elFirst;
		this.nElements=elLast-this.nFirst+1;
		this.P=new Vect[this.nElements];
		this.V=new Vect[this.nElements];
		this.vectMode=vectMode;
		this.arrowMode=arrowMode;
		this.flux=new TransformGroup();
	
	}
		

	
	public void setFlux(Model model, ColorBar cBar,int n,boolean isH)
		{
		this.dim=model.dim;

		this.isH=isH;
		if(this.vectMode==-1)
			this.arrowX=new PyramidArrowX[this.nElements];
		else if(this.vectMode==0)
		this.arrowB=new PyramidArrow[this.nElements];
		else if(this.vectMode==1 )
			this.arrow=new Arrow[this.nElements];
		
		double Vmin,Vmax=0;
		double magn;
		if(n==0){
			Vmin=model.Bmin;
			Vmax=model.Bmax;
		}
	
		if(Vmax>0)
		this.scaleFactor=2/Vmax*model.minEdgeLength;
	
		//this.cBar=new ColorBar(Vmin,Vmax);	
	
		removeChild(this.flux);
		this.flux=new TransformGroup();
		Vect arrow=new Vect(this.dim);
		setCenters(model);
		setVectors(model,n);
			for(int j=0;j<this.nElements;j++){	
				if(this.V[j]!=null)
					arrow=this.V[j];
				else
					arrow=new Vect(this.dim);
		
				magn=arrow.norm();
	
				this.arrowSize=magn*this.scaleFactor;
				if(this.vectMode==-1){
				this.arrowX[j]=new PyramidArrowX(this.P[j],arrow,this.arrowSize,cBar.getColor(magn));
				this.flux.addChild(this.arrowX[j]);
				}
				if(this.vectMode==0){
					this.arrowB[j]=new PyramidArrow(this.P[j],arrow,this.arrowSize,cBar.getColor(magn));
					this.flux.addChild(this.arrowB[j]);
					}
				else if(this.vectMode==1){
					
						this.arrow[j]=new Arrow(this.dim,this.P[j],arrow,this.arrowSize,cBar.getColor(magn),this.arrowMode);
					
						this.flux.addChild(this.arrow[j]);
					}

				}		
		
	}
	
	public void setCenters(Model model){
	for(int j=0;j<this.nElements;j++)	
		this.P[j]=model.getElementCenter(model.region[this.nReg].getFirstEl()+j);
		
	}
	
	public void setVectors(Model model,int n){
		for(int j=0;j<this.nElements;j++){	
			if(n==0)
			this.V[j]=model.element[model.region[this.nReg].getFirstEl()+j].getB();	
			else
			this.V[j]=model.element[model.region[this.nReg].getFirstEl()+j].getJe();	
		
		}
		}

	
	public void rescale(ColorBar cBar,double a){
		
		if(this.flux.getParent()!=null){
			removeChild(this.flux);
		double Bn=0;
			for(int j=0;j<this.nElements;j++){	
			
				if(this.V[j]==null)
					Bn=0;
				else
					Bn=this.V[j].norm();
				
				if(this.vectMode==-1){
				this.flux.removeChild(this.arrowX[j]);
				this.arrowX[j]=new PyramidArrowX(this.P[j],this.V[j],a/this.scaleFactor*this.arrowB[j].height,cBar.getColor(Bn));
				this.flux.addChild(this.arrowX[j]);
				}
				if(this.vectMode==0){
					this.flux.removeChild(this.arrowB[j]);
					this.arrowB[j]=new PyramidArrow(this.P[j],this.V[j],a/this.scaleFactor*this.arrowB[j].height,cBar.getColor(Bn));
					this.flux.addChild(this.arrowB[j]);
					}
				else
					if(this.vectMode==1){
						this.flux.removeChild(this.arrow[j]);
						this.arrow[j]=new Arrow(this.dim,this.P[j],this.V[j],a/this.scaleFactor*this.arrow[j].scale,(this.isH)?Color.gray:cBar.getColor(Bn),(this.isH)?1:this.arrowMode);
						this.flux.addChild(this.arrow[j]);
						}
			
			
		}
			addChild(this.flux);
	}
		
		this.scaleFactor=a;
	
	}
	
	/*
	public void setJ0(Model model)
	{
		int dim=model.dim;

	double meshSize=model.maxEdgeLength;
	double maxDim=model.maxDim;
	this.arrowJ=new PyramidArrow[this.nElements];
	Vect J;
	double Jmax=model.Jmax;
	double Jmin=model.Jmin;
	double Jn,arrowJscale;
//	this.cBar=new ColorBar(Jmin,Jmax);
	Vect P;

		for(int j=0;j<this.nElements;j++){	
			P=new Vect(dim);
			for(int k=0;k<this.nVer;k++)
				P=P.add(model.node[model.element[model.region[this.nReg].getFirstEl()+j].getVertNumb(k)].getCoord());
			P=P.times(1.0/this.nVer);
			J=model.element[model.region[this.nReg].getFirstEl()+j].getJ();	
			Jn=J.norm();
			arrowJscale=2*meshSize/maxDim*Jn/Jmax;
			this.current.removeChild(this.arrowJ[j]);
			this.arrowJ[j]=new PyramidArrow(P,J,arrowJscale,cBar.getColor(J.norm()));
			this.current.addChild(this.arrowJ[j]);

			}

}*/

	public void showFlux(boolean b){
		if(b){

			if(this.flux.getParent()==null)		
			addChild(this.flux);
			
		}
		else{

			removeChild(this.flux);
		}
			
		}
	
	public void showJ(boolean b){
		if(b){

			if(this.current.getParent()==null)
			addChild(this.current);
		}
		else{
			
			removeChild(this.current);

		}
			
		}

	public double getVectorScale(){
		return this.scaleFactor;
	}


}
