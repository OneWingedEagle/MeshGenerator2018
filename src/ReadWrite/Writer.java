package ReadWrite;

import static java.lang.Math.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import fem.EdgeSet;
import fem.Model;
import math.Vect;
import math.util;

public class Writer {
	
	public static void main(String[] args) throws IOException{
		Writer wr=new Writer();
		String nodeFile ="C:\\Users\\Hassan\\Desktop\\Triangle\\model.node";
		String elFile = "C:\\Users\\Hassan\\Desktop\\Triangle\\model.1.ele";
		//wr.writeNodeX(nodeFile);
		
	//	wr.writeMesh(nodeFile,elFile);
		
		}
	
	public void writeMesh(Model model,String bunFilePath , boolean deformed){

		DecimalFormat formatter;
		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000000");
		else 
			formatter= new DecimalFormat("0.00000");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		
			pwBun.println(model.elType);
			pwBun.println("//Number_of_Node");
			pwBun.println(model.numberOfNodes);

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
					pwBun.print(vertNumb[j]+",");
				pwBun.println();
			}
			for(int i=1;i<=model.numberOfNodes;i++){ 
				Vect xyz;;
			
					xyz=model.node[i].getCoord();
				for(int j=0;j<model.dim;j++){
			pwBun.print(formatter.format((xyz.el[j])*model.scaleFactor)+" ,");
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
		if(model.dim==3){
			String bun2D=System.getProperty("user.dir")+"\\bun2D.txt";
			writeMesh322(model, bun2D,false);
	
		}
	}


	public void writeMesh322(Model model,String bunFilePath , boolean deformed){
		DecimalFormat formatter;
		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000");
		else 
			formatter= new DecimalFormat("0.00");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		

			pwBun.println("quadrangle");
			pwBun.println("//Number_of_Node");
			pwBun.println(model.numberOfNodes/2);

			pwBun.println("//Number_of_Element");
			pwBun.println(model.numberOfElements);

			pwBun.println("//Number_of_Region");
			pwBun.println(model.numberOfRegions);
			pwBun.println("//Factor");
			pwBun.println(model.scaleFactor);

			for(int i=1;i<=model.numberOfElements;i++){ 
				int[] vertNumb=model.element[i].getVertNumb();
				for(int j=4;j<model.nElVert;j++)
					pwBun.print(vertNumb[j]+",");
				pwBun.println();
			}

			int m=0;

			for(int i=1;i<=model.numberOfNodes/2;i++){ 
				Vect xyz;;

				Vect v=new Vect(model.dim);
				v.rand();
				v=v.add(-.5).times(.1);


				xyz=model.node[i].getCoord();
				if(m==1)
					if(xyz.norm()<.99) xyz=xyz.add(v);

				for(int j=0;j<2;j++){

					pwBun.print(formatter.format((xyz.el[j])*model.scaleFactor)+" ,");
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
			//System.out.println(" Size of the generated mesh file :"+trunc(filesizeInMB,3)+" MB");

			pwBun.close();
		}
		catch(IOException e){}
	}

	public void writeMeshq2h(Model model,String bunFilePath , boolean deformed){

		double h=.02;

		DecimalFormat formatter;
		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000000");
		else 
			formatter= new DecimalFormat("0.0000");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		
			pwBun.println("hexahedron");
			pwBun.println("//Number_of_Node");
			pwBun.println(2*model.numberOfNodes);

			pwBun.println("//Number_of_Element");
			pwBun.println(model.numberOfElements);

			pwBun.println("//Number_of_Region");
			pwBun.println(model.numberOfRegions);
			pwBun.println("//Factor");
			pwBun.println(model.scaleFactor);

			for(int i=1;i<=model.numberOfElements;i++){
				int[] vertNumb=model.element[i].getVertNumb();

				for(int j=4;j<8;j++)
					pwBun.print((model.numberOfNodes+vertNumb[j-4])+",");
				for(int j=0;j<model.nElVert;j++)
					pwBun.print(vertNumb[j]+",");

				pwBun.println();
			}
			for(int i=1;i<=model.numberOfNodes;i++){ 
				Vect xyz;;

				xyz=model.node[i].getCoord();
				for(int j=0;j<model.dim;j++)
					pwBun.print(formatter.format(xyz.el[j]*model.scaleFactor)+" ,");
				pwBun.print(formatter.format(0*model.scaleFactor)+" ,");

				pwBun.println();	

			}

			for(int i=model.numberOfNodes+1;i<=2*model.numberOfNodes;i++){ 
				Vect xyz;;

				xyz=model.node[i-model.numberOfNodes].getCoord();
				for(int j=0;j<model.dim;j++)
					pwBun.print(formatter.format(xyz.el[j]*model.scaleFactor)+" ,");
				pwBun.print(formatter.format(h*model.scaleFactor)+" ,");

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
		//	System.out.println(" Size of the generated mesh file :"+trunc(filesizeInMB,3)+" MB");

			pwBun.close();
		}
		catch(IOException e){}
		if(model.dim==3){
			String bun2D=System.getProperty("user.dir")+"\\bun2D.txt";
			model.writeMesh322(bun2D,false);
		}
	}

	
	public void writeMesh323(Model model,String bunFilePath,double r1, double r2){
	
		Vect[] nodep=new Vect[model.numberOfNodes+model.numberOfElements+1];
		for(int i=1;i<=model.numberOfNodes;i++)
			nodep[i]=model.node[i].getCoord().deepCopy();

	


		int[][] elv=new int[3*model.numberOfElements+1][3];

		int[] ixr=new int[model.numberOfRegions+1];
		int in=0;
		int ix=0;
		
		for(int ir=1;ir<=model.numberOfRegions;ir++)
			for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			ixr[ir]++;
			ix++;
			int[] vertNumb=model.element[i].getVertNumb();
			Vect v=model.getElementCenter(i);
			if(v.norm()<r1 || v.norm()>r2) {
				elv[ix]=vertNumb;
				
				continue;
			}
			
			in++;
			nodep[in+model.numberOfNodes]=v.deepCopy();
			elv[ix][0]=vertNumb[0];
			elv[ix][1]=model.numberOfNodes+in;
			elv[ix][2]=vertNumb[2];

			ixr[ir]++;
			ix++;	
				
				elv[ix][0]=vertNumb[0];
				elv[ix][1]=vertNumb[1];
				elv[ix][2]=model.numberOfNodes+in;
				
			ixr[ir]++;
			ix++;
			elv[ix][0]=model.numberOfNodes+in;
			elv[ix][1]=vertNumb[1];
			elv[ix][2]=vertNumb[2];
				
			}
		

		int nNodeNew=model.numberOfNodes+in;;
		int nElNew=ix;
		DecimalFormat formatter;

		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000000");
		else 
			formatter= new DecimalFormat("0.0000");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		

			pwBun.println("triangle");
			pwBun.println("//Number_of_Node");
			pwBun.println(nNodeNew);

			pwBun.println("//Number_of_Element");
			pwBun.println(nElNew);

			pwBun.println("//Number_of_Region");
			pwBun.println(model.numberOfRegions);
			pwBun.println("//Factor");
			pwBun.println(model.scaleFactor);

			for(int i=1;i<=nElNew;i++){ 
				for(int j=0;j<3;j++)
					pwBun.print(elv[i][j]+",");
				pwBun.println();
			}
			for(int i=1;i<=nNodeNew;i++){ 
				Vect xyz;;

				xyz=nodep[i].deepCopy();
				for(int j=0;j<2;j++){

					pwBun.print(formatter.format((xyz.el[j])*model.scaleFactor)+" ,");
				}
				pwBun.println();	

			}

			int[][] regEnds=new int[model.numberOfRegions+1][2];
			regEnds[1][0]=1;
			regEnds[1][1]=ixr[1];
			for(int i=2;i<=model.numberOfRegions;i++){
				regEnds[i][0]=regEnds[i-1][1]+1;
				regEnds[i][1]=regEnds[i][0]-1+ixr[i];

			}

			for(int i=1;i<=model.numberOfRegions;i++){ 

				pwBun.println(regEnds[i][0]+","+regEnds[i][1]+","+model.region[i].getName());

			}



			System.out.println();
			System.out.println(" Bun data was written to:");
			System.out.println("    "+bunFilePath);
			System.out.println();
		
			pwBun.close();
		}
		catch(IOException e){}
	}
	

