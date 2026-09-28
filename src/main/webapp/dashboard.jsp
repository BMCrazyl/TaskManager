<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Phân Công Công Việc</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">

<!-- Thanh Navbar trên cùng với nút 3 gạch -->
<nav class="navbar navbar-dark bg-dark mb-4 shadow-sm py-2">
    <div class="container-fluid px-3">
        <!-- Nút 3 gạch mở Menu dọc -->
        <button class="btn btn-outline-light border-0 fs-4 me-2" type="button" 
                data-bs-toggle="offcanvas" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu">
            <i class="bi bi-list"></i>
        </button>

        <a class="navbar-brand fw-bold me-auto" href="${pageContext.request.contextPath}/dashboard">
            <i class="bi bi-kanban me-2 text-primary"></i>Task Manager
        </a>
    </div>
</nav>

<!-- Sidebar hàng dọc trượt từ mép trái ra khi bấm nút 3 gạch -->
<div class="offcanvas offcanvas-start bg-dark text-white" tabindex="-1" id="sidebarMenu" aria-labelledby="sidebarMenuLabel" style="width: 280px;">
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
            
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/assign">
                <i class="bi bi-person-check fs-5 text-danger"></i> Phân Công
            </a>
        </div>
    </div>
</div>

<div class="container pb-5">
    <!-- Tính toán thống kê an toàn trực tiếp từ taskList -->
    <c:set var="completedCount" value="0" />
    <c:set var="inProgressCount" value="0" />
    <c:forEach var="item" items="${taskList}">
        <c:if test="${item.status == 'Hoàn thành'}">
            <c:set var="completedCount" value="${completedCount + 1}" />
        </c:if>
        <c:if test="${item.status == 'Đang thực hiện'}">
            <c:set var="inProgressCount" value="${inProgressCount + 1}" />
        </c:if>
    </c:forEach>

    <!-- Thống kê số lượng theo Yêu cầu 2.2 -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 border-start border-success border-4 p-3 bg-white">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted text-uppercase fw-semibold small">Công việc Hoàn thành</span>
                        <h2 class="fw-bold text-success mb-0">${completedCount}</h2>
                    </div>
                    <div class="bg-success-subtle text-success p-3 rounded-circle">
                        <i class="bi bi-check2-all fs-2"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-0 border-start border-primary border-4 p-3 bg-white">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted text-uppercase fw-semibold small">Đang thực hiện</span>
                        <h2 class="fw-bold text-primary mb-0">${inProgressCount}</h2>
                    </div>
                    <div class="bg-primary-subtle text-primary p-3 rounded-circle">
                        <i class="bi bi-hourglass-split fs-2"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-0 border-start border-danger border-4 p-3 bg-white">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted text-uppercase fw-semibold small">Đã quá hạn</span>
                        <h2 class="fw-bold text-danger mb-0">${overdueTasks != null ? overdueTasks : 0}</h2>
                    </div>
                    <div class="bg-danger-subtle text-danger p-3 rounded-circle">
                        <i class="bi bi-calendar-x fs-2"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Khối tìm kiếm lọc đa tiêu chí theo Yêu cầu 2.2 -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-3">
            <form action="${pageContext.request.contextPath}/dashboard" method="GET" class="row g-2">
                <div class="col-md-4">
                    <input type="text" name="empName" class="form-control" placeholder="Tìm theo tên nhân viên..." value="${param.empName}">
                </div>
                <div class="col-md-4">
                    <input type="text" name="projectName" class="form-control" placeholder="Tìm theo tên dự án..." value="${param.projectName}">
                </div>
                <div class="col-md-3">
                    <select name="status" class="form-select">
                        <option value="">-- Tất cả --</option>
                        <option value="Chưa bắt đầu" ${param.status == 'Chưa bắt đầu' ? 'selected' : ''}>Chưa bắt đầu</option>
                        <option value="Đang thực hiện" ${param.status == 'Đang thực hiện' ? 'selected' : ''}>Đang thực hiện</option>
                        <option value="Hoàn thành" ${param.status == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100"><i class="bi bi-search"></i></button>
                </div>
            </form>
        </div>
    </div>

    <!-- Danh sách hiển thị -->
    <div class="card border-0 shadow-sm">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 class="fw-bold mb-0 text-dark">Tiến Độ Công Việc</h5>
            <a href="${pageContext.request.contextPath}/assign" class="btn btn-primary btn-sm"><i class="bi bi-plus-lg me-1"></i>Phân công mới</a>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>#</th>
                        <th>Công Việc</th>
                        <th>Dự Án</th>
                        <th>Người Phụ Trách</th>
                        <th>Hạn Chót</th>
                        <th>Mức Ưu Tiên</th>
                        <th>Trạng Thái</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty taskList}">
                            <c:forEach var="t" items="${taskList}">
                                <tr>
                                    <td><strong>#${t.id}</strong></td>
                                    <td>
                                        <div class="fw-bold">${t.taskName}</div>
                                        <small class="text-muted">${t.description}</small>
                                    </td>
                                    <td><span class="badge bg-secondary">${t.projectName}</span></td>
                                    <td>
                                        <c:set var="name" value="${not empty t.employeeName ? t.employeeName : t.assignedEmployee}" />
                                        <c:choose>
                                            <c:when test="${not empty name and name != '' and name != 'Chưa phân công'}">
                                                <span class="badge bg-info text-dark">
                                                    <i class="bi bi-person-fill me-1"></i>${name}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-light text-muted border">
                                                    <i class="bi bi-person me-1"></i>Chưa phân công
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${t.deadline}</td>
                                    <td>
                                        <span class="badge ${t.priority == 'Cao' ? 'bg-danger' : (t.priority == 'Trung bình' ? 'bg-warning text-dark' : 'bg-light text-dark border')}">
                                            ${t.priority}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${t.status == 'Hoàn thành' ? 'bg-success' : (t.status == 'Đang thực hiện' ? 'bg-primary' : 'bg-secondary')}">
                                            ${t.status}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center py-4 text-muted">Không tìm thấy bản ghi nào.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>