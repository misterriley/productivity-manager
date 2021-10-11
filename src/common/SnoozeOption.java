package common;

public enum SnoozeOption
{
	FOR_SESSION, FOR_TIME;

	@Override
	public String toString()
	{
		switch (this)
		{
			case FOR_SESSION:
				return Messages.getString("SnoozeOption.0"); //$NON-NLS-1$
			case FOR_TIME:
				return Messages.getString("SnoozeOption.1"); //$NON-NLS-1$
			default:
				throw new RuntimeException(Messages.getString("SnoozeOption.2")); //$NON-NLS-1$

		}
	}
}
