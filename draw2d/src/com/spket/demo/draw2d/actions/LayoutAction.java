package com.spket.demo.draw2d.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;

import com.spket.demo.draw2d.Draw2DApp;

public final class LayoutAction extends Action {
	private static final String[] algorithmNames = { "Spring", "Tree - V", "Tree - H", "Radial", "Grid", "Horizontal", "Vertical" };
	private static final LayoutAlgorithm[] algorithms = {
			new SpringLayoutAlgorithm(LayoutStyles.NONE),
			new TreeLayoutAlgorithm(LayoutStyles.NONE),
			new HorizontalTreeLayoutAlgorithm(LayoutStyles.NONE),
			new RadialLayoutAlgorithm(LayoutStyles.NONE),
			new GridLayoutAlgorithm(LayoutStyles.NONE),
			new HorizontalLayoutAlgorithm(LayoutStyles.NONE),
			new VerticalLayoutAlgorithm(LayoutStyles.NONE)
	};
	
	public static void fill(Draw2DApp application, IContributionManager manager) {
		for (int i = 0, length = algorithmNames.length; i < length; i++) {
			LayoutAction action = new LayoutAction(application, algorithmNames[i], algorithms[i]);
			if (i == 1)
				action.setChecked(true);
			manager.add(action);
		}
	}
	
	private Draw2DApp application;
	private LayoutAlgorithm algorithm;
	
	private LayoutAction(Draw2DApp application, String name, LayoutAlgorithm algorithm) {
		super(name, IAction.AS_RADIO_BUTTON);
		
		this.application = application;
		this.algorithm = algorithm;
	}

	@Override
	public void run() {
		application.applyLayout(algorithm);
	}
}
