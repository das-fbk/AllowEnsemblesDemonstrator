package eu.allowensembles.robustness.controller;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.ProcessEngineFacade;
import eu.allowensembles.controller.events.StepEvent;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.robustness.controller.Message.MType;
import eu.allowensembles.robustness.presentation.ReplicationView.ActivityState;
import eu.allowensembles.robustness.presentation.RobustnessView;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

/**
 * Created by sim on 11/18/15.
 */
public class RobustnessController {

	/**
	 * States a transaction can have
	 */
	private enum TransactionState {
		FIRST_RUN, MASTER_ELECTED_PASSIVE, MASTER_RELECTED, REPLICAS_NOTIFIED_OF_ROLE, ACITIVIY_EXECUTED, UPDATE_SENT, ACK_RECEIVED, TRANSACTION_COMPLETE, WAITING_FOR_NEXT_RUN,

		// error processing states
		TIMEOUT_OCCURED, VCREQUEST_SENT, VOTE_RECEIVED, STATE_DETERMINED, STATE_UPDATE_SENT,
		
		// active election states
		MASTER_ELECTED_ACTIVE,
	}

	/**
	 * All Link failures that could occur
	 */
	public enum Failure {
		LINK_12_FAILED, LINK_23_FAILED,
	}

	private static final Logger logger = LogManager.getLogger(ProcessEngineFacade.class);
	private final Replica[] replicas;
	private TransactionState currentState = TransactionState.FIRST_RUN;
	private ArrayList<Message> list1 = new ArrayList<>(), list2 = new ArrayList<>();
	private HashSet<Failure> failures = new HashSet<>();
	private boolean passiveReplication = true;
	private boolean stepping = false;
	private RobustnessAnnotationHandler annotationHandler;
	private MainWindow window;
	private ArrayList<ProcessActivity> executedActivities = new ArrayList<>();
	private final RobustnessView robustnessView;
	private boolean activeStateDrawn = false;
	
	private HashMap<String, Integer> futureMasters = new HashMap<>();

	private ProcessDiagram currentProcessDiagram;

	private ProcessActivity currentActivity;
	private ProcessActivity nextActivity;

	private int currentMaster = -1;
	private int compensateMaster = -1;
	private int newMaster = -1;
	private int otherNode = -1;

	private HashMap<String, Integer> nextMasters = new HashMap<>();

	public RobustnessController(MainWindow window) {
		annotationHandler = new RobustnessAnnotationHandler();
		this.robustnessView = new RobustnessView(this);
		replicas = new Replica[3];
		for (int i = 0; i < 3; i++) {
			replicas[i] = new Replica(i);
		}

		this.window = window;
		MainController.register(this);
	}

