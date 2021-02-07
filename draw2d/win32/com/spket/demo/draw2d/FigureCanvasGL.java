package com.spket.demo.draw2d;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jetbrains.skija.BackendRenderTarget;
import org.jetbrains.skija.ColorSpace;
import org.jetbrains.skija.DirectContext;
import org.jetbrains.skija.FramebufferFormat;
import org.jetbrains.skija.Surface;
import org.jetbrains.skija.SurfaceColorFormat;
import org.jetbrains.skija.SurfaceOrigin;

public class FigureCanvasGL extends FigureCanvas {
	private static final String USE_OWNDC_KEY = "org.eclipse.swt.internal.win32.useOwnDC"; //$NON-NLS-1$
	
	private static int checkStyle(Composite parent, int style) {
		parent.getDisplay().setData(USE_OWNDC_KEY, Boolean.TRUE);
		
		style |= SWT.V_SCROLL | SWT.H_SCROLL;
		
		return style;
	}
	
	private static GLData createDefaultData() {
		GLData data = new GLData();
		data.doubleBuffer = true;
		data.stencilSize = 8;
		return data;
	}
	
	int foreContext;
	int backContext;

	private Surface surface;
	private DirectContext context;
	private BackendRenderTarget renderTarget;

	public FigureCanvasGL(Composite parent) {
		this(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND, createDefaultData());
	}
	
	public FigureCanvasGL(Composite parent, int style) {
		this(parent, style, createDefaultData());
	}
	
	public FigureCanvasGL(Composite parent, int style, GLData data) {
		super(checkStyle(parent, style), parent, new LightweightSystemGL());
		
		Display display = parent.getDisplay();
		
		display.setData(USE_OWNDC_KEY, Boolean.FALSE);
		
		initGL(parent.getDisplay(), data, (style & SWT.MULTI) != 0);
		
		//getLightweightSystem().setUpdateManager(new DeferredUpdateManagerGL());
		
		addListener(SWT.Dispose, e -> onDispose());
	}
	
