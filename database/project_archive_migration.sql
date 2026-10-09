/*
 * TaskManager migration: archive completed projects and normalize old sample values.
 * Safe to run repeatedly. It never drops or recreates business tables.
 */
USE task_management_db;
GO
IF COL_LENGTH(N'dbo.Project', N'isArchived') IS NULL
BEGIN
    ALTER TABLE dbo.Project ADD isArchived BIT NOT NULL
        CONSTRAINT DF_Project_isArchived DEFAULT (0) WITH VALUES;
END;
GO
IF COL_LENGTH(N'dbo.Project', N'archivedAt') IS NULL
BEGIN
    ALTER TABLE dbo.Project ADD archivedAt DATETIME2(0) NULL;
END;
GO
UPDATE dbo.Task
SET status = CASE LTRIM(RTRIM(status))
    WHEN N'Hoan thanh' THEN N'Hoàn thành'
    WHEN N'Dang thuc hien' THEN N'Đang thực hiện'
    WHEN N'Chua bat dau' THEN N'Chưa bắt đầu'
    ELSE status END
WHERE LTRIM(RTRIM(status)) IN (N'Hoan thanh', N'Dang thuc hien', N'Chua bat dau');
GO
UPDATE dbo.Task
SET priority = CASE LTRIM(RTRIM(priority))
    WHEN N'Trung binh' THEN N'Trung bình'
    WHEN N'Thap' THEN N'Thấp'
    ELSE priority END
WHERE LTRIM(RTRIM(priority)) IN (N'Trung binh', N'Thap');
GO
UPDATE p SET isArchived = 1, archivedAt = COALESCE(p.archivedAt, SYSDATETIME())
FROM dbo.Project p
WHERE ISNULL(p.isArchived, 0) = 0
  AND EXISTS (SELECT 1 FROM dbo.Task t WHERE t.project_id = p.id)
  AND NOT EXISTS (
      SELECT 1 FROM dbo.Task t WHERE t.project_id = p.id
        AND LTRIM(RTRIM(ISNULL(t.status, N''))) <> N'Hoàn thành'
  );
GO
