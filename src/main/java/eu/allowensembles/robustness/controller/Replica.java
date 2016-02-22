package eu.allowensembles.robustness.controller;

import eu.allowensembles.robustness.controller.Message.MType;

import java.util.ArrayList;

public class Replica {

	public final int id;
	private boolean isMaster = false;
	private boolean online = true;

	public Replica(int id) {
		this.id = id;
	}

	/**
	 * Check if the replica is currently a Master replica
	 * 
	 * @return
	 */
	public boolean isMaster() {
		return isMaster;
	}

	/**
	 * Wrapper to process only one message
	 *
	 * @param message
	 *            Message to process
	 * @return
	 */
	public ArrayList<Message> processMessage(Message message) {
		ArrayList<Message> messages = new ArrayList<>();
		messages.add(message);
		return processMessage(messages);
	}

	/**
	 * Process messages according to algorithm
	 *
	 * @param messages
	 *            Messages to process
	 * @return Messages to send as a response to the received Messages
	 */
	public ArrayList<Message> processMessage(ArrayList<Message> messages) {
		assert messages != null;
		ArrayList<Message> messagesToSend = new ArrayList<>();
		int messageCount = messages.size();
		if (messageCount == 0) {
			// no messages, failure
			return messagesToSend;
		}

		Message message = messages.get(0);

		switch (message.type) {
		case UPDATE:
			assert !isMaster;
			messagesToSend.add(new Message(id, message.sourceReplica, MType.ACK));
			break;
		case NOTIFY_MASTER:
			assert isMaster;
			for (int i = 0; i < 3; i++) {
				if (i != id) {
					messagesToSend.add(new Message(id, i, MType.UPDATE));
				}
			}
		default:
		}
		return messagesToSend;
	}

	/**
	 * Set whether or not the Replica is online
	 * 
	 * @param online
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * 
	 * @return whether or not the Replica is online
	 */
	public boolean isOnline() {
		return online;
	}

	public boolean toggleAvailiability() {
		online = !online;
		return online;
	}

	public void degrade() {
		isMaster = false;
	}

	public void promote() {
		isMaster = true;
	}

}
