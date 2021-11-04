package common;

public enum RepeatRestartType
{
	UPON_COMPLETION, ON_DUE_DATE;

	public static RepeatRestartType parseRepeatRestartType(final String p_value)
	{
		for (final RepeatRestartType rrt : RepeatRestartType.values())
		{
			if (rrt.toString().equals(p_value))
			{
				return rrt;
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case UPON_COMPLETION:
				return Messages.getString("RepeatRestartType.0"); //$NON-NLS-1$
			case ON_DUE_DATE:
				return Messages.getString("RepeatRestartType.1"); //$NON-NLS-1$
		}
		throw new RuntimeException();
	}
}
