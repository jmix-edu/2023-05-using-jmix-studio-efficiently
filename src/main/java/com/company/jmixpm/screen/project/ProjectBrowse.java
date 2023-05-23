package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Table;
import io.jmix.ui.screen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UiController("Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
public class ProjectBrowse extends StandardLookup<Project> {
    private static final Logger log = LoggerFactory.getLogger(ProjectBrowse.class);

    @Subscribe("projectsTable")
    public void onProjectsTableSelection(Table.SelectionEvent<Project> event) {
        log.info("Selected " + event.getSelected().size());
    }



}