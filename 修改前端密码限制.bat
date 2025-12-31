@echo off
echo ========================================
echo 移除前端密码长度限制
echo ========================================
echo.

echo [1/3] 修改登录页面...
powershell -Command "(Get-Content E:\shixun\frontend\src\views\Login.vue) -replace '{ min: 6, message: ''密码长度不能少于6位'', trigger: ''blur'' }', '' | Set-Content E:\shixun\frontend\src\views\Login.vue"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Login.vue 已修改
) else (
    echo ✗ Login.vue 修改失败
)

echo.
echo [2/3] 修改注册页面...
powershell -Command "(Get-Content E:\shixun\frontend\src\views\Register.vue) -replace '{ min: 6, message: ''密码长度不能少于6位'', trigger: ''blur'' }', '' | Set-Content E:\shixun\frontend\src\views\Register.vue"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Register.vue 已修改
) else (
    echo ✗ Register.vue 修改失败
)

echo.
echo [3/3] 修改个人信息页面...
powershell -Command "(Get-Content E:\shixun\frontend\src\views\campus\Profile.vue) -replace 'else if \(value\.length < 6\) \{ callback\(new Error\(''密码长度不能少于6位''\)\) \}', '' | Set-Content E:\shixun\frontend\src\views\campus\Profile.vue"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Profile.vue 已修改
) else (
    echo ✗ Profile.vue 修改失败
)

echo.
echo 前端修改完成！
echo 请手动检查3个文件是否正确修改
echo.
pause
