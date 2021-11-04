package common;

import java.time.LocalDate;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Task object. Contains information about the task and how it relates to other
 * Tasks. Is dumb.
 *
 * @author Steve Riley
 */
public class Task
{
	private final int				m_id;
	private final int				m_parentID;
	private String					m_description;
	private int						m_priority;
	private boolean					m_hasOpeningDate;
	private int						m_openingYear;
	private int						m_openingMonth;
	private int						m_openingDate;
	private boolean					m_hasDueDate;
	private int						m_dueYear;
	private int						m_dueMonth;
	private int						m_dueDate;
	private boolean					m_isRepeatingTask;
	private int						m_repeatPeriodCount;
	private RepeatPeriodType		m_repeatPeriodType;
	private RepeatRestartType		m_repeatRestartType;
	private boolean					m_isCompleted;
	private int						m_completionYear;
	private int						m_completionMonth;
	private int						m_completionDate;
	private DefaultMutableTreeNode	m_node;
	private boolean					m_childrenAreOrdered;
	private int						m_liveChildID;			// only used when m_childAreOrdered == true. Indicates which
								// child is the current live task.
	private volatile boolean m_isSnoozing;			// volatile because it is written to by a scheduling thread

	/**
	 * @param p_id
	 * @param p_description
	 * @param p_priority
	 * @param p_isCompleted
	 * @param p_completionYear
	 * @param p_completionMonth
	 * @param p_completionDate
	 */
	public Task(
		final int p_id,
		final int p_parentID,
		final boolean p_childrenAreOrdered,
		final int p_liveChildID,
		final String p_description,
		final int p_priority,
		final boolean p_hasOpeningDate,
		final int p_openingYear,
		final int p_openingMonth,
		final int p_openingDate,
		final boolean p_hasDueDate,
		final int p_dueYear,
		final int p_dueMonth,
		final int p_dueDate,
		final boolean p_isRepeatingTask,
		final int p_repeatPeriodCount,
		final RepeatPeriodType p_repeatPeriodType,
		final RepeatRestartType p_repeatRestartType,
		final boolean p_isCompleted,
		final int p_completionYear,
		final int p_completionMonth,
		final int p_completionDate)
	{
		m_id = p_id;
		m_parentID = p_parentID;
		m_childrenAreOrdered = p_childrenAreOrdered;
		m_liveChildID = p_liveChildID;
		m_description = p_description;
		m_priority = p_priority;
		m_hasOpeningDate = p_hasOpeningDate;
		m_openingYear = p_openingYear;
		m_openingMonth = p_openingMonth;
		m_openingDate = p_openingDate;
		m_hasDueDate = p_hasDueDate;
		m_dueDate = p_dueDate;
		m_dueMonth = p_dueMonth;
		m_dueYear = p_dueYear;
		m_isRepeatingTask = p_isRepeatingTask;
		m_repeatPeriodType = p_repeatPeriodType;
		m_repeatPeriodCount = p_repeatPeriodCount;
		m_repeatRestartType = p_repeatRestartType;
		m_isCompleted = p_isCompleted;
		m_completionYear = p_completionYear;
		m_completionMonth = p_completionMonth;
		m_completionDate = p_completionDate;
		m_isSnoozing = false;
	}

	public boolean childrenAreOrdered()
	{
		// TODO Auto-generated method stub
		return m_childrenAreOrdered;
	}

	public int getCompletionDate()
	{
		return m_completionDate;
	}

	public LocalDate getCompletionLocalDate()
	{
		return LocalDate.of(m_completionYear, m_completionMonth, m_completionDate);
	}

	public int getCompletionMonth()
	{
		return m_completionMonth;
	}

	public int getCompletionYear()
	{
		return m_completionYear;
	}

	/**
	 * @return The text description of this task
	 */
	public String getDescription()
	{
		return m_description;
	}

	public int getDueDate()
	{
		return m_dueDate;
	}

	public LocalDate getDueLocalDate()
	{
		return LocalDate.of(m_dueYear, m_dueMonth, m_dueDate);
	}

	public int getDueMonth()
	{
		return m_dueMonth;
	}

	public int getDueYear()
	{
		return m_dueYear;
	}

	/**
	 * @return The integer id of this task
	 */
	public int getID()
	{
		return m_id;
	}

	public int getLiveChildID()
	{
		return m_liveChildID;
	}

	public DefaultMutableTreeNode getNode()
	{
		return m_node;
	}

	public int getOpeningDate()
	{
		return m_openingDate;
	}

	public LocalDate getOpeningLocalDate()
	{
		return LocalDate.of(m_openingYear, m_openingMonth, m_openingDate);
	}

