package com.education.platform.util;

import java.sql.*;
import java.util.Scanner;

public class FixStudentCount {

    // 数据库连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/education_platform?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 学生人数修复工具 ===");
        System.out.println("1. 诊断数据问题");
        System.out.println("2. 修复student表数据");
        System.out.println("3. 更新class表student_count");
        System.out.println("4. 完整修复流程");
        System.out.println("5. 验证修复结果");
        System.out.println("0. 退出");
        System.out.print("请选择操作: ");

        int choice = scanner.nextInt();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            switch (choice) {
                case 1:
                    diagnose(conn);
                    break;
                case 2:
                    fixStudentTable(conn);
                    break;
                case 3:
                    updateClassCount(conn);
                    break;
                case 4:
                    fixStudentTable(conn);
                    updateClassCount(conn);
                    verify(conn);
                    break;
                case 5:
                    verify(conn);
                    break;
                default:
                    System.out.println("退出程序");
            }
        } catch (SQLException e) {
            System.err.println("数据库错误: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }

    private static void diagnose(Connection conn) throws SQLException {
        System.out.println("\n=== 诊断数据问题 ===");

        // 检查user表和student表的class_id不一致
        String sql1 = "SELECT u.username, u.real_name, u.class_id as user_class, s.class_id as student_class " +
                     "FROM user u JOIN student s ON u.id = s.user_id " +
                     "WHERE u.class_id != s.class_id";

        System.out.println("\n1. user表和student表class_id不一致的用户:");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql1)) {
            boolean hasIssue = false;
            while (rs.next()) {
                hasIssue = true;
                System.out.printf("  用户: %s, 姓名: %s, user.class_id=%d, student.class_id=%d\n",
                    rs.getString("username"), rs.getString("real_name"),
                    rs.getInt("user_class"), rs.getInt("student_class"));
            }
            if (!hasIssue) {
                System.out.println("  ✅ 没有不一致的数据");
            }
        }

        // 检查各班级student_count
        String sql2 = "SELECT c.id, c.class_name, c.student_count, " +
                     "(SELECT COUNT(*) FROM student s WHERE s.class_id = c.id) as actual_count " +
                     "FROM class c WHERE c.deleted = 0";

        System.out.println("\n2. 各班级学生人数统计:");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql2)) {
            while (rs.next()) {
                int classId = rs.getInt("id");
                String className = rs.getString("class_name");
                int recordedCount = rs.getInt("student_count");
                int actualCount = rs.getInt("actual_count");

                String status = recordedCount == actualCount ? "✅" : "❌";
                System.out.printf("  %s 班级ID=%d, %s: 记录=%d, 实际=%d\n",
                    status, classId, className, recordedCount, actualCount);
            }
        }
    }

    private static void fixStudentTable(Connection conn) throws SQLException {
        System.out.println("\n=== 修复student表数据 ===");

        String sql = "UPDATE student s " +
                    "JOIN user u ON s.user_id = u.id " +
                    "SET s.class_id = u.class_id " +
                    "WHERE u.class_id != s.class_id";

        try (Statement stmt = conn.createStatement()) {
            int affected = stmt.executeUpdate(sql);
            System.out.printf("  修复了 %d 条记录\n", affected);
        }
    }

    private static void updateClassCount(Connection conn) throws SQLException {
        System.out.println("\n=== 更新class表student_count ===");

        // 先获取所有班级的实际学生数
        String countSql = "SELECT class_id, COUNT(*) as student_count " +
                         "FROM student WHERE deleted = 0 GROUP BY class_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            while (rs.next()) {
                int classId = rs.getInt("class_id");
                int count = rs.getInt("student_count");

                String updateSql = "UPDATE class SET student_count = " + count +
                                 " WHERE id = " + classId;
                stmt.executeUpdate(updateSql);
                System.out.printf("  班级ID=%d: 更新为 %d 人\n", classId, count);
            }
        }

        // 检查是否有班级没有学生记录
        String checkSql = "SELECT c.id, c.class_name, c.student_count " +
                         "FROM class c " +
                         "WHERE c.deleted = 0 AND c.student_count > 0 " +
                         "AND NOT EXISTS (SELECT 1 FROM student s WHERE s.class_id = c.id)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            while (rs.next()) {
                System.out.printf("  ⚠️  班级ID=%d, %s: 记录有%d人，但student表无记录\n",
                    rs.getInt("id"), rs.getString("class_name"), rs.getInt("student_count"));
            }
        }
    }

    private static void verify(Connection conn) throws SQLException {
        System.out.println("\n=== 验证修复结果 ===");

        // 检查user和student表一致性
        String sql1 = "SELECT COUNT(*) as inconsistent_count " +
                     "FROM user u JOIN student s ON u.id = s.user_id " +
                     "WHERE u.class_id != s.class_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql1)) {
            if (rs.next()) {
                int count = rs.getInt("inconsistent_count");
                if (count == 0) {
                    System.out.println("✅ user表和student表class_id完全一致");
                } else {
                    System.out.printf("❌ 仍有 %d 条不一致记录\n", count);
                }
            }
        }

        // 检查各班级student_count
        String sql2 = "SELECT c.id, c.class_name, c.student_count, " +
                     "(SELECT COUNT(*) FROM student s WHERE s.class_id = c.id) as actual_count " +
                     "FROM class c WHERE c.deleted = 0";

        System.out.println("\n班级学生人数验证:");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql2)) {
            boolean allCorrect = true;
            while (rs.next()) {
                String className = rs.getString("class_name");
                int recorded = rs.getInt("student_count");
                int actual = rs.getInt("actual_count");

                if (recorded == actual) {
                    System.out.printf("  ✅ %s: %d人\n", className, recorded);
                } else {
                    System.out.printf("  ❌ %s: 记录%d人, 实际%d人\n", className, recorded, actual);
                    allCorrect = false;
                }
            }

            if (allCorrect) {
                System.out.println("\n🎉 所有班级学生人数修复完成！");
            }
        }
    }
}
