## setting 配置

1. version-1

   ```json
   {
     "files.associations": {
       "*.cjson": "jsonc",
       "*.wxss": "css",
       "*.wxs": "javascript",
       "*.vue": "vue"
     },
     "emmet.includeLanguages": {
       "wxml": "html",
       "vue-html": "html",
       "vue": "html"
     },
     "minapp-vscode.disableAutoConfig": true,
     "editor.renderControlCharacters": true,
     "editor.renderWhitespace": "all",
     // 启用后，保存文件时在文件末尾插入一个最终新行
     "files.insertFinalNewline": true,
     // 启用后，将在保存文件时剪裁尾随空格
     "files.trimTrailingWhitespace": true,
     // 加载和侧边栏显示时,忽略的文件/文件夹
     "files.exclude": {
       "**/.svn": true,
       "**/.hg": true,
       "**/.DS_Store": true,
       // "**/_posts":true,
       "**/.sass-cache": true,
       "**/.vscode": true,
       "**/node_modules": true,
       "**/.idea": true
     },
     // 改变 powershell 为 git 相关
     "terminal.integrated.shell.windows": "E:\\Git\\bin\\bash.exe",
     "terminal.integrated.shell.linux": "/bin/bash",
     // 启用后，将使用的参数和方法名称的类型进行提示。
     "docthis.inferTypesFromNames": true,

     "eslint.validate": [
       "javascript",
       "javascriptreact",
       "html",
       {
         "language": "vue",
         "autoFix": true
       }
     ],
     "eslint.options": {
       "plugins": ["html"]
     },
     // 控制编辑器是否应在键入后自动设置行的格式
     "editor.formatOnType": true,
     "editor.formatOnSave": true,
     "window.zoomLevel": 0,
     "breadcrumbs.enabled": true,

     //  #使用带引号替代双引号
     "prettier.singleQuote": true,
     //  #让函数(名)和后面的括号之间加个空格
     "javascript.format.insertSpaceBeforeFunctionParenthesis": true,

     // python
     /**
     -- ubuntu
     "python.pythonPath": "/usr/bin/python3",
     "python.linting.pylintPath": "pylint",
     "python.linting.enabled": true,
     "python.linting.lintOnSave": true,
     // 默认使用pylint对Python文件进行静态检查
     "python.linting.pylintEnabled": true,
     "python.linting.pylintUseMinimalCheckers": true,
     "python.formatting.provider": "autopep8",
     **/

     "extensions.ignoreRecommendations": true,
     "workbench.statusBar.feedback.visible": false,
     "workbench.iconTheme": "material-icon-theme",
     "editor.fontLigatures": true,
     "editor.fontFamily": "'Fira Code', Consolas, 'Courier New', monospace",
     "editor.fontSize": 16,
     "editor.fontWeight": "500",
     "terminal.integrated.fontFamily": "'Fira Code'",
     "terminal.integrated.fontWeight": "500",
     "markdown.preview.fontFamily": "'Fira Code', -apple-system, BlinkMacSystemFont, 'Segoe WPC', 'Segoe UI', 'Ubuntu', 'Droid Sans', sans-serif",
     "editor.suggestSelection": "first",
     "vsintellicode.modify.editor.suggestSelection": "automaticallyOverrodeDefaultValue",
     "workbench.colorTheme": "Monokai Dimmed"
   }
   ```

