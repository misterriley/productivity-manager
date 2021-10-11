package common;

import java.util.TreeMap;

/**
 * Dumb class that holds data relevant to the productivity manager.
 *
 * @author Steve Riley
 */
public class MainModel
{
	private TreeMap<Integer, Task>			m_tasks;
	private int								m_maxTaskIDSoFar;
	private int								m_maxRequirementIDSoFar;

	public MainModel()
	{
		m_maxTaskIDSoFar = -1;
		m_maxRequirementIDSoFar = -1;
	}

	public void addTask(Task p_createdTask)
	{
		m_tasks.put(p_createdTask.getID(), p_createdTask);
	}

	public synchronized int getNextRequirementID()
	{
		m_maxRequirementIDSoFar++;
		return m_maxRequirementIDSoFar;
	}

	public synchronized int getNextTaskID()
	{
		m_maxTaskIDSoFar++;
		return m_maxTaskIDSoFar;
	}

	/**
	 * @return The map of task objects
	 */
	public TreeMap<Integer, Task> getTasks()
	{
		return m_tasks;
	}

	/**
	 * Sets the tasks object to the passed in map
	 *
	 * @param p_tasks
	 */
	public void setTasks(final TreeMap<Integer, Task> p_tasks)
	{
		m_tasks = p_tasks;
		for (final Integer id : p_tasks.keySet())
		{
			if (id > m_maxTaskIDSoFar)
			{
				m_maxTaskIDSoFar = id;
			}
		}
	}
}
