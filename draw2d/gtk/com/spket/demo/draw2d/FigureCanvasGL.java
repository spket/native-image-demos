package com.spket.demo.draw2d;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.gtk.GDK;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.GdkWindowAttr;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.internal.opengl.glx.GLX;
import org.eclipse.swt.internal.opengl.glx.XVisualInfo;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.jetbrains.skija.BackendRenderTarget;
import org.jetbrains.skija.ColorSpace;
import org.jetbrains.skija.DirectContext;
import org.jetbrains.skija.FramebufferFormat;
import org.jetbrains.skija.Surface;
import org.jetbrains.skija.SurfaceColorFormat;
import org.jetbrains.skija.SurfaceOrigin;

public class FigureCanvasGL extends FigureCanvas {
	private static int checkStyle(int style) {
		style |= SWT.V_SCROLL | SWT.H_SCROLL;
		
		return style;
	}
	
	private static GLData createDefaultData() {
		GLData data = new GLData();
		data.doubleBuffer = true;
		data.stencilSize = 8;
		return data;
	}
	
	long foreContext;
	long backContext;

	//long context;
	long xWindow;
	long glWindow;
	XVisualInfo vinfo;
	static final int MAX_ATTRIBUTES = 32;

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
		super(checkStyle(style), parent, new LightweightSystemGL());
		
