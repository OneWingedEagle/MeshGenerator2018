package fem;

import graphics.Cartesian2D;
import graphics.Curve;

import java.awt.Color;

import materialData.MaterialData;
import math.*;
import static java.lang.Math.*;


import ReadWrite.Loader;
import ReadWrite.MeshConverter;
import ReadWrite.Writer;

import drawingPanel.GUI;

public class Model{

	public int dim=3;
	public int numberOfRegions, numberOfNodes,numberOfElements,numberOfEdges,
	nBlocks,nBH,nLam;
	public int nodeNodeConnectionMax=27,nElVert=8,nBoundary=6;
	public int nElEdge=12;
	public double[] spaceBoundary;
	public int[] BCtype;
	public String[] blockMaterial;
	public Vect[] diricB=new Vect[6];
	public Region[] region;
	public Node[] node;
	public Element[] element;
	public Edge[] edge;
	private MeshSet meshSet=new MeshSet();
	private Loader loader=new Loader();
	private Writer writer=new Writer();

	public int[] edgeUnknownIndex,unknownEdgeNumber,nodeUnknownIndex,unknownNodeNumber,
	U_unknownIndex,unknownUnumber,A_unknownIndex,unknownAnumber;
	public int[] knownEdgeNumber,nodeVarIndex,varNodeNumber;
	public double[] knownEdgeValue;
	public double scaleFactor,Bmax1=0,Bmin1=0,Bmax,Bmin,stressMax=0,nodalStressMax=0,
	stressMin,nodalStressMin=0,Jmin,Jmax,Jemin,Jemax,maxDim,minEdgeLength,maxEdgeLength,
	FmsMin,FmsMax=0,FreluctMax=0,FreluctMin=0,uMax=0,AuMax,JmsuMax,defScale;
	public int numberOfUnknownEdges,numberOfKnownEdges,numberOfVarNodes
	,numberOfKnownPhis,numberOfUnknowns,analysisMode,stressViewCode=-1,nodalStressMaxCode
	,numberOfUnknownU,numberOfUnknownUcomp,numberOfUnknownA,numberOfUnknownAcomp,defMode;
	public boolean deform,hasJ,hasM,forceLoaded,fluxLoaded,potentialLoaded,
	stressLoaded,forceCalcLoaded,coupled,cpvms,calCurve,cpIMET,nonLin,hasPBC,edgeElement=true;
	public int nEdEd=34,nNodNod=27,nEdNod=18,nNodEd=54,nEdgeHasJ;
	int[][] facetVert={{6,2,5},{7,4,3},{6,7,2},{0,4,1},{4,7,5},{0,1,3}};

	public byte elCode=4;
	public double nu0=1e7/(4*PI),x1,x2,x3;
	public MaterialData matData=new MaterialData();
	public double freq=1,rdt,dt,errMax=1e-6;
	public int nF,rotAng,nRotorElements;
	//public BHSCurve[] BHS;
	//public LamBSCurve[] lamBS;
	public double[] BHstress;
	//public LamBCurve[] lamB;
	//public BHCurve[] BH;
	public String elType="hexahedron";
	public Vect b,bU;
	public boolean motor,twoPiece,writeFiles,resultReady;
	public double alpha1,alpha2,r1,r2,rm,TrqZ,height=.06,cpb=1;
	public int[] PBCpair;
	public int iterMax=10000;;
	public String eddyFolder,filePath,meshFilePath,dataFilePath,fluxFilePath,eddyFilePath,fluxFolder;

	public Model(){}
	
	
	public Model(int nRegions, int nElements,int nNodes, String elType){
		
		this.numberOfRegions=nRegions;
		this.numberOfElements=nElements;

		this.numberOfNodes=nNodes;

		this.setElType(elType);


		region=new Region[this.numberOfRegions+1];
		for(int i=1;i<=this.numberOfRegions;i++)
			region[i]=new Region(dim);
		
		element=new Element[this.numberOfElements+1];
		for(int i=1;i<=this.numberOfElements;i++)
			element[i]=new Element(elType);

		node=new Node[this.numberOfNodes+1];
		for(int i=1;i<=this.numberOfNodes;i++)
			node[i]=new Node(dim);
	}
	


