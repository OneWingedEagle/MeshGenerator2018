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

