package common;

public enum CompletionBehavior
{
	WAIT_UNTIL_REPEAT_PERIOD_ELAPSES, REPEAT_IMMEDIATELY;

	@Override
	public String toString()
	{
		switch (this)
		{
			case REPEAT_IMMEDIATELY:
				return Messages.getString("CompletionBehavior.0"); //$NON-NLS-1$
			case WAIT_UNTIL_REPEAT_PERIOD_ELAPSES:
				return Messages.getString("CompletionBehavior.1"); //$NON-NLS-1$
		}

		throw new RuntimeException();
	}
}
