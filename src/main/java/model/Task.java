package model;

import java.sql.Date;

public class Task {
    private int id;
    private String taskName;
    private String description;
    private Date deadline;
    private String status;
    private String priority;
    private int projectId;
    
    // Thuộc tính hỗ trợ JOIN hiển thị
    private String projectName;
    private int employeeId;
    private String employeeName;

    public Task() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    // Hai hàm bổ sung để tương thích hoàn toàn với setAssignedEmployee và JSP
    public String getAssignedEmployee() { 
        return this.employeeName; 
    }
    public void setAssignedEmployee(String assignedEmployee) { 
        this.employeeName = assignedEmployee; 
    }
}