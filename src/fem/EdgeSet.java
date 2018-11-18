package fem;

import math.util;

public class EdgeSet {

	public EdgeSet(){}

	public void setEdge(Model model){

		if(model.dim==2) {setEdge2D(model); return;}

		byte[][] edgeLocalNodes={{6,7},{5,4},{2,3},{1,0},{6,5},{7,4},{2,1},{3,0},{6,2},{7,3},{5,1},{4,0}};

		int[][][] nodeNode=new int[2][model.numberOfNodes+1][5*model.nBoundary];
		byte[] nodeNodeIndex=new byte[model.numberOfNodes+1];
		int nEdge=0;
		int n1,n2,m;
		int[][] edgeNodes=new int[20*model.numberOfNodes+1][2];

		for(int i=1;i<=model.numberOfElements;i++){
			int[] vertNumb=model.element[i].getVertNumb();
			for(int j=0;j<model.nElEdge;j++){
				n1=vertNumb[edgeLocalNodes[j][0]];
				n2=vertNumb[edgeLocalNodes[j][1]];
				if(n2<n1) { int tmp=n1; n1=n2; n2=tmp;}
				m=util.search(nodeNode[0][n1],n2);

				if(m<0){

					nEdge++;
					edgeNodes[nEdge][0]=n1;
					edgeNodes[nEdge][1]=n2;
					nodeNode[0][n1][nodeNodeIndex[n1]]=n2;
					nodeNode[1][n1][nodeNodeIndex[n1]++]=nEdge;
					model.element[i].setEdgeNumb(j,nEdge);
				}
				else
					model.element[i].setEdgeNumb(j,nodeNode[1][n1][m]);

			}

		}

		model.numberOfEdges=nEdge;

		model.edge=new Edge[model.numberOfEdges+1];

		for(int i=1;i<=nEdge;i++){
			model.edge[i]=new Edge(edgeNodes[i][0],edgeNodes[i][1]);
			model.edge[i].setLength(model.edgeLength(i));
		}
		byte b0=0,b1=1,b2=2;
		for(int i=1;i<=model.numberOfElements;i++){
			int[] edgeNumb=model.element[i].getEdgeNumb();
			for(int j=0;j<model.nElEdge;j++)
				if(j<4)
					model.edge[edgeNumb[j]].setDirection(b0);
				else if(j<8)
					model.edge[edgeNumb[j]].setDirection(b1);
				else
					model.edge[edgeNumb[j]].setDirection(b2);
		}

		model.setMinEdge();
		model.setMaxEdge();
	}

	public void setEdge2D(Model model){

		int ix=0;
		int[] nodeCount=new int[model.numberOfNodes+1];
		for(int i=1;i<=model.numberOfElements;i++){
			int[] vert=model.element[i].getVertNumb();
			for(int j=0;j<model.nElVert;j++)	{	
				if(nodeCount[vert[j]]==0)	{	
				nodeCount[model.element[i].getVertNumb(j)]=++ix;
			}
			}
		}

		model.numberOfEdges=ix;

		model.edge=new Edge[model.numberOfEdges+1];
	
		ix=0;
		for(int i=1;i<=model.numberOfNodes;i++){
			int k=nodeCount[i];
			if(k!=0){	
				model.edge[k]=new Edge(i,i);
				model.edge[k].setLength(1.0);
				}
				
			}
		

		for(int i=1;i<=model.numberOfElements;i++){
			int[] vertNumb=model.element[i].getVertNumb();
			for(int j=0;j<model.nElVert;j++){
				model.element[i].setEdgeNumb(j,nodeCount[vertNumb[j]]);
			}

		}

	 byte b2=2;
		for(int i=1;i<=model.numberOfEdges;i++){
			model.edge[i].setDirection(b2);
		}
		
}


	public int[][] getEdgeNodesXY(Model model,int[][] elEdge ){
		return getEdgeNodesXY(model, 1,model.numberOfRegions,elEdge);
	}

	public int[][] getEdgeNodesXY(Model model,int ir1, int ir2,int[][] elEdge ){
		byte[][] arr3={{0,1},{1,2},{0,2}};
		byte[][] arr4={{1,2},{0,3},{2,3},{1,0}};
		byte[][] edgeLocalNodes=new byte[1][1];
		int cc=2;
		if(model.elCode==0){
			edgeLocalNodes=arr3;
			cc=10;
		}
		else if(model.elCode==1){
			edgeLocalNodes=arr4;
		}

		boolean b=true;
		if(elEdge.length==0) b=false;

		int[][][] nodeNode=new int[2][model.numberOfNodes+1][10*model.nBoundary];
		byte[] nodeNodeIndex=new byte[model.numberOfNodes+1];
		int nEdge=0;
		int n1,n2,m;
		int[][] edgeNodes=new int[cc*model.numberOfNodes+1][2];

		for(int ir=ir1;ir<=ir2;ir++)
			for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
				int[] vertNumb=model.element[i].getVertNumb();
				for(int j=0;j<model.nElEdge;j++){
					n1=vertNumb[edgeLocalNodes[j][0]];
					n2=vertNumb[edgeLocalNodes[j][1]];
					if(n2<n1) { int tmp=n1; n1=n2; n2=tmp;}
					m=util.search(nodeNode[0][n1],n2);

					if(m<0){
						nEdge++;
						edgeNodes[nEdge][0]=n1;
						edgeNodes[nEdge][1]=n2;
						nodeNode[0][n1][nodeNodeIndex[n1]]=n2;
						nodeNode[1][n1][nodeNodeIndex[n1]++]=nEdge;
						if(b)
							elEdge[i][j]=nEdge;						
					}
					else{
						if(b)
							elEdge[i][j]=nodeNode[1][n1][m];
					}

				}


			}

		int[][] edgeNodes2=new int[nEdge+1][2];
		double min=1e40;
		double max=0;
		for(int i=1;i<=nEdge;i++){
			edgeNodes2[i]=edgeNodes[i].clone();
			double length=model.node[edgeNodes2[i][0]].getCoord().sub(model.node[edgeNodes2[i][1]].getCoord()).norm();

			if(max<length) max=length;
			if(min>length) min=length;
		}

		model.minEdgeLength=min;
		model.maxEdgeLength=max;
		return edgeNodes2;
	}



}
