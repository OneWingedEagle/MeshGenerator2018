package graphics;
import java.awt.Color;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import math.util;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.geometry.Triangulator;


public class Cartesian2D extends TransformGroup{
		
	public Cartesian2D(double[] boundary, Color xColor, Color yColor){	

		float elevation=1e-3f;
		double axisSize=util.max(boundary);
		axisSize=1;
		float[] bound=new float[4];
		for(int i=0;i<4;i++)
			bound[i]=(float)(boundary[i]/axisSize);
		float Xm,Ym;
		Xm=(bound[1]-bound[0])/2;
		Ym=(bound[3]-bound[2])/2;

		float XTrans=(bound[0]+bound[1])/2;
		float YTrans=(bound[2]+bound[3])/2;

		Transform3D tf=new Transform3D();
		tf.setTranslation(new Vector3f(XTrans,YTrans,0));
		this.setTransform(tf);
		LineArray axis=new LineArray(4,LineArray.COORDINATES|LineArray.COLOR_3);
		axis.setCoordinate(0,new Point3f(-Xm,0,elevation));
		axis.setCoordinate(1,new Point3f(Xm,0,elevation));
		axis.setCoordinate(2,new Point3f(0,-Ym,elevation));
		axis.setCoordinate(3,new Point3f(0,Ym,elevation));


		axis.setColor(0,new Color3f(xColor));
		axis.setColor(1,new Color3f(xColor));
		axis.setColor(2,new Color3f(yColor));
		axis.setColor(3,new Color3f(yColor));

		
		LineAttributes la=new LineAttributes(1f,LineAttributes.PATTERN_SOLID,false);
		Appearance app=new Appearance();
		app.setLineAttributes(la);

		
		TransformGroup tgX=new TransformGroup();
		TransformGroup tgY=new TransformGroup();

		
		Shape3D axisShape=new Shape3D(axis,app);
		addChild(axisShape);

		Text2D X = new Text2D("X", 
				new Color3f(xColor), 
				"Times New Roman",40, Font.PLAIN);
		X.setRectangleScaleFactor(bound[1]*.002f);
		
		Text2D Y = new Text2D("Y", 
				new Color3f(yColor), 
				"Times New Roman", 40, Font.PLAIN);
		Y.setRectangleScaleFactor(bound[3]*.002f);



		Transform3D transX=new Transform3D();
		Transform3D transY=new Transform3D();
		
		
		transX.setTranslation(new Vector3f(1.02f*Xm,0,0));	
		transY.setTranslation(new Vector3f(0f,1.02f*Ym,0f));		
		
		tgX.setTransform(transX);
		tgY.setTransform(transY);
		
		tgX.addChild(X);
		tgY.addChild(Y);
		this.addChild(tgX);
		this.addChild(tgY);


		  int dim=2;

		  P3f[][] vertex=new P3f[dim][3];
		  
		  vertex[0][0]=new P3f(Xm,.025f*Ym,0);
		  vertex[0][1]=new P3f(Xm,-.025f*Ym,0);
		  vertex[0][2]=new P3f(1.05f*Xm,0,0);
		  vertex[1][0]=new P3f(-.025f*Xm,Ym,0);
		  vertex[1][1]=new P3f(.025f*Xm,Ym,0);
		  vertex[1][2]=new P3f(0,1.05f*Ym,0);
		
			  TriangleArray heads= new TriangleArray(3*dim,
						 GeometryArray.COORDINATES | GeometryArray.NORMALS |GeometryArray.COLOR_3);
		  Color3f cl0=new Color3f(xColor);
		  Color3f cl1=new Color3f(yColor);
			 V3f normal=new V3f(0,0,1);
			 for(int i=0;i<dim;i++){
				 for(int j=0;j<3;j++){
				 heads.setCoordinate(3*i+j,vertex[i][j]);
				 heads.setNormal(3*i+j,normal);
				 if(i==0)
				 heads.setColor(3*i+j,cl0);
				 else
					 heads.setColor(3*i+j,cl1);
				 }

				}
			 
			 Shape3D  tips=new Shape3D(heads);
	
			 this.addChild(tips);


		
	}


	


	}