	public Model(GUI gui){

		nBlocks=gui.drp.nBlocks;
		blockMaterial=new String[nBlocks+1];
		scaleFactor=gui.drp.scaleFactor;
		analysisMode=gui.drp.analysisMode;
		defMode=gui.drp.defMode;
		deform=gui.drp.deform;
		coupled=gui.drp.coupled;

		for(int j=0;j<nBoundary;j++){
			diricB[j]=new Vect(dim);
		}
		BCtype=new int[nBoundary];
		spaceBoundary=new double[nBoundary];


		for(int i=0;i<=nBlocks;i++)
			blockMaterial[i]=gui.drp.blockMaterial[i];

		for(int j=0;j<6;j++){
			BCtype[j] =gui.drp.BCtype[j];
			diricB[j]=gui.drp.diricB[j];
		}

	
		meshSet.setMesh(this,gui);
	


	}
	
	public Model(String bun){
		new Model();
		loadMesh(bun);
	}
	

	
	public void alloc(int nRegions, int nElements,int nNodes, String elType){
		
		this.numberOfRegions=nRegions;
		this.numberOfElements=nElements;

		this.numberOfNodes=nNodes;

		this.setElType(elType);


		region=new Region[this.numberOfRegions+1];
		for(int i=1;i<=this.numberOfRegions;i++)
			region[i]=new Region(dim);
		
		element=new Element[this.numberOfElements+1];
		for(int i=1;i<=this.numberOfElements;i++)
			element[i]=new Element(elType);

		node=new Node[this.numberOfNodes+1];
		for(int i=1;i<=this.numberOfNodes;i++)
			node[i]=new Node(dim);
	}
	public void loadMesh(String bunFilePath){

		loader.loadMesh(this, bunFilePath);
		

	}
	


	public Model deepCopy(){
		
		Model copy=new Model(this.numberOfRegions,this.numberOfElements,this.numberOfNodes,this.elType);
	
		for(int i=1;i<=this.numberOfRegions;i++){
			copy.region[i].setFirstEl(this.region[i].getFirstEl());
			copy.region[i].setLastEl(this.region[i].getLastEl());
		}

	
		for(int i=1;i<=this.numberOfElements;i++){
			copy.element[i].setVertNumb(this.element[i].getVertNumb());
			copy.element[i].setRegion(this.element[i].getRegion());
		}
		
		
		for(int i=1;i<=this.numberOfNodes;i++){
			copy.node[i].setCoord(this.node[i].getCoord());
		}
		copy.scaleFactor=this.scaleFactor;
		return copy;
	}
	
	
	public  void setBoundary(GUI gui){
		for(int i=0;i<6;i++)
			spaceBoundary[i]=gui.drp.blockBoundary[0][i]/scaleFactor;

	}
	
	public double edgeLength(int i){
		double length=node[edge[i].endNodeNumber[1]].getCoord().sub(node[edge[i].endNodeNumber[0]].getCoord()).norm();
		return length;
	}
	
	public void setMinEdge(){
		double minEdge=1e40;
		for(int i=1;i<=this.numberOfEdges;i++){
			if(edge[i].length<minEdge) minEdge=edge[i].length;
		}
		this.minEdgeLength=minEdge;
	}
	public void setMaxEdge(){
		double maxEdge=0;
		for(int i=1;i<=this.numberOfEdges;i++)
			if(edge[i].length>maxEdge) maxEdge=edge[i].length;

		this.maxEdgeLength=maxEdge;
	}

	public  void setMaxDim(){
	
		maxDim=getRmax();
	}

	public void setHasJ(){
		for(int i=1;i<=numberOfRegions;i++)
			if(region[i].hasJ){
				hasJ=true;
				break;
			}
	}
	public void setHasM(){
		for(int i=1;i<=numberOfRegions;i++)
			if(region[i].hasM){
				hasM=true;
				break;
			}
	}

