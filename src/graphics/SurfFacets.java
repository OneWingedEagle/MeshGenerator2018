package graphics;

import java.awt.Color;
import static java.lang.Math.*;
import math.Vect;
import math.util;
import fem.EdgeSet;
import fem.Model;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;

public class SurfFacets extends TransformGroup{
	
	private Shape3D surfFacets;
	private Shape3D surfEdges;
	int nElements,nFirst,nSurfEdges,elCode;
	private QuadArray facet;
	private TriangleArray facet3ang;
	private LineArray allEdge;
	public int[][] surfaceQuadNodes;
	private PolygonAttributes pa;

	public SurfFacets(){}

	public SurfFacets(Model model, int ir,Color color,double transp){
		color=Color.red;
		
		  
		pa=new PolygonAttributes();
		pa.setPolygonOffsetFactor(1.f);
		pa.setPolygonOffset(1e-12f);
		
		//double offFactor=-3e-4;
		this.elCode=model.elCode;
		if(this.elCode==0) setFacest3ang(model,ir,color,transp);
		else if(this.elCode==1) setFacestQuad(model,ir,color,transp);
		else{
		nFirst=model.region[ir].getFirstEl();
		nElements=model.region[ir].getLastEl()-nFirst+1;
		if(nElements>0){

			int nEdge=model.numberOfEdges;

		
		int[][] edgeElement=new int[nEdge+1][4];
		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			int[] edgeNumb=model.element[i].getEdgeNumb();
			for(int j=0;j<12;j++)
				edgeElement[edgeNumb[j]][j%4]=i;
				
		}


		int nx=0;
		int k;
		boolean[] onSurf=new boolean[nEdge+1];
		
		for(int i=1;i<=nEdge;i++){
			k=0;
			for(int j=0;j<4;j++)
				if(edgeElement[i][j]>0)
					k++;
				
			if(k<4 && k>0) 
			{
				onSurf[i]=true;
					nx++;
			}
				}

		
		nSurfEdges=nx;
		int[] onSurfEdgeNumber=new int[nx+1];
		nx=0;
		for(int i=1;i<=nEdge;i++)
			if(onSurf[i])
				onSurfEdgeNumber[++nx]=i;
				                                
		int[][] surfaceQuadNodes1=new int[1+2*nSurfEdges][4];
		int ix=0;		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
		
	
			int[] vertNumb=model.element[i].getVertNumb();
			int[] edgeNumb=model.element[i].getEdgeNumb();
				if((onSurf[edgeNumb[0]] && onSurf[edgeNumb[1]])
						|| (onSurf[edgeNumb[4]] && onSurf[edgeNumb[5]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[4];
					surfaceQuadNodes1[ix][1]=vertNumb[7];
					surfaceQuadNodes1[ix][2]=vertNumb[6];
					surfaceQuadNodes1[ix][3]=vertNumb[5];

			
					ix++;
				}
				
				
		 if((onSurf[edgeNumb[0]] && onSurf[edgeNumb[2]])
		 
						|| (onSurf[edgeNumb[8]] && onSurf[edgeNumb[9]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[3];
					surfaceQuadNodes1[ix][1]=vertNumb[2];
					surfaceQuadNodes1[ix][2]=vertNumb[6];
					surfaceQuadNodes1[ix][3]=vertNumb[7];
					ix++;
				}
				
				if((onSurf[edgeNumb[1]] && onSurf[edgeNumb[3]])
						|| (onSurf[edgeNumb[10]] && onSurf[edgeNumb[11]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[0];
					surfaceQuadNodes1[ix][1]=vertNumb[4];
					surfaceQuadNodes1[ix][2]=vertNumb[5];
					surfaceQuadNodes1[ix][3]=vertNumb[1];
					ix++;
				}
				
				if((onSurf[edgeNumb[2]] && onSurf[edgeNumb[3]])
						|| (onSurf[edgeNumb[6]] && onSurf[edgeNumb[7]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[0];
					surfaceQuadNodes1[ix][1]=vertNumb[1];
					surfaceQuadNodes1[ix][2]=vertNumb[2];
					surfaceQuadNodes1[ix][3]=vertNumb[3];
					ix++;
				}

					if((onSurf[edgeNumb[4]] && onSurf[edgeNumb[6]])
						|| (onSurf[edgeNumb[8]] && onSurf[edgeNumb[10]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[1];
					surfaceQuadNodes1[ix][1]=vertNumb[5];
					surfaceQuadNodes1[ix][2]=vertNumb[6];
					surfaceQuadNodes1[ix][3]=vertNumb[2];
					ix++;
				}
				
						if((onSurf[edgeNumb[5]] && onSurf[edgeNumb[7]])
						|| (onSurf[edgeNumb[9]] && onSurf[edgeNumb[11]]))
				{
					surfaceQuadNodes1[ix][0]=vertNumb[0];
					surfaceQuadNodes1[ix][1]=vertNumb[3];
					surfaceQuadNodes1[ix][2]=vertNumb[7];
					surfaceQuadNodes1[ix][3]=vertNumb[4];
					ix++;
				}
		}
		

	
		
		int nSurfQuads=ix;
		
		surfaceQuadNodes=new int[ix][4];
			   
		for(int i=0;i<ix;i++)
			for(int j=0;j<4;j++)
				surfaceQuadNodes[i][j]=surfaceQuadNodes1[i][j];

		surfaceQuadNodes1=null;
	facet = new QuadArray(4*nSurfQuads,  GeometryArray.COORDINATES | GeometryArray.NORMALS |QuadArray.COLOR_3);
		

	P3f[][] vertex=new P3f[nSurfQuads][4];
		
			V3f[][] normal=new V3f[nSurfQuads][4];
			 for(int i=0;i<nSurfQuads;i++){
				 Vect v1=model.node[surfaceQuadNodes[i][1]].getCoord().sub( model.node[surfaceQuadNodes[i][0]].getCoord());
				 Vect v2=model.node[surfaceQuadNodes[i][3]].getCoord().sub( model.node[surfaceQuadNodes[i][0]].getCoord());
				 Vect vn=v1.cross(v2);
				 vn.normalize();
				 V3f nOutward=new V3f(vn);
				 for(int j=0;j<4;j++){
					Vect v=model.node[surfaceQuadNodes[i][j]].getCoord();
					//Vect dvn=vn.times(offFactor);
					//v=v.add(dvn);
					 vertex[i][j]=new P3f(v);
				
				 normal[i][j]=nOutward;
				 }		
			 }
			 
			
			 
			 Color3f color3=new Color3f(color);

			 for(int i=0;i<nSurfQuads;i++){
				
				 for(int j=0;j<4;j++){
				 facet.setCoordinate(4*i+j,vertex[i][j]);
				facet.setNormal(4*i+j,normal[i][j]);
				facet.setColor(4*i+j,color3);

				}
			 }
	
			    
			 Appearance facetApp = new Appearance();
			 Appearance edgeApp = new Appearance();
		
			//  Color3f ambientColour =color3.;
			 	Color3f ambientColour = new Color3f(2f*color3.getX(), 2f*color3.getY(), 2f*color3.getZ());
			    Color3f emissiveColour = new Color3f(.1f, .1f, .1f);
			    Color3f specularColour = new Color3f(.1f, .1f, .1f);
			    Color3f diffuseColour =new Color3f(0f, 0f, 0f);//=new Color3f(2f*color3.getX(), 2f*color3.getY(), 2f*color3.getZ());
			    float shininess = 100.0f;
			
			    
			    Material material=new Material(ambientColour, emissiveColour,
				        diffuseColour, specularColour, shininess);
			    
			  facetApp.setMaterial(material);
			
   
	/*		  PolygonAttributes pa = new PolygonAttributes();
				pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
				pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
				pa.setCapability(PolygonAttributes.ALLOW_OFFSET_READ);
				pa.setCapability(PolygonAttributes.ALLOW_OFFSET_WRITE);
				pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
				pa.setPolygonMode(PolygonAttributes.CULL_FRONT);
				
				facetApp.setPolygonAttributes(pa);*/
			  
			   color3=new Color3f(.95f,.95f,.95f);			    
			   //color3=new Color3f(.3f,.3f,.3f);
			    
			 allEdge=new LineArray(2*nSurfEdges,LineArray.COORDINATES |LineArray.COLOR_3);
				
			    
				for(int i=0; i<nSurfEdges;i++)
				{
					allEdge.setCoordinate(2*i,new P3f(model.node[model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[0]].getCoord()));
					allEdge.setCoordinate(2*i+1,new P3f(model.node[model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[1]].getCoord()));
					allEdge.setColor(2*i,color3);
					allEdge.setColor(2*i+1,color3);
			
		}
				
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);

			    TransparencyAttributes t_attr1,t_attr2,t_attr3;
				 t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,.0f);
				 t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
				 t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	
				    
				edgeApp.setLineAttributes(la);
				if(!model.region[ir].getMaterial().startsWith("air")){
			
				facetApp.setTransparencyAttributes(t_attr1);
				edgeApp.setTransparencyAttributes(t_attr1);
				}
				else{
					
				facetApp.setTransparencyAttributes(t_attr2);
				edgeApp.setTransparencyAttributes(t_attr3);
			
				}
		
				
				facetApp.setPolygonAttributes(pa);
				
				edgeApp.setPolygonAttributes(pa);
				
			
				
			 surfFacets=new Shape3D(facet,facetApp);
			surfEdges=new Shape3D(allEdge,edgeApp);


		}
		
		}
	}
	
	
	public void setFacest3ang(Model model, int ir,Color color,double transp){
		double offset1=0;
		double offset2=0;
		double defScale=model.defScale;
		int nElVert=model.nElVert;
		nFirst=model.region[ir].getFirstEl();
		nElements=model.region[ir].getLastEl()-nFirst+1;
			
			P3f[][] vertex=new P3f[nElements][3];
		
			
			Vect[] v=new Vect[3];
			 int[] vertNumb;
			
			 int ix=0;
			for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
				{
				 vertNumb=model.element[i].getVertNumb();
				 
				 for(int j=0;j<nElVert;j++){
					 v[j]=model.node[vertNumb[j]].getCoord();
					 if(model.node[vertNumb[j]].u!=null)
							v[j]= v[j].add(model.node[vertNumb[j]].u.times(defScale));

					 vertex[ix][j]=new P3f(v[j].el[0],v[j].el[1],offset1);
				 }
				 ix++;
				
			 }
			 

			facet3ang = new TriangleArray(3*nElements,
					 GeometryArray.COORDINATES | GeometryArray.NORMALS |GeometryArray.COLOR_3);
		
			 Color3f cl3=new Color3f(color);
			 V3f normal=new V3f(0,0,1);
			 for(int i=0;i<nElements;i++){
				 for(int j=0;j<3;j++){
				 facet3ang.setCoordinate(3*i+j,vertex[i][j]);
				 facet3ang.setNormal(3*i+j,normal);
				 facet3ang.setColor(3*i+j,cl3);

				 }
				
				}
		
			 
			 Appearance facetApp = new Appearance();
			 Appearance edgeApp = new Appearance();
			 
			 Color3f color3=new Color3f(color);

						
			 int[][] xx=new int[0][0];
			 EdgeSet eds=new EdgeSet();
			 int[][] edgeNodes=eds.getEdgeNodesXY(model,ir, ir, xx);
			
			 nSurfEdges=edgeNodes.length-1;
				
			  color3=new Color3f(.75f,.95f,.95f);
			 // color3=new Color3f(.1f,.1f,.1f);
			  
			  allEdge=new LineArray(2*nSurfEdges,LineArray.COORDINATES |LineArray.COLOR_3);
				
			    
				for(int i=0; i<nSurfEdges;i++)
				{
					Vect v1=model.node[edgeNodes[i+1][0]].getCoord();
					Vect v2=model.node[edgeNodes[i+1][1]].getCoord();
					
					 if(model.node[edgeNodes[i+1][0]].u!=null)
							v1= v1.add(model.node[edgeNodes[i+1][0]].u.times(defScale));
					 if(model.node[edgeNodes[i+1][1]].u!=null)
							v2= v2.add(model.node[edgeNodes[i+1][1]].u.times(defScale));
					 
					allEdge.setCoordinate(2*i,new P3f(v1.el[0],v1.el[1],offset2));
					allEdge.setCoordinate(2*i+1,new P3f(v2.el[0],v2.el[1],offset2));
					allEdge.setColor(2*i,color3);
					allEdge.setColor(2*i+1,color3);
				}
				
				//  PolygonAttributes pa = new PolygonAttributes();
		/*			pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
					pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
					pa.setCapability(PolygonAttributes.ALLOW_OFFSET_READ);
					pa.setCapability(PolygonAttributes.ALLOW_OFFSET_WRITE);*/
			/*		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
					pa.setPolygonMode(PolygonAttributes.CULL_BACK);
					
					facetApp.setPolygonAttributes(pa);*/

				  TransparencyAttributes t_attr1,t_attr2,t_attr3;
					 t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,0f);
					 t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
					 t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	
					    
				
					if(!model.region[ir].getMaterial().startsWith("air")){
				
					facetApp.setTransparencyAttributes(t_attr1);
					edgeApp.setTransparencyAttributes(t_attr1);
					}
					else{
						
					facetApp.setTransparencyAttributes(t_attr2);
					edgeApp.setTransparencyAttributes(t_attr3);
				
					}
				LineAttributes la=new LineAttributes(1.3f,LineAttributes.PATTERN_SOLID,false);
				    
				edgeApp.setLineAttributes(la);
		
				surfFacets=new Shape3D(facet3ang,facetApp);
			 surfEdges=new Shape3D(allEdge,edgeApp);

  
		}
		
	
	public void setFacestQuad(Model model, int ir,Color color,double transp){
	
		double offset=-1e-4;
		double defScale=model.defScale;
	
		int nElVert=model.nElVert;
		nFirst=model.region[ir].getFirstEl();
		nElements=model.region[ir].getLastEl()-nFirst+1;
			
			P3f[][] vertex=new P3f[nElements][4];
		
			
			Vect[] v=new Vect[4];
			 int[] vertNumb;
			
			 int ix=0;
			for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
				{
				 vertNumb=model.element[i].getVertNumb();
				 
				 for(int j=0;j<nElVert;j++){
					 v[j]=model.node[vertNumb[j]].getCoord();
					 if(model.node[vertNumb[j]].u!=null)
						v[j]= v[j].add(model.node[vertNumb[j]].u.times(defScale));

					 vertex[ix][j]=new P3f(v[j].el[0],v[j].el[1],offset);
				 }
				 ix++;
				
			 }
			 

			facet = new QuadArray(4*nElements,
					 GeometryArray.COORDINATES | GeometryArray.NORMALS |QuadArray.COLOR_3);
		
			 
			V3f normal=new V3f(0,0,1);
			 Color3f cl3=new Color3f(color);
			 for(int i=0;i<nElements;i++){
				 for(int j=0;j<4;j++){
				 facet.setCoordinate(4*i+j,vertex[i][j]);
				 facet.setNormal(4*i+j,normal);
				 facet.setColor(4*i+j,cl3);
				 }
				}
			 
			 
			 Appearance facetApp = new Appearance();
			 Appearance edgeApp = new Appearance();
			 
			 Color3f color3=new Color3f(color);

			
			 int[][] xx=new int[0][0];
			 
			 EdgeSet eds=new EdgeSet();
			 int[][] edgeNodes=eds.getEdgeNodesXY(model,ir, ir, xx);

			 nSurfEdges=edgeNodes.length-1;
				
			//  color3=new Color3f(.75f,.95f,.95f);
			 color3=new Color3f(.25f,.25f,.25f);
			  
			  allEdge=new LineArray(2*nSurfEdges,LineArray.COORDINATES |LineArray.COLOR_3);
				
			    
				for(int i=0; i<nSurfEdges;i++)
				{

					Vect v1=model.node[edgeNodes[i+1][0]].getCoord();
					Vect v2=model.node[edgeNodes[i+1][1]].getCoord();
					
					 if(model.node[edgeNodes[i+1][0]].u!=null)
							v1= v1.add(model.node[edgeNodes[i+1][0]].u.times(defScale));
					 if(model.node[edgeNodes[i+1][1]].u!=null)
							v2= v2.add(model.node[edgeNodes[i+1][1]].u.times(defScale));
	
					allEdge.setCoordinate(2*i,new P3f(v1.el[0],v1.el[1],0));
					allEdge.setCoordinate(2*i+1,new P3f(v2.el[0],v2.el[1],0));
					allEdge.setColor(2*i,color3);
					allEdge.setColor(2*i+1,color3);
				}
				
				LineAttributes la=new LineAttributes(1.3f,LineAttributes.PATTERN_SOLID,false);
				    
				edgeApp.setLineAttributes(la);
				
				
				  TransparencyAttributes t_attr1,t_attr2,t_attr3;
					 t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,0f);
					 t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
					 t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	
					    
					if(!model.region[ir].getMaterial().startsWith("air")){
				
					facetApp.setTransparencyAttributes(t_attr1);
					edgeApp.setTransparencyAttributes(t_attr1);
					}
					else{
						
					facetApp.setTransparencyAttributes(t_attr2);
					edgeApp.setTransparencyAttributes(t_attr3);
				
					}
								
				surfFacets=new Shape3D(facet,facetApp);
				surfEdges=new Shape3D(allEdge,edgeApp);

  
		}
	
	
	public void show(boolean b){
		if(this.nElements==0) return;
		showFacets(b);
		showEdges(b);
	}
		
	public void showEdges(boolean b){
		if(this.nElements==0) return;
		
		if(b){
			if(surfEdges.getParent()==null)
			addChild(surfEdges);
		}
		else
		
			removeChild(surfEdges);
			
		}
	
	public void showFacets(boolean b){
		if(this.nElements==0) return;
		
		
		if(b){
			if(surfFacets.getParent()==null)
			addChild(surfFacets);
			}
		else
			removeChild(surfFacets);
			
		}
	
	public void setFaceColor(int i, Color color){
		if(this.nElements==0) return;
		
		if(this.elCode==0){
			facet3ang.setColor(i,new Color3f(color));
		}
		else
			facet.setColor(i,new Color3f(color));
		
		}
	
	public void setFaceColor3ang(Color color){
		Color3f color3=new Color3f(color);
		for(int i=0;i<nElements;i++)
			for(int j=0;j<3;j++){
				facet3ang.setColor(3*i+j,color3);
			}
		
	}
	
	public void setFaceColorQuad(Color color){
		Color3f color3=new Color3f(color);
		for(int i=0;i<nElements;i++)
			for(int j=0;j<4;j++){
				facet.setColor(4*i+j,color3);
			}
		
	}
	
	public void setFaceColor(Color color){
		if(this.nElements==0) return;
		if(elCode==0) {setFaceColor3ang(color); return;}
		else if(elCode==1) {setFaceColorQuad(color); return;}
			 
		Color3f color3=new Color3f(color);
		color3=new Color3f(2f*color3.getX(), 2f*color3.getY(), 2f*color3.getZ());
		
		 for(int i=0;i<surfaceQuadNodes.length;i++)
			 for(int j=0;j<4;j++)
			facet.setColor(4*i+j,color3);
	
		surfFacets.getAppearance().getMaterial().setAmbientColor(color3);
		surfFacets.getAppearance().getMaterial().setDiffuseColor(color3);
		
		}
	

	public void setEdgeColor(Color color){
		if(this.nElements==0) return;
		
		for(int i=0;i<2*this.nSurfEdges;i++)
		allEdge.setColor(i,new Color3f(color));
		
		
		}
	
	public void setEdgeColor(Color color,double transp){
		if(this.nElements==0) return;
		
		for(int i=0;i<2*this.nSurfEdges;i++)
		allEdge.setColor(i,new Color3f(color));
		TransparencyAttributes tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		surfEdges.getAppearance().setTransparencyAttributes( tr_attr);
		
		}
	
	public void setFaceTransparency(double transp){
		if(this.nElements==0) return;
		
		TransparencyAttributes tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		surfFacets.getAppearance().setTransparencyAttributes( tr_attr);
		
		}
	
	public void setEdgeTransparency(double transp){
		if(this.nElements==0) return;
		
		TransparencyAttributes tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		surfEdges.getAppearance().setTransparencyAttributes( tr_attr);
		
		}
	
	public void paintStress(Model model){
		paintStress(model,1);
	}
	
	public void paintStress(Model model, double pw){

		if(this.nElements==0) return;
		
		if(model.dim==2){ paintPlaneStress(model,pw); return;}
		
		double strMax=pow(model.nodalStressMax,pw);
		ColorBar cBar=new ColorBar(0,strMax);

		for(int i=0;i<surfaceQuadNodes.length;i++)
			for(int j=0;j<4;j++){

				double colorValue=pow(model.node[surfaceQuadNodes[i][j]].stress,pw);
				setFaceColor(4*i+j,cBar.getColor(colorValue));
			}

		//surfFacets.getAppearance().setMaterial( new Material());
		
		}

	
	

	public void paintPlaneStress(Model model,double pw){
		if(this.nElements==0) return;
		
		double strMax=signum(model.nodalStressMax)*pow(abs(model.nodalStressMax),pw);
		double strMin=signum(model.nodalStressMin)*pow(abs(model.nodalStressMin),pw);


		int nElVert=model.nElVert;
		int[] vertNumb;
		ColorBar cBar=new ColorBar(strMin,strMax);

		for(int i=0;i<nElements;i++){
			vertNumb=model.element[i+nFirst].getVertNumb();
			for(int j=0;j<nElVert;j++){
				//double colorValue=pow(model.node[vertNumb[j]].stress,pw);
				double sn=model.node[vertNumb[j]].stress;
				double colorValue=signum(sn)*pow(abs(sn),pw);
				setFaceColor(nElVert*i+j,cBar.getColor(colorValue));
				
			}
			
	}
		
		
		}
	

	
	public void paintDisp(Model model){

		if(this.nElements==0) return;
		double pw=1;
		double uMax=pow(model.uMax,pw);
		int nElVert=model.nElVert;
		int[] vertNumb;
		ColorBar cBar=new ColorBar(-.2*uMax,uMax);
		int m=0;
		if(model.elCode<2) m=nElVert;
		else if(model.elCode==4) m=4;

		for(int i=0;i<nElements;i++){
			vertNumb=model.element[i+nFirst].getVertNumb();
			for(int j=0;j<m;j++){

				double colorValue=pow(model.node[vertNumb[j]].u.norm(),pw);
				setFaceColor(m*i+j,cBar.getColor(colorValue));
				
			}
		}
			
	}



}
