package fem;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import math.Mat;
import math.Vect;
import math.util;


public class Element {
	private int nRegion;
	public int dim;
	private int nVert, nEdge;
	private int[] vertexNumb;
	private int[] edgeNumb;
	private Vect nu,sigma,B,J,Je,M;
	private Vect stress;
	private double yng,pois,ro;
	private String type;
	private boolean hasJ,hasM,isConductor,deformable,nonlin,MS;

	public Element(String type){
		this.type=type;

		if(type.equals("triangle") ){
			nVert=3;
			nEdge=3;
			dim=2;
		}
		else if(type.equals("triangle2nd") ){
			nVert=6;
			nEdge=6;
			dim=2;
		}
		
		else if(type.equals("quadrangle") ){
			nVert=4;
			nEdge=4;
			dim=2;
		}
		else if(type.equals("tetrahedron") ){
			nVert=4;
			nEdge=6;
			dim=3;
		}
		else if(type.equals("hexahedron") ){
			nVert=8;
			nEdge=12;
			dim=3;
		}
		this.vertexNumb=new int[nVert];
		this.edgeNumb=new int[nEdge];

		this.B=new Vect(dim);
		this.nu=new Vect().ones(dim);



		
	}

	public static void main(String[] args){


		Vect v2=new Vect(3);

		v2.show();

	}

	public void setJ(Vect J){
		this.J=J.deepCopy();
		this.hasJ=true;

	}
	public Vect getJ(){
		return this.J.deepCopy();

	}

	public void setM(Vect M){
		this.M=M.deepCopy();
		this.hasM=true;

	}
	
	public void setNu(Vect nu){

			this.nu=nu.deepCopy();
	}

	public Vect getM(){
		if(hasM)
		return this.M.deepCopy();
		else return new Vect(dim);

	}


	public void setB(Vect B){

		this.B=B;

	}
	

	public Vect getB(){
		return this.B.deepCopy();

	}


	public void setJe(Vect Je){
		this.Je=Je;

	}

	public Vect getJe(){
		return this.Je.deepCopy();

	}


	public Vect getNu(){
		return this.nu.deepCopy();

	}


	public void setRegion(int nr){
		this.nRegion=nr;

	}
	
	public int getRegion(){
		
		return this.nRegion;
	}

	public void setSigma(Vect sigma){
	
		if(sigma.norm()>0) {
			this.sigma=sigma.deepCopy();
			this.isConductor=true;
			Je=new Vect(3);
		}
		
		else
			this.isConductor=false;


	}


	public Vect getSigma(){
		return this.sigma.deepCopy();

	}
	
	public double getSigmaZ(){
		return this.sigma.el[0];

	}

	public void setPois(double  pois){
		this.pois=pois;

	}

	public double  getPois(){
		return this.pois;

	}

	public void setYng(double  yng){
		this.yng=yng;

	}
	
	public double getYng(){
		return this.yng;

	}

	public void setRo(double  ro){
		this.ro=ro;

	}

	public double getRo(){
		return this.ro;

	}

	public void setStress(Vect  stress){
		this.stress=stress.deepCopy();

	}
	
	public void setStress(Mat  T){
		for(int i=0;i<dim;i++)
		this.stress.el[i]=T.el[i][i];
		stress.el[dim]=T.el[0][1];
		if(dim==3){
			stress.el[4]=T.el[1][2];
			stress.el[5]=T.el[0][2];
		}

	}

	public Vect getStress(){
		if(deformable)
		return this.stress.deepCopy();
		else
			return new Vect(3*(dim-1));

	}
	
	public Mat getStressTensor(){

		if(stress==null) return new Mat(dim,dim);
		else
		return util.tensorize(stress);

	}
	
	public int getNvert(){
		
		return this.nVert;
	}
	
	public int getNedge(){
		
		return this.nEdge;
	}
	
	public int getDim(){
		
		return this.dim;
	}
	
	public String getShape(){
		return this.type;
	}
	
	public void setEdgeNumb(int[] ne){
		nEdge=ne.length;
		edgeNumb=new int[nEdge];
		for(int i=0;i<nEdge;i++){
			edgeNumb[i]=ne[i];
		}
	}
	
	public int[] getEdgeNumb(){
		int[] ne=new int[nEdge];
		for(int i=0;i<nEdge;i++)
			ne[i]=edgeNumb[i];
		return  ne;
	}
	
	public void setVertNumb(int[] nv){
		nVert=nv.length;
		 vertexNumb=new int[nVert];
		for(int i=0;i<nVert;i++)
			vertexNumb[i]=nv[i];
	}
	
	public int[] getVertNumb(){
		int[] nv=new int[nVert];
		for(int i=0;i<nVert;i++)
			nv[i]=vertexNumb[i];
		return  nv;
	}
	
	public void setEdgeNumb(int j, int ne){

			edgeNumb[j]=ne;
	}
	
	public int getEdgeNumb(int j){

		return  edgeNumb[j];
	}
	
	public void setVertNumb(int j, int nv){
		
			vertexNumb[j]=nv;
	}
	
	public int getVertNumb(int j){

		return  vertexNumb[j];
	}
	
	public void setHasJ(boolean b){
		this.hasJ=b;
	}
	public void setHasM(boolean b){
		this.hasM=b;
	}
	
	public void setHasMS(boolean b){
		this.MS=b;
	}
	
	public void setNonlin(boolean b){
		this.nonlin=b;
	}
	
	public void setDeformable(boolean b){
		this.deformable=b;
		if(b){

			stress=new Vect(3*(dim-1));
		}
		else{
			stress=null;
		}
	}
	public boolean hasJ(){
		return this.hasJ;
	}
	
	public boolean hasM(){
		return this.hasM;
	}
	public boolean hasMS(){
		return this.MS;
	}
	public boolean isDeformable(){
		return this.deformable;
	}
	
	public boolean isConductor(){
		return (this.isConductor || this.hasJ);
	}
	public boolean isNonlin(){
		return this.nonlin;
	}
	
}