	public void setNonLin(){
		for(int ir=1;ir<=numberOfRegions;ir++)
			if(region[ir].isNonLinear){
				nonLin=true;
				break;
			}
			
		for(int ir=1;ir<=numberOfRegions;ir++){
			if(region[ir].isNonLinear)
				for(int i=region[ir].getFirstEl();i<=region[ir].getLastEl();i++)
						element[i].setNonlin(true);
						
	
						
		}	

	}

	
	public void writeMesh(String bunFilePath){
		writeMesh(bunFilePath,false);
	}



	public void writeMesh(String bunFilePath , boolean deformed){
		writer.writeMesh(this,bunFilePath ,deformed);

	}


	public void writeMesh322(String bunFilePath , boolean deformed){
		writer.writeMesh322(this,bunFilePath ,deformed);
	}

	public void writeMeshq2h(String bunFilePath , boolean deformed){
		writer.writeMeshq2h(this,bunFilePath ,deformed);
	}

	public void writeMesh323(String bunFilePath,double r1,double r2){
		writer.writeMesh323(this,bunFilePath,r1,r2);
	}
	
	public void writeMesh32q(String bunFilePath){
		writer.writeMesh32q(this,bunFilePath);
	}

	public void writeMeshq23(String bunFilePath){
		writer.writeMeshq23(this,bunFilePath);

	}


	public void writeBunX(String bunFilePath ){
		writer.writeBunX(this,bunFilePath);

	}



	public void writeData(String dataFilePath){
		if(this.dim==2)
			writer.writeData2D(this,dataFilePath);
		else
		writer.writeData(this,dataFilePath);

	}

	public boolean loadFlux(String fluxFilePath){
		
		return loader.loadFlux(this,fluxFilePath);
		
	}	

	public boolean loadPotential(String file){
		
		return loader.loadPotential(this,file);
		
	}
	public boolean loadStress(String stressFilePath){
		return loader.loadStress(this,stressFilePath);
	}



	public boolean loadNodalField(String filePath,int mode){
		
		return loader.loadNodalField(this,filePath,mode);

		
	}

	public boolean loadEdgeField(String edgeFilePath,int mode){
		return loader.loadNodalField(this,edgeFilePath,mode);
	}

	public void reportData(){
		writer.reportData(this);
	}

	public Node[] elementNodes(int i){
		Node[] elementNode=new Node[nElVert];
		int[] vertNumb=element[i].getVertNumb();
		for(int j=0;j<nElVert;j++){
			elementNode[j]=node[vertNumb[j]];
		}

		return elementNode;
	}
	public Edge[] elementEdges(int i){
		Edge[] elementEdge=new Edge[nElEdge];
		int[] edgeNumb=element[i].getEdgeNumb();
		for(int j=0;j<nElEdge;j++)
			elementEdge[j]=edge[edgeNumb[j]];
		return elementEdge;
	}

	public int[] getContainingElement(Vect P){
		if(elCode==0) return getContaining3angElement(P);
		else if(elCode==1) return getContainingQuadElement(P);
		int[] elResult;
		int[] result=new int[7];
		for(int i=1;i<=numberOfElements;i++){
			elResult=elContains(i,P);
			result[0]=0;
			for(int j=0;j<6;j++)
				if(elResult[j]<0){
					result[0]=-1;
					break;
				}

			if(result[0]==-1) continue;		


			result[0]=i;
			for(int j=0;j<6;j++)
				result[j+1]=elResult[j];
			break;

		}



		return result;
	}

	public int[] getContaining3angElement(Vect P){
		int[] ne=new int[1];


		double[] S;
		double S0,St;
		for(int i=1;i<=numberOfElements;i++){
			S0=el3angArea(i);
			S=subS3ang(i,P);
			St=S[0]+S[1]+S[2];

			if(abs(1-St/S0)<1e-6){ ne[0]= i; break;}

		}

		return ne;
	}



	public int[] elContains(int i,Vect P){
		int n;
		int[] result=new int [nBoundary];
		for(int ns=0;ns<nBoundary;ns++){
			n=pointOnFace(i,ns,P);
			result[ns]=n;

		}

		return result;
	}

