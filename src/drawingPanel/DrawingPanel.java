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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import static java.lang.Math.*;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.image.TextureLoader;

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


public class DrawingPanel extends JPanel implements ActionListener,ItemListener {
	private ButtonIcon bAddBlock, bRemoveBlock,bShowAxes;
	public Button bLoadModel, bClear, bSpace, bNavL, bNavR, 
	bNavU ,bNavD,bNavZoomIn,bNavZoomOut;
	public Button bInfo;
	private Button bApplySize;
	public ButtonIcon bDiscretize, bDefaultView,bFullScreen, bChangeBackground;
	private JCheckBox chCoupled;
	private JComboBox units, analysisMethod,defAnalysisMethod;
	private TextField tfRefMesh, tfRefBase;
	private JPanel blockButtonPanel, southPanel, centerPanel;
	private BlockSpec blockSpec = new BlockSpec();
	private EditBlock editBlock = new EditBlock();
	private SpaceSpec spaceSpec = new SpaceSpec();
	private JFrame messageFrame,fsFrame;
	private TextField tfNumbBlocks;
	private Button[] blockButton;
	public int nBlocks = 0, zoom = 0;
	private HexaHedron[] block;
	private BranchGroup group = new BranchGroup();
	private TransformGroup refGroup, blockGroup, vFieldGroup;
	private Cartesian cartesian;
	private SimpleUniverse universe;
	public Color[] blockMatColor;
	private double spaceTransparency = .85, blockTransparency = 0;
	public MaterialData matData = new MaterialData();
	public Canvas3D canvas;
	public int nChosenBlock = -1, nPrevChosenBlock = -1,
	nBlocksMax, nBoundary = 6;
	public int decimal = 3;
	public String[] blockName,blockMaterial;
	public double[][] blockBoundary;
	public double[][] minMeshLeft;
	public double[][] minMeshRight;
	public double[][] baseLeft;
	public double[][] baseRight;
	public boolean[][] discLeft, discRight;
	public boolean[]  hasCurrent, hasMag,linearBH;
	public byte[] BCtype = new byte[6];
	public Vect[] mur,sigma,J, M, diricB;
	double refMeshSize = 200.0, refBase = 2, refBoundary = 1000.0;
	public double  moveStep0,moveStep,scaleFactor;
	private Vect camEye,camEye0=new Vect(.6,-2.6,1.6), target,target0=new Vect(3),upVect,upVect0=new Vect(0,0,1);
	public boolean coupled, discretized, deform,hasCoil,axesShown;
	public String bunFilePath, dataFilePath;
	ColorBar cBar;
	private JScrollPane messageScrollPane;
	public JTextArea messageArea;
	public int analysisMode,fieldMode,defMode,dim;
	public JProgressBar progressBar;
	private Thread progressThread;
	private Background background;
	ImageComponent2D image;
	private int  width,height,backgroundMode=0,unitIndex=1,nfs;
	private DecimalFormat formatter=new DecimalFormat("0.0E0");
	private MouseRotate mouseRotate;
	private Loader loader=new Loader();


