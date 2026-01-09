package com.education.platform.util;

import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExecuteSQL {

    private static final String URL = "jdbc:mysql://localhost:3306/education_platform?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("用法: java ExecuteSQL <sql文件路径>");
            return;
        }

        String sqlFile = args[0];

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             BufferedReader reader = new BufferedReader(new FileReader(sqlFile))) {

            System.out.println("正在执行SQL脚本: " + sqlFile);
            System.out.println("=====================================");

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int lineCount = 0;
            int statementCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                // 跳过注释行
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }

                sqlBuilder.append(line).append(" ");

                // 如果遇到分号，执行SQL
                if (line.contains(";")) {
                    String sql = sqlBuilder.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            if (sql.toUpperCase().startsWith("SELECT")) {
                                // 查询语句
                                executeQuery(conn, sql);
                                statementCount++;
                            } else {
                                // 更新语句
                                executeUpdate(conn, sql);
                                statementCount++;
                            }
                        } catch (SQLException e) {
                            System.err.println("执行失败: " + e.getMessage());
                        }
                        sqlBuilder = new StringBuilder();
                    }
                }
            }

            System.out.println("=====================================");
            System.out.println("执行完成！共执行 " + statementCount + " 条语句");

        } catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("数据库连接错误: " + e.getMessage());
        }
    }

    private static void executeQuery(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // 打印标题
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", meta.getColumnName(i));
            }
            System.out.println();
            System.out.println("-------------------------------------");

            // 打印数据
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value == null) value = "NULL";
                    System.out.printf("%-20s", value);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static void executeUpdate(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            int affected = stmt.executeUpdate(sql);
            System.out.println("执行成功: 影响行数 = " + affected);
        }
    }
}
