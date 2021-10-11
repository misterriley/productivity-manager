package common;

/**
 * A set of exposed constants that all classes in this package can see
 *
 * @author Steve Riley
 */
public class Constants
{
	public static final String	DEFAULT_DESCRIPTION				= Messages.getString("Constants.0"); //$NON-NLS-1$
	public static final int		DEFAULT_PRIORITY_SENSITIVITY	= 50;
	public static final int		MAX_PRIORITY_SENSITIVITY		= 100;
	public static final int		DEFAULT_TASK_PRIORITY			= 50;
	public static final int		MAX_TASK_PRIORITY				= 100;
	public static final int		NO_PARENT						= -1;
	public static final int		NO_ID							= -1;
	public static final int		TASK_TARGET_PRIORITY_MEAN		= MAX_TASK_PRIORITY / 2;
	public static final double	TASK_TARGET_PRIORITY_SD			= MAX_TASK_PRIORITY / 6.0;
	private static final double	MUTLITPLIER_PER_TENTH			= 2.5;
	public static final double	SELECTION_TEMP_MULTIPLIER		= Math.log(MUTLITPLIER_PER_TENTH)
			/ (MAX_TASK_PRIORITY / 10.0);
	public static final int		WIDTH							= 1200;
	public static final int		HEIGHT							= (int)(WIDTH * .618);
	public static final int     DEFAULT_SNOOZE_MINUTES = 15;
}
