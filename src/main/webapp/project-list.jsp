<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Dự Án</title>
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
    <div class="row g-4">
        <!-- Khung Form Thêm / Sửa Dự án -->
        <div class="col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-header ${not empty project ? 'bg-warning text-dark' : 'bg-primary text-white'} py-3">
                    <h5 class="fw-bold mb-0">
                        <i class="bi ${not empty project ? 'bi-pencil-square' : 'bi-folder-plus'} me-2"></i>
                        ${not empty project ? 'Cập Nhật Dự Án' : 'Thêm Dự Án Mới'}
                    </h5>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/projects" method="POST">
                        <!-- ID ẩn dùng khi sửa -->
                        <input type="hidden" name="id" value="${project.id}">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tên dự án</label>
                            <input type="text" name="projectName" class="form-control" 
                                   value="${project.projectName}" placeholder="VD: Hệ Thống Đặt Chỗ" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Ngày bắt đầu</label>
                            <input type="date" name="startDate" class="form-control" value="${project.startDate}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Ngày kết thúc</label>
                            <input type="date" name="endDate" class="form-control" value="${project.endDate}" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold">Mô tả dự án</label>
                            <textarea name="description" class="form-control" rows="3" 
                                      placeholder="Mục tiêu và phạm vi dự án...">${project.description}</textarea>
                        </div>
                        
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn ${not empty project ? 'btn-warning fw-bold' : 'btn-primary fw-bold'} w-100">
                                <i class="bi bi-save me-1"></i> ${not empty project ? 'Lưu Cập Nhật' : 'Lưu Dự Án'}
                            </button>
                            <c:if test="${not empty project}">
                                <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary">Hủy</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Bảng danh sách dự án -->
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0 text-dark">Danh Sách Dự Án</h5>
                    <span class="badge bg-secondary">${projectList.size()} dự án</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>#ID</th>
                                <th>Tên Dự Án</th>
                                <th>Thời Gian</th>
                                <th>Mô Tả</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${projectList}">
                                <tr>
                                    <td><strong>#${p.id}</strong></td>
                                    <td class="fw-bold text-primary">${p.projectName}</td>
                                    <td>
                                        <small class="d-block text-muted">Bắt đầu: ${p.startDate}</small>
                                        <small class="d-block text-danger">Kết thúc: ${p.endDate}</small>
                                    </td>
                                    <td><small class="text-secondary">${p.description}</small></td>
                                    <td class="text-center">
                                        <div class="d-inline-flex gap-1">
                                            <!-- Nút Sửa -->
                                            <a href="${pageContext.request.contextPath}/projects?action=edit&id=${p.id}" 
                                               class="btn btn-outline-warning btn-sm" title="Sửa thông tin dự án">
                                                <i class="bi bi-pencil-square"></i> Sửa
                                            </a>
                                            <!-- Nút Xóa -->
                                            <a href="${pageContext.request.contextPath}/projects?action=delete&id=${p.id}" 
                                               onclick="return confirm('Bạn có chắc muốn xóa dự án này? Toàn bộ công việc thuộc dự án cũng sẽ bị xóa!')" 
                                               class="btn btn-outline-danger btn-sm" title="Xóa dự án">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty projectList}">
                                <tr>
                                    <td colspan="5" class="text-center py-4 text-muted">Chưa có dự án nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>