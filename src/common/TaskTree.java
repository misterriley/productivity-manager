package common;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class TaskTree extends JPanel implements TreeSelectionListener
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= 6673081031349265845L;

	private final JTree						m_tree;
	private final DefaultMutableTreeNode	m_liveTasksNode;
	private final DefaultMutableTreeNode	m_completedTasksNode;
	private final TaskManagementPanel		m_taskManagementPanel;

	public TaskTree(TaskManagementPanel p_taskManagementPanel)
	{
		super(new GridLayout(1, 0));

		m_taskManagementPanel = p_taskManagementPanel;

		// Create the nodes
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode(Messages.getString("TaskTree.0")); //$NON-NLS-1$
		m_liveTasksNode = new DefaultMutableTreeNode(Messages.getString("TaskTree.1")); //$NON-NLS-1$
		m_completedTasksNode = new DefaultMutableTreeNode(Messages.getString("TaskTree.2")); //$NON-NLS-1$

		top.add(m_liveTasksNode);
		top.add(m_completedTasksNode);

		for (final Task task : p_taskManagementPanel.getMainModel().getTasks().values())
		{
			final DefaultMutableTreeNode node = new DefaultMutableTreeNode(task);
			task.setNode(node);

			if (task.isCompleted())
			{
				m_completedTasksNode.add(node);
			}
			else if (!task.hasParent())
			{
				m_liveTasksNode.add(node);
			}
		}

		for (final Task task : p_taskManagementPanel.getMainModel().getTasks().values())
		{
			if (task.hasParent() && task.getNode().getParent() == null)
			{
				final Task parent = p_taskManagementPanel.getMainModel().getTasks().get(task.getParentID());
				parent.getNode().add(task.getNode());
			}
		}

		// Create a tree that allows one selection at a time.
		m_tree = new JTree(top);
		m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		m_tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		final JScrollPane treeView = new JScrollPane(m_tree);

		// Add the split pane to this panel.
		add(treeView);
	}

	public void addAndHighlightNode(DefaultMutableTreeNode p_parentNode, Task p_childTask)
	{
		final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(p_childTask);
		p_childTask.setNode(newNode);

		final DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();
		model.insertNodeInto(newNode, p_parentNode, p_parentNode.getChildCount());
		p_parentNode.add(newNode);
		model.reload(p_parentNode);

		setSelectedTask(p_childTask);
	}

	public void addAndHighlightNode(Task p_task, DefaultMutableTreeNode p_parent)
	{
		final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(p_task);
		p_task.setNode(newNode);

		final DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();

		final DefaultMutableTreeNode parent = p_parent == null ? m_liveTasksNode : p_parent;
		model.insertNodeInto(newNode, parent, parent.getChildCount());

		parent.add(newNode);
		model.reload(parent);

		setSelectedTask(p_task);
	}

	public boolean canCurrentNodeBeDeleted()
	{
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();
		return node != m_completedTasksNode && node != m_liveTasksNode && node != m_tree.getModel().getRoot();
	}

	public void deleteCurrentTask()
	{
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode nextInLine = node.getNextSibling();
		if (nextInLine == null)
		{
			nextInLine = node.getPreviousSibling();
		}
		if (nextInLine == null)
		{
			nextInLine = (DefaultMutableTreeNode)node.getParent();
		}
		final DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();
		final DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
		model.removeNodeFromParent(node);
		model.reload(parent);

		final TreePath tp = new TreePath(nextInLine.getPath());
		m_tree.setSelectionPath(tp);
	}

	public Task getCurrentTask()
	{
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();
		if (node != null && node.getUserObject() instanceof Task)
		{
			return (Task)node.getUserObject();
		}

		return null;
	}

	public DefaultMutableTreeNode getSelectedNode()
	{
		return (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();
	}

	public void moveCurrentTaskToCompletedTree()
	{
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode nextInLine = node.getNextSibling();
		if (nextInLine == null)
		{
			nextInLine = node.getPreviousSibling();
		}
		if (nextInLine == null)
		{
			nextInLine = (DefaultMutableTreeNode)node.getParent();
		}
		final DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();

		model.removeNodeFromParent(node);
		model.reload(m_liveTasksNode);

		model.insertNodeInto(node, m_completedTasksNode, m_completedTasksNode.getChildCount());
		m_completedTasksNode.add(node);
		model.reload(m_completedTasksNode);

		final TreePath tp = new TreePath(node.getPath());
		m_tree.setSelectionPath(tp);
		m_tree.expandPath(tp);
	}

	public void refresh(DefaultMutableTreeNode p_node)
	{
		final DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();
		model.reload(p_node);
	}

	public void refreshSelectedNode()
	{
		refresh((DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent());
	}

	public void setSelectedTask(Task p_task)
	{
		final TreePath tp = new TreePath(p_task.getNode().getPath());
		m_tree.setSelectionPath(tp);
		// m_tree.expandPath(tp);
	}

	/** Required by TreeSelectionListener interface. */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getLastSelectedPathComponent();

		if (node == null)
		{
			return;
		}

		final Object nodeInfo = node.getUserObject();
		m_taskManagementPanel.getTaskInfoPanel().saveUIToCurrentTask();
		if (nodeInfo instanceof String)
		{
			m_taskManagementPanel.getTaskInfoPanel().setCurrentTask(null);
		}
		else
		{
			m_taskManagementPanel.getTaskInfoPanel().setCurrentTask((Task)nodeInfo);
		}
	}
}
