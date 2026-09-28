/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Date;
/**
 *
 * @author mai09
 */
public class Project {
    private int id;
    private String projectName;
    private Date startDate;
    private Date endDate;
    private String description;

    public Project() {}

    public Project(int id, String projectName, Date startDate, Date endDate, String description) {
        this.id = id;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
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
}
