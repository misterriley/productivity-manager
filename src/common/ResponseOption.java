package common;

public enum ResponseOption
{
	COMPLETE, SNOOZE;

	@Override
	public String toString()
	{
		switch (this)
		{
			case COMPLETE:
				return Messages.getString("ResponseOption.0"); //$NON-NLS-1$
			case SNOOZE:
				return Messages.getString("ResponseOption.1"); //$NON-NLS-1$
		}

		throw new RuntimeException();
	}
}
