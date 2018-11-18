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
import java.text.DecimalFormat;
import static java.lang.Math.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.image.TextureLoader;

import javax.swing.BorderFactory;
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

public class ViewingPanel extends JPanel implements ActionListener {
	public ButtonIcon bShowAxes,bShowMesh;
	public ButtonIcon bDeform;
	public Button bLoadMesh, bClear,bNavL, bNavR, 
	bNavU ,bNavD,bNavZoomIn,bNavZoomOut,bFindValue,bColorChooser;
	private Button bInfo, bApplyVscale,bRefresh;
	public ButtonIcon bDefaultView,bFullScreen, bChangeBackground;
	public JComboBox  distrib;
	public TextField tfVectorScale;
	private JPanel  regButtonPanel ,southPanel, centerPanel,colBar,eastPanel;
	public FindValue fv = new FindValue();
	private JFrame messageFrame,fsFrame;
	private TextField tfNumbRegs;
	private Button[] regButton;
	private JColorChooser cch=new JColorChooser();
	public int  zoom = 0;
	private VectField[] vField;
	private NodalField[] nodalField;
	private BranchGroup group = new BranchGroup();
	private TransformGroup refGroup, blockGroup, vFieldGroup;
	private Cartesian cartesian;
	private Cartesian2D cartesian2D;
	private SurfFacets[] surfFacets;
	private SimpleUniverse universe;
	public Color[] regionMatColor;
	private Color tempColor;
	private double spaceTransparency = .85, blockTransparency = 0;
	public MaterialData matData = new MaterialData();
	public Canvas3D canvas;
	public int nChosenRegion=0, nBoundary = 6;
	public int decimal = 3, numberOfElements, numberOfRegions;
	public double  scaleFactor,moveStep0,moveStep,Vmin,Vmax;
	private Vect camEye,camEye0=new Vect(.6,-2.6,1.6), target,target0=new Vect(3),upVect,upVect0=new Vect(0,0,1);
	public boolean meshDrawn = false,meshLoaded,axesShown=true,meshShown;
	public boolean[] showRegion;
	public String bunFilePath, dataFilePath, fluxFilePath, fluxFilePath1,
	eddyFilePath, elastDataFilePath, vPotFilePath,elType;
	ColorBar cBar=new ColorBar();
	private JScrollPane messageScrollPane;
	public JTextArea messageArea;
	public int fieldMode,defMode,dim;
	public JProgressBar progressBar;
	private Thread progressThread;
	private Background background;
	ImageComponent2D image;
	private int  width,height,backgroundMode=0,unitIndex=1,nfs;
	private DecimalFormat formatter=new DecimalFormat("0.0E0");
	private MouseRotate mouseRotate;
	private Loader loader=new Loader();
	private double[] spaceBoundary=new double[6];
	public TextField[] tfc=new TextField[2];
	private String cBatTitle;

