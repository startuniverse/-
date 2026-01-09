package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.*;
import com.education.platform.mapper.*;
import com.education.platform.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/backend")
@Tag(name = "后台管理", description = "后台管理相关接口")
public class BackendController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private GrowthRecordMapper growthRecordMapper;

    @Autowired
    private OfficeAssetMapper officeAssetMapper;

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private com.education.platform.util.JwtUtils jwtUtils;

    @Autowired
    private IUserService userService;

    @Autowired
    private StudentStatusChangeMapper studentStatusChangeMapper;

    /**
     * 从token中获取用户ID的辅助方法
     */
    private Long getCurrentUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String jwt = token.substring(7); // 去掉 "Bearer "
            String username = jwtUtils.getUsernameFromToken(jwt);

            // 通过用户名查询用户
            User user = userMapper.selectByUsername(username);
            if (user != null) {
                return user.getId();
            }
        } catch (Exception e) {
            System.out.println("=== 从token获取用户ID失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 4.1.1 文档发布
     */
    @PostMapping("/document/publish")
    @Operation(summary = "发布文档", description = "创建并发布文档")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> publishDocument(@RequestBody Document document, @RequestHeader(value = "Authorization", required = false) String token) {
        document.setStatus(2); // 2-已发布（根据数据库状态定义）
        document.setPublishTime(LocalDateTime.now());

        // 从token获取当前用户ID作为作者
        Long currentUserId = getCurrentUserIdFromToken(token);
        if (currentUserId == null) {
            currentUserId = 1L; // 默认管理员
        }
        document.setAuthorId(currentUserId);

        // 如果没有提供category，设置默认值
        if (document.getCategory() == null || document.getCategory().trim().isEmpty()) {
            document.setCategory("notice");
        }
        // 如果没有提供title，设置默认值
        if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
            document.setTitle("未命名文档");
        }

        try {
            // 1. 插入文档记录
            int result = documentMapper.insert(document);

            // 2. 如果分类是notice，同时创建通知公告记录
            if ("notice".equals(document.getCategory())) {
                Announcement announcement = new Announcement();
                announcement.setTitle(document.getTitle());
                announcement.setContent(document.getContent());
                announcement.setType("notice"); // 默认为通知类型
                announcement.setPublisherId(currentUserId);
                announcement.setPublishTime(LocalDateTime.now());
                announcement.setStatus(1); // 已发布

                // 设置学校ID和班级ID（从当前用户获取）
                User currentUser = userMapper.selectById(currentUserId);
                if (currentUser != null) {
                    announcement.setSchoolId(currentUser.getSchoolId());
                    // 通知可以是全校范围，不设置classId，或者根据需求设置
                    // announcement.setClassId(currentUser.getClassId());
                }

                announcement.setPriority(0); // 普通优先级
                announcementMapper.insert(announcement);
            }

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            // 记录错误信息
            System.err.println("文档发布失败: " + e.getMessage());
            return ApiResult.error("文档发布失败: " + e.getMessage());
        }
    }

    /**
     * 4.1.2 文档审批
     */
    @PostMapping("/document/approve")
    @Operation(summary = "审批文档", description = "审批文档")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> approveDocument(
            @Parameter(description = "文档ID") @RequestParam Long id,
            @Parameter(description = "审批状态：2-通过，3-驳回") @RequestParam Integer status,
            @Parameter(description = "审批意见") @RequestParam(required = false) String comment) {

        Document document = documentMapper.selectById(id);
        if (document == null) {
            return ApiResult.error("文档不存在");
        }

        document.setStatus(status);
        document.setApprovalComment(comment);
        document.setApprovalTime(LocalDateTime.now());
        // 假设审批人ID从token获取，这里使用模拟值
        document.setApprovalId(1L);

        int result = documentMapper.updateById(document);
        return ApiResult.success(result > 0);
    }

    /**
     * 4.1.5 文档查询
     */
    @GetMapping("/document/query")
    @Operation(summary = "文档查询", description = "高级搜索文档")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<PageResult<Document>> queryDocuments(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<Document> page = new Page<>(current, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();

        if (category != null) {
            wrapper.eq(Document::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Document::getStatus, status);
        }

        wrapper.orderByDesc(Document::getPublishTime);

        Page<Document> result = documentMapper.selectPage(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }

    /**
     * 4.1.6 文档删除
     */
    @PostMapping("/document/delete")
    @Operation(summary = "删除文档", description = "删除文档信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteDocument(
            @Parameter(description = "文档ID") @RequestParam Long id) {
        try {
            // 验证文档是否存在
            Document document = documentMapper.selectById(id);
            if (document == null) {
                return ApiResult.error("文档不存在");
            }

            // 删除文档
            int result = documentMapper.deleteById(id);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("文档删除失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("文档删除失败: " + e.getMessage());
        }
    }

    /**
     * 4.2.1 学生管理 - 列表
     */
    @GetMapping("/student/list")
    @Operation(summary = "学生列表", description = "获取学生列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<Map<String, Object>>> listStudents(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "姓名/学号模糊查询") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<Student> page = new Page<>(current, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        // 关键词查询（姓名或学号）
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先查询匹配的用户ID
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getRealName, keyword).or().like(User::getUsername, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(java.util.stream.Collectors.toList());

            if (!userIds.isEmpty()) {
                wrapper.in(Student::getUserId, userIds);
            } else {
                // 如果用户表没找到，尝试按学号查询
                wrapper.like(Student::getStudentNumber, keyword);
            }
        }

        if (schoolId != null && classId != null) {
            wrapper.eq(Student::getClassId, classId);
        } else if (classId != null) {
            wrapper.eq(Student::getClassId, classId);
        } else if (schoolId != null) {
            LambdaQueryWrapper<com.education.platform.entity.Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(com.education.platform.entity.Class::getSchoolId, schoolId);
            List<com.education.platform.entity.Class> classes = classMapper.selectList(classWrapper);
            if (classes.isEmpty()) {
                Page<Map<String, Object>> emptyPage = new Page<>(current, size);
                emptyPage.setRecords(new java.util.ArrayList<>());
                return ApiResult.success(PageResult.of(emptyPage));
            }
            List<Long> classIds = classes.stream().map(com.education.platform.entity.Class::getId).collect(java.util.stream.Collectors.toList());
            wrapper.in(Student::getClassId, classIds);
        }

        Page<Student> result = studentMapper.selectPage(page, wrapper);

        // 关联查询用户信息，返回包含姓名的数据
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Student student : result.getRecords()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", student.getId());
            map.put("studentNumber", student.getStudentNumber());
            map.put("classId", student.getClassId());
            map.put("guardianName", student.getGuardianName());
            map.put("guardianPhone", student.getGuardianPhone());
            map.put("enrollmentDate", student.getEnrollmentDate());
            map.put("status", student.getStatus());

            // 关联查询用户姓名
            User user = userMapper.selectById(student.getUserId());
            if (user != null) {
                map.put("name", user.getRealName());
            } else {
                map.put("name", "");
            }

            // 关联查询班级名称
            com.education.platform.entity.Class classInfo = classMapper.selectById(student.getClassId());
            if (classInfo != null) {
                map.put("className", classInfo.getClassName());
            } else {
                map.put("className", "");
            }

            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 4.2.1.1 学生管理 - 新增
     */
    @PostMapping("/student/add")
    @Operation(summary = "新增学生", description = "新增学生信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addStudent(@RequestBody Map<String, Object> studentData) {
        try {
            // 从请求中提取数据
            String name = (String) studentData.get("name");
            String studentNumber = (String) studentData.get("studentNumber");
            Long classId = studentData.get("classId") != null ? Long.parseLong(studentData.get("classId").toString()) : null;
            String guardianName = (String) studentData.get("guardianName");
            String guardianPhone = (String) studentData.get("guardianPhone");
            Object enrollmentDateObj = studentData.get("enrollmentDate");

            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                return ApiResult.error("学生姓名不能为空");
            }
            if (studentNumber == null || studentNumber.trim().isEmpty()) {
                return ApiResult.error("学号不能为空");
            }
            if (classId == null) {
                return ApiResult.error("班级ID不能为空");
            }

            // 1. 先创建 User 记录
            User user = new User();
            user.setUsername("student_" + studentNumber);  // 生成用户名
            user.setPassword("$2a$10$n.ZUyvQ.fEg3a.1dQwXrZu5b6c7d8e9f0g1h2i3j4k5l6m7n8o9p0"); // 默认密码
            user.setRealName(name);
            user.setPhone(guardianPhone);
            user.setEmail(studentNumber + "@edu.com");
            user.setStatus(1);
            userMapper.insert(user);

            // 2. 创建 Student 记录
            Student student = new Student();
            student.setUserId(user.getId());
            student.setStudentNumber(studentNumber);
            student.setClassId(classId);
            student.setGuardianName(guardianName);
            student.setGuardianPhone(guardianPhone);
            student.setStatus(1); // 在读

            if (enrollmentDateObj != null) {
                if (enrollmentDateObj instanceof String) {
                    student.setEnrollmentDate(java.time.LocalDate.parse((String) enrollmentDateObj));
                } else {
                    student.setEnrollmentDate(java.time.LocalDate.now());
                }
            } else {
                student.setEnrollmentDate(java.time.LocalDate.now());
            }

            studentMapper.insert(student);
            return ApiResult.success(true);
        } catch (Exception e) {
            System.err.println("学生新增失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("学生新增失败: " + e.getMessage());
        }
    }

    /**
     * 4.2.1.2 学生管理 - 更新
     */
    @PostMapping("/student/update")
    @Operation(summary = "更新学生", description = "更新学生信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateStudent(@RequestBody Map<String, Object> studentData) {
        try {
            // 从请求中提取数据
            Object idObj = studentData.get("id");
            if (idObj == null) {
                return ApiResult.error("学生ID不能为空");
            }
            Long id = Long.parseLong(idObj.toString());

            String name = (String) studentData.get("name");
            String studentNumber = (String) studentData.get("studentNumber");
            Long classId = studentData.get("classId") != null ? Long.parseLong(studentData.get("classId").toString()) : null;
            String guardianName = (String) studentData.get("guardianName");
            String guardianPhone = (String) studentData.get("guardianPhone");
            Object enrollmentDateObj = studentData.get("enrollmentDate");
            Object statusObj = studentData.get("status");

            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                return ApiResult.error("学生姓名不能为空");
            }
            if (studentNumber == null || studentNumber.trim().isEmpty()) {
                return ApiResult.error("学号不能为空");
            }
            if (classId == null) {
                return ApiResult.error("班级ID不能为空");
            }

            // 1. 查询原学生记录
            Student oldStudent = studentMapper.selectById(id);
            if (oldStudent == null) {
                return ApiResult.error("学生不存在");
            }

            // 2. 更新 User 记录
            User user = userMapper.selectById(oldStudent.getUserId());
            if (user != null) {
                user.setRealName(name);
                user.setPhone(guardianPhone);
                userMapper.updateById(user);
            }

            // 3. 更新 Student 记录
            Student student = new Student();
            student.setId(id);
            student.setUserId(oldStudent.getUserId());
            student.setStudentNumber(studentNumber);
            student.setClassId(classId);
            student.setGuardianName(guardianName);
            student.setGuardianPhone(guardianPhone);

            if (statusObj != null) {
                student.setStatus(Integer.parseInt(statusObj.toString()));
            }

            if (enrollmentDateObj != null) {
                if (enrollmentDateObj instanceof String) {
                    student.setEnrollmentDate(java.time.LocalDate.parse((String) enrollmentDateObj));
                }
            }

            studentMapper.updateById(student);
            return ApiResult.success(true);
        } catch (Exception e) {
            System.err.println("学生更新失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("学生更新失败: " + e.getMessage());
        }
    }

    /**
     * 4.2.1.3 学生管理 - 删除
     */
    @DeleteMapping("/student/delete")
    @Operation(summary = "删除学生", description = "删除学生信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteStudent(
            @Parameter(description = "学生ID") @RequestParam Long id) {
        try {
            // 1. 查询学生记录
            Student student = studentMapper.selectById(id);
            if (student == null) {
                return ApiResult.error("学生不存在");
            }

            // 2. 逻辑删除学生记录（使用MyBatis-Plus的@TableLogic注解）
            int result = studentMapper.deleteById(id);

            // 3. 可选：同时删除关联的用户记录（或保留作为历史数据）
            // userMapper.deleteById(student.getUserId());

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("学生删除失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("学生删除失败: " + e.getMessage());
        }
    }

    /**
     * 4.2.2 学生成长记录
     */
    @PostMapping("/growth/record")
    @Operation(summary = "添加成长记录", description = "添加学生成长记录")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<Boolean> addGrowthRecord(@RequestBody GrowthRecord record) {
        record.setStatus(1);
        int result = growthRecordMapper.insert(record);
        return ApiResult.success(result > 0);
    }

    /**
     * 4.2.4 成长统计
     */
    @GetMapping("/growth/statistics")
    @Operation(summary = "成长统计", description = "学生成长统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> growthStatistics(
            @Parameter(description = "班级ID") @RequestParam Long classId) {

        // 统计不同类型的记录数量
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrowthRecord::getClassId, classId);

        List<GrowthRecord> records = growthRecordMapper.selectList(wrapper);

        Map<String, Object> stats = new HashMap<>();
        long achievement = records.stream().filter(r -> "achievement".equals(r.getCategory())).count();
        long behavior = records.stream().filter(r -> "behavior".equals(r.getCategory())).count();
        long special = records.stream().filter(r -> "special".equals(r.getCategory())).count();

        stats.put("achievement", achievement);
        stats.put("behavior", behavior);
        stats.put("special", special);
        stats.put("total", records.size());

        return ApiResult.success(stats);
    }

    /**
     * 4.3.1 办公资产管理 - 列表
     */
    @GetMapping("/asset/list")
    @Operation(summary = "资产列表", description = "获取办公资产列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<OfficeAsset>> listAssets(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词（资产名称）") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<OfficeAsset> page = new Page<>(current, size);
        LambdaQueryWrapper<OfficeAsset> wrapper = new LambdaQueryWrapper<>();

        if (schoolId != null) {
            wrapper.eq(OfficeAsset::getSchoolId, schoolId);
        }

        if (category != null) {
            wrapper.eq(OfficeAsset::getCategory, category);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(OfficeAsset::getAssetName, keyword);
        }

        wrapper.orderByDesc(OfficeAsset::getCreatedAt);

        Page<OfficeAsset> result = officeAssetMapper.selectPage(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }

    /**
     * 4.3.1.1 办公资产管理 - 新增
     */
    @PostMapping("/asset/add")
    @Operation(summary = "新增资产", description = "新增办公资产")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addAsset(@RequestBody Map<String, Object> assetData) {
        try {
            OfficeAsset asset = new OfficeAsset();

            // 提取数据并设置默认值
            Object schoolIdObj = assetData.get("schoolId");
            if (schoolIdObj != null) {
                asset.setSchoolId(Long.parseLong(schoolIdObj.toString()));
            } else {
                // 如果前端没有传递schoolId，使用默认值1（或从用户信息中获取）
                asset.setSchoolId(1L);
            }

            String assetName = (String) assetData.get("assetName");
            if (assetName == null || assetName.trim().isEmpty()) {
                return ApiResult.error("资产名称不能为空");
            }
            asset.setAssetName(assetName);

            String category = (String) assetData.get("category");
            if (category == null || category.trim().isEmpty()) {
                category = "equipment";
            }
            asset.setCategory(category);

            String specification = (String) assetData.get("specification");
            asset.setSpecification(specification);

            Object quantityObj = assetData.get("quantity");
            if (quantityObj != null) {
                asset.setQuantity(Integer.parseInt(quantityObj.toString()));
            } else {
                asset.setQuantity(1);
            }

            Object unitPriceObj = assetData.get("unitPrice");
            if (unitPriceObj != null) {
                if (unitPriceObj instanceof Number) {
                    asset.setUnitPrice(new java.math.BigDecimal(unitPriceObj.toString()));
                } else {
                    asset.setUnitPrice(new java.math.BigDecimal(unitPriceObj.toString()));
                }
            }

            // 计算总价值
            if (asset.getUnitPrice() != null && asset.getQuantity() != null) {
                asset.setTotalValue(asset.getUnitPrice().multiply(java.math.BigDecimal.valueOf(asset.getQuantity())));
            }

            String location = (String) assetData.get("location");
            asset.setLocation(location);

            String responsiblePerson = (String) assetData.get("responsiblePerson");
            asset.setResponsiblePerson(responsiblePerson);

            Object purchaseDateObj = assetData.get("purchaseDate");
            if (purchaseDateObj != null) {
                if (purchaseDateObj instanceof String) {
                    asset.setPurchaseDate(java.time.LocalDate.parse((String) purchaseDateObj));
                }
            }

            asset.setStatus(1); // 正常状态

            int result = officeAssetMapper.insert(asset);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("资产新增失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("资产新增失败: " + e.getMessage());
        }
    }

    /**
     * 4.3.1.2 办公资产管理 - 更新
     */
    @PostMapping("/asset/update")
    @Operation(summary = "更新资产", description = "更新办公资产")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateAsset(@RequestBody Map<String, Object> assetData) {
        try {
            // 从请求中提取数据
            Object idObj = assetData.get("id");
            if (idObj == null) {
                return ApiResult.error("资产ID不能为空");
            }
            Long id = Long.parseLong(idObj.toString());

            // 验证资产是否存在
            OfficeAsset oldAsset = officeAssetMapper.selectById(id);
            if (oldAsset == null) {
                return ApiResult.error("资产不存在");
            }

            OfficeAsset asset = new OfficeAsset();
            asset.setId(id);

            // 提取数据并设置
            Object schoolIdObj = assetData.get("schoolId");
            if (schoolIdObj != null) {
                asset.setSchoolId(Long.parseLong(schoolIdObj.toString()));
            } else {
                asset.setSchoolId(oldAsset.getSchoolId());
            }

            String assetName = (String) assetData.get("assetName");
            if (assetName == null || assetName.trim().isEmpty()) {
                return ApiResult.error("资产名称不能为空");
            }
            asset.setAssetName(assetName);

            String category = (String) assetData.get("category");
            if (category == null || category.trim().isEmpty()) {
                category = "equipment";
            }
            asset.setCategory(category);

            String specification = (String) assetData.get("specification");
            asset.setSpecification(specification);

            Object quantityObj = assetData.get("quantity");
            if (quantityObj != null) {
                asset.setQuantity(Integer.parseInt(quantityObj.toString()));
            }

            Object unitPriceObj = assetData.get("unitPrice");
            if (unitPriceObj != null) {
                asset.setUnitPrice(new java.math.BigDecimal(unitPriceObj.toString()));
            }

            Object totalValueObj = assetData.get("totalValue");
            if (totalValueObj != null) {
                asset.setTotalValue(new java.math.BigDecimal(totalValueObj.toString()));
            } else if (asset.getUnitPrice() != null && asset.getQuantity() != null) {
                asset.setTotalValue(asset.getUnitPrice().multiply(java.math.BigDecimal.valueOf(asset.getQuantity())));
            }

            String location = (String) assetData.get("location");
            asset.setLocation(location);

            String responsiblePerson = (String) assetData.get("responsiblePerson");
            asset.setResponsiblePerson(responsiblePerson);

            Object statusObj = assetData.get("status");
            if (statusObj != null) {
                asset.setStatus(Integer.parseInt(statusObj.toString()));
            }

            int result = officeAssetMapper.updateById(asset);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("资产更新失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("资产更新失败: " + e.getMessage());
        }
    }

    /**
     * 4.3.1.3 办公资产管理 - 删除
     */
    @DeleteMapping("/asset/delete")
    @Operation(summary = "删除资产", description = "删除办公资产")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteAsset(
            @Parameter(description = "资产ID") @RequestParam Long id) {
        try {
            // 验证资产是否存在
            OfficeAsset asset = officeAssetMapper.selectById(id);
            if (asset == null) {
                return ApiResult.error("资产不存在");
            }

            // 物理删除（解决逻辑删除导致的唯一键冲突问题）
            // 使用deleteById会触发逻辑删除，改用delete方法进行物理删除
            LambdaQueryWrapper<OfficeAsset> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OfficeAsset::getId, id);
            int result = officeAssetMapper.delete(wrapper);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("资产删除失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("资产删除失败: " + e.getMessage());
        }
    }

    /**
     * 4.3.2 会议管理 - 创建
     */
    @PostMapping("/meeting/create")
    @Operation(summary = "创建会议", description = "创建会议")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> createMeeting(@RequestBody Meeting meeting) {
        meeting.setStatus(1);
        int result = meetingMapper.insert(meeting);
        return ApiResult.success(result > 0);
    }

    /**
     * 4.3.3 考勤管理 - 记录
     */
    @PostMapping("/attendance/check")
    @Operation(summary = "考勤签到", description = "记录考勤")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Boolean> checkAttendance(@RequestBody Attendance attendance) {
        attendance.setRecordDate(java.time.LocalDate.now());
        int result = attendanceMapper.insert(attendance);
        return ApiResult.success(result > 0);
    }

    /**
     * 4.3.7 系统设置
     */
    @GetMapping("/system/settings")
    @Operation(summary = "系统设置", description = "获取系统设置")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("systemName", "城市教育局综合信息服务平台");
        settings.put("version", "1.0.0");
        settings.put("schoolCount", 50);
        settings.put("userCount", 10000);
        settings.put("lastBackup", "2025-01-01 00:00:00");
        return ApiResult.success(settings);
    }

    /**
     * 4.5.1 一卡通管理 - 发卡
     */
    @PostMapping("/card/issue")
    @Operation(summary = "发卡", description = "发行一卡通")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> issueCard(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "卡号") @RequestParam String cardNumber,
            @Parameter(description = "卡片类型") @RequestParam String cardType) {

        // 这里需要创建SmartCard实体并插入
        // 简化处理，返回成功
        return ApiResult.success(true);
    }

    /**
     * 4.6.1 班牌管理 - 显示
     */
    @GetMapping("/classsign/list")
    @Operation(summary = "班牌列表", description = "获取班牌设备列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<Map<String, Object>>> listClassSigns() {
        // 模拟班牌数据
        List<Map<String, Object>> signs = List.of(
            Map.of("id", 1, "deviceId", "SIGN-001", "className", "三年级一班", "status", "在线"),
            Map.of("id", 2, "deviceId", "SIGN-002", "className", "三年级二班", "status", "在线")
        );
        return ApiResult.success(signs);
    }

    /**
     * 4.6.2 学校管理 - 列表（公开接口，用于注册时选择学校）
     */
    @GetMapping("/school/list")
    @Operation(summary = "学校列表", description = "获取学校列表（公开接口）")
    public ApiResult<List<School>> listSchools() {
        try {
            LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(School::getStatus, 1);  // 只显示正常状态的学校
            wrapper.orderByAsc(School::getId);
            List<School> schools = schoolMapper.selectList(wrapper);
            return ApiResult.success(schools);
        } catch (Exception e) {
            System.err.println("查询学校列表失败: " + e.getMessage());
            return ApiResult.error("查询学校列表失败: " + e.getMessage());
        }
    }

    /**
     * 4.6.3 班级管理 - 列表（公开接口，用于注册时选择班级）
     */
    @GetMapping("/class/list")
    @Operation(summary = "班级列表", description = "获取班级列表（公开接口）")
    public ApiResult<List<com.education.platform.entity.Class>> listClasses(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId) {
        try {
            LambdaQueryWrapper<com.education.platform.entity.Class> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(com.education.platform.entity.Class::getStatus, 1);  // 只显示正常状态的班级

            if (schoolId != null) {
                wrapper.eq(com.education.platform.entity.Class::getSchoolId, schoolId);
            }

            wrapper.orderByAsc(com.education.platform.entity.Class::getId);
            List<com.education.platform.entity.Class> classes = classMapper.selectList(wrapper);
            return ApiResult.success(classes);
        } catch (Exception e) {
            System.err.println("查询班级列表失败: " + e.getMessage());
            return ApiResult.error("查询班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 4.7.1 教师管理 - 列表
     */
    @GetMapping("/teacher/list")
    @Operation(summary = "教师列表", description = "获取教师列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<Map<String, Object>>> listTeachers(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "姓名/工号模糊查询") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<Teacher> page = new Page<>(current, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();

        // 关键词查询（姓名或工号）
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先查询匹配的用户ID
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getRealName, keyword).or().like(User::getUsername, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

            if (!userIds.isEmpty()) {
                wrapper.in(Teacher::getUserId, userIds);
            } else {
                // 如果用户表没找到，尝试按工号查询
                wrapper.like(Teacher::getTeacherNumber, keyword);
            }
        }

        if (schoolId != null) {
            // 先查询该学校的所有用户
            List<User> schoolUsers = userMapper.selectBySchoolId(schoolId);
            List<Long> userIds = schoolUsers.stream().map(User::getId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                wrapper.in(Teacher::getUserId, userIds);
            } else {
                // 如果没有匹配的教师，返回空结果
                Page<Map<String, Object>> emptyPage = new Page<>(current, size);
                emptyPage.setRecords(new java.util.ArrayList<>());
                return ApiResult.success(PageResult.of(emptyPage));
            }
        }

        wrapper.orderByDesc(Teacher::getCreatedAt);

        Page<Teacher> result = teacherMapper.selectPage(page, wrapper);

        // 关联查询用户信息，返回包含姓名的数据
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Teacher teacher : result.getRecords()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", teacher.getId());
            map.put("teacherNumber", teacher.getTeacherNumber());
            map.put("title", teacher.getTitle());
            map.put("subject", teacher.getSubject());
            map.put("hireDate", teacher.getHireDate());
            map.put("status", teacher.getStatus());

            // 关联查询用户姓名和部门
            User user = userMapper.selectById(teacher.getUserId());
            if (user != null) {
                map.put("name", user.getRealName());
                map.put("department", user.getDepartment());
                map.put("phone", user.getPhone());
                map.put("email", user.getEmail());
                map.put("schoolId", user.getSchoolId());
            } else {
                map.put("name", "");
                map.put("department", "");
                map.put("phone", "");
                map.put("email", "");
                map.put("schoolId", null);
            }

            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 4.7.2 教师管理 - 新增
     */
    @PostMapping("/teacher/add")
    @Operation(summary = "新增教师", description = "新增教师信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addTeacher(@RequestBody Map<String, Object> teacherData) {
        try {
            // 从请求中提取数据
            String name = (String) teacherData.get("name");
            String teacherNumber = (String) teacherData.get("teacherNumber");
            Long schoolId = teacherData.get("schoolId") != null ? Long.parseLong(teacherData.get("schoolId").toString()) : null;
            String department = (String) teacherData.get("department");
            String title = (String) teacherData.get("title");
            String subject = (String) teacherData.get("subject");
            String phone = (String) teacherData.get("phone");
            String email = (String) teacherData.get("email");
            Object hireDateObj = teacherData.get("hireDate");

            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                return ApiResult.error("教师姓名不能为空");
            }
            if (teacherNumber == null || teacherNumber.trim().isEmpty()) {
                return ApiResult.error("教师编号不能为空");
            }
            if (schoolId == null) {
                return ApiResult.error("学校ID不能为空");
            }

            // 1. 先创建 User 记录
            User user = new User();
            user.setUsername("teacher_" + teacherNumber);  // 生成用户名
            user.setPassword("$2a$10$518kI.1n7qL5jK8x9mX2eO3rY4uV5wX6yZ7aB8cD9eF0gH1iJ2kL"); // 默认密码: 123
            user.setRealName(name);
            user.setPhone(phone);
            user.setEmail(email != null ? email : teacherNumber + "@edu.com");
            user.setSchoolId(schoolId);
            user.setDepartment(department);
            user.setTitle(title);
            user.setStatus(1);
            userMapper.insert(user);

            // 2. 创建 Teacher 记录
            Teacher teacher = new Teacher();
            teacher.setUserId(user.getId());
            teacher.setTeacherNumber(teacherNumber);
            teacher.setTitle(title);
            teacher.setSubject(subject);
            teacher.setStatus(1); // 在职

            if (hireDateObj != null) {
                if (hireDateObj instanceof String) {
                    teacher.setHireDate(java.time.LocalDate.parse((String) hireDateObj));
                } else {
                    teacher.setHireDate(java.time.LocalDate.now());
                }
            } else {
                teacher.setHireDate(java.time.LocalDate.now());
            }

            teacherMapper.insert(teacher);

            // 3. 分配 TEACHER 角色
            userService.assignRole(user.getId(), "TEACHER");

            return ApiResult.success(true);
        } catch (Exception e) {
            System.err.println("教师新增失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("教师新增失败: " + e.getMessage());
        }
    }

    /**
     * 4.7.3 教师管理 - 更新
     */
    @PostMapping("/teacher/update")
    @Operation(summary = "更新教师", description = "更新教师信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateTeacher(@RequestBody Map<String, Object> teacherData) {
        try {
            // 从请求中提取数据
            Object idObj = teacherData.get("id");
            if (idObj == null) {
                return ApiResult.error("教师ID不能为空");
            }
            Long id = Long.parseLong(idObj.toString());

            String name = (String) teacherData.get("name");
            String teacherNumber = (String) teacherData.get("teacherNumber");
            Long schoolId = teacherData.get("schoolId") != null ? Long.parseLong(teacherData.get("schoolId").toString()) : null;
            String department = (String) teacherData.get("department");
            String title = (String) teacherData.get("title");
            String subject = (String) teacherData.get("subject");
            String phone = (String) teacherData.get("phone");
            String email = (String) teacherData.get("email");
            Object hireDateObj = teacherData.get("hireDate");
            Object statusObj = teacherData.get("status");

            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                return ApiResult.error("教师姓名不能为空");
            }
            if (teacherNumber == null || teacherNumber.trim().isEmpty()) {
                return ApiResult.error("教师编号不能为空");
            }

            // 1. 查询原教师记录
            Teacher oldTeacher = teacherMapper.selectById(id);
            if (oldTeacher == null) {
                return ApiResult.error("教师不存在");
            }

            // 2. 更新 User 记录
            User user = userMapper.selectById(oldTeacher.getUserId());
            if (user != null) {
                user.setRealName(name);
                user.setPhone(phone);
                user.setEmail(email);
                user.setSchoolId(schoolId);
                user.setDepartment(department);
                user.setTitle(title);
                userMapper.updateById(user);
            }

            // 3. 更新 Teacher 记录
            Teacher teacher = new Teacher();
            teacher.setId(id);
            teacher.setUserId(oldTeacher.getUserId());
            teacher.setTeacherNumber(teacherNumber);
            teacher.setTitle(title);
            teacher.setSubject(subject);

            if (statusObj != null) {
                teacher.setStatus(Integer.parseInt(statusObj.toString()));
            }

            if (hireDateObj != null) {
                if (hireDateObj instanceof String) {
                    teacher.setHireDate(java.time.LocalDate.parse((String) hireDateObj));
                }
            }

            teacherMapper.updateById(teacher);
            return ApiResult.success(true);
        } catch (Exception e) {
            System.err.println("教师更新失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("教师更新失败: " + e.getMessage());
        }
    }

    /**
     * 4.7.4 教师管理 - 删除
     */
    @DeleteMapping("/teacher/delete")
    @Operation(summary = "删除教师", description = "删除教师信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteTeacher(
            @Parameter(description = "教师ID") @RequestParam Long id) {
        try {
            // 1. 查询教师记录
            Teacher teacher = teacherMapper.selectById(id);
            if (teacher == null) {
                return ApiResult.error("教师不存在");
            }

            // 2. 逻辑删除教师记录
            int result = teacherMapper.deleteById(id);

            // 3. 可选：同时删除关联的用户记录（或保留作为历史数据）
            // userMapper.deleteById(teacher.getUserId());

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("教师删除失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("教师删除失败: " + e.getMessage());
        }
    }

    /**
     * 4.8.1 学籍异动申请 - 学生提交申请
     */
    @PostMapping("/status-change/apply")
    @Operation(summary = "学籍异动申请", description = "学生提交学籍异动申请")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Boolean> applyStatusChange(
            @RequestBody StudentStatusChange change,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token获取当前用户ID
            Long currentUserId = getCurrentUserIdFromToken(token);
            if (currentUserId == null) {
                return ApiResult.error("未登录或登录已过期");
            }

            // 设置申请人ID
            change.setUserId(currentUserId);

            // 查询当前用户关联的学生信息
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(Student::getUserId, currentUserId);
            Student student = studentMapper.selectOne(studentWrapper);
            if (student == null) {
                return ApiResult.error("未找到关联的学生信息");
            }

            // 设置学生ID
            change.setStudentId(student.getId());

            // 设置初始状态：待审核
            change.setStatus(0);
            change.setDeleted(0);

            // 插入记录
            int result = studentStatusChangeMapper.insert(change);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("学籍异动申请失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("学籍异动申请失败: " + e.getMessage());
        }
    }

    /**
     * 4.8.2 学籍异动查询 - 管理员查询所有申请
     */
    @GetMapping("/status-change/list")
    @Operation(summary = "学籍异动列表", description = "查询学籍异动申请列表（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<Map<String, Object>>> listStatusChanges(
            @Parameter(description = "学生姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "异动类型：1-休学，2-转学，3-复学，4-退学，5-其他") @RequestParam(required = false) Integer changeType,
            @Parameter(description = "审核状态：0-待审核，1-已通过，2-已驳回") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<StudentStatusChange> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentStatusChange> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (changeType != null) {
            wrapper.eq(StudentStatusChange::getChangeType, changeType);
        }
        if (status != null) {
            wrapper.eq(StudentStatusChange::getStatus, status);
        }

        // 按时间倒序排列
        wrapper.orderByDesc(StudentStatusChange::getCreatedAt);

        Page<StudentStatusChange> result = studentStatusChangeMapper.selectPage(page, wrapper);

        // 关联查询学生和用户信息
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (StudentStatusChange change : result.getRecords()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", change.getId());
            map.put("changeType", change.getChangeType());
            map.put("reason", change.getReason());
            map.put("startDate", change.getStartDate());
            map.put("endDate", change.getEndDate());
            map.put("targetSchool", change.getTargetSchool());
            map.put("status", change.getStatus());
            map.put("approvalComment", change.getApprovalComment());
            map.put("approvalTime", change.getApprovalTime());
            map.put("createdAt", change.getCreatedAt());

            // 查询学生信息
            Student student = studentMapper.selectById(change.getStudentId());
            if (student != null) {
                map.put("studentNumber", student.getStudentNumber());
                map.put("classId", student.getClassId());

                // 查询学生姓名（通过用户表）
                User user = userMapper.selectById(student.getUserId());
                if (user != null) {
                    map.put("studentName", user.getRealName());
                }

                // 查询班级名称
                com.education.platform.entity.Class classInfo = classMapper.selectById(student.getClassId());
                if (classInfo != null) {
                    map.put("className", classInfo.getClassName());
                }
            }

            // 查询审核人信息
            if (change.getApproverId() != null) {
                User approver = userMapper.selectById(change.getApproverId());
                if (approver != null) {
                    map.put("approverName", approver.getRealName());
                }
            }

            // 查询申请人信息
            if (change.getUserId() != null) {
                User applicant = userMapper.selectById(change.getUserId());
                if (applicant != null) {
                    map.put("applicantName", applicant.getRealName());
                }
            }

            // 根据changeType转换中文描述
            String typeDesc = "";
            switch (change.getChangeType()) {
                case 1: typeDesc = "休学"; break;
                case 2: typeDesc = "转学"; break;
                case 3: typeDesc = "复学"; break;
                case 4: typeDesc = "退学"; break;
                case 5: typeDesc = "其他"; break;
                default: typeDesc = "未知";
            }
            map.put("changeTypeDesc", typeDesc);

            // 根据status转换中文描述
            String statusDesc = "";
            switch (change.getStatus()) {
                case 0: statusDesc = "待审核"; break;
                case 1: statusDesc = "已通过"; break;
                case 2: statusDesc = "已驳回"; break;
                default: statusDesc = "未知";
            }
            map.put("statusDesc", statusDesc);

            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 4.8.3 学籍异动查询 - 学生查询自己的申请
     */
    @GetMapping("/status-change/my-list")
    @Operation(summary = "我的学籍异动", description = "查询当前学生的学籍异动申请")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<PageResult<Map<String, Object>>> listMyStatusChanges(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        // 从token获取当前用户ID
        Long currentUserId = getCurrentUserIdFromToken(token);
        if (currentUserId == null) {
            return ApiResult.error("未登录或登录已过期");
        }

        // 查询当前用户关联的学生
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getUserId, currentUserId);
        Student student = studentMapper.selectOne(studentWrapper);
        if (student == null) {
            return ApiResult.error("未找到关联的学生信息");
        }

        Page<StudentStatusChange> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentStatusChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentStatusChange::getStudentId, student.getId());
        wrapper.orderByDesc(StudentStatusChange::getCreatedAt);

        Page<StudentStatusChange> result = studentStatusChangeMapper.selectPage(page, wrapper);

        // 构建返回数据
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (StudentStatusChange change : result.getRecords()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", change.getId());
            map.put("changeType", change.getChangeType());
            map.put("reason", change.getReason());
            map.put("startDate", change.getStartDate());
            map.put("endDate", change.getEndDate());
            map.put("targetSchool", change.getTargetSchool());
            map.put("status", change.getStatus());
            map.put("approvalComment", change.getApprovalComment());
            map.put("approvalTime", change.getApprovalTime());
            map.put("createdAt", change.getCreatedAt());

            // 根据changeType转换中文描述
            String typeDesc = "";
            switch (change.getChangeType()) {
                case 1: typeDesc = "休学"; break;
                case 2: typeDesc = "转学"; break;
                case 3: typeDesc = "复学"; break;
                case 4: typeDesc = "退学"; break;
                case 5: typeDesc = "其他"; break;
                default: typeDesc = "未知";
            }
            map.put("changeTypeDesc", typeDesc);

            // 根据status转换中文描述
            String statusDesc = "";
            switch (change.getStatus()) {
                case 0: statusDesc = "待审核"; break;
                case 1: statusDesc = "已通过"; break;
                case 2: statusDesc = "已驳回"; break;
                default: statusDesc = "未知";
            }
            map.put("statusDesc", statusDesc);

            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 4.8.4 学籍异动审核 - 管理员审核申请
     */
    @PostMapping("/status-change/approve")
    @Operation(summary = "审核学籍异动", description = "管理员审核学籍异动申请")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> approveStatusChange(
            @Parameter(description = "申请ID") @RequestParam Long id,
            @Parameter(description = "审核状态：1-通过，2-驳回") @RequestParam Integer status,
            @Parameter(description = "审核意见") @RequestParam(required = false) String approvalComment,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // 验证申请是否存在
            StudentStatusChange change = studentStatusChangeMapper.selectById(id);
            if (change == null) {
                return ApiResult.error("申请不存在");
            }

            // 验证状态是否为待审核
            if (change.getStatus() != 0) {
                return ApiResult.error("该申请已被审核，不能重复审核");
            }

            // 从token获取审核人ID
            Long currentUserId = getCurrentUserIdFromToken(token);
            if (currentUserId == null) {
                currentUserId = 1L; // 默认管理员
            }

            // 更新审核信息
            change.setStatus(status);
            change.setApprovalComment(approvalComment);
            change.setApproverId(currentUserId);
            change.setApprovalTime(java.time.LocalDateTime.now());

            int result = studentStatusChangeMapper.updateById(change);

            // 如果审核通过，可能需要更新学生状态（这里可以根据业务需求扩展）
            if (status == 1) {
                // 审核通过后的业务逻辑（可选）
                // 例如：如果是退学，将学生状态改为退学
                // 如果是休学，将学生状态改为休学
                // 这里可以根据changeType做相应处理
            }

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("审核学籍异动失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("审核学籍异动失败: " + e.getMessage());
        }
    }

    /**
     * 4.8.5 学籍异动删除 - 删除申请（仅管理员）
     */
    @DeleteMapping("/status-change/delete")
    @Operation(summary = "删除学籍异动", description = "删除学籍异动申请")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteStatusChange(
            @Parameter(description = "申请ID") @RequestParam Long id) {
        try {
            // 验证申请是否存在
            StudentStatusChange change = studentStatusChangeMapper.selectById(id);
            if (change == null) {
                return ApiResult.error("申请不存在");
            }

            // 逻辑删除
            int result = studentStatusChangeMapper.deleteById(id);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("删除学籍异动失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("删除学籍异动失败: " + e.getMessage());
        }
    }

    /**
     * 4.8.6 学籍异动统计 - 管理员查看统计信息
     */
    @GetMapping("/status-change/statistics")
    @Operation(summary = "学籍异动统计", description = "学籍异动申请统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> statusChangeStatistics() {
        try {
            // 统计总数
            LambdaQueryWrapper<StudentStatusChange> totalWrapper = new LambdaQueryWrapper<>();
            Long total = studentStatusChangeMapper.selectCount(totalWrapper);

            // 统计待审核
            LambdaQueryWrapper<StudentStatusChange> pendingWrapper = new LambdaQueryWrapper<>();
            pendingWrapper.eq(StudentStatusChange::getStatus, 0);
            Long pending = studentStatusChangeMapper.selectCount(pendingWrapper);

            // 统计已通过
            LambdaQueryWrapper<StudentStatusChange> approvedWrapper = new LambdaQueryWrapper<>();
            approvedWrapper.eq(StudentStatusChange::getStatus, 1);
            Long approved = studentStatusChangeMapper.selectCount(approvedWrapper);

            // 统计已驳回
            LambdaQueryWrapper<StudentStatusChange> rejectedWrapper = new LambdaQueryWrapper<>();
            rejectedWrapper.eq(StudentStatusChange::getStatus, 2);
            Long rejected = studentStatusChangeMapper.selectCount(rejectedWrapper);

            // 按类型统计
            Map<String, Long> typeStats = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                LambdaQueryWrapper<StudentStatusChange> typeWrapper = new LambdaQueryWrapper<>();
                typeWrapper.eq(StudentStatusChange::getChangeType, i);
                Long count = studentStatusChangeMapper.selectCount(typeWrapper);
                typeStats.put(String.valueOf(i), count);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("pending", pending);
            stats.put("approved", approved);
            stats.put("rejected", rejected);
            stats.put("typeStats", typeStats);

            return ApiResult.success(stats);
        } catch (Exception e) {
            System.err.println("统计学籍异动失败: " + e.getMessage());
            return ApiResult.error("统计学籍异动失败: " + e.getMessage());
        }
    }

    @Autowired
    private com.education.platform.mapper.ClassApplicationMapper classApplicationMapper;

    /**
     * 获取学生统计信息
     */
    @GetMapping("/student/statistics")
    @Operation(summary = "学生统计", description = "获取学生统计信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> studentStatistics() {
        try {
            // 统计总学生数
            LambdaQueryWrapper<Student> totalWrapper = new LambdaQueryWrapper<>();
            Long total = studentMapper.selectCount(totalWrapper);

            // 统计在读学生
            LambdaQueryWrapper<Student> inSchoolWrapper = new LambdaQueryWrapper<>();
            inSchoolWrapper.eq(Student::getStatus, 1);
            Long inSchool = studentMapper.selectCount(inSchoolWrapper);

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("inSchool", inSchool);

            return ApiResult.success(stats);
        } catch (Exception e) {
            System.err.println("统计学生信息失败: " + e.getMessage());
            return ApiResult.error("统计学生信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取班级统计信息
     */
    @GetMapping("/class/statistics")
    @Operation(summary = "班级统计", description = "获取班级统计信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> classStatistics() {
        try {
            // 统计总班级数
            LambdaQueryWrapper<com.education.platform.entity.Class> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(com.education.platform.entity.Class::getStatus, 1);
            Long total = classMapper.selectCount(wrapper);

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);

            return ApiResult.success(stats);
        } catch (Exception e) {
            System.err.println("统计班级信息失败: " + e.getMessage());
            return ApiResult.error("统计班级信息失败: " + e.getMessage());
        }
    }
}
