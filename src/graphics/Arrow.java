package graphics;
import math.Vect;
import math.util;

import java.awt.Color;

import javax.media.j3d.Appearance;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;

import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3f;


import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

import static java.lang.Math.*;


public class Arrow extends TransformGroup {
	double scale;	
	public Arrow(int dim,Vect P,Vect v,double scale,Color color,int mode){
		 this.scale=scale;


		if(dim==3){
			if(mode==-1)
			setArrowm1(P,v,scale,color);
			else if(mode==0)
			setArrow0(P,v,scale,color);
			else if(mode==1)
			setArrow1(P,v,scale,color);
		}
		else if(mode==-1)
			setArrow2Dm1(P,v,scale,color);
		else if(mode==0)
			setArrow2D0(P,v,scale,color);
		else if(mode==1)
			setArrow2D1(P,v,scale,color);
			
	
	}
	
	
	public void setArrowm1(Vect P,Vect v,double scale,Color color){
		double base=.2*scale;
		P3f[] vertex=new P3f[4];
		vertex[0]=new P3f(-base/2,-base/2,0);
		vertex[1]=new P3f(base/2,-base/2,0);
		vertex[2]=new P3f(0,base/2,0);
		vertex[3]=new P3f(0,0,scale);

		LineArray edge=new LineArray(12, GeometryArray.COORDINATES | GeometryArray.COLOR_3);


				edge.setCoordinate(0,vertex[0]);
				edge.setCoordinate(1,vertex[1]);
				edge.setCoordinate(2,vertex[1]);
				edge.setCoordinate(3,vertex[2]);
				edge.setCoordinate(4,vertex[2]);
				edge.setCoordinate(5,vertex[0]);
				edge.setCoordinate(6,vertex[0]);
				edge.setCoordinate(7,vertex[3]);
				edge.setCoordinate(8,vertex[1]);
				edge.setCoordinate(9,vertex[3]);
				edge.setCoordinate(10,vertex[2]);
				edge.setCoordinate(11,vertex[3]);
				
			Color3f color3f=new Color3f(color);
				for(int i=0;i<12;i++)
					edge.setColor(i,color3f);
				    Shape3D pyrShape=new Shape3D(edge);
					    addChild(pyrShape);
					   
					    Transform3D trans=new Transform3D();


						Matrix3d M =util.mat3d(v,new Vect(0,0,1));	   
						trans.setRotation(M);

				trans.setTranslation(new V3f(P));		  
				setTransform(trans);
					  
		 }
	
	public void setArrow0(Vect P,Vect v,double scale,Color color){double base=.1*scale;
	
	IndexedQuadArray pyramid = new IndexedQuadArray(8,
		        IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
		    P3f[] cubeCoordinates = { 
		    	new P3f(0,.0,scale),
		    	new P3f(0,0,scale),
		    	new P3f(0,-0,scale),
		    	new P3f(0,0,scale),
		    	new P3f(base,base,0),
		    	new P3f(-base,base,0),
		    	new P3f(-base,-base,0),
		    	new P3f(base,-base,0),
		    	};
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


		    pyramid.setCoordinates(0, cubeCoordinates);
		 pyramid.setNormals(0, normals);
		    pyramid.setCoordinateIndices(0, coordIndices);
		 pyramid.setNormalIndices(0, normalIndices);
		    Appearance app = new Appearance();
		    
		    Color3f color3=new  Color3f(color);
		    Color3f ambientColour =color3;
		    Color3f emissiveColour = new Color3f(.1f*color3.getX(), .1f*color3.getY(), .1f*color3.getY());
		    Color3f specularColour = new Color3f(.2f, .2f, .2f);
		    Color3f diffuseColour = new Color3f(.2f, .2f, .2f);
		    float shininess = 1.0f;
		    
		    app.setMaterial(new Material(ambientColour, emissiveColour,
		        diffuseColour, specularColour, shininess));
		    
		    TransparencyAttributes tAtt =new TransparencyAttributes(TransparencyAttributes.NONE,0);
		    app.setTransparencyAttributes(tAtt);
		    
		    Shape3D pyrShape=new Shape3D(pyramid,app);
		   
		    addChild(pyrShape);
		   
			Transform3D trans=new Transform3D();


			Matrix3d M =util.mat3d(v,new Vect(0,0,1));	   
			trans.setRotation(M);

	trans.setTranslation(new V3f(P));		  
	setTransform(trans);
		  }

	
	public void setArrow1(Vect P,Vect v,double scale,Color color){
	
		double radius=0,height=0,coneHeight=0,coneRadius;

			height=scale;
			radius=.05*scale;
			coneHeight=.4*height;
			coneRadius=2*radius;
			double hh=.5*height;

		  Color3f color3=new  Color3f(color);
		
		    Color3f emissiveColour = new Color3f(.1f*color3.getX(), .04f*color3.getY(), .04f*color3.getZ());
		    Color3f specularColour = new Color3f(.3f, .3f, .3f);
		    Color3f diffuseColour =new Color3f(.5f, .5f, .5f);
		    float shininess = 1.0f;
		    
		    Material material=new Material(color3, emissiveColour,
			        diffuseColour, specularColour, shininess);

		Appearance app=new Appearance();
		app.setMaterial(material);
		Cone cone=new Cone() ;

			cone =new Cone((float)(coneRadius),(float)(coneHeight),app);
		TransformGroup tgCone=new TransformGroup();

		Transform3D transCone=new Transform3D();

		transCone.rotX(PI/2);
		

		transCone.setTranslation(new V3f(0,0,(height+coneHeight/2)));	

		tgCone.setTransform(transCone);
		tgCone.addChild(cone);
		
		Cylinder axis=new Cylinder();

			axis=new Cylinder((float)(radius),(float)(height),app);
		TransformGroup tgAx=new TransformGroup();
		Transform3D transAx=new Transform3D();
		transAx.rotX(PI/2);
		

			transAx.setTranslation(new V3f(0,0,hh));	
			
			tgAx.setTransform(transAx);
			
			tgAx.addChild(axis);
		
			addChild(tgCone);
			
			addChild(tgAx);

			Transform3D trans=new Transform3D();


					Matrix3d M =util.mat3d(v,new Vect(0,0,1));	   
					trans.setRotation(M);

			trans.setTranslation(new V3f(P));		  
			setTransform(trans);

	}
	
	public void setArrow2D0(Vect P,Vect v,double scale,Color color){
		Color3f color3=new Color3f(color);
		Color3f color31=new Color3f(color.darker());
	double base=0,height=0;


	base=.05*scale;
	height=.4*scale;
	
	TriangleArray  nail=new TriangleArray(3,TriangleArray.COORDINATES | TriangleArray.NORMALS |TriangleArray.COLOR_3);
	P3f[] vert=new P3f[3];
	V3f normal=new V3f(0,0,1);


		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-base,0,0);
		vert[2]=new P3f(base,0,0);
		
	
	for(int i=0;i<3;i++){
		nail.setCoordinate(i,vert[i]);
		if(i==0)
		nail.setColor(i,color31);
		else
		nail.setColor(i,color3);
		nail.setNormal(i,normal);
	}
	
	
		Shape3D shpHead=new Shape3D(nail);
		
		TransformGroup arrow=new TransformGroup();
	
		arrow.addChild(shpHead);


	Transform3D trans=new Transform3D();

	trans.setRotation(util.mat3d(v,new Vect(0,1)));
	trans.setTranslation(new V3f(P.v3()));

	addChild(arrow);
	setTransform(trans);

}
	
