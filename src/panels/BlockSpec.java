package panels;
import components.*;
import materialData.MaterialData;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class BlockSpec extends JPanel implements ItemListener{
	Font tfFont = new Font("Arial", 0, 14);
	public TextField tfMaterial,tfName;
	public TextField[] tfBoundary,tfMu,tfSigma;;
	public TextField[] tfMeshSize,tfBase,tfCurrent,tfMag;
	public JCheckBox[] na;
	public Button bApply=new Button("Apply");
	public Button bEdit=new Button("Edit");
	public JCheckBox hasCurrent,hasMag,nonlinear;
	
	int nBoundary=6;
	public Color panelColor=new Color(.9f,.9f,.85f);
	public Color defaultColor=new Color(215, 255, 225);
	double refMeshSize=25,refBase=2;

	public BlockSpec (){
		super();	
		setLayout(new FlowLayout(0,5,5));
		setBackground(panelColor);
		
		tfMaterial=new TextField("iron");
		tfMaterial.setBackground(new MaterialData().color[0]);

	
		tfMaterial.setOpaque(true);
		
		tfName=new TextField("iron");
		tfName.setBackground(new MaterialData().matColor(tfMaterial.getText()));	
		tfName.setOpaque(true);

		
		Label[] lbBoundary;
		lbBoundary=new Label[nBoundary];
		lbBoundary[0]=new Label("X1", Label.CENTER);
		lbBoundary[1]=new Label("X2", Label.CENTER);
		lbBoundary[2]=new Label("Y1", Label.CENTER);
		lbBoundary[3]=new Label("Y2", Label.CENTER);
		lbBoundary[4]=new Label("Z1", Label.CENTER);		
		lbBoundary[5]=new Label("Z2", Label.CENTER);
		
		for(int j=0;j<nBoundary;j++){
			lbBoundary[j].setOpaque(true);
			if(j%2==1)
				lbBoundary[j].setBackground(Color.orange);
			else
				lbBoundary[j].setBackground(new Color(10,255,10));
		}

		tfBoundary=new TextField[nBoundary];
		tfMeshSize=new TextField[2*nBoundary];
		na=new JCheckBox[2*nBoundary];
		
		ItemListener naListener=new  ItemListener(){	
			public void itemStateChanged(ItemEvent e){
			JCheckBox nai=(JCheckBox)e.getSource();
			int naIndex=Integer.parseInt(nai.getName());
			if(na[naIndex].isSelected()){
			tfMeshSize[naIndex].setEditable(false);
			tfMeshSize[naIndex].setBackground(Color.lightGray);
			tfBase[naIndex].setEditable(false);
			tfBase[naIndex].setBackground(Color.lightGray);
			}
		else{
			try{		
				tfMeshSize[naIndex].setEditable(true);
				 if(Double.parseDouble(tfMeshSize[naIndex].getText())==refMeshSize)
					 tfMeshSize[naIndex].setBackground(defaultColor);
						 else tfMeshSize[naIndex].setBackground(Color.white);
			tfBase[naIndex].setEditable(true);
			if(Double.parseDouble(tfBase[naIndex].getText())==refBase)
				 tfBase[naIndex].setBackground(defaultColor);
					 else tfBase[naIndex].setBackground(Color.white);
			} catch(NumberFormatException nfe){ 
				 String msg="The text field does not contain a number.";
					JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
				}
			}
			}
			};
		
		for(int i=0;i<2*nBoundary;i++){
			na[i]=new JCheckBox("N.A");
			na[i].setName(Integer.toString(i));
			 na[i].addItemListener(naListener);
			 if(i%2==0){
					na[i].setHorizontalAlignment(JCheckBox.RIGHT);
				na[i].setHorizontalTextPosition(JCheckBox.LEFT);
				}
				else
					na[i].setHorizontalAlignment(JCheckBox.LEFT);

		}
			
		tfBase=new TextField[2*nBoundary];
		for(int i=0;i<nBoundary;i++){
			
			tfBoundary[i]=new TextField();
			if(i%2==0)
				tfBoundary[i]=new TextField("-1000.0");
				else
					tfBoundary[i]=new TextField("1000.0");
			
			lbBoundary[i].setOpaque(true);
			if(i%2==0)
				lbBoundary[i].setBackground(new Color(10,255,10));
			else
				lbBoundary[i].setBackground(Color.orange);
				}
		
		
		
		for(int i=0;i<2*nBoundary;i++){

			tfBase[i]=new TextField();
			tfBase[i].setName(Integer.toString(i));
			tfMeshSize[i]=new TextField();

			tfMeshSize[i].setName(Integer.toString(i));
			tfBase[i].setOpaque(true);
			tfMeshSize[i].setOpaque(true);
			tfBase[i].setBackground(defaultColor);
			tfMeshSize[i].setBackground(defaultColor);			
			na[i].setBackground(panelColor);				
			}
		
		ActionListener tfListenerMesh=new  ActionListener(){
			public void actionPerformed(ActionEvent e){
				 TextField tf=(TextField)(e.getSource());
				 try{
				double base=Double.parseDouble(tf.getText());
				 if(!na[Integer.parseInt(tf.getName())].isSelected())
					 if(base==refMeshSize) 
						 tf.setBackground(defaultColor);
					 else
				 tf.setBackground(Color.white);
				 }
				 catch(NumberFormatException nfe){ 
					 String msg="The text field does not contain a number.";
						JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
					}
				 
				}};
				
				ActionListener tfListenerBase=new  ActionListener(){
					public void actionPerformed(ActionEvent e){
						 TextField tf=(TextField)(e.getSource());
						 try{
						double base=Double.parseDouble(tf.getText());
						 if(!na[Integer.parseInt(tf.getName())].isSelected())
							 if(base==refBase) 
								 tf.setBackground(defaultColor);
							 else
						 tf.setBackground(Color.white);
						 }
						 catch(NumberFormatException nfe){ 
							 String msg="The text field does not contain a number.";
								JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
							}
						 
						}};
						


		MouseListener mouseClickListener=new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				 TextField tf=(TextField)(e.getSource());
				 if(!na[Integer.parseInt(tf.getName())].isSelected())
				 tf.setBackground(Color.white);
				 }};
		
	
		 for(int j=0;j<2*nBoundary;j++){
			
			 tfMeshSize[j].addActionListener(tfListenerMesh);
				tfMeshSize[j].addMouseListener(mouseClickListener);
				tfBase[j].addActionListener(tfListenerBase);
				tfBase[j].addMouseListener(mouseClickListener);
				
			}
		 		

		
		Label lbMu=new Label("\u03BC"+"r ",Label.CENTER);
		lbMu.setFont(new Font("Times New Roman",2,18));
		Label lbSigma=new Label("\u03C3",Label.CENTER);
		lbSigma.setFont(new Font("Times New Roman",2,18));
		tfMu=new TextField[3];
		tfSigma=new TextField[3];
		for(int k=0;k<3;k++){
		tfMu[k]=new TextField("1000.0");
		tfSigma[k]=new TextField("0.0");
		}
		
		tfCurrent=new TextField[3];
		for(int i=0;i<3;i++){
		tfCurrent[i]=new TextField("0.0");
		tfCurrent[i].setVisible(false);
		}
		
		tfMag=new TextField[3];
		for(int i=0;i<3;i++){
		tfMag[i]=new TextField("0.0");
		tfMag[i].setVisible(false);
		}
	
		
		hasCurrent=new JCheckBox("J ");
		hasCurrent.setBackground(panelColor);
		hasCurrent.setSelected(false);
		hasCurrent.setFont(new Font("Times New Roman",3,12));
		
		hasMag=new JCheckBox("M ");
		hasMag.setBackground(panelColor);
		hasMag.setSelected(false);
		hasMag.setFont(new Font("Times New Roman",3,12));
		
		JPanel specPanel=new JPanel(new GridLayout(4,5,5,5));
		specPanel.setBackground(panelColor);
		specPanel.add(new Label());
		specPanel.add(lbMu);
		specPanel.add(lbSigma);
		specPanel.add(hasCurrent);
		specPanel.add(hasMag);
		specPanel.add(tfName);
		specPanel.add(tfMu[0]);
		specPanel.add(tfSigma[0]);
		specPanel.add(tfCurrent[0]);
		specPanel.add(tfMag[0]);
		specPanel.add(tfMaterial);
		specPanel.add(tfMu[1]);
		specPanel.add(tfSigma[1]);
		specPanel.add(tfCurrent[1]);
		specPanel.add(tfMag[1]);
		specPanel.add(new Label());
		specPanel.add(tfMu[2]);
		specPanel.add(tfSigma[2]);
		specPanel.add(tfCurrent[2]);
		specPanel.add(tfMag[2]);

		JPanel okPanel=new JPanel(new GridLayout(3,1,5,5));
		okPanel.setBackground(panelColor);
		 okPanel.setPreferredSize(new Dimension(70,80));

		
		nonlinear=new JCheckBox("nonlinear ");
		nonlinear.setBackground(panelColor);
		nonlinear.setSelected(false);
		
		okPanel.add(bApply);
		okPanel.add(bEdit);
		okPanel.add(nonlinear);
		
		
		
		JPanel[] subPanel=new JPanel[nBoundary];
		
		for(int i=0;i<nBoundary;i++){
		subPanel[i]=new JPanel(new GridLayout(3,3,5,5));
		subPanel[i].setBackground(panelColor);
		subPanel[i].add(na[2*i]);
		subPanel[i].add(lbBoundary[i]);
		lbBoundary[i].setBorder(BorderFactory.createLineBorder(Color.gray));
		subPanel[i].add(na[2*i+1]);
		subPanel[i].add(tfMeshSize[2*i]);
		subPanel[i].add(tfBoundary[i]);
		subPanel[i].add(tfMeshSize[2*i+1]);
		subPanel[i].add(tfBase[2*i]);
		subPanel[i].add(new Label());
		subPanel[i].add(tfBase[2*i+1]);
		}
		
		add(okPanel);
		add(specPanel);
		for(int i=0;i<nBoundary;i++)
			add(subPanel[i]);
		
	
		hasCurrent.addItemListener(this);
		hasMag.addItemListener(this);
		nonlinear.addItemListener(this);

		
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==hasCurrent){
		if(hasCurrent.isSelected())
			for(int i=0;i<3;i++)
				tfCurrent[i].setVisible(true);	
		else
			for(int i=0;i<3;i++)
				tfCurrent[i].setVisible(false);
		}
		
		else if(e.getSource()==hasMag){
			if(hasMag.isSelected())
				for(int i=0;i<3;i++)
					tfMag[i].setVisible(true);	
			else
				for(int i=0;i<3;i++)
					tfMag[i].setVisible(false);
			}
		
		else if(e.getSource()==nonlinear){
		if(nonlinear.isSelected())
			for(int i=0;i<3;i++)
			tfMu[i].setEnabled(false);
		else
			for(int i=0;i<3;i++)
			tfMu[i].setEnabled(true);
		}
	
		
	}


}
