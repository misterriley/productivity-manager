package common;

import java.io.File;
import java.util.Collection;
import java.util.TreeMap;

public class TaskIO
{
	private static final String			ID_COLUMN_NAME						= Messages.getString("TaskIO.0"); //$NON-NLS-1$
	private static final String			PARENT_ID_COLUMN_NAME				= Messages.getString("TaskIO.1"); //$NON-NLS-1$
	private static final String			CHILDREN_ARE_ORDERED_COLUMN_NAME	= Messages.getString("TaskIO.2"); //$NON-NLS-1$
	private static final String			LIVE_CHILD_ID_COLUMN_NAME			= Messages.getString("TaskIO.3"); //$NON-NLS-1$
	private static final String			DESC_COLUMN_NAME					= Messages.getString("TaskIO.4"); //$NON-NLS-1$
	private static final String			PRIORITY_COLUMN_NAME				= Messages.getString("TaskIO.5"); //$NON-NLS-1$
	private static final String			HAS_OPENING_DATE_COLUMN_NAME		= Messages.getString("TaskIO.6"); //$NON-NLS-1$
	private static final String			OPENING_YEAR_COLUMN_NAME			= Messages.getString("TaskIO.7"); //$NON-NLS-1$
	private static final String			OPENING_MONTH_COLUMN_NAME			= Messages.getString("TaskIO.8"); //$NON-NLS-1$
	private static final String			OPENING_DATE_COLUMN_NAME			= Messages.getString("TaskIO.9"); //$NON-NLS-1$
	private static final String			HAS_DUE_DATE_COLUMN_NAME			= Messages.getString("TaskIO.10"); //$NON-NLS-1$
	private static final String			DUE_YEAR_COLUMN_NAME				= Messages.getString("TaskIO.11"); //$NON-NLS-1$
	private static final String			DUE_MONTH_COLUMN_NAME				= Messages.getString("TaskIO.12"); //$NON-NLS-1$
	private static final String			DUE_DATE_COLUMN_NAME				= Messages.getString("TaskIO.13"); //$NON-NLS-1$
	private static final String			IS_REPEATING_TASK_COLUMN_NAME		= Messages.getString("TaskIO.14"); //$NON-NLS-1$
	private static final String			REPEAT_PERIOD_COUNT_COLUMN_NAME		= Messages.getString("TaskIO.15"); //$NON-NLS-1$
	private static final String			REPEAT_PERIOD_TYPE_COLUMN_NAME		= Messages.getString("TaskIO.16"); //$NON-NLS-1$
	private static final String			REPEAT_RESTART_TYPE_COLUMN_NAME		= Messages.getString("TaskIO.17"); //$NON-NLS-1$
	private static final String			IS_COMPLETED_COLUMN_NAME			= Messages.getString("TaskIO.18"); //$NON-NLS-1$
	private static final String			COMPLETION_YEAR_COLUMN_NAME			= Messages.getString("TaskIO.19"); //$NON-NLS-1$
	private static final String			COMPLETION_MONTH_COLUMN_NAME		= Messages.getString("TaskIO.20"); //$NON-NLS-1$
	private static final String			COMPLETION_DATE_COLUMN_NAME			= Messages.getString("TaskIO.21"); //$NON-NLS-1$

	private static final String	TASKS_FILE_NAME						= Messages.getString("TaskIO.22"); //$NON-NLS-1$

	private static File getTasksFile()
	{
		final String fileLoc = PMMainController.getProperty(PropertiesManager.SAVE_LOCATION_KEY);
		return new File(fileLoc, TASKS_FILE_NAME);
	}

