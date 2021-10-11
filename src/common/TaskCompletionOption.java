package common;

public enum TaskCompletionOption
{
	FINISHED_TODAY, FINISHED_PREVIOUSLY;

	@Override
	public String toString()
	{
		switch (this)
		{
			case FINISHED_PREVIOUSLY:
				return Messages.getString("TaskCompletionOption.0"); //$NON-NLS-1$
			case FINISHED_TODAY:
				return Messages.getString("TaskCompletionOption.1"); //$NON-NLS-1$
			default:
				throw new RuntimeException();
		}
	}
}
