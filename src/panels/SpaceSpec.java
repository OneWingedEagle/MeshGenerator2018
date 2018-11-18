package panels;
import components.*;
import materialData.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SpaceSpec extends JPanel implements ItemListener{
	Font font = new Font("Times New Roman", 1, 12);
	public TextField tfMaterial,tfName;
	public TextField[] tfBoundary,tfMu,tfSigma;;
	public TextField[] tfCurrent;
	public TextField[][] tfDiric;
	public JComboBox[] bCondSelector;

	public Button bClose=new Button("Close");
	public Button bApply=new Button("Apply");
	public Button bRemove=new Button("Remove ");
	public JCheckBox hasCurrent;
	public JPanel[] diricPanel,B_Panel;;
	private int nBoundary=6;
	public Color panelColor=new Color(.9f,.9f,.85f);
	public Color defaultColor=new Color(215, 255, 225);

	public SpaceSpec (){
		super();	
		setLayout(new FlowLayout(0,5,5));
		setBackground(panelColor);
		tfMaterial=new TextField("air");
		tfMaterial.setBackground(new MaterialData().color[0]);
		tfMaterial.setOpaque(true);
		
		tfName=new TextField("airSpace");
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

		
		tfDiric=new TextField[nBoundary][3];
		tfBoundary=new TextField[nBoundary];
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
		
		
		bCondSelector=new JComboBox[6];
		String[] bCondType = {"Neu."," Dir."," Per."};
		
		for(int i=0;i<nBoundary;i++){
			bCondSelector[i] =new JComboBox(bCondType);
			bCondSelector[i].setBackground(panelColor);	
			bCondSelector[i].setFont(font);
			bCondSelector[i].addItemListener(this);
			for(int j=0;j<3;j++)
				tfDiric[i][j]=new TextField("0.0");
		}
		
		Label lbMu=new Label("\u03BC"+"r ");
		lbMu.setFont(new Font("Times New Roman",2,18));
		Label lbSigma=new Label("\u03C3 ");
		lbSigma.setFont(new Font("Times New Roman",2,18));
		tfMu=new TextField[3];
		tfSigma=new TextField[3];
		for(int k=0;k<3;k++){
		tfMu[k]=new TextField("1.0");
		tfSigma[k]=new TextField("0.0");
		}
		
		tfCurrent=new TextField[3];
		for(int i=0;i<3;i++){
		tfCurrent[i]=new TextField("0.0");
		tfCurrent[i].setVisible(false);
		}
		
		hasCurrent=new JCheckBox("J ");
		hasCurrent.setBackground(panelColor);
		hasCurrent.setSelected(false);
		hasCurrent.setFont(new Font("Times New Roman",3,12));
		
		
		JPanel specPanel=new JPanel(new GridLayout(4,4,5,5));
		specPanel.setBackground(panelColor);
		specPanel.add(new Label());
		specPanel.add(lbMu);
		specPanel.add(lbSigma);
		specPanel.add(hasCurrent);
		specPanel.add(tfName);
		specPanel.add(tfMu[0]);
		specPanel.add(tfSigma[0]);
		specPanel.add(tfCurrent[0]);
		specPanel.add(tfMaterial);
		specPanel.add(tfMu[1]);
		specPanel.add(tfSigma[1]);
		specPanel.add(tfCurrent[1]);
		specPanel.add(new Label());
		specPanel.add(tfMu[2]);
		specPanel.add(tfSigma[2]);
		specPanel.add(tfCurrent[2]);

		JPanel okPanel=new JPanel(new GridLayout(3,1,5,5));
		okPanel.setBackground(panelColor);
		 okPanel.setPreferredSize(new Dimension(70,80));

	
		
		okPanel.add(bApply);
		okPanel.add(bClose);
		
	

		diricPanel=new JPanel[nBoundary];
		
		
		for(int i=0;i<nBoundary;i++){
			diricPanel[i]=new JPanel(new GridLayout(2,3,2,2));
			
			diricPanel[i].setBackground(panelColor);
			diricPanel[i].setVisible(false);
			diricPanel[i].add(new Label("Bx",Label.CENTER));
			diricPanel[i].add(new Label("By",Label.CENTER));
			diricPanel[i].add(new Label("Bz",Label.CENTER));
			diricPanel[i].add(tfDiric[i][0]);
			diricPanel[i].add(tfDiric[i][1]);
			diricPanel[i].add(tfDiric[i][2]);
		}
		
		JPanel[] boundaryPanel=new JPanel[nBoundary];
		
		
		for(int i=0;i<nBoundary;i++){
			boundaryPanel[i]=new JPanel(new GridLayout(2,3,2,2));
			boundaryPanel[i].setBackground(panelColor);	
			
			boundaryPanel[i].add(new Label("B.C.",Label.LEFT));
			boundaryPanel[i].add(lbBoundary[i]);
			boundaryPanel[i].add(new Label());
			
			boundaryPanel[i].add(bCondSelector[i]);
			boundaryPanel[i].add(tfBoundary[i]);
			boundaryPanel[i].add(new Label());

		}
		
		JPanel[] subPanel=new JPanel[nBoundary];
		
		for(int i=0;i<nBoundary;i++){
		subPanel[i]=new JPanel(new GridLayout(2,1,5,5));
		subPanel[i].setBackground(panelColor);
		subPanel[i].add(boundaryPanel[i]);
		subPanel[i].add(diricPanel[i]);

		lbBoundary[i].setBorder(BorderFactory.createLineBorder(Color.gray));

		}
		
		add(okPanel);
		add(specPanel);
		for(int i=0;i<nBoundary;i++)
			add(subPanel[i]);
		
	

		hasCurrent.addItemListener(new  ItemListener(){	
			public void itemStateChanged(ItemEvent e){
				if(hasCurrent.isSelected())
					for(int i=0;i<3;i++)
						tfCurrent[i].setVisible(true);								
				else
					for(int i=0;i<3;i++)
						tfCurrent[i].setVisible(false);
			}

		});
		
		
	}
	
	public void itemStateChanged(ItemEvent item) {
		JComboBox selected=(JComboBox) item.getSource();
		for(int i=0;i<6;i++){
		if(selected.equals(bCondSelector[i])){
			if(selected.getSelectedIndex()==1)			
				diricPanel[i].setVisible(true);				
			else
				diricPanel[i].setVisible(false);

		break;}
		}
	
		
	}
	

	
	public static void main(String[] args){
		SpaceSpec sp=new SpaceSpec();
		JFrame frame=new JFrame();
		frame.add(sp);
		frame.setSize(1200,200);
		frame.setVisible(true);
	}

}
