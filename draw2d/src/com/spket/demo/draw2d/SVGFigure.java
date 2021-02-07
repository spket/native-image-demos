package com.spket.demo.draw2d;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionDimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;
import org.jetbrains.skija.Point;
import org.jetbrains.skija.svg.DOM;

public class SVGFigure extends Figure implements LayoutEntity {
	private static final Dimension DEFAULT_SIZE = new PrecisionDimension(64, 64);
	
	private DOM svg;
	private Object layoutInfo;

	public SVGFigure() {
	}

	public SVGFigure(DOM svg) {
		this.svg = svg;
		initSize();
	}

	protected void initSize() {
		Dimension pSize = DEFAULT_SIZE;
		//*
		Point size = svg.getContainerSize();
		if (size != null)
			pSize = new PrecisionDimension(size.getX(), size.getY());
		
		//*/
		setPreferredSize(pSize);
		setSize(pSize);
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		if (svg != null) {
			if (graphics instanceof SkiaGraphics) {
				org.eclipse.draw2d.geometry.Rectangle b = getBounds();
				((SkiaGraphics) graphics).draw(svg, b.x, b.y);
			}
		}
	}

	@Override
	public void setGraphData(Object o) {
		if (svg != o) {
			if (o instanceof DOM) {
				svg = (DOM) o;
			} else {
				svg = null;
			}
			initSize();
		}
	}

	@Override
	public Object getGraphData() {
		return svg;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public void setLocationInLayout(double x, double y) {
		getParent().setConstraint(this, new PrecisionRectangle(x, y, bounds.preciseWidth(), bounds.preciseHeight()));
		//setLocation(new PrecisionPoint(x, y));
	}

	@Override
	public void setSizeInLayout(double width, double height) {
	}

	@Override
	public double getXInLayout() {
		return bounds.preciseX();
	}

	@Override
	public double getYInLayout() {
		return bounds.preciseY();
	}

	@Override
	public double getWidthInLayout() {
		return bounds.preciseWidth();
	}

	@Override
	public double getHeightInLayout() {
		return bounds.preciseHeight();
	}

	@Override
	public Object getLayoutInformation() {
		return layoutInfo;
	}

	@Override
	public void setLayoutInformation(Object internalEntity) {
		layoutInfo = internalEntity;
	}

	@Override
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		// TODO Auto-generated method stub
		
	}

}
