# Introduction #

> Are you a developer interested in extending the native functionalities provided by XE? Well, this is the right place for you to start. This tool can be used by both types of developers:
    * the ones that already have experience with XE
    * the ones that are just getting started using XE

> As you may know XE makes it very easy for developers to add functionality to the core code.  You can build _modules_ on top of the existing ones or write your own from scratch. Also, you can change the behavior of the existing modules by using _add-ons_ or insert snippets of code directly into pages using _widgets_.

> Basically this tool provides an easy way to start creating new modules, widgets and addons. For the developers that are new to XE it provides some demo versions that will help you to get started.


# Prerequisites #

> You need to have PHP 5.2.0 or newer installed. That’s pretty much it!

> There are also some PHP extensions (libxml and PHAR) required but usually these extensions are installed and enabled by default, so in most cases you should not worry about these extensions.

> _Note 1_: this tool does not require an XE installation on the same machine. But in order to test and use what this tool produces you need to have an XE installation.

> _Note 2_: if you want to build yourself the PHAR file for this tool you will need to have _Phing_ installed. But this is not mandatory because you can use an already built PHAR file or to use the raw files (not embedded everything in a PHP archive).

# Download and Run #

> This tool was created in PHP and should be executed from the PHP CLI (command line). So it works in the same way on all platforms (Unix, Windows, Mac).

