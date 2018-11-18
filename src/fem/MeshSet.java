package fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import math.Vect;
import drawingPanel.GUI;

public class MeshSet {
	private EdgeSet edgeSet=new EdgeSet();
	

	public void setMesh(Model model, GUI gui){

		//**************************
		Discretizer discretizer=new Discretizer(gui);

		int I,J,K;
		double[] X=discretizer.X;
		double[] Y=discretizer.Y;
		double[] Z=discretizer.Z;

		I=X.length; J=Y.length; K=Z.length;

		model.numberOfNodes=I*J*K;   
		model.numberOfElements=(I-1)*(J-1)*(K-1); 
		model.node=new Node[model.numberOfNodes+1];
		model.element=new Element[model.numberOfElements+1];
		for(int i=1;i<=model.numberOfElements;i++){
			model.element[i]=new Element("hexahedron");
		}
		for(int i=1;i<=model.numberOfNodes;i++)
			model.node[i]=new Node(model.dim);

		int nodeNumber=0;
		for(int k=0;k<K;k++)
			for(int j=0;j<J;j++)
				for(int i=0;i<I;i++)
				{
					nodeNumber++;
					model.node[nodeNumber].setCoord(new Vect(X[i],Y[j],Z[k]));	
				}
		int elementNumber,Nx,Nxy;

			
		Nx=I;
		Nxy=I*J;
		elementNumber=0;

		int[][] elementVertex1=new int[model.numberOfElements+1][8];
		for(int k=0;k<K-1;k++)
			for(int j=0;j<J-1;j++)
				for(int i=0;i<I-1;i++){
					elementNumber++;
					elementVertex1[elementNumber][0]=(k+1)*Nxy+(j+1)*Nx+i+2;
					elementVertex1[elementNumber][1]=(k+1)*Nxy+(j+1)*Nx+i+1;
					elementVertex1[elementNumber][2]=(k+1)*Nxy+j*Nx+i+1;
					elementVertex1[elementNumber][3]=(k+1)*Nxy+j*Nx+i+2;
					elementVertex1[elementNumber][4]=k*Nxy+(j+1)*Nx+i+2;
					elementVertex1[elementNumber][5]=k*Nxy+(j+1)*Nx+i+1;
					elementVertex1[elementNumber][6]=k*Nxy+j*Nx+i+1;
					elementVertex1[elementNumber][7]=k*Nxy+j*Nx+i+2;
					

				}

		Vect centerOfMass;
		
		int[] elementBlock=new int[model.numberOfElements+1];
		for(int i=1;i<=model.numberOfElements;i++){
			centerOfMass=new Vect(3);
			for(int v=0;v<8; v++)
				centerOfMass=centerOfMass.add(model.node[elementVertex1[i][v]].getCoord());
			centerOfMass=centerOfMass.times(.125);
			for(int ir=1;ir<=model.nBlocks;ir++)
				if(discretizer.blockBoundary[ir][0]<centerOfMass.el[0])
					if(centerOfMass.el[0]<discretizer.blockBoundary[ir][1])
						if(discretizer.blockBoundary[ir][2]<centerOfMass.el[1])
							if(centerOfMass.el[1]<discretizer.blockBoundary[ir][3])
								if(discretizer.blockBoundary[ir][4]<centerOfMass.el[2])
									if(centerOfMass.el[2]<discretizer.blockBoundary[ir][5]){
										elementBlock[i]=ir;


									}
		} 
		



		int nRegX=model.nBlocks+1;
		int[] nBlockElements=new int[nRegX+1];
		Region[] regionx=new Region[nRegX+1];
		
		int[]  mapElement=new int[model.numberOfElements+1];
		
		elementNumber=0;
		
		for(int ir=1;ir<nRegX;ir++)					
			for(int i=1;i<=model.numberOfElements;i++)
				if(elementBlock[i]==ir)
					mapElement[++elementNumber]=i;

		for(int i=1;i<=model.numberOfElements;i++)
			if(elementBlock[i]==0)
				mapElement[++elementNumber]=i;
		
		
		for(int ir=1;ir<=nRegX;ir++)
			regionx[ir]=new Region(model.dim);
		
		regionx[1].setFirstEl(1);
		
		for(int ir=1;ir<nRegX;ir++){
			if(ir>1) regionx[ir].setFirstEl(regionx[ir-1].getLastEl()+1);
			for(int i=1;i<=model.numberOfElements;i++)
				if(elementBlock[i]==ir)
					nBlockElements[ir]++;
			regionx[ir].setLastEl(regionx[ir].getFirstEl()+nBlockElements[ir]-1);
			regionx[ir].setName(gui.drp.blockName[ir]);	
			regionx[ir].setMaterial(gui.drp.blockMaterial[ir]);	
		}
		
		regionx[nRegX].setFirstEl(regionx[nRegX-1].getLastEl()+1);
		for(int i=1;i<=model.numberOfElements;i++)
			if(elementBlock[i]==0)
				nBlockElements[nRegX]++;
		regionx[nRegX].setLastEl(regionx[nRegX].getFirstEl()+nBlockElements[nRegX]-1);
		regionx[nRegX].setName(gui.drp.blockName[0]);	
		regionx[nRegX].setMaterial(gui.drp.blockMaterial[0]);
		
		

		List<String> list1=new ArrayList<String>();
		for(int i=1;i<=nRegX;i++){
				list1.add(regionx[i].getName());
		}
	
			Set<String> set = new HashSet<String>(list1);
			
			ArrayList<String> regName = new ArrayList<String>(set);
		
			int nair=0;
	
			model.numberOfRegions=regName.size();
			
			for(int i=0; i<model.numberOfRegions; i++){
				if(regName.get(i).startsWith("air"))
					nair++;
			}
			
			String[] sr1=new String[model.numberOfRegions-nair];
			String[] sr2=new String[nair];
			
			int i1=0,i2=0;
			for(int i=0; i<model.numberOfRegions; i++)
				if(!regName.get(i).startsWith("air"))
				sr1[i1++]=regName.get(i);
				else
				sr2[i2++]=regName.get(i);
				
			Arrays.sort(sr1);
			Arrays.sort(sr2);
			int ix=0;
			
			String[] sortedRegions=new String[model.numberOfRegions];
				for(int i=0; i<sr1.length; i++)
					if(sr1[i]!=null)
						sortedRegions[ix++]=sr1[i];
				for(int i=0; i<sr2.length; i++)
					if(sr2[i]!=null)
						sortedRegions[ix++]=sr2[i];
			

			int[][] br=new int[model.numberOfRegions+1][nRegX];
			for(int ir=1; ir<=model.numberOfRegions; ir++){
				 ix=0;
				for(int ib=1; ib<=nRegX;ib++){
					if(list1.get(ib-1).equals(sortedRegions[ir-1])){
							br[ir][ix++]=ib;
							
					}
				}
			
			}

			model.region=new Region[model.numberOfRegions+1];
			 
			int elNumb=0;
			for(int ir=1; ir<=model.numberOfRegions; ir++){
				 model.region[ir]=new Region(model.dim);

				model.region[ir].setFirstEl(elNumb+1);
				for(int ib=0; (ib<nRegX && br[ir][ib]!=0) ;ib++){
					for(int i=regionx[br[ir][ib]].getFirstEl();i<=regionx[br[ir][ib]].getLastEl();i++)
				{		elNumb++;

						model.element[elNumb].setVertNumb(elementVertex1[mapElement[i]]);

				}	
				}
				

				model.region[ir].setLastEl(elNumb);
			}
			

		
			for(int ir=1; ir<=model.numberOfRegions; ir++)
			{
			
				int i=br[ir][0];
				if(i==nRegX) i=0;

				model.region[ir].setName(sortedRegions[ir-1]);
				
				model.region[ir].setMaterial(gui.drp.blockMaterial[i]);
				model.region[ir].setMur(gui.drp.mur[i]);
				model.region[ir].setSigma(gui.drp.sigma[i]);

				model.region[ir].setJ(gui.drp.J[i]);

				if(gui.drp.hasMag[i])
					model.region[ir].setM(gui.drp.M[i]);

				if(gui.drp.linearBH[i])
					model.region[ir].setNonLinear(false);
				else
					model.region[ir].setNonLinear(true);

				model.region[ir].setRo(model.matData.getRo(model.region[ir].getMaterial()));

			}
			
		
			
		for(int ir=1;ir<=model.numberOfRegions;ir++)
			for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
				model.element[i].setRegion(ir);

		

	}

}
