@echo off

for /r %%i in (airtaskdesktop-*-all.jar) do (
  javaw -jar %%~nxi airtask.properties
  goto :eof
)
