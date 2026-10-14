/*
  TaskManager report upload migration.
  Safe to run repeatedly; the application also creates ProjectReport on demand.
*/
USE task_management_db;
GO
IF OBJECT_ID(N'dbo.ProjectReport', N'U') IS NULL
BEGIN
 CREATE TABLE dbo.ProjectReport (
  id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  project_id INT NOT NULL,
  task_id INT NOT NULL,
  employee_id INT NOT NULL,
  original_file_name NVARCHAR(255) NOT NULL,
  content_type NVARCHAR(150) NOT NULL,
  file_size BIGINT NOT NULL,
  file_data VARBINARY(MAX) NOT NULL,
  submitted_at DATETIME2(0) NOT NULL CONSTRAINT DF_ProjectReport_submitted_at DEFAULT SYSDATETIME(),
  review_status NVARCHAR(30) NOT NULL CONSTRAINT DF_ProjectReport_review_status DEFAULT N'Chờ tiếp nhận',
  received_by NVARCHAR(100) NULL,
  received_at DATETIME2(0) NULL,
  CONSTRAINT FK_ProjectReport_Task FOREIGN KEY (task_id) REFERENCES dbo.Task(id) ON DELETE CASCADE,
  CONSTRAINT FK_ProjectReport_Employee FOREIGN KEY (employee_id) REFERENCES dbo.Employee(id) ON DELETE CASCADE
 );
END;
GO