	protected void initGL(Display display, GLData data, boolean createBackContext) {
		display.setData(USE_OWNDC_KEY, Boolean.FALSE);
		if (data == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		PIXELFORMATDESCRIPTOR pfd = new PIXELFORMATDESCRIPTOR();
		pfd.nSize = (short) PIXELFORMATDESCRIPTOR.sizeof;
		pfd.nVersion = 1;
		pfd.dwFlags = WGL.PFD_DRAW_TO_WINDOW | WGL.PFD_SUPPORT_OPENGL;
		pfd.dwLayerMask = WGL.PFD_MAIN_PLANE;
		pfd.iPixelType = (byte) WGL.PFD_TYPE_RGBA;
		if (data.doubleBuffer) pfd.dwFlags |= WGL.PFD_DOUBLEBUFFER;
		if (data.stereo) pfd.dwFlags |= WGL.PFD_STEREO;
		pfd.cRedBits = (byte) data.redSize;
		pfd.cGreenBits = (byte) data.greenSize;
		pfd.cBlueBits = (byte) data.blueSize;
		pfd.cAlphaBits = (byte) data.alphaSize;
		pfd.cDepthBits = (byte) data.depthSize;
		pfd.cStencilBits = (byte) data.stencilSize;
		pfd.cAccumRedBits = (byte) data.accumRedSize;
		pfd.cAccumGreenBits = (byte) data.accumGreenSize;
		pfd.cAccumBlueBits = (byte) data.accumBlueSize;
		pfd.cAccumAlphaBits = (byte) data.accumAlphaSize;
		pfd.cAccumBits = (byte) (pfd.cAccumRedBits + pfd.cAccumGreenBits + pfd.cAccumBlueBits + pfd.cAccumAlphaBits);

		int hDC = OS.GetDC(handle);
		int pixelFormat = WGL.ChoosePixelFormat(hDC, pfd);
		if (pixelFormat == 0 || !WGL.SetPixelFormat(hDC, pixelFormat, pfd)) {
			OS.ReleaseDC(handle, hDC);
			dispose();
			SWT.error(SWT.ERROR_UNSUPPORTED_DEPTH);
		}
		foreContext = WGL.wglCreateContext(hDC);
		if (foreContext == 0) {
			OS.ReleaseDC(handle, hDC);
			SWT.error(SWT.ERROR_NO_HANDLES);
		} else if (createBackContext) {
			backContext = WGL.wglCreateContext(hDC);
			if (backContext != 0) {
				boolean rc = WGL.wglShareLists(foreContext, backContext);
				if (!rc ) {
					WGL.wglDeleteContext(backContext);
					backContext = 0;
				}
			}
		}
		OS.ReleaseDC(handle, hDC);
	}
	
	public boolean setCurrent(boolean set) {
		return setCurrent(SWT.FOREGROUND, set);
	}
	
	public boolean setCurrent(int pos, boolean set) {
		int ctx = foreContext;
		if (SWT.BACKGROUND == pos) {
			//if (backContext == 0) ; //throw @error?
			if (backContext != 0)
				ctx = backContext;
		}
		return makeCurrent(ctx, set);
	}
	
	public void releaseContext() {
		if (backContext != 0) {
			if (WGL.wglDeleteContext(backContext))
				backContext = 0;
		}
		if (foreContext != 0) {
			if (WGL.wglGetCurrentContext() == foreContext)
				WGL.wglMakeCurrent(0, 0);
			if (WGL.wglDeleteContext(foreContext))
				foreContext = 0;
		}
	}
	
	public DirectContext getContext() {
		if (context == null) {
			setCurrent(SWT.FOREGROUND, true);
			context = DirectContext.makeGL();
		}
		return context;
	}
	
	public Surface getSurface() {
		checkWidget();
		if (context == null) {
			setCurrent(SWT.FOREGROUND, true);
			context = DirectContext.makeGL();
		}
		if (surface == null) {
			Rectangle rect = getClientArea();
			renderTarget = BackendRenderTarget.makeGL(rect.width, rect.height, 0, 8, 0, FramebufferFormat.GR_GL_RGBA8);
			surface = Surface.makeFromBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.getDisplayP3());
		}
		return surface;
	}
	/*
	public Canvas getCanvas() {
		checkWidget();
		if (context == null) {
			setCurrent(SWT.FOREGROUND, true);
			context = DirectContext.makeGL();
		}
		if (surface == null) {
			Rectangle rect = getClientArea();
			renderTarget = BackendRenderTarget.makeGL(rect.width, rect.height, 0, 8, 0, FramebufferFormat.GR_GL_RGBA8);
			surface = Surface.makeFromBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.getDisplayP3());
		}
		return surface.getCanvas();
	}
	//*/
	public void swapBuffers() {
		checkWidget();
		int hDC = OS.GetDC(handle);
		WGL.SwapBuffers(hDC);
		OS.ReleaseDC(handle, hDC);
	}
	
	protected boolean makeCurrent(int context, boolean set) {
		boolean rc = false;
		if (context != 0) {
			//synchronized (this) {
				int curContext = WGL.wglGetCurrentContext();
				if (set) {
					if (isDisposed()) SWT.error(SWT.ERROR_WIDGET_DISPOSED);
					if (curContext == context) {
						rc = true;
					} else {
						int hDC = OS.GetDC(handle);
						rc = WGL.wglMakeCurrent(hDC, context);
						OS.ReleaseDC(handle, hDC);
					}
				} else if (curContext == context) {
					rc = WGL.wglMakeCurrent(0, 0);
				}
			//}
		}
		return rc;
	}
	
	void releaseSurface() {
		if (surface != null) {
			surface.close();
			surface = null;
		}
		if (renderTarget != null) {
			renderTarget.close();
			renderTarget = null;
		}
	}
	
	private void releaseResource() {
		if (context != null) {
			context.close();
			context = null;
		}
	}
	
	protected void onResize() {
		releaseSurface();
	}
	
	protected void onDispose() {
		releaseResource();
		
		releaseContext();
	}
}
