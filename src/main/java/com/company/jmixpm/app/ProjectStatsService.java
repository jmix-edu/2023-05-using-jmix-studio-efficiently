package com.company.jmixpm.app;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.ProjectStats;
import com.company.jmixpm.entity.Task;
import com.company.jmixpm.entity.User;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlanRepository;
import io.jmix.core.FetchPlans;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.ui.UiEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProjectStatsService {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private FetchPlanRepository fetchPlanRepository;
    @Autowired
    private ReportRunner reportRunner;
    @Autowired
    private TaskService taskService;

    public List<ProjectStats> fetchProjectStats() {

        List<Project> list = dataManager.load(Project.class)
                .query("select p from Project p where p.manager.id = :current_user_id")
                .list();

        List<Project> projects = dataManager.load(Project.class)
                .all()
                .fetchPlan("projects-with-tasks")
                .list();

        List<ProjectStats> projectStats = projects.stream()
                .map(project -> {
                    ProjectStats stats = dataManager.create(ProjectStats.class);
                    stats.setProjectName(project.getName());

                    List<Task> tasks = project.getTasks();
                    stats.setTaskCount(tasks.size());

                    Integer plannedEfforts = tasks.stream()
                            .map(task -> task.getEstimatedEfforts() == null ? 0 : task.getEstimatedEfforts())
                            .reduce(0, Integer::sum);

                    stats.setPlannedEfforts(plannedEfforts);

                    stats.setActualEfforts(getActualEfforts(project.getId()));
                    return stats;
                }).collect(Collectors.toList());
        return projectStats;
    }

    private Integer getActualEfforts(UUID projectId) {
        Optional<Integer> actualEffortsOpt = dataManager.loadValue(
                        "select sum(te.timeSpent) from TimeEntry te where te.task.project.id = :projectId",
                        Integer.class)
                .parameter("projectId", projectId)
                .optional();
        return actualEffortsOpt.orElse(0);
    }
}