2. version-2

   ```json
   {
     "files.associations": {
       "*.cjson": "jsonc",
       "*.wxss": "css",
       "*.wxs": "javascript",
       "*.vue": "vue",
       "*.conf": "ignore",
       ".Jenkins": "groovy"
     },
     "emmet.includeLanguages": {
       "wxml": "html",
       "vue-html": "html",
       "vue": "html"
     },
     "minapp-vscode.disableAutoConfig": true,
     "editor.renderControlCharacters": true,
     "editor.renderWhitespace": "all",
     "openapi.securityAuditToken": "CiDQXCJox7/PLxoysxYrBjp33ktRnf9FHqiI7npYtgxSThIYtPOfCuMM0B0aCPbc+t+tCHhLeqg8qiSnGnKoSte7LLQpq78SxChRnEqXNAGIp9o/PwSiv08qoEK7aKJBoK4iVcqnsr7Qj2EpnWLREo6qqCig543kTLOH8kdi60CYdPh58e0qutXha1pTr6uVuw1e7bsRLH8XTI6+OMOU6071gVo2vvEc8YMZkPZh+6Y=",
     // 启用后，保存文件时在文件末尾插入一个最终新行
     "files.insertFinalNewline": true,
     // 启用后，将在保存文件时剪裁尾随空格
     "files.trimTrailingWhitespace": true,
     // 加载和侧边栏显示时,忽略的文件/文件夹
     "files.exclude": {
       "**/.svn": true,
       "**/.hg": true,
       "**/.DS_Store": true,
       // "**/_posts":true,
       "**/.sass-cache": true,
       "**/.vscode": true,
       "**/node_modules": true,
       "**/.idea": true
     },
     // 改变 powershell 为 git 相关
     "terminal.integrated.shell.windows": "E:\\Git\\bin\\bash.exe",
     // 启用后，将使用的参数和方法名称的类型进行提示。
     "docthis.inferTypesFromNames": true,

     "eslint.validate": [
       "javascript",
       "javascriptreact",
       "html",
       {
         "language": "vue",
         "autoFix": true
       }
     ],
     "eslint.options": {
       "plugins": ["html"]
     },
     // 控制编辑器是否应在键入后自动设置行的格式
     "editor.formatOnType": true,
     "editor.formatOnSave": true,
     "window.zoomLevel": 0,
     "breadcrumbs.enabled": true,

     //  #使用带引号替代双引号
     "prettier.singleQuote": true,
     //  #让函数(名)和后面的括号之间加个空格
     "javascript.format.insertSpaceBeforeFunctionParenthesis": true,
     "extensions.ignoreRecommendations": true,
     "workbench.statusBar.feedback.visible": false,
     "workbench.iconTheme": "material-icon-theme",
     "editor.fontLigatures": true,
     "editor.fontFamily": "'Fira Code', Consolas, 'Courier New', monospace",
     "editor.fontSize": 16,
     "editor.fontWeight": "500",
     "terminal.integrated.fontFamily": "'Fira Code'",
     "terminal.integrated.fontWeight": "500",
     "markdown.preview.fontFamily": "'Fira Code', -apple-system, BlinkMacSystemFont, 'Segoe WPC', 'Segoe UI', 'Ubuntu', 'Droid Sans', sans-serif",
     "editor.suggestSelection": "first",
     "vsintellicode.modify.editor.suggestSelection": "automaticallyOverrodeDefaultValue",
     "workbench.colorTheme": "Visual Studio Light",
     "[json]": {
       "editor.defaultFormatter": "esbenp.prettier-vscode"
     },
     "workbench.startupEditor": "newUntitledFile",
     "[markdown]": {
       "editor.defaultFormatter": "esbenp.prettier-vscode"
     },
     "[javascript]": {
       "editor.defaultFormatter": "vscode.typescript-language-features"
     },
     "[vue]": {
       "editor.defaultFormatter": "octref.vetur"
     }
   }
   ```

### vue

1. vue-template

   ```json
    {
      "vue-template": {
        "prefix": "vue-template",
        "body": [
          "<template> "
            "  <div>"
            ""
            "  </div>"
            "</template>"
            ""
            "<script>
            "export default {"
            "  data () {"
            "    return {}"
            "  },"
            "  props: {"
            "    prop: {"
            "      type: String,"
            "      required: false"
            "    }"
            "  },"
            "  components: {},"
            "  computed: {},"
            "  watch: {},"
            "  methods: {},"
            "  beforeCreate () { },"
            "  created () { },"
            "  beforeMount () { },"
            "  mounted () { },"
            "  beforeUpdate () { },"
            "  updated () { },"
            "  beforeDestroy () { },"
            "  destroyed () { },"
            "  // brower has keep-alive will trigger this lifecycle"
            "  activated () { },"
            "  filters: {}"
            "}"
            "</script>"
            ""
            "<style></style>"

        ],
        "description": "your build code"
      }
    }
   ```

