package com.spket.demo.draw2d;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.TextLayout;
import org.jetbrains.skija.Canvas;
import org.jetbrains.skija.Paint;
import org.jetbrains.skija.PaintMode;
import org.jetbrains.skija.Rect;
import org.jetbrains.skija.svg.DOM;

public class SkiaGraphics extends Graphics {
	private Canvas canvas;
	private Paint cFill;
	private Paint cStroke;
	
	private org.jetbrains.skija.Path path;
	private Rectangle clip;
	
	private int savePoint;
	
	public SkiaGraphics(Canvas canvas, Rectangle region) {
		this.canvas = canvas;
		
		savePoint = canvas.save();
		
		clip = region;
		cFill = new Paint();
		cFill.setMode(PaintMode.FILL).setColor(0xffffffff);
		cStroke = new Paint();
		cStroke.setMode(PaintMode.STROKE);
		path = new org.jetbrains.skija.Path();
	}
	
	public void draw(DOM dom, float x, float y) {
		canvas.translate(x, y);
		dom.render(canvas);
		canvas.translate(-x, -y);
	}

	@Override
	public void clipRect(Rectangle r) {
		//TODO
		canvas.clipRect(Rect.makeXYWH(r.x, r.y, r.width, r.height), false);
	}

	@Override
	public void dispose() {
		cFill.close();
		cStroke.close();
		path.close();
		
		canvas.restoreToCount(savePoint);
	}

	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int length) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawFocus(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawImage(Image srcImage, int x, int y) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolygon(PointList points) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolyline(PointList points) {
		int[] ps = points.toIntArray();
		float[] fps = new float[ps.length];
		
		for (int i = 0; i < ps.length; i++)
			fps[i] = ps[i];
		
		path.addPoly(fps, false);
		
		canvas.drawPath(path, cStroke);
	}

	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		canvas.drawRect(Rect.makeXYWH(x, y, width, height), cStroke);
	}

	@Override
	public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(String s, int x, int y) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawText(String s, int x, int y) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int length) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillOval(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillPolygon(PointList points) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		canvas.drawRect(Rect.makeXYWH(x, y, width, height), cFill);
	}

	@Override
	public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillString(String s, int x, int y) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillText(String s, int x, int y) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getClip(Rectangle rect) {
		rect.setBounds(clip);
		
		return rect;
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public FontMetrics getFontMetrics() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getForegroundColor() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLineWidthFloat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getXORMode() {
		return false;
	}

	@Override
	public void popState() {
		canvas.restore();
	}

	@Override
	public void pushState() {
		canvas.save();
	}

	@Override
	public void restoreState() {
		canvas.restore();
		canvas.save();
	}

	@Override
	public void scale(double amount) {
		float s = (float) amount;
		
		canvas.scale(s, s);
	}

	@Override
	public void setBackgroundColor(Color rgb) {
		cFill.setARGB(rgb.getAlpha(), rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	@Override
	public void setClip(Rectangle r) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFont(Font f) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setForegroundColor(Color rgb) {
		cStroke.setARGB(rgb.getAlpha(), rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	@Override
	public void setLineStyle(int style) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLineWidth(int width) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLineWidthFloat(float width) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLineMiterLimit(float miterLimit) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setXORMode(boolean b) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(int dx, int dy) {
		canvas.translate(dx, dy);
	}

	@Override
	public void clipPath(Path path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPath(Path path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPoint(int x, int y) {
		canvas.drawPoint(x, y, cStroke);
	}

	@Override
	public void drawPolygon(int[] points) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolyline(int[] points) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawText(String s, int x, int y, int style) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawTextLayout(TextLayout layout, int x, int y, int selectionStart, int selectionEnd, Color selectionForeground, Color selectionBackground) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillPath(Path path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillPolygon(int[] points) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public double getAbsoluteScale() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public boolean getAdvanced() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAlpha() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAntialias() {
		return SWT.ON;
	}

	@Override
	public int getFillRule() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getInterpolation() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public LineAttributes getLineAttributes() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLineCap() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLineJoin() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public float getLineMiterLimit() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTextAntialias() {
		// TODO Auto-generated method stub
		return SWT.ON;
	}

	@Override
	public void rotate(float degrees) {
		canvas.rotate(degrees);
	}

	@Override
	public void scale(float horizontal, float vertical) {
		canvas.scale(horizontal, vertical);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAdvanced(boolean advanced) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAntialias(int value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBackgroundPattern(Pattern pattern) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClip(Path path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFillRule(int rule) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setForegroundPattern(Pattern pattern) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInterpolation(int interpolation) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLineAttributes(LineAttributes attributes) {
		// TODO Auto-generated method stub
		//super.setLineAttributes(attributes);
	}

	@Override
	public void setLineCap(int cap) {
		// TODO Auto-generated method stub
		//super.setLineCap(cap);
	}

	@Override
	public void setLineDash(int[] dash) {
		// TODO Auto-generated method stub
		//super.setLineDash(dash);
	}

	@Override
	public void setLineDash(float[] value) {
		// TODO Auto-generated method stub
		//super.setLineDash(value);
	}

	@Override
	public void setLineDashOffset(float value) {
		// TODO Auto-generated method stub
		//super.setLineDashOffset(value);
	}

	@Override
	public void setLineJoin(int join) {
		// TODO Auto-generated method stub
		//super.setLineJoin(join);
	}

	@Override
	public void setTextAntialias(int value) {
		// TODO Auto-generated method stub
		//super.setTextAntialias(value);
	}

	@Override
	public void shear(float horz, float vert) {
		//canvas.
	}

	@Override
	public void translate(float dx, float dy) {
		canvas.translate(dx, dy);
	}
}
