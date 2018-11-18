package fem;

public class Edge {
	public byte direction;
	public double  length,Au,prevAu,J0,Jms;
	public int[] endNodeNumber=new int[2];
	public boolean edgeKnown,hasJ,PBC,common;
	public int map;
	
	public Edge(int n1,int n2)
	{

	if(n1<n2){
	endNodeNumber[0]=n1;
	endNodeNumber[1]=n2;
	}
	else{
		endNodeNumber[0]=n2;
		endNodeNumber[1]=n1;
	}

	}
	
	public void setKnownA(double Au){
		edgeKnown=true;
		this.Au=Au;

	}
	
	public void setSolvedAL(double Au){
		this.prevAu=this.Au;
		this.Au=Au;
	}
	
	public void setJL(double Ju){
		hasJ=true;
		this.J0=Ju*length;
	}

	public void setLength(double length){
				
		this.length=length;
	}
	
	public void setDirection(byte i){
		
		this.direction=i;
	}
	
	public double getDiffAu(){
		return Au-prevAu;
	}

	public void setAu(double Au) {

		this.Au=Au;
		
	}
	
	public double getAu() {

		return this.Au;
		
	}

}
