package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Project {
    private int id;
    private String projectName;
    private Date startDate;
    private Date endDate;
    private String description;
    private boolean archived;
    private Timestamp archivedAt;

    public Project() {}
    public Project(int id, String projectName, Date startDate, Date endDate, String description) {
        this.id = id; this.projectName = projectName; this.startDate = startDate;
        this.endDate = endDate; this.description = description;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isArchived() { return archived; }
    public boolean getArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Timestamp getArchivedAt() { return archivedAt; }
    public void setArchivedAt(Timestamp archivedAt) { this.archivedAt = archivedAt; }
}