	public int getOpeningMonth()
	{
		return m_openingMonth;
	}

	public int getOpeningYear()
	{
		return m_openingYear;
	}

	public int getParentID()
	{
		return m_parentID;
	}

	/**
	 * @return The priority of this task
	 */
	public int getPriority()
	{
		return m_priority;
	}

	public int getRepeatPeriodCount()
	{
		return m_repeatPeriodCount;
	}

	public RepeatPeriodType getRepeatPeriodType()
	{
		return m_repeatPeriodType;
	}

	public RepeatRestartType getRepeatRestartType()
	{
		return m_repeatRestartType;
	}

	public boolean hasDueDate()
	{
		return m_hasDueDate;
	}

	public boolean hasOpeningDate()
	{
		return m_hasOpeningDate;
	}

	public boolean hasParent()
	{
		return m_parentID != -1;
	}

	public boolean isCompleted()
	{
		return m_isCompleted;
	}

	public boolean isRepeatingTask()
	{
		return m_isRepeatingTask;
	}

	public boolean isSnoozing()
	{
		return m_isSnoozing;
	}

	public void setChildrenAreOrdered(final boolean p_childrenAreOrdered)
	{
		m_childrenAreOrdered = p_childrenAreOrdered;
	}

	public void setCompleted(final boolean p_isCompleted)
	{
		m_isCompleted = p_isCompleted;
	}

	public void setCompletionDate(final int p_completionDate)
	{
		m_completionDate = p_completionDate;
	}

	public void setCompletionLocalDate(final LocalDate p_localDate)
	{
		m_completionDate = p_localDate.getDayOfMonth();
		m_completionMonth = p_localDate.getMonthValue();
		m_completionYear = p_localDate.getYear();
	}

	public void setCompletionMonth(final int p_completionMonth)
	{
		m_completionMonth = p_completionMonth;
	}

	public void setCompletionYear(final int p_completionYear)
	{
		m_completionYear = p_completionYear;
	}

	public void setDescription(final String p_description)
	{
		m_description = p_description;
	}

	public void setDueDate(final int p_dueDate)
	{
		m_dueDate = p_dueDate;
	}

	public void setDueLocalDate(final LocalDate p_localDate)
	{
		m_dueDate = p_localDate.getDayOfMonth();
		m_dueMonth = p_localDate.getMonthValue();
		m_dueYear = p_localDate.getYear();
	}

	public void setDueMonth(final int p_dueMonth)
	{
		m_dueMonth = p_dueMonth;
	}

	public void setDueYear(final int p_dueYear)
	{
		m_dueYear = p_dueYear;
	}

	public void setHasDueDate(final boolean p_hasDueDate)
	{
		m_hasDueDate = p_hasDueDate;
	}

	public void setHasOpeningDate(final boolean p_hasOpeningDate)
	{
		m_hasOpeningDate = p_hasOpeningDate;
	}

	public void setLiveChildID(final int p_liveChildID)
	{
		m_liveChildID = p_liveChildID;
	}

	public void setNode(final DefaultMutableTreeNode p_node)
	{
		m_node = p_node;
	}

	public void setOpeningDate(final int p_openingDate)
	{
		m_openingDate = p_openingDate;
	}

	public void setOpeningLocalDate(final LocalDate p_localDate)
	{
		m_openingDate = p_localDate.getDayOfMonth();
		m_openingMonth = p_localDate.getMonthValue();
		m_openingYear = p_localDate.getYear();
	}

	public void setOpeningMonth(final int p_openingMonth)
	{
		m_openingMonth = p_openingMonth;
	}

	public void setOpeningYear(final int p_openingYear)
	{
		m_openingYear = p_openingYear;
	}

	/**
	 * @param p_priority
	 */
	public void setPriority(final int p_priority)
	{
		m_priority = p_priority;
	}

	public void setRepeatingTask(final boolean p_isRepeatingTask)
	{
		m_isRepeatingTask = p_isRepeatingTask;
	}

	public void setRepeatPeriodCount(final int p_repeatPeriodCount)
	{
		m_repeatPeriodCount = p_repeatPeriodCount;
	}

	public void setRepeatPeriodType(final RepeatPeriodType p_repeatPeriodType)
	{
		m_repeatPeriodType = p_repeatPeriodType;
	}

	public void setRepeatRestartType(final RepeatRestartType p_repeatRestartType)
	{
		m_repeatRestartType = p_repeatRestartType;
	}

	public void setSnoozing(final boolean p_isSnoozing)
	{
		m_isSnoozing = p_isSnoozing;
	}

	@Override
	public String toString()
	{
		return m_description;
	}
}