	public void setArrow2D1(Vect P,Vect v,double scale,Color color){

		Color3f color3=new Color3f(color);
		Color3f color31=new Color3f(color.darker());
	double base=0,height=0,hh;
		base=.3*scale/(1+5*scale);
		height=scale;
		hh=1.3*base;

	TriangleArray  head=new TriangleArray(3,TriangleArray.COORDINATES | TriangleArray.NORMALS |TriangleArray.COLOR_3);
	QuadArray  tail=new QuadArray(4,QuadArray.COORDINATES | QuadArray.NORMALS |QuadArray.COLOR_3);
	P3f[] vert=new P3f[7];
	V3f normal=new V3f(0,0,1);

		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-.5*base,height-hh,0);
		vert[2]=new P3f(.5*base,height-hh,0);
		vert[3]=new P3f(-.2*base,height-hh,0);
		vert[4]=new P3f(-.2*base,0,0);
		vert[5]=new P3f(.2*base,0,0);
		vert[6]=new P3f(.2*base,height-hh,0);
	
	
	for(int i=0;i<3;i++){
		head.setCoordinate(i,vert[i]);
		head.setColor(i,color3);
		head.setNormal(i,normal);
	}
	
		for(int i=0;i<4;i++){
			tail.setCoordinate(i,vert[i+3]);
			tail.setNormal(i,normal);
			
			//if((i!=1 && i!=2))
			tail.setColor(i,color3);
		/*	else
			tail.setColor(i,color31);*/
		}

	
		Shape3D shpHead=new Shape3D(head);
		Shape3D shpTail=new Shape3D(tail);
		
		TransformGroup arrow=new TransformGroup();
	
arrow.addChild(shpHead);
arrow.addChild(shpTail);


	Transform3D trans=new Transform3D();

	trans.setRotation(util.mat3d(v,new Vect(0,1)));
	
	trans.setTranslation(new V3f(P.v3()));

	addChild(arrow);
	setTransform(trans);

}
	



public void setArrow2Dm1(Vect P,Vect v,double scale,Color color){
	
		Color3f color3=new Color3f(color);
	
	double base=0,height=0;

		base=.1*scale;
		height=.4*scale;
	
	 LineArray head=new LineArray(6, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

	 P3f[] vert=new P3f[3];

		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-.5*base,0,0);
		vert[2]=new P3f(.5*base,.0,0);

	
		head.setCoordinate(0,vert[0]);
		head.setCoordinate(1,vert[1]);
		head.setCoordinate(2,vert[1]);
		head.setCoordinate(3,vert[2]);
		head.setCoordinate(4,vert[2]);
		head.setCoordinate(5,vert[0]);

for(int i=0;i<6;i++){
		head.setColor(i,color3);
	}

LineAttributes la=new LineAttributes(1.4f,LineAttributes.PATTERN_SOLID,false);
Appearance app=new Appearance();
app.setLineAttributes(la);


		Shape3D shpHead=new Shape3D(head,app);
		
		TransformGroup arrow=new TransformGroup();
	
		arrow.addChild(shpHead);
		


			


	Transform3D trans=new Transform3D();

	trans.setRotation(util.mat3d(v,new Vect(0,1)));
	
	trans.setTranslation(new V3f(P.v3()));

	addChild(arrow);
	setTransform(trans);

}




}