	public int pointOnFace(int i, int ns,Vect P){
		Vect v0,v1,v2,vP;

		v0=node[element[i].getVertNumb(facetVert[ns][0])].getCoord();
		v1=node[element[i].getVertNumb(facetVert[ns][1])].getCoord();
		v2=node[element[i].getVertNumb(facetVert[ns][2])].getCoord();
		vP=P.sub(v0);

		if(vP.norm()==0)
			return 0;
		double crossdot=v1.sub(v0).cross(v2.sub(v0)).dot(P.sub(v0));
		if(crossdot==0)
			return 0;
		else if(crossdot>1e-10)
			return -1;
		else
			return 1;

	}

	public int[] getContainingQuadElement(Vect P){

		int[] ne=new int[1];

		Vect[] v=new Vect[4];

		for(int i=1;i<=numberOfElements;i++){
			int[] vertNumb=element[i].getVertNumb();
			for(int j=0;j<4;j++)
				v[j]=node[vertNumb[j]].getCoord();

			double S0=v[1].sub(v[0]).cross(v[3].sub(v[0])).norm()+
			v[1].sub(v[2]).cross(v[3].sub(v[2])).norm();
			double S=0;
			for(int j=0;j<4;j++)
			{
				S=S+v[j].sub(P).cross(v[(j+1)%4].sub(P)).norm();
			}
			if(abs(1-S/S0)<1e-6) {ne[0]=i; break;}

		}

		return ne;

	}

	
	public double getElementArea(int i){
		if(elCode==0) return el3angArea(i);
		else 	if(elCode==1) return elQuadArea(i);
		else throw new NullPointerException(" Element is not 2D. ");
			
	}
	
	public double el3angArea(int i){
		Node[] vertexNode=elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[2].getCoord().sub(vertexNode[0].getCoord());
		double S=abs(v1.cross(v2).norm())/2;
		return S;
	}

	public double elQuadArea(int i){
		Node[] vertexNode=elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[3].getCoord().sub(vertexNode[0].getCoord());
		Vect v3=vertexNode[1].getCoord().sub(vertexNode[2].getCoord());
		Vect v4=vertexNode[3].getCoord().sub(vertexNode[2].getCoord());
		double S=(v1.cross(v2).norm()+v4.cross(v3).norm())/2;
		return S;
	}
	
	public void setSolvedAL(Vect x){

		for(int i=1;i<=numberOfUnknownEdges;i++){
			edge[unknownEdgeNumber[i]].setSolvedAL(x.el[i-1]);	
			
		
		}
	
		if(this.hasPBC){
		
		for(int i=1;i<=numberOfEdges;i++){
			if(edge[i].map>0) {edge[i].Au=cpb*edge[edge[i].map].Au;
			}
		}	
		}
		
		
		
	}


	public void setU(Vect u){
				
		int ix=0;
		for(int i=1;i<=numberOfUnknownU;i++){
			int nodeNumb=unknownUnumber[i];
			if(node[nodeNumb].isDeformable()){
				for(int k=0;k<dim;k++){
					if(!node[ nodeNumb].is_U_known(k)){
						node[ nodeNumb].setU(k, u.el[ix]);
						ix++;
					}
				}

			}
		}

	}



	public void setElType(String type){
		elType=type;
		if(type.equals("triangle") ){
			elCode=0;
			nElVert=3;
			nElEdge=3;
			this.dim=2;
		}
		else if(type.equals("quadrangle") ){
			elCode=1;
			nElVert=4;
			nElEdge=4;
			this.dim=2;
		}
		else if(type.equals("tetrahedron") ){
			elCode=2;
			nElVert=4;
			nElEdge=6;
			dim=3;
		}
		else if(type.equals("hexahedron") ){
			elCode=3;
			nElVert=8;
			nElEdge=12;
			dim=3;
		}
		nBoundary=2*dim;
	}



	public void setEdge(){
		
		EdgeSet edgeSet=new EdgeSet();
		edgeSet.setEdge(this);
		
	}

