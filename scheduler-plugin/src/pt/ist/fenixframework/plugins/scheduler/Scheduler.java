package pt.ist.fenixframework.plugins.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.plugins.scheduler.domain.SchedulerSystem;
import pt.ist.fenixframework.pstm.Transaction;

public class Scheduler extends TimerTask {

    public static void initialize() {
	new Scheduler();
    }

    private final Timer timer = new Timer(true);

    public Scheduler() {
	timer.scheduleAtFixedRate(this, 25000, 20000);
    }

    @Override
    public void run() {
	try {
	    Transaction.withTransaction(false, new TransactionalCommand() {
		@Override
		public void doIt() {
		    SchedulerSystem.queueTasks();
		}
	    });
	} finally {
	    Transaction.forceFinish();
	}
	runPendingTask();
    }

    private static final String LOCK_VARIABLE = SchedulerSystem.class.getName();

    private void runPendingTask() {
	try {
	    Transaction.withTransaction(true, new TransactionalCommand() {
		@Override
		public void doIt() {
		    final Connection connection = Transaction.getCurrentJdbcConnection();
		    try {
			runPendingTask(connection);
		    } catch (SQLException e) {
			throw new Error(e);
		    }
		}
	    });
	} finally {
	    Transaction.forceFinish();
	}
    }

    private void runPendingTask(final Connection connection) throws SQLException {
	Statement statement = null;
	ResultSet resultSet = null;
	try {
	    try {
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT GET_LOCK('" + LOCK_VARIABLE + "', 10)");
		if (resultSet.next() && (resultSet.getInt(1) == 1)) {
		    SchedulerSystem.runPendingTask();
		}
	    } finally {
		Statement statement2 = null;
		try {
		    statement2 = connection.createStatement();
		    statement2.executeUpdate("DO RELEASE_LOCK('" + LOCK_VARIABLE + "')");
		} finally {
		    if (statement2 != null) {
			try {
			    statement2.close();
			} catch (final SQLException e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	    if (statement != null) {
		try {
		    statement.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

}
