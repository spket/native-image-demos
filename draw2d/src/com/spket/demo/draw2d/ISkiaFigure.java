package com.spket.demo.draw2d;

import org.jetbrains.skija.Canvas;

public interface ISkiaFigure {
	default void paint(Canvas canvas) {
		int sp = canvas.save();
		try {
			paintFigure(canvas);
			paintClientArea(canvas);
			paintBorder(canvas);
		} finally {
			canvas.restoreToCount(sp);
		}
	}
	
	void paintBorder(Canvas canvas);
	void paintFigure(Canvas canvas);
	void paintClientArea(Canvas canvas);
}