	public ViewingPanel() {
		
		// Constructing the drawing panel north ===================== begin
		
		JPanel drwpNorth = new JPanel(new FlowLayout(0, 10, 10));
		width=1000;
		height=800;
		for(int i=0;i<spaceBoundary.length;i++)
			if(i%2==0)
			spaceBoundary[i]=-1000;
			else
				spaceBoundary[i]=1000;
				
		tfc[0]=new TextField("");
		tfc[1]=new TextField("");


		
		this.bDeform = new ButtonIcon();
		this.bDeform.setPreferredSize(new Dimension(30, 30));
		this.bDeform.setImageIcon("deform.jpg","derom");

		this.bDefaultView = new ButtonIcon();
		this.bDefaultView.setPreferredSize(new Dimension(30, 30));
		this.bDefaultView.setImageIcon("defView.jpg","Default View");
		
		this.bClear = new Button();
		this.bClear.setPreferredSize(new Dimension(30, 30));
		this.bClear.setImageIcon("clear.jpg", "Add block");
		
		
		this.bChangeBackground = new ButtonIcon();
		this.bChangeBackground.setPreferredSize(new Dimension(40, 30));
		this.bChangeBackground.setImageIcon("ChangeBackground2.jpg","Change Background Color");

		this.bRefresh = new Button();
		this.bRefresh.setPreferredSize(new Dimension(30, 30));
		this.bRefresh.setImageIcon("refresh.jpg","Refresh");

		this.bLoadMesh = new Button();
		this.bLoadMesh.setPreferredSize(new Dimension(30, 30));
		this.bLoadMesh.setImageIcon("loadModel.jpg","Load Mesh");

		this.bInfo = new Button();
		this.bInfo.setPreferredSize(new Dimension(30, 30));
		this.bInfo.setImageIcon("info.jpg","info");

		this.bNavL = new Button();	
		this.bNavU = new Button();
		this.bNavR = new Button();
		this.bNavD = new Button();
		this.bNavZoomIn = new Button();
		this.bNavZoomOut = new Button();
		this.bNavL.setImageIcon("arrowLeft.jpg","left");
		this.bNavR.setImageIcon("arrowRight.jpg","right");
		this.bNavU.setImageIcon("arrowUp.jpg","up");
		this.bNavD.setImageIcon("arrowDown.jpg","down");
		this.bNavZoomIn.setImageIcon("zoomIn.jpg","Zoom In");
		this.bNavZoomOut.setImageIcon("zoomOut.jpg","Zoom In");
		

		this.bNavL.setMargin(new Insets(0,0,0,0));  
		this.bNavR.setMargin(new Insets(0,0,0,0));  
		this.bNavD.setMargin(new Insets(0,0,0,0));  
		this.bNavU.setMargin(new Insets(0,0,0,0));  
		this.bNavZoomIn.setMargin(new Insets(0,0,0,0));  
		this.bNavZoomOut.setMargin(new Insets(0,0,0,0));  

		
		this.bNavL.setPreferredSize(new Dimension(18, 18));
		this.bNavR.setPreferredSize(new Dimension(18, 18));
		this.bNavU.setPreferredSize(new Dimension(18, 18));
		this.bNavD.setPreferredSize(new Dimension(18, 18));
		this.bNavZoomIn.setPreferredSize(new Dimension(18, 18));
		this.bNavZoomOut.setPreferredSize(new Dimension(18, 18));
		
		this.bNavL.addActionListener(this);
		this.bNavR.addActionListener(this);
		this.bNavU.addActionListener(this);
		this.bNavD.addActionListener(this);
		this.bNavZoomIn.addActionListener(this);
		this.bNavZoomOut.addActionListener(this);
		this.bChangeBackground.addActionListener(this);
		
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
		
		this.bColorChooser = new Button();
		this.bColorChooser.setMargin(new Insets(0,0,0,0));  
		this.bColorChooser.setPreferredSize(new Dimension(26, 26));
		this.bColorChooser.setImageIcon("colorPicker.jpg","Color Picker");
		
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
		navPanel.add(bNavL);
		navPanel.add(bNavZoomIn);
		navPanel.add(bNavR);
		navPanel.add(bNavD);
		navPanel.add(bNavZoomOut);
		navPanel.add(bNavU);
	
		drwpNorth.add(this.bDefaultView);
		drwpNorth.add(this.bDeform);
		drwpNorth.add(this.bLoadMesh);
		drwpNorth.add(this.bClear);
		drwpNorth.add(this.bRefresh);
		drwpNorth.add(this.bChangeBackground);
		drwpNorth.add(this.bInfo);
		drwpNorth.add(this.bFullScreen);
		drwpNorth.add(this.bShowAxes);
		drwpNorth.add(this.bShowMesh);
		drwpNorth.add(this.bColorChooser);
		drwpNorth.add(navPanel);

		
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
			
			drwpWest.addTab("Regions",regButtonPanel);
			
			JScrollPane westScrollPane = new JScrollPane(drwpWest,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Constructing Constructing the drawing panel West  ===================== end



		// Constructing Constructing the drawing panel East  ===================== begin

		eastPanel= new JPanel(new FlowLayout(0, 1, 10));
			//eastPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			eastPanel.setPreferredSize(new Dimension(100, 1000));
			eastPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.bApplyVscale = new Button("Apply");
			this.bApplyVscale.addActionListener(this);
			
			eastPanel.add(this.tfVectorScale);
			eastPanel.add(this.bApplyVscale);
			


	
		
		// Constructing Constructing the drawing panel East  ===================== end

			
			
			
			
		
		// Constructing SimpleUniverse ===================== begin
		
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		this.canvas = new Canvas3D(config);

		centerPanel = new JPanel(new GridLayout(1, 1));
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		centerPanel.add(this.canvas);
	

		this.universe = new SimpleUniverse(this.canvas);
	
		this.background = new Background();
	//	background.setColor(new Color3f(Color.white));
		TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky.jpg"), this );
		ImageComponent2D image = loader.getScaledImage((int)(1.8*this.width), (int)(1.5*this.height));
		this.background.setImage(image);
		
		BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 1000);
		this.background.setApplicationBounds(sphere);
		this.group.addChild(this.background);
	
			
			
			
			
		
		// Constructing SimpleUniverse ===================== end
		
					
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
		




	/*	JScrollPane westScrollPane = new JScrollPane(this.blockButtonPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);*/

		setLayout(new BorderLayout());
		add("North", drwpNorth);
		add("South", southScrollPane);
		add("East", eastPanel);
		add("West", westScrollPane);
		add("Center", centerPanel);

		this.bDefaultView.addActionListener(this);
		this.bClear.addActionListener(this);
		//this.bDeform.addActionListener(this);
		this.bInfo.addActionListener(this);
		this.bRefresh.addActionListener(this);
		this.bShowMesh.addActionListener(this);
		this.bShowAxes.addActionListener(this);
		this.bColorChooser.addActionListener(this);
		this.bFullScreen.addActionListener(this);

		camEye=camEye0.deepCopy();
		target=target0.deepCopy();
		upVect=upVect0.deepCopy();
		newViewer();
		getScene();
		this.universe.addBranchGraph(this.group);
		
				

	}

