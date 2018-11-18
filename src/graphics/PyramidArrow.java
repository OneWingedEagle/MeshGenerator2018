package graphics;
import math.Vect;
import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;

import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Text2D;

import static java.lang.Math.*;


public class PyramidArrow extends TransformGroup {
double height;
 public PyramidArrow(Vect P,Vect v,double scale,Color color){
 double base=.1*scale;
 height=scale;
		 IndexedQuadArray pyramid = new IndexedQuadArray(8,
			        IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
			    P3f[] cubeCoordinates = { 
			    	new P3f(0,.0,height),
			    	new P3f(0,0,height),
			    	new P3f(0,-0,height),
			    	new P3f(0,0,height),
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
			    
			    Color3f color3=new Color3f(color);
			    
			    Color3f ambientColour =color3;
			    Color3f emissiveColour = new Color3f(.0f, .0f, .0f);
			    Color3f specularColour = new Color3f(.2f, .2f, .2f);
			    Color3f diffuseColour =color3;
			    float shininess = 1.0f;
			    
			    app.setMaterial(new Material(ambientColour, emissiveColour,
			        diffuseColour, specularColour, shininess));
			    
			    TransparencyAttributes tAtt =new TransparencyAttributes(TransparencyAttributes.NONE,0);
			    app.setTransparencyAttributes(tAtt);
			    
			    Shape3D pyrShape=new Shape3D(pyramid,app);
			   
			    addChild(pyrShape);
			   
			   
			    Transform3D trans=new Transform3D();
			    
			    if(v.norm()!=0){
			    Vect vr=new Vect(0,0,1);
				 double alpha,cos;
				 Vect rotAx=vr.cross(v);
				
				 if(rotAx.norm()>0){
				rotAx.normalize();
				 cos=vr.dot(v)/v.norm();
				 if(cos>=1)
					 alpha=0;
				 else if(cos<=-1)alpha=PI;
				 else
					 alpha=acos(cos);
		
			    Matrix3d M =mat3d(rotAx,alpha);	   
			    trans.setRotation(M);
			   
			    }
				 else if(v.el[2]<0){
					 trans.rotX(PI); 
				 }
			    
			    }
			    
			   trans.setTranslation(new V3f(P));		  
			    setTransform(trans);
			  
 }
 
 public Matrix3d mat3d(Vect rotAx, double alpha ){

	 	double[] mArray=new double[9];
		
		double e1,e2,e3,e4;
		   
		   e1=rotAx.el[0]*sin(alpha/2);
		   e2=rotAx.el[1]*sin(alpha/2);
		   e3=rotAx.el[2]*sin(alpha/2);
		   e4=cos(alpha/2);
		   
		   mArray[0]=pow(e1,2)-pow(e2,2)-pow(e3,2)+pow(e4,2);
		   mArray[1]=2*(e1*e2-e3*e4);
		   mArray[2]=2*(e1*e3+e2*e4);
		   mArray[3]=2*(e1*e2+e3*e4);
		   mArray[4]=-pow(e1,2)+pow(e2,2)-pow(e3,2)+pow(e4,2);
		   mArray[5]=2*(e2*e3-e1*e4);
		   mArray[6]=2*(e1*e3-e2*e4);
		   mArray[7]=2*(e2*e3+e1*e4);
		   mArray[8]=-pow(e1,2)-pow(e2,2)+pow(e3,2)+pow(e4,2);
		  
		   Matrix3d M=new Matrix3d(mArray);
		   return M;
 }

}