	@Subscribe
	public void onStep(StepEvent se) {
		try {
			currentProcessDiagram = window.getController().getCurrentRefinedProcessDiagram();
			currentProcessDiagram = window.getController().getProcessEngineFacade().getCompleteProcessDiagram(currentProcessDiagram);

			assignFutureMasters(false);

			boolean setNextActivity = false;
			for (ProcessActivity activity : currentProcessDiagram.getActivities()) {
				if (setNextActivity) {
					nextActivity = activity;
					break;
				}
				if (!activity.isAbstract() && activity.isExecuted() && !executedActivities.contains(activity)) {
					setCurrentActivity(activity);
					setNextActivity = true;
				}
			}

			robustnessView.getWorkflowView().updateWorkflowView(currentProcessDiagram, (isFinished() || activeStateDrawn), currentActivity, executedActivities);
			
			if (currentProcessDiagram != null) {
				clearFuture();
				drawFuture(isStepping() && !activeStateDrawn && !isFinished());
			}
			drawMessages();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * Toggle the current activity in the workflowView
	 * 
	 * This method is called whenever an activity is drawn in the replicationView
	 */
	private void toggleCurrentActivityInWorkflowView() {
		ProcessDiagram pd = window.getController().getCurrentRefinedProcessDiagram();
		pd = window.getController().getProcessEngineFacade().getCompleteProcessDiagram(pd);
		robustnessView.getWorkflowView().updateWorkflowView(pd, true, currentActivity, executedActivities);
		clearFuture();
		drawFuture(false);
	}

	private void drawMessages() {
		for (ProcessActivity activity : currentProcessDiagram.getActivities()) {
			if (!activity.isAbstract() && activity.isExecuted() && !executedActivities.contains(activity)) {
				executedActivities.add(activity);
				setCurrentActivity(activity);
				clearFuture();
				if (isStepping()) {
					robustnessView.enableStepButtoon();
				} else {
					execute();
				}
				clearFuture();
				drawFuture(isStepping() && !activeStateDrawn && !isFinished());
			}
		}
	}

	/**
	 * Set the currently performed activity. used to print the activity name and
	 * choose how to render
	 * 
	 * @param currentActivity
	 */
	public void setCurrentActivity(ProcessActivity currentActivity) {
		if (currentState != TransactionState.TRANSACTION_COMPLETE && currentState != TransactionState.FIRST_RUN && currentState != TransactionState.WAITING_FOR_NEXT_RUN) {
			finishActivity();
		}
		
		this.currentActivity = currentActivity;
		passiveReplication = !annotationHandler.getActivity(currentActivity).isActivelyReplicated();


		currentState = TransactionState.WAITING_FOR_NEXT_RUN;
	}

	/**
	 * Execute current activity. In case of stepping, only perform one micro step
	 */
	public void execute() {
		if (tooManyFailures()) {
			JOptionPane.showMessageDialog(robustnessView,
					"Too many Failures, please make sure two nodes are available and can communicate");
			return;
		}
		if (stepping) {
			step();
		} else {
			do {
				step();
			} while (currentState != TransactionState.TRANSACTION_COMPLETE);
		}
	}

	/**
	 * Perform one micro step
	 */
	public void step() {

		switch (currentState) {
		case TRANSACTION_COMPLETE:
			stepping = false;
		case FIRST_RUN:
		case WAITING_FOR_NEXT_RUN:
			activeStateDrawn = false;
			list2.clear();
			if (currentMaster == -1 || replicas[currentMaster].isOnline()
					&& replicas[futureMasters.get(currentActivity.getName())].isOnline()
					&& !isPartitioned(futureMasters.get(currentActivity.getName()))) {
				currentMaster = futureMasters.get(currentActivity.getName());;
				robustnessView.setNewMaster(currentMaster);
			} else {
				initiateElection();
				break;
			}

			if (passiveReplication) {
				currentState = TransactionState.MASTER_ELECTED_PASSIVE;
			} else {
				currentState = TransactionState.MASTER_ELECTED_ACTIVE;
			}
			break;

		case TIMEOUT_OCCURED:

			robustnessView.setNewMaster(newMaster);
			robustnessView.getReplicationView().drawMessage(new Message(newMaster, otherNode, MType.VCREQUEST));
			currentState = TransactionState.VCREQUEST_SENT;
			break;

		case VCREQUEST_SENT:

			robustnessView.getReplicationView().drawMessage(new Message(otherNode, newMaster, MType.VOTE));
			currentState = TransactionState.VOTE_RECEIVED;
			break;

		case VOTE_RECEIVED:

			robustnessView.getReplicationView().drawActivityForOne(newMaster, "Determine State", ActivityState.FAILURE);
			currentState = TransactionState.STATE_DETERMINED;
			break;

		case STATE_DETERMINED:

			robustnessView.getReplicationView().drawMessage(new Message(newMaster, otherNode, MType.UPDATE_STATE));
			currentState = TransactionState.STATE_UPDATE_SENT;
			break;

		case STATE_UPDATE_SENT:

			robustnessView.getReplicationView().drawMessage(new Message(otherNode, newMaster, MType.ACK));

			// set new master
			replicas[currentMaster].degrade();
			currentMaster = newMaster;
			replicas[currentMaster].promote();

			// reset variables
			otherNode = -1;
			newMaster = -1;

			// allow changing failure states
			robustnessView.allowFailureChanges(true);

			// replan future
			assignFutureMasters(true);
			clearFuture();
			drawFuture(true);

			if (passiveReplication) {
				currentState = TransactionState.MASTER_ELECTED_PASSIVE;
			} else {
				if (activeStateDrawn) {
					activeStateDrawn = false;
					currentState = TransactionState.TRANSACTION_COMPLETE;
				} else {
					currentState = TransactionState.MASTER_ELECTED_ACTIVE;
				}
			}
			break;

		case MASTER_ELECTED_PASSIVE:
			if (!replicas[currentMaster].isOnline()) {
				initiateElection();
			} else {
				drawState();
				currentState = TransactionState.REPLICAS_NOTIFIED_OF_ROLE;
			}
			break;
			
		case MASTER_ELECTED_ACTIVE:
			
			list1.clear();
			drawActivelyReplicatedState();
			activeStateDrawn = true;
			currentState = TransactionState.TRANSACTION_COMPLETE;
			break;

		case MASTER_RELECTED:

			// reroute NotifyMasterMessage and drop all others
			list1 = new ArrayList<>();
			list1.add(new Message(0, currentMaster, MType.NOTIFY_MASTER));

			list2.clear();
			drawState();
			currentState = TransactionState.REPLICAS_NOTIFIED_OF_ROLE;
			break;

		case REPLICAS_NOTIFIED_OF_ROLE:

			if (!replicas[currentMaster].isOnline()) {
				compensateMaster = currentMaster;
				initiateElection();
				break;
			}
			// notify all replicas of new roles
			for (Message m : list1) {
				list2.addAll(replicas[m.destionationReplica].processMessage(m));
			}
			// List is empty because we already completed one round
			if (list1.isEmpty() || !passiveReplication) {
				list2.addAll(
						replicas[currentMaster].processMessage(new Message(-1, currentMaster, MType.NOTIFY_MASTER)));
			}
			list1.clear();
			currentState = TransactionState.ACITIVIY_EXECUTED;
			break;

		case ACITIVIY_EXECUTED:

			if (isPartitioned(currentMaster)) {
				compensateMaster = currentMaster;
				initiateElection();
				break;
			}
			// only master responds with two update messages, others respond
			// with OK
			assert list2.size() == 2;
			for (Message message : list2) {
				assert message.type == Message.MType.UPDATE;
				list1.addAll(replicas[message.destionationReplica].processMessage(message));
			}
			list2.clear();
			currentState = TransactionState.UPDATE_SENT;
			break;

		case UPDATE_SENT:

			// master receives ACKs, transaction completes
			list2 = replicas[currentMaster].processMessage(list1);
			list1.clear();
			currentState = TransactionState.ACK_RECEIVED;
			// notify the next master if it't not the same as the current one
			if (annotationHandler.getActivity(nextActivity).isActivelyReplicated()) {
				for (int i = 0; i < 3; i++) {
					if (i != currentMaster && i != getNextMaster()) {
						list2.add(new Message(currentMaster, i, MType.NOTIFY_FOLLOWER));
					}
				}
			}

			if (currentMaster != getNextMaster()) {
				list2.add(new Message(currentMaster, getNextMaster(), MType.NOTIFY_MASTER));
			}
			// fall through to the next state

		case ACK_RECEIVED:

			// only thing left to do is to check if we need to compensate or not
			if (compensateMaster != -1) {
				if (replicas[compensateMaster].isOnline() && !isPartitioned(compensateMaster)) {
					compensate();
				} else {
					if (isStepping()) {
						JOptionPane.showMessageDialog(robustnessView, "Please make sure  Replica No. "
								+ (compensateMaster + 1) + " is active and not partitioned for compensation");
					} else {
						JOptionPane.showMessageDialog(robustnessView,
								"Compensation skipped because nodes where not available");
						compensateMaster = -1;
						currentState = TransactionState.TRANSACTION_COMPLETE;
					}
					return;
				}
			}
			currentState = TransactionState.TRANSACTION_COMPLETE;
			break;

		default:
			assert false;
		}
		filterMessages(list1);
		filterMessages(list2);
		if (currentState != TransactionState.MASTER_RELECTED
				&& currentState != TransactionState.REPLICAS_NOTIFIED_OF_ROLE) {
			drawMessages(list1);
			drawMessages(list2);
			clearFailedMessages(list1);
			clearFailedMessages(list2);
		} else if (currentState == TransactionState.MASTER_RELECTED) {
			// in case of relection draw the messages
			drawMessages(list2);
			clearFailedMessages(list2);
		}
	}

	private void initiateElection() {

		// replace future
		assignFutureMasters(true);
		// initialize variables for timeout processing
		newMaster = (int) (Math.random() * 3);
		while (isPartitioned(newMaster) || !replicas[newMaster].isOnline()) {
			newMaster = (int) (Math.random() * 3);
		}
		for (int i = 0; i < 3; i++) {
			if (i != newMaster && !isPartitioned(i) && replicas[i].isOnline()) {
				otherNode = i;
				break;
			}
		}

		// disable changing failures for the time being
		robustnessView.allowFailureChanges(false);

		robustnessView.getReplicationView().drawActivityForOne(newMaster, "Timeout", ActivityState.FAILURE);
		futureMasters.put(currentActivity.getName(), newMaster);
		currentState = TransactionState.TIMEOUT_OCCURED;
	}

	private boolean tooManyFailures() {
		if (failures.contains(Failure.LINK_12_FAILED) && failures.contains(Failure.LINK_23_FAILED)) {
			return true;
		}
		int failedReplicas = 0;

		for (int i = 0; i < 3; i++) {
			if (!replicas[i].isOnline()) {
				failedReplicas++;
				switch (i) {
				case 0:
					if (failures.contains(Failure.LINK_23_FAILED)) {
						return true;
					}
					break;
				case 1:
					if (!failures.isEmpty()) {
						return true;
					}
					break;
				case 2:
					if (failures.contains(Failure.LINK_12_FAILED)) {
						return true;
					}
					break;
				}
			}
		}
		if (failedReplicas > 1) {
			return true;
		}

		return false;
	}

	/**
	 * Check if the node is alone in a partition
	 * 
	 * @param nodeID
	 * @return
	 */
	private boolean isPartitioned(int nodeID) {
		switch (nodeID) {
		case 0:
			return failures.contains(Failure.LINK_12_FAILED);

		case 1:
			return failures.contains(Failure.LINK_12_FAILED) && failures.contains(Failure.LINK_23_FAILED);

		case 2:
			return failures.contains(Failure.LINK_23_FAILED);
		}
		return false;
	}

	private void clearFailedMessages(ArrayList<Message> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			Message m = list.get(i);
			if (m.isFailed()) {
				list.remove(m);
			}
		}
	}

	/**
	 * Remove all messages due to simulated failures
	 * 
	 * @param messages
	 *            assumes a non null list
	 */
	private void filterMessages(ArrayList<Message> messages) {
		boolean reassingMasters = false;
		for (int i = messages.size() - 1; i >= 0; i--) {
			Message message = messages.get(i);
			if (!replicas[0].isOnline()) {
				if (message.destionationReplica == 0) {
					messages.remove(message);
					reassingMasters = true;
				}
			}
			if (!replicas[1].isOnline()) {
				if (message.destionationReplica == 1) {
					messages.remove(message);
					reassingMasters = true;
				}
			}
			if (!replicas[2].isOnline()) {
				if (message.destionationReplica == 2) {
					messages.remove(message);
					reassingMasters = true;
				}
			}
			if (failures.contains(Failure.LINK_12_FAILED)) {
				if (message.sourceReplica == 0 || message.destionationReplica == 0) {
					message.failMessage(0);
					reassingMasters = true;
				}
			}
			if (failures.contains(Failure.LINK_23_FAILED)) {
				if (message.sourceReplica == 2 || message.destionationReplica == 2) {
					message.failMessage(1);
					reassingMasters = true;
				}
			}
		}
		if (reassingMasters) {
			assignFutureMasters(true);
		}
	}

	private void compensate() {
		ArrayList<Message> messages = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			if (i != compensateMaster) {
				messages.add(new Message(compensateMaster, i, MType.UPDATE));
			}
		}
		filterMessages(messages);
		drawMessages(messages);
		messages.clear();
		for (int i = 0; i < 3; i++) {
			if (i != compensateMaster) {
				messages.add(new Message(i, compensateMaster, MType.REJECT));
			}
		}
		filterMessages(messages);
		drawMessages(messages);
		robustnessView.getReplicationView().drawActivityForOne(compensateMaster, "C " + currentActivity.getName(),
				ActivityState.COMPENSATE);
		compensateMaster = -1;
	}

