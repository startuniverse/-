package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.EducationalResource;
import com.education.platform.mapper.EducationalResourceMapper;
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

/**
 * 资源管理控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/resource")
@Tag(name = "资源管理", description = "教育资源管理相关接口")
public class ResourceController {

    @Autowired
    private EducationalResourceMapper resourceMapper;

    /**
     * 5.1.1 资源上传
     */
    @PostMapping("/upload")
    @Operation(summary = "上传资源", description = "上传教育资源")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<Boolean> uploadResource(@RequestBody EducationalResource resource) {
        resource.setStatus(0); // 待审核
        resource.setUploadTime(LocalDateTime.now());
        resource.setViewCount(0);
        resource.setDownloadCount(0);

        // 如果没有提供uploader_id，使用默认值1（管理员/教师）
        if (resource.getUploaderId() == null) {
            resource.setUploaderId(1L);
        }
        // 如果没有提供file_url，设置默认值
        if (resource.getFileUrl() == null || resource.getFileUrl().trim().isEmpty()) {
            resource.setFileUrl("/resources/default.pdf");
        }
        // 如果没有提供category，设置默认值
        if (resource.getCategory() == null || resource.getCategory().trim().isEmpty()) {
            resource.setCategory("other");
        }
        // 如果没有提供title，设置默认值
        if (resource.getTitle() == null || resource.getTitle().trim().isEmpty()) {
            resource.setTitle("未命名资源");
        }

        try {
            int result = resourceMapper.insert(resource);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("资源上传失败: " + e.getMessage());
            return ApiResult.error("资源上传失败: " + e.getMessage());
        }
    }

    /**
     * 5.1.2 资源审核
     */
    @PostMapping("/review")
    @Operation(summary = "审核资源", description = "审核教育资源")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> reviewResource(
            @Parameter(description = "资源ID") @RequestParam Long id,
            @Parameter(description = "审核状态：1-通过，2-拒绝") @RequestParam Integer status) {

        EducationalResource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return ApiResult.error("资源不存在");
        }

        resource.setStatus(status);
        resource.setApprovalTime(LocalDateTime.now());
        // 模拟审批人ID
        resource.setApproverId(1L);

        int result = resourceMapper.updateById(resource);
        return ApiResult.success(result > 0);
    }

    /**
     * 5.1.3 资源下载
     */
    @PostMapping("/download/{id}")
    @Operation(summary = "下载资源", description = "下载教育资源")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, String>> downloadResource(@PathVariable Long id) {
        EducationalResource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return ApiResult.error("资源不存在");
        }

        if (resource.getStatus() != 1) {
            return ApiResult.error("资源未发布");
        }

        // 更新下载次数
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        resourceMapper.updateById(resource);

        Map<String, String> result = new HashMap<>();
        result.put("url", resource.getFileUrl());
        result.put("filename", resource.getTitle());

        return ApiResult.success(result);
    }

    /**
     * 5.1.5 资源统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "资源统计", description = "资源使用统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> resourceStatistics(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId) {

        LambdaQueryWrapper<EducationalResource> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(EducationalResource::getSchoolId, schoolId);
        }

        List<EducationalResource> resources = resourceMapper.selectList(wrapper);

        Map<String, Object> stats = new HashMap<>();
        long total = resources.size();
        long published = resources.stream().filter(r -> r.getStatus() == 1).count();
        long totalViews = resources.stream().mapToInt(EducationalResource::getViewCount).sum();
        long totalDownloads = resources.stream().mapToInt(EducationalResource::getDownloadCount).sum();

        stats.put("totalResources", total);
        stats.put("publishedResources", published);
        stats.put("totalViews", totalViews);
        stats.put("totalDownloads", totalDownloads);

        return ApiResult.success(stats);
    }

    /**
     * 5.1.4 资源分享
     */
    @PostMapping("/share")
    @Operation(summary = "分享资源", description = "分享资源链接")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, String>> shareResource(
            @Parameter(description = "资源ID") @RequestParam Long id) {

        EducationalResource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getStatus() != 1) {
            return ApiResult.error("资源不可用");
        }

        // 生成分享码（实际项目中使用更复杂的算法）
        String shareCode = "SHARE" + System.currentTimeMillis();

        Map<String, String> result = new HashMap<>();
        result.put("shareCode", shareCode);
        result.put("url", "/api/v1/resource/share/" + shareCode);

        return ApiResult.success(result);
    }

    /**
     * 5.1.10 查看资源详情（增加浏览次数）
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "查看资源详情", description = "查看资源详情并增加浏览次数")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<EducationalResource> viewResourceDetail(@PathVariable Long id) {
        EducationalResource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return ApiResult.error("资源不存在");
        }

        if (resource.getStatus() != 1) {
            return ApiResult.error("资源未发布");
        }

        // 增加浏览次数
        resource.setViewCount(resource.getViewCount() + 1);
        resourceMapper.updateById(resource);

        return ApiResult.success(resource);
    }

    /**
     * 5.1.6 资源删除
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除资源", description = "删除教育资源")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteResource(
            @Parameter(description = "资源ID") @RequestParam Long id) {
        try {
            // 验证资源是否存在
            EducationalResource resource = resourceMapper.selectById(id);
            if (resource == null) {
                return ApiResult.error("资源不存在");
            }

            // 删除资源
            int result = resourceMapper.deleteById(id);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("资源删除失败: " + e.getMessage());
            return ApiResult.error("资源删除失败: " + e.getMessage());
        }
    }

    /**
     * 5.1.7 管理员添加资源（直接发布）
     */
    @PostMapping("/admin/add")
    @Operation(summary = "管理员添加资源", description = "管理员直接添加并发布教育资源")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> adminAddResource(@RequestBody EducationalResource resource) {
        // 设置默认值
        resource.setStatus(1); // 管理员添加直接发布
        resource.setUploadTime(LocalDateTime.now());
        resource.setApprovalTime(LocalDateTime.now());
        resource.setViewCount(0);
        resource.setDownloadCount(0);

        // 如果没有提供uploader_id，设置为1（管理员）
        if (resource.getUploaderId() == null) {
            resource.setUploaderId(1L);
        }
        // 设置审批人为管理员
        resource.setApproverId(1L);

        // 如果没有提供file_url，设置默认值
        if (resource.getFileUrl() == null || resource.getFileUrl().trim().isEmpty()) {
            resource.setFileUrl("/resources/default.pdf");
        }
        // 如果没有提供category，设置默认值
        if (resource.getCategory() == null || resource.getCategory().trim().isEmpty()) {
            resource.setCategory("other");
        }
        // 如果没有提供title，设置默认值
        if (resource.getTitle() == null || resource.getTitle().trim().isEmpty()) {
            resource.setTitle("未命名资源");
        }
        // 如果没有提供description，设置默认值
        if (resource.getDescription() == null || resource.getDescription().trim().isEmpty()) {
            resource.setDescription("暂无描述");
        }
        // 如果没有提供subject，设置默认值
        if (resource.getSubject() == null || resource.getSubject().trim().isEmpty()) {
            resource.setSubject("通用");
        }

        try {
            int result = resourceMapper.insert(resource);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("管理员添加资源失败: " + e.getMessage());
            return ApiResult.error("管理员添加资源失败: " + e.getMessage());
        }
    }

    /**
     * 5.1.8 管理员编辑资源
     */
    @PutMapping("/admin/edit")
    @Operation(summary = "管理员编辑资源", description = "管理员编辑教育资源")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> adminEditResource(@RequestBody EducationalResource resource) {
        try {
            // 验证资源是否存在
            EducationalResource existing = resourceMapper.selectById(resource.getId());
            if (existing == null) {
                return ApiResult.error("资源不存在");
            }

            // 更新修改时间
            resource.setUpdatedAt(LocalDateTime.now());

            int result = resourceMapper.updateById(resource);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("管理员编辑资源失败: " + e.getMessage());
            return ApiResult.error("管理员编辑资源失败: " + e.getMessage());
        }
    }

    /**
     * 5.1.9 管理员获取所有资源列表（包含待审核、已拒绝等状态）
     */
    @GetMapping("/admin/list")
    @Operation(summary = "管理员资源列表", description = "管理员查看所有资源列表（包含所有状态）")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<EducationalResource>> adminListResources(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "科目") @RequestParam(required = false) String subject,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "标题关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<EducationalResource> page = new Page<>(current, size);
        LambdaQueryWrapper<EducationalResource> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq(EducationalResource::getCategory, category);
        }
        // 科目筛选
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(EducationalResource::getSubject, subject);
        }
        // 状态筛选
        if (status != null) {
            wrapper.eq(EducationalResource::getStatus, status);
        }
        // 关键词搜索（标题和描述）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EducationalResource::getTitle, keyword)
                   .or()
                   .like(EducationalResource::getDescription, keyword);
        }

        // 按上传时间倒序排列
        wrapper.orderByDesc(EducationalResource::getUploadTime);

        Page<EducationalResource> result = resourceMapper.selectPage(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }

    /**
     * 5.1.1 资源列表查询
     */
    @GetMapping("/list")
    @Operation(summary = "资源列表", description = "查询资源列表")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<PageResult<EducationalResource>> listResources(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "科目") @RequestParam(required = false) String subject,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<EducationalResource> page = new Page<>(current, size);
        LambdaQueryWrapper<EducationalResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EducationalResource::getStatus, 1); // 只显示已发布的

        if (category != null && !category.isEmpty()) {
            wrapper.eq(EducationalResource::getCategory, category);
        }
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(EducationalResource::getSubject, subject);
        }
        // 关键词搜索（标题和描述）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EducationalResource::getTitle, keyword)
                   .or()
                   .like(EducationalResource::getDescription, keyword);
        }

        wrapper.orderByDesc(EducationalResource::getUploadTime);

        Page<EducationalResource> result = resourceMapper.selectPage(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }
}
