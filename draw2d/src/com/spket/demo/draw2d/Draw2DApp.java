package com.spket.demo.draw2d;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutAnimator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.jetbrains.skija.impl.Library;

import com.spket.demo.draw2d.actions.ExitAction;
import com.spket.demo.draw2d.actions.LayoutAction;
import com.spket.demo.draw2d.actions.NewWizardAction;

public class Draw2DApp extends ApplicationWindow {
	public static void main(String[] args) {
		loadLibrary();
		
		Draw2DApp app = new Draw2DApp();
		
		app.setBlockOnOpen(true);
		
		app.open();
	}
	
	private static void loadLibrary() {
		System.loadLibrary("skija");
		System.setProperty("skija.staticLoad", String.valueOf(true));
		Library._loaded = true;
		Library._nAfterLoad();
	}
	
	private FigureCanvasGL canvas;
	private LayoutAlgorithm layoutAlgorithm;
	private LayoutEntity[] nodes;
	private LayoutRelationship[] connections;
	private boolean hasPendingLayoutRequest;
	
	public Draw2DApp() {
		super(null);
		
		createMenu();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		
		shell.setText("Draw2D Demo");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		canvas = new FigureCanvasGL(parent);
		canvas.setScrollBarVisibility(FigureCanvas.NEVER);

		Images.load();

		canvas.getViewport().setContents(createContent());
		
		canvas.addListener(SWT.Resize, e -> applyLayout());
		
		return canvas;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}
	
	protected void createMenu() {
		addMenuBar();
		
		MenuManager menu = getMenuBarManager();
		
		MenuManager file = new MenuManager("&File");
		createFileMenu(file);
		
		menu.add(file);
		
		MenuManager layout = new MenuManager("&Layout");
		LayoutAction.fill(this, layout);
		
		menu.add(layout);
	}
	
	protected void createFileMenu(MenuManager menu) {
		menu.add(new NewWizardAction(this));
		
		menu.add(new Separator());
		
		menu.add(new ExitAction(this));
	}
	

	protected IFigure createContent() {		
		ZestRootLayer zestRootLayer = new ZestRootLayer();

		zestRootLayer.setLayoutManager(new FreeformLayout());

		zestRootLayer.addLayoutListener(LayoutAnimator.getDefault());
		
		createNodes(zestRootLayer);
		
		return zestRootLayer;
	}
	
	protected void createNodes(ZestRootLayer zest) {
		SVGFigure analytics, internet, search, folder, gear, shield, gps, ipad;
		List<SVGFigure> ents = new ArrayList<>();
		List<SVGRelationship> conns = new ArrayList<>();
		ents.add(analytics = new SVGFigure(Images.getImage(Images.ANALYTICS_LAPTOP)));
		ents.add(internet = new SVGFigure(Images.getImage(Images.INTERNET)));
		ents.add(shield = new SVGFigure(Images.getImage(Images.INTERNET_SHIELD)));
		ents.add(search = new SVGFigure(Images.getImage(Images.SEARCH)));
		ents.add(folder = new SVGFigure(Images.getImage(Images.FOLDER)));
		ents.add(gear = new SVGFigure(Images.getImage(Images.SETTINGS_GEAR)));
		ents.add(gps = new SVGFigure(Images.getImage(Images.GPS)));
		ents.add(ipad = new SVGFigure(Images.getImage(Images.IPAD)));
				
		conns.add(new SVGRelationship(search, analytics));
		conns.add(new SVGRelationship(search, folder));
		conns.add(new SVGRelationship(search, internet));
		conns.add(new SVGRelationship(internet, shield));
		conns.add(new SVGRelationship(internet, gear));
		conns.add(new SVGRelationship(gps, ipad));
		//conns.add(new SVGRelationship(internet, shield));
		conns.add(new SVGRelationship(search, gps));
		
		ents.toArray(nodes = new LayoutEntity[ents.size()]);
		conns.toArray(connections = new LayoutRelationship[conns.size()]);
		
		for (IFigure c : conns)
			zest.addConnection(c);
		for (IFigure e : ents)
			zest.addNode(e);
	}
	
	public void applyLayout() {
		if (!hasPendingLayoutRequest) {
			hasPendingLayoutRequest = true;
			
			canvas.redraw();
			
			canvas.getDisplay().asyncExec(() -> doLayout());
		}
	}
	
	public void applyLayout(LayoutAlgorithm algorithm) {
		if (layoutAlgorithm != algorithm) {
			layoutAlgorithm = algorithm;
			
			applyLayout();
		}
	}
	
	protected void doLayout() {
		hasPendingLayoutRequest = false;
		if (canvas.isDisposed())
			return;
		
		int layoutStyle = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
		if (layoutAlgorithm == null) {
			layoutAlgorithm = new TreeLayoutAlgorithm(layoutStyle);
		} else {
			layoutAlgorithm.setStyle(layoutAlgorithm.getStyle() | layoutStyle);
		}
		Dimension d = canvas.getViewport().getSize();
		
		try {
			Animation.markBegin();
			layoutAlgorithm.applyLayout(nodes, connections, 0, 0, d.width, d.height, false, false);
			Animation.run(500);
			//canvas.getLightweightSystem().getUpdateManager().performUpdate();
		} catch (InvalidLayoutConfiguration e) {
			e.printStackTrace();
		}
	}
}
