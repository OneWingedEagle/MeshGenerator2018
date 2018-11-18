package graphics;
import java.awt.Color;
import java.util.Arrays;

import math.Vect;
import math.util;
import fem.Model;


import javax.media.j3d.Appearance;

import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;

import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Sphere;

import static java.lang.Math.*;

public class HexaHedron extends TransformGroup{
	Vect centerMass=new Vect(3);
	 IndexedQuadArray body;
	 LineArray edge;
	 Shape3D facets;
	 Appearance app1,app2,app3;
	 Shape3D edge12;
	 Shape3D allSurfEdges;
	 Vect[] vertex;;
	 Vect J;
	 TransformGroup arrowJ=new TransformGroup();	
	 double[] boundary;

	public HexaHedron(){}
	 
	 public HexaHedron(double[] bound,double scale){

		 Vect[] elVertex=new Vect[8];
		 elVertex[0]=new Vect(bound[1],bound[3],bound[5]);
		 elVertex[1]=new Vect(bound[0],bound[3],bound[5]);
		 elVertex[2]=new Vect(bound[0],bound[2],bound[5]);
		 elVertex[3]=new Vect(bound[1],bound[2],bound[5]);
		 elVertex[4]=new Vect(bound[1],bound[3],bound[4]);
		 elVertex[5]=new Vect(bound[0],bound[3],bound[4]);
		 elVertex[6]=new Vect(bound[0],bound[2],bound[4]);
		 elVertex[7]=new Vect(bound[1],bound[2],bound[4]);
		 
		 setUp(elVertex,scale);
		 
		 boundary=new double[6];
		 for(int i=0;i<6;i++)
			 boundary[i]=bound[i]/scale;
	 }
	 
	 HexaHedron(Vect[] elVertex,double scale){
		 setUp(elVertex,scale);
	 }
	 
	 public void setUp(Vect[] elVertex,double scale){
		 Color3f color=new Color3f(Color.red);
		int Nvert=elVertex.length;
		vertex=new Vect[Nvert];
		 for(int i=0;i<8;i++)
			 vertex[i]=elVertex[i];
		Vect[] vert=new Vect[Nvert];
		for(int i=0;i<Nvert;i++)
			vert[i]=vertex[i].times(1.0/scale);
		for(int i=0;i<Nvert;i++)
		 centerMass= centerMass.add(vert[i]);	
		 centerMass= centerMass.times(1.0/(Nvert));

		 P3f[] vertP3f=new P3f[Nvert];
		 for(int i=0;i<Nvert;i++)
			 vertP3f[i]=new P3f(vert[i]);
		 body = new IndexedQuadArray(8,
			        IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
			
			    Vector3f[] normals = {
			    		new Vector3f(0.0f, 0.0f, -1.0f),
			        new Vector3f(0.0f, 0.0f, 1.0f),
			        new Vector3f(-1.0f, 0.0f, 0.0f),
			        new Vector3f(1.0f, 0.0f, 0.0f),
			        new Vector3f(0.0f, -1.0f, 0.0f),
			        new Vector3f(0.0f, 1.0f, 0.0f) };

			    int coordIndices[] = { 0, 1, 2, 3, 7, 6, 5, 4, 0, 3, 7, 4, 5, 6, 2, 1,
				        0, 4, 5, 1, 6, 7, 3, 2 };
			    
			 int normalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
				        4, 4, 4, 4, 5, 5, 5, 5 };


			    body.setCoordinates(0, vertP3f);
			    body.setNormals(0, normals);
			    body.setCoordinateIndices(0, coordIndices);
			    body.setNormalIndices(0, normalIndices);
			    app1 = new Appearance();
    
			    Color3f ambientColour =color;
			    Color3f emissiveColour = new Color3f(.0f, .0f, .0f);
			    Color3f specularColour = new Color3f(.2f, .2f, .2f);
			    Color3f diffuseColour =color;
			    float shininess = 1.0f;
			    
			    Material material=new Material(ambientColour, emissiveColour,
				        diffuseColour, specularColour, shininess);
			    
			    app1.setMaterial(material);
			  
			    TransparencyAttributes t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,0f);
			    app1.setTransparencyAttributes(t_attr1);
			    
