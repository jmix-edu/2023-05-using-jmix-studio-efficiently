package com.company.jmixpm.app;

import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.FileStorage;
import io.jmix.core.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    protected final Resources resources;
    private final DataManager dataManager;

    public TaskService(DataManager dataManager, Resources resources) {
        this.dataManager = dataManager;
        this.resources = resources;
    }

    public User findLeastBusyUser() {
        return dataManager.loadValues("select u, count(t.id) " +
                        "from User u left outer join Task_ t " +
                        "on u = t.assignee " +
                        "group by u order by count(t.id)")
                .properties("user", "tasks")
                .list().stream()
                .map(e -> e.<User>getValue("user"))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}