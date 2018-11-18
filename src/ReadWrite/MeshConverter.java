package ReadWrite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import math.Mat;
import math.Vect;
import math.util;
import fem.Model;
import fem.Region;

public class MeshConverter {

public MeshConverter(){}


	public static void main(String[] args){
	
		MeshConverter mc=new MeshConverter();
		//mc.dropUnusedNodes();
		//mc.rescale(1);
		mc.quadToTriang();
		//mc.rotate(30);
		//mc.triangToQuad();
		////mc.quadToHexa();
		//mc.reRegion();
		
		//mc.triFiner(.026,0.0276);
	}
	
	
	
	public void triangToQuad(){
		
		String bun=util.getFile();
		if(bun==null || bun.equals("") )return;
	
		Model model=new Model();
		model.loadMesh(bun);
		
		
		String quadMesh = System.getProperty("user.dir") + "//quadEl.txt";

		
		if(model.elCode==0)
			model.writeMesh32q(quadMesh);
		
	}
	
	
	
	public void triFiner(double r1, double r2){
		
		String bun=util.getFile();
		if(bun==null || bun.equals("") )return;
		Model model=new Model();
		model.loadMesh(bun);
		
		
		String finerMesh = System.getProperty("user.dir") + "//triangElH.txt";

		
		if(model.elCode==0)
			model.writeMesh323(finerMesh, r1,  r2);
		
	}
	