		initGL(data, style);
	}
	
	protected void initGL(GLData data, int style) {
		if (OS.IsWin32) SWT.error (SWT.ERROR_NOT_IMPLEMENTED);
		if (data == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		int glxAttrib [] = new int [MAX_ATTRIBUTES];
		int pos = 0;
		glxAttrib [pos++] = GLX.GLX_RGBA;
		if (data.doubleBuffer) glxAttrib [pos++] = GLX.GLX_DOUBLEBUFFER;
		if (data.stereo) glxAttrib [pos++] = GLX.GLX_STEREO;
		if (data.redSize > 0) {
			glxAttrib [pos++] = GLX.GLX_RED_SIZE;
			glxAttrib [pos++] = data.redSize;
		}
		if (data.greenSize > 0) {
			glxAttrib [pos++] = GLX.GLX_GREEN_SIZE;
			glxAttrib [pos++] = data.greenSize;
		}
		if (data.blueSize > 0) {
			glxAttrib [pos++] = GLX.GLX_BLUE_SIZE;
			glxAttrib [pos++] = data.blueSize;
		}
		if (data.alphaSize > 0) {
			glxAttrib [pos++] = GLX.GLX_ALPHA_SIZE;
			glxAttrib [pos++] = data.alphaSize;
		}
		if (data.depthSize > 0) {
			glxAttrib [pos++] = GLX.GLX_DEPTH_SIZE;
			glxAttrib [pos++] = data.depthSize;
		}
		if (data.stencilSize > 0) {
			glxAttrib [pos++] = GLX.GLX_STENCIL_SIZE;
			glxAttrib [pos++] = data.stencilSize;
		}
		if (data.accumRedSize > 0) {
			glxAttrib [pos++] = GLX.GLX_ACCUM_RED_SIZE;
			glxAttrib [pos++] = data.accumRedSize;
		}
		if (data.accumGreenSize > 0) {
			glxAttrib [pos++] = GLX.GLX_ACCUM_GREEN_SIZE;
			glxAttrib [pos++] = data.accumGreenSize;
		}
		if (data.accumBlueSize > 0) {
			glxAttrib [pos++] = GLX.GLX_ACCUM_BLUE_SIZE;
			glxAttrib [pos++] = data.accumBlueSize;
		}
		if (data.accumAlphaSize > 0) {
			glxAttrib [pos++] = GLX.GLX_ACCUM_ALPHA_SIZE;
			glxAttrib [pos++] = data.accumAlphaSize;
		}
		if (data.sampleBuffers > 0) {
			glxAttrib [pos++] = GLX.GLX_SAMPLE_BUFFERS;
			glxAttrib [pos++] = data.sampleBuffers;
		}
		if (data.samples > 0) {
			glxAttrib [pos++] = GLX.GLX_SAMPLES;
			glxAttrib [pos++] = data.samples;
		}
		glxAttrib [pos++] = 0;
		GTK.gtk_widget_realize (handle);
		long window = GTK.gtk_widget_get_window (handle);

		long xDisplay = GDK.gdk_x11_display_get_xdisplay(GDK.gdk_window_get_display(window));
		long infoPtr = GLX.glXChooseVisual (xDisplay, OS.XDefaultScreen (xDisplay), glxAttrib);
		if (infoPtr == 0) {
			dispose ();
			SWT.error (SWT.ERROR_UNSUPPORTED_DEPTH);
		}
		vinfo = new XVisualInfo ();
		GLX.memmove (vinfo, infoPtr, XVisualInfo.sizeof);
		OS.XFree (infoPtr);
		long screen = GDK.gdk_screen_get_default ();
		long gdkvisual = GDK.gdk_x11_screen_lookup_visual (screen, vinfo.visualid);
		long share = /* data.shareContext != null ? data.shareContext.context : */0;
		foreContext = GLX.glXCreateContext (xDisplay, vinfo, share, true);
		if (foreContext == 0) SWT.error (SWT.ERROR_NO_HANDLES);
		GdkWindowAttr attrs = new GdkWindowAttr ();
		attrs.width = 1;
		attrs.height = 1;
		attrs.event_mask = GDK.GDK_KEY_PRESS_MASK | GDK.GDK_KEY_RELEASE_MASK |
			GDK.GDK_FOCUS_CHANGE_MASK | GDK.GDK_POINTER_MOTION_MASK |
			GDK.GDK_BUTTON_PRESS_MASK | GDK.GDK_BUTTON_RELEASE_MASK |
			GDK.GDK_ENTER_NOTIFY_MASK | GDK.GDK_LEAVE_NOTIFY_MASK |
			GDK.GDK_EXPOSURE_MASK | GDK.GDK_POINTER_MOTION_HINT_MASK;
		attrs.window_type = GDK.GDK_WINDOW_CHILD;
		attrs.visual = gdkvisual;
		glWindow = GDK.gdk_window_new (window, attrs, GDK.GDK_WA_VISUAL);
		GDK.gdk_window_set_user_data (glWindow, handle);
		if ((style & SWT.NO_BACKGROUND) != 0) {
			//TODO: implement this on GTK3 as pixmaps are gone.
		}

		if (GTK.GTK4) {
			// TODO: Enable when the GdkWindow to GdkSurface changes are in
			//xWindow = GDK.gdk_x11_surface_get_xid(glWindow);
		} else {
			xWindow = GDK.gdk_x11_window_get_xid (glWindow);
		}

		GDK.gdk_window_show (glWindow);
		//*
		Listener listener = event -> {
			switch (event.type) {
			case SWT.Paint:
				int [] viewport = new int [4];
				GLX.glGetIntegerv (GLX.GL_VIEWPORT, viewport);
				GLX.glViewport (viewport [0],viewport [1],viewport [2],viewport [3]);
				break;
			case SWT.Resize:
				Rectangle clientArea = DPIUtil.autoScaleUp(getClientArea());
				GDK.gdk_window_move (glWindow, clientArea.x, clientArea.y);
				GDK.gdk_window_resize (glWindow, clientArea.width, clientArea.height);
				break;
			case SWT.Dispose:
				onDispose();
				break;
			}
		};
		
		addListener (SWT.Resize, listener);
		addListener (SWT.Paint, listener);
		addListener (SWT.Dispose, listener);
		//*/
	}
	
	public boolean setCurrent(boolean set) {
		return setCurrent(SWT.FOREGROUND, set);
	}
	
	public boolean setCurrent(int pos, boolean set) {
		long ctx = foreContext;
		if (SWT.BACKGROUND == pos) {
			//if (backContext == 0) ; //throw @error?
			if (backContext != 0)
				ctx = backContext;
		}
		return makeCurrent(ctx, set);
	}
	
	private void releaseContext() {
		long window1 = GTK.gtk_widget_get_window(handle);
		long xDisplay1 = gdk_x11_display_get_xdisplay(window1);
		if (backContext != 0) {
			GLX.glXDestroyContext(xDisplay1, backContext);
			backContext = 0;
		}
		if (foreContext != 0) {
			if (GLX.glXGetCurrentContext() == foreContext)
				GLX.glXMakeCurrent(xDisplay1, 0, 0);
			GLX.glXDestroyContext(xDisplay1, foreContext);
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
	
	public void swapBuffers() {
		checkWidget();
		long window = GTK.gtk_widget_get_window(handle);
		long xDisplay = gdk_x11_display_get_xdisplay(window);
		GLX.glXSwapBuffers(xDisplay, xWindow);
	}
	
	protected boolean makeCurrent(long context, boolean set) {
		boolean rc = false;
		if (context != 0) {
			//synchronized (this) {
				long curContext = GLX.glXGetCurrentContext();
				if (set) {
					if (isDisposed()) SWT.error(SWT.ERROR_WIDGET_DISPOSED);
					if (curContext == context) {
						rc = true;
					} else {
						long window = GTK.gtk_widget_get_window(handle);
						long xDisplay = gdk_x11_display_get_xdisplay(window);
						GLX.glXMakeCurrent(xDisplay, xWindow, context);
					}
				} else if (curContext == context) {
					long window = GTK.gtk_widget_get_window(handle);
					long xDisplay = gdk_x11_display_get_xdisplay(window);
					rc = GLX.glXMakeCurrent(xDisplay, 0, 0);
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

	private long gdk_x11_display_get_xdisplay(long window) {
		return GDK.gdk_x11_display_get_xdisplay(GDK.gdk_window_get_display(window));
	}
}
