REM ******************
REM ** Tomcat Copy  **
REM ******************
REM Copies the files needed to deploy a GWT app to Tomcat
REM version 1.1
REM Last update 6/21/08
REM
REM ** Adapted to using common JAR's (so deleted unused directories)
REM To customize for a project, change directory
REM dataFest to yourDir
REM and also check JAR's being copied to lib
REM
c:
cd\
cd c:\tomcat\tomcat 5.5\webapps\festival\WEB-INF
del *.xml
del *.txt
del *.1
cd c:\tomcat\tomcat 5.5\webapps\festival\META-INF
del *.xml
cd c:\tomcat\tomcat 5.5\webapps\festival
del *.htm
del *.js
del *.css
del *.xml
del *.gif
del *.rpc
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.htm
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.js
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.css
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.xml
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.gif
copy C:\RSDDocs\Projects\festival\www\com.digitalenergyinc.festival.festival\*.rpc
cd \
cd C:\tomcat\Tomcat 5.5\webapps\festival\META-INF
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\public\META-INF\*.xml

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\public\WEB-INF\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\lib
del *.jar
copy C:\RSDDocs\Projects\festJars\festCommon.jar
copy C:\RSDDocs\Projects\festJars\festServer.jar
copy C:\java\gwt-windows-1.4.61\gwt-user.jar
copy C:\java\gwt-windows-1.4.61\gwt-servlet.jar
copy C:\java\gwt-incubator\gwt-incubator_1-4_final.jar
copy C:\java\logging-log4j-1.2.14\dist\lib\log4j-1.2.14.jar
copy C:\java\gwt-stuff\GWT-Stuff-20070605.jar
copy C:\java\dozer-4.0\dist\dozer-4.0.jar
copy C:\java\commons-logging\commons-logging-1.0.4.jar
copy C:\java\commons-collections-3.2\commons-collections-3.2.jar
copy C:\java\commons-beanutils-1.7.0\commons-beanutils.jar
copy C:\java\commons-lang-2.3\commons-lang-2.3.jar
copy C:\mysql\mysql-connector-java-5.0.4\mysql-connector-java-5.0.4-bin.jar
copy C:/java/gwt-widgets-0.1.5/gwt-widgets-0.1.5.jar

cd C:\tomcat\Tomcat 5.5\webapps\festival\images
del *.jpg
del *.png
del *.gif
copy C:\RSDDocs\Projects\festival\src\com\digitalenergyinc\festival\public\images\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\client
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\client\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\client\model
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\client\model\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\client\view
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\client\view\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\client\control
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\client\control\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\public
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\public\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes\com\digitalenergyinc\festival\server
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\server\*.*

cd C:\tomcat\Tomcat 5.5\webapps\festival\WEB-INF\classes
copy C:\RSDDocs\Projects\festival\bin\com\digitalenergyinc\festival\public\dozerBeanMapping.*
pause
