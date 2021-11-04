package common;

import java.awt.BorderLayout;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class TaskManagementPanel extends JSplitPane
{
	/**
	 * No idea
	 */
	private static final long	serialVersionUID	= 3988887100451259417L;
	private final TaskTree		m_taskTree;
	private final TaskInfoPanel	m_taskInfoPanel;
	private final MainModel		m_mainModel;

	public TaskManagementPanel(final MainModel p_mainModel)
	{
		m_mainModel = p_mainModel;

		setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		final JPanel leftPanel = new JPanel(new BorderLayout());
		m_taskInfoPanel = new TaskInfoPanel(this);
		m_taskTree = new TaskTree(this);
		leftPanel.add(m_taskTree, BorderLayout.CENTER);

		final JPanel subPanel = new JPanel();
		final JButton addTaskButton = new JButton(Messages.getString("TaskManagementPanel.0")); //$NON-NLS-1$
		final JButton addSiblingButton = new JButton(Messages.getString("TaskManagementPanel.1")); //$NON-NLS-1$
		final JButton addSubtaskButton = new JButton(Messages.getString("TaskManagementPanel.2")); //$NON-NLS-1$
		final JButton deleteTaskButton = new JButton(Messages.getString("TaskManagementPanel.3")); //$NON-NLS-1$

		subPanel.add(addTaskButton);
		subPanel.add(addSiblingButton);
		subPanel.add(addSubtaskButton);
		subPanel.add(deleteTaskButton);
		leftPanel.add(subPanel, BorderLayout.SOUTH);

		setLeftComponent(leftPanel);
		setRightComponent(m_taskInfoPanel);

		addTaskButton.addActionListener(p_arg0 -> showNewTask(true));
		addSiblingButton.addActionListener(p_arg0 -> showNewTask(false));
		addSubtaskButton.addActionListener(p_arg0 -> showNewSubtask());
		deleteTaskButton.addActionListener(p_arg0 -> deleteCurrentTask());
	}

	public MainModel getMainModel()
	{
		return m_mainModel;
	}

	public TaskInfoPanel getTaskInfoPanel()
	{
		return m_taskInfoPanel;
	}

	public TaskTree getTaskTree()
	{
		return m_taskTree;
	}

	public void showNewTask(final boolean p_topLevel)
	{
		final LocalDate now = LocalDate.now();

		final DefaultMutableTreeNode selected = m_taskTree.getSelectedNode();
		DefaultMutableTreeNode parent = null;
		int parentID = Constants.NO_PARENT;

		if (!p_topLevel)
		{
			if (selected.getUserObject() instanceof Task)
			{
				parent = (DefaultMutableTreeNode) selected.getParent();
				parentID = ((Task) parent.getUserObject()).getID();
			}
			else
			{
				JOptionPane.showMessageDialog(m_taskInfoPanel, Messages.getString("TaskManagementPanel.9")); //$NON-NLS-1$
				return;
			}
		}

		final Task createdTask = new Task(
			m_mainModel.getNextTaskID(),
			parentID,
			false,
			Constants.NO_ID,
			Constants.DEFAULT_DESCRIPTION,
			Constants.DEFAULT_TASK_PRIORITY,
			false,
			now.getYear(),
			now.getMonthValue(),
			now.getDayOfMonth(),
			false,
			now.getYear(),
			now.getMonthValue(),
			now.getDayOfMonth(),
			false,
			1,
			RepeatPeriodType.DAYS,
			RepeatRestartType.ON_DUE_DATE,
			false,
			0,
			0,
			0);

		m_taskTree.addAndHighlightNode(createdTask, parent);
		m_mainModel.addTask(createdTask);
	}

	private void deleteCurrentTask()
	{
		if (!m_taskTree.canCurrentNodeBeDeleted())
		{
			return;
		}

		final Task currentTask = m_taskTree.getCurrentTask();
		final String desc = currentTask.getDescription();
		final int choice = JOptionPane
			.showConfirmDialog(
				this,
				Messages.getString("TaskManagementPanel.4") + desc + Messages.getString("TaskManagementPanel.5")); //$NON-NLS-1$ //$NON-NLS-2$
		if (choice == JOptionPane.YES_OPTION)
		{
			m_taskTree.deleteCurrentTask();
		}
		m_mainModel.getTasks().remove(currentTask.getID());
	}

	private void showNewSubtask()
	{
		final DefaultMutableTreeNode selectedNode = m_taskTree.getSelectedNode();
		if (selectedNode == null || selectedNode.getUserObject() instanceof String)
		{
			JOptionPane.showMessageDialog(this, Messages.getString("TaskManagementPanel.6")); //$NON-NLS-1$
		}
		else
		{
			final Task parentTask = (Task) selectedNode.getUserObject();
			if (parentTask.isCompleted())
			{
				JOptionPane.showMessageDialog(this, Messages.getString("TaskManagementPanel.7")); //$NON-NLS-1$
			}
			else
			{
				final Task childTask = new Task(
					m_mainModel.getNextTaskID(),
					parentTask.getID(),
					false,
					Constants.NO_ID,
					parentTask.getDescription() + Messages.getString("TaskManagementPanel.8"), //$NON-NLS-1$
					parentTask.getPriority(),
					parentTask.hasOpeningDate(),parentTask.getOpeningYear(),
					parentTask.getOpeningMonth(),
					parentTask.getOpeningDate(),
					parentTask.hasDueDate(),
					parentTask.getDueYear(),
					parentTask.getDueMonth(),
					parentTask.getDueDate(),
					parentTask.isRepeatingTask(),
					parentTask.getRepeatPeriodCount(),
					parentTask.getRepeatPeriodType(),
					parentTask.getRepeatRestartType(),
					false,
					0,
					0,
					0);

				m_taskTree.addAndHighlightNode(selectedNode, childTask);
				m_mainModel.addTask(childTask);
			}
		}
	}
}
