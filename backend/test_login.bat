@echo off
chcp 65001 >nul
echo ===================================
echo 测试登录接口
echo ===================================

echo.
echo 1. 测试登录接口 (POST http://localhost:8081/api/v1/auth/login)
echo.

curl -X POST http://localhost:8081/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^
  -w "\nHTTP状态码: %{http_code}\n" ^
  --connect-timeout 5

echo.
echo ===================================
echo 2. 检查数据库中的 admin 用户
echo ===================================
echo 请手动执行以下SQL:
echo mysql -u root -p education_platform -e "SELECT id, username, status, password FROM user WHERE username = 'admin';"

echo.
echo ===================================
echo 3. 检查后端是否在运行
echo ===================================
netstat -ano | findstr :8081

echo.
pause