	public DrawingPanel() {
		
		this.width=1000;
		this.height=900;
		setSize(width,height);
		
		this.nBlocksMax=500;
		// Constructing the drawing panel north ===================== begin
		
		JPanel drwpNorth = new JPanel(new FlowLayout(0, 10, 10));
		
		this.bAddBlock = new ButtonIcon();
		this.bAddBlock.setPreferredSize(new Dimension(30, 30));
		this.bAddBlock.setImageIcon("addBlock2.jpg","Add block");

		this.bRemoveBlock = new ButtonIcon();
		this.bRemoveBlock.setPreferredSize(new Dimension(30, 30));
		this.bRemoveBlock.setImageIcon("removeBlock.jpg","Remove block");


		this.bDiscretize = new ButtonIcon();
		this.bDiscretize.setPreferredSize(new Dimension(30, 30));
		this.bDiscretize.setImageIcon("discretize.jpg","Discretize");

		this.bDiscretize.setEnabled(false);

		this.bDefaultView = new ButtonIcon();
		this.bDefaultView.setPreferredSize(new Dimension(30, 30));
		bDefaultView.setImageIcon("defView.jpg","Default View");
		
		this.bClear = new Button();
		this.bClear.setPreferredSize(new Dimension(30, 30));
		this.bClear.setImageIcon("clear.jpg", "Add block");
		
		
		this.bChangeBackground = new ButtonIcon();
		this.bChangeBackground.setPreferredSize(new Dimension(40, 30));
		this.bChangeBackground.setImageIcon("ChangeBackground2.jpg","Change Background Color");


		this.bLoadModel = new Button("");
		this.bLoadModel.setPreferredSize(new Dimension(30, 30));
		this.bLoadModel.setImageIcon("loadModel.jpg","Load Model");

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
		this.blockSpec.bApply.addActionListener(this);
		this.blockSpec.bEdit.addActionListener(this);

		this.bFullScreen = new ButtonIcon();
		this.bFullScreen.setPreferredSize(new Dimension(30, 30));
		this.bFullScreen.setImageIcon("maxmin.jpg","Full Screen");
		
		
		this.bShowAxes = new ButtonIcon();
		this.bShowAxes.setPreferredSize(new Dimension(30, 30));
		this.bShowAxes.setImageIcon("axes.jpg","Show/Hide axes");
		this.bShowAxes.addActionListener(this);

		
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
		
		
		
		drwpNorth.add(this.bAddBlock);
		drwpNorth.add(this.bRemoveBlock);
		drwpNorth.add(this.bDiscretize);
		drwpNorth.add(this.bDefaultView);
		drwpNorth.add(this.bLoadModel);
		drwpNorth.add(this.bClear);
		drwpNorth.add(this.bChangeBackground);
		drwpNorth.add(this.bInfo);
		drwpNorth.add(this.bFullScreen);
		drwpNorth.add(this.bShowAxes);
		drwpNorth.add(navPanel);

		//==========================
				
		// Constructing Constructing the drawing panel north  ===================== end
		
		
		
		// Constructing Constructing the drawing panel West  ===================== begin
		
		 JTabbedPane drwpWest = new JTabbedPane();
		 drwpWest.setFont(new Font("Arial", 1, 12));

		 
			this.tfNumbBlocks = new TextField("0");

			this.bSpace = new Button("Space");
			
			this.tfNumbBlocks.setPreferredSize(new Dimension(60, 25));
			this.tfNumbBlocks.setFont(new Font("Times New Roman", 1, 12));
			this.tfNumbBlocks.setEditable(false);


			this.blockButtonPanel = new JPanel(new FlowLayout(0, 1, 10));
			this.blockButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.blockButtonPanel.setPreferredSize(new Dimension(130, 3000));
			Label lbNumbBlocks = new Label("Number of Blocks");
			lbNumbBlocks.setFont(new Font("Times New Roman", 1, 12));

			this.blockButtonPanel.add(this.bSpace);
			this.blockButtonPanel.add(lbNumbBlocks);
			this.blockButtonPanel.add(this.tfNumbBlocks);
		
			
			drwpWest.addTab("Blocks",blockButtonPanel);
			
		
			JScrollPane westScrollPane = new JScrollPane(drwpWest,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			
		// Constructing Constructing the drawing panel West  ===================== end



		// Constructing Constructing the drawing panel East  ===================== begin

			JPanel eastPanel = new JPanel(new FlowLayout(0, 1, 10));
			eastPanel.setPreferredSize(new Dimension(150, 1000));
			eastPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			this.tfRefMesh = new TextField();
			this.tfRefMesh.setText(String.valueOf(this.refMeshSize) + " ");
			this.tfRefMesh.setPreferredSize(new Dimension(50, 25));
			this.tfRefMesh.setFont(new Font("Arial", 0, 13));
			this.tfRefMesh.setOpaque(true);

			this.tfRefBase = new TextField();
			this.tfRefBase.setText(String.valueOf(this.refBase) + " ");
			this.tfRefBase.setPreferredSize(new Dimension(50, 25));
			this.tfRefBase.setFont(new Font("Arial", 0, 13));
			this.tfRefBase.setOpaque(true);
		
			String[] unitOption = {"Meter", "Milli",  "micro" };
			this.units = new JComboBox(unitOption);{
				this.units.setSelectedIndex(1);
				scaleFactor=1000;
			}
			
			String[] analysisOption = {"Magn.Stat", "Eddy-A","Eddy-A-phi" };
			this.analysisMethod = new JComboBox(analysisOption);
			this.analysisMethod.setFont(new Font("Arial", 1, 11));
			this.analysisMethod.setSelectedIndex(0);
			this.analysisMode = this.analysisMethod.getSelectedIndex();

			String[] defAnalysisOption = {"None","Mag. Force", "MS Force","Both" };
			this.defAnalysisMethod = new JComboBox(defAnalysisOption);
			this.defAnalysisMethod.setFont(new Font("Arial", 1, 11));
			this.defAnalysisMethod.setSelectedIndex(0);
			this.defMode = this.defAnalysisMethod.getSelectedIndex();
			


			this.chCoupled = new JCheckBox("Copuled");
			this.chCoupled.setSelected(false);
			this.deform = this.defAnalysisMethod.getSelectedIndex()>0;
			this.coupled = this.defAnalysisMethod.getSelectedIndex()>3;
			this.bApplySize = new Button("Apply");
			this.bApplySize.setPreferredSize(new Dimension(30, 25));
			this.bApplySize.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.bApplySize.setBorder(BorderFactory.createLineBorder(Color.gray));
			
			JPanel meshSizePanel=new JPanel(new GridLayout(3,2,5,5));
			meshSizePanel.add(new Label("Mesh"));
			meshSizePanel.add(this.tfRefMesh);
			meshSizePanel.add(new Label("Growth"));
			meshSizePanel.add(this.tfRefBase);
			meshSizePanel.add(new Label());
			meshSizePanel.add(this.bApplySize);
			
			eastPanel.add(new Label("Units "));
			eastPanel.add(this.units);
			eastPanel.add(new Label("Analysis Method"));
			eastPanel.add(this.analysisMethod);
			eastPanel.add(new Label("Deform. Method"));
			eastPanel.add(defAnalysisMethod);
			eastPanel.add(chCoupled);
			eastPanel.add(meshSizePanel);

			this.analysisMethod.addItemListener(this);
			this.defAnalysisMethod.addItemListener(this);
			this.units.addItemListener(this);

			
	
		
		// Constructing Constructing the drawing panel East  ===================== end

			
			
			
			
		
		// Constructing SimpleUniverse ===================== begin
		
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		this.canvas = new Canvas3D(config);

		centerPanel = new JPanel(new GridLayout(1, 1));
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		centerPanel.add(this.canvas);

		this.universe = new SimpleUniverse(this.canvas);
		
		this.background = new Background();
		TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky2.jpg"), this );
		this.image = loader.getScaledImage((int)(1.8*this.width), (int)(1.1*this.height));
		this.background.setImage(this.image);
		BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 1000);
		this.background.setApplicationBounds(sphere);
		this.group.addChild(this.background);
		
		// Constructing SimpleUniverse ===================== end
	
		
		
		this.blockSpec.setBorder(BorderFactory.createLineBorder(Color.black));
		this.southPanel = new JPanel(new FlowLayout(0, 1, 10));
		this.southPanel.setPreferredSize(new Dimension(1850, 130));

		JScrollPane southScrollPane = new JScrollPane(this.southPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		////MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM

					
		this.bunFilePath = System.getProperty("user.dir") + "//bun.txt";
		this.dataFilePath = System.getProperty("user.dir") + "//data.txt";

		this.nBlocksMax = 200;
		initialize();

	
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setBounds(1060, 820, 100, 20);
		this.progressBar.setForeground(Color.green);
		add(this.progressBar);
		this.progressBar.setVisible(false);


		setLayout(new BorderLayout());
		add("North", drwpNorth);
		add("South", southScrollPane);
		add("East", eastPanel);
		add("West", westScrollPane);
		add("Center", centerPanel);

		this.bAddBlock.addActionListener(this);
		this.bRemoveBlock.addActionListener(this);
		this.bDefaultView.addActionListener(this);
		this.bInfo.addActionListener(this);
		this.bFullScreen.addActionListener(this);
		this.editBlock.bApply.addActionListener(this);
		

		this.bSpace.addActionListener(this);
		this.bApplySize.addActionListener(this);
		

		camEye=camEye0.deepCopy();
		target=target0.deepCopy();
		upVect=upVect0.deepCopy();
		newViewer();
		getScene();
		this.universe.addBranchGraph(this.group);

			repaint();
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
				DrawingPanel.this.zoom -= steps;
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
		addSpace();
		this.cartesian = new Cartesian(this.blockBoundary[0], Color.red, Color.green,
				Color.blue);
		//this.refGroup.addChild(this.cartesian);


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

	public void initialize() {
		this.block = new HexaHedron[this.nBlocksMax];
		this.blockButton = new Button[this.nBlocksMax];
		this.blockMaterial = new String[this.nBlocksMax];
		this.blockName = new String[this.nBlocksMax];
		this.blockBoundary = new double[this.nBlocksMax][6];
		this.minMeshLeft = new double[this.nBlocksMax][6];
		this.minMeshRight = new double[this.nBlocksMax][6];
		this.baseLeft = new double[this.nBlocksMax][6];
		this.baseRight = new double[this.nBlocksMax][6];
		this.discLeft = new boolean[this.nBlocksMax][6];
		this.discRight = new boolean[this.nBlocksMax][6];
		this.linearBH=new boolean[this.nBlocksMax];		
		for (int i = 0; i < this.nBlocksMax; i++)
			this.linearBH[i]=true;

		for (int i = 0; i < this.nBlocksMax; i++)
			for (int j = 0; j < this.nBoundary; j++) {
				this.discLeft[i][j] = true;
				this.discRight[i][j] = true;
			}
		this.hasCurrent = new boolean[this.nBlocksMax];
		this.hasMag = new boolean[this.nBlocksMax];
		this.mur = new Vect[this.nBlocksMax];
		this.sigma = new Vect[this.nBlocksMax];
		this.blockMatColor = new Color[this.nBlocksMax];
		this.M = new Vect[this.nBlocksMax + 1];
		this.J = new Vect[this.nBlocksMax + 1];
		this.diricB = new Vect[6];

		for (int j = 0; j < 6; j++)
			this.diricB[j] = new Vect(3);
	}

	public void addLights() {

		Color3f light1Color = new Color3f(.6f, .6f, .6f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		Vector3f light1Direction = new Vector3f(1f, -1f, .5f);
		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		group.addChild(light1);
		
		Color3f light3Color = new Color3f(.5f, .5f, .5f);
			Vector3f light3Direction = new Vector3f(-1f, -1f, .3f);
		DirectionalLight light3 = new DirectionalLight(light3Color,
				light3Direction);
		light3.setInfluencingBounds(bounds);
		group.addChild(light3);

		AmbientLight light4 = new AmbientLight(new Color3f(.3f, .3f, .3f));
		
		light4.setInfluencingBounds(bounds);
		group.addChild(light4);

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.bAddBlock) {

			addBlock();
		}
		else if (e.getSource() == this.bRemoveBlock) {
			
			if (this.nBlocks > 0) {
				if (this.nChosenBlock == -1)
					removeBlock(this.nBlocks);
				else if (this.nChosenBlock > 0)
					removeBlock(this.nChosenBlock);

			}
		}

		else if (e.getSource() == this.bDefaultView) {

			resetView();

		}

		else if (e.getSource() == this.bFullScreen) {

			fullScreen();

		}


		else if (e.getSource() == this.bSpace) {
			if (this.nChosenBlock > 0)
				releaseBlock(this.nChosenBlock);
			this.nPrevChosenBlock = this.nChosenBlock;
			this.nChosenBlock = 0;
			selectSpace();

		} else if (e.getSource() == this.bApplySize) {
			applySize();
			applyBase();
		} else if (e.getSource() == this.spaceSpec.bApply) {
			saveSpaceSpec();
			fillSpaceSpec();
			updateSpace();
			//southPanelMode(-1);
		}

		else if (e.getSource() == this.blockSpec.bApply) {
			saveBlockSpec(this.nChosenBlock);
			fillBlockSpec(this.nChosenBlock);
			updateBlock(this.nChosenBlock);

		}

		else if (e.getSource() == this.bInfo) {
			this.messageFrame.setVisible(true);
			this.messageFrame.pack();

		}

		else if (e.getSource() == this.blockSpec.bEdit) {

			this.editBlock.setLocation(getX() + 20, getY() + 500);
			this.editBlock.setVisible(true);

		} else if (e.getSource() == this.editBlock.bApply) {
			applyChanges(this.nChosenBlock);

		} 

		else if (e.getSource() == this.bChangeBackground)
			changeBackground();
		
		else if (e.getSource() == this.bShowAxes){
			axesShown=!axesShown;
			showAxes(axesShown);
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

			int blk=-1;
			for( int i=0;i<=this.nBlocks;i++){

				if(e.getSource() == this.blockButton[i]) {

					try {
						this.nPrevChosenBlock = this.nChosenBlock;
						this.nChosenBlock = i/*Integer.parseInt(((Button) e.getSource())*/;
						
						if (this.nPrevChosenBlock == 0)
							releaseSpace();
						else if (this.nPrevChosenBlock > 0)
							releaseBlock(this.nPrevChosenBlock);

						selectBlock(this.nChosenBlock);
						southPanelMode(1);
					}

					catch (NumberFormatException nfe) {
					}

					blk=i;

					break;

				}
			}

		}
	}

		
	public void itemStateChanged(ItemEvent e) {

		if (e.getSource() == this.units) {
			int newUnitIndex=this.units.getSelectedIndex();
			updateUnits(newUnitIndex);
			unitIndex=newUnitIndex;

		}

		else if (e.getSource() == this.analysisMethod) {
			this.analysisMode = this.analysisMethod.getSelectedIndex();

		}
		
		else if (e.getSource() == this.defAnalysisMethod) {
			
			this.defMode = this.defAnalysisMethod.getSelectedIndex();

			if(defMode>0)
				this.deform = true;
			else
				this.deform = false;
		}

		else if (e.getSource() == this.chCoupled) {
			if (this.chCoupled.isSelected())
				this.coupled=true;
			else
				this.coupled=false;

		} 


	}

	public void addSpace() {
		for (int j = 0; j < this.nBoundary; j++) {
			if (j % 2 == 0)
				this.blockBoundary[0][j] = -this.refBoundary;
			else
				this.blockBoundary[0][j] = this.refBoundary;

		}

		/*this.blockBoundary[0][4] = -this.refBoundary/5;
		this.blockBoundary[0][5] = this.refBoundary/5;*/
		this.blockName[0] = this.spaceSpec.tfName.getText();
		this.blockMaterial[0] = this.spaceSpec.tfMaterial.getText();
		this.mur[0]=new Vect(3);
		this.sigma[0]=new Vect(3);
		for(int k=0;k<3;k++){
			this.mur[0].el[k] = Double.parseDouble(this.spaceSpec.tfMu[k].getText());
			this.sigma[0].el[k] = Double.parseDouble(this.spaceSpec.tfSigma[k].getText());
		}
		this.blockMatColor[0] = this.matData.matColor(this.blockMaterial[0]);
		this.J[0] = new Vect(0, 0, 0);

		this.block[0] = new HexaHedron(this.blockBoundary[0], this.scaleFactor);
		this.block[0].setEdgeColor(this.blockMatColor[0]);
		this.block[0].setFaceColor(this.blockMatColor[0]);
		this.block[0].setEdgeWidth(1);
		this.block[0].setTransp(this.spaceTransparency);
		this.block[0].setEdgeTransp(this.spaceTransparency);
		this.block[0].showEdges(true);

		this.refGroup.addChild(this.block[0]);

		this.nChosenBlock = 0;
		this.nPrevChosenBlock = 0;
		southPanelMode(0);

		this.spaceSpec.bApply.addActionListener(this);
		this.spaceSpec.bClose.addActionListener(this);




	}

	public void addBlock() {
		addBlock(false, 0, 0);
	}

	public void addBlock(boolean duplicate, int nb, int dir) {

		if (this.nBlocks < this.nBlocksMax - 1) {
			this.nBlocks++;

			addBlockButton();
			addData(duplicate, nb, dir);
			addHexa();
			this.nPrevChosenBlock = this.nChosenBlock;
			this.nChosenBlock = this.nBlocks;
			selectBlock(this.nChosenBlock);
			this.blockSpec.tfMaterial.setText(this.blockMaterial[this.nBlocks]);
			this.blockSpec.tfName.setText(this.blockName[this.nBlocks]);

		} else {
			String msg = "Number Of Blocks exceeds"
				+ Integer.toString(this.nBlocksMax);
			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void addBlockButton() {
		this.blockButton[this.nBlocks] = new Button("Block " + Integer.toString(this.nBlocks));
		this.blockButton[this.nBlocks].setName(Integer.toString(this.nBlocks));
		this.blockButton[this.nBlocks].addActionListener(this);
		this.blockButton[this.nBlocks].setPreferredSize(new Dimension(85, 25));
		this.blockButtonPanel.add(this.blockButton[this.nBlocks]);
		this.blockButtonPanel.updateUI();
		this.tfNumbBlocks.setText(Integer.toString(this.nBlocks));

	}

	public void addHexa() {
		this.group.detach();
		this.block[this.nBlocks] = new HexaHedron(this.blockBoundary[this.nBlocks], this.scaleFactor);
		this.block[this.nBlocks].setFaceColor(this.blockMatColor[this.nBlocks]);
		this.block[this.nBlocks].setEdgeColor(this.blockMatColor[this.nBlocks].darker().darker());

		if(this.blockMaterial[this.nBlocks].equals("air"))
			this.block[this.nBlocks].setTransp(this.spaceTransparency);
		else
			this.block[this.nBlocks].setTransp(this.blockTransparency);

		this.block[this.nBlocks].showFacets(true);
		this.block[this.nBlocks].showEdges(true);
		this.blockGroup.addChild(this.block[this.nBlocks]);
		this.universe.addBranchGraph(this.group);
	}

	public void removeBlock(int nb) {

		this.group.detach();
		this.blockGroup.removeChild(this.block[nb]);
		this.block[nb] = null;
		this.nPrevChosenBlock = -1;

		this.universe.addBranchGraph(this.group);
		removeBlockButton(nb);
		if (this.nBlocks == 0)
			this.bDiscretize.setEnabled(false);

	}

	public void showBlock(int nb, boolean b) {

		this.group.detach();

		this.block[nb].showFacets(b);

		this.universe.addBranchGraph(this.group);

	}

	public void showAllBlocks(boolean b) {

		for (int i = 1; i <= this.nBlocks; i++)
			showBlock(i, b);

	}

	public void removeBlockButton(int nb) {
		this.blockButtonPanel.remove(this.blockButton[nb]);
		reorderData(nb);
		this.nBlocks--;
		this.nChosenBlock = -1;
		southPanelMode(-1);

	}

	public void removeAllButtons() {
		for (int i = 1; i <= this.nBlocks; i++)
			this.blockButtonPanel.remove(this.blockButton[i]);
		this.tfNumbBlocks.setText(Integer.toString(0));
		this.blockButtonPanel.updateUI();
		this.nBlocks = 0;
		this.nChosenBlock = -1;
		southPanelMode(-1);

	}

	public void updateBlock(int nb) {

		this.group.detach();
		this.blockGroup.removeChild(this.block[nb]);

		this.block[nb] = new HexaHedron(this.blockBoundary[nb], this.scaleFactor);
		this.block[nb].setFaceColor(this.blockMatColor[nb]);
		this.block[nb].setEdgeColor(this.blockMatColor[nb].darker().darker());
		if(this.blockMaterial[this.nBlocks].equals("air"))
			this.block[this.nBlocks].setTransp(this.spaceTransparency);
		else
			this.block[this.nBlocks].setTransp(this.blockTransparency);
		this.block[nb].showFacets(true);
		this.block[nb].showEdges(true);
		this.blockGroup.addChild(this.block[nb]);
		this.universe.addBranchGraph(this.group);

	}

	public void updateSpace() {
		this.group.detach();
		this.refGroup.removeChild(this.block[0]);
		this.block[0] = new HexaHedron(this.blockBoundary[0], this.scaleFactor);
		this.block[0].setEdgeColor((this.blockMatColor[0]));
		this.block[0].setFaceColor(this.blockMatColor[0]);
		this.block[0].setEdgeWidth(1);
		this.block[0].setTransp(this.spaceTransparency);
		this.block[0].setEdgeTransp(this.spaceTransparency);
		this.block[0].showEdges(true);
		// block[0].showFacets(true);
		this.refGroup.addChild(this.block[0]);

		this.universe.addBranchGraph(this.group);

	}

	public void releaseBlock(int nb) {

		this.group.detach();
		this.block[nb].setFaceColor(this.blockMatColor[nb]);
		this.universe.addBranchGraph(this.group);
		this.blockButton[nb].setBackground(this.bAddBlock.getBackground());

	}

	public void releaseSpace() {

		saveSpaceSpec();
		this.group.detach();
		this.block[0].setEdgeColor(this.blockMatColor[0]);
		this.universe.addBranchGraph(this.group);
		this.bSpace.setBackground(this.bAddBlock.getBackground());

	}

	public void releaseCoil(int nc) {

		// saveCoilSpec();
		this.group.detach();
		// coil[nc].setEdgeColor(matColor[0]);
		this.universe.addBranchGraph(this.group);
		// bCoil.setBackground(bAddBlock.getBackground());

	}

	public void selectBlock(int nb) {
		if (nb > -1) {
			if (this.nPrevChosenBlock == 0)
				releaseSpace();

			else if (this.nPrevChosenBlock > 0)
				releaseBlock(this.nPrevChosenBlock);
			this.blockButton[nb].setBackground(Color.GREEN);
			this.group.detach();
			if (nb == 0)
				this.block[nb].setEdgeColor(Color.GREEN);
			else
				this.block[nb].setFaceColor(Color.GREEN);
			this.universe.addBranchGraph(this.group);
			southPanelMode(1);
			this.nChosenBlock = nb;
			fillBlockSpec(nb);
		}

	}

	public void selectSpace() {
		this.nPrevChosenBlock = this.nChosenBlock;
		this.bSpace.setBackground(Color.GREEN);
		this.group.detach();
		this.block[0].setEdgeColor(Color.GREEN);
		this.universe.addBranchGraph(this.group);
		southPanelMode(0);
		fillSpaceSpec();

	}

	private void applyChanges(int nb) {

		if (this.editBlock.mode == 0) {
			Vect shift = new Vect(3);
			try {
				shift.el[0] = Double.parseDouble(this.editBlock.tfxyz[0].getText());
				shift.el[1] = Double.parseDouble(this.editBlock.tfxyz[1].getText());
				shift.el[2] = Double.parseDouble(this.editBlock.tfxyz[2].getText());
			} catch (NumberFormatException e) {
			}

			translate(nb, shift);
		} else if (this.editBlock.mode == 1) {

			int rotPhi = this.editBlock.rotPhi.getSelectedIndex();
			int rotTheta = this.editBlock.rotTheta.getSelectedIndex();
			rotate(nb, rotPhi, rotTheta);

		}

		else if (this.editBlock.mode == 2) {
			int direction = this.editBlock.combDuplic.getSelectedIndex();
			addBlock(true, this.nChosenBlock, direction);
		}
		selectBlock(this.nChosenBlock);
		fillBlockSpec(this.nChosenBlock);

	}

	private void translate(int nb, Vect shift) {
		this.blockBoundary[nb][0] += shift.el[0];
		this.blockBoundary[nb][1] += shift.el[0];
		this.blockBoundary[nb][2] += shift.el[1];
		this.blockBoundary[nb][3] += shift.el[1];
		this.blockBoundary[nb][4] += shift.el[2];
		this.blockBoundary[nb][5] += shift.el[2];
		updateBlock(nb);

	}

	private void rotate(int nb, int rotPhi, int rotTheta) {
		if (rotPhi == 1) {
			double temp = this.blockBoundary[nb][0];
			this.blockBoundary[nb][0] = this.blockBoundary[nb][2];
			this.blockBoundary[nb][2] = temp;
			temp = this.blockBoundary[nb][1];
			this.blockBoundary[nb][1] = this.blockBoundary[nb][3];
			this.blockBoundary[nb][3] = temp;
		}

		if (rotTheta == 1) {
			double temp = this.blockBoundary[nb][0];
			this.blockBoundary[nb][0] = this.blockBoundary[nb][4];
			this.blockBoundary[nb][4] = temp;
			temp = this.blockBoundary[nb][1];
			this.blockBoundary[nb][1] = this.blockBoundary[nb][5];
			this.blockBoundary[nb][5] = temp;
		}

		updateBlock(nb);

	}

	public void southPanelMode(int n) {
		if (n == -1) {

			this.southPanel.removeAll();

		}

		else if (n == 0) {
			this.southPanel.removeAll();
			this.southPanel.add(this.spaceSpec);

		} else if (n == 1) {
			this.southPanel.removeAll();
			this.southPanel.add(this.blockSpec);
		} else if (n == 2) {
			this.southPanel.removeAll();
		}

		this.southPanel.updateUI();

	}

	public void applySize() {
		this.refMeshSize = Double.parseDouble(this.tfRefMesh.getText());
		for (int i = 0; i <= this.nBlocks; i++)
			for (int j = 0; j < this.nBoundary; j++) {
				this.minMeshLeft[i][j] = this.refMeshSize;
				this.minMeshRight[i][j] = this.refMeshSize;
			}
		if (this.nChosenBlock == 0)
			fillSpaceSpec();
		else if (this.nChosenBlock > 0)
			fillBlockSpec(this.nChosenBlock);
	}

	public void applyBase() {
		this.refBase = Double.parseDouble(this.tfRefBase.getText());
		for (int i = 0; i <= this.nBlocks; i++)
			for (int j = 0; j < this.nBoundary; j++) {
				this.baseLeft[i][j] = this.refBase;
				this.baseRight[i][j] = this.refBase;
			}
		if (this.nChosenBlock == 0)
			fillSpaceSpec();
		else if (this.nChosenBlock > 0)
			fillBlockSpec(this.nChosenBlock);
	}

	public void addData() {
		addData(false, 0, 0);

	}

	public void addData(boolean duplicate, int nb, int direction) {

		if (duplicate) {

			double shift = 0;
			switch (direction) {
			case 0: {
				shift = this.blockBoundary[nb][0] - this.blockBoundary[nb][1];
				break;
			}
			case 1: {
				shift = this.blockBoundary[nb][1] - this.blockBoundary[nb][0];
				break;
			}
			case 2: {
				shift = this.blockBoundary[nb][2] - this.blockBoundary[nb][3];
				break;
			}
			case 3: {
				shift = this.blockBoundary[nb][3] - this.blockBoundary[nb][2];
				break;
			}
			case 4: {
				shift = this.blockBoundary[nb][4] - this.blockBoundary[nb][5];
				break;
			}
			case 5: {
				shift = this.blockBoundary[nb][5] - this.blockBoundary[nb][4];
				break;
			}
			}

			for (int j = 0; j < 6; j++) {
				if (j / 2 == direction / 2)
					this.blockBoundary[this.nBlocks][j] = this.blockBoundary[nb][j] + shift;
				else
					this.blockBoundary[this.nBlocks][j] = this.blockBoundary[nb][j];
				this.minMeshLeft[this.nBlocks][j] = this.refMeshSize;
				this.minMeshRight[this.nBlocks][j] = this.refMeshSize;
				this.baseLeft[this.nBlocks][j] = this.refBase;
				this.baseRight[this.nBlocks][j] = this.refBase;

			}
			this.J[this.nBlocks] = new Vect(0, 0, 0);
			this.M[this.nBlocks] = new Vect(0, 0, 0);
			this.mur[this.nBlocks] = this.mur[nb].deepCopy();
			this.sigma[this.nBlocks] = this.sigma[nb].deepCopy();
			this.blockName[this.nBlocks] = this.blockName[nb];
			this.blockMaterial[this.nBlocks] = this.blockMaterial[nb];
			this.blockMatColor[this.nBlocks] = this.blockMatColor[nb];

		}

		else {

			for (int j = 0; j < this.nBoundary; j++) {
				this.blockBoundary[this.nBlocks][j] = this.blockBoundary[0][j] / 5;


				this.minMeshLeft[this.nBlocks][j] = this.refMeshSize;
				this.minMeshRight[this.nBlocks][j] = this.refMeshSize;
				this.baseLeft[this.nBlocks][j] = this.refBase;
				this.baseRight[this.nBlocks][j] = this.refBase;
			}

			this.mur[this.nBlocks]=new Vect(3);
			this.sigma[this.nBlocks]=new Vect(3);
			for(int k=0;k<3;k++){
				this.mur[this.nBlocks].el[k] = Double.parseDouble(this.blockSpec.tfMu[k].getText());
				this.sigma[this.nBlocks].el[k] = Double.parseDouble(this.blockSpec.tfSigma[k].getText());
			}

			this.J[this.nBlocks] = new Vect(0, 0, 0);
			this.M[this.nBlocks] = new Vect(0, 0, 0);
			this.blockName[this.nBlocks] = "block"+nBlocks;
			this.blockMaterial[this.nBlocks] = "iron";
			this.blockMatColor[this.nBlocks] = this.matData.matColor(this.blockMaterial[this.nBlocks]);
		}

		if (!this.bDiscretize.isEnabled())
			this.bDiscretize.setEnabled(true);
	}

	public void addCoil() {

		this.hasCoil = true;

		double tx = .02*scaleFactor, ty = .02*scaleFactor;
		double tz = .1*scaleFactor;
		double Jk = 2e6;
		double lx = .2*scaleFactor, ly = .2*scaleFactor, 
		d = 0, tg = .03*scaleFactor, tb = .2*scaleFactor, d1 = .2*scaleFactor, dd = .3*scaleFactor;
		double mu1 = 1000;
		double sigma1 = 1e7;

		boolean bfalse = false;
		addBlock();
		this.blockBoundary[this.nBlocks][0] = -lx;
		this.blockBoundary[this.nBlocks][1] = lx;
		this.blockBoundary[this.nBlocks][2] = ly;
		this.blockBoundary[this.nBlocks][3] = ly + ty;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(-Jk, 0, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil1";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discLeft[this.nBlocks][0] = bfalse;
		this.discRight[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = -lx - tx;
		this.blockBoundary[this.nBlocks][1] = -lx;
		this.blockBoundary[this.nBlocks][2] = -ly;
		this.blockBoundary[this.nBlocks][3] = ly;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(0, -Jk, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil2";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discLeft[this.nBlocks][2] = bfalse;
		this.discRight[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = lx;
		this.blockBoundary[this.nBlocks][1] = lx + tx;
		this.blockBoundary[this.nBlocks][2] = -ly;
		this.blockBoundary[this.nBlocks][3] = ly;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(0, Jk, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil3";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discLeft[this.nBlocks][2] = bfalse;
		this.discRight[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = -lx - tx;
		this.blockBoundary[this.nBlocks][1] = -lx;
		this.blockBoundary[this.nBlocks][2] = -ly - ty;
		this.blockBoundary[this.nBlocks][3] = -ly;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(Jk / 2, -Jk / 2, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockName[this.nBlocks] = "coil4";
		this.blockMaterial[this.nBlocks] = "copper";
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = lx;
		this.blockBoundary[this.nBlocks][1] = lx + tx;
		this.blockBoundary[this.nBlocks][2] = -ly - tx;
		this.blockBoundary[this.nBlocks][3] = -ly;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(Jk / 2, Jk / 2, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil5";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = lx;
		this.blockBoundary[this.nBlocks][1] = lx + tx;
		this.blockBoundary[this.nBlocks][2] = ly;
		this.blockBoundary[this.nBlocks][3] = ly + ty;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(-Jk / 2, Jk / 2, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil6";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = -lx - tx;
		this.blockBoundary[this.nBlocks][1] = -lx;
		this.blockBoundary[this.nBlocks][2] = ly;
		this.blockBoundary[this.nBlocks][3] = ly + ty;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(-Jk / 2, -Jk / 2, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil7";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discRight[this.nBlocks][0] = bfalse;
		this.discLeft[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = -lx;
		this.blockBoundary[this.nBlocks][1] = lx;
		this.blockBoundary[this.nBlocks][2] = -ly - ty;
		this.blockBoundary[this.nBlocks][3] = -ly;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		this.J[this.nBlocks] = new Vect(Jk, 0, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = 1.0;
		this.blockName[this.nBlocks] = "coil8";
		this.blockMaterial[this.nBlocks] = "copper";
		this.discLeft[this.nBlocks][0] = bfalse;
		this.discRight[this.nBlocks][1] = bfalse;
		this.discRight[this.nBlocks][2] = bfalse;
		this.discLeft[this.nBlocks][3] = bfalse;
		this.blockMatColor[this.nBlocks] = Color.orange;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = .8 * lx - d / 2;
		this.blockBoundary[this.nBlocks][1] = .8 * lx + 2 * tb;
		this.blockBoundary[this.nBlocks][2] = -.8 * ly + d;
		this.blockBoundary[this.nBlocks][3] = .8 * ly - d;
		this.blockBoundary[this.nBlocks][4] = -2 * tz - tb;
		this.blockBoundary[this.nBlocks][5] = -2 * tz - d;
		this.J[this.nBlocks] = new Vect(0, 0, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = mu1;
		this.blockName[this.nBlocks] = "iron1";
		this.blockMaterial[this.nBlocks] = "iron";
		this.blockMatColor[this.nBlocks] = Color.red;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = -.8 * lx;
		this.blockBoundary[this.nBlocks][1] = .8 * lx;
		this.blockBoundary[this.nBlocks][2] = -.8 * ly;
		this.blockBoundary[this.nBlocks][3] = .8 * ly;
		this.blockBoundary[this.nBlocks][4] = -2 * tz - tb;
		this.blockBoundary[this.nBlocks][5] = 2 * tz + tb;
		this.J[this.nBlocks] = new Vect(0, 0, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = mu1;
		this.blockName[this.nBlocks] = "iron2";
		this.blockMaterial[this.nBlocks] = "iron";
		this.blockMatColor[this.nBlocks] = Color.red;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = .8 * lx - d / 2;
		this.blockBoundary[this.nBlocks][1] = .8 * lx + 2* tb;
		this.blockBoundary[this.nBlocks][2] = -.8 * ly + d;
		this.blockBoundary[this.nBlocks][3] = .8 * ly - d;
		this.blockBoundary[this.nBlocks][4] = 2 * tz + d;
		this.blockBoundary[this.nBlocks][5] = 2 * tz + tb;
		this.J[this.nBlocks] = new Vect(0, 0, 0);
		for(int k=0;k<3;k++)
			this.mur[this.nBlocks].el[k] = mu1;
		this.blockName[this.nBlocks] = "iron3";
		this.blockMaterial[this.nBlocks] = "iron";		
		this.blockMatColor[this.nBlocks] = Color.red;
		updateBlock(this.nBlocks);

		addBlock();
		this.blockBoundary[this.nBlocks][0] = lx +tb/2;
		this.blockBoundary[this.nBlocks][1] = .8 * lx + 2 * tb;
		this.blockBoundary[this.nBlocks][2] = -.8 * ly + d;
		this.blockBoundary[this.nBlocks][3] = .8 * ly - d;
		this.blockBoundary[this.nBlocks][4] = - 2*tz;
		this.blockBoundary[this.nBlocks][5] =  2 * tz - tg/2;
		this.J[this.nBlocks] = new Vect(0, 0, 0);
		for(int k=0;k<3;k++){
			this.mur[this.nBlocks].el[k] = mu1;
			this.sigma[this.nBlocks].el[k] = sigma1;
		}

		this.blockName[this.nBlocks] = "iron4";
		this.blockMaterial[this.nBlocks] = "iron";
		this.blockMatColor[this.nBlocks] = Color.red;
		updateBlock(this.nBlocks);

		addBlock();
		removeBlock(this.nBlocks);

		for (int i = 1; i <= this.nBlocks; i++) {
			this.hasCurrent[i] = true;
		}

		for (int i = 0; i < 6; i++)
			this.BCtype[i] = 1;

		fillSpaceSpec();
		saveSpaceSpec();

	}




	public void addCore() {

		this.units.setSelectedIndex(0);
		for (int i = 0; i < 3; i++)
			addBlock();

		/*
		 * double lx1=89.9; double lx2=152.4; double ly1=38.75; double
		 * ly2=101.6;
		 */

		double lx1 = 100;
		double lx2 = 400;
		double ly1 = 100;
		double ly2 = 400;
		double tz = 50;

		double mu1 = 1000.0, sigma1 = 1e7;

		this.blockBoundary[this.nBlocks][0] = -lx1;
		this.blockBoundary[this.nBlocks][1] = lx1;
		this.blockBoundary[this.nBlocks][2] = -ly1;
		this.blockBoundary[this.nBlocks][3] = ly1;
		this.blockBoundary[this.nBlocks][4] = -tz;
		this.blockBoundary[this.nBlocks][5] = tz;
		for(int k=0;k<3;k++){
			this.mur[this.nBlocks].el[k] = 1;
			this.sigma[this.nBlocks].el[k] = 0;
		}
		this.blockMaterial[this.nBlocks] = "air";

		this.blockBoundary[this.nBlocks - 1][0] = -lx2;
		this.blockBoundary[this.nBlocks - 1][1] = lx2;
		this.blockBoundary[this.nBlocks - 1][2] = -ly2;
		this.blockBoundary[this.nBlocks - 1][3] = ly2;
		this.blockBoundary[this.nBlocks - 1][4] = -tz;
		this.blockBoundary[this.nBlocks - 1][5] = tz;
		for(int k=0;k<3;k++){
			this.mur[this.nBlocks-1].el[k] = mu1;
			this.sigma[this.nBlocks-1].el[k] = sigma1;
		}
		this.blockMaterial[this.nBlocks - 1] = "iron";

		for (int i = this.nBlocks; i >= this.nBlocks - 2; i--) {
			if (this.blockMaterial[i].equals("iron")) {
				this.blockMatColor[i] = Color.red;
			} else if (this.blockMaterial[i] == "air")
				this.blockMatColor[i] = this.blockMatColor[0];

			updateBlock(i);
		}
		removeBlock(this.nBlocks - 2);

		this.BCtype[4] = 1;
		this.diricB[4] = new Vect(0, 0, 1);

		fillSpaceSpec();
		saveSpaceSpec();

	}

	public void fillBlockSpec(int nb) {

		for (int j = 0; j < this.nBoundary; j++) {

			this.blockSpec.tfBoundary[j].setText(Double.toString(trunc(
					this.blockBoundary[nb][j], this.decimal - 1)));

			if (!this.discLeft[nb][j])
				this.blockSpec.na[2 * j].setSelected(true);
			else
				this.blockSpec.na[2 * j].setSelected(false);

			if (!this.discRight[nb][j])
				this.blockSpec.na[2 * j + 1].setSelected(true);
			else
				this.blockSpec.na[2 * j + 1].setSelected(false);

			this.blockSpec.tfMeshSize[2 * j].setText(Double.toString(trunc(
					this.minMeshLeft[nb][j], this.decimal)));
			this.blockSpec.tfMeshSize[2 * j + 1].setText(Double.toString(trunc(
					this.minMeshRight[nb][j], this.decimal)));
			this.blockSpec.tfBase[2 * j].setText(Double.toString(trunc(
					this.baseLeft[nb][j], 2)));
			this.blockSpec.tfBase[2 * j + 1].setText(Double.toString(trunc(
					this.baseRight[nb][j], 2)));
		}

		this.blockSpec.tfMaterial.setText(this.blockMaterial[nb]);
		this.blockSpec.tfName.setText(this.blockName[nb]);



		this.blockSpec.tfName.setBackground(this.blockMatColor[nb]);
		this.blockSpec.tfMaterial.setBackground(this.blockMatColor[nb]);

		for (int k = 0; k < 3; k++){
			this.blockSpec.tfMu[k].setText(this.formatter.format(this.mur[nb].el[k]));
			this.blockSpec.tfSigma[k].setText(this.formatter.format(this.sigma[nb].el[k]));
			this.blockSpec.tfCurrent[k].setText(this.formatter.format(this.J[nb].el[k]));

		}

		for (int k = 0; k < 3; k++)
			this.blockSpec.tfMag[k].setText(Double.toString(this.M[nb].el[k]));

		if (this.hasCurrent[nb])
			this.blockSpec.hasCurrent.setSelected(true);
		else
			this.blockSpec.hasCurrent.setSelected(false);

		if (this.hasMag[nb])
			this.blockSpec.hasMag.setSelected(true);
		else
			this.blockSpec.hasMag.setSelected(false);

		if (this.linearBH[nb])
			this.blockSpec.nonlinear.setSelected(false);
		else
			this.blockSpec.nonlinear.setSelected(true);

	}

	public void fillSpaceSpec() {

		for (int j = 0; j < this.nBoundary; j++) {

			this.spaceSpec.tfBoundary[j].setText(Double.toString(trunc(
					this.blockBoundary[0][j], this.decimal - 1)));
			if (this.BCtype[j]==1) {
				this.spaceSpec.bCondSelector[j].setSelectedIndex(1);
				for (int k = 0; k < 3; k++)
					this.spaceSpec.tfDiric[j][k].setText(Double
							.toString(this.diricB[j].el[k]));
			} else	if (this.BCtype[j]==2) this.spaceSpec.bCondSelector[j].setSelectedIndex(2);

			else this.spaceSpec.bCondSelector[j].setSelectedIndex(0);
		}

		if (this.hasCurrent[0])
			this.spaceSpec.hasCurrent.setSelected(true);
		else
			this.spaceSpec.hasCurrent.setSelected(false);

		for (int k = 0; k < 3; k++){

			this.spaceSpec.tfCurrent[k].setText(this.formatter.format(this.J[0].el[k]));
			this.spaceSpec.tfMu[k].setText(this.formatter.format(this.mur[0].el[k]));
			this.spaceSpec.tfSigma[k].setText(this.formatter.format(this.sigma[0].el[k]));
		}

	}

	public void saveBlockSpec(int nb) {
		if (nb > -1)
			if (nb == 0)
				saveSpaceSpec();
			else
				try {

					this.blockName[nb] = this.blockSpec.tfName.getText();
					this.blockMaterial[nb] = this.blockSpec.tfMaterial.getText();
					this.blockMatColor[nb] = this.matData.matColor(this.blockMaterial[nb]);
					for(int k=0;k<3;k++){
						this.mur[nb].el[k] = Double.parseDouble(this.blockSpec.tfMu[k].getText());
						this.sigma[nb].el[k] = Double.parseDouble(this.blockSpec.tfSigma[k].getText());
					}
					if (this.blockSpec.hasCurrent.isSelected())
						this.hasCurrent[nb] = true;
					else
						this.hasCurrent[nb] = false;

					if (this.blockSpec.nonlinear.isSelected())
					{
						this.linearBH[nb] = false;
					}
					else
						this.linearBH[nb] = true;

					this.J[nb] = new Vect(
							Double.parseDouble(this.blockSpec.tfCurrent[0].getText()),
							Double.parseDouble(this.blockSpec.tfCurrent[1].getText()),
							Double.parseDouble(this.blockSpec.tfCurrent[2].getText()));

					if (this.blockSpec.hasMag.isSelected())
						this.hasMag[nb] = true;
					else
						this.hasMag[nb] = false;

					this.M[nb] = new Vect(Double.parseDouble(this.blockSpec.tfMag[0]
					                                                              .getText()), Double.parseDouble(this.blockSpec.tfMag[1]
					                                                                                                                   .getText()), Double.parseDouble(this.blockSpec.tfMag[2]
					                                                                                                                                                                        .getText()));

					for (int j = 0; j < this.nBoundary; j++) {
						this.blockBoundary[nb][j] = Double
						.parseDouble(this.blockSpec.tfBoundary[j].getText());
						this.minMeshLeft[nb][j] = Double
						.parseDouble(this.blockSpec.tfMeshSize[2 * j]
						                                       .getText());
						this.minMeshRight[nb][j] = Double
						.parseDouble(this.blockSpec.tfMeshSize[2 * j + 1]
						                                       .getText());
						this.baseLeft[nb][j] = Double
						.parseDouble(this.blockSpec.tfBase[2 * j].getText());
						this.baseRight[nb][j] = Double
						.parseDouble(this.blockSpec.tfBase[2 * j + 1]
						                                   .getText());

						if (this.blockSpec.na[2 * j].isSelected())
							this.discLeft[nb][j] = false;
						else
							this.discLeft[nb][j] = true;

						if (this.blockSpec.na[2 * j + 1].isSelected())
							this.discRight[nb][j] = false;
						else
							this.discRight[nb][j] = true;

					}


				}

		catch (NumberFormatException nfe) {
			String msg = "One or more text fields do not contain a number.";
			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE);
		}
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
		zoom--;
		//moveStep=moveStep0*exp(.1*zoom);
		camEye.el[2]*=exp(this.zoom / 20.0);
		newViewer();

	}


	public void saveSpaceSpec() {

		try {
			this.blockName[0] = this.spaceSpec.tfName.getText();
			this.blockMatColor[0] = this.matData.matColor(this.blockMaterial[0]);
			this.blockMaterial[0] = this.spaceSpec.tfMaterial.getText();
			for(int k=0;k<3;k++){
				this.mur[0].el[k] = Double.parseDouble(this.spaceSpec.tfMu[k].getText());
				this.sigma[0].el[k] = Double.parseDouble(this.spaceSpec.tfSigma[k].getText());
				this.J[0].el[k] = Double.parseDouble(this.spaceSpec.tfCurrent[k].getText());

			}

			if (this.spaceSpec.hasCurrent.isSelected())
				this.hasCurrent[0] = true;
			else
				this.hasCurrent[0] = false;

			for (int j = 0; j < this.nBoundary; j++) {
				this.blockBoundary[0][j] = Double
				.parseDouble(this.spaceSpec.tfBoundary[j].getText());

				if (this.spaceSpec.bCondSelector[j].getSelectedIndex() == 1) {
					this.BCtype[j] = 1;

					for(int k=0;k<3;k++){

						this.diricB[j].el[k] = Double.parseDouble(this.spaceSpec.tfDiric[j][k]
						                                                                    .getText());

					}

				} 
				else if (this.spaceSpec.bCondSelector[j].getSelectedIndex() == 2)
					this.BCtype[j] = 2;
				else
					this.BCtype[j] = 0;

			}


		}

		catch (NumberFormatException nfe) {
			String msg = "One or more text fields do not contain a number.";
			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE);
		}

	}


	public void reorderData(int nb) {

		for (int i = nb; i < this.nBlocks; i++) {
			this.blockButton[i] = this.blockButton[i + 1];
			this.blockButton[i].setText("Block " + Integer.toString(i));
			this.blockButton[i].setName(Integer.toString(i));
		}

		this.tfNumbBlocks.setText(Integer.toString(this.nBlocks - 1));
		this.blockButtonPanel.updateUI();
		for (int i = nb; i < this.nBlocks; i++) {
			for (int j = 0; j < this.nBoundary; j++) {
				this.blockBoundary[i][j] = this.blockBoundary[i + 1][j];
				this.minMeshLeft[i][j] = this.minMeshLeft[i + 1][j];
				this.minMeshRight[i][j] = this.minMeshRight[i + 1][j];
				this.baseLeft[i][j] = this.baseRight[i + 1][j];
				this.baseRight[i][j] = this.baseRight[i + 1][j];

			}
			this.blockMaterial[i] = this.blockMaterial[i + 1];
			this.blockMatColor[i] = this.blockMatColor[i + 1];
			this.mur[i] = this.mur[i + 1].deepCopy();;
			this.sigma[i] = this.sigma[i + 1].deepCopy();;
			this.J[i] = this.J[i + 1].deepCopy();

			this.block[i] = this.block[i + 1];

		}
	}

	public void clearStuff() {
		removeAllButtons();
		this.group.detach();
		this.refGroup.removeChild(this.blockGroup);
		this.refGroup.removeChild(this.vFieldGroup);
		this.refGroup.removeChild(this.block[0]);
		this.blockGroup = new TransformGroup();
		this.vFieldGroup = new TransformGroup();
		this.refGroup.addChild(this.blockGroup);
		southPanelMode(-1);
		this.spaceSpec = new SpaceSpec();
		southPanelMode(0);
		initialize();
		addSpace();


		this.nBlocks = 0;
		this.fieldMode=0;
		resetView();
		this.discretized = false;
		this.bDiscretize.setEnabled(false);
		this.universe.addBranchGraph(this.group);

		this.progressThread = null;

	}



	public void showAxes(boolean b) {
		this.group.detach();
		this.group.removeChild(this.refGroup);
		if (b)
			this.refGroup.addChild(this.cartesian);			
		else
			this.refGroup.removeChild(this.cartesian);
		this.group.addChild(this.refGroup);
		this.universe.addBranchGraph(this.group);
	}


	private void updateUnits(int newUnintIndex) {
		double c=1;
		if(newUnintIndex==0 && unitIndex==1) c=1e-3;
		else if(newUnintIndex==0 && unitIndex==2) c=1e-6;
		else if(newUnintIndex==1 && unitIndex==0) c=1e3;
		else if(newUnintIndex==1 && unitIndex==2) c=1e-3;
		else if(newUnintIndex==2 && unitIndex==0) c=1e6;
		else if(newUnintIndex==2 && unitIndex==1) c=1e3;

		if(newUnintIndex==0 ) {scaleFactor=1; decimal=4;}
		else if(newUnintIndex==1 ) {scaleFactor=1e3; decimal=1;}
		else if(newUnintIndex==2 ) {scaleFactor=1e6; decimal=1;}

		if(c!=1){
			this.refMeshSize = trunc(c * this.refMeshSize, this.decimal);
			this.refBoundary*=c;
			for (int i = 0; i <= this.nBlocks; i++) {
				for (int j = 0; j < this.nBoundary; j++) {
					this.blockBoundary[i][j] = trunc(c * this.blockBoundary[i][j], this.decimal);
					this.minMeshLeft[i][j] = trunc(c * this.minMeshLeft[i][j], this.decimal);
					this.minMeshRight[i][j] = trunc(c * this.minMeshRight[i][j], this.decimal);
				}

			}

			this.tfRefMesh.setText(String.valueOf(this.refMeshSize));
			if (this.nChosenBlock !=-1){
				if (this.nChosenBlock == 0)
					for (int j = 0; j < this.nBoundary; j++)
						this.spaceSpec.tfBoundary[j].setText(String
								.valueOf(this.blockBoundary[this.nChosenBlock][j]));
				else {
					for (int j = 0; j < this.nBoundary; j++)
						this.blockSpec.tfBoundary[j].setText(String
								.valueOf(this.blockBoundary[this.nChosenBlock][j]));
					for (int j = 0; j < this.nBoundary; j++) {
						this.blockSpec.tfMeshSize[2 * j].setText(String
								.valueOf(this.minMeshLeft[this.nChosenBlock][j]));
						this.blockSpec.tfMeshSize[2 * j + 1].setText(String
								.valueOf(this.minMeshRight[this.nChosenBlock][j]));
					}
				}
			}
		}
	}


	public void discretizeMode() {

		if (this.nChosenBlock == 0)
			saveSpaceSpec();
		else if (this.nChosenBlock > 0)
			saveBlockSpec(this.nChosenBlock);
		this.discretized = false;
		this.messageArea.setText("");
		Console.redirectOutput(this.messageArea);

		this.progressThread = new Thread() {

			public void run() {
				DrawingPanel.this.progressBar.setVisible(true);
				int kt = 0;
				while (!DrawingPanel.this.discretized) {
					kt++;
					DrawingPanel.this.progressBar.setValue(10 * (kt % 11));
					DrawingPanel.this.progressBar.repaint();
					try {
						Thread.sleep(500);
					} catch (InterruptedException err) {
					}

				}
			

				DrawingPanel.this.progressBar.setVisible(false);
			}
		};

		this.progressThread.start();

	}


	public void writeModel(String modelFilePath){

		try{
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(modelFilePath)));	
			pw.println("Model Description");
			pw.println();
			pw.println("Number_of_Blocks: "+this.nBlocks);
			pw.println();
			pw.println("space :");
			pw.println();
			pw.println("Name: "+this.blockName[0]);
			pw.println("Material: "+this.blockMaterial[0]);
			pw.println("   Block_Boundaries: [ "+this.blockBoundary[0][0]+" "+this.blockBoundary[0][1]+" "+this.blockBoundary[0][2]+" "+this.blockBoundary[0][3]+" "+this.blockBoundary[0][4]+" "+this.blockBoundary[0][5]+" ]");
			pw.println("   mur: [ "+this.mur[0].el[0]+" "+this.mur[0].el[1]+" "+this.mur[0].el[2]+" ]");
			pw.println(" sigma: [ "+this.sigma[0].el[0]+" "+this.sigma[0].el[1]+" "+this.sigma[0].el[2]+" ]");
			pw.println();

			for(int i=1;i<=this.nBlocks;i++){

				pw.println("region "+i +": ");
				pw.println();
				pw.println("Name: "+this.blockName[i]);
				pw.println("Material: "+this.blockMaterial[i]);
				pw.println("   Block_Boundaries: [ "+this.blockBoundary[i][0]+" "+this.blockBoundary[i][1]+" "+this.blockBoundary[i][2]+" "+this.blockBoundary[i][3]+" "+this.blockBoundary[i][4]+" "+this.blockBoundary[i][5]+" ]");
				pw.print("   discretize: ");
				for(int j=0;j<6;j++) {pw.print(this.discLeft[i][j]+"  "); pw.print(this.discRight[i][j]+"  ");}
				pw.println();
				pw.println("Nonlinear: "+!this.linearBH[i]);
				if(this.linearBH[i])
					pw.println("   mur: [ "+this.mur[i].el[0]+" "+this.mur[i].el[1]+" "+this.mur[i].el[2]+" ]");

				pw.println(" sigma: [ "+this.sigma[i].el[0]+" "+this.sigma[i].el[1]+" "+this.sigma[i].el[2]+" ]");
				pw.println("     J: [ "+this.J[i].el[0]+" "+this.J[i].el[1]+" "+this.J[i].el[2]+" ]");
				pw.println("     M: [ "+this.M[i].el[0]+" "+this.M[i].el[1]+" "+this.M[i].el[2]+" ]");
				pw.println();

			}

			pw.close();
		}
		catch(IOException e){}
	}

	public void loadModel(String modelFilePath) {
		loader.loadModel(this,modelFilePath);

	}
	
	private double trunc(double a, int n) {
		return floor(a * pow(10, n)) / pow(10, n);
	}

	public void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}