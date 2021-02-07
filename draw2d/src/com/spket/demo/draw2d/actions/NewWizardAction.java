package com.spket.demo.draw2d.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

public class NewWizardAction extends Action {
	private IShellProvider shellProvider;
	
	public NewWizardAction(IShellProvider shellProvider) {
		super("&New...");
		this.shellProvider = shellProvider;
	}

	@Override
	public void run() {
		Shell shell = shellProvider.getShell();
		if (shell != null) {
			//TODO
		}
	}
}
