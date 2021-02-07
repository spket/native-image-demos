package com.spket.demo.draw2d;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GraphicsSource;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.jetbrains.skija.Canvas;
import org.jetbrains.skija.ColorAlphaType;
import org.jetbrains.skija.ImageInfo;
import org.jetbrains.skija.Surface;

public class LightweightSystemGL extends LightweightSystem implements GraphicsSource {
	private class RootFigureGL extends RootFigure {
		private Rectangle clip = new Rectangle();
		
		@Override
		public void paint(Graphics graphics) {
			if (graphics instanceof SkiaGraphics) {
				super.paint(graphics);
			} else {
				graphics.getClip(clip);
				
				Graphics g = getGraphics(clip);
				
				super.paint(g);
				
				g.dispose();
				
				flushGraphics(clip);
			}
		}
	}
	
	private FigureCanvasGL glCanvas;
	private Surface surface;
	
	@Override
	public void setControl(org.eclipse.swt.widgets.Canvas c) {
		if (c != glCanvas) {
			if (c instanceof FigureCanvasGL) {
				glCanvas = (FigureCanvasGL) c;
			} else {
				glCanvas = null;
			}
			super.setControl(c);
			if (glCanvas != null)
				getUpdateManager().setGraphicsSource(this);
		}
	}
	
	@Override
	public void paint(GC gc) {
		super.paint(gc);
		
		swapBuffer();
	}
	
	@Override
	public Graphics getGraphics(Rectangle region) {
		return new SkiaGraphics(getCanvas(), region);
	}
	
	@Override
	public void flushGraphics(Rectangle region) {
		surface.flush();
		
		swapBuffer();
		//glCanvas.redraw(region.x, region.y, region.width, region.height, false);
	}
	
	@Override
	protected RootFigure createRootFigure() {
		RootFigure f = new RootFigureGL();
		f.addNotify();
		f.setOpaque(true);
		f.setLayoutManager(new StackLayout());
		return f;
	}
	
	@Override
	protected void addListeners() {
		super.addListeners();
		if (glCanvas != null) {
			glCanvas.addListener(SWT.Dispose, e -> onDispose());
		}
	}

	@Override
	protected void controlResized() {
		if (surface != null) {
			surface.close();
			surface = null;
		}
		glCanvas.releaseSurface();
		
		super.controlResized();
	}
	
	protected void onDispose() {
		if (surface != null && glCanvas != null && !glCanvas.isDisposed()) {
			surface.close();
			surface = null;
		}
		Images.dispose();
	}
	
	private void swapBuffer() {
		Surface surf = glCanvas.getSurface();
		Canvas canvas = surf.getCanvas();
		if (surface != null) {
			surface.draw(canvas, 0, 0, null);
		} else {
			canvas.clear(0xffffffff);
		}
		surf.flush();
		glCanvas.swapBuffers();
	}
	
	private Canvas getCanvas() {
		if (surface == null) {
			org.eclipse.swt.graphics.Rectangle r = glCanvas.getClientArea();
			ImageInfo info = ImageInfo.makeN32(r.width, r.height, ColorAlphaType.OPAQUE);
			surface = Surface.makeRenderTarget(glCanvas.getContext(), false, info);
		}
		return surface.getCanvas();
	}
}
