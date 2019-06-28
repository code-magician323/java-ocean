## git 相关命令:

### git 简介:

- **[Git](https://www.yiibai.com/git/getting-started-git-basics.html) 更像是把数据看作是对小型文件系统的一组快照。 每次你提交更新，或在 Git 中保存项目状态时，它主要对当时的全部文件制作一个快照并保存这个快照的索引。 为了高效，如果文件没有修改，Git 不再重新存储该文件，而是只保留一个链接指向之前存储的文件。 Git 对待数据更像是一个 快照流。**
- **Git 数据库中保存的信息都是以文件内容的哈希值(24b9da6552252987aa493b52f8696cd6d3b0037)来索引，不是文件名**
- **Git 一般只添加数据**
- **remote/origin/master 表示远程分支**
- **Git 有三种状态**

  - committed(已提交): Git 仓库--Git 用来保存项目的元数据和对象数据库的地方
  - modified(已修改): 工作目录--对项目的某个版本独立提取出来的内容--暂存区域是一个文件，保存了下次将提交的文件列表信息，一般在 Git 仓库目录中
  - staged(已暂存): 暂存区域
    ![avatar](http://www.yiibai.com/uploads/images/201707/0607/744160702_48164.png)

### git 安装和设置

### 工作区/暂存区/版本库

- 数量问题: 工作区 和 暂存区 都只有一个, 版本库可有有不同分支的版本库. 通过 ADD 命令将工作区改动添加到暂存区, 通过 COMMIT 将暂存区提交到本地的 HEAD 指向的分支
- HEAD 指向分支, BRANCH 指向提交点

- 工作区(working directory): 简单来说，电脑中能看到的目录，就是一个工作区.
- 版本库(repository): 工作区中有一个隐藏目录.git， 这个包含暂存区和狭义的版本库

- Git 的版本库里存在很多东西，其中最为重要的是 stage（或者叫 index）的暂存区; 还有各个分支(master git 默认创建的)
- init 后 repository 存储如图所示
  ![avatar](https://img-blog.csdn.net/20170614164756098?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjIzMzc4Nzc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
- add 后 repository 存储如图所示: 修改进入缓存区[暂存区]
  ![avatar](https://img-blog.csdn.net/20170614164914194?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjIzMzc4Nzc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
- commit 后 repository 存储如图所示: 修改进入版本库分支
  ![avatar](https://img-blog.csdn.net/20170614165135525)

#### 安装与配置

```shell
# sudo yum install git
sudo apt-get install git
# 查看配置: sys, global, local
git config user.name
git config --global  --list
# 更改设置
git config --global[|system|local] user.name "username"
git config --global user.email maxsu@yiibai.com
git config --global core.editor
# 配置的使用
git help config
```

### 使用

#### 常用指令

```shell
# 查看谁在何时修改了对文件做了什么修改
git blame FILENAME
# 查看远程仓库信息
git remote show origin
```

#### .gitignore 的使用

```shell
# ignore all file contains in
target
# ignore all file endWith .a
*.a
# but do track lib.a, even though you're ignoring .a files above
!lib.a
# only ignore the TODO file in the current directory, not subdir/TODO
/TODO
# ignore all files in the build/ directory
build/
# ignore doc/notes.txt, but not doc/server/arch.txt
doc/*.txt
# ignore all .pdf files in the doc/ directory
doc/**/*.pdf
```

#### INIT/ADD/STATUS

```shell
# 克隆远程仓库到指定文件夹
git clone url [own-repository-name]  --branch xxx [FIELDIR]
cd [own-repository-name]
# 初始化一个新的本地仓库
git init
# 做改动, 将改动从工作区添加到暂存区
git add [-A | text.txt | *]
# 放弃暂存: 将 text.txt 文件从暂存区移出到工作区
git reset HEAD text.txt
# 放弃更改(--): 从版本库分支检出文件
git checkout -- mytext.txt
# 查看当前工作区的状态
git status [--short | -s]
```

#### PSUH

```shell
git push origin srcBranch:destBranch
# 指定上游分支并推送
git push set-upstream origin new_branch
# 强制推送远程分支
git push -f origin xxx
# 删除远程分支
git push origin --delete Chapater6
git push origin :Chapter6
# origin/master 分支的说明
追踪远程的分支, PUSH 动作是执行 PUSH 操作, 再将 origin/master 指向最新的一次 COMMIT_ID
```

#### PULL/PICK

```shell
git pull = git fetch + git merge
git pull origin srcBranch:destBranch
# 将 COMMIT_ID 的改动应用到当前分支, 有顺序的
git cherry-pick COMMIT_ID
```

#### COMMIT

- 基本使用
  ```shell
  # 将改动从暂存区添加到版本库分支
  git commit -m 'your commits'
  # 等价于 git add -A + git commit -m ''
  git commit -a -m 'your commits'
  # 等价. 但是对新文件无效
  git commit -am 'your commits'
  # 修改上一次的 commit
  git commit --amend -m 'new commit'
  ```
- 合并 COMMIT

  ```shell
  # 提交改动
  git add -A
  git commit -m 'xxx'
  # 重命名分支
  git branch -m old-name new-name
  # 切到主分支
  git checkout develop
  # 拉取最新的代码
  git pull
  # 切回之前的分支
  git checkout -b new-new-name
  # rebase develop 的代码
  git rebase develop
  # 解决冲突，继续rebase
  git add .
  git rebase --continue
  # merge 代码进这个分支
  git merge --squash new-name
  # 添加 commit 信息
  git commit -m 'xxx'
  # 推送远程分支
  git push -f origin xxx

  # 第二次提交改动, 没有commit 的提交
  git status
  git add .
  git commit --amend
  git push --set-upstream origin feat-team-share-page -f
  # rebase develop
  git fetch
  git rebase origin/develop
  # resolve conflict
  git add .
  git rebase --continue
  git push -f
  ```

  ```shell
  git log # 查看要 rebase 到的点
  git rebase -i COMMITID # 消除之间的, 并将最后一个改为 squash
  # 退出之后, 删除不要的 commit message
  ```

#### MV/RM

```shell
# 重命名
git mv file_from file_to
# git rm 是删除文件, 并执行 git add 操作
git rm log/\*.log
# rm 只是在工作区进行了删除操作, 并没有执行 git add 操作
rm log/\*.log
```

#### LOG

```shell
# 查看提交历史: 显示每次提交的内容差异, 次数
git log [-p -2]
git log --[stat| pretty] [short| full| fuller] | [=oneline]
git log --graph
git log --graph --abbrev-commit
# 查看操作日志
git reflog
```

|       选项        |            说明            |
| :---------------: | :------------------------: |
|       -(n)        |   仅显示最近的 n 条提交    |
| --since, --after  |  仅显示指定时间之后的提交  |
| --until, --before |  仅显示指定时间之前的提交  |
|     --author      |  仅显示指定作者相关的提交  |
|    --committer    | 仅显示指定提交者相关的提交 |

#### BRANCH

```shell
# 配置别名
git config --global alias.br branch
# 查看分支[所有]
git branch [-a]
# 创建分支
git branch new_branch
# 切换分支[没有就创建新的分支]
# git push set-upstream origin new_branch
git checkout [-b] new_branch [remote_branch]
# 强制删除本地分支
git branch -D test_branch
# 删除远程分支
git push origin --delete Chapater6
git push origin :Chapter6
# 重命名分支
git branch -m new_branch wchar_support

# 查看所在分支最新一次的 commit
git branch -v
# 查看所有的分支, 包含远程分支
git branch -a
# 查看所有分支, 并显示最近一次　COMMIT_ID
git branch -av
# origin/master 分支的说明
追踪远程的分支, PUSH 动作是执行 PUSH 操作, 再将 origin/master 指向最新的一次 COMMIT_ID
```

#### CHECKOUT

```shell
# 放弃对工作区文件的修改, 从上一次提交中检出文件
git checkout -- FILENAME
# 切换[并创建]分支, 并与 origin/master 分支对应
git checkout [-b] new_branch_name [origin/master]
git checkout --track [origin/master]
```

#### RESET 撤销各个阶段的错误文件

```shell
# 已经 add 的文件移除暂存区, 放弃追踪
git reset HEAD FILENAME
# 不指定文件就会全部撤销 add 操作
git reset HEAD^
git reset HEAD~1
git reset commit_id
# 已经 commit 的文件
git log # 查看节点 commit_id
git reset -soft commit_id # 代码保留的回退上一次 commit 节点, 但是会把改动放入缓存区(不需要 add 操作)
git reset -mixed commit_id # 代码保留的回退上一次 commit 节点, 但是不会把改动放入缓存区(需要 add 操作)
git reset -hard commit_id # 代码不保留的回退上一次 commit 节点(完全回退)
# 已经 push 的文件
# git revert是提交一个新的版本，将需要revert的版本的内容再反向修改回去，版本会递增，不影响之前提交的内容
git revert commit_id
```

#### STASH

```shell
# 将现在的改动 stash 起来
git stash
# 查看所有的 stash 信息
git stash list
# 恢复最近的 stash, 并从 list 中删除
git stash pop
# 恢复 stash, 但不从 list 中删除
git stash apply stash@{n}
# 删除 list 中的 stash 信息
git stash drop stash@{n}
```

#### DIFF

```shell
# 查看工作区[目标文件]与暂存区[原始文件]的差别
git diff [FILENAME]
# 查看工作区与指定的 COMMIT_ID 时刻的差别
git diff COMMIT_ID
git diff HEAD # 查看工作区与最新的 COMMIT_ID 之前的差别
# 查看暂存区与指定的 COMMIT_ID 之间的差别
git diff ---[cached |staged] COMMIT_ID
git diff --cached # 查看暂存区与最新的 COMMIT_ID 之前的差别
```

#### REBASE

- rebase 建议只能在自己的且没有 `push` 的分支, ; 在自己的分支上 rebase origin 分支.
- git rebase: **在另一个分支基础之上重新应用，用于把一个分支的修改合并到当前分支**, 出现冲，git 停下来解决冲突，在执行 `git add . git git rebase --continue`, `git rebase --abort 放弃 rebase`
  ```shell
  # rebase 过程中出现了冲突, 要解决冲突;
  git add .
  git rebase --continue
  # git 会继续应用之前余下的补丁
  # 放弃 rebase
  git rebase --abort
  ```
- [参考](https://www.yiibai.com/git/git_rebase.html)
- 示意图
  ![avatar](http://www.yiibai.com/uploads/images/201707/1307/842100748_44775.png)<br/>
  ![avatar](http://www.yiibai.com/uploads/images/201707/1307/810100749_17109.png)
- git merge<br/>
  ![avatar](http://www.yiibai.com/uploads/images/201707/1307/350100750_71786.png)
- git rebase<br/>
  ![avatar](http://www.yiibai.com/uploads/images/201707/1307/845100751_76810.png)
  ![avatar](http://www.yiibai.com/uploads/images/201707/1307/645100753_82870.png)

#### HEAD[/.git/HEAD]

- HEAD 指向的是分支; 分支指向的是提交的 COMMIT 点
  ![avatar](https://img-blog.csdnimg.cn/20190627193737823.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

- FETCH_HEAD:

  - 是一个版本链接， 记录在本地的一个文件中， 指向着目前已经从远程仓库取下来的分支的末端版本

---

## 说明

### git fetch 和 git pull 的区别:

- [参考](https://www.cnblogs.com/ToDoToTry/p/4095626.html)
- git remote: 本地的 repo 和远程的 repo 进行版本对应
- _git remote add：_ 来添加当前本地长度的远程 repo，本地就知道 push 到哪个 branch
- git branch: 为了单独记录软件的某一个发布版本而存在的

* _git branch:_ 可以查看本地分支
* _git branch -r:_ 可以用来查看远程分支
* _git push origin feature/001:_ 地分支和远程分支在 git push 的时候可以随意指定，交错对应，只要不出现**版本从图**即可
* _git pull :_ 等价于 _git fetch + git merge_

- git push 和 commit-id: git commit 操作来保存当前工作到本地的 repo，此时会产生一个 commit-id，这是一个能唯一标识一个版本的序列号。 在使用 git push 后，这个序列号还会同步到远程 repo.
- git merge: 团队协作时使用
- git fetch: **将更新 git remote 中所有的远程 repo 所包含分支的最新 commit-id, 将其记录到.git/FETCH_HEAD 文件中(将远程的 commit-id 拿到本地)**
- git fetch remote_repo: **将更新名称为 remote_repo 的远程 repo 上的所有 branch 的最新 commit-id，将其记录**
- git fetch remote_repo remote_branch_name: **将更新名称为 remote_repo 的远程 repo 上的分支： remote_branch_name**
- git fetch remote_repo remote_branch_name:local_branch_name: **将更新名称为 remote_repo 的远程 repo 上的分支： remote_branch_name ，并在本地创建 local_branch_name 本地分支保存远端分支的所有数据**
- git pull [origin feature/001]: **首先，基于本地的[FETCH_HEAD](#FETCH_HEAD)记录，比对本地的 FETCH_HEAD 记录与远程仓库的版本号，然后 git fetch 获得当前指向的远程分支的后续版本的数据，然后再利用 git merge 将其与本地的当前分支合并**
  ![avatar](https://www.yiibai.com/uploads/allimg/140613/0A025G34-0.jpg)
