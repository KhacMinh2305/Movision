package architecture.other;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class TaskManager {

    public interface Task {
        void doTask();
    }

    private final ConnectionMonitor connMonitor;
    private final List<Task> listTask;

    // supply get to pass the lifecycle directly to the ConnectionMonitor.
    public ConnectionMonitor getConnectionMonitor() {
        return connMonitor;
    }

    @Inject
    public TaskManager(ConnectionMonitor connMonitor) {
        this.connMonitor = connMonitor;
        listTask = new ArrayList<>();
    }

    public void pushTask(Task task) {
        listTask.add(task);
    }

    public void doAllTasks() {
        while(!listTask.isEmpty()) {
            listTask.get(0).doTask();
            listTask.remove(0);
        }
    }
}