	public void setInUseNodes(){
		//=====================	identifying the used nodes
		boolean[] nodeCount=new boolean[this.numberOfNodes+1];
		for(int i=1;i<=this.numberOfElements;i++){
			int[] vert=this.element[i].getVertNumb();
			for(int j=0;j<this.nElVert;j++)	{	
				if(!nodeCount[vert[j]])	{	
				nodeCount[vert[j]]=true;
				this.node[vert[j]].setMap(vert[j]);
			}
			}
		}
				
	}
	


	public void scaleKnownEdgeAL(double c){
		for(int i=1;i<=numberOfKnownEdges;i++)
			if(knownEdgeValue[i]!=0)
				edge[knownEdgeNumber[i]].setSolvedAL(knownEdgeValue[i]*c);	
	}


	public void setEdgePrevAL(Vect Ap){
		for(int i=1;i<=Ap.length;i++)
			edge[unknownEdgeNumber[i]].prevAu=Ap.el[i];	
	}

	public void setNodePhi(Vect x){
		int nodeNumber;
		for(int i=1;i<=numberOfVarNodes;i++){
			nodeNumber=varNodeNumber[i];	
			if(nodeNumber>0)
				node[varNodeNumber[i]].setPhi(x.el[i+numberOfUnknownEdges-1]);	
		}

	}



	public void setSolution(Vect x){
		
		setSolvedAL(x);
		if(analysisMode==2)
			setNodePhi(x);

	}
	
	public Vect getUnknownA(){
		Vect x=new Vect(this.numberOfUnknownEdges);
		for(int i=0;i<x.length;i++)
			x.el[i]=edge[unknownEdgeNumber[i+1]].Au;	
		
		return x;
	}
	
	public Vect getPrevUnknownA(){
		Vect x=new Vect(this.numberOfUnknownEdges);
		for(int i=0;i<x.length;i++)
			x.el[i]=edge[unknownEdgeNumber[i+1]].prevAu;	
		
		return x;
	}

	
	
	public double getRmax(){	

		double rmax=0;

		for(int i=1;i<=numberOfNodes;i++){

			double rn=node[i].getCoord().norm();
			if(rn>rmax) rmax=rn;

		}
		return rmax;
	}
	
	
	public int[] getRegionNodeNumbs(int ir){

		boolean[] nc=new boolean[1+this.numberOfNodes];
		int[] nn=new int[this.numberOfNodes];
		int ix=0;
		for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++){		
			int[] vertNumb=this.element[i].getVertNumb();
		for(int j=0;j<nElVert;j++){
			int nx=vertNumb[j];
			if(!nc[nx]){
				
				nc[nx]=true;
				nn[ix]=nx;
				ix++;
			}
		}
		}
		
		int[] regNodes=new int[ix];
		for(int i=0;i<ix;i++)
			regNodes[i]=nn[i];
		
		return regNodes;

		
	}
	
	public int[] getRegionEdgeNumbs(int ir){

		boolean[] edc=new boolean[1+this.numberOfEdges];
		int[] ned=new int[this.numberOfEdges];
		int ix=0;
		for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++){		
			int[] edgeNumb=this.element[i].getEdgeNumb();
		for(int j=0;j<nElEdge;j++){
			int nx=edgeNumb[j];
			if(!edc[nx]){
				
				edc[nx]=true;
				ned[ix]=nx;
				ix++;
			}
		}
		}
		
		int[] regEdges=new int[ix];
		for(int i=0;i<ix;i++)
			regEdges[i]=ned[i];
		
		return regEdges;

		
	}

	

	
	public void setAuMax(){	

		AuMax=0;

		for(int i=1;i<=numberOfEdges;i++){
			double aum=abs(edge[i].Au);
			if(aum>AuMax) AuMax=aum;

		}

	}
	
	public void setuMax(){	

		uMax=0;

		for(int i=1;i<=numberOfNodes;i++){
			if(node[i].u==null) continue;
			double a=node[i].u.norm();
			if(a>uMax) uMax=a;

		}

	}

	public void setJmsuMax(){	

		JmsuMax=0;

		for(int i=1;i<=numberOfEdges;i++){
			double Jmsum=abs(edge[i].Jms);
			if( Jmsum>JmsuMax) JmsuMax= Jmsum;

		}
	}

	public Vect[] getAllU(){	

		Vect[] u=new Vect[this.numberOfUnknownU];

		for(int i=0;i<this.numberOfUnknownU;i++){

			u[i]=node[unknownUnumber[i+1]].u.deepCopy();
		}
		return u;
	}

	public void setFactJ(double a){
		for(int ir=1;ir<=numberOfRegions;ir++){
			if(region[ir].hasJ) region[ir].setFactJ(a);

		}
		
	}

	
	public Vect getFOf(Vect globalCo){

		int m[]=getContainingElement(globalCo);

		if(m[0]<=0) throw new NullPointerException("given point outside the space ");

		if(element[m[0]].getNu().norm()==nu0)
			return new Vect(dim);
		Vect F=new Vect(dim);
		for(int j=0;j<nElVert;j++)
			F=F.add(node[element[m[0]].getVertNumb(j)].F);

		return  F.times(1.0/nElVert);	

	}
	
	public Vect getJeAt(Vect globalCo){

		int[] m=getContainingElement(globalCo);
		if(m[0]<=0) throw new NullPointerException("given point outside the space ");
		else if(!this.element[m[0]].isConductor()) return new Vect(3);
	 return this.element[m[0]].getJe();
			
	}


