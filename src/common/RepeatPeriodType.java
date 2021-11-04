package common;

import java.util.Calendar;

public enum RepeatPeriodType
{
	DAYS, WEEKS, MONTHS, YEARS;

	public static RepeatPeriodType parseRepeatPeriodType(final String p_value)
	{
		for (final RepeatPeriodType rpt : RepeatPeriodType.values())
		{
			if (rpt.toString().equals(p_value))
			{
				return rpt;
			}
		}

		return null;
	}

	public int toCalendarType()
	{
		switch (this)
		{
			case DAYS:
				return Calendar.DATE;
			case MONTHS:
				return Calendar.MONTH;
			case WEEKS:
				return Calendar.WEEK_OF_YEAR;
			case YEARS:
				return Calendar.YEAR;
		}

		throw new RuntimeException();
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case DAYS:
				return Messages.getString("RepeatPeriodType.0"); //$NON-NLS-1$
			case MONTHS:
				return Messages.getString("RepeatPeriodType.1"); //$NON-NLS-1$
			case WEEKS:
				return Messages.getString("RepeatPeriodType.2"); //$NON-NLS-1$
			case YEARS:
				return Messages.getString("RepeatPeriodType.3"); //$NON-NLS-1$
			default:
				break;
		}
		throw new RuntimeException();
	}
}