	/**
	 * Add Failure to the modelling Drops duplicates
	 *
	 * @param failure
	 *            Failure to add
	 */
	public void addFailure(Failure failure) {
		failures.add(failure);
	}

	/**
	 * Removes failure from the modelling Doesn't fail in case failure is not
	 * present
	 *
	 * @param failure
	 */
	public void removeFailure(Failure failure) {
		failures.remove(failure);
	}

	/**
	 * Draw all messages contained in list
	 * 
	 * @param messages
	 */
	private void drawMessages(ArrayList<Message> messages) {
		if (!messages.isEmpty()) {
			robustnessView.getReplicationView().drawMessages(messages);
		}
	}

	/**
	 * Draw the current state
	 */
	private void drawState() {
		robustnessView.getReplicationView().drawActivityForOne(currentMaster, currentActivity.getName(),
				ActivityState.EXECUTED);
		toggleCurrentActivityInWorkflowView();
	}
	
	
	/**
	 * 
	 */
	private void drawActivelyReplicatedState() {
		ArrayList<Integer> activeReplicas = new ArrayList<>(3);
		for (Replica r: replicas) {
			if (r.isOnline()) {
				activeReplicas.add(r.id);
			}
		}
		robustnessView.getReplicationView().drawActivityForAll(activeReplicas, currentActivity.getName(), ActivityState.EXECUTED);
		toggleCurrentActivityInWorkflowView();
	}

