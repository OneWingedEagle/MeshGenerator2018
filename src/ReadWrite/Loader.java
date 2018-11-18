package ReadWrite;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import drawingPanel.DrawingPanel;
import math.Mat;
import math.Vect;
import math.util;
import fem.*;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Hassan.
 *         Created Aug 15, 2012.
 */
public class Loader {

	private String regex="[: ,=\\t]+";

	public void loadMesh(Model model, String bunFilePath){

		try{
			FileReader fr=new FileReader(bunFilePath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;
			String regex="[ ,\\t]+";

			String elType=br.readLine();
			model.setElType(elType);

			br.readLine();		
			line=br.readLine();
			model.numberOfNodes=getIntData(line);



			br.readLine();		
			line=br.readLine();
			model.numberOfElements=getIntData(line);

			
			model.element=new Element[model.numberOfElements+1];
			for(int i=1;i<=model.numberOfElements;i++){
				model.element[i]=new Element(elType);
			}

			model.spaceBoundary=new double[model.nBoundary];

			for(int i=0;i<model.dim;i++){
				model.spaceBoundary[2*i]=1e10;
				model.spaceBoundary[2*i+1]=-1e10;
			}

			model.node=new Node[model.numberOfNodes+1];

			for(int i=1;i<=model.numberOfNodes;i++)
				model.node[i]=new Node(model.dim);

			br.readLine();		
			line=br.readLine();
			model.numberOfRegions=getIntData(line);

			model.region=new Region[model.numberOfRegions+1];
			for(int i=1;i<=model.numberOfRegions;i++)
				model.region[i]=new Region(model.dim);

			br.readLine();

			line=br.readLine();


			model.scaleFactor=Double.parseDouble(line);

			double factor=1.0/model.scaleFactor;


			for(int i=1;i<=model.numberOfElements;i++){
				line=br.readLine();
				sp=line.split(regex);
				int k=0;
				for(int j=0;j<sp.length;j++){
					if(!sp[j].equals(""))		
						model.element[i].setVertNumb(k++, Integer.parseInt(sp[j]));		
				}
			}


			Vect z=new Vect(model.dim);
			for(int i=1;i<=model.numberOfNodes;i++){
				line=br.readLine();
				sp=line.split(regex);
				int k=0;

				for(int j=0;j<sp.length;j++)
					if(!sp[j].equals(""))
						z.el[k++]=Double.parseDouble(sp[j])*factor;


				model.node[i].setCoord(z);

				if(model.node[i].getCoord(0)<model.spaceBoundary[0]) model.spaceBoundary[0]=model.node[i].getCoord(0);
				else if(model.node[i].getCoord(0)>model.spaceBoundary[1]) model.spaceBoundary[1]=model.node[i].getCoord(0);

				if(model.node[i].getCoord(1)<model.spaceBoundary[2]) model.spaceBoundary[2]=model.node[i].getCoord(1);
				else if(model.node[i].getCoord(1)>model.spaceBoundary[3]) model.spaceBoundary[3]=model.node[i].getCoord(1);
				if(model.dim==3){
					if(model.node[i].getCoord(2)<model.spaceBoundary[4]) model.spaceBoundary[4]=model.node[i].getCoord(2);
					else if(model.node[i].getCoord(2)>model.spaceBoundary[5]) model.spaceBoundary[5]=model.node[i].getCoord(2);
				}	


			}

			for(int i=1;i<=model.numberOfRegions;i++){

				line=br.readLine();
				sp=line.split(regex);

				String[] str=new String[3];
				int k=0;
				for(int j=0;j<sp.length;j++){
					if(!sp[j].equals(""))
						str[k++]=sp[j];
				}
				model.region[i].setFirstEl(Integer.parseInt(str[0]));	

				model.region[i].setLastEl(Integer.parseInt(str[1]));
				model.region[i].setName(str[2]);
				model.region[i].setMaterial(str[2]+"mat");
			}

			System.out.println();
			System.out.println("Loading mesh file completed.");

			br.close();
			fr.close();

			for(int ir=1;ir<=model.numberOfRegions;ir++)
				for( int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
					model.element[i].setRegion(ir);

			model.setMaxDim();


		}
		catch(IOException e){System.err.println("Error in loading model file.");
		}


	}	

	public void loadData(Model model,String dataFilePath){
		loadData(model,dataFilePath,0,0,0);
	}


	public void loadData(Model model,String dataFilePath,double Im, double betta, double wt){
		int dim=model.dim;
		Vect v=new Vect(dim);
		for(int j=0;j<model.nBoundary;j++){
			model.diricB[j]=new Vect(dim);
		}
		model.BCtype=new int[model.nBoundary];
		for(int j=0;j<model.nBoundary;j++)
			model.BCtype[j]=-1;
		model.PBCpair=new int[model.nBoundary];


		try{
			BufferedReader br = new BufferedReader(new FileReader(dataFilePath));
			String line;
			String s;
			StringTokenizer tk;
			line=br.readLine();
			int nRegions=getIntData(line);
			
			if(nRegions!=model.numberOfRegions)
				System.err.println("Mesh and Data do not match in the number of regions: "+nRegions+" and "+model.numberOfRegions);

			line=br.readLine();
			line=br.readLine();

			Vect B;
			int[] bcData=new int[2];
			for(int j=0;j<model.nBoundary;j++){
				line=br.readLine();
				B=new Vect(dim);
				if(model.BCtype[j]>-1) continue;
				bcData=getBCdata(line,B);
				model.BCtype[j]=bcData[0];
				if(model.BCtype[j]>1){
					model.PBCpair[j]=bcData[1];
					model.BCtype[model.PBCpair[j]]=bcData[0];
					model.PBCpair[model.PBCpair[j]]=j;
				}
				if(model.BCtype[j]==1) model.diricB[j]=B.deepCopy();


				if(model.BCtype[j]>1) model.hasPBC=true;

			}


			for(int i=1;i<=model.numberOfRegions;i++){
				line=br.readLine();
				line=br.readLine();
				line=br.readLine();
				model.region[i].setName(getStringData(line));
				line=br.readLine();
				model.region[i].setMaterial(getStringData(line));

				line=br.readLine();
				boolean b=getBooleanData(line);
				model.region[i].setNonLinear(b);
				if(!b){
					line=br.readLine();
					v=this.getVectData(line, dim);
					model.region[i].setMur(v);	
				}

				line=br.readLine();
				v=this.getVectData(line, 3);
				model.region[i].setSigma(v);	

				line=br.readLine();

				if(model.dim==3){
					v=this.getVectData(line, dim);
					model.region[i].setJ(v);	
				}
				else{
					v=new Vect(0,0,getScalarData(line));
				}
				model.region[i].setJ(v);	
				line=br.readLine();
				v=this.getVectData(line, dim);
				model.region[i].setM(v);

				line=br.readLine();
				model.region[i].setYng(getScalarData(line));
				line=br.readLine();			
				model.region[i].setPois(getScalarData(line));
				model.region[i].setRo(model.matData.getRo(model.region[i].getMaterial()));

			}

			line=br.readLine();
			line=br.readLine();
			model.analysisMode=getIntData(line);
			line=br.readLine();
			line=br.readLine();
			model.deform=getBooleanData(line);
			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			model.coupled=getBooleanData(line);
			line=br.readLine();
			line=br.readLine();
	
			model.cpvms=getBooleanData(line);
			line=br.readLine();
			line=br.readLine();
			model.calCurve=getBooleanData(line);
			
			if(model.calCurve){
			line=br.readLine();
			
			model.BHstress=getArrayData(line);
			
			//model.BHstress=new Vect().linspace(-4.88, 6.45, 27).el;
			model.BHstress=new Vect().linspace(-5.23, 5.23, 1.436).el;
			for(int i=0; i<model.BHstress.length-1;i++)
				if(model.BHstress[i]*model.BHstress[i+1]<0) model.BHstress[i]=0;
			
			}
			line=br.readLine();
			line=br.readLine();
			model.cpIMET=getBooleanData(line);

			line=br.readLine();
			line=br.readLine();
			model.motor=getBooleanData(line);;

			System.out.println();
			System.out.println("Loading data file completed.");

			if(Im>0){
				double pi=Math.PI;
				double b0=pi*2/3;
				double t1=1,t2=1,t3=1;
				double bettaRad=(betta+wt)*pi/180+pi/2;
				/*		model.x1=Math.cos(bettaRad);
				model.x2=Math.cos(bettaRad-b0);
				model.x3=Math.cos(bettaRad+b0);*/
				for(int i=1;i<=model.numberOfRegions;i++){
					if(model.region[i].getName().equals("coilU+")) {model.region[i].setJ(new Vect(0,0,t1*Im*Math.cos(bettaRad)));}
					else if(model.region[i].getName().equals("coilU-")) model.region[i].setJ(new Vect(0,0,-t1*Im*Math.cos(bettaRad)));
					else if(model.region[i].getName().equals("coilV+")) model.region[i].setJ(new Vect(0,0,t2*Im*Math.cos(bettaRad-b0)));
					else if(model.region[i].getName().equals("coilV-")) model.region[i].setJ(new Vect(0,0,-t2*Im*Math.cos(bettaRad-b0)));
					else if(model.region[i].getName().equals("coilW+")) model.region[i].setJ(new Vect(0,0,t3*Im*Math.cos(bettaRad+b0)));
					else if(model.region[i].getName().equals("coilW-")) model.region[i].setJ(new Vect(0,0,-t3*Im*Math.cos(bettaRad+b0)));


				}
			}

		

			model.setNonLin();


			model.setEdge();



			br.close();
		}

		catch(IOException e){System.err.println("Error in loading data file.");
		}



	}


	public boolean loadFlux(Model model,String fluxFilePath){

		try{
			Scanner scr=new Scanner(new FileReader(fluxFilePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());

			int nElements=Integer.parseInt(scr.next());
			if(nElements!=model.numberOfElements) {
				String msg="Flux file does not match the mesh";
				JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				return false;
			}



			double Bn2,Bmax2=-1e40,Bmin2=1e40;
			for(int i=1;i<=nElements;i++){

				Vect B1=new Vect(dim);

				for(int j=0;j<dim;j++)
					B1.el[j]=Double.parseDouble(scr.next());
				model.element[i].setB(B1);

				Bn2=B1.dot(B1);

				if(Bn2>Bmax2)
					Bmax2=Bn2;
				if(Bn2<Bmin2)
					Bmin2=Bn2;

			}


			model.Bmax=sqrt(Bmax2);
			model.Bmin=sqrt(Bmin2);
			scr.close();

			System.out.println("Flux was loaded from "+fluxFilePath+" to the model.");
			model.fluxLoaded=true;
			return true;
		}
		catch(IOException e){System.err.println("Error in loading flux file.");
		return false;
		}

	}	

	public boolean loadPotential(Model model,String vPotFile){

		try{
			Scanner scr=new Scanner(new FileReader(vPotFile));

			scr.nextLine();
			int dim=Integer.parseInt(scr.next());

			int nEdges=Integer.parseInt(scr.next());
			if(nEdges!=model.numberOfEdges) {
				String msg="Vector potential file does not match the mesh";
				JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				return false;
			}

			for(int i=1;i<=nEdges;i++){

				double Au=Double.parseDouble(scr.next());

				model.edge[i].setAu(Au);

			}


			scr.close();

			System.out.println("Vector potential was loaded from "+vPotFile+" to the model.");
			model.potentialLoaded=true;
			return true;
		}
		catch(IOException e){System.err.println("Error in loading vector potential file.");
		return false;
		}

	}	

	public boolean loadStress(Model model,String stressFilePath){

		try{
			Scanner scr=new Scanner(new FileReader(stressFilePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());
			int L=3*(dim-1);

			int nElements=Integer.parseInt(scr.next());
			if(nElements!=model.numberOfElements) {
				String msg="Stress file doesnt match the mesh";
				JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				return false;
			}

			for(int i=1;i<=nElements;i++){
				Vect ss=new Vect(model.nBoundary);
				for(int j=0;j<L;j++)
					ss.el[j]=Double.parseDouble(scr.next());
				if(ss.norm()>0){
					model.element[i].setDeformable(true);
					model.element[i].setStress(ss);

				}

			}

			for(int i=1;i<=model.numberOfNodes;i++){

				model.node[i].setU_is_known(true);

			}
			int nNode;
			while(scr.hasNext()){
				nNode=Integer.parseInt(scr.next());
				model.node[nNode].setU_is_known(false);
			}


			scr.close();

			System.out.println("Stress was loaded from "+stressFilePath+" to the model.");
			return true;
		}
		catch(IOException e){System.err.println("Error in loading stress file.");
		return false;
		}



	}



	public boolean loadNodalField(Model model,String stressFilePath,int mode){

		try{
			Scanner scr=new Scanner(new FileReader(stressFilePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());
			model.dim=dim;
			int nNodes=Integer.parseInt(scr.next());

			if(nNodes!=model.numberOfNodes) {
				String msg="Nodal field file does not match the mesh";
				JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				return false;
			}


			double sn2=0,smax2=0,smin2=0;
			for(int i=1;i<=nNodes;i++){

				Vect v=new Vect(dim);
				for(int j=0;j<dim;j++)
					v.el[j]=Double.parseDouble(scr.next());

				if(v.norm()>0){
					model.node[i].setDeformable(true);
					if(mode==-1){
						v.timesVoid(1e-9);
						model.node[i].setU(v);
					}
					else if(mode==1)
						model.node[i].setF(v);
					else if(mode==2)
						model.node[i].setFms(v);
					sn2=v.dot(v);
				}

				if(sn2>smax2)
					smax2=sn2;
				if(sn2<smin2)
					smin2=sn2;
			}

			if(mode==-1){
				model.uMax=sqrt(smax2);

			}
			else	if(mode==1){
				model.FreluctMax=sqrt(smax2);
			}

			else	if(mode==2){
				model.FmsMax=sqrt(smax2);

			}
			if(mode>0)
				model.forceLoaded=true;


			scr.close();


			System.out.println("Force was loaded from "+stressFilePath+" to the model.");
			return true;
		}
		catch(IOException e){System.err.println("Error in loading force.");


		return false;
		}
	}

	public void loadModel(DrawingPanel gui,String modelFilePath) {
		int dim=3;
		double[] spaceBoundary=new double[6];
		try{
			BufferedReader br = new BufferedReader(new FileReader(modelFilePath));
			String line;
			StringTokenizer tk;
			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			int nBlks=getIntData(line);

			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			gui.blockName[0]=getStringData(line);
			line=br.readLine();
			gui.blockMaterial[0]=getStringData(line);
			gui.blockMatColor[0] = gui.matData.matColor(gui.blockMaterial[0]);
			line=br.readLine();
			spaceBoundary=getArrayData(line,6);
			line=br.readLine();
			gui.mur[0]=getVectData(line,dim);
			line=br.readLine();
			gui.sigma[0]=getVectData(line,dim);


			for(int i=1;i<=nBlks;i++){

				gui.addBlock();
				line=br.readLine();

				line=br.readLine();
				line=br.readLine();
				line=br.readLine();
				gui.blockName[i]=getStringData(line);
				line=br.readLine();
				gui.blockMaterial[i]=getStringData(line);
				gui.blockMatColor[i] = gui.matData.matColor(gui.blockMaterial[i]);
				line=br.readLine();
				gui.blockBoundary[i]=getArrayData(line,6);

				line=br.readLine();
				tk= new StringTokenizer(line," ");
				tk.nextToken();
				for(int j=0;j<6;j++){
					gui.discLeft[i][j]=Boolean.parseBoolean(tk.nextToken());
					gui.discRight[i][j]=Boolean.parseBoolean(tk.nextToken());
				}				
				line=br.readLine();
				gui.linearBH[i]=!getBooleanData(line);
				if(gui.linearBH[i]){
					line=br.readLine();
					gui.mur[i]=getVectData(line,dim);
				}


				line=br.readLine();

				gui.sigma[i]=getVectData(line,dim);
				line=br.readLine();
				gui.J[i]=getVectData(line,dim);
				if(gui.J[i].norm()>0) gui.hasCurrent[i] = true;
				line=br.readLine();
				gui.M[i]=getVectData(line,dim);
				if(gui.M[i].norm()>0) gui.hasMag[i] = true;
				gui.updateBlock(gui.nBlocks);
			}

			gui.addBlock();
			gui.removeBlock(gui.nBlocks);
			br.close();



		}
		catch(IOException e){System.err.println("Error in loading model file.");
		}

		for(int j=0;j<6;j++)
			gui.blockBoundary[0][j]=spaceBoundary[j];

		for(int j=0;j<6;j++)	
			gui.BCtype[j]=1;

		gui.updateSpace();


	}

	public Vect loadNodalU(String uuFile,int n,int p){
		double[] a=new double[1000];
		Vect v=new Vect(1000);
		int L=0;
		try{
			FileReader fr=new FileReader(uuFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;
			String regex="[ ,\\t]+";
			boolean found=false;
			for(int i=0;i<100000;i++){
				line=br.readLine();
				if(line==null) break;
				sp=line.split(regex);
				if(sp.length<2){
					int nn=Integer.parseInt(sp[0]);
					if(nn==n){
						found=true;

						for(int ir=0;ir<=p;ir++)
							line=br.readLine();

						sp=line.split(regex);
						L=sp.length;

						for(int k=0;k<L;k++){
							a[k]=Double.parseDouble(sp[k]);

						}

					}
				}

				v=new Vect(L);
				for(int k=0;k<L;k++){
					v.el[k]=a[k];
				}

				if(found){
					break;
				}
			}


		}catch(IOException e){System.err.println("file not found.");}

		return v;

	} 


	public void loadNodalU2(String uuFile,int n,int p){

		try{
			FileReader fr=new FileReader(uuFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;
			String regex="[ ,\\t]+";

			for(int i=0;i<100000;i++){
				line=br.readLine();
				if(line==null) break;
				sp=line.split(regex);
				if(sp.length<2){
					int nn=Integer.parseInt(sp[0]);
					if(nn==n){
						for(int ir=0;ir<=p;ir++)
							line=br.readLine();

						sp=line.split(regex);
						for(int k=0;k<sp.length;k++)
							util.pr(Double.parseDouble(sp[k]));
					}
				}
			}

		}catch(IOException e){System.err.println("file not found.");}

	} 

	private int[] getBCdata(String line,Vect B){

		int[] bctp=new int[2];
		String[] sp=line.split("");	
		int pair=-1,bct=0;
		int k=0;
		while(!sp[k].equals(":")){ k++;}	
		k++;
		if(k==sp.length) return bctp;
		if(sp[k].equals(" ")) k++;
		String s=sp[k];

		if(s.equals("P")){

			if(sp[k+1].equals("S")){
				bct=2;
			}

			else if(sp[k].equals("A")){

				bct=3;
			}

			sp=line.split(regex);
			String s2=sp[sp.length-1];
			if(s2.equals(","))
				s2=sp[sp.length-2];
			pair=Byte.parseByte(s2)-1;

		}

		else if(s.equals("N"))
			bct=0;

		else if(s.equals("D")){
			sp=line.split(regex);
			bct=1;
			k=0;
			while(k<sp.length && !sp[k].equals("[")){k++;}	

			if(k<sp.length-1){
				k+=1;
				if(sp[k].equals(" ")) k++;

				for( int p=0;p<B.length;p++)
					B.el[p]=Double.parseDouble(sp[k+p]);
			}

		}



		bctp[0]=bct;
		bctp[1]=pair;

		return bctp;
	}

	private Vect getVectData(String line, int dim){

		String[] sp=line.split(regex);	
		Vect v=new Vect(dim);
		int k=0;
		while(!sp[k].equals("[")){k++;}	

		k+=1;
		if(sp[k].equals(" ")) k++;

		for( int p=0;p<dim;p++)
			v.el[p]=Double.parseDouble(sp[k+p]);

		return v;
	}

	private double[] getArrayData(String line, int L){

		String[] sp=line.split(regex);	
		double[] v=new double[L];
		int k=0;
		while(!sp[k].equals("[")){k++;}			
		k+=1;
		if(sp[k].equals(" ")) k++;

		for( int p=0;p<L;p++)
			v[p]=Double.parseDouble(sp[k+p]);

		return v;
	}
	private double[] getArrayData(String line){
		String[] sp=line.split(regex);	
		int L=0;
		int k=0;
		while(!sp[k].equals("[")){k++;}			
		k+=1;
		if(sp[k].equals(" ")) k++;

		while(!sp[k].equals("]")){
			k++;
			L++;
		}		
						
		return  getArrayData(line,L);
	}

	private double[] getPair(String line){
		String[] sp=line.split(regex);	
		double[] v=new double[2];
		int k=0;
		if(sp[k].equals(" ")) k++;

		for( int p=0;p<2;p++)
			v[p]=Double.parseDouble(sp[k+p]);

		return v;
	}
	private double getScalarData(String line){
		String[] sp=line.split(regex);	
		return Double.parseDouble(sp[sp.length-1]);
	}



	private int getIntData(String line){
		String[] sp=line.split(regex);	
		return Integer.parseInt(sp[sp.length-1]);
	}

	private boolean getBooleanData(String line){
		String[] sp=line.split(regex);	
		if(sp[sp.length-1].startsWith("t"))
			return true;
		else return false;

	}

	private String getStringData(String line){
		String[] sp=line.split(":");	
		String[] sp2=sp[sp.length-1].split(" ");
		return sp2[sp2.length-1];
	}

}