	public void rescale(double scale){
		
		String bun=util.getFile();
		if(bun==null || bun.equals("") )return;
	
		
		Model model=new Model();
		model.loadMesh(bun);
		
		
		String sqaledMesh = System.getProperty("user.dir") + "//scaled.txt";
		
		util.pr(model.node[3].getCoord().norm());
		util.pr(model.node[4].getCoord().norm());
		

		for(int i=1;i<=model.numberOfNodes;i++)
		{
			Vect v=model.node[i].getCoord().times(scale);
		
			if(v.norm()<.027755) v=v.normalized().times(.02775);
				model.node[i].setCoord(v);
		}
		model.writeMesh(sqaledMesh);
		
		
		
	}
	
	
public void quadToTriang(){
		
		String bun=util.getFile();
		if(bun==null || bun.equals("") )return;
		Model model=new Model();
		model.loadMesh(bun);
		

		String triangMesh = System.getProperty("user.dir") + "//triangEl.txt";
		
		if(model.elCode==1)
			model.writeMeshq23(triangMesh);
		
	}

public void quadToTriang(String quad,String tri){
	
	
	Model model=new Model();
	model.loadMesh(quad);
	

	String triangMesh = System.getProperty("user.dir") + "//triangEl.txt";
	
	if(model.elCode==1)
		model.writeMeshq23(tri);
	
}
	
public void quadToHexa(){
	
	String bun=util.getFile();
	if(bun==null || bun.equals("") )return;
	Model model=new Model();
	model.loadMesh(bun);

	String hexMesh = System.getProperty("user.dir") + "//hexaEl.txt";

	if(model.elCode==1)
		model.writeMeshq2h(hexMesh,false);
	
}


public void reRegion(){
	
	String bun=util.getFile();
	if(bun==null || bun.equals("") )return;
	Model model=new Model();
	model.loadMesh(bun);
	double PI=Math.PI;
		for(int i=1;i<=model.numberOfElements;i++){
			if(i==5 || i==6) model.element[i].setRegion(2);
			else
			if(i==14 || i==15) model.element[i].setRegion(3);
			else model.element[i].setRegion(1);
			
			if(model.element[i].getRegion()>=2 && model.element[i].getRegion()<=7){/*
				Vect c=model.getElementCenter(i);
				double ang=util.getAng(c);
		
			 if(Math.abs(ang-PI/2)<PI/12 || Math.abs(ang-3*PI/2)<PI/12 ){
				model.element[i].setRegion(2);
			}
			
			 else  if(Math.abs(ang)<PI/12 || Math.abs(ang-PI)<PI/12 || Math.abs(ang-2*PI)<PI/12){
					model.element[i].setRegion(3);
				}
			else if(Math.abs(ang-150*PI/180)<PI/12 || Math.abs(ang-PI-150*PI/180)<PI/12){
				model.element[i].setRegion(4);
			}
			else if(Math.abs(ang-PI/3)<PI/12 || Math.abs(ang-PI-PI/3)<PI/12){
				model.element[i].setRegion(5);
			}
			else if(Math.abs(ang-PI/6)<PI/12 || Math.abs(ang-PI-PI/6)<PI/12){
				model.element[i].setRegion(6);
			}
			else if(Math.abs(ang-2*PI/3)<PI/12 || Math.abs(ang-PI-2*PI/3)<PI/12){
				model.element[i].setRegion(7);
			}

		*/}
		
		}

		
		int[] mape=new int[model.numberOfElements+1];
		int[][] mapr=new int[model.numberOfRegions+1][2];
		
		int ix=0;
		int[][] vn=new int[model.numberOfElements+1][model.nElVert];
		
		for(int ir=1;ir<=model.numberOfRegions;ir++){
			mapr[ir][0]=ix+1;
		for(int i=1;i<=model.numberOfElements;i++)
			if(model.element[i].getRegion()==ir)
			{
				mape[++ix]=i;
				vn[ix]=model.element[i].getVertNumb();
			}
		mapr[ir][1]=ix;
		}
		
		
		for(int ir=1;ir<=model.numberOfRegions;ir++){
			model.region[ir].setFirstEl(mapr[ir][0]);
			model.region[ir].setLastEl(mapr[ir][1]);
		}
		
		
		for(int i=1;i<=model.numberOfElements;i++){
			model.element[i].setVertNumb(vn[i]);
		}
		
		String file = System.getProperty("user.dir") + "//bunReReg.txt";
		
		model.writeMesh(file);
}


public void dropUnusedNodes(){
	
	String bun=util.getFile();
	if(bun==null || bun.equals("") )return;
	Model model=new Model(bun);
	dropUnusedNodes(model);
}
	
public void dropUnusedNodes(Model model){
	

	model.setInUseNodes();
	
	int ix=0;
	int[] map=new int[1+model.numberOfNodes];
	for(int i=1;i<=model.numberOfNodes;i++)
		if(model.node[i].getMap()>0) map[i]=++ix;

	DecimalFormat formatter;
	if(model.scaleFactor==1)
		formatter= new DecimalFormat("0.0000000");
	else 
		formatter= new DecimalFormat("0.00000");
	String bunFilePath = System.getProperty("user.dir") + "//noUnusedNodes.txt";

	try{
		PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		
		pwBun.println(model.elType);
		pwBun.println("//Number_of_Node");
		pwBun.println(ix);

		pwBun.println("//Number_of_Element");
		pwBun.println(model.numberOfElements);

		pwBun.println("//Number_of_Region");
		pwBun.println(model.numberOfRegions);
		pwBun.println("//Factor");
		pwBun.println(model.scaleFactor);

		double r0=model.node[1].getCoord().norm();

		for(int i=1;i<=model.numberOfElements;i++){
			int[] vertNumb=model.element[i].getVertNumb();
			for(int j=0;j<model.nElVert;j++)
				pwBun.print(map[vertNumb[j]]+",");
			pwBun.println();
		}
		for(int i=1;i<=model.numberOfNodes;i++){ 
			if(model.node[i].getMap()==0) continue;
			
			Vect xyz=model.node[i].getCoord();
			
			for(int j=0;j<model.dim;j++){
		
			pwBun.print(formatter.format(xyz.el[j]*model.scaleFactor)+" ,");
			}
			pwBun.println();	

		}

		for(int i=1;i<=model.numberOfRegions;i++){ 

			pwBun.println(model.region[i].getFirstEl()+","+model.region[i].getLastEl()+","+model.region[i].getName());

		}

		System.out.println();
		System.out.println(" Bun data was written to:");
		System.out.println("    "+bunFilePath);
		System.out.println();
		File file=new File(bunFilePath);
		long filesize = file.length();
		double filesizeInMB=(double)filesize /(1024*1024);

		pwBun.close();
	}
	catch(IOException e){}


}


public void rotate(double deg){
	
	String bun=util.getFile();
	if(bun==null || bun.equals("") )return;
	rotate(bun,deg);
	
}

public void rotate(String bun,double deg){
	
	Model model=new Model();
	model.loadMesh(bun);
	rotate(model,deg, true);

}

public void rotate(Model model,double deg, boolean write){
	
	double rad=Math.PI*deg/180;

	int dim=model.dim;
	Mat R=new Mat(dim,dim);
	if(dim==2) R=util.rotMat2D(rad);
	else
		R=util.rotEuler(new Vect(0,0,1), rad);

	for(int i=1;i<=model.numberOfNodes;i++){
			Vect v=model.node[i].getCoord();
			model.node[i].setCoord(R.mul(v));
		}
		
		if(write){
		String file = System.getProperty("user.dir") + "//bunRotated.txt";
		
		model.writeMesh(file);
		}
}


}


