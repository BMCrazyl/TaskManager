-- 1. Tạo cơ sở dữ liệu nếu chưa tồn tại
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'task_management_db')
    CREATE DATABASE task_management_db;
GO

USE task_management_db;
GO

-- 2. Xóa bảng cũ nếu đã có (đúng thứ tự để không dính ràng buộc Foreign Key)
IF OBJECT_ID('Assignment', 'U') IS NOT NULL DROP TABLE Assignment;
IF OBJECT_ID('Task', 'U') IS NOT NULL DROP TABLE Task;
IF OBJECT_ID('Project', 'U') IS NOT NULL DROP TABLE Project;
IF OBJECT_ID('Employee', 'U') IS NOT NULL DROP TABLE Employee;
GO

-- 3. Tạo bảng Nhân Viên (Employee)
CREATE TABLE Employee (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    position NVARCHAR(100) NOT NULL
);
GO

-- 4. Tạo bảng Dự Án (Project)
CREATE TABLE Project (
    id INT IDENTITY(1,1) PRIMARY KEY,
    projectName NVARCHAR(150) NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    description NVARCHAR(MAX)
);
GO

-- 5. Tạo bảng Công Việc (Task)
CREATE TABLE Task (
    id INT IDENTITY(1,1) PRIMARY KEY,
    taskName NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    deadline DATE NOT NULL,
    status NVARCHAR(50) DEFAULT N'Chưa bắt đầu',
    priority NVARCHAR(30) DEFAULT N'Trung bình',
    project_id INT NOT NULL,
    CONSTRAINT FK_Task_Project FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE
);
GO

-- 6. Tạo bảng Phân Công (Assignment)
CREATE TABLE Assignment (
    id INT IDENTITY(1,1) PRIMARY KEY,
    task_id INT NOT NULL,
    employee_id INT NOT NULL,
    assigned_date DATE DEFAULT CAST(GETDATE() AS DATE),
    CONSTRAINT FK_Assignment_Task FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE,
    CONSTRAINT FK_Assignment_Employee FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE CASCADE
);
GO

-- 7. Nạp dữ liệu mẫu cho Nhân Viên
INSERT INTO Employee (name, email, phone, position) VALUES
(N'Nguyễn Văn An', 'an.nv@company.com', '0901234567', N'Project Manager'),
(N'Trần Thị Bình', 'binh.tt@company.com', '0912345678', N'Backend Developer'),
(N'Lê Hoàng Long', 'long.lh@company.com', '0987654321', N'Tester'),
(N'Phạm Thu Hà', 'ha.pt@company.com', '0933445566', N'Frontend Developer'),
(N'Tấn Hồ Võ Phúc', 'hovophuctan1403@gmail.com', '0123456789', N'Backend Developer');
GO

-- 8. Nạp dữ liệu mẫu cho Dự Án
INSERT INTO Project (projectName, startDate, endDate, description) VALUES
(N'Website E-Commerce', '2026-01-01', '2028-12-31', N'Xây dựng website thương mại điện tử'),
(N'fifa', '2026-09-23', '2028-01-01', N'game');
GO

-- 9. Nạp dữ liệu mẫu cho Công Việc
INSERT INTO Task (taskName, description, deadline, status, priority, project_id) VALUES
(N'Thiết kế CSDL SQL Server', N'Viết script tạo bảng và nạp dữ liệu', '2026-02-15', N'Hoàn thành', N'Cao', 1),
(N'Lập trình REST API Auth', N'Xử lý JWT, Login, Register', '2028-10-24', N'Đang thực hiện', N'Cao', 1),
(N'Kiểm thử giao diện', N'Viết test cases và test chức năng', '2026-05-20', N'Đang thực hiện', N'Cao', 1),
(N'Hacker', N'khó', '2027-01-01', N'Đang thực hiện', N'Cao', 1);
GO

-- 10. Nạp dữ liệu phân công ban đầu
INSERT INTO Assignment (task_id, employee_id, assigned_date) VALUES
(1, 1, '2026-01-10'),
(2, 2, '2026-02-01'),
(3, 3, '2026-03-15'),
(4, 5, '2026-09-27'); -- Phân công task Hacker cho nhân viên Tấn Hồ Võ Phúc
GO