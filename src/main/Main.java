package main;
import drawingPanel.*;
import panels.Console;
import panels.SimpleGUI;
import fem.*;
import math.*;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import ReadWrite.Writer;


	public class Main implements ActionListener, DropTargetListener{

	private GUI gui;

	private Model model;
	private Thread thread1;
	private Writer writer=new Writer();
	private int mheavy=200,nMesh=0,iterMax=2000,angiter;	
	private double  errMax,dt=4.0/36*1e-3;
	private boolean heavy,resultReady,vibr=false;
	private DecimalFormat formatter=new DecimalFormat("0.000E00");
	private String path = System.getProperty("user.dir") + "\\model.txt";
	private Vect u0,u1;

	Main()
	{		
		this.model=new Model();
		//this.simGui=new SimpleGUI(this.path);
		//this.simGui.bMainGUI.addActionListener(this);
		//this.simGui.Run.addActionListener(this);
		//this.simGui.bTerminate.addActionListener(this);
		//this.simGui.setVisible(true);
		openMainGUI();
		//this.simGui.setVisible(true);
	}	

/*		
	public void loadSimple2(){
		Console.redirectOutput(this.simGui.progressArea);
		this.model.meshFilePath=this.simGui.tfMeshFile.getText();
		this.model.dataFilePath=this.simGui.tfDataFile.getText();
		this.errMax=Double.parseDouble(this.simGui.tfErrorMax.getText());
		this.iterMax=Integer.parseInt(this.simGui.tfIterMax.getText());
		this.thread1=new Thread(){
			long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			@Override
			public void run(){

				String filepath = Main.this.model.meshFilePath;
				Main.this.model.loadMesh(filepath);	
				Main.this.model.loadData( Main.this.model.dataFilePath);
				System.gc();
				long m2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Used memory for  model setup: "+(m2-this.m1)/(1024*1024)+"MB");
				Main.this.model.iterMax=Main.this.iterMax;
				Main.this.model.errMax=Main.this.errMax;
				runFEM();
				util.pr("Stress Max: "+Main.this.model.stressMax);			
			
			}
		};
		this.thread1.start();		
	}

	public void loadSimple(){
		

		Console.redirectOutput(this.simGui.progressArea);
		this.model.meshFilePath=this.simGui.tfMeshFile.getText();
		this.model.dataFilePath=this.simGui.tfDataFile.getText();
		this.errMax=Double.parseDouble(this.simGui.tfErrorMax.getText());
		this.iterMax=Integer.parseInt(this.simGui.tfIterMax.getText());
		this.thread1=new Thread(){
			@Override
			public void run(){

				String def = System.getProperty("user.dir") + "//uu"+Main.this.model.defMode+".txt";
				String Trq = System.getProperty("user.dir") + "//Defom.txt";
				try {

					PrintWriter pwu=new PrintWriter(new BufferedWriter(new FileWriter(def)));
					PrintWriter pwt=new PrintWriter(new BufferedWriter(new FileWriter(Trq)));
					
				
					String rotf = System.getProperty("user.dir") + "\\rotFull.txt";
					String statf = System.getProperty("user.dir") + "\\statFull.txt";

					String roth = System.getProperty("user.dir") + "\\rotHalfDrop.txt";
					String stath = System.getProperty("user.dir") + "\\statHalfDrop.txt";
					String roth = System.getProperty("user.dir") + "\\rot4th.txt";
					String stath = System.getProperty("user.dir") + "\\stat4th.txt";
					

					int N1=6;
					int N2=6;
					
					Vect[][] uu=new Vect[1][1];
					boolean rec=false;
					boolean old=true;
					if(rec){
					uu=new Vect[2000][N2-N1+1];
					}
			
					if(N2-N1>0) Main.this.model.writeFiles=false;
					else
						Main.this.model.writeFiles=true;
					
					Main.this.simGui.lbX[0].setText("rot ang. ");
					Main.this.simGui.lbX[1].setText("Trq. ");
					
					
					if(Main.this.vibr){
						angiter=0;

					u0=null;u1=null;
					}
					
					
					for(int i=N1;i<=N2;i+=1){

						int ang=i;
						Main.this.angiter=i;
						
						Main.this.simGui.tfX[0].setText(Integer.toString(i-N1+1)+"/"+(N2-N1+1));


						String filepath = Main.this.model.meshFilePath;
						filepath=System.getProperty("user.dir") + "\\motorFull.txt";
						
					//Main.this.model.loadMesh(filepath);	
						String dataFilepath;
						if(old){
							Main.this.model.combineFull(rotf,statf,ang);
							dataFilepath= System.getProperty("user.dir") + "\\dataMotor.txt";
						}
						else{
							Main.this.model.combine(roth,stath,ang);
							dataFilepath= System.getProperty("user.dir") + "\\dataMotorH.txt";
						}
		

					filepath= System.getProperty("user.dir") + "\\motor4th"+i+".txt";
					Main.this.model.writeMesh(filepath);
													 
						
						//String dataFilepath= System.getProperty("user.dir") + "\\dataMotor.txt";
						
						Main.this.model.loadData( dataFilepath,0*702304,0,2*ang);

						for(int ir=1;ir<=Main.this.model.numberOfRegions;ir++){
							Main.this.model.region[ir].setFactJ(1.0);
						}

						filepath= System.getProperty("user.dir") + "\\motor"+i+".txt";
					Main.this.model.writeMesh(filepath);
						 

						
						if(Main.this.model.writeFiles)
							prepare(ang);
						Main.this.femSolver=new FEMsolver(Main.this.model);
						boolean loadFlux=false;
						boolean loadPotential=false;

						if(!loadFlux && !loadPotential){
							Vect x=Main.this.femSolver.solveMagLin(Main.this.model, 1);	

							
							if(Main.this.model.coupled){
								Main.this.femSolver.solveCoupled(Main.this.model,x);
							}
							else
								if(Main.this.model.nonLin){
									x=Main.this.femSolver.solveNonLinear(Main.this.model,x,true);
								}
						

						//	model.writeNodalField( model.fluxFolder+"\\nodalForceReluct"+istep+".txt",1);
							String vPotFile = System.getProperty("user.dir")+"\\flux\\vPot"+(i)+".txt";
							model.writeA(vPotFile);
						}
						else if(loadFlux)
						{					
							String fluxFile = System.getProperty("user.dir")+"\\flux3A-0-180\\flux"+(ang)+".txt";
							//String fluxFile = System.getProperty("user.dir")+"\\flux5A-0-180\\flux5A"+(ang)+".txt";

							Main.this.model.loadFlux(fluxFile);
							 fluxFile = System.getProperty("user.dir")+"\\flux\\flux"+(i)+".txt";
							model.writeB(fluxFile);

						}
						else {
			
							String vPot = System.getProperty("user.dir")+"\\flux\\vPot"+(ang)+".txt";
							Main.this.model.loadPotential(vPot);
							Main.this.model.setB();
							
						}
						
					
												
						Main.this.model.setReluctForce();
					//	model.setMSstrain();
						model.setMSForce();
						model.setTorque(0.005,.02775);
						util.pr("torque >>>>>>>"+Main.this.model.TrqZ);

						//pwt.println(Main.this.model.TrqZ);
						
						
				


							if( Main.this.model.writeFiles){
							
								Main.this.model.writeMesh( Main.this.model.fluxFolder+"\\bun"+ang+".txt");
								
								String fluxFile =  Main.this.model.fluxFolder+"\\vPot"+(ang)+".txt";
								Main.this.model.writeA(fluxFile);
								
								String nodalfile=Main.this.model.fluxFolder+"\\nodalForceReluct"+ang+".txt";
								Main.this.model.writeNodalField(nodalfile,1);
								 nodalfile=Main.this.model.fluxFolder+"\\nodalForceMS"+ang+".txt";
								Main.this.model.writeNodalField(nodalfile,2);
								
								 nodalfile=Main.this.model.fluxFolder+"\\nodalForceTotal"+ang+".txt";
									Main.this.model.writeNodalField(nodalfile,3);


							}

						if(Main.this.model.deform){
							
							if(vibr){
								
								model.freq=50;
								
								dt=1.0/model.freq/180*2;

							Vect u2=Main.this.femSolver.getVibration(Main.this.model,u0,u1,dt,angiter);	
							if(u1!=null)
							u0=u1.deepCopy();
							u1=u2.deepCopy();
							}
							else
								Main.this.femSolver.setDeformation(Main.this.model);	
							
							
					
							if( Main.this.model.writeFiles){
							String nodalfile=Main.this.model.fluxFolder+"\\nodalDisp"+ang+".txt";
							Main.this.model.writeNodalField(nodalfile,-1);
							
							String stressFilePath= Main.this.model.fluxFolder+"\\stress.txt";
							Main.this.model.writeStress(stressFilePath);
							}
							
							pwt.println(model.node[467].getU().norm());
					
						}

						util.pr("Stress Max >>>>>>>"+Main.this.model.stressMax);

						if(Main.this.model.solver.terminate) break;

						if(rec){

							int ix=0;
						for(int k=1;k<=Main.this.model.numberOfNodes;k++){
							
							if(!Main.this.model.node[k].isDeformable()) continue;
							if(util.getAng(Main.this.model.node[k].getCoord())>PI/2) continue;
								uu[ix++][i-N1]=Main.this.model.node[k].getU();
							
						}
						}
						
						Main.this.simGui.tfX[1].setText(Main.this.formatter.format(Main.this.model.TrqZ));

						if(Main.this.model.solver.terminate) break;

					}

					if(rec){
						int ix=0;
					for(int k=1;k<=Main.this.model.numberOfNodes;k++){
						if(Main.this.model.node[k].u==null ) continue;
						if(util.getAng(Main.this.model.node[k].getCoord())>PI/2) continue;

						pwu.println(k+"\t"+model.node[k].getNodalMass());
						for(int i=N1;i<=N2;i+=1){							
							pwu.print(uu[ix][i-N1].el[0]+"\t");
						}
						pwu.println();
						for(int i=N1;i<=N2;i+=1){
							pwu.print(uu[ix][i-N1].el[1]+"\t");
						}
						pwu.println();
						for(int i=N1;i<=N2;i+=1){
							pwu.print(uu[ix][i-N1].norm()+"\t");
						}
						pwu.println();
							for(int i=N1+1;i<=N2;i+=1){							
								pwu.print(uu[ix][i-N1].sub(uu[ix][i-N1-1]).el[0]/dt+"\t");
							}
							pwu.println();
							for(int i=N1+1;i<=N2;i+=1){							
								pwu.print(uu[ix][i-N1].sub(uu[ix][i-N1-1]).el[1]/dt+"\t");
							}
							pwu.println();
							for(int i=N1+1;i<=N2;i+=1){							
								pwu.print(uu[ix][i-N1].sub(uu[ix][i-N1-1]).norm()/dt+"\t");
							}
						
						ix++;
						pwu.println();
					}
					}

					pwu.close();
					pwt.close();





				} catch (IOException exception) {
					// TODO Auto-generated catch-block stub.
					exception.printStackTrace();
				}

				//Main.this.simGui.writeLog(model.fluxFolder+"\\log.txt");
				Main.this.simGui.Run.setBackground(Color.green);

			}
		};


		this.thread1.start();		}*/
	
	public static void main(String[] args){

		new Main();
	}

	public void openMainGUI(){
		this.gui=new GUI(this.path);
		this.gui.setVisible(true);
		this.gui.setVisible(true);	
		this.gui.drp.bDiscretize.addActionListener(this);
		this.gui.drp.bLoadModel.addActionListener(this);
		this.gui.drp.bClear.addActionListener(this);
		this.gui.vwp.fv.bGetValue.addActionListener(this);
		this.gui.vwp.bDeform.addActionListener(this);
		new DropTarget(this.gui.vwp.canvas, this);
	}


	public void discretize(){

		this.gui.drp.discretizeMode();
		System.gc();

		long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.model=new Model(this.gui);						


		long m2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long usedMB=(m2-m1)/(1024*1024);
		if(usedMB<0 || usedMB>this.mheavy){
			this.heavy=true;
			System.out.println(" High memory is required. The graphics are disabled");
		}
		else
			this.heavy=false;
		
		this.gui.tbPanel.setSelectedIndex(1);
		this.gui.drp.discretized=true;

		if(!this.heavy){

			this.gui.vwp.setMesh(this.model);	
			
		}


	//	this.simGui.paramArea.setText("");
	//	Console.redirectOutput(this.simGui.paramArea);
		//this.writer.reportData(this.model);
		//Console.redirectOutput(this.simGui.progressArea);
		
		Console.redirectOutput(this.gui.drp.messageArea);
		
		System.out.println("Used memory for  model setup: "+usedMB+"MB");

		String modelFilePath = System.getProperty("user.dir") + "\\model.txt";
		this.gui.drp.writeModel(modelFilePath);	

		this.model.writeMesh(this.gui.vwp.bunFilePath);	
	
	//	this.model.writeData(this.gui.vwp.dataFilePath);
		
		this.gui.drp.bInfo.doClick();
		
		
		this.gui.vwp.meshDrawn=true;

	}



	public void loadModel(){	

		this.gui.vwp.loadMode();

		this.thread1=new Thread(){

			@Override
			public void run(){
				System.gc();
				long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

				Main.this.model.loadMesh(Main.this.model.filePath);


				long m2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Used memory for  model setup: "+(m2-m1)/(1024*1024)+"MB");


					if(m2<m1 || (m2-m1)/(1024*1024)<Main.this.mheavy)
						Main.this.gui.vwp.setMesh(Main.this.model);
					else
						Main.this.heavy=true;

					Main.this.gui.setTitle("FEM Drawing Panel: "+Main.this.model.filePath);
					Main.this.gui.repaint();


					long m3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					System.out.println("Used memory for drawing mesh: "+(m3-m2)/(1024*1024)+"MB");
					System.out.println();
					System.out.println(" Number of regions: "+Main.this.model.numberOfRegions);
					System.out.println(" Total number of elements: "+Main.this.model.numberOfElements);
					System.out.println(" Total number of nodes: "+Main.this.model.numberOfNodes);

					System.out.println();
				}

		//	}
		};

		this.thread1.start();


	}		



	@Override
	public void actionPerformed(ActionEvent e)
	{	
	
		 if(e.getSource()==this.gui.drp.bDiscretize){
			this.thread1=new Thread(){
				@Override
				public void run(){
					discretize();
				}
			};
			this.thread1.start();	
		}


		else if(e.getSource()==this.gui.drp.bLoadModel)
		{

			FileDialog fd = new FileDialog(new Frame(),"Select bun  file",FileDialog.LOAD);
			fd.setVisible(true);
			fd.toFront();
			String bunFolder=fd.getDirectory();
			String bunFile = fd.getFile();
			if(bunFolder+ bunFile!=null)
			{
				this.model.filePath=bunFolder+"\\"+bunFile;
				this.gui.drp.loadModel( this.model.filePath);

			}
		}


		else if(e.getSource()==this.gui.drp.bClear){
			this.gui.drp.clearStuff();

			System.gc();
		}
	}


	@Override
	public void dragEnter(DropTargetDragEvent dtde) 
	{}

	@Override
	public void dragExit(DropTargetEvent dte) 
	{ }

	@Override
	public void dragOver(DropTargetDragEvent dtde) 
	{ }

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) 
	{  }

	@Override
	public void drop(DropTargetDropEvent dtde) {	   
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			
			for (int i = 0; i < flavors.length; i++) {/*
				if (flavors[i].isFlavorJavaFileListType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					List list = (List) tr.getTransferData(flavors[i]);
					Console.redirectOutput(this.gui.vwp.messageArea);				
					this.model.filePath=list.get(i).toString();
				
					System.out.println("Dropped File: "+ this.model.filePath);
					String str=readFirst( this.model.filePath);

					if(str.equals("hexahedron") || str.equals("triangle")|| str.equals("quadrangle")){
						this.thread1=new Thread(){
							@Override
							public void run(){
								loadModel(); 
							}
						};
						this.thread1.start();
						this.nMesh++;
					}
					else if (str.equals("flux")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadFlux(); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}
					else if (str.equals("vPot")){
						
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadPotential(); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}
					else if (str.equals("H-field")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadH(); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}

					else if (str.equals("stress")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadStress(); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}

					else if (str.equals("displacement")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									//paintDisplacement(); 
									loadNodalField(-1); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}


					else if (str.equals("force_reluc")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadNodalField(1); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}

					else if (str.equals("force_ms")){
						if(this.nMesh>0){
							this.thread1=new Thread(){
								@Override
								public void run(){
									loadNodalField(2); 
								}
							};

							this.thread1.start();

						}
						else{
							String msg="No mesh loded yet.";
							JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
						}
					}
				
					else {
						String msg="Invalid input file.";
						JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
					}

					
				}
				dtde.dropComplete(true);
			
			*/}

			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	public void wait(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {

			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				deleteDir(new File(dir, children[i]));

			}
		}
		else
			dir.delete();



	}



}

