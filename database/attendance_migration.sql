/*
 * Migration: nhật ký chấm công xác nhận hoàn thành công việc.
 * Chạy một lần trên database task_management_db hiện tại.
 * Không xóa hoặc tạo lại các bảng nghiệp vụ đang có.
 */
USE task_management_db;
GO

IF OBJECT_ID('dbo.AttendanceLog', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.AttendanceLog (
        id INT IDENTITY(1,1) PRIMARY KEY,
        task_id INT NOT NULL,
        employee_id INT NOT NULL,
        checked_at DATETIME2(0) NOT NULL
            CONSTRAINT DF_AttendanceLog_checked_at DEFAULT SYSDATETIME(),
        CONSTRAINT UQ_AttendanceLog_task_employee UNIQUE (task_id, employee_id),
        CONSTRAINT FK_AttendanceLog_Task FOREIGN KEY (task_id)
            REFERENCES dbo.Task(id) ON DELETE CASCADE,
        CONSTRAINT FK_AttendanceLog_Employee FOREIGN KEY (employee_id)
            REFERENCES dbo.Employee(id) ON DELETE CASCADE
    );
END;
GO
