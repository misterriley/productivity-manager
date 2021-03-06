/**
 * Copyright 2004 Juan Heyns. All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met: 1. Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. THIS
 * SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL JUAN HEYNS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. The views and conclusions
 * contained in the software and documentation are those of the authors and
 * should not be interpreted as representing official policies, either expressed
 * or implied, of Juan Heyns.
 */
package datepicker.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Created on 26 Mar 2004
 *
 * @author Juan Heyns
 */
public class JNextIcon implements Icon
{

	private int	width;
	private int	height;

	private final int[]	xPoints	= new int[3];
	private final int[]	yPoints	= new int[3];

	private boolean			doubleArrow;
	private final boolean	enabled;

	public JNextIcon(final int width, final int height, final boolean doubleArrow, final boolean enabled)
	{
		setDimension(width, height);
		this.doubleArrow = doubleArrow;
		this.enabled = enabled;
	}

	public boolean getDoubleArrow()
	{
		return doubleArrow;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}

	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y)
	{
		if (enabled)
		{
			g.setColor(Color.BLACK);
		}
		else
		{
			g.setColor(Color.GRAY);
		}

		if (doubleArrow)
		{
			xPoints[0] = x + width / 2;
			yPoints[0] = y + height / 2;

			xPoints[1] = x;
			yPoints[1] = y - 1;

			xPoints[2] = x;
			yPoints[2] = y + height;

			g.fillPolygon(xPoints, yPoints, 3);

			xPoints[0] = x + width;
			yPoints[0] = y + height / 2;

			xPoints[1] = x + width / 2;
			yPoints[1] = y - 1;

			xPoints[2] = x + width / 2;
			yPoints[2] = y + height;

			g.fillPolygon(xPoints, yPoints, 3);
		}
		else
		{
			xPoints[0] = x + width;
			yPoints[0] = y + height / 2;

			xPoints[1] = x;
			yPoints[1] = y - 1;

			xPoints[2] = x;
			yPoints[2] = y + height;

			g.fillPolygon(xPoints, yPoints, 3);
		}
	}

	public void setDimension(final int width, final int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setDoubleArrow(final boolean doubleArrow)
	{
		this.doubleArrow = doubleArrow;
	}

}