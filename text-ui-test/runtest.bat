@ECHO OFF
setlocal enabledelayedexpansion

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM compile the code into the bin folder (excluding JavaFX classes)
set FILES=
for /r ..\src\main\java %%i in (*.java) do (
    set "filename=%%~nxi"
    if not "!filename!"=="Main.java" if not "!filename!"=="MainWindow.java" if not "!filename!"=="DialogBox.java" if not "!filename!"=="Launcher.java" (
        set FILES=!FILES! "%%i"
    )
)
javac -cp ..\src\main\java -Xlint:none -d ..\bin !FILES!
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)
REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ..\bin luffy.Luffy < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
