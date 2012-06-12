package setup;

/**
 * Holds the command to be executed and the command type
 */
public class Command {
	String cmd;					//command to execute
	CommandType commandType;	//command type (CREATE_INSTANCE, PAUSE_INSTANCE, ASSOCIATE_DOMAIN etc...)

	/**
	 * Retains the given command to execute and its type
	 */
	public Command(String cmd, CommandType commandType) {
		this.cmd = cmd;
		this.commandType = commandType;
	}
}
