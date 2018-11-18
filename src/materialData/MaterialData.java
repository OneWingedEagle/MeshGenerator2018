package materialData;


import java.awt.Color;
import java.util.Arrays;
import static java.lang.Math.random;
import javax.vecmath.Color3f;

import math.util;

	public class MaterialData {
	public Color[] color={Color.cyan,new Color(.85f,.85f,.0f), Color.red,  Color.lightGray, Color.pink,Color.yellow,Color.magenta.brighter(),new Color(.5f,1f,.5f),new Color(.3f,.3f,1f),Color.gray};
	public Color3f[] color3f;
	public String[] List={"air","iron","coil","wood","plastic","copper","permalloy","hard","cast","magnet"};
	private  double[] Yng={1e0, 2.1e11 ,1e20,1e10,1e10,1e10,1e10,2e11,1e13,1e12};
	private  double[] Pois={.0,.3,.2,.1,.2,.3,.2,.2,.3,.3,.3};
	public double[] mu={1,1e3,1,1,1,1,1e5,1,1e3,1};
	public double[] ro={1.3,7650,2e3,6e2,8e2,2e3,6e3,4e3,6000,8000};
	public double[] sigma={0,1e4,1e6,0,0,1e6,1e4,0,1e5,1e5};
	
	public MaterialData(){
		
		color3f=new Color3f[color.length];
		for(int i=0;i<color3f.length;i++)
			color3f[i]=new Color3f(color[i]);
		
	}
	
	public  Color3f matColor3f(String material){
		Color3f matColor3f=new Color3f((float)(.5+.5*random()),(float)(.5+.5*random()),(float)(.5+.5*random())); 
		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				matColor3f=color3f[i];
				break;
			}
		
		}
		return matColor3f;
		
	}
	
	public  double getYng(String material){
		double  yng=1e11; 
		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				yng=Yng[i];
				break;
			}
		
		}
		return yng;
		
	}
	
	public  double getPois(String material){
		double  pois=.3; 
		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				pois=Pois[i];
				break;
			}
		
		}
		return pois;
		
	}
	
	public  double getRo(String material){
		double  ro=7650; 
		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				ro=this.ro[i];
				break;
			}
		
		}
		return ro;
		
	}
	
	public  Color matColor(String material){
		
		
		Color matColor=this.color[(int)(List.length*random())];

		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				matColor=color[i];
				break;
			}
		
		}
		return matColor;
		
	}
	
}
