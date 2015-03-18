# Description #
If you're an expirienced GWT developers you may be confronted with difficulties conserned with writting a lot of complicated Java code to design your pages.

HTMLTmplateWidget allows to use HTML files for designing.

# Example #
To use HTMLTemplate widget you have to download gdwidgets.jar file, add it to your classpath and import in you GWT project by writting this line:

```
<inherits name='com.delaware.widgets.GDWidgets'/>
```

in your **.gwt.xml file.**

Now you can create HTMLTemplateWidget by this way:

```
final HTMLTemplateWidget templateWidget = new HTMLTemplateWidget("test.html");
```

as parameter in constructor you pass path to HTML file and its name. HTML files storage root is your GWT public folder, where you keep your resources.

Example of HTML file:

```
Enter your name: <input  type="text" id="helloworld"></input>
```

for simple I create only one input tag and set id attribute value - "helloworld", this is very important step, because later we will use this id to set in this place TextBox widget.

In your GWT module class write this:
```
	public void onModuleLoad() {
		final TextBox textBox = new TextBox ();
		final HTMLTemplateWidget templateWidget = new HTMLTemplateWidget(
				"test.html");
		templateWidget.addWidget("helloworld", textBox );
		RootPanel.get().add(templateWidget);
	}
```

templateWidget.addWidget("helloworld", textBox ); - this row add your widget to html file in place of tag with id "helloworld".

and now you can run your project.

Admite, that now your can create you design in HTML, or even give this task to your html designer, and then only specify id attributes to tags that must be replaced by HTMLTemplateWidget.

With HTMLTemplateWidget you can write inline css, or styles directly in you tags in HTML file and keep your Java code clear from design. Now your JAVA code is using only for defining controls and its behavior.

You can use any number of HTMLTemplateWidgets in your project and you can put on HTMLTemplateWidget in another, and easy make complicated design. And any redesigning now is much more simpler you have to modify only html files but not JAVA classes

Please read more about HTMLTemplateWidget in links to topics in Google groups

http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/47d5db9846268136

http://groups.google.com/group/Google-Web-Toolkit-Contributors/browse_thread/thread/ae0a4886a4f6f958