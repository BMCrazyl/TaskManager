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
public class Assignment {
    private int id;
    private int taskId;
    private int employeeId;
    private Date assignedDate;

    public Assignment() {}

    public Assignment(int id, int taskId, int employeeId, Date assignedDate) {
        this.id = id;
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.assignedDate = assignedDate;
    }

    public Assignment(int taskId, int employeeId, Date assignedDate) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.assignedDate = assignedDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public Date getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Date assignedDate) { this.assignedDate = assignedDate; }
}
