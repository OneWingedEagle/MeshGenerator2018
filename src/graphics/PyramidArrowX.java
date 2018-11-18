package graphics;
import math.Vect;
import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;

import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3f;
import static java.lang.Math.*;


public class PyramidArrowX extends TransformGroup {
double height;
 public PyramidArrowX(Vect P,Vect v,double scale,Color color){
 double base=.1*scale;
 height=scale;
 Color3f color3=new Color3f(color);
 
 LineArray edge=new LineArray(12, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
 


 P3f[] vertex=new P3f[4];
 vertex[0]=new P3f(-base/2,-base/2,0);
 vertex[1]=new P3f(base/2,-base/2,0);
 vertex[2]=new P3f(0,base/2,0);
 vertex[3]=new P3f(0,0,height);

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
	
	
	for(int i=0;i<12;i++)
		edge.setColor(i,color3);
	
			
			    
			    Shape3D pyrShape=new Shape3D(edge);
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