	public void getScene() {

		addLights();
		this.group.setCapability(BranchGroup.ALLOW_DETACH);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				1000);

		mouseRotate = new MouseRotate();

		MouseTranslate mouseTranslate = new MouseTranslate();
		mouseTranslate.setFactor(.001,.001);

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
		this.cartesian = new Cartesian(this.spaceBoundary, Color.red, Color.green.darker(),
				Color.blue);
		
		this.refGroup.addChild(this.cartesian);


		this.group.addChild(this.refGroup);





	}


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

		/*double Px = .2;
		double Py =.5;
		double Pz =2;


		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new Point3d(Px, Py, Pz), new Point3d(0, 0, 0),
				new Vector3d(-Px, -Py, 1));
		lookAt.invert();
		universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);*/

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

	public void fullScreen() {
		if(nfs%2==0){
			centerPanel.remove(canvas);
			fsFrame.add(canvas);
			fsFrame.setVisible(true);
		}
		else{		
			fsFrame.remove(canvas);	
			fsFrame.setVisible(false);
			centerPanel.add(canvas);

		}
		nfs++;
		repaint();

	}

	public void addLights() {
		Color3f light1Color = new Color3f(.3f, .3f, .3f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		Vector3f light1Direction = new Vector3f(1f, -1f, .5f);
		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		group.addChild(light1);
		
		Color3f light3Color = new Color3f(.7f, .7f, .7f);
			Vector3f light3Direction = new Vector3f(-1f, -1f, .3f);
		DirectionalLight light3 = new DirectionalLight(light3Color,
				light3Direction);
		light3.setInfluencingBounds(bounds);
		group.addChild(light3);

		AmbientLight light4 = new AmbientLight(new Color3f(.4f, .4f, .4f));
		
		light4.setInfluencingBounds(bounds);
		group.addChild(light4);
		}

	public void actionPerformed(ActionEvent e) {

	if (e.getSource() == this.bDefaultView) {

			resetView();

		}

		else if (e.getSource() == this.bFullScreen) {

			fullScreen();

		}


		else if (e.getSource() == this.bInfo) {
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
		
			if(fieldMode==1)
				this.showFlux(true);

			else if(fieldMode>1 && fieldMode<5)
				this.showNodalField(true);
			else
				this.showMesh(true);

		}

		else if (e.getSource() == this.bChangeBackground)
			changeBackground();
		
		else if (e.getSource() == this.bShowAxes){
			axesShown=!axesShown;
			showAxes(axesShown);
		}
		else if (e.getSource() == this.bShowMesh){
			meshShown=!meshShown;
			showMesh(meshShown);
		}
		else if (e.getSource() == this.bColorChooser){
		
			tempColor=cch.showDialog(new JFrame(), "Pick a Color",Color.red);
			this.regionMatColor[this.nChosenRegion]=tempColor;

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
		else if (e.getSource() == this.bNavZoomOut)
			zoomOut();
		else{

				for( int i=1;i<=this.numberOfRegions;i++){

					if(e.getSource() == this.regButton[i]) {

						try {

							this.nChosenRegion =i;
							showRegion[this.nChosenRegion]=!showRegion[this.nChosenRegion];
							if(showRegion[this.nChosenRegion])
								regButton[this.nChosenRegion].setBackground(this.regionMatColor[this.nChosenRegion]);
							else
								regButton[this.nChosenRegion].setBackground(Color.white);
							

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
		if(showRegion[ir])
			this.regButton[ir].setBackground(this.matData.matColor(name));
		this.regButton[ir].addActionListener(this);
		this.regButton[ir].setPreferredSize(new Dimension(80, 25));
		this.regButton[ir].setMargin(new Insets(1,1,1,1));  
		this.regButtonPanel.add(this.regButton[ir]);
		this.regButtonPanel.updateUI();
		this.tfNumbRegs.setText(Integer.toString(ir));

	}


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
			// cch=new JColorChooser();
			Color color=cch.showDialog(new JFrame(), "Pick a Color",Color.blue);
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



	}

	public void moveLeft(){
		camEye.el[0]+=moveStep;
		target.el[0]+=moveStep;
		newViewer();
	}
	public void moveRight(){
		camEye.el[0]-=moveStep;
		target.el[0]-=moveStep;
		newViewer();
	}
	public void moveUp(){
		camEye.el[1]-=moveStep;
		target.el[1]-=moveStep;
		newViewer();
	}
	public void moveDown(){

		camEye.el[1]+=moveStep;
		target.el[1]+=moveStep;
		newViewer();

	}
	public void zoomIn(){
		
		camEye.el[2]-=moveStep;
		newViewer();

	}
	public void zoomOut(){
		camEye.el[2]+=moveStep;
		newViewer();

	}



	public void setMesh(Model model) {
		util.pr(model.dim);
		if(model.dim==3)
			model.setEdge();
		
		tfc[0]=new TextField("");
		tfc[1]=new TextField("");
		
		this.dim=model.dim;
		model.defScale=Double.parseDouble(this.tfVectorScale.getText());
		clearRegButtons();
		this.meshLoaded = true;
		this.numberOfRegions = model.numberOfRegions;
		regButton=new Button[this.numberOfRegions+1];
		showRegion=new boolean[this.numberOfRegions+1];
		this.regionMatColor=new Color[this.numberOfRegions+1];
		
		for (int ir = 1; ir <= this.numberOfRegions; ir++){

			this.regionMatColor[ir] = this.matData.matColor(model.region[ir].getMaterial());

		//	if(/*!model.motor ||*/ ir==11)
			showRegion[ir]=true;

			addRegButton(ir,model.region[ir].getMaterial());

		}

		this.surfFacets = new SurfFacets[this.numberOfRegions + 1];

		this.group.detach();
		this.refGroup.removeChild(this.vFieldGroup);
		this.vFieldGroup.removeAllChildren();

		for (int ir = 1; ir <= this.numberOfRegions; ir++) {

			this.vFieldGroup.removeChild(this.surfFacets[ir]);
			this.surfFacets[ir] = new SurfFacets(model, ir,this.matData.matColor(model.region[ir].getMaterial()),this.spaceTransparency);
			this.vFieldGroup.addChild(this.surfFacets[ir]);
		}

		if (this.vFieldGroup.getParent() == null)
			this.refGroup.addChild(this.vFieldGroup);
		this.universe.addBranchGraph(this.group);


		for (int ir = 1; ir <= this.numberOfRegions; ir++)
			if(model.region[ir].getMaterial().startsWith("air")){
				showRegion[ir]=false;
				regButton[ir].setBackground(Color.white);
			}


		if(model.dim==2) {
			
			addCartesian2D(model);

			if(model.spaceBoundary==null) camEye0.el[2]=1;
			else 
				camEye0.el[2]=2*model.spaceBoundary[1];

			camEye0.el[0]=0;
			camEye0.el[1]=0;
			target0=new Vect(0,0,0);
			upVect0.el[0]=0;  upVect0.el[1]=1;upVect0.el[2]=0;
			camEye=camEye0.deepCopy();
			target=target0.deepCopy();
			upVect=upVect0.deepCopy();
			mouseRotate.setFactor(0);

			newViewer();

		}

		else
		{
			if(this.cartesian2D!=null && this.cartesian2D.getParent()!=null){
			this.group.detach();
			this.refGroup.removeChild(this.cartesian2D);
			this.refGroup.addChild(this.cartesian);
			this.universe.addBranchGraph(this.group);
			}
			
			zoom=-(int)(1/model.spaceBoundary[5]);
			newViewer();}

		moveStep0=model.maxDim*.005;	
		moveStep=moveStep0;
		
		
		showMesh(true);

	}
	

	public void addCartesian2D(Model model){
		double[] spaceBoundary2D={model.spaceBoundary[0],model.spaceBoundary[1],
				model.spaceBoundary[2],model.spaceBoundary[3]};

		this.group.detach();
		this.refGroup.removeChild(this.cartesian);
		this.refGroup.removeChild(this.cartesian2D);
		this.cartesian2D = new Cartesian2D(spaceBoundary2D, Color.red, Color.green.darker());
		this.refGroup.addChild(this.cartesian2D);
		this.universe.addBranchGraph(this.group);
	}
	
	public void paintDisp(Model model) {


		this.group.detach();

		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			if (model.region[ir].getMaterial().startsWith("air")) continue;
			this.vFieldGroup.removeChild(this.surfFacets[ir]);
			this.surfFacets[ir].setEdgeTransparency(.5);
			this.surfFacets[ir].paintDisp(model);


			this.vFieldGroup.addChild(this.surfFacets[ir]);

		}

		this.universe.addBranchGraph(this.group);

	}

	public void paintStress(Model model) {
		
		this.fieldMode=5;

		double pw=1;
		this.Vmax=model.nodalStressMax;
		
		this.group.detach();

		for (int ir = 1; ir <=this.numberOfRegions; ir++) {

			this.vFieldGroup.removeChild(this.surfFacets[ir]);
			this.surfFacets[ir].setEdgeTransparency(.5);
			this.surfFacets[ir].paintStress(model, pw);	

			this.vFieldGroup.addChild(this.surfFacets[ir]);

		}

		this.universe.addBranchGraph(this.group);

		if(pw==0)
			setColorBarLog("Stress (Pa)",0,this.Vmax);
		else
			setColorBar("Stress (Pa)",model.nodalStressMin,model.nodalStressMax,pw);


	}
	public void showFlux(boolean b) {


		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++){

			if(showRegion[ir]){
				this.surfFacets[ir].showFacets(false);
				
				this.vField[ir].showFlux(true);
				if (this.vField[ir].getParent() == null)
					this.vFieldGroup.addChild(this.vField[ir]);

				if (dim==2)
					this.surfFacets[ir].setEdgeColor(Color.gray,.5);	
					else
					this.surfFacets[ir].setEdgeColor(Color.gray,.7);	
				this.surfFacets[ir].showEdges(true);
			}
			
			this.vField[ir].showFlux(showRegion[ir]);
		}

		DecimalFormat formatter=new DecimalFormat("0.00E00");
		this.tfVectorScale.setText(formatter.format(this.vField[1].getVectorScale()));

		this.universe.addBranchGraph(this.group);
		
		cBatTitle="Flux Density (T)";
		
		setColorBar(cBatTitle,this.Vmin, this.Vmax,1);
		

	}

	public void setColorBarLog(String title,double min, double max){
		{
			
			if(colBar!=null)
				eastPanel.remove(colBar);

			colBar=this.cBar.getColorBarPnLog(min,max,title,15);
			eastPanel.updateUI();

		}
	}

	public void setColorBar(String title,double min, double max,double pw){
		{
			cBar.setEnds(min, max);

			if(colBar!=null)
				eastPanel.remove(colBar);

			colBar=this.cBar.getColorBarPn(min,max,pw,title,15);

			eastPanel.add(colBar);
			eastPanel.updateUI();

		}
	}

	public void showElJ(boolean b) {

		this.bShowMesh.doClick();
		this.group.detach();

		for (int ir = 1; ir < this.numberOfRegions; ir++) {
			this.vFieldGroup.removeChild(this.vField[ir]);
			this.vField[ir].showJ(b);
			this.vFieldGroup.addChild(this.vField[ir]);
		}
		if (this.vFieldGroup.getParent() == null)
			this.refGroup.addChild(this.vFieldGroup);

		this.universe.addBranchGraph(this.group);

	}

	public void clearStuff() {
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

	}
	
	
	public void clearRegButtons() {
		for (int i = 1; i <= this.numberOfRegions; i++)
			this.regButtonPanel.remove(this.regButton[i]);
		this.tfNumbRegs.setText(Integer.toString(0));
		this.regButtonPanel.updateUI();
		this.numberOfRegions = 0;
		if(colBar!=null)
			regButtonPanel.remove(colBar);
			//	southPanelMode(-1);

	}
	
	
	public void setFlux(Model model){
		setFlux(model,false);

	}

	public void setFlux(Model model,boolean isH) {
		this.Vmax=model.Bmax;
		this.Vmin=model.Bmin;
		
		cBar.setEnds(Vmin, Vmax);
		int n;

		if (this.meshLoaded)
			n = 0;
		else
			n = this.distrib.getSelectedIndex();
	
		for (int ir = 1; ir <=model.numberOfRegions; ir++) 
			this.vField[ir].setFlux(model,cBar, n,isH);
		
		fieldMode=1;
		
		showFlux(true);

	}


	public void rescale(double a) {

	

		if(a<=0){
			String msg = "Scale factor must be positive.";

			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE); 
			return;
			
		}
		double e1=Double.parseDouble(this.cBar.tfc[0].getText());
		double e2=Double.parseDouble(this.cBar.tfc[1].getText());
		if(e1>0 || e2>=0){
			Vmin=e1;
			Vmax=e2;
			setColorBar(cBatTitle,this.Vmin, this.Vmax,1);
		}
			
			this.group.detach();

			for (int ir = 1; ir <=this.numberOfRegions; ir++){
		
				if(!showRegion[ir]) continue;
				if(fieldMode==1 ){
					if(this.vField!=null && ir<this.vField.length && this.vField[ir]!=null && this.vField[ir].getParent()!=null)
						this.vField[ir].rescale(cBar,a);
				}
					else if(fieldMode>=2 && fieldMode<=4)
						this.nodalField[ir].rescale(cBar,a);
			}
			this.universe.addBranchGraph(this.group);
		
	}

/*

	public void setCurrent(Model model) {
		setVectField(model);
		for (int ir = 1; ir <= model.numberOfRegions; ir++)
			if (model.region[ir].hasJ) {
				this.vField[ir].setJ0(model);
			}
	}*/

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


	public void setNodalField(Model model,int mode) {
		if(mode==-1)
			this.Vmax=model.uMax;
		if(mode==1){
			this.Vmin=model.FreluctMin;
			this.Vmax=model.FreluctMax;
		}
		else
			if(mode==2){
				this.Vmin=model.FmsMin;
				this.Vmax=model.FmsMax;
			}

		if(mode==-1) fieldMode=2;
		else
			fieldMode=mode+2;
		this.numberOfRegions = model.numberOfRegions;
		this.nodalField = new NodalField[this.numberOfRegions + 1];

	
		cBar.setEnds(Vmin, Vmax);
		
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			if(model.dim==3)
				//nodalField[ir] = new NodalField(model,surfFacets[ir].surfaceQuadNodes,mode,0);
				nodalField[ir] = new NodalField(model,cBar,ir,mode,1);
			else
				this.nodalField[ir] = new NodalField(model,cBar,ir,mode,1);
			
		}

		showNodalField(true);

	}


	public void showNodalField(boolean b) {
		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			{

				if(showRegion[ir] && this.nodalField[ir].getParent()==null)
				this.vFieldGroup.addChild(this.nodalField[ir]);
				else if(!showRegion[ir])
					this.vFieldGroup.removeChild(this.nodalField[ir]);
				
				this.surfFacets[ir].showFacets(!b && showRegion[ir]);
				this.nodalField[ir].showField(b && showRegion[ir]);
				if (dim==2)
				this.surfFacets[ir].setEdgeColor(Color.gray,.6);	
				else
				this.surfFacets[ir].setEdgeColor(Color.gray,.6);	
			}
		}

		this.universe.addBranchGraph(this.group);

		DecimalFormat formatter=new DecimalFormat("0.00E00");
		this.tfVectorScale.setText(formatter.format(this.nodalField[1].getVectorScale()));

		if(fieldMode==2){
			cBatTitle="Displacem. (m)";
			
		}
		else if(fieldMode==3){
			cBatTitle="Reluct. Force (N)";
		}
		else if(fieldMode==4){
			cBatTitle="MS Force (N)";
		}


		if(b) setColorBar(cBatTitle,0, this.Vmax,1);
	}


	public void showMesh(boolean b) {

		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
			{	
				if(this.surfFacets[ir]==null) continue;
				this.surfFacets[ir].setFaceColor(regionMatColor[ir]);
				this.surfFacets[ir].showFacets(b &&showRegion[ir]);
				this.surfFacets[ir].showEdges(b&& showRegion[ir]);
			}
		}

		this.universe.addBranchGraph(this.group);
	}


	public void showAxes(boolean b) {
		this.group.detach();
		this.group.removeChild(this.refGroup);
		if (b){
			if(dim==3)
			this.refGroup.addChild(this.cartesian);		
			else
			this.refGroup.addChild(this.cartesian2D);		
		}
		else{
			if(dim==3)
			this.refGroup.removeChild(this.cartesian);
			else
			this.refGroup.removeChild(this.cartesian2D);
		}
		
		this.group.addChild(this.refGroup);
		this.universe.addBranchGraph(this.group);
	}



	public void loadMode() {

		this.messageArea.setText("");
		Console.redirectOutput(this.messageArea);
		/*	this.messageFrame.setVisible(true);
		this.messageFrame.pack();*/
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setBounds(1060, 820, 100, 20);
		this.progressBar.setForeground(Color.green);
		add(this.progressBar);
		this.progressBar.setVisible(false);

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

	}



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