			    TransparencyAttributes t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.8f);
			  
			    app2 = new Appearance();
			    app2.setMaterial(material);
			    app2.setTransparencyAttributes(t_attr2);
			  
	    		facets=new Shape3D();
	    	
	    		facets.addGeometry(body);
			    facets.setAppearance(app1);
	
		
		
		edge=new LineArray(24, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		

		edge.setCoordinate(0,vertP3f[0]);
		edge.setCoordinate(1,vertP3f[1]);
		edge.setCoordinate(2,vertP3f[0]);
		edge.setCoordinate(3,vertP3f[3]);
		edge.setCoordinate(4,vertP3f[0]);
		edge.setCoordinate(5,vertP3f[4]);
		edge.setCoordinate(6,vertP3f[1]);
		edge.setCoordinate(7,vertP3f[2]);
		edge.setCoordinate(8,vertP3f[1]);
		edge.setCoordinate(9,vertP3f[5]);
		edge.setCoordinate(10,vertP3f[2]);
		edge.setCoordinate(11,vertP3f[3]);
		
		edge.setCoordinate(12,vertP3f[2]);
		edge.setCoordinate(13,vertP3f[6]);
		edge.setCoordinate(14,vertP3f[3]);
		edge.setCoordinate(15,vertP3f[7]);
		edge.setCoordinate(16,vertP3f[4]);
		edge.setCoordinate(17,vertP3f[5]);
		edge.setCoordinate(18,vertP3f[4]);
		edge.setCoordinate(19,vertP3f[7]);
		edge.setCoordinate(20,vertP3f[5]);
		edge.setCoordinate(21,vertP3f[6]);
		edge.setCoordinate(22,vertP3f[6]);
		edge.setCoordinate(23,vertP3f[7]);
	
		
		for(int i=0;i<24;i++)
		edge.setColor(i,color);
		
		 
		LineAttributes la=new LineAttributes(1.2f,LineAttributes.PATTERN_SOLID,false);
		Appearance app4=new Appearance();
		app4.setTransparencyAttributes(t_attr2);
		app4.setLineAttributes(la);

		edge12=new Shape3D(edge,app4);

	 }
	

	public void setFaceColor(Color color){
		facets.getAppearance().getMaterial().setAmbientColor(new Color3f(color));
		facets.getAppearance().getMaterial().setDiffuseColor(new Color3f(color));
	}
	
	public void setEdgeColor(Color color){
		Color3f color3=new Color3f(color);
		for(int i=0;i<24;i++)
			edge.setColor(i,color3);
		
	}
	public void setEdgeWidth(double width){
		edge12.getAppearance().getLineAttributes().setLineWidth((float)width);
		
	}
	public void setEdgeTransp(double transp){
		edge12.getAppearance().getTransparencyAttributes().setTransparency((float)transp);
		
	}
	public void showEdges(boolean b){
		if(b){
			if(edge12.getParent()==null)
			addChild(edge12);}
		else
			removeChild(edge12);
			
		}
	public void showFacets(boolean b){
		if(b){
			if(facets.getParent()==null)
			addChild(facets);
		}
		else
			removeChild(facets);
			
		}

	public void setJ(Vect J,double Jscale,Color color){
		this.J=new Vect(3);
		this.J=J;
		PyramidArrow arJ=new PyramidArrow(centerMass,J,Jscale,color);
		arrowJ.addChild(arJ);
	}

	public void showJ(boolean b){
		if(b){
			showFacets(false);
			showEdges(true);
			addChild(arrowJ);
		}
		else{
			
			removeChild(arrowJ);
			showFacets(true);
			showEdges(false);
		}
		}
	
	public void setTransp(double transpar){
		if(transpar==0)
	facets.setAppearance(app1);
		else
		facets.setAppearance(app2);
		facets.getAppearance().getTransparencyAttributes().setTransparency((float)transpar);

	}



}
