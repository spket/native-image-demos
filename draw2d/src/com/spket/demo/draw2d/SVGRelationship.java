package com.spket.demo.draw2d;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.zest.core.widgets.internal.PolylineArcConnection;
import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

public class SVGRelationship extends PolylineArcConnection implements LayoutRelationship {
	private Object layoutInfo;
	
	public SVGRelationship(SVGFigure source, SVGFigure target) {
		setSourceAnchor(new ChopboxAnchor(source));
		setTargetAnchor(new ChopboxAnchor(target));
	}
	
	@Override
	public Object getGraphData() {
		return null;
	}

	@Override
	public void setGraphData(Object o) {
	}

	@Override
	public LayoutEntity getSourceInLayout() {
		return (LayoutEntity) getSourceAnchor().getOwner();
	}

	@Override
	public LayoutEntity getDestinationInLayout() {
		return (LayoutEntity) getTargetAnchor().getOwner();
	}

	@Override
	public Object getLayoutInformation() {
		return layoutInfo;
	}

	@Override
	public void setLayoutInformation(Object layoutInformation) {
		layoutInfo = layoutInformation;
	}

	@Override
	public void setBendPoints(LayoutBendPoint[] bendPoints) {
	}

	@Override
	public void clearBendPoints() {
	}

	@Override
	public void populateLayoutConstraint(LayoutConstraint constraint) {
	}

}