	/**
	 * @param p_file
	 * @return A TaskDB object built from the given file. The file should be in the
	 *         SimpleDB format and have a header. The header should have columns for
	 *         each element of the constructor of the task object.
	 */
	public static TreeMap<Integer, Task> loadTasksFromFile()
	{
		final TreeMap<Integer, Task> ret = new TreeMap<>();
		final SimpleDB simpleDB = SimpleDBIO.loadFromFile(getTasksFile(), true);

		for (int i = 0; i < simpleDB.numRows(); i++)
		{
			final int id = simpleDB.get(i, ID_COLUMN_NAME).asInt();
			final int parentID = simpleDB.get(i, PARENT_ID_COLUMN_NAME).asInt();
			final boolean childrenAreOrdered = simpleDB.get(i, CHILDREN_ARE_ORDERED_COLUMN_NAME).asBoolean();
			final int liveChildID = simpleDB.get(i, LIVE_CHILD_ID_COLUMN_NAME).asInt();
			final String description = simpleDB.get(i, DESC_COLUMN_NAME).asString();
			final int priority = simpleDB.get(i, PRIORITY_COLUMN_NAME).asInt();

			final boolean hasOpeningDate = simpleDB.get(i, HAS_OPENING_DATE_COLUMN_NAME).asBoolean();
			final int openingYear = simpleDB.get(i, OPENING_YEAR_COLUMN_NAME).asInt();
			final int openingMonth = simpleDB.get(i, OPENING_MONTH_COLUMN_NAME).asInt();
			final int openingDate = simpleDB.get(i, OPENING_DATE_COLUMN_NAME).asInt();

			final boolean hasDueDate = simpleDB.get(i, HAS_DUE_DATE_COLUMN_NAME).asBoolean();
			final int dueYear = simpleDB.get(i, DUE_YEAR_COLUMN_NAME).asInt();
			final int dueMonth = simpleDB.get(i, DUE_MONTH_COLUMN_NAME).asInt();
			final int dueDate = simpleDB.get(i, DUE_DATE_COLUMN_NAME).asInt();

			final boolean isRepeatingTask = simpleDB.get(i, IS_REPEATING_TASK_COLUMN_NAME).asBoolean();
			final int repeatPeriodCount = simpleDB.get(i, REPEAT_PERIOD_COUNT_COLUMN_NAME).asInt();
			final RepeatPeriodType repeatPeriodType = simpleDB.get(i, REPEAT_PERIOD_TYPE_COLUMN_NAME)
					.asRepeatPeriodType();
			final RepeatRestartType repeatRestartType = simpleDB.get(i, REPEAT_RESTART_TYPE_COLUMN_NAME)
					.asRepeatRestartType();

			final boolean isCompleted = simpleDB.get(i, IS_COMPLETED_COLUMN_NAME).asBoolean();
			final int completionYear = simpleDB.get(i, COMPLETION_YEAR_COLUMN_NAME).asInt();
			final int completionMonth = simpleDB.get(i, COMPLETION_MONTH_COLUMN_NAME).asInt();
			final int completionDate = simpleDB.get(i, COMPLETION_DATE_COLUMN_NAME).asInt();

			final Task task = new Task(id, parentID, childrenAreOrdered, liveChildID, description, priority,
					hasOpeningDate, openingYear, openingMonth, openingDate, hasDueDate, dueYear, dueMonth, dueDate,
					isRepeatingTask, repeatPeriodCount, repeatPeriodType, repeatRestartType, isCompleted,
					completionYear, completionMonth, completionDate);
			ret.put(id, task);
		}

		return ret;
	}

	public static void main(String[] p_args)
	{
		final SimpleDB db = SimpleDBIO.loadFromFile(getTasksFile(), true);
		SimpleDBIO.addColumnToDB(db, LIVE_CHILD_ID_COLUMN_NAME, String.valueOf(Constants.NO_ID));
		SimpleDBIO.saveToFile(db, getTasksFile());
	}

	public static void saveTasksToFile(final Collection<Task> p_tasks)
	{
		final SimpleDB toSave = new SimpleDB();
		int rowIndex = 0;
		for (final Task task : p_tasks)
		{
			toSave.set(rowIndex, ID_COLUMN_NAME, String.valueOf(task.getID()));
			toSave.set(rowIndex, PARENT_ID_COLUMN_NAME, String.valueOf(task.getParentID()));
			toSave.set(rowIndex, CHILDREN_ARE_ORDERED_COLUMN_NAME, String.valueOf(task.childrenAreOrdered()));
			toSave.set(rowIndex, LIVE_CHILD_ID_COLUMN_NAME, String.valueOf(task.getLiveChildID()));
			toSave.set(rowIndex, DESC_COLUMN_NAME, task.getDescription());
			toSave.set(rowIndex, PRIORITY_COLUMN_NAME, String.valueOf(task.getPriority()));

			toSave.set(rowIndex, HAS_OPENING_DATE_COLUMN_NAME, String.valueOf(task.hasOpeningDate()));
			toSave.set(rowIndex, OPENING_YEAR_COLUMN_NAME, String.valueOf(task.getOpeningYear()));
			toSave.set(rowIndex, OPENING_MONTH_COLUMN_NAME, String.valueOf(task.getOpeningMonth()));
			toSave.set(rowIndex, OPENING_DATE_COLUMN_NAME, String.valueOf(task.getOpeningDate()));

			toSave.set(rowIndex, HAS_DUE_DATE_COLUMN_NAME, String.valueOf(task.hasDueDate()));
			toSave.set(rowIndex, DUE_YEAR_COLUMN_NAME, String.valueOf(task.getDueYear()));
			toSave.set(rowIndex, DUE_MONTH_COLUMN_NAME, String.valueOf(task.getDueMonth()));
			toSave.set(rowIndex, DUE_DATE_COLUMN_NAME, String.valueOf(task.getDueDate()));

			toSave.set(rowIndex, IS_REPEATING_TASK_COLUMN_NAME, String.valueOf(task.isRepeatingTask()));
			toSave.set(rowIndex, REPEAT_PERIOD_COUNT_COLUMN_NAME, String.valueOf(task.getRepeatPeriodCount()));
			toSave.set(rowIndex, REPEAT_PERIOD_TYPE_COLUMN_NAME, String.valueOf(task.getRepeatPeriodType()));
			toSave.set(rowIndex, REPEAT_RESTART_TYPE_COLUMN_NAME, String.valueOf(task.getRepeatRestartType()));

			toSave.set(rowIndex, IS_COMPLETED_COLUMN_NAME, String.valueOf(task.isCompleted()));
			toSave.set(rowIndex, COMPLETION_YEAR_COLUMN_NAME, String.valueOf(task.getCompletionYear()));
			toSave.set(rowIndex, COMPLETION_MONTH_COLUMN_NAME, String.valueOf(task.getCompletionMonth()));
			toSave.set(rowIndex, COMPLETION_DATE_COLUMN_NAME, String.valueOf(task.getCompletionDate()));
			rowIndex++;
		}

		SimpleDBIO.saveToFile(toSave, getTasksFile());
	}
}