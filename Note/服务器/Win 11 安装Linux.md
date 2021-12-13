## Win 11 安装Linux

由于开发过程中需要用到一些必须在Linux部署的服务，如Docker等，故需要安装Linux。

部署步骤：

-   CPU启用虚拟化
-   Windows启用"适用于 Linux 的 Windows 子系统" 
-   Windows启用"虚拟机平台"
-   更新Linux内核
-   升级WSL至WSL2
-   安装Linux

### CPU启用虚拟化

>   以下为微星主板 启用AMD CPU 虚拟化功能

-   进入BIOS
-   启用专家模式
-   点击OC配置下的CPU高级特性
-   启用SVM (Support Virtual Machin) Mode

### 升级至WSL2

#### 运行 WSL 2 的要求

>   若要更新到 WSL 2，需要运行 Windows 10。
>
>   -   对于 x64 系统：**版本 1903** 或更高版本，采用 **内部版本 18362** 或更高版本。
>   -   对于 ARM64 系统：**版本 2004** 或更高版本，采用 **内部版本 19041** 或更高版本。
>   -   低于 18362 的版本不支持 WSL 2。 使用 [Windows Update 助手](https://www.microsoft.com/software-download/windows10)更新 Windows 版本。

由于Windows 11必须由Windows 10的2004或更高版本升级，因此是符合要求的。

#### 启用前置功能

以管理员身份打开 PowerShell 并运行：

```powershell
# 启用适用于 Linux 的 Windows 子系统
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
# 启用虚拟机功能
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
```

也可以在`控制面板`->`程序和功能`->`启用或关闭Windows功能`，启用对应功能即可。

**重新启动** 计算机，以完成 WSL 安装并更新到 WSL 2。

#### 更新Linux内核

下载并安装更新包：

[适用于 x64 计算机的 WSL2 Linux 内核更新包](https://wslstorestorage.blob.core.windows.net/wslblob/wsl_update_x64.msi)

#### 启用WSL2

以管理员身份打开 PowerShell 并运行：

```powershell
# 设置WSL 2 设置为默认版本
wsl --set-default-version 2
# 确定是否启用成功
wsl -l -v
```

### 安装Linux 发行版

1.   打开 [Microsoft Store](https://aka.ms/wslstore)，并选择你偏好的 Linux 发行版。
2.   单击以下链接会打开每个分发版的 Microsoft Store 页面：
     -   [Ubuntu 18.04 LTS](https://www.microsoft.com/store/apps/9N9TNGVNDL3Q)
     -   [Ubuntu 20.04 LTS](https://www.microsoft.com/store/apps/9n6svws3rx71)
     -   [openSUSE Leap 15.1](https://www.microsoft.com/store/apps/9NJFZK00FGKV)
     -   [SUSE Linux Enterprise Server 12 SP5](https://www.microsoft.com/store/apps/9MZ3D1TRP8T1)
     -   [SUSE Linux Enterprise Server 15 SP1](https://www.microsoft.com/store/apps/9PN498VPMF3Z)
     -   [Kali Linux](https://www.microsoft.com/store/apps/9PKR34TNCV07)
     -   [Debian GNU/Linux](https://www.microsoft.com/store/apps/9MSVKQC78PK6)
     -   [Fedora Remix for WSL](https://www.microsoft.com/store/apps/9n6gdm4k2hnc)
     -   [Pengwin](https://www.microsoft.com/store/apps/9NV1GV1PXZ6P)
     -   [Pengwin Enterprise](https://www.microsoft.com/store/apps/9N8LP0X93VCP)
     -   [Alpine WSL](https://www.microsoft.com/store/apps/9p804crf0395)
3.   安装所选发行版
4.   [为新的 Linux 分发版创建用户帐户和密码](https://docs.microsoft.com/zh-cn/windows/wsl/user-support)

## 参考

[适用于 Linux 的 Windows 子系统安装指南 (Windows 10)](https://docs.microsoft.com/zh-cn/windows/wsl/install-win10#step-4---download-the-linux-kernel-update-package)

[WSL 2 Won't Run Ubuntu - Error 0x80370102](https://askubuntu.com/questions/1264102/wsl-2-wont-run-ubuntu-error-0x80370102)