	/**
	 * Set weather or not to perform micro steps
	 *
	 * @param stepping
	 */
	public void setStepping(boolean stepping) {
		this.stepping = stepping;
	}

	/**
	 * Check if we are currently stepping
	 * 
	 * @return
	 */
	public boolean isStepping() {
		return stepping;
	}

	/**
	 * Make sure that the previous activity was finished In case someone macro
	 * steps, and didn't complete all micro steps
	 */
	public void finishActivity() {
		stepping = false;
		execute();
	}

	/**
	 * Check if the current transaction has been completed i.e. all the messages
	 * have been drawn.
	 *
	 */
	public boolean isFinished() {
		return currentState == TransactionState.TRANSACTION_COMPLETE;
	}

	/**
	 * Toggle the availabilty of a replica
	 * 
	 * @param replicaId
	 *            Replica Id to toggle
	 * @return true if replica is now available, false if it is now down
	 */
	public boolean toggleReplicasAvailability(int replicaId) {
		return replicas[replicaId].toggleAvailiability();
	}

	/**
	 * Get an object that holds all the properties of an activity
	 * 
	 * @param name
	 *            name of the activity
	 * @return the activity corresponding to the name or null if not found
	 */
	public Activity getActivityProperties(String name) {
		return annotationHandler.getActivity(name);
	}

