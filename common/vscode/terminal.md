## profiles

```json
{
  "$schema": "https://aka.ms/terminal-profiles-schema",
  "defaultProfile": "{0caa0dad-35be-5f56-a8ff-afceeeaa6101}",
  "profiles": [
    {
      // Make changes here to the cmd.exe profile
      "guid": "{0caa0dad-35be-5f56-a8ff-afceeeaa6101}",
      "name": "Git",
      "cursorShape": "emptyBox",
      "fontFace": "Fira Code",
      "fontSize": 12,
      "commandline": "E:\\Git\\bin\\bash.exe",
      "hidden": false,
      "backgroundImage": "ms-appdata:///roaming/wallpaperlol.jpg",
      "backgroundImageOpacity": 0.5,
      "useAcrylic": false,
      "acrylicOpacity": 0.1,
      "Acrylic": 0.2
    },
    {
      // Make changes here to the powershell.exe profile
      "guid": "{61c54bbd-c2c6-5271-96e7-009a87ff44bf}",
      "name": "Windows PowerShell",
      "commandline": "powershell.exe",
      "cursorShape": "emptyBox",
      "colorScheme": "Material Dark",
      "useAcrylic": false,
      "acrylicOpacity": 0.1,
      "fontFace": "Fira Code",
      "fontSize": 12,
      "hidden": false,
      "backgroundImage": "ms-appdata:///roaming/wallpaperlol.jpg",
      "backgroundImageOpacity": 0.3
    },
    {
      "guid": "{b453ae62-4e3d-5e58-b989-0a998ec441b8}",
      "hidden": false,
      "name": "Azure Cloud Shell",
      "source": "Windows.Terminal.Azure"
    }
  ],
  "schemes": [
    {
      "name": "Material Dark",
      "colors": [
        "#212121",
        "#b7141f",
        "#45dde4",
        "#f6981e",
        "#434ea2",
        "#560088",
        "#0e717c",
        "#efefef",
        "#656565",
        "#e83b3f",
        "#7aba3a",
        "#257fad",
        "#54a4f3",
        "#aa4dbc",
        "#26bbd1",
        "#d9d9d9"
      ],
      "foreground": "#e5e5e5",
      "background": "#171717"
    }
  ],
  "keybindings": [
    {
      "command": "closeTab",
      "keys": ["ctrl+w"]
    },
    {
      "command": "newTab",
      "keys": ["ctrl+t"]
    },
    {
      "command": "newTabProfile0",
      "keys": ["ctrl+shift+1"]
    }
  ]
}
```
