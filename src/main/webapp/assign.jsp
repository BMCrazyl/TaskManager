<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phân Công Công Việc</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .nav-pills .nav-link:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        .nav-pills .nav-link.active {
            background-color: #0d6efd !important;
        }
    </style>
</head>
<body class="bg-light">

<!-- Navbar với nút 3 gạch nằm sát mép trái -->
<nav class="navbar navbar-dark bg-dark mb-4 shadow-sm py-2">
    <div class="container-fluid px-3">
        <button class="btn btn-outline-light border-0 fs-4 me-2" type="button" 
                data-bs-toggle="offcanvas" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" title="Mở danh mục">
            <i class="bi bi-list"></i>
        </button>

        <a class="navbar-brand fw-bold me-auto" href="${pageContext.request.contextPath}/dashboard">
            <i class="bi bi-kanban me-2 text-primary"></i>Task Manager
        </a>
    </div>
</nav>

<!-- Sidebar danh sách hàng dọc trượt ra từ bên trái -->
<div class="offcanvas offcanvas-start bg-dark text-white" tabindex="-1" id="sidebarMenu" aria-labelledby="sidebarMenuLabel" style="width: 270px;">
    <div class="offcanvas-header border-bottom border-secondary py-3">
        <h5 class="offcanvas-title fw-bold text-primary" id="sidebarMenuLabel">
            <i class="bi bi-kanban me-2"></i>Task Manager
        </h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    
    <div class="offcanvas-body p-0 pt-3">
        <div class="nav flex-column nav-pills px-3 gap-2">
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/dashboard">
                <i class="bi bi-speedometer2 fs-5 text-info"></i> Tổng Quan
            </a>
            
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/employees">
                <i class="bi bi-people fs-5 text-warning"></i> Nhân Viên
            </a>
            
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/projects">
                <i class="bi bi-folder2 fs-5 text-primary"></i> Dự Án
            </a>
            
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/tasks">
                <i class="bi bi-check2-square fs-5 text-success"></i> Công Việc
            </a>
            
            <a class="nav-link active text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/assign">
                <i class="bi bi-person-check fs-5"></i> Phân Công
            </a>
        </div>
    </div>
</div>

<div class="container pb-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h5 class="fw-bold mb-0">
                        <i class="bi bi-person-check me-2"></i>Phân Công Việc Cho Nhân Viên
                    </h5>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/assign" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Chọn công việc</label>
                            <select name="taskId" class="form-select" required>
                                <option value="" disabled selected>-- Chọn công việc --</option>
                                <c:forEach var="t" items="${taskList}">
                                    <option value="${t.id}">${t.taskName} (${t.projectName})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tìm nhân viên</label>
                            <input type="search" id="employeeSearch" class="form-control mb-2"
                                   placeholder="Nhập tên, email hoặc chức vụ để lọc...">
                            <label class="form-label fw-bold" for="employeeSelect">Chọn nhân viên phụ trách</label>
                            <select name="employeeId" id="employeeSelect" class="form-select" required>
                                <option value="" selected>-- Chọn nhân viên --</option>
                                <c:forEach var="e" items="${employeeList}">
                                    <option value="${e.id}" data-search="${e.name} ${e.email} ${e.position} ${e.phone}">
                                        ${e.name} - ${e.position}
                                    </option>
                                </c:forEach>
                            </select>
                            <small class="text-muted">Danh sách tự lấy từ hồ sơ nhân viên hiện có.</small>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold">Ngày phân công</label>
                            <input type="date" name="assignedDate" class="form-control" required>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary w-100 fw-bold py-2">
                                <i class="bi bi-check-circle me-1"></i> Xác Nhận Phân Công
                            </button>
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary px-4 d-flex align-items-center">
                                Quay lại
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    const search = document.getElementById("employeeSearch");
    const select = document.getElementById("employeeSelect");
    if (!search || !select) return;
    const options = Array.from(select.options).slice(1);
    search.addEventListener("input", function () {
        const query = search.value.trim().toLocaleLowerCase("vi");
        options.forEach(function (option) {
            option.hidden = query !== "" && !(option.dataset.search || option.textContent).toLocaleLowerCase("vi").includes(query);
        });
        if (select.selectedOptions.length && select.selectedOptions[0].hidden) select.value = "";
    });
});
</script>
</body>
</html>