<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Nhân Viên</title>
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
<div class="offcanvas offcanvas-start bg-dark text-white d-flex flex-column" tabindex="-1" id="sidebarMenu" aria-labelledby="sidebarMenuLabel" style="width: 280px;">
    <div class="offcanvas-header border-bottom border-secondary py-3">
        <h5 class="offcanvas-title fw-bold text-primary" id="sidebarMenuLabel">
            <i class="bi bi-kanban me-2"></i>Task Manager
        </h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
    </div>
    
    <div class="offcanvas-body p-0 pt-3 d-flex flex-column">
        <div class="nav flex-column nav-pills px-3 gap-2">
            <a class="nav-link text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
               href="${pageContext.request.contextPath}/dashboard">
                <i class="bi bi-speedometer2 fs-5 text-info"></i> Tổng Quan
            </a>
            
            <a class="nav-link active text-white py-2 px-3 rounded d-flex align-items-center gap-3 fs-6" 
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

        <!-- Khối thông tin tài khoản & nút Đăng Xuất ở đáy Sidebar -->
        <div class="mt-auto p-3 border-top border-secondary">
            <div class="d-flex align-items-center mb-2">
                <i class="bi bi-person-circle fs-3 me-2 text-info"></i>
                <div class="overflow-hidden">
                    <div class="fw-bold text-truncate" style="max-width: 170px;">
                        ${not empty sessionScope.user.fullname ? sessionScope.user.fullname : 'Quản Lý'}
                    </div>
                    <span class="badge ${sessionScope.user.role == 'ADMIN' ? 'bg-danger' : 'bg-primary'}">
                        ${sessionScope.user.role == 'ADMIN' ? 'Quản Lý' : 'Nhân Viên'}
                    </span>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm w-100 mt-2">
                <i class="bi bi-box-arrow-right me-1"></i> Đăng Xuất
            </a>
        </div>
    </div>
</div>

<div class="container pb-5">
    <div class="row g-4">
        <!-- Form Thêm / Cập nhật Nhân Viên -->
        <div class="col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-header ${not empty employee ? 'bg-warning text-dark' : 'bg-primary text-white'} py-3">
                    <h5 class="fw-bold mb-0">
                        <i class="bi ${not empty employee ? 'bi-pencil-square' : 'bi-person-plus'} me-2"></i>
                        ${not empty employee ? 'Cập Nhật Nhân Viên' : 'Thêm Nhân Viên Mới'}
                    </h5>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/employees" method="POST">
                        <!-- ID ẩn để nhận diện sửa hay thêm mới -->
                        <input type="hidden" name="id" value="${employee.id}">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Họ và tên</label>
                            <input type="text" name="name" class="form-control" value="${employee.name}" placeholder="VD: Nguyễn Văn A" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" class="form-control" value="${employee.email}" placeholder="VD: an.nv@company.com" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Số điện thoại</label>
                            <input type="text" name="phone" class="form-control" value="${employee.phone}" placeholder="VD: 0901234567">
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold">Vị trí / Chức vụ</label>
                            <select name="position" class="form-select" required>
                                <option value="Backend Developer" ${employee.position == 'Backend Developer' ? 'selected' : ''}>Backend Developer</option>
                                <option value="Frontend Developer" ${employee.position == 'Frontend Developer' ? 'selected' : ''}>Frontend Developer</option>
                                <option value="Fullstack Developer" ${employee.position == 'Fullstack Developer' ? 'selected' : ''}>Fullstack Developer</option>
                                <option value="Project Manager" ${employee.position == 'Project Manager' ? 'selected' : ''}>Project Manager</option>
                                <option value="Tester" ${employee.position == 'Tester' ? 'selected' : ''}>Tester</option>
                                <option value="UI/UX Designer" ${employee.position == 'UI/UX Designer' ? 'selected' : ''}>UI/UX Designer</option>
                            </select>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn ${not empty employee ? 'btn-warning fw-bold' : 'btn-primary fw-bold'} w-100">
                                <i class="bi bi-save me-1"></i> ${not empty employee ? 'Lưu Cập Nhật' : 'Lưu Nhân Viên'}
                            </button>
                            <c:if test="${not empty employee}">
                                <a href="${pageContext.request.contextPath}/employees" class="btn btn-outline-secondary">Hủy</a>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Bảng Danh Sách Nhân Viên -->
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0 text-dark">Danh Sách Nhân Viên</h5>
                    <span class="badge bg-secondary">${employeeList.size()} nhân viên</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>#ID</th>
                                <th>Họ Tên</th>
                                <th>Liên Hệ</th>
                                <th>Chức Vụ</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="e" items="${employeeList}">
                                <tr>
                                    <td><strong>#${e.id}</strong></td>
                                    <td class="fw-bold text-primary">${e.name}</td>
                                    <td>
                                        <div><i class="bi bi-envelope me-1 text-muted"></i>${e.email}</div>
                                        <small class="text-muted"><i class="bi bi-telephone me-1"></i>${e.phone}</small>
                                    </td>
                                    <td><span class="badge bg-light text-dark border">${e.position}</span></td>
                                    <td class="text-center">
                                        <div class="d-inline-flex gap-1">
                                            <!-- Nút Sửa -->
                                            <a href="${pageContext.request.contextPath}/employees?action=edit&id=${e.id}" 
                                               class="btn btn-outline-warning btn-sm" title="Sửa thông tin">
                                                <i class="bi bi-pencil-square"></i> Sửa
                                            </a>

                                            <!-- Nút Xóa có hộp thoại xác nhận -->
                                            <a href="${pageContext.request.contextPath}/employees?action=delete&id=${e.id}" 
                                               onclick="return confirm('Bạn có chắc chắn muốn xóa nhân viên [${e.name}] không?')" 
                                               class="btn btn-outline-danger btn-sm" title="Xóa nhân viên">
                                                <i class="bi bi-trash"></i> Xóa
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employeeList}">
                                <tr>
                                    <td colspan="5" class="text-center py-4 text-muted">Chưa có nhân sự nào.</td>
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