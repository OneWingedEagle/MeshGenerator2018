package drawingPanel;
import ReadWrite.Loader;
import components.Button;
import components.*;
import components.Label;
import components.TextField;
import graphics.*;
import panels.*;
import materialData.MaterialData;
import math.Vect;
import math.util;
import fem.Model;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import static java.lang.Math.*;

import javax.media.j3d.ImageComponent2D;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class ViewingPanel2D extends JPanel implements ActionListener {
	public ButtonIcon bShowAxes,bShowMesh;
	public ButtonIcon bDeform;
	public Button bLoadMesh, bClear,bNavL, bNavR, 
	bNavU ,bNavD,bNavZoomIn,bNavZoomOut,bFindValue;
	private Button bInfo, bApplyVscale,bRefresh;
	public ButtonIcon bDefaultView,bFullScreen, bChangeBackground;
	//public JCheckBox showMesh;
	public JComboBox  distrib;
	public TextField tfVectorScale;
	private JPanel  regButtonPanel ,southPanel,colBar;
	private Canvas2D board2D;
	public FindValue fv = new FindValue();
	private JFrame messageFrame,fsFrame;
	private TextField tfNumbRegs;
	private Button[] regButton;
	public int  zoom = 0;
	private VectField[] vField;

	public Color[] regionMatColor;
	private double spaceTransparency = .85, blockTransparency = 0;
	public MaterialData matData = new MaterialData();
	public int nChosenRegion=0, nBoundary = 6;
	public int decimal = 3, numberOfElements, numberOfRegions;
	public double  scaleFactor,moveStep0,moveStep,Bmin,Bmax,nodalFmin,nodalFmax,
	nodalStressMax,stressMax;
	private Vect camEye,camEye0=new Vect(.6,-2.6,1.6), target,target0=new Vect(3),upVect,upVect0=new Vect(0,0,1);
	public boolean meshDrawn = false,meshLoaded,axesShown=true,meshShown;
	public boolean[] showRegion;
	public String bunFilePath, dataFilePath, fluxFilePath, fluxFilePath1,
	eddyFilePath, elastDataFilePath, vPotFilePath,elType;
	ColorBar cBar;
	private JScrollPane messageScrollPane;
	public JTextArea messageArea;
	public int fieldMode,defMode,dim;
	public JProgressBar progressBar;
	private Thread progressThread;
	private int  width,height,backgroundMode=0,unitIndex=1,nfs;
	private DecimalFormat formatter=new DecimalFormat("0.0E0");
	private Loader loader=new Loader();
	private double[] spaceBoundary=new double[6];

	public ViewingPanel2D() {
		
		// Constructing the drawing panel north ===================== begin
		
		JPanel drwpNorth = new JPanel(new FlowLayout(0, 10, 10));
		this.width=1000;
		this.height=800;
		for(int i=0;i<this.spaceBoundary.length;i++)
			if(i%2==0)
			this.spaceBoundary[i]=-1000;
			else
				this.spaceBoundary[i]=1000;
				
		
		this.bDeform = new ButtonIcon();
		this.bDeform.setPreferredSize(new Dimension(30, 30));
		this.bDeform.setImageIcon("deform.jpg","Add Coil");

		this.bDefaultView = new ButtonIcon();
		this.bDefaultView.setPreferredSize(new Dimension(30, 30));
		this.bDefaultView.setImageIcon("defView.jpg","Default View");
		
		this.bClear = new Button("");
		this.bClear.setPreferredSize(new Dimension(30, 30));
		this.bClear.setImageIcon("clear.jpg", "Add block");
		
		
		this.bChangeBackground = new ButtonIcon();
		this.bChangeBackground.setPreferredSize(new Dimension(40, 30));
		this.bChangeBackground.setImageIcon("ChangeBackground.jpg","Change Background Color");

		this.bRefresh = new Button("");
		this.bRefresh.setPreferredSize(new Dimension(30, 30));
		this.bRefresh.setImageIcon("refresh.jpg","Refresh");

		this.bLoadMesh = new Button("");
		this.bLoadMesh.setPreferredSize(new Dimension(30, 30));
		this.bLoadMesh.setImageIcon("loadModel.jpg","Load Mesh");

		this.bInfo = new Button("");
		this.bInfo.setPreferredSize(new Dimension(30, 30));
		this.bInfo.setImageIcon("info.jpg","info");

		this.bNavL = new Button("\u2190");	
		this.bNavU = new Button("\u2191");
		this.bNavR = new Button("\u2192");
		this.bNavD = new Button("\u2193");
		this.bNavZoomIn = new Button("+");
		this.bNavZoomOut = new Button("-");

		this.bNavL.setFont(new Font("Times New Roman", 1, 12));
		this.bNavR.setFont(new Font("Times New Roman", 1, 12));
		this.bNavU.setFont(new Font("Times New Roman", 1, 12));
		this.bNavD.setFont(new Font("Times New Roman", 1, 12));
		this.bNavZoomIn.setFont(new Font("Times New Roman", 1, 12));
		this.bNavZoomOut.setFont(new Font("Times New Roman", 1, 12));

		this.bNavL.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavL.setBorder(BorderFactory.createLineBorder(Color.black));
		this.bNavR.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavR.setBorder(BorderFactory.createLineBorder(Color.black));
		this.bNavD.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavD.setBorder(BorderFactory.createLineBorder(Color.black));
		this.bNavU.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavU.setBorder(BorderFactory.createLineBorder(Color.black));
		this.bNavZoomIn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavZoomIn.setBorder(BorderFactory.createLineBorder(Color.black));
		this.bNavZoomOut.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.bNavZoomOut.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.bNavL.setPreferredSize(new Dimension(15, 15));
		this.bNavR.setPreferredSize(new Dimension(15, 15));
		this.bNavU.setPreferredSize(new Dimension(15, 15));
		this.bNavD.setPreferredSize(new Dimension(15, 15));
		this.bNavZoomIn.setPreferredSize(new Dimension(15, 15));
		this.bNavZoomOut.setPreferredSize(new Dimension(15, 15));
		
		this.tfVectorScale = new TextField("1");
		this.tfVectorScale.setPreferredSize(new Dimension(55, 25));
		

		this.bFullScreen = new ButtonIcon();
		this.bFullScreen.setPreferredSize(new Dimension(30, 30));
		this.bFullScreen.setImageIcon("maxmin.jpg","Full Screen");
		
		
		this.bShowAxes = new ButtonIcon();
		this.bShowAxes.setPreferredSize(new Dimension(30, 30));
		this.bShowAxes.setImageIcon("axes.jpg","Show/Hide axes");
		
		this.bShowMesh = new ButtonIcon();
		this.bShowMesh.setPreferredSize(new Dimension(30, 30));
		this.bShowMesh.setImageIcon("discretize.jpg","Show/Hide Mesh");
		
		
		///  message frame ====
		this.messageArea = new JTextArea();
		this.messageArea.setBackground(Color.white);

		this.messageArea.setFont(new Font("Arial", 0, 14));
		this.messageArea.setEditable(false);

		this.messageScrollPane = new JScrollPane(this.messageArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.messageScrollPane.setPreferredSize(new Dimension(500, 125));
		this.messageScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 15,10));

		this.messageFrame = new JFrame(" Analysis Progress");
		this.messageFrame.setLocation(50, 550);
		this.messageFrame.setPreferredSize(new Dimension(600, 400));
		this.messageFrame.add(this.messageScrollPane);
		//===========
		
		// fullscreen frame========
		this.fsFrame=new JFrame();
		this.fsFrame.setSize(1500, 1200);
		//=========================
	

		//==== Navigation panel 
		Panel navPanel=new Panel(new GridLayout(2,3));
		navPanel.add(this.bNavL);
		navPanel.add(this.bNavU);
		navPanel.add(this.bNavZoomIn);
		navPanel.add(this.bNavR);
		navPanel.add(this.bNavD);
		navPanel.add(this.bNavZoomOut);
	
		drwpNorth.add(this.bDefaultView);
		drwpNorth.add(this.bLoadMesh);
		drwpNorth.add(this.bClear);
		drwpNorth.add(navPanel);
		drwpNorth.add(this.bRefresh);
		drwpNorth.add(this.bChangeBackground);
		drwpNorth.add(this.bInfo);
		drwpNorth.add(this.bFullScreen);
		drwpNorth.add(this.bShowAxes);
		drwpNorth.add(this.bShowMesh);
		
		//==========================
				
		// Constructing Constructing the drawing panel north  ===================== end
		
		
		
		// Constructing Constructing the drawing panel West  ===================== begin
		
		 JTabbedPane drwpWest = new JTabbedPane();
		 drwpWest.setFont(new Font("Arial", 1, 12));

		 
			this.tfNumbRegs = new TextField("0");
			
			this.regButtonPanel = new JPanel(new  FlowLayout(0, 1, 10));
			this.regButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.regButtonPanel.setPreferredSize(new Dimension(130, 3000));
			Label lbNumbRegs = new Label("Number of Regions");
			lbNumbRegs.setFont(new Font("Times New Roman", 1, 12));
			this.tfNumbRegs.setPreferredSize(new Dimension(60, 25));
			this.tfNumbRegs.setEditable(false);

			this.regButtonPanel.add(lbNumbRegs);
			this.regButtonPanel.add(this.tfNumbRegs);
			
			drwpWest.addTab("Regions",this.regButtonPanel);
			
			JScrollPane westScrollPane = new JScrollPane(drwpWest,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Constructing Constructing the drawing panel West  ===================== end



		// Constructing Constructing the drawing panel East  ===================== begin

			JPanel eastPanel = new JPanel(new FlowLayout(0, 1, 10));
			//eastPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			eastPanel.setPreferredSize(new Dimension(120, 1000));
			eastPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	
		
		// Constructing Constructing the drawing panel East  ===================== end

			
			
			
			
		
		// Constructing drawing panel ===================== begin
		



		//this.board2D = new JPanel(new GridLayout(1, 1));
			this.board2D=new Canvas2D();
		this.board2D.setBorder(BorderFactory.createLineBorder(Color.black));
		this.board2D.setBackground(Color.white);

			
			
			
		
		// Constructing SimpleUniverse ===================== end
		

				
		//MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
		
					
		this.bunFilePath = System.getProperty("user.dir") + "//bun.txt";
		this.fluxFilePath = System.getProperty("user.dir") + "//flux.txt";
		this.fluxFilePath1 = System.getProperty("user.dir") + "//flux";
		this.eddyFilePath = System.getProperty("user.dir") + "//eddy.txt";
		this.vPotFilePath = System.getProperty("user.dir") + "//vPot.txt";
		this.dataFilePath = System.getProperty("user.dir") + "//data.txt";
		this.elastDataFilePath = System.getProperty("user.dir") + "//elastData.txt";



		JScrollPane southScrollPane = new JScrollPane(this.southPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		


		setLayout(new BorderLayout());
		add("North", drwpNorth);
		add("South", southScrollPane);
		add("East", eastPanel);
		add("West", westScrollPane);
		add("Center", this.board2D);

		this.bDefaultView.addActionListener(this);
		this.bClear.addActionListener(this);
		this.bDeform.addActionListener(this);
		this.bInfo.addActionListener(this);
		this.bRefresh.addActionListener(this);
		this.bShowMesh.addActionListener(this);
		this.bShowAxes.addActionListener(this);

		this.camEye=this.camEye0.deepCopy();
		this.target=this.target0.deepCopy();
		this.upVect=this.upVect0.deepCopy();
		newViewer();
		//getScene();
		

	}
	
	

	
	public void paintComponent(Graphics g) {
		// Clear background if opaque
		super.paintComponent(g);
		// Cast Graphics to Graphics2D
		Graphics2D g2d = (Graphics2D)g;
		// Set pen parameters
		double x=600,y=500,diameter=200;
		
		Ellipse2D.Double circle =
				new Ellipse2D.Double(x, y, diameter, diameter);
				g2d.fill(circle);
/*		g2d.setPaint(fillColorOrPattern);
		g2d.setStroke(penThicknessOrPattern);
		g2d.setComposite(someAlphaComposite);
		g2d.setFont(anyFont);
		g2d.translate(...);
		g2d.rotate(...);
		g2d.scale(...);
		g2d.shear(...);
		g2d.setTransform(someAffineTransform);
		// Allocate a shape
		SomeShape s = new SomeShape(...);
		// Draw shape
		g2d.draw(s); // outline
		g2d.fill(s); // solid
*/	}

	/*public void getScene() {

		addLights();
		this.group.setCapability(BranchGroup.ALLOW_DETACH);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				1000);

		mouseRotate = new MouseRotate();

		MouseTranslate mouseTranslate = new MouseTranslate();


		this.refGroup = new TransformGroup();
		this.refGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.refGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		mouseRotate.setTransformGroup(this.refGroup);
		mouseTranslate.setTransformGroup(this.refGroup);

		this.refGroup.addChild(mouseRotate);
		this.refGroup.addChild(mouseTranslate);

		mouseRotate.setSchedulingBounds(bounds);
		mouseTranslate.setSchedulingBounds(bounds);

		this.canvas.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int steps = e.getWheelRotation();
				ViewingPanel.this.zoom -= steps;
				camEye=camEye.add(target.sub(camEye).times(.1*steps));
				newViewer();
			}
		});

		View view = this.universe.getViewer().getView();            
		// far way objects are not shown
		view.setBackClipDistance(100.0d);
		// very close objects are also clipped
		view.setFrontClipDistance(0.001d);             
		this.blockGroup = new TransformGroup();
		this.vFieldGroup = new TransformGroup();
		this.refGroup.addChild(this.blockGroup);
		this.cartesian = new Cartesian(this.spaceBoundary, Color.red, Color.green,
				Color.blue);
		this.refGroup.addChild(this.cartesian);


		this.group.addChild(this.refGroup);





	}
	
*/
	
	public void newViewer() {

/*
		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new P3d(camEye), new P3d(target),
				new Vector3d(upVect.el[0],upVect.el[1],upVect.el[2]));
		lookAt.invert();
		this.universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);*/

	}


/*

	public void newViewer() {


		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new P3d(camEye), new P3d(target),
				new Vector3d(upVect.el[0],upVect.el[1],upVect.el[2]));
		lookAt.invert();
		this.universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);

	}

	public void fitViewer() {
		for(int i=0;i<20;i++){
			this.zoom--;
			wait(100);
			newViewer();
		}

		double Px = .2;
		double Py =.5;
		double Pz =2;


		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new Point3d(Px, Py, Pz), new Point3d(0, 0, 0),
				new Vector3d(-Px, -Py, 1));
		lookAt.invert();
		universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);

	}

	public void viewer2D() {
		zoom=-30;

		double Px = 0;
		double Py =0;
		double Pz = 0.6 * exp(this.zoom / 20.0);


		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new Point3d(Px, Py, Pz), new Point3d(0, 0, 0),
				new Vector3d(-Px, -Py, 2));
		lookAt.invert();
		this.universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);


	}

	public void resetView() {

		this.refGroup.setTransform(new Transform3D());
		camEye=camEye0.deepCopy();
		target=target0;
		this.zoom = 0;
		newViewer();
	}
*/
/*	public void fullScreen() {
		if(nfs%2==0){
			board2D.remove(canvas);
			fsFrame.add(canvas);
			fsFrame.setVisible(true);
		}
		else{		
			fsFrame.remove(canvas);	
			fsFrame.setVisible(false);
			board2D.add(canvas);

		}
		nfs++;
		repaint();

	}*/

/*	public void addLights() {

		Color3f light1Color = new Color3f(.85f, .85f, .85f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		Vector3f light1Direction = new Vector3f(-5f, -10f, 20f);
		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		group.addChild(light1);

		Color3f light2Color = new Color3f(.3f, .3f, .3f);
		Vector3f light2Direction = new Vector3f(-1f, -1f, -10f);
		DirectionalLight light2 = new DirectionalLight(light2Color,
				light2Direction);
		light2.setInfluencingBounds(bounds);
		group.addChild(light2);


		AmbientLight light3 = new AmbientLight(new Color3f(.6f, .6f, .6f));
		
		light3.setInfluencingBounds(bounds);
		group.addChild(light3);

	}*/

	public void actionPerformed(ActionEvent e) {

	/*if (e.getSource() == this.bDefaultView) {

			resetView();

		}

		else if (e.getSource() == this.bFullScreen) {

			fullScreen();

		}
*/

		 if (e.getSource() == this.bInfo) {
			this.messageFrame.setVisible(true);
			this.messageFrame.pack();

		}

		else if (e.getSource() == this.bFindValue) {


			this.fv.setLocation(getX() + 800, getY() +400);
			this.fv.setVisible(true);



		}
	else if (e.getSource() == this.bApplyVscale) {
			double a;
			try {
				a = Double.parseDouble(this.tfVectorScale.getText());
				rescale(a);
			} catch (NumberFormatException ex) {
			}
		}
		else if (e.getSource() == this.bRefresh) {
			if(this.fieldMode==1)
				this.showFlux(true);

			else if(this.fieldMode>1 && this.fieldMode<5)
				this.showNodalField(true);
			else
				this.showMesh(true);

		}

		/*else if (e.getSource() == this.bChangeBackground)
			changeBackground();*/
		
		else if (e.getSource() == this.bShowAxes){
			this.axesShown=!this.axesShown;
			showAxes(this.axesShown);
		}
		else if (e.getSource() == this.bShowMesh){
			this.meshShown=!this.meshShown;
			showMesh(this.meshShown);
		}
		else if (e.getSource() == this.bClear){
			clearStuff();
		}
		else if (e.getSource() == this.bNavL)
			moveLeft();
		else if (e.getSource() == this.bNavR)
			moveRight();
		else if (e.getSource() == this.bNavU)
			moveUp();
		else if (e.getSource() == this.bNavD)
			moveDown();
		else if (e.getSource() == this.bNavZoomIn)
			zoomIn();

		else{

				for( int i=1;i<=this.numberOfRegions;i++){

					if(e.getSource() == this.regButton[i]) {

						try {

							this.nChosenRegion =i;

							this.showRegion[this.nChosenRegion]=!this.showRegion[this.nChosenRegion];
							if(this.showRegion[this.nChosenRegion])
								this.regButton[this.nChosenRegion].setBackground(this.regionMatColor[this.nChosenRegion]);
							else
								this.regButton[this.nChosenRegion].setBackground(Color.white);

						}

						catch (NumberFormatException nfe) {
						}

						break;

					}
				}
		}
	}




	public void addRegButton(int ir,String name) {
		this.regButton[ir] = new Button(name);
		this.regButton[ir].setName(Integer.toString(ir));
		if(this.showRegion[ir])
			this.regButton[ir].setBackground(this.matData.matColor(name));
		this.regButton[ir].addActionListener(this);
		this.regButton[ir].setPreferredSize(new Dimension(85, 25));
		this.regButtonPanel.add(this.regButton[ir]);
		this.regButtonPanel.updateUI();
		this.tfNumbRegs.setText(Integer.toString(ir));

	}

/*
	public void changeBackground() {
		this.backgroundMode++;
		this.group.detach();

		int mode=this.backgroundMode%5;

		if (mode==1) {

			TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);

		}
		if (mode==2) {
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("black.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);

		}
		else if (mode==3)
		{
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("white.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);
		}
		else if (mode==4)
		{
			JColorChooser ch=new JColorChooser();
			Color color=ch.showDialog(new JFrame(), "Pick a Color",Color.blue);
			background.setImage(null);
			background.setColor(new Color3f(color));
			background.setCapability(Background.ALLOW_COLOR_WRITE);
			BoundingSphere sphere = new BoundingSphere(new Point3d(0,0,0), 100000);
			background.setApplicationBounds(sphere);



		}
		else if (mode==0)
		{
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky2.jpg"), this );
			this.image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(this.image);

		}

		this.universe.addBranchGraph(this.group);



	}*/

	public void moveLeft(){
		this.camEye.el[0]+=this.moveStep;
		this.target.el[0]+=this.moveStep;
		newViewer();
	}
	public void moveRight(){
		this.camEye.el[0]-=this.moveStep;
		this.target.el[0]-=this.moveStep;
		newViewer();
	}
	public void moveUp(){
		this.camEye.el[1]-=this.moveStep;
		this.target.el[1]-=this.moveStep;
		newViewer();
	}
	public void moveDown(){

		this.camEye.el[1]+=this.moveStep;
		this.target.el[1]+=this.moveStep;
		newViewer();

	}
	public void zoomIn(){
		this.zoom--;
		//moveStep=moveStep0*exp(.1*zoom);
		this.camEye.el[2]*=exp(this.zoom / 20.0);
		newViewer();

	}



	public void setMesh(Model model) {
		this.dim=model.dim;
		model.defScale=Double.parseDouble(this.tfVectorScale.getText());
		clearRegButtons();
		this.meshLoaded = true;
		this.numberOfRegions = model.numberOfRegions;
		this.regButton=new Button[this.numberOfRegions+1];
		this.showRegion=new boolean[this.numberOfRegions+1];
		this.regionMatColor=new Color[this.numberOfRegions+1];

		for (int ir = 1; ir <= this.numberOfRegions; ir++){

			this.regionMatColor[ir] = this.matData.matColor(model.region[ir].getMaterial());

			this.showRegion[ir]=true;

			addRegButton(ir,model.region[ir].getMaterial());

		}

		for (int ir = 1; ir <= this.numberOfRegions; ir++)
			if(model.region[ir].getMaterial().startsWith("air")){
				this.showRegion[ir]=false;
				this.regButton[ir].setBackground(Color.white);
			}


		if(model.dim==2) {
			if(model.spaceBoundary==null) this.camEye0.el[2]=1;
			else 
				this.camEye0.el[2]=2*model.spaceBoundary[1];

			this.camEye0.el[0]=0;
			this.camEye0.el[1]=0;
			this.target0=new Vect(0,0,0);
			this.upVect0.el[0]=0;  this.upVect0.el[1]=1;this.upVect0.el[2]=0;
			this.camEye=this.camEye0.deepCopy();
			this.target=this.target0.deepCopy();
			this.upVect=this.upVect0.deepCopy();
			newViewer();

		}

		else
		{
			this.zoom=-(int)(1/model.spaceBoundary[5]);
			newViewer();}

		this.moveStep0=model.maxDim*.005;	
		this.moveStep=this.moveStep0;

	}

	public void paintDisp(Model model) {/*


		this.group.detach();

		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			if (model.region[ir].getMaterial().startsWith("air")) continue;
			this.vFieldGroup.removeChild(this.surfFacets[ir]);
			this.surfFacets[ir].setEdgeTransparency(.5);
			this.surfFacets[ir].paintDisp(model);


			this.vFieldGroup.addChild(this.surfFacets[ir]);

		}

		this.universe.addBranchGraph(this.group);

	*/}

	public void paintStress(Model model) {/*

		double pw=.5;
		this.nodalStressMax=model.nodalStressMax;
		this.stressMax=model.stressMax;

		this.group.detach();

		for (int ir = 1; ir <=this.numberOfRegions; ir++) {

			this.vFieldGroup.removeChild(this.surfFacets[ir]);
			this.surfFacets[ir].setEdgeTransparency(.5);
			if(pw==0)
				this.surfFacets[ir].paintStressLog(model);
			else
				this.surfFacets[ir].paintStress(model, pw);	

			this.vFieldGroup.addChild(this.surfFacets[ir]);

		}

		this.universe.addBranchGraph(this.group);

		if(pw==0)
			setColorBarLog("Stress (Pa)",0,this.nodalStressMax);
		else
			setColorBar("Stress (Pa)",model.nodalStressMin,model.nodalStressMax,pw);


	*/}
	public void showFlux(boolean b) {/*


		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++){

			if(showRegion[ir]){
				this.surfFacets[ir].showFacets(false);

				if (dim==2)
					this.surfFacets[ir].setEdgeColor(Color.gray,.3);	
					else
					this.surfFacets[ir].setEdgeColor(Color.gray,.6);	
				this.surfFacets[ir].showEdges(true);
			}
			this.vField[ir].showFlux(b && showRegion[ir]);
		}

		DecimalFormat formatter=new DecimalFormat("0.00E00");
		this.tfVectorScale.setText(formatter.format(this.vField[1].getVectorScale()));

		this.universe.addBranchGraph(this.group);


		if(b) setColorBar("Flux density (T)",this.Bmin, this.Bmax,1);


	*/}

	public void setColorBarLog(String title,double min, double max){
		{
			this.cBar=new ColorBar();

			if(this.colBar!=null)
				this.regButtonPanel.remove(this.colBar);

			this.colBar=this.cBar.getColorBarPnLog(min,max,title,15);

			this.regButtonPanel.add(this.colBar);
			this.regButtonPanel.updateUI();

		}
	}

	public void setColorBar(String title,double min, double max,double pw){
		{
			this.cBar=new ColorBar(min,max);

			if(this.colBar!=null)
				this.regButtonPanel.remove(this.colBar);

			this.colBar=this.cBar.getColorBarPn(min,max,pw,title,15);

			this.regButtonPanel.add(this.colBar);
			this.regButtonPanel.updateUI();

		}
	}

	public void clearStuff() {/*
		this.group.detach();
		this.refGroup.removeChild(this.vFieldGroup);
		this.blockGroup = new TransformGroup();
		this.vFieldGroup = new TransformGroup();


		this.fieldMode=0;
		resetView();
		this.meshDrawn = false;
		this.meshLoaded = false;
		this.universe.addBranchGraph(this.group);

		clearRegButtons();
		
		this.distrib.setEnabled(true);
		this.progressThread = null;
		this.bShowMesh.doClick();

	*/}
	
	
	public void clearRegButtons() {
		for (int i = 1; i <= this.numberOfRegions; i++)
			this.regButtonPanel.remove(this.regButton[i]);
		this.tfNumbRegs.setText(Integer.toString(0));
		this.regButtonPanel.updateUI();
		this.numberOfRegions = 0;
		if(this.colBar!=null)
			this.regButtonPanel.remove(this.colBar);
			//	southPanelMode(-1);

	}
	
	
	public void setFlux(Model model){
		setFlux(model,false);
		this.Bmax=model.Bmax;
		this.Bmin=model.Bmin;
	}

	public void setFlux(Model model,boolean isH) {/*

		int n;

		if (this.meshLoaded)
			n = 0;
		else
			n = this.distrib.getSelectedIndex();
		this.group.detach();
		this.refGroup.removeChild(this.vFieldGroup);
		for (int ir = 1; ir <=model.numberOfRegions; ir++) {
			this.vFieldGroup.removeChild(this.vField[ir]);
			this.vField[ir].setFlux(model, n,isH);
			this.vField[ir].showFlux(showRegion[ir]);
			if (this.vField[ir].getParent() == null)
				this.vFieldGroup.addChild(this.vField[ir]);
		}

		if (this.vFieldGroup.getParent() == null)
			this.refGroup.addChild(this.vFieldGroup);
		this.universe.addBranchGraph(this.group);

		fieldMode=1;

	*/}


	public void rescale(double a) {/*

		if(a<=0){
			String msg = "Scale factor must be positive.";

			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE);
		}
		else{
			this.group.detach();

			for (int ir = 1; ir <=this.numberOfRegions; ir++){
		
				if(!showRegion[ir]) continue;
				if(fieldMode==1 ){
					if(this.vField!=null && ir<this.vField.length && this.vField[ir]!=null && this.vField[ir].getParent()!=null)
						this.vField[ir].rescale(a);
				}
					else if(fieldMode>=2 && fieldMode<=4)
						this.nodalField[ir].rescale(a);
			}
			this.universe.addBranchGraph(this.group);
		}
	*/}



/*	public void setCurrent(Model model) {
		setVectField(model);
		for (int ir = 1; ir <= model.numberOfRegions; ir++)
			if (model.region[ir].hasJ) {
				this.vField[ir].setJ0(model);
			}
	}
*/
	public void setVectField(Model model) {
		int vectMode=0,arrMode=1;
		if(model.dim==2) vectMode=1;
		this.numberOfRegions = model.numberOfRegions;

		this.vField = new VectField[this.numberOfRegions + 1];

		for (int ir = 1; ir <= this.numberOfRegions; ir++) {

			this.vField[ir] = new VectField(ir, model.region[ir].getFirstEl(),
					model.region[ir].getLastEl(),
					this.matData.matColor(model.region[ir].getMaterial()),vectMode,arrMode);
		}
	}


	public void setNodalField(Model model,int mode) {/*
		if(mode==-1)
			this.nodalFmax=1e9*model.uMax;
		if(mode==1)
			this.nodalFmax=model.FreluctMax;
		else
			if(mode==2)
				this.nodalFmax=model.FmsMax;

		if(mode==-1) fieldMode=2;
		else
			fieldMode=mode+2;
		this.numberOfRegions = model.numberOfRegions;
		this.nodalField = new NodalField[this.numberOfRegions + 1];

		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			if(model.dim==3)
				//nodalField[ir] = new NodalField(model,surfFacets[ir].surfaceQuadNodes,mode,0);
				nodalField[ir] = new NodalField(model,ir,mode,1);
			else
				this.nodalField[ir] = new NodalField(model,ir,mode,1);
			this.vFieldGroup.addChild(this.nodalField[ir]);
		}

		this.universe.addBranchGraph(this.group);

	*/}


	public void showNodalField(boolean b) {/*
		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			{

				this.surfFacets[ir].showFacets(!b && showRegion[ir]);
				this.nodalField[ir].showField(b && showRegion[ir]);
				if (dim==2)
				this.surfFacets[ir].setEdgeColor(Color.gray,.5);	
				else
				this.surfFacets[ir].setEdgeColor(Color.gray,.6);	
			}
		}

		this.universe.addBranchGraph(this.group);

		DecimalFormat formatter=new DecimalFormat("0.00E00");
		this.tfVectorScale.setText(formatter.format(this.nodalField[1].getVectorScale()));

		String st="";
		if(fieldMode==2){
			st="Displac.(nm)";
		}
		else if(fieldMode==3){
			st="Reluct. Force";
		}
		else if(fieldMode==4){
			st="M.S. Force";
		}


		if(b) setColorBar(st,0, this.nodalFmax,1);
	*/}


	public void showMesh(boolean b) {/*

		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			{	
				if(this.surfFacets[ir]==null) continue;

				this.surfFacets[ir].showFacets(b &&showRegion[ir]);
				this.surfFacets[ir].showEdges(b&& showRegion[ir]);
			}
		}

		this.universe.addBranchGraph(this.group);

		if(b && this.numberOfRegions>0){
			southPanel.removeAll();
		//	southPanel.add(this.regButtonPanel);
			southPanel.updateUI();
		}




	*/}


	public void showAxes(boolean b) {/*
		this.group.detach();
		this.group.removeChild(this.refGroup);
		if (b)
			this.refGroup.addChild(this.cartesian);			
		else
			this.refGroup.removeChild(this.cartesian);
		this.group.addChild(this.refGroup);
		this.universe.addBranchGraph(this.group);
	*/}



	public void loadMode() {/*

		this.messageArea.setText("");
		Console.redirectOutput(this.messageArea);
			this.messageFrame.setVisible(true);
		this.messageFrame.pack();

		this.progressThread = new Thread() {

			public void run() {
				ViewingPanel.this.progressBar.setVisible(true);
				int kt = 0;
				while (!ViewingPanel.this.meshLoaded) {
					kt++;
					ViewingPanel.this.progressBar.setValue(10 * (kt % 11));
					ViewingPanel.this.progressBar.repaint();
					try {
						Thread.sleep(500);
					} catch (InterruptedException err) {
					}

				}
				ViewingPanel.this.progressBar.setVisible(false);
			}
		};

		this.progressThread.start();

	*/}



/*	public void loadModel(String modelFilePath) {
		loader.loadModel(this,modelFilePath);

	}*/

	public void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}