/*	public Vect getJeAt(Vect globalCo){

		int[] m=getContainingElement(globalCo);

		if(m[0]<=0) throw new NullPointerException("given point outside the space ");

		if(!element[m[0]].isConductor())
			return new Vect(3);

		Node[] vertex=elementNodes(m[0]);
		Edge[] edge=elementEdges(m[0]);
		double[] dAe=new double[nElEdge];
		
		for(int j=0;j<nElEdge;j++)
			dAe[j]=edge[j].getDiffAu();
		

		if(elCode==0){
			dA=getElement3angA(m[0]);

		Vect dA=getElementA(vertex,dAe);
		if(analysisMode==1)
		return dA.times(element[m[0]].getSigma().times(-rdt));
		else if(analysisMode==2){
			
			double[] nodePhi=new double[nElVert];
			Vect gradPhi=new Vect(dim);
			for(int j=0;j<nElVert;j++)
				nodePhi[j]=vertex[j].getPhi();
			gradPhi=femCalc.gradPhi(vertex,nodePhi);
			return (dA.times(rdt).add(gradPhi)).times(element[m[0]].getSigma().times(-1));

		}
		else
			return new Vect(dim);

		
	}*/


	public Vect getJeOf(int m){
		if(!element[m].isConductor())
			return new Vect(3);

		Vect Jn=new Vect(3);
		if(m>0)
			Jn=element[m].getJe();

		else if(m<-1)
			Jn=element[-m-1].getJe();

		return Jn;
	}
	
	public void writeB(String file){
		writer.writeB(this, file);

	}
	public void writeA(String file){
		writer.writeA(this, file);

	}
	
	public void writeNodalField(String nodalForceFile,int mode){
		
		writer.writeNodalField(this, nodalForceFile, mode);
	}

	public void writeJe(String eddyFile){
		writer.writeJe(this, eddyFile);
		
	}
	
	public void writeStress(String stressFile){
		
		writer.writeStress(this, stressFile);
		
	}
	

	public void rotate(double deg){
		MeshConverter mc=new MeshConverter();
		mc.rotate(this,deg,false);	
	}
	

	private double[] subS3ang(int ie, Vect P){

		Node[] vertexNode=elementNodes(ie);
		Vect v1=vertexNode[0].getCoord().sub(P);
		Vect v2=vertexNode[1].getCoord().sub(P);
		Vect v3=vertexNode[2].getCoord().sub(P);

		double[] N=new double[3];

		N[0]=v2.cross(v3).norm()/2;
		N[1]=v3.cross(v1).norm()/2;
		N[2]=v1.cross(v2).norm()/2;
		return N;
	}

	
	public Vect getElementCenter(int ie){


		Vect center=new Vect(dim);
		int[] vertNumb=element[ie].getVertNumb();
		for(int j=0;j<nElVert;j++)
			center=center.add(node[vertNumb[j]].getCoord());


		return center.times(1.0/nElVert);
	}





	
	

}