	public void writeMesh32q(Model model,String bunFilePath){


		int[][] elEdge=new int[model.numberOfElements+1][3];
		
		EdgeSet eds=new EdgeSet();
		int[][] edgeNodes=eds.getEdgeNodesXY(model,elEdge);

		int nEdge=edgeNodes.length-1;

		Vect[] nodep=new Vect[model.numberOfNodes+nEdge+model.numberOfElements+1];
		for(int i=1;i<=model.numberOfNodes;i++)
			nodep[i]=model.node[i].getCoord().deepCopy();

		for(int i=1;i<=nEdge;i++){
			nodep[model.numberOfNodes+i]=model.node[edgeNodes[i][0]].getCoord().add(model.node[edgeNodes[i][1]].getCoord()).times(.5);
		}


		int nx=model.numberOfNodes+nEdge;

		int[][] elv=new int[3*model.numberOfElements+1][4];

		for(int i=1;i<=model.numberOfElements;i++){

			int[] vertNumb=model.element[i].getVertNumb();
		
			nodep[++nx]=model.getElementCenter(i);

			for(int j=0;j<model.nElVert;j++){
				int ne=3*(i-1)+j+1;
				elv[ne][0]=vertNumb[j];
				elv[ne][1]=model.numberOfNodes+elEdge[i][j];
				elv[ne][2]=nx;
				elv[ne][3]=model.numberOfNodes+elEdge[i][(j+2)%3];
			}
		}

		int nNodeNew=nx;
		int nElNew=3*model.numberOfElements;
		DecimalFormat formatter;

		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000000");
		else 
			formatter= new DecimalFormat("0.0000");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		

			pwBun.println("quadrangle");
			pwBun.println("//Number_of_Node");
			pwBun.println(nNodeNew);

			pwBun.println("//Number_of_Element");
			pwBun.println(nElNew);

			pwBun.println("//Number_of_Region");
			pwBun.println(model.numberOfRegions);
			pwBun.println("//Factor");
			pwBun.println(model.scaleFactor);

			for(int i=1;i<=nElNew;i++){ 
				for(int j=0;j<4;j++)
					pwBun.print(elv[i][j]+",");
				pwBun.println();
			}
			for(int i=1;i<=nNodeNew;i++){ 
				Vect xyz;;

				xyz=nodep[i].deepCopy();
				for(int j=0;j<2;j++){

					pwBun.print(formatter.format((xyz.el[j])*model.scaleFactor)+" ,");
				}
				pwBun.println();	

			}

			int[][] regEnds=new int[model.numberOfRegions+1][2];
			regEnds[1][0]=1;
			regEnds[1][1]=3*model.region[1].getLastEl();
			for(int i=2;i<=model.numberOfRegions;i++){
				regEnds[i][0]=regEnds[i-1][1]+1;
				regEnds[i][1]=regEnds[i][0]-1+3*model.region[i].getNumbElements();

			}

			for(int i=1;i<=model.numberOfRegions;i++){ 

				pwBun.println(regEnds[i][0]+","+regEnds[i][1]+","+model.region[i].getName());

			}



			System.out.println();
			System.out.println(" Bun data was written to:");
			System.out.println("    "+bunFilePath);
			System.out.println();


			pwBun.close();
		}
		catch(IOException e){}
	}


	
	public void writeMeshq23(Model model,String bunFilePath){


		int nElNew=2*model.numberOfElements;

		DecimalFormat formatter;

		if(model.scaleFactor==1)
			formatter= new DecimalFormat("0.0000000");
		else 
			formatter= new DecimalFormat("0.0000");

		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));		

			pwBun.println("triangle");
			pwBun.println("//Number_of_Node");
			pwBun.println(model.numberOfNodes);

			pwBun.println("//Number_of_Element");
			pwBun.println(nElNew);

			pwBun.println("//Number_of_Region");
			pwBun.println(model.numberOfRegions);
			pwBun.println("//Factor");
			pwBun.println(model.scaleFactor);

			for(int i=1;i<=model.numberOfElements;i++){ 
				int[] vertNumb=model.element[i].getVertNumb();
				double d1=model.node[vertNumb[0]].getCoord().sub(model.node[vertNumb[2]].getCoord()).norm();
				double d2=model.node[vertNumb[1]].getCoord().sub(model.node[vertNumb[3]].getCoord()).norm();
				if(d1>d2){

					pwBun.println(vertNumb[0]+","+vertNumb[1]+","+vertNumb[3]);
					pwBun.println(vertNumb[1]+","+vertNumb[2]+","+vertNumb[3]);
				}
				else{

					pwBun.println(vertNumb[0]+","+vertNumb[1]+","+vertNumb[2]);
					pwBun.println(vertNumb[2]+","+vertNumb[3]+","+vertNumb[0]);
				}
			}
			for(int i=1;i<=model.numberOfNodes;i++){ 
				Vect xyz;;

				xyz=model.node[i].getCoord();
				for(int j=0;j<2;j++){

					pwBun.print(formatter.format(xyz.el[j]*model.scaleFactor)+" ,");
				}
				pwBun.println();	

			}

			int[][] regEnds=new int[model.numberOfRegions+1][2];
			regEnds[1][0]=1;
			regEnds[1][1]=2*model.region[1].getLastEl();
			for(int i=2;i<=model.numberOfRegions;i++){
				regEnds[i][0]=regEnds[i-1][1]+1;
				regEnds[i][1]=regEnds[i][0]-1+2*model.region[i].getNumbElements();

			}

			for(int i=1;i<=model.numberOfRegions;i++){ 

				pwBun.println(regEnds[i][0]+","+regEnds[i][1]+","+model.region[i].getName());

			}

			System.out.println();
			System.out.println(" Bun data was written to:");
			System.out.println("    "+bunFilePath);
			System.out.println();
			File file=new File(bunFilePath);
			long filesize = file.length();
			double filesizeInMB=(double)filesize /(1024*1024);
			System.out.println(" Size of the generated mesh file :"+trunc(filesizeInMB,3)+" MB");

			pwBun.close();
		}
		catch(IOException e){}
	}


	public void writeBunX(Model model,String bunFilePath ){
		Vect zz=new Vect();
		Vect ff=zz.linspace(0, PI/2, 10);
		Vect rr=zz.linspace(100,1000, 10);
		int I,J;
		I=rr.length;
		J=ff.length;
		int Ne=(I-1)*(J-1);
		int Nn=I*J;
		int[][] EN=new int[Ne+1][4];

		double[][] xyz=new double[Nn][2];

		int nn;
		int[][] CN=new int[I][J];

			for(int j=0;j<J;j++)
				for(int i=0;i<I;i++){
					nn=j*I+i;
					xyz[nn][0]=rr.el[i]*cos(ff.el[j]);
					xyz[nn][1]=rr.el[i]*sin(ff.el[j]);

					CN[i][j]=nn;

				}


		int Nex=J-1;

		for(int i=0;i<I-1;i++)
				for(int j=0;j<J-1;j++)
				{
					int ne=i*Nex+j+1;

					int jn=j+1;
					/*if(j==J-2)
						jn=0;*/
					EN[ne][0]=CN[i+1][jn];
					EN[ne][1]=CN[i][jn];
					EN[ne][2]=CN[i][j];
					EN[ne][3]=CN[i+1][j];
					
				}

		int nReg=3;
		double[][] radius=new double[nReg+1][2];
		double[][] tht=new double[nReg+1][2];
		radius[1][0]=100;
		radius[1][1]=300;
		radius[2][0]=300;
		radius[2][1]=350;
		radius[3][0]=700;
		radius[3][1]=1000;
	
		/*		radius[4][0]=-1700;
		radius[4][1]=-9100;*/
		tht[1][0]=0;
		tht[1][1]=6;
		tht[2][0]=0;
		tht[2][1]=6;
		tht[3][0]=.4;
		tht[3][1]=.9;

		/*	tht[4][0]=.9;
		tht[4][1]=1.1;*/
		int[][] regEl=new int[nReg+1][Ne+1];
		int[] nRegEl=new int[nReg+1];
		for(int i=1;i<=Ne;i++){
			Vect p=new Vect(2);
			for(int j=0;j<4;j++)
				p=p.add(new Vect(xyz[EN[i][j]]));
			p=p.times(.25);
			double th=atan(p.el[1]/p.el[0]);
			double pn=p.norm();

			boolean b=false;
			for(int ir=1;ir<nReg;ir++){
				if(pn>radius[ir][0] && pn<radius[ir][1] && th>tht[ir][0] && th<tht[ir][1]){
					regEl[ir][++nRegEl[ir]]=i;
					b=true;
					break;
				}
			}
			if(!b){
				regEl[nReg][++nRegEl[nReg]]=i;
			}
		}




		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFilePath)));
			pwBun.println("quadrangle");
			pwBun.println("//Number_of_Node");
			pwBun.println(Nn);

			pwBun.println("//Number_of_Element");
			pwBun.println(Ne);

			pwBun.println("//Number_of_Region");
			pwBun.println(nReg);
			pwBun.println("//Factor");
			pwBun.println(1000.0);

			/*		for(int i=1;i<=Ne;i++){
				for(int j=0;j<8;j++)
					pwBun.print((EN[i][j]+1)+",");
				pwBun.println();
			}*/
			int nx=0;
			for(int ir=1;ir<=nReg;ir++){
				for(int i=1;i<=nRegEl[ir];i++){
					int ne=regEl[ir][i];
					for(int j=0;j<4;j++)
						pwBun.print((EN[ne][j]+1)+",");
					pwBun.println();
				}
			}

			for(int i=1;i<=Nn;i++){

				for(int j=0;j<2;j++)
					pwBun.print(xyz[i-1][j]+",");
				pwBun.println();

			}

			for(int ir=1;ir<=nReg;ir++){
				if(ir==nReg)
					pwBun.println(regEl[ir][1]+","+(regEl[ir][1]+nRegEl[ir]-1)+","+"air");
				else
					pwBun.println(regEl[ir][1]+","+(regEl[ir][1]+nRegEl[ir]-1)+","+"reg"+ir);
			}
			//pwBun.println(regEl[nReg][0]+","+(regEl[nReg][0]+nRegEl[nReg]-1)+","+"air");



			System.out.println();
			System.out.println(" Bun data was written to:");
			System.out.println("    "+bunFilePath);
			System.out.println();
			File file=new File(bunFilePath);
			long filesize = file.length();
			double filesizeInMB=(double)filesize /(1024*1024);
			System.out.println(" Size of the generated mesh file :"+trunc(filesizeInMB,3)+" MB");

			pwBun.close();
		}
		catch(IOException e){}
	}



	public void writeData(Model model,String dataFilePath){

		try{

			PrintWriter pwData = new PrintWriter(new BufferedWriter(new FileWriter(dataFilePath)));		

			pwData.println("Number_of_Regions: "+model.numberOfRegions);

			pwData.println();
			pwData.println("Boundary condition ( D: Dirichlet  N: Neumann PS: Periodic , PA: Anti-periodic) :");
			for(int j=0;j<6;j++){
				int pair=0;
				if(model.BCtype[j]==1)
				pwData.print("Boundary "+(j+1)+": D, [ "+model.diricB[j].el[0]+" "+model.diricB[j].el[1]+" "+model.diricB[j].el[2]+" ] ");
				else if(model.BCtype[j]==0)
				pwData.print("Boundary "+(j+1)+": N,");
				else if(model.BCtype[j]==2)
				pwData.print("Boundary "+(j+1)+": PS, "+pair);
				else
				pwData.print("Boundary "+(j+1)+": PA, "+pair);
				pwData.println();
			}
			pwData.println();			
			double yng,pois;
			for(int i=1;i<=model.numberOfRegions;i++){

				yng=model.matData.getYng(model.region[i].getMaterial());
				pois=model.matData.getPois(model.region[i].getMaterial());
				pwData.println("region "+i +": ");
				pwData.println("Name: "+model.region[i].getName());
				pwData.println("Material: "+model.region[i].getMaterial());
				Vect v=new Vect();
				if(model.region[i].isNonLinear){
					pwData.println("Nonlinear: true");
					 v=new Vect().ones(model.dim);
				}
				else{
					pwData.println("Nonlinear: false");
					v=model.region[i].getMur();
				}
					pwData.println("   mur: [ "+v.el[0]+" "+v.el[1]+" "+v.el[2]+" ]");
				

				 v=model.region[i].getSigma();
				pwData.println("   sigma: [ "+v.el[0]+" "+v.el[1]+" "+v.el[2]+" ]");

				v=model.region[i].getJ();
				pwData.println("   J: [ "+v.el[0]+" "+v.el[1]+" "+v.el[2]+" ]");
				v=model.region[i].getM();
				pwData.println("   M: [ "+v.el[0]+" "+v.el[1]+" "+v.el[2]+" ]");

				pwData.println("Young's Moduls: "+yng);
				pwData.println("Posion's Ratio: "+pois);
				pwData.println();

			}

			pwData.print("Analysis type (0: Magneto static, 1: Eddy current A method, 2: Eddy currrent A-phi method ): ");
			pwData.println(model.analysisMode);
			pwData.println();
			pwData.print("Deformation: ");
			pwData.println(model.deform);
			pwData.println();
			pwData.print("Deformation mode: ");
			pwData.println(model.defMode);
			pwData.println();
			pwData.print("Coupled: ");
			pwData.println(model.coupled);
			pwData.println();
			pwData.print("coupling2(vms): ");
			pwData.println(model.cpvms);
			pwData.println();
			pwData.print("calulate Curves: ");
			pwData.println(model.calCurve);
			pwData.println();
			if(model.calCurve){
				DecimalFormat formatter= new DecimalFormat("00.00");
				pwData.print("[ ");
				for(int k=0;k< model.BHstress.length;k++)
					pwData.print(formatter.format(model.BHstress[k])+" ");
				pwData.print(" ]");
				
			}
			pwData.print("coupling3(Tmsf): ");
			pwData.println(model.cpIMET);
			pwData.println();
			pwData.print("motor: ");
			pwData.println(model.motor);
			System.out.println();
			System.out.println(" Model data was written to:");
			System.out.println("    "+dataFilePath);
			System.out.println();
			pwData.close();
		}
		catch(IOException e){}

		String data2D=System.getProperty("user.dir")+"\\data2D.txt";
		writeData2D(model,data2D);
	}

	public void writeData2D(Model model, String dataFilePath){

		try{
			PrintWriter pwData = new PrintWriter(new BufferedWriter(new FileWriter(dataFilePath)));		

			pwData.println("Number_of_Regions: "+model.numberOfRegions);

			pwData.println();
			pwData.println("Boundary condition ( D: Dirichlet  N: Neumann PS: Periodic , PA: Anti-periodic) :");
			for(int j=0;j<4;j++){
				int pair=0;
				if(model.BCtype[j]==1)
				pwData.print("Boundary "+(j+1)+": D, [ "+model.diricB[j].el[0]+" "+model.diricB[j].el[1]+" ] ");
				else if(model.BCtype[j]==0)
				pwData.print("Boundary "+(j+1)+": N,");
				else if(model.BCtype[j]==2)
				pwData.print("Boundary "+(j+1)+": PS, "+pair);
				else
				pwData.print("Boundary "+(j+1)+": PA, "+pair);
				pwData.println();
			}
	
			pwData.println();		
			double yng,pois;
			for(int i=1;i<=model.numberOfRegions;i++){

				yng=model.matData.getYng(model.region[i].getMaterial());
				pois=model.matData.getPois(model.region[i].getMaterial());
				pwData.println("region "+i +": ");
				pwData.println("Name: "+model.region[i].getName());
				pwData.println("Material: "+model.region[i].getMaterial());
				if(model.region[i].isNonLinear)
					pwData.println("Nonlinear: true");
				else{
					pwData.println("Nonlinear: false");
					Vect v=model.region[i].getMur();
					pwData.println("   mur: [ "+v.el[0]+" "+v.el[1]+" ]");
				}

				Vect v=model.region[i].getSigma();
				pwData.println("   sigma: [ "+v.el[0]+" "+v.el[1]+" "+v.el[2]+" ]");
				
				v=model.region[i].getJ();
				pwData.println("   Jz: "+v.el[2]);
				v=model.region[i].getM();
				pwData.println("   M: [ "+v.el[0]+" "+v.el[1]+" ]");

				pwData.println("Young's Moduls: "+yng);
				pwData.println("Posion's Ratio: "+pois);
				pwData.println();

			}

			pwData.print("Analysis type (0: Magneto static, 1: Eddy current A method, 2: Eddy current A-phi method ): ");
			if(model.analysisMode==2)
				pwData.println(1);
			else
			pwData.println(model.analysisMode);
			
			pwData.println();
			pwData.print("Deformation: ");
			pwData.println(model.deform);
			pwData.println();
			pwData.print("Deformation mode: ");
			pwData.println(model.defMode);
			pwData.println();
			pwData.print("Coupled: ");
			pwData.println(model.coupled);
			pwData.println();
			pwData.print("coupling2(vms): ");
			pwData.println(model.cpvms);
			pwData.println();
			pwData.print("calulate Curves: ");
			pwData.println(model.calCurve);
			pwData.println();
			pwData.print("coupling3(IMET): ");
			pwData.println(model.cpIMET);
			pwData.println();
			pwData.print("motor: ");
			pwData.println(model.motor);
			System.out.println();
			System.out.println(" Model data was written to:");
			System.out.println("    "+dataFilePath);
			System.out.println();
			pwData.close();
		}
		catch(IOException e){}
	}
	
	
	public void writeB(Model model,String fluxFile){
		int dim=model.dim;
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(fluxFile)));
			pw.println("flux");
			pw.println(dim);
			pw.println(model.numberOfElements);
			for(int i=1;i<=model.numberOfElements;i++){
				Vect B=model.element[i].getB();
				
				for(int k=0;k<dim;k++)					
					pw.format("%E\t",B.el[k]);

				pw.println();
			}
			pw.close();
		} catch(IOException e){System.out.println("writing flux file failed.");}

		System.out.println(" Magnetic flux density was written to "+fluxFile);
		
	}

	public void writeA(Model model,String vPotFile){
		int dim=model.dim;
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(vPotFile)));
			pw.println("vPot");
			pw.println(dim);
			pw.println(model.numberOfEdges);
			for(int i=1;i<=model.numberOfEdges;i++){
				double Au=model.edge[i].Au;
				pw.println(Au);

			}
			pw.close();
		} catch(IOException e){System.out.println("writing vector potential file failed.");}

		System.out.println(" Magnetic vector potential was written to "+vPotFile);
		
	}	
		
	public void writeJe(Model model,String eddyFile){
		//int dim=model.dim;
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(eddyFile)));
			pw.println("flux");
			pw.println(3);
			pw.println(model.numberOfElements);
			for(int n=1;n<model.numberOfElements+1;n++){
				
				if(model.element[n].isConductor()){
				
					for(int k=0;k<3;k++)
						pw.format("%20.4f",model.element[n].getJe().el[k]);
					}
				else
					for(int k=0;k<3;k++)
						pw.format("%20.4f",0.0);
				
				
				pw.println();
			}
			pw.close();
		} catch(IOException e){System.out.println("writing flux file failed.");}

		System.out.println(" Eddy current was written to "+eddyFile);
	}


	public void writeJ0(Model model,String J0File){
		int dim=model.dim;
		try{
			double factJ=1;
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(J0File)));
			pw.println("flux");
			pw.println(dim);
			pw.println(model.numberOfElements);
			for(int ir=1;ir<=model.numberOfRegions;ir++){
				factJ=model.region[ir].getFactJ();
				for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
					Vect J=model.element[i].getJ();
					if(J==null)
						for(int k=0;k<dim;k++)
							pw.format("%E\t",0.0);
					else
						for(int k=0;k<dim;k++)
							pw.format("%E\t",J.el[k]*factJ);
					pw.println();
				}
			}
			pw.close();
		} catch(IOException e){System.out.println("writing flux file failed.");}

		System.out.println(" J0 density was written to "+J0File);
	}



	public void writeStress(Model model,String stressFile){
		
		int dim=model.dim;
		int L=3*(dim-1);
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(stressFile)));
			pw.println("stress");
			pw.println(dim);
			pw.println(model.numberOfElements);
			pw.println(model.stressViewCode);
			pw.println(model.nodalStressMaxCode);
			pw.println(model.stressMin);
			pw.println(model.stressMax);
			for(int i=1;i<=model.numberOfElements;i++){
				Vect stress=model.element[i].getStress();
			//	stress=model.getElementCenter(i).times(1e4).v3();
				
				for(int k=0;k<L;k++)
					pw.format("%E\t",stress.el[k]);
				pw.println();
			}

			for(int n=1;n<=model.numberOfNodes;n++){
				if(!model.node[n].is_U_known())
					pw.println(n);
			}
			pw.close();
		} catch(IOException e){System.out.println("writing flux file failed.");}

		System.out.println(" Structural stress was written to "+stressFile);
	}

	public void writeNodalField(Model model,String nodalForceFile,int mode){
		int dim=model.dim;
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(nodalForceFile)));
			if(mode==1)
				pw.println("force_reluc");
			else if(mode==2)
				pw.println("force_ms");
			else if(mode==3)
				pw.println("force_reluc");
			else if(mode==-1)
				pw.println("displacement");
		
			pw.println(dim);
			
			pw.println(model.numberOfNodes);
			
			
				for(int n=1;n<=model.numberOfNodes;n++)
					{
					
				Vect v=model.node[n].getNodalVect(mode);

				if(v==null){
						for(int k=0;k<dim;k++)
							pw.format("%20.4f",0.0);
				}
					else{
						
						if( mode==-1) v=v.times(1e9);
						for(int k=0;k<dim;k++)
							pw.format("%20.4f",v.el[k]);
					}
					pw.println();


				}

			pw.close();
		} catch(IOException e){System.out.println("writing nodal file failed.");}

		System.out.println(" Magnetic nodal force was written to "+nodalForceFile);
	}



	
	
	public void reportData(Model model){
		System.out.println();

		DecimalFormat formatter = new DecimalFormat("0.00E0");

		for(int i=1;i<=model.numberOfRegions;i++){

			System.out.println("model.region "+i +": ");
			System.out.println("Material: "+model.region[i].getMaterial());
			if(model.region[i].isNonLinear)
				System.out.println("   Nonlinear BH Curve"); 
			else{
				Vect v=model.region[i].getMur();
				System.out.printf("%10s","mur:"); v.hshow();	
			}
			Vect v=model.region[i].getSigma();
			System.out.printf("%10s","sigma: "); v.hshow();			
			v=model.region[i].getJ();
			System.out.printf("%10s","J: "); v.hshow();	

			if(model.region[i].hasM){
				v=model.region[i].getM();
				System.out.printf("%10s","M: "); v.hshow();				}

			System.out.println("Young's Moduls: "+formatter.format(model.region[i].getYng()));
			System.out.println("Posion's Ratio: "+model.region[i].getPois());
			System.out.println();

		}
		String method="";
		if(model.analysisMode==0)
			method=" Anlysis Type:   Magnetostatic" ;
		else if(model.analysisMode==1)
			method=" Anlysis Type:   Eddy current A method" ;
		else if(model.analysisMode==2)
			method=" Anlysis Type:   Eddy current A-phi- method" ;

		System.out.println();
		System.out.println("     "+method);
		System.out.println();

		for(int ir=1;ir<=model.numberOfRegions;ir++)
			if(model.region[ir].isNonLinear)
				System.out.println(" Region "+ir+" : Nonlinear B-H curve cosidered. ");
		System.out.println();
		if(model.deform)
			System.out.println(" Including Structural Alanysis. ");

		System.out.println(" Number of regions: "+model.numberOfRegions);
		System.out.println(" Number of elements: "+model.numberOfElements);
		System.out.println(" Number of nodes   : "+model.numberOfNodes+"    known: "+model.numberOfKnownPhis+" , unknown: "+model.numberOfVarNodes);
		System.out.println(" Number of edges   : "+model.numberOfEdges+"    known: "+model.numberOfKnownEdges+" , unknown: "+model.numberOfUnknownEdges);	
		System.out.println(" Total number of unknows   : "+model.numberOfUnknowns);	
		System.out.println();



	}

	public void writeNodeX(String nodeFile){
		Vect zz=new Vect();
		Vect ff=zz.linspace(0, 2*PI, 10);
		Vect rr=zz.linspace(0,400, 10);
		int I,J;
		I=rr.length;
		J=ff.length;
		int Nn=I*J;
Nn=10000;
		double[][] xyz=new double[Nn][2];
		int nn=0;

		xyz[nn][0]=0;
		xyz[nn++][1]=0;

		I=10;
		double r=300;
		for(int i=1;i<=I;i++){
			int L=35-0*i;
		for(int j=0;j<L;j++){
			double theta=j*PI*2.0/L;
			double rx=(1-pow((i-1)*1.0/I,1.5))*r;
			xyz[nn][0]=rx*cos(theta);
			xyz[nn++][1]=rx*sin(theta);
		}
	}
		I=10;
		for(int i=1;i<=I;i++){
			int L=35-2*i;
		for(int j=0;j<L;j++){
			double theta=j*PI*2.0/L;
			double rx=pow((i+I)*1.0/I,1.2)*r;
			xyz[nn][0]=rx*cos(theta);
			xyz[nn++][1]=rx*sin(theta);
		}
	}
		
		for(int i=1;i<=11;i++){
		xyz[nn][0]=(i-6)*200;
		xyz[nn++][1]=-1000;
		}
		for(int i=1;i<=11;i++){
			xyz[nn][0]=(i-6)*200;
			xyz[nn++][1]=1000;
			}
		for(int i=1;i<=9;i++){
			xyz[nn][0]=-1000;
			xyz[nn++][1]=(i-5)*200;
			}
		for(int i=1;i<=9;i++){
			xyz[nn][0]=1000;
			xyz[nn++][1]=(i-5)*200;
			}
	Nn=nn;		
			/*for(int j=0;j<J-1;j++)
				for(int i=1;i<I;i++){
					nn=j*I+i;
					xyz[nn][0]=rr.el[i]*cos(ff.el[j]);
					xyz[nn][1]=rr.el[i]*sin(ff.el[j]);


				}*/



		try{
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(nodeFile)));

			pwBun.println("# "+nodeFile);
			pwBun.println("# ");
			pwBun.println(+Nn+" "+2+" "+0+" "+0);
			pwBun.println("# ");
			for(int i=1;i<=Nn;i++){
				pwBun.print(i+" ");
				for(int j=0;j<2;j++)
					pwBun.print(xyz[i-1][j]+" ");
				pwBun.println();

			}

	
			System.out.println();
			System.out.println(" node file written to:");
			System.out.println("    "+nodeFile);
			

			pwBun.close();
		}
		catch(IOException e){}
	}
	
	
	public void writeMesh(String nodeFile ,String elFile){
		 String bunFile=System.getProperty("user.dir")+"\\circle.txt";

		DecimalFormat formatter;
try{
			FileReader frn=new FileReader(nodeFile);
			FileReader fre=new FileReader(elFile);
			BufferedReader brn = new BufferedReader(frn);
			BufferedReader bre = new BufferedReader(fre);
			PrintWriter pwBun = new PrintWriter(new BufferedWriter(new FileWriter(bunFile)));
			String regex="[ ,\\t]+";

			String elType="triangle";
			
			pwBun.println(elType);
			
			
			String line;
			String[] sp;
			int Nn,Ne,nR=2;
			double factor=1000.0;
			brn.readLine();
			brn.readLine();
			line=brn.readLine();
			sp=line.split(regex);

			if(!sp[0].equals(""))		
				Nn=Integer.parseInt(sp[0]);
			else
				Nn=Integer.parseInt(sp[1]);
		
			line=bre.readLine();
			sp=line.split(regex);
			if(!sp[0].equals(""))	
				Ne=Integer.parseInt(sp[0]);
			else
				Ne=Integer.parseInt(sp[1]);
			util.pr("Ne: "+Ne);		
			pwBun.println("//Number_of_Nodes:");
			pwBun.println(Nn);
			pwBun.println("//Number_of_Elements:");
			pwBun.println(Ne);
			pwBun.println("//Number_of_Regions:");
			pwBun.println(nR);
			pwBun.println("//Factor:");
			pwBun.println(factor);
			
			double r=300;
			int[][] ee=new int[Ne+1][3];
			double[][] nn=new double[Nn+1][2];
			
			for(int i=1;i<=Ne;i++){
				line=bre.readLine();
				
				sp=line.split(regex);

				int k=0;
				for(int j=sp.length-3;j<sp.length;j++)
					ee[i][k++]=Integer.parseInt(sp[j]);

			}
			brn.readLine();
			for(int i=1;i<=Nn;i++){
				line=brn.readLine();
				sp=line.split(regex);
				for(int j=0;j<2;j++)
					nn[i][j]=Double.parseDouble(sp[j+1]);
	}
			int[][] ee2=new int[Ne+1][3];
			int ix=0;

			for(int i=1;i<=Ne;i++){
				double xm=(nn[ee[i][0]][0]+nn[ee[i][1]][0]+nn[ee[i][2]][0])/3;
				double ym=(nn[ee[i][0]][1]+nn[ee[i][1]][1]+nn[ee[i][2]][1])/3;
				if(xm*xm+ym*ym<r*r) 
					ee2[++ix]=ee[i];
			}
			
			int nIron=ix;
			
			for(int i=1;i<=Ne;i++){
				double xm=(nn[ee[i][0]][0]+nn[ee[i][1]][0]+nn[ee[i][2]][0])/3;
				double ym=(nn[ee[i][0]][1]+nn[ee[i][1]][1]+nn[ee[i][2]][1])/3;
				if(xm*xm+ym*ym>r*r) 
					ee2[++ix]=ee[i];
			}
			
			for(int i=1;i<=Ne;i++){
	
				for(int j=0;j<3;j++)
				pwBun.print(ee2[i][j]+",");
				pwBun.println();
			}
			
			for(int i=1;i<=Nn;i++){
				
				for(int j=0;j<2;j++)
				pwBun.print(nn[i][j]+", ");
				pwBun.println();
			}
			
			pwBun.println(1+","+nIron+", iron");
			pwBun.println((nIron+1)+","+Ne+", air");
	
			brn.close();
			bre.close();
			pwBun.close();
			brn.close();
			bre.close();
		}
		catch(IOException e){}
		
		}
	

	private double trunc(double a,int n){
		return floor(a*pow(10,n))/pow(10,n);
	}
}
