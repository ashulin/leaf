# Linux常用指令

## 目录相关

### find 命令

- 查找指定文件名的文件(不区分大小写)：`find -iname "MyProgram.c"` 。
- 对找到的文件执行某个命令：`find -iname "MyProgram.c" -exec md5sum {} \;` 。
- 查找 home 目录下的所有空文件：`find ~ -empty` 。

🦅 **【常用】如何在 /usr 目录下找出大小超过 10MB 的文件?**

- 输入命令 `find /usr -type f -size +10240k` 命令。

🦅 **如何在 /var 目录下找出 90 天之内未被访问过的文件？**

- 输入命令：`find /var \! -atime -90` 。

🦅 **如何在 /home 目录下找出 120 天之前被修改过的文件？**

- 输入命令：`find /home -mtime +120` 。

🦅 **在整个目录树下查找文件 “core” ，如发现则无需提示直接删除它们？**

- 输入命令：`find / -name core -exec rm {} \;` 。

### ls 命令

> listing

- 以易读的方式显示文件大小(显示为 MB,GB…)：`ls -lh` 。
- 以最后修改时间升序列出文件：`ls -ltr` 。
- 在文件名后面显示文件类型：`ls -F` 。

### pwd 命令

> print working directory

输出当前工作目录：`pwd` 

### cd 命令

> change directory

```wiki
cd    进入用户主目录；
cd ~  进入用户主目录；
cd -  返回进入此目录之前所在的目录；
cd ..  返回上级目录（若当前目录为“/“，则执行完后还在“/"；".."为上级目录的意思）；
cd ../..  返回上两级目录；
cd !$  把上个命令的参数作为cd参数使用。
```

### mkdir 命令

> make directory

- 在 home 目录下创建一个名为 temp 的目录：`mkdir ~/temp` 。
- 使用 `-p` 选项可以创建一个路径上所有不存在的目录：`mkdir -p dir1/dir2/dir3/dir4/` 。

### df 命令

> disk free

- 显示文件系统的磁盘使用情况，默认情况下 `df -k` 将以字节为单位输出磁盘的使用量。
- 使用 `df -h` 选项可以以更符合阅读习惯的方式显示磁盘使用量。
- 使用 `df -T` 选项显示文件系统类型。

### rm 命令

> remove

- 删除文件前先确认：`rm -i filename.txt` 。
- 在文件名中使用 shell 的元字符会非常有用。删除文件前先打印文件名并进行确认：`rm -i file*` 。
- 递归删除文件夹下所有文件，并删除该文件夹：`rm -r example` 。

### mv 命令

> move

- 将 file1 重命名为 file2 ，如果 file2 存在则提示是否覆盖：`mv -i file1 file2` 。
- `-v` 会输出重命名的过程，当文件名中包含通配符时，这个选项会非常方便：`mv -v file1 file2` 。

### cp 命令

> copy

- 拷贝 file1 到 file2 ，并保持文件的权限、属主和时间戳：`cp -p file1 file2` 。
- 拷贝 file1 到 file2 ，如果 file2 存在会提示是否覆盖：`cp -i file1 file2` 。

🦅 **有一普通用户想在每周日凌晨零点零分定期备份 /user/backup到 /tmp 目录下，该用户应如何做?**

- 配置如下：

    ```
    crontab -e0 0 * * 7 /bin/cp /user/backup /tmp
    ```

🦅 **每周一下午三点将 /tmp/logs 目录下面的后缀为 \*.log 的所有文件 rsync 同步到备份服务器 192.168.1.100 中同样的目录下面**

- 配置如下：

    ```
    crontab -e00 15 * * 1 rsync -avzP /tmp/logs/*.log root@192.168.1.100:/tmp/logs
    ```

