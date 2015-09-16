# Introduction #

Prerequisites: phing should be installed (see http://www.phing.info/trac/wiki/Users/Download).

# Details #

After you have installed phing, you can check what build configuration (targets) are available by running:
```
phing -f <XML_filename> -l
```
To actually create the tar.gz and zip archives you need to call phing without the -l argument. It is recommended that you specify a build target. For example, for creating the XE Core release for global market the command is:
```
phing -f build-xe-en.xml xe_en -Dconfig.svn="C:\path_to_svn\svn.exe"
```
As you can see the path for svn is required, because the default is configured for a Linux environment.

Also, the build files contain commands for validating PHP syntax. In order to skip these you should comment the config.php4, config.php52 and config.php53 properties inside build-xe-en.xml.

And don't forget to always run a svn update on the xe-tools code so that you export the right tag from svn (for the right version).