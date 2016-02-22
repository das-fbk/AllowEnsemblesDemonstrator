package eu.allowensembles.robustness.controller;

public class Message {

	public enum MType {
		NOTIFY_MASTER, // sent to new master
		UPDATE, // contains status sent from master to others
		NOTIFY_FOLLOWER, // sent to notify replicas of new master
		ACK, // sent to acknowledge commit
		ELECTION, // sent to notify nodes, that node has been promoted to master
		REJECT, // sent to old master to notify of compensation

		VCREQUEST, // sent after timeout
		VOTE, // send after vote request
		UPDATE_STATE, // sent after determining state
	}

	public final MType type;
	public final int sourceReplica;
	public final int destionationReplica;
	private int failedLink = -1;

	Message(int source, int destionation, MType type) {
		this.sourceReplica = source;
		this.destionationReplica = destionation;
		this.type = type;
	}

	/**
	 * Mark message as failed. This means the message was sent across a failed
	 * link
	 */
	public void failMessage(int link) {
		failedLink = link;
	}

	/**
	 * Check if the message is failed
	 * 
	 * @return
	 */
	public boolean isFailed() {
		return failedLink != -1;
	}

	/**
	 * Get the failed link
	 * 
	 * @return
	 */
	public int getFailure() {
		return failedLink;
	}

}
