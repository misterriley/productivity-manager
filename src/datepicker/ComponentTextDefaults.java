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
package datepicker;

public final class ComponentTextDefaults
{

	public enum Key
	{

		// Months of the year
		JANUARY(Messages.getString("ComponentTextDefaults.0"), Messages.getString("ComponentTextDefaults.1"), 0), //$NON-NLS-1$//$NON-NLS-2$
		FEBRUARY(Messages.getString("ComponentTextDefaults.2"), Messages.getString("ComponentTextDefaults.3"), 1), //$NON-NLS-1$//$NON-NLS-2$
		MARCH(
			Messages.getString("ComponentTextDefaults.4"), //$NON-NLS-1$
			Messages.getString("ComponentTextDefaults.5"),      //$NON-NLS-1$
			2),
		APRIL(Messages.getString("ComponentTextDefaults.6"), Messages.getString("ComponentTextDefaults.7"), 3), //$NON-NLS-1$//$NON-NLS-2$
		MAY(Messages.getString("ComponentTextDefaults.8"), Messages.getString("ComponentTextDefaults.9"), 4), //$NON-NLS-1$//$NON-NLS-2$
		JUNE(Messages.getString("ComponentTextDefaults.10"), Messages.getString("ComponentTextDefaults.11"), 5), //$NON-NLS-1$//$NON-NLS-2$
		JULY(      
			Messages.getString("ComponentTextDefaults.12"), //$NON-NLS-1$
			Messages.getString("ComponentTextDefaults.13"), //$NON-NLS-1$
			6),
		AUGUST(Messages.getString("ComponentTextDefaults.14"), Messages.getString("ComponentTextDefaults.15"), 7), //$NON-NLS-1$//$NON-NLS-2$
		SEPTEMBER(
			Messages.getString("ComponentTextDefaults.16"),     //$NON-NLS-1$
			Messages.getString("ComponentTextDefaults.17"), //$NON-NLS-1$
			8),
		OCTOBER(Messages.getString("ComponentTextDefaults.18"), Messages.getString("ComponentTextDefaults.19"), 9), //$NON-NLS-1$//$NON-NLS-2$
		NOVEMBER(
			Messages.getString("ComponentTextDefaults.20"), //$NON-NLS-1$
			Messages.getString("ComponentTextDefaults.21"),     //$NON-NLS-1$
			10),
		DECEMBER(Messages.getString("ComponentTextDefaults.22"), Messages.getString("ComponentTextDefaults.23"), 11), //$NON-NLS-1$ //$NON-NLS-2$

		// Days of the week abbreviated where necessary
		SUN(Messages.getString("ComponentTextDefaults.24"), Messages.getString("ComponentTextDefaults.25"), 0), //$NON-NLS-1$//$NON-NLS-2$
		MON(Messages.getString("ComponentTextDefaults.26"), Messages.getString("ComponentTextDefaults.27"), 1), //$NON-NLS-1$//$NON-NLS-2$
		TUE(Messages.getString("ComponentTextDefaults.28"), Messages.getString("ComponentTextDefaults.29"), 2), //$NON-NLS-1$//$NON-NLS-2$
		WED(
			Messages.getString("ComponentTextDefaults.30"), //$NON-NLS-1$
			Messages.getString("ComponentTextDefaults.31"),        //$NON-NLS-1$
			3),
		THU(Messages.getString("ComponentTextDefaults.32"), Messages.getString("ComponentTextDefaults.33"), 4), //$NON-NLS-1$//$NON-NLS-2$
		FRI(Messages.getString("ComponentTextDefaults.34"), Messages.getString("ComponentTextDefaults.35"), 5), //$NON-NLS-1$//$NON-NLS-2$
		SAT(Messages.getString("ComponentTextDefaults.36"), Messages.getString("ComponentTextDefaults.37"), 6);     //$NON-NLS-1$ //$NON-NLS-2$

		public static Key getDowKey(final int index)
		{
			for (final Key key : values())
			{
				if (Messages.getString("ComponentTextDefaults.38").equals(key.getKind()) && index == key.getIndex()) //$NON-NLS-1$
				{
					return key;
				}
			}
			return null;
		}

		public static Key getMonthKey(final int index)
		{
			for (final Key key : values())
			{
				if (Messages.getString("ComponentTextDefaults.39").equals(key.getKind()) && index == key.getIndex()) //$NON-NLS-1$
				{
					return key;
				}
			}
			return null;
		}

		private final String property;

		private final String kind;

		private final Integer index;

		private Key(final String property, final String kind, final Integer index)
		{
			this.property = property;
			this.kind = kind;
			this.index = index;
		}

		public Integer getIndex()
		{
			return index;
		}

		public String getKind()
		{
			return kind;
		}

		public String getProperty()
		{
			return property;
		}

	}

	private static ComponentTextDefaults instance;

	public static ComponentTextDefaults getInstance()
	{
		if (instance == null)
		{
			instance = new ComponentTextDefaults();
		}
		return instance;
	}
}