2. vscode setting

   ```json
   {
     // ******************************************************************** vue **************************************************************************
     "editor.tabSize": 2, //制表符符号eslint
     "editor.formatOnSave": true, //每次保存自动格式化
     "prettier.eslintIntegration": true, //让prettier使用eslint的代码格式进行校验
     "prettier.semi": true, //去掉代码结尾的分号
     "prettier.singleQuote": true, //使用带引号替代双引号
     "javascript.format.insertSpaceBeforeFunctionParenthesis": true, //让函数(名)和后面的括号之间加个空格
     "vetur.format.defaultFormatter.html": "js-beautify-html", //格式化.vue中html
     "vetur.format.defaultFormatterOptions": {
       "js-beautify-html": {
         // #vue组件中html代码格式化样式
         "wrap_attributes": "force-aligned", //也可以设置为“auto”，效果会不一样
         "wrap_line_length": 200,
         "end_with_newline": true,
         "semi": false,
         "singleQuote": true
       }
     },
     "eslint.format.enable": true, // eslint格式化开启
     "eslint.validate": [
       // eslint校验的文件列表
       "javascript",
       "vue",
       "html"
     ],
     /* 添加如下配置 */
     "vetur.format.defaultFormatter.js": "vscode-typescript", // 取消vetur默认的JavaScript格式化工具
     "[javascript]": {
       "editor.defaultFormatter": "dbaeumer.vscode-eslint" // 只采用eslint的格式化
     },
     "[vue]": {
       "editor.defaultFormatter": "octref.vetur" // vue文件还是采用vetur格式化
     },
     "[jsonc]": {
       "editor.defaultFormatter": "vscode.json-language-features"
     },
     "liveServer.settings.donotShowInfoMsg": true
     // ******************************************************************** vue **************************************************************************
   }
   ```

## ui

- font
  1. Fira Code(https://github.com/tonsky/FiraCode)
- color theme
  1. Monokai Dimmed
- icon theme
  1. Material Icon Theme

## keyboard shutcuts

```json
// Place your key bindings in this file to override the defaults
[
  {
    "key": "ctrl+u",
    "command": "editor.action.transformToUppercase",
    "when": "editorTextFocus"
  },
  {
    "key": "ctrl+l",
    "command": "editor.action.transformToLowercase",
    "when": "editorTextFocus"
  },
  {
    "key": "alt+q",
    "command": "workbench.action.editor.changeLanguageMode"
  },
  {
    "key": "ctrl+k m",
    "command": "-workbench.action.editor.changeLanguageMode"
  },
  {
    "key": "alt+e alt+d",
    "command": "workbench.action.editor.changeEncoding"
  }
]
```

## plugins

|        plugin-name        | function |
| :-----------------------: | :------: |
|     Path Intellisence     |   PATH   |
|          GitLens          |   GIT    |
|         git-graph         |   GIT    |
|      TODO Highlight       |   TOOL   |
|      open in browser      |   TOOL   |
|    VSCode Great Icons     |   TOOL   |
|       vscode-faker        |   TOOL   |
|     Markdown Preview      |    MD    |
|         CSS Peek          |   CSS    |
|       Document This       |    --    |
|          ESLint           |    JS    |
|     HTML Boilerpalte      |    H5    |
|          MDTools          |    MD    |
|  Prettier-Code formatter  |  FORMAT  |
|          TSLint           |  FORMAT  |
|      Vue 2 Snippets       |   VUE    |
|           Vetur           |   VUE    |
|          minapp           |  WECHAT  |
|      TODO Highlight       |   TOOL   |
| Visual Studio Intellicode |   TOOL   |
|        JSON Tools         |   TOOL   |
|         XML Tools         |   TOOL   |
|    Material Icon Theme    |  THEME   |
