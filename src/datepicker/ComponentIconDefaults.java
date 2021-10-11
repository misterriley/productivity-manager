/**
 Copyright 2004 Juan Heyns. All rights reserved.
 Redistribution and use in source and binary forms, with or without modification, are
 permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright notice, this list of
 conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list
 of conditions and the following disclaimer in the documentation and/or other materials
 provided with the distribution.
 THIS SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JUAN HEYNS OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 The views and conclusions contained in the software and documentation are those of the
 authors and should not be interpreted as representing official policies, either expressed
 or implied, of Juan Heyns.
 */
package datepicker;

import javax.swing.Icon;

import datepicker.graphics.JNextIcon;
import datepicker.graphics.JPreviousIcon;

public final class ComponentIconDefaults
{

	private static ComponentIconDefaults instance;

	public static ComponentIconDefaults getInstance()
	{
		if (instance == null)
		{
			instance = new ComponentIconDefaults();
		}
		return instance;
	}

	private Icon nextMonthIconEnabled;
	private Icon nextYearIconEnabled;
	private Icon previousMonthIconEnabled;
	private Icon previousYearIconEnabled;
	private Icon nextMonthIconDisabled;
	private Icon nextYearIconDisabled;
	private Icon previousMonthIconDisabled;
	private Icon previousYearIconDisabled;

	private Icon popupButtonIcon;

	private ComponentIconDefaults()
	{

		nextMonthIconEnabled = new JNextIcon(4, 7, false, true);
		nextYearIconEnabled = new JNextIcon(8, 7, true, true);
		previousMonthIconEnabled = new JPreviousIcon(4, 7, false, true);
		previousYearIconEnabled = new JPreviousIcon(8, 7, true, true);
		nextMonthIconDisabled = new JNextIcon(4, 7, false, false);
		nextYearIconDisabled = new JNextIcon(8, 7, true, false);
		previousMonthIconDisabled = new JPreviousIcon(4, 7, false, false);
		previousYearIconDisabled = new JPreviousIcon(8, 7, true, false);
		popupButtonIcon = null;
	}

	public Icon getNextMonthIconDisabled()
	{
		return nextMonthIconDisabled;
	}

	public Icon getNextMonthIconEnabled()
	{
		return nextMonthIconEnabled;
	}

	public Icon getNextYearIconDisabled()
	{
		return nextYearIconDisabled;
	}

	public Icon getNextYearIconEnabled()
	{
		return nextYearIconEnabled;
	}

	public Icon getPopupButtonIcon()
	{
		return popupButtonIcon;
	}

	public Icon getPreviousMonthIconDisabled()
	{
		return previousMonthIconDisabled;
	}

	public Icon getPreviousMonthIconEnabled()
	{
		return previousMonthIconEnabled;
	}

	public Icon getPreviousYearIconDisabled()
	{
		return previousYearIconDisabled;
	}

	public Icon getPreviousYearIconEnabled()
	{
		return previousYearIconEnabled;
	}

	public void setNextMonthIconDisabled(Icon nextMonthIconDisabled)
	{
		this.nextMonthIconDisabled = nextMonthIconDisabled;
	}

	public void setNextMonthIconEnabled(Icon nextMonthIconEnabled)
	{
		this.nextMonthIconEnabled = nextMonthIconEnabled;
	}

	public void setNextYearIconDisabled(Icon nextYearIconDisabled)
	{
		this.nextYearIconDisabled = nextYearIconDisabled;
	}

	public void setNextYearIconEnabled(Icon nextYearIconEnabled)
	{
		this.nextYearIconEnabled = nextYearIconEnabled;
	}

	public void setPopupButtonIcon(Icon popupButtonIcon)
	{
		this.popupButtonIcon = popupButtonIcon;
	}

	public void setPreviousMonthIconDisabled(Icon previousMonthIconDisabled)
	{
		this.previousMonthIconDisabled = previousMonthIconDisabled;
	}

	public void setPreviousMonthIconEnabled(Icon previousMonthIconEnabled)
	{
		this.previousMonthIconEnabled = previousMonthIconEnabled;
	}

	public void setPreviousYearIconDisabled(Icon previousYearIconDisabled)
	{
		this.previousYearIconDisabled = previousYearIconDisabled;
	}

	public void setPreviousYearIconEnabled(Icon previousYearIconEnabled)
	{
		this.previousYearIconEnabled = previousYearIconEnabled;
	}

}