相比来说，`rsync` 比 `scp` 的性能更好。具体可以看看 [《scp 与 rsync 性能实测》](https://tinyhema.iteye.com/blog/2107158) 文章。

### mount 命令

- 如果要挂载一个文件系统，需要先创建一个目录，然后将这个文件系统挂载到这个目录上：

    ```
    # mkdir /u01# mount /dev/sdb1 /u01
    ```

- 也可以把它添加到 fstab 中进行自动挂载，这样任何时候系统重启的时候，文件系统都会被加载：

    ```
    /dev/sdb1 /u01 ext2 defaults 0 2
    ```

### cat 命令

> concatenate：连接

- 你可以一次查看多个文件的内容，下面的命令会先打印 file1 的内容，然后打印 file2 的内容：`cat file1 file2` 。
- `-n` 命令可以在每行的前面加上行号：`cat -n /etc/logrotate.conf` 。

🦅 **如何看当前 Linux 系统有几颗物理 CPU 和每颗 CPU 的核数？**

```
[root@centos6 ~ 10:55 #35]# cat /proc/cpuinfo|grep -c 'physical id'4
[root@centos6 ~ 10:56 #36]# cat /proc/cpuinfo|grep -c 'processor'4
```

### tail 命令

- tail 命令默认显示文件最后的 10 行文本：`tail filename.txt` 。
- 你可以使用 `-n` 选项指定要显示的行数：`tail -n N filename.txt` 。
- 你也可以使用 `-f` 选项进行实时查看，这个命令执行后会等待，如果有新行添加到文件尾部，它会继续输出新的行，在查看日志时这个选项会非常有用。你可以通过 `CTRL-C` 终止命令的执行：`tail -f log-file` 。

### less 命令

> view the contents of a file

- 这个命名可以在不加载整个文件的前提下显示文件内容，在查看大型日志文件的时候这个命令会非常有用：`less huge-log-file.log` 。

- 当你用 less 命令打开某个文件时，下面两个按键会给你带来很多帮助，他们用于向前和向后滚屏：

    ```
    CTRL+F – forward one window
    CTRL+B – backward one window
    ```

## 通用命令

### grep 命令

> General Regular Expression Print：通用正则表达式打印

[《Linux 命令大全 —— grep 命令》](http://man.linuxde.net/grep)

- 在文件中查找字符串(不区分大小写)：`grep -i "the" demo_file` 。
- 输出成功匹配的行，以及该行之后的三行：`grep -A 3 -i "example" demo_text` 。
- 在一个文件夹中递归查询包含指定字符串的文件：`grep -r "ramesh" *` 。

### sed 命令

> Stream Editor

[《Linux 命令大全 —— sed 命令》](http://man.linuxde.net/sed)

- 当你将 Dos 系统中的文件复制到 Unix/Linux 后，这个文件每行都会以 `\r\n` 结尾，sed 可以轻易将其转换为 Unix 格式的文件，使用`\n` 结尾的文件：`sed 's/.$//' filename` 。
- 反转文件内容并输出：`sed -n '1!G; h; p' filename` 。
- 为非空行添加行号：`sed '/./=' thegeekstuff.txt | sed 'N; s/\n/ /'` 。

🦅 **用 sed 命令将指定的路径 /usr/local/http 替换成为 /usr/src/local/http ？**

```
[root@centos7 ~]# echo "/usr/local/http/" | sed 's#/usr/local/#/usr/src/local/#'
```

🦅 **打印 /etc/ssh/sshd_config 的第一百行？**

```
sed -n '100p' /etc/ssh/sshd_config
```

🦅 **用 sed 命令永久关闭防火墙？**

```
[root@centos7 ~]# sed -i.bak 's/SELINUX=enforcing/SELINUX=disabled/' /etc/selinux/config 
[root@centos7 ~]# cat /etc/selinux/config

# This file controls the state of SELinux on the system.
# SELINUX= can take one of these three values:
#     enforcing - SELinux security policy is enforced.
#     permissive - SELinux prints warnings instead of enforcing.
#     disabled - No SELinux policy is loaded.SELINUX=disabled
# SELINUXTYPE= can take one of three two values:
#     targeted - Targeted processes are protected,
#     minimum - Modification of targeted policy. Only selected processes are protected. 
#     mls - Multi Level Security protection.SELINUXTYPE=targeted
```

### awk 命令

> Aho Weinberger and Kernighan：这个语言以作者 Al Aho, Peter Weinberger 和 Brian Kernighan 的姓来命名。

[《Linux 命令大全 —— awk 命令》](http://man.linuxde.net/awk)

- 删除重复行：`$ awk '!($0 in array) { array[$0]; print}' temp` 。
- 打印 `/etc/passwd` 中所有包含同样的 uid 和 gid 的行：`awk -F ':' '$3=$4' /etc/passwd` 。
- 打印文件中的指定部分的字段：`awk '{print $2,$5;}' employee.txt` 。

可能会有胖友刚开始会懵逼，awk 和 sed 命令不是类似的么，那么就可以看看 [《【总结】awk 与 sed 的区别》](https://blog.csdn.net/lonfee88/article/details/6034396) 。

🦅 **打印 /etc/passwd 的 1 到 3 行？**

- 使用 sed 命令：

    ```
    [root@centos7 ~]# sed -n '1,3p' /etc/passwdroot:x:0:0:root:/root:/bin/bashsystem:x:0:0::/home/system:/bin/bashbin:x:1:1:bin:/bin:/sbin/nologin
    ```

- 使用 awk 命令：

    ```
    [root@centos7 ~]# awk 'NR>=1&&NR<=3{print $0}' /etc/passwd   root:x:0:0:root:/root:/bin/bashsystem:x:0:0::/home/system:/bin/bashbin:x:1:1:bin:/bin:/sbin/nologin
    ```

### vim 命令

>  vi Improved

[《Linux vi/vim》](http://www.runoob.com/linux/linux-vim.html)

- 打开文件并跳到第 10 行：`vim +10 filename.txt` 。
- 打开文件跳到第一个匹配的行：`vim +/search-term filename.txt` 。
- 以只读模式打开文件：`vim -R /etc/passwd` 。

### diff 命令

> 貌似不太常用，当学习下。

[《Linux 命令大全 —— diff 命令》](http://man.linuxde.net/diff)

- 比较的时候忽略空白符：`diff -w name_list.txt name_list_new.txt` 。

### sort 命令

> 貌似不太常用，当学习下。

[《Linux 命令大全 —— sort 命令》](http://man.linuxde.net/sort)

- 以升序对文件内容排序：`sort names.txt` 。
- 以降序对文件内容排序：`sort -r names.txt` 。
- 以第三个字段对 `/etc/passwd` 的内容排序：`sort -t: -k 3n /etc/passwd | more` 。

🦅 ****

### xargs 命令

[《Linux 命令大全 —— xargs 命令》](http://man.linuxde.net/xargs)

- 将所有图片文件拷贝到外部驱动器：`ls *.jpg | xargs -n1 -i cp {} /external-hard-drive/directory` 。
- 将系统中所有 jpg 文件压缩打包：`find / -name *.jpg -type f -print | xargs tar -cvzf images.tar.gz` 。
- 下载文件中列出的所有 url 对应的页面：`cat url-list.txt | xargs wget –c` 。

🦅 **把当前目录下所有后缀名为 .txt 的文件的权限修改为 777 ？**

- 方式一，使用 xargs 命令：`find ./ -type f -name "*.txt" |xargs chmod 777` 。
- 方式二，使用 exec 命令：`find ./ -type f -name "*.txt" -exec chmod 777 {}` 。

## 压缩相关

### tar 命令

> tarball

[《tar 压缩解压缩命令详解》](https://www.cnblogs.com/jyaray/archive/2011/04/30/2033362.html)

- 创建一个新的 tar 文件： `tar cvf archive_name.tar dirname/` 。
- 解压 tar 文件：`tar xvf archive_name.tar` 。
- 查看 tar 文件：`tar tvf archive_name.tar` 。

### gzip 命令



[《Linux 命令大全 —— gzip 命令》](http://man.linuxde.net/gzip)

- 创建一个 `*.gz` 的压缩文件：`gzip test.txt` 。
- 解压 `*.gz` 文件：`gzip -d test.txt.gz` 。
- 显示压缩的比率：`gzip -l *.gz` 。

### bzip2 命令

[《Linux 命令大全 —— bzip2 命令》](http://man.linuxde.net/bzip2)

- 创建 `*.bz2` 压缩文件：`bzip2 test.txt` 。
- 解压 `*.bz2` 文件：`bzip2 -d test.txt.bz2` 。

### unzip 命令

[《Linux 命令大全 —— unzip 命令》](http://man.linuxde.net/unzip)

- 解压 `*.zip` 文件：`unzip test.zip` 。
- 查看 `*.zip` 文件的内容：`unzip -l jasper.zip` 。

## 系统命令

### export 命令

[《Linux 命令大全 —— export 命令》](http://man.linuxde.net/export)

- 输出跟字符串 oracle 匹配的环境变量：`export | grep ORCALE` 。
- 设置全局环境变量：`export ORACLE_HOME=/u01/app/oracle/product/10.2.0` 。

### kill 命令

[《Linux 命令大全 —— kill 命令》](http://man.linuxde.net/kill)

kill 用于终止一个进程。一般我们会先用 `ps -ef` 查找某个进程得到它的进程号，然后再使用 `kill -9` 进程号终止该进程。你还可以使用killall、pkill、xkill 来终止进程

> 艿艿：注意，`-9` 表示强制终止指定进程。实际场景下，不会这么做。
>
> 但一般情况下，只需要 `kill 进程编号` 就可结束。

```
$ ps -ef | grep vimramesh    7243  7222  9 22:43 pts/2    00:00:00 vim$ kill -9 7243
```

### passwd 命令

> password

[《Linux 命令大全 —— passwd 命令》](http://man.linuxde.net/passwd)

- passwd 用于在命令行修改密码，使用这个命令会要求你先输入旧密码，然后输入新密码：`passwd` 。
- 超级用户可以用这个命令修改其他用户的密码，这个时候不需要输入用户的密码：`passwd USERNAME` 。
- passwd 还可以删除某个用户的密码，这个命令只有 root 用户才能操作，删除密码后，这个用户不需要输入密码就可以登录到系统：`passwd -d USERNAME` 。

### su 命令

> **switch user**

[《Linux 命令大全 —— su 命令》](http://man.linuxde.net/su)

- su 命令用于切换用户账号，超级用户使用这个命令可以切换到任何其他用户而不用输入密码：`su - USERNAME` 。

- 用另外一个用户名执行一个命令下面的示例中用户 john 使用 raj 用户名执行 ls 命令，执行完后返回 john 的账号：

    ```
    [john@dev-server]$ su - raj -c 'ls'[john@dev-server]$
    ```

- 用指定用户登录，并且使用指定的 shell 程序，而不用默认的：`su -s 'SHELLNAME' USERNAME` 。

### yum 命令

> Yellow dog Updater, Modified

[《Linux 命令大全 —— yum 命令》](http://man.linuxde.net/yum)

- 使用 yum 安装 apache ：`yum install httpd` 。
- 更新 apache ：`yum update httpd` 。
- 卸载/删除 apache ：`yum remove httpd` 。

### rpm 命令

> RPM Package Manager: RPM软件包的管理工具。

[《Linux 命令大全 —— rpm 命令》](http://man.linuxde.net/rpm)

- 使用 rpm 安装 apache ：`rpm -ivh httpd-2.2.3-22.0.1.el5.i386.rpm` 。
- 更新 apache ：`rpm -uvh httpd-2.2.3-22.0.1.el5.i386.rpm` 。
- 卸载/删除 apache ：`rpm -ev httpd` 。

### shutdown 命令

[《Linux 命令大全 —— shutdown 命令》](http://man.linuxde.net/shutdown)

- 关闭系统并立即关机：`shutdown -h now` 。
- 10 分钟后关机：`shutdown -h +10` 。
- 重启：`shutdown -r now` 。
- 重启期间强制进行系统检查：`shutdown -Fr now` 。

### crontab 命令

[《Linux 命令大全 —— crontab 命令》](http://man.linuxde.net/crontab)

- 查看某个用户的 crontab 配置：`crontab -u user -l` 。
- 设置一个每十分钟执行一次的计划任务：`*/10 * * * * /home/ramesh/check-disk-space` 。【前提是，在 crontab 下】

### service 命令

[《Linux 命令大全 —— service 命令》](http://man.linuxde.net/service)

- service 命令用于运行 System V init 脚本，这些脚本一般位于 `/etc/init.d` 文件下，这个命令可以直接运行这个文件夹里面的脚本，而不用加上路径。
- 查看服务状态：`service ssh status` 。
- 查看所有服务状态：`service --status-all` 。
- 重启服务：`service ssh restart` 。

另外，使用 chkconfig 命令，可以设置服务在系统启动时，是否自动启动。详细的，见 [《Linux 命令大全 —— chkconfig 命令》](http://man.linuxde.net/chkconfig) 文章。

### chmod 命令

> change mode

[《Linux 命令大全 —— chmod 命令》](http://man.linuxde.net/chmod)

- chmod 用于改变文件和目录的权限。
- 给指定文件的属主和属组所有权限(包括读、写、执行)：`chmod ug+rwx file.txt` 。
- 删除指定文件的属组的所有权限：`chmod g-rwx file.txt` 。
- 修改目录的权限，以及递归修改目录下面所有文件和子目录的权限：`chmod -R ug+rwx file.txt` 。

### chown 命令

[《Linux 命令大全 —— chown 命令》](http://man.linuxde.net/chown)

- chown 用于改变文件属主和属组。
- 同时将某个文件的属主改为 oracle ，属组改为 db ：`chown oracle:dba dbora.sh` 。
- 使用 `-R` 选项对目录和目录下的文件进行递归修改：`chown -R oracle:dba /home/oracle` 。

### uname 命令

[《Linux 命令大全 —— uname 命令》](http://man.linuxde.net/uname)

- uname 可以显示一些重要的系统信息，例如内核名称、主机名、内核版本号、处理器类型之类的信息：`uname -a` 。

### whereis 命令

[《Linux 命令大全 —— whereis 命令》](http://man.linuxde.net/whereis)

- 当你不知道某个命令的位置时可以使用 whereis 命令，下面使用 whereis 查找 ls 的位置：`whereis ls` 。
- 当你想查找某个可执行程序的位置，但这个程序又不在 whereis 的默认目录下，你可以使用 `-B` 选项，并指定目录作为这个选项的参数。下面的命令在 `/tmp` 目录下查找 lsmk 命令：`whereis -u -B /tmp -f lsmk` 。

### locate 命令

[《Linux 命令大全 —— locate 命令》](http://man.linuxde.net/locate)

- locate 命名可以显示某个指定文件（或一组文件）的路径，它会使用由 updatedb 创建的数据库。
- 下面的命令会显示系统中所有包含 crontab 字符串的文件：`locate crontab` 。

另外，胖友如果使用 CentOS 找不到 locate 命令，可以看看 [《CentOS 系统找到 locate 命令及 locate 搜索不到存在的文件》](https://blog.csdn.net/u014800380/article/details/72476026) 文章。

### man 命令

[《Linux 命令大全 —— man 命令》](http://man.linuxde.net/man)

- 显示某个命令的 man 页面：`man crontab` 。

- 些命令可能会有多个 ma n页面，每个 man 页面对应一种命令类型：`man SECTION-NUMBER commandname` 。

- 命令类型：

    - man 页面一般可以分为 8 种命令类型。

        - 1. 用户命令
        - 1. 系统调用
        - 1. c 库函数
        - 1. 设备与网络接口
        - 1. 文件格式
        - 1. 游戏与屏保
        - 1. 环境、表、宏
        - 1. 系统管理员命令和后台运行命令

    - 例如，我们执行 `whatis crontab` ，你可以看到 crontab 有两个命令类型 1 和 5 ，所以我们可以通过下面的命令查看命令类型 5 的 man 页面：

        ```
        $ whatis crontabcrontab (1)          - maintain crontab files for individual users (V3)crontab (5)          - tables for driving cron$ man 5 crontab
        ```

## 网络相关

### ifconfig 命令

[《Linux 命令大全 —— ifconfig 命令》](http://man.linuxde.net/ifconfig)

- ifconfig 用于查看和配置 Linux 系统的网络接口。
- 查看所有网络接口及其状态：`ifconfig -a` 。
- 使用 up 和 down 命令启动或停止某个接口：`ifconfig eth0 up` 和 `ifconfig eth0 down` 。

🦅 **用一条命令显示本机 eth0 网卡的 IP 地址，不显示其它字符？**

- 输入命令任一一个命令即可：

    ```
    # 方法一：ifconfig eth0|grep inet|awk -F ':' '{print $2}'|awk '{print $1}'# 方法二ifconfig eth0|grep "inet addr"|awk -F '[ :]+' '{print $4}' # 方法三：ifconfig eth0|awk -F '[ :]+' 'NR==2 {print $4}' # 方法四：ifconfig eth0|sed -n '2p'|sed 's#^.*addr:##g'|sed 's# Bc.*$##g'# 方法五：ifconfig eth0|sed -n '2p'|sed -r 's#^.*addr:(.*)  Bc.*$#\1#g'# 方法六(CENTOS7 也适用)：ip addr|grep eth0|grep inet|awk '{print $2}'|awk -F '/' '{print $1}'
    ```

### ping 命令

[《Linux 命令大全 —— ping 命令》](http://man.linuxde.net/ping)

- ping 一个远程主机，只发 5 个数据包：`ping -c 5 gmail.com` 。

🦅 **如何禁止服务器被 ping ？**

```
[root@node0 ~]# echo 0 > /proc/sys/net/ipv4/icmp_echo_ignore_all  // 这个时候，别人是可以 ping 通自己的[root@node1 ~]# ping 192.168.6.6PING 192.168.6.6 (192.168.6.6) 56(84) bytes of data.64 bytes from 192.168.6.6: icmp_seq=1 ttl=64 time=1.79 ms64 bytes from 192.168.6.6: icmp_seq=2 ttl=64 time=0.597 ms[root@node0 ~]# echo 1 > /proc/sys/net/ipv4/icmp_echo_ignore_all[root@node1 ~]# ping 192.168.6.6    // ping 不能了PING 192.168.6.6 (192.168.6.6) 56(84) bytes of data.--- 192.168.6.6 ping statistics ---93 packets transmitted, 0 received, 100% packet loss, time 92168ms
```

### curl 命令

[《Linux 命令大全 —— curl 命令》](http://man.linuxde.net/curl)

如果我们使用 ping 测试某个地址是否能连接，那么 curl 测试用个 URL 是否可以访问。

🦅 **写出一个 curl 命令，访问指定服务器 61.135.169.121 上的如下 URL ：http://www.baidu.com/s?wd=test ，访问的超时时间是 20 秒**

- 输入命令 `curl --connect-timeout 20 http://61.135.169.121/s?wd=test` 。

### wget 命令

[《Linux 命令大全 —— wget 命令》](http://man.linuxde.net/wget)

- 使用 wget 从网上下载软件、音乐、视频：`wget http://prdownloads.sourceforge.net/sourceforge/nagios/nagios-3.2.1.tar.gz` 。
- 下载文件并以指定的文件名保存文件：`wget -O taglist.zip http://www.vim.org/scripts/download_script.php?src_id=7701` 。

### ftp 命令

[《Linux 命令大全 —— ftp 命令》](http://man.linuxde.net/ftp)

- ftp 命令和 sftp 命令的用法基本相似。

- 连接 ftp 服务器并下载多个文件：

    ```
    $ ftp IP/hostnameftp> mget *.html
    ```

- 显示远程主机上文件列表：

    ```
    ftp> mls *.html -/ftptest/features.html/ftptest/index.html/ftptest/othertools.html/ftptest/samplereport.html/ftptest/usage.html
    ```

### ssh 命令

[《Linux 命令大全 —— ssh 命令》](http://man.linuxde.net/ssh)

- 登录到远程主机：`ssh username@remotehost.example.com` 。
- 显示 ssh 客户端版本：`ssh -V` 。

## 【重要】服务器状态相关

通过如下命令，我们可以知道 Linux 服务器运行情况，从而可以排查性能的情况。

因为我们是每小节一个命令，胖友后面可以看看 [《Linux 性能分析工具介绍（CPU，内存，磁盘 I/O，网络）》](https://blog.csdn.net/ZYC88888/article/details/79028175) 文章，它将本小节的命令，又做了一次归类，和介绍。所以，可以结合着一起读读。

### ps 命令

> processes：进程

[《Linux 命令大全 —— ps 命令》](http://man.linuxde.net/ps)

- ps 命令用于显示正在运行中的进程的信息。
- 查看当前正在运行的所有进程：`ps -ef | more` 。
- 以树状结构显示当前正在运行的进程，H 选项表示显示进程的层次结构：`ps -efH | more` 。

🦅 **查看后台所有 java 进程？**

- 方式一：`ps -ef |grep java` 。
- 方式二：`jps -m` 。

### uptime 命令

[《Linux 命令大全 —— uptime 命令》](http://man.linuxde.net/uptime)

![uptime](http://static2.iocoder.cn/64991a70b828228d02ec18bc722ed10f)

这个命令可以快速查看机器的负载情况。在 Linux 系统中，这些数据表示等待 CPU 资源的进程和阻塞在不可中断 IO 进程（进程状态为 D）的数量。这些数据可以让我们对系统资源使用有一个宏观的了解。

命令的输出分别表示 1 分钟、5 分钟、15 分钟的平均负载情况。通过这三个数据，可以了解服务器负载是在趋于紧张还是趋于缓解。

- 如果 1 分钟平均负载很高，而 15 分钟平均负载很低，说明服务器正在命令高负载情况，需要进一步排查 CPU 资源都消耗在了哪里。
- 反之，如果 15 分钟平均负载很高，1 分钟平均负载较低，则有可能是 CPU 资源紧张时刻已经过去。
- 上面例子中的输出，可以看见最近 1 分钟的平均负载非常高，且远高于最近 15 分钟负载，因此我们需要继续排查当前系统中有什么进程消耗了大量的资源。可以通过下文将会介绍的 vmstat、mpstat 等命令进一步排查。

另外，还有一个 [《Linux 命令大全 —— w 命令》](http://man.linuxde.net/w) ，也是使用比较方便的，快速查看系统负载情况的命令。

### dmesg 命令

[《Linux 命令大全 —— dmesg 命令》](http://man.linuxde.net/dmesg)

![dmesg](http://static2.iocoder.cn/b1e0efa3199c1dad93db3f69de812e7d)

该命令会输出系统日志的最后 10 行。示例中的输出，可以看见一次内核的 oom kill 和一次 TCP 丢包。这些日志可以帮助排查性能问题。千万不要忘了这一步。

### vmstat 命令

[《Linux 命令大全 —— vmstat 命令》](http://man.linuxde.net/vmstat)

![vmstat](http://static2.iocoder.cn/cb2d25b03441be2a1c068e336390b895)

vmstat 命令，每行会输出一些系统核心指标，这些指标可以让我们更详细的了解系统状态。后面跟的参数 1 ，表示每秒输出一次统计信息，表头提示了每一列的含义，这几介绍一些和性能调优相关的列：

- r：等待在 CPU 资源的进程数。这个数据比平均负载更加能够体现 CPU 负载情况，数据中不包含等待 IO 的进程。如果这个数值大于机器 CPU 核数，那么机器的 CPU 资源已经饱和。
- free：系统可用内存数（以千字节为单位），如果剩余内存不足，也会导致系统性能问题。下文介绍到的 free 命令，可以更详细的了解系统内存的使用情况。
- si，so：交换区写入和读取的数量。如果这个数据不为 0 ，说明系统已经在使用交换区（swap），机器物理内存已经不足。
- us, sy, id, wa, st：这些都代表了 CPU 时间的消耗，它们分别表示用户时间(user)、系统（内核）时间(sys)、空闲时间(idle)、IO等待时间(wait)和被偷走的时间(stolen，一般被其他虚拟机消耗)。

上述这些 CPU 时间，可以让我们很快了解 CPU 是否处于繁忙状态。一般情况下，如果用户时间和系统时间相加非常大，CPU 出于忙于执行指令。如果IO等待时间很长，那么系统的瓶颈可能在磁盘 IO 。

示例命令的输出可以看见，大量 CPU 时间消耗在用户态，也就是用户应用程序消耗了 CPU 时间。这不一定是性能问题，需要结合 r 队列，一起分析。

### mpstat 命令

![mpstat](http://static2.iocoder.cn/8c61ee5a2a59b6fb2de4edd893cb7412)

该命令可以显示每个 CPU 的占用情况，如果有一个 CPU 占用率特别高，那么有可能是一个单线程应用程序引起的。

### pidstat 命令

![pidstat](http://static2.iocoder.cn/21cb3c25dac3045e9392a55906c581e4)

pidstat 命令输出进程的 CPU 占用率，该命令会持续输出，并且不会覆盖之前的数据，可以方便观察系统动态。如上的输出，可以看见两个 JAVA 进程占用了将近 1600% 的CPU时间，既消耗了大约 16 个 CPU 核心的运算资源。

### iostat 命令

![iostat](http://static2.iocoder.cn/7cf02db233d20982427c0799c713fb32)

- r/s, w/s, rkB/s, wkB/s：分别表示每秒读写次数和每秒读写数据量（千字节）。读写量过大，可能会引起性能问题。
- await：IO 操作的平均等待时间，单位是毫秒。这是应用程序在和磁盘交互时，需要消耗的时间，包括 IO 等待和实际操作的耗时。如果这个数值过大，可能是硬件设备遇到了瓶颈或者出现故障。
- avgqu-sz：向设备发出的请求平均数量。如果这个数值大于 1 ，可能是硬件设备已经饱和（部分前端硬件设备支持并行写入）。
- %util：设备利用率。这个数值表示设备的繁忙程度，经验值是如果超过 60 ，可能会影响 IO 性能（可以参照 IO 操作平均等待时间）。如果到达 100% ，说明硬件设备已经饱和。

如果显示的是逻辑设备的数据，那么设备利用率不代表后端实际的硬件设备已经饱和。值得注意的是，即使 IO 性能不理想，也不一定意味这应用程序性能会不好，可以利用诸如预读取、写缓存等策略提升应用性能。

### free 命令

[《Linux 命令大全 —— free 命令》](http://man.linuxde.net/free)

![free](http://static2.iocoder.cn/c6afc22251bafccf163d532c1e96b7f2)

free 命令可以查看系统内存的使用情况，`-m` 参数表示按照兆字节展示。最后两列分别表示用于IO缓存的内存数，和用于文件系统页缓存的内存数。需要注意的是，第二行 `-/+ buffers/cache` ，看上去缓存占用了大量内存空间。

这是 Linux 系统的内存使用策略，尽可能的利用内存，如果应用程序需要内存，这部分内存会立即被回收并分配给应用程序。因此，这部分内存一般也被当成是可用内存。

如果可用内存非常少，系统可能会动用交换区(如果配置了的话)，这样会增加 IO 开销(可以在 iostat 命令中体现)，降低系统性能。

🦅 **【重要】Linux系统里，您知道 buffer 和 cache 如何区分吗？**

Buffer 和 Cache 都是内存中的一块区域。

- 当 CPU 需要写数据到磁盘时，由于磁盘速度比较慢，所以 CPU 先把数据存进 Buffer ，然后 CPU 去执行其他任务，Buffer中的数据会定期写入磁。
- 当 CPU 需要从磁盘读入数据时，由于磁盘速度比较慢，可以把即将用到的数据提前存入 Cache ，CPU 直接从 Cache中 拿数据要快的多。

详细的，可以看看 [《Linux中 buffer/cache、swap、虚拟内存和Page》](https://blog.csdn.net/cymm_liu/article/details/8228828) 。

### sar 命令

[《Linux 命令大全 —— sar 命令》](http://man.linuxde.net/sar)

![sar 设备](http://static2.iocoder.cn/a32adc09e567efe7ca9ab0d7e77c22ce)

- sar 命令在这里可以查看网络设备的吞吐率。在排查性能问题时，可以通过网络设备的吞吐量，判断网络设备是否已经饱和。如示例输出中，eth0 网卡设备，吞吐率大概在 22 Mbytes/s ，既 176 Mbits/sec ，没有达到 1Gbit/sec 的硬件上限。

![sar TCP](http://static2.iocoder.cn/c47f9cf21355ced275d4c1dadc254b21)

- sar命令在这里用于查看 TCP 连接状态，其中包括：
    - active/s：每秒本地发起的TCP连接数，既通过connect调用创建的TCP连接；
    - passive/s：每秒远程发起的TCP连接数，即通过accept调用创建的TCP连接；
    - retrans/s：每秒TCP重传数量；

TCP 连接数可以用来判断性能问题是否由于建立了过多的连接，进一步可以判断是主动发起的连接，还是被动接受的连接。TCP 重传可能是因为网络环境恶劣，或者服务器压力。

🦅 **我们可以使用哪个命令查看系统的历史负载（比如说两天前的）？**

```
sar -q -f /var/log/sa/sa22 # 查看 22 号的系统负载
```

### top 命令

[《Linux 命令大全 —— top 命令》](http://man.linuxde.net/top)

![top](http://static2.iocoder.cn/27d6182eb0d582a975870534368a436b)

top 命令包含了前面好几个命令的检查的内容。比如系统负载情况（uptime）、系统内存使用情况（free）、系统 CPU 使用情况（vmstat）等。因此通过这个命令，可以相对全面的查看系统负载的来源。同时，top 命令支持排序，可以按照不同的列排序，方便查找出诸如内存占用最多的进程、CPU占用率最高的进程等。

但是，top 命令相对于前面一些命令，输出是一个瞬间值，如果不持续盯着，可能会错过一些线索。这时可能需要暂停 top 命令刷新，来记录和比对数据。

### netstat 命令

> network state

[《Linux 命令大全 —— netstat 命令》](http://man.linuxde.net/netstat)

🦅 **如何查看系统都开启了哪些端口？**

```
[root@centos6 ~ 13:20 #55]# netstat -lnpActive Internet connections (only servers)Proto Recv-Q Send-Q Local Address               Foreign Address             State       PID/Program nametcp        0      0 0.0.0.0:22                  0.0.0.0:*                   LISTEN      1035/sshdtcp        0      0 :::22                       :::*                        LISTEN      1035/sshdudp        0      0 0.0.0.0:68                  0.0.0.0:*                               931/dhclientActive UNIX domain sockets (only servers)Proto RefCnt Flags       Type       State         I-Node PID/Program name    Pathunix  2      [ ACC ]     STREAM     LISTENING     6825   1/init              @/com/ubuntu/upstartunix  2      [ ACC ]     STREAM     LISTENING     8429   1003/dbus-daemon    /var/run/dbus/system_bus_socket
```

🦅 **如何查看网络连接状况？**

```
[root@centos6 ~ 13:22 #58]# netstat -anActive Internet connections (servers and established)Proto Recv-Q Send-Q Local Address               Foreign Address             Statetcp        0      0 0.0.0.0:22                  0.0.0.0:*                   LISTENtcp        0      0 192.168.147.130:22          192.168.147.1:23893         ESTABLISHEDtcp        0      0 :::22                       :::*                        LISTENudp        0      0 0.0.0.0:68                  0.0.0.0:*// ... 省略其它
```

🦅 **如何统计系统当前进程连接数？**

- 输入命令 `netstat -an | grep ESTABLISHED | wc -l` 。
- 输出结果 `177` 。一共有 177 连接数。

🦅 **用 netstat 命令配合其他命令，按照源 IP 统计所有到 80 端口的 ESTABLISHED 状态链接的个数？**

> 严格来说，这个题目考验的是对 awk 的使用。

- 首先，使用 `netstat -an|grep ESTABLISHED` 命令。结果如下：

    ```
    tcp        0      0 120.27.146.122:80       113.65.18.33:62721      ESTABLISHEDtcp        0      0 120.27.146.122:80       27.43.83.115:47148      ESTABLISHEDtcp        0      0 120.27.146.122:58838    106.39.162.96:443       ESTABLISHEDtcp        0      0 120.27.146.122:52304    203.208.40.121:443      ESTABLISHEDtcp        0      0 120.27.146.122:33194    203.208.40.122:443      ESTABLISHEDtcp        0      0 120.27.146.122:53758    101.37.183.144:443      ESTABLISHEDtcp        0      0 120.27.146.122:27017    23.105.193.30:50556     ESTABLISHED// ... 省略其它
    ```

- 然后，进一步修改命令，使用 `netstat -an|grep ESTABLISHED|grep ":80"|awk 'BEGIN{FS="[[:space:]:]+"}{print $4}'` 命令。结果如下：

    ```
    120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.122120.27.146.12210.47.111.216// ... 省略其它
    ```

    - 说明：`FS` 是字段分隔符，简单的可以用多个 awk 过滤。

- 最后，再进一步修改命令，使用 `netstat -an|grep ESTABLISHED|grep ":80"|awk 'BEGIN{FS="[[:space:]:]+"}{print $4}'|sort|uniq -c|sort -nr` 命令。结果如下：

    ```
    47 120.27.146.122 1 10.47.111.216
    ```

    - 第一列为连接数，第二列为 IP 。

> 虽然我们这里罗列了很多的命令，下面，还是会有其它命令。

# 参考

[Linux命令大全](https://man.linuxde.net/ls)