	public RobustnessAnnotationHandler getAnnotaionHandler() {
		return annotationHandler;
	}
	
	/**
	 * Show the robustnessView
	 */
	public void showRobustnessView() {
		MainController.register(this);
		robustnessView.setVisible(true);
		robustnessView.fixSizes();
	}
	
	
	private void drawFuture(boolean includeCurrentActivity) {
		assignFutureMasters(false);
		robustnessView.getReplicationView().beginDrawingFuture();
		for (ProcessActivity a: currentProcessDiagram.getActivities()) {
			if (!a.isExecuted() || a.equals(currentActivity) && includeCurrentActivity) {
				drawFutureActivity(a);
			}
		}
	}
	
	private void clearFuture() {
		robustnessView.getReplicationView().clearFuture();
	}
	
	private void drawFutureActivity(ProcessActivity activity) {
		int randomMaster = futureMasters.get(activity.getName());
		robustnessView.getReplicationView().drawFutureActivity(activity.getName() + " R. Val: " + (double) annotationHandler.getActivity(activity).getRobustness() / 100, randomMaster, annotationHandler.getActivity(activity).isActivelyReplicated(), activity.isAbstract());
	}

	private int getNextMaster() {
		return futureMasters.get(nextActivity.getName());
	}

	private void assignFutureMasters(boolean reassignFailedNodes) {
		boolean keepMaster = false;
		boolean firstInAbstract = false;
		int lastMaster = -1;
		ProcessActivity lastActivity = null;
		for (ProcessActivity activity: currentProcessDiagram.getActivities()) {
			if (keepMaster) {
				futureMasters.put(activity.getName(), lastMaster);
				keepMaster = false;
			} else if (firstInAbstract && nextMasters.containsKey(lastActivity.getName())) {
				futureMasters.put(activity.getName(), nextMasters.get(lastActivity.getName()));
				firstInAbstract = false;
			} else if (lastActivity == null && nextMasters.containsKey("FIRST_ACITIVITY_MASTER_ID")) {
				futureMasters.put(activity.getName(), nextMasters.get("FIRST_ACITIVITY_MASTER_ID"));
			} else {
				if (futureMasters.containsKey(activity.getName())) {
					lastMaster = futureMasters.get(activity.getName());
					if (reassignFailedNodes) {
						if (isPartitioned(lastMaster) || !replicas[lastMaster].isOnline()) {
							int newMaster = (int) (Math.random() * 2);
							if (lastMaster == 0) {
								newMaster++;
							}
							futureMasters.put(activity.getName(), newMaster);
							lastMaster = newMaster;
						}
					}
				} else {
					lastMaster = (int) (Math.random() * 3);
					while (isPartitioned(lastMaster) || !replicas[lastMaster].isOnline()) {
						lastMaster = (int) (Math.random() * 3);
					}
					futureMasters.put(activity.getName(), lastMaster);
				}
			}
			if (annotationHandler.getActivity(activity).isActivelyReplicated()) {
				keepMaster = true;
			}

			if (nextMasters.containsKey(activity.getName())) {
				firstInAbstract = true;
			}
			if (activity.isAbstract()) {
				if (lastActivity == null) {
					nextMasters.put("FIRST_ACITIVITY_MASTER_ID", futureMasters.get(activity.getName()));
				} else {
					nextMasters.put(lastActivity.getName(), futureMasters.get(activity.getName()));
				}
			}
			lastActivity = activity;
		}
	}
}
