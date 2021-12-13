## Git Command

### commit

1.  查看提交历史

```bash
git log
```

```bash
commit 80ad94bd6c91bea9b04cf06aabff8470ee2a5674 
commit df86c221e4d70f8e7e26fbd333fdc3b1532386f2
commit 3dbca29d82670bab7863aa2d87c4bc10b1a38580
commit dc55fbfa37e6094417861d23c73f3591b4cbee34
commit 3cf62109414c9db8bc918dc1364653ed8e97f9ed
commit 596dadd9a44d140b802c180d6c2a24fff1ba6cb4
```

2.  选取需要合并的历史

```bash
# 从HEAD版本修改5个版本的信息
git rebase -i HEAD~5
# 指定修改某个特定版本号之后的信息
git rebase -i 596dadd
```

```bash
pick 3cf6210 #46475 [hotfix] ORA-01289
pick dc55fbf #46475 [hotfix] start scn
pick 3dbca29 #46475 [hotfix] SPECIFIC_OFFSET mode error
pick df86c22 #46475 [feature] 可本地启动web ui
pick 80ad94b #47110 [feature] Upgrade debezium in mysql-cdc

# p, pick <commit> = 保留变更且使用原commit信息
# r, reword <commit> = 保留变更且修改commit信息
# e, edit <commit> = 保留变更，且可修改commit文件等
# s, squash <commit> = 保留变更且将commit信息合并到上一个commit
# f, fixup <commit> = 保留变更但丢弃commit信息
# b, break = 自动执行'git rebase --continue'
# d, drop <commit> = 删除变更
```

3.  修改commit

```bash
git commit --amend
# --cleanup=scissors
git commit --amend --cleanup=scissors
# 修改commit author
git commit --amend --author "name <email>" 
```

3.  修改确认

```bash
# 保留本次修改
git rebase --continue
# 放弃本次修改
git rebase --abort
```

4.  提交修改

```bash
git push
# 强制提交更新
git push --force
```

### config

```bash
# 默认即为--local
# 查看当前项目配置
git config --local --list
# 修改当前项目配置
git config --local key "value"

# 查看全局配置
git config --global --list
# 修改全局配置
git config --global key "value"
```

### merge

```bash
#Step 1. Fetch and check out the branch for this merge request

git fetch origin
git checkout -b "feature-refactoring" "origin/feature-refactoring"
#Step 2. Review the changes locally

#Step 3. Merge the branch and fix any conflicts that come up

git fetch origin
git checkout "origin/develop"
git merge --no-ff "feature-refactoring"
#Step 4. Push the result of the merge to GitLab

git push origin "develop"
```