> There are 3 possible ways to download the necessary files in order to be able to run the script:
    * download the PHAR (PHP archive)(like an executable file) from [here](http://www.google.com/url?q=http%3A%2F%2Fcode.google.com%2Fp%2Fxe-tools%2Fdownloads%2Fdetail%3Fname%3Dxe-generator.phar) (recommended)
    * download the ZIP archive from [here](http://www.google.com/url?q=http%3A%2F%2Fcode.google.com%2Fp%2Fxe-tools%2Fdownloads%2Fdetail%3Fname%3Dxe-generator.zip)
    * checkout the latest tool source code using the following command line:
> > > _svn checkout https://xe-tools.googlecode.com/svn/trunk/generator generator_


> The PHAR files provides a way to put entire PHP applications into a single file called a "PHAR" (PHP Archive) for easy distribution and installation. So instead of downloading several files you can download a single one that contains everything. That’s why this method is the recommended one.

> In order to run the tool in the form of a PHAR file you should use the following command line:
> > _php xe-generator.phar_


> If you prefer not using the PHAR method you should use the following command line (from the generator folder):
> > _php xe-generator.php_


> Note: In case you prefer using the using the ZIP archive you should first unzip it in a folder (let’s say to a “generator” folder).

# Available Commands #

> You can see below the list of available commands that are supported by the tool:
    * _module_
> > This helps you generate a new module.<br />
> > A module in XE is a program. Multiple modules can be combined into a bigger module, and each module can operate independently. In short, modules are programs that are additionally installed to implement specific features on websites. Bulletin board, Forum, Wiki and Textyle can be considered the representative modules provided in XE.<br />
> > The list of the possible modules that can be generated and their particular options are presented in the next sections.
    * _widget_
> > This helps you generate a new widget.<br />
> > A widget is inserted into and operated in a layout or a page module. Widgets can display data already created in modules, or create its own data. For example, you can use a widget to display the list of the latest posts in the front page of your website. Language selection and login information display widgets are examples of representative widgets provided by XE.<br />
> > The list of the possible widgets that can be generate and their particular options are presented in the next sections.
    * _addon_
> > This helps you generate a new addon.<br />
> > Add-ons are used to control operation, and not to display HTML results. They are small programs that interrupt the operations of modules and performs its own operations. In addition to modules, addons execute their own features by themselves. However, addons are different from modules in that they do not provide the execution results to outside. Default counter and mobile XE are examples of the representative Addons provided by XE.<br />
> > The list of the possible addons that can be generate and their particular options are presented in the next sections.

# Module Generation #


> There are 3 types of modules that can be generated:
    * _basic_
> > This generates a new module that contains the entire structure (necessary files and folders). So instead of loosing time with the creation of all files and folders (necessary for a module), one can use this new generated module and focus the energy on implementing their custom behaviour.<br />
> > It’s recommended for XE advanced developers.
    * _developer_
> > This generates an enhanced basic module. In addition to the basic one, this has a standard backend functionality implemented. So one can create, modify, delete and list instances of this module.<br />
> > It’s recommended for XE intermediate developers.
    * _demo_
> > This generates an enhanced developer module. In addition to the developer one, this has some functionality implemented on front end. This functionality consist of: adding a some email addresses into a table(created especially for this module), displaying the list of already inserted email addresses. From this module one can learn how to: create a new table for a certain module, create some front end view methods, some front end associated controller methods(actions), use the rulesets.<br />
> > It’s recommended for XE beginner developers.


> You can see below the descriptions of the possible options:
    * _name_ - required (`*`)
> > Description: the name of the module<br />Applies to: basic, developer, demo
    * _desc_ - `[`optional`]`
> > Description: the description of the module<br />Applies to: basic, developer, demo
    * _vers_ - `[`optional`]`
> > Description: the version number of the module<br />Applies to: basic, developer, demo
    * _date_ - `[`optional`]`
> > Description: creation date of the module<br />Applies to: basic, developer, demo
    * _cat_ - `[`optional`]`
> > Description: the category of the module<br />Applies to: basic, developer, demo
    * _author-name_ - `[`optional`]`
> > Description: the name of the module’s author<br />Applies to: basic, developer, demo
    * _author-url_ - `[`optional`]`
> > Description: the URL of the module’s author<br />Applies to: basic, developer, demo
    * _author-email_ - `[`optional`]`
> > Description: the email of the module’s author<br />Applies to: basic, developer, demo
    * _about_ - `[`optional`]`
> > Description: the about field of the module that will appear in the lang file<br />Applies to: basic, developer, demo
    * _del-warn-msg_ - `[`optional`]`
> > Description: the delete warning message that will be displayed when deleting an instance of the module<br />Applies to: basic, developer, demo<br />

> You can see below an example of a command line that will create a new demo module called _email_:<br />
> > _php xe-generator.phar module demo name email desc "This module is for managing email addresses" vers "0.3" date "2012-03-15" cat "service" author-name "Surname Name" author-url "http://www.domain.com" author-email "admin@domain.com" about "It helps you manage email addresses" del-warn-msg "If you delete it you cannot undo the operation"_

# Widget Generation #


> There are 2 types of widgets that can be generated:
    * basic
> > This generates a new widget that contains the entire structure (necessary files and folders). So instead of loosing time with the creation of all files and folders (necessary for a widget), one can use this new generated widget and focus the energy on implementing their custom behaviour.<br />
> > It’s recommended for XE advanced developers.
    * demo
> > This generates an enhanced basic widget. In addition to the basic one, this has some functionality implemented. This functionality consist of: showing the usage percentage of well-known browsers (Mozilla, Chrome, IE, others)<br />
> > It’s recommended for XE beginner developers.


> You can see below the descriptions of the possible options:
    * _name_ - required (`*`)
> > Description: the name of the widget<br />Applies to: basic, demo
    * _alias_ - required (`*`)
> > Description: the alias of the widget (how it appears in the widgets list)<br />Applies to: basic, demo
    * _desc_ - `[`optional`]`
> > Description: the description of the widget<br />Applies to: basic, demo
    * _vers_ - `[`optional`]`
> > Description: the version number of the widget<br />Applies to: basic, demo
    * _date_ - `[`optional`]`
> > Description: creation date of the widget<br />Applies to: basic, demo
    * _author-name_ - `[`optional`]`
> > Description: the name of the widget’s author<br />Applies to: basic, demo
    * _author-url_ - `[`optional`]`
> > Description: the URL of the widget’s author<br />Applies to: basic, demo
    * _author-email_ - `[`optional`]`
> > Description: the email of the widget’s author<br />Applies to: basic, demo


> You can see below an example of a command line that will create a new demo widget called _browser\_usage_:<br />
> > _php xe-generator.phar widget demo name browser\_usage alias "Browser Usage" desc "This widget displays the statistics of the browser usage" vers "0.4" date "2012-03-15" author-name "Surname Name" author-url "http://www.domain.com" author-email "admin@domain.com"_

# Addon Generation #


> There are 2 types of addons that can be generated:
    * _basic_
> > This generates a new addon that contains the entire structure (necessary files and folders). So instead of loosing time with the creation of all files and folders (necessary for a addon), one can use this new generated addon and focus the energy on implementing their custom behaviour.<br />
> > It’s recommended for XE advanced developers.
    * _demo_
> > This generates an enhanced basic addon. In addition to the basic one, this has some functionality implemented. This functionality consist of: transform all the links in a page such that all of them open in a new browser window.<br />
> > It’s recommended for XE beginner developers.


> You can see below the descriptions of the possible options:
    * _name_ - required (`*`)
> > > Description: the name of the addon<br />Applies to: basic, demo
    * _alias_ - required (`*`)
> > > Description: the alias of the addon (how it appears in the addons list)<br />Applies to: basic, demo
    * _desc_ - `[`optional`]`
> > > Description: the description of the addon<br />Applies to: basic, demo
    * _vers_ - `[`optional`]`
> > > Description: the version number of the addon<br />Applies to: basic, demo
    * _date_ - `[`optional`]`
> > > Description: creation date of the addon<br />Applies to: basic, demo
    * _author-name_ - `[`optional`]`
> > > Description: the name of the addon’s author<br />Applies to: basic, demo
    * _author-url_ - `[`optional`]`
> > > Description: the URL of the addon’s author<br />Applies to: basic, demo
    * _author-email_ - `[`optional`]`
> > > Description: the email of the addon’s author<br />Applies to: basic, demo


> You can see below an example of a command line that will create a new demo addon called _new\_window_:<br />
> > _php xe-generator.phar addon demo name new\_window\_links alias "New window links" desc "This addon that transform all the links in a document in order to open in a new window" vers "0.6" date "2012-03-15" author-name "Surname Name" author-url "http://www.domain.com" author-email "admin@domain.com"_