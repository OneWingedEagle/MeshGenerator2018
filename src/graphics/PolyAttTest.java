package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class PolyAttTest {
public static class MyDrawingArea extends JPanel {
private Canvas3D _canvas3d;
public static PolygonAttributes _pa;

public MyDrawingArea() {
setPreferredSize(new Dimension(500,500));

GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
_canvas3d = new Canvas3D(config);
_canvas3d.setBackground(Color.black);

setLayout( new BorderLayout() );
add(_canvas3d, BorderLayout.CENTER);

SimpleUniverse simple_u = new SimpleUniverse(_canvas3d);
simple_u.getViewingPlatform().setNominalViewingTransform();
simple_u.getViewer().getView().setFrontClipDistance(0.001);

_pa = new PolygonAttributes();
_pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
_pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
_pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);

Point3f a = new Point3f(0,0,0);
Point3f b = new Point3f(0.8f,0,0);
Point3f c = new Point3f(0,0.8f,0);
Point3f d = new Point3f(0.8f,0.8f,0);

Point3f e = new Point3f(0,0,0);
Point3f f = new Point3f(-0.8f,0,0);
Point3f g = new Point3f(0,-0.8f,0);
Point3f h = new Point3f(-0.8f,-0.8f,0);

TriangleArray trias1 = new TriangleArray(6, TriangleArray.COORDINATES | TriangleArray.NORMALS);
trias1.setCoordinate(0, a);
trias1.setCoordinate(1, b);
trias1.setCoordinate(2, c);
trias1.setCoordinate(3, c);
trias1.setCoordinate(4, b);
trias1.setCoordinate(5, d);

/*TriangleArray trias2 = new TriangleArray(6, TriangleArray.COORDINATES | TriangleArray.NORMALS);
trias2.setCoordinate(0, e);
trias2.setCoordinate(1, f);
trias2.setCoordinate(2, g);
trias2.setCoordinate(3, g);
trias2.setCoordinate(4, f);
trias2.setCoordinate(5, h);*/

Appearance ap;

ap = new Appearance();
ap.setPolygonAttributes(_pa);
Shape3D shp_1 = new Shape3D(trias1, ap);

/*ap = new Appearance();
ap.setPolygonAttributes(_pa);
Shape3D shp_2 = new Shape3D(trias2, ap);*/

BranchGroup root = new BranchGroup();
root.addChild(shp_1);
//root.addChild(shp_2);

simple_u.addBranchGraph(root);
}

}

private static MyDrawingArea _mda;

public static void main(String[] args) {
JFrame window = new JFrame("Polygon Attributes Test");

// if someone closes the main window, the program shall end.
window.addWindowListener(new WindowAdapter() {
public void windowClosing(WindowEvent e) {
System.exit(0);
}
});

_mda = new MyDrawingArea();

JButton jb_switch = new JButton("Switch Polygon Mode");
jb_switch.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
if (MyDrawingArea._pa.getPolygonMode()!=PolygonAttributes.POLYGON_LINE) {
MyDrawingArea._pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
} else {
MyDrawingArea._pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
}
}
});

window.setLayout(new BorderLayout());
window.add(_mda, BorderLayout.CENTER);
window.add(jb_switch, BorderLayout.SOUTH);
window.pack();

window.setVisible(true);
}
}