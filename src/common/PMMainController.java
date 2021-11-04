package common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import datepicker.DateModel;

/**
 * @author Steve Riley
 */
public class PMMainController
{
	private static final Random			RANDOM	= new Random();
	private static PropertiesManager	m_propertiesManager;
	private static MainView				m_view;
	private static MainModel			m_model;

	static
	{
		m_propertiesManager = new PropertiesManager();
	}

	public static void convertToOrderedParent(final Task p_task)
	{
		boolean reset = false;
		final int liveChildID = p_task.getLiveChildID();
		if (liveChildID == Constants.NO_ID)
		{
			reset = true;
		}

		if (liveChildID != Constants.NO_ID)
		{
			final Task liveChildTask = m_model.getTasks().get(liveChildID);
			if (liveChildTask == null || liveChildTask.getNode().getParent() != p_task.getNode())
			{
				reset = true;
			}
		}

		if (reset)
		{
			final DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) p_task.getNode().getFirstChild();
			final Task liveChildTask = (Task) firstChild.getUserObject();
			p_task.setLiveChildID(liveChildTask.getID());
		}
	}

	public static LocalDate getDateObject(final DateModel<?> p_model)
	{
		final int year = p_model.getYear();
		final int month = p_model.getMonthButWrong() + 1;
		final int date = p_model.getDay();

		return LocalDate.of(year, month, date);
	}

	public static String getProperty(final String p_key)
	{
		return m_propertiesManager.getProperty(p_key);
	}

	public static double getSelectionTemperature(final int p_sensitivity, final int p_maxSensitivity)
	{
		return Constants.SELECTION_TEMP_MULTIPLIER * p_sensitivity / (p_maxSensitivity - p_sensitivity);
	}

	public static boolean isTaskLive(final Task p_task)
	{
		if (p_task == null)
		{
			return false;
		}

		if (p_task.isSnoozing())
		{
			return false;
		}

		if (p_task.isCompleted())
		{
			return false;
		}

		if (p_task.getNode().getChildCount() > 0)
		{
			return false;
		}

		final TreeNode[] path = p_task.getNode().getPath();
		for (int i = 1; i < path.length; i++)
		{
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path[i];
			final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path[i - 1];

			if (node.getUserObject() instanceof Task && parent.getUserObject() instanceof Task)
			{
				final Task parentTask = (Task) parent.getUserObject();
				final Task childTask = (Task) node.getUserObject();

				if (parentTask.childrenAreOrdered() && parentTask.getLiveChildID() != childTask.getID())
				{
					// basically, if anything in this stack is not a live node,
					// the task isn't live
					return false;
				}
			}
		}

		if (!p_task.hasOpeningDate())
		{
			return true;
		}

		if (p_task.getOpeningLocalDate().isBefore(LocalDate.now()))
		{
			return true;
		}

		return p_task.getOpeningLocalDate().equals(LocalDate.now());
	}

	/**
	 * @param p_args
	 */
	public static void main(final String[] p_args)
	{
		runProductivityManager();
	}

	public static boolean markSelectedTaskAsComplete()
	{
		final Task currentTask = m_view.getTaskManagementPanel().getTaskTree().getCurrentTask();
		if (currentTask == null)
		{
			return true;
		}

		if (currentTask.getNode().getChildCount() > 0)
		{
			JOptionPane.showMessageDialog(m_view.getFrame(), Messages.getString("PMMainController.0")); //$NON-NLS-1$
			return true;
		}

		// figure out correct date of completion
		final TaskCompletionOption[] options = TaskCompletionOption.values();
		final int response = JOptionPane
			.showOptionDialog(
				m_view.getFrame(),
				Messages.getString("PMMainController.1"),                //$NON-NLS-1$
				Messages.getString("PMMainController.2"), //$NON-NLS-1$
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,null,
				options,
				null);

		if (response == JOptionPane.CLOSED_OPTION)
		{
			return false;
		}

		LocalDate completionDate = null;

		switch (options[response])
		{
			case FINISHED_PREVIOUSLY:
				completionDate = DatePickerDialog.getDate(m_view.getFrame());
				break;
			case FINISHED_TODAY:
				completionDate = LocalDate.now();
				break;
			default:
				break;
		}

		currentTask.setCompletionLocalDate(completionDate);

		final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) currentTask.getNode().getParent();
		if (parentNode.getUserObject() instanceof Task)
		{
			final Task parentTask = (Task) parentNode.getUserObject();

			if (parentTask.childrenAreOrdered() && parentTask.getLiveChildID() == currentTask.getID())
			{
				final DefaultMutableTreeNode sibling =
					(DefaultMutableTreeNode) parentNode.getChildAfter(currentTask.getNode());
				if (sibling == null)
				{
					parentTask.setLiveChildID(Constants.NO_ID);
				}
				else
				{
					final Task siblingTask = (Task) sibling.getUserObject();
					parentTask.setLiveChildID(siblingTask.getID());
				}
			}
		}

		// do repeating task stuff
		if (currentTask.isRepeatingTask())
		{
			if (currentTask.hasDueDate())
			{
				final LocalDate currentDueDate = currentTask.getDueLocalDate();
				LocalDate baseDate;

				switch (currentTask.getRepeatRestartType())
				{
					case ON_DUE_DATE:
						baseDate = currentTask.getDueLocalDate();
						break;
					case UPON_COMPLETION:
						baseDate = LocalDate.now();
						break;
					default:
						throw new RuntimeException();
				}

				LocalDate nextDueDate;
				switch (currentTask.getRepeatPeriodType())
				{
					case DAYS:
						nextDueDate = baseDate.plusDays(currentTask.getRepeatPeriodCount());
						break;
					case MONTHS:
						nextDueDate = baseDate.plusMonths(currentTask.getRepeatPeriodCount());
						break;
					case WEEKS:
						nextDueDate = baseDate.plusWeeks(currentTask.getRepeatPeriodCount());
						break;
					case YEARS:
						nextDueDate = baseDate.plusYears(currentTask.getRepeatPeriodCount());
						break;
					default:
						throw new RuntimeException();
				}
				currentTask.setDueLocalDate(nextDueDate);

				if (currentTask.hasOpeningDate())
				{
					final long daysAdvanced = ChronoUnit.DAYS.between(currentDueDate, nextDueDate);
					final LocalDate newOpeningDate = currentTask.getOpeningLocalDate().plusDays(daysAdvanced);
					currentTask.setOpeningLocalDate(newOpeningDate);
				}
			}
			else
			{
				// unsure what to do here - there's a warning about it in the UI
			}

			m_view.getTaskManagementPanel().getTaskTree().refresh(currentTask.getNode());
			m_view.getTaskManagementPanel().getTaskInfoPanel().setCurrentTask(currentTask);
		}
		else
		{
			currentTask.setCompleted(true);
			m_view.getTaskManagementPanel().getTaskTree().moveCurrentTaskToCompletedTree();
		}

		saveModel();
		JOptionPane.showMessageDialog(m_view.getFrame(), Messages.getString("PMMainController.3")); //$NON-NLS-1$
		return true;
	}

	public static Task pullTask(final Collection<Task> p_tasks, final int p_sensitivity, final int p_maxSensitivity)
	{
		final double temp = getSelectionTemperature(p_sensitivity, p_maxSensitivity);
		final Task[] taskArray = p_tasks.toArray(new Task[] {});
		final double[] weights = new double[p_tasks.size()];

		double sum = 0;
		final ArrayList<Task> currentBest = new ArrayList<>();

		for (int i = 0; i < taskArray.length; i++)
		{
			if (isTaskLive(taskArray[i]))
			{
				final int priority = taskArray[i].getPriority();
				if (currentBest.size() == 0 || currentBest.get(0).getPriority() == priority)
				{
					currentBest.add(taskArray[i]);
				}
				else
					if (currentBest.get(0).getPriority() < priority)
					{
						currentBest.clear();
						currentBest.add(taskArray[i]);
					}

				weights[i] = Math.exp(temp * priority);
				sum += weights[i];
			}
		}

		if (Double.isFinite(temp))
		{
			if (sum == 0)
			{
				return null;
			}
			final double rnd = RANDOM.nextDouble() * sum;

			double comp = 0;
			for (int i = 0; i < weights.length; i++)
			{
				comp += weights[i];
				if (comp >= rnd)
				{
					return taskArray[i];
				}
			}

			throw new RuntimeException();
		}
		if (currentBest.size() == 0)
		{
			return null;
		}
		final int rndIndex = RANDOM.nextInt(currentBest.size());
		return currentBest.get(rndIndex);
	}

	public static void renormalizePriorities()
	{
		m_view.getTaskManagementPanel().getTaskInfoPanel().saveUIToCurrentTask();

		double sum = 0;
		double sumOfSquares = 0;
		final int count = m_model.getTasks().size();

		for (final Task task : m_model.getTasks().values())
		{
			final double priority = task.getPriority();
			sum += priority;
			sumOfSquares += Math.pow(priority, 2);
		}

		final double mean = sum / count;
		final double variance = (sumOfSquares - sum * sum / count) / (count - 1);
		final double sd = Math.sqrt(variance);

		for (final Task task : m_model.getTasks().values())
		{
			final double oldPriority = task.getPriority();
			final double zScore = (oldPriority - mean) / sd;
			double newPriority = Constants.TASK_TARGET_PRIORITY_MEAN + zScore * Constants.TASK_TARGET_PRIORITY_SD;

			if (newPriority > Constants.MAX_TASK_PRIORITY)
			{
				newPriority = Constants.MAX_TASK_PRIORITY;
			}

			if (newPriority < 0)
			{
				newPriority = 0;
			}

			task.setPriority((int) Math.round(newPriority));
		}

		saveModel();
		m_view.getTaskManagementPanel().getTaskInfoPanel().refreshCurrentTask();
	}

	public static void saveModel()
	{
		TaskIO.saveTasksToFile(m_model.getTasks().values());
	}

	private static void runProductivityManager()
	{
		m_model = new MainModel();
		m_model.setTasks(TaskIO.loadTasksFromFile());

		m_view = new MainView(m_model);
		m_view.makeItSo();

		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			m_view.getTaskManagementPanel().getTaskInfoPanel().saveUIToCurrentTask();
			saveModel();
			m_propertiesManager.save();
		}));
	}
}
