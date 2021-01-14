## plugins

|     name     | function |
| :----------: | :------: |
|   prettier   |  format  |
|   preview    | preview  |
| markdownlint |   lint   |
|  markdown all in one| tip|

## common config

1. user

   ```json
   "[markdown]": {
       // 自动保存
       "editor.formatOnSave": true,
       // 显示空格
       "editor.renderWhitespace": "all",
       // 快速补全
       "editor.quickSuggestions": {
       "other": true,
       "comments": true,
       "strings": true
       },
       // snippet 提示优先
       "editor.snippetSuggestions": "top",
       "editor.tabCompletion": "on",
       // 使用enter 接受提示
       "editor.acceptSuggestionOnEnter": "on",
       "editor.defaultFormatter": "esbenp.prettier-vscode"
   },
   "markdownlint.config": {
       "default": true,
       "MD041": { "level": 2 }
   }
   ```
   
## reference

1. [markdown](https://www.markdownguide.org/basic-syntax/)
2. [disable specified rule](https://superuser.com/questions/1295409/how-do-i-change-markdownlint-settings-in-visual-studio-code)
3. [rules detai](https://www.jianshu.com/p/51523a1c6fe1)

