package com.delaware.widgets.client.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * The HTMLWidget class represent widget which allow insert widgets in HTML
 * template.
 * 
 * @author Taras Petrytsyn
 * 
 * @version 1.0 September 2007
 * 
 */

public class HTMLTemplateWidget extends HTML {

    private Map widgets = new HashMap();
    
    private int enabled = 0;

    private boolean callback = false;

    /**
     * Creates a HTMLWidget with HTML from the given HTML file
     * 
     * @param htmlContextFileName
     *            name of HTML file with context without URL prefix of the
     *            module
     * 
     */

    public HTMLTemplateWidget(String htmlContextFileName) {
        super();
        callback = false;
        String url = GWT.getModuleBaseURL() + htmlContextFileName;

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);

        try {
            Request response = builder.sendRequest(Long.toString(System
                    .currentTimeMillis()), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    System.out.println(exception.getMessage());
                    callback = false;
                }

                public void onResponseReceived(Request request,
                        Response response) {
                    setHTML(response.getText());
                    if (enabled >= 0) {
                        try {
                            generateWidgets();
                        } catch (ElementNotFound e) {
                            onError(request, e);
                        }
                    }
                    callback = true;
                }
            });
        } catch (RequestException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds the <code>widget</code> with given <code>id</code> to the
     * HTMLWidget widgets list.
     * 
     * @param id
     *            the <code>id</code> of widget being added.
     * @param widget
     *            the <code>widget</code> being added.
     */

    public boolean addWidget(String id, Widget widget) {

        widgets.put(id, widget);
        if (enabled >= 0 && callback) {
            ElementTO element;
            try {
                element = removeChildElement(id);
                AttachWidget attachWidget = null;
                
                if (!widget.isAttached()) {
                    attachWidget = new AttachWidget(widget);
                }else {
                    attachWidget = (AttachWidget) widget.getParent();
                }
                addChildElement(element, attachWidget);
            } catch (ElementNotFound e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the widget with given <code>id</code> from the HTMLWidget
     * widgets list.
     * 
     * @param id
     *            the <code>id</code> of widget being removed.
     * @throws ElementNotFound
     */

    public void removeWidget(String id) throws ElementNotFound {

        widgets.remove(id);

        if (enabled >= 0 && callback) {
            removeChildElement(id);
        }
    }

    /**
     * Disable updating of widget's HTML on page when <code>addWidget()</code>,
     * <code>removeWidget()</code> and <code>replaceWidget()</code> methods
     * calls. <br>
     * <br>
     * <b>Note:</b> disabling is incremental, and if you want enable updating,
     * you must call <code>enableUpdate()</code> method at least the same
     * times as <code>disableUpdate()</code> was call.
     * 
     */

    public void disableUpdate() {
        enabled--;
    }

    /**
     * Enable updating of widget's HTML on page when <code>addWidget()</code>,
     * <code>removeWidget()</code> and <code>replaceWidget()</code> methods
     * calls. <br>
     * <br>
     * <b>Note:</b> enabling is incremental, and if you want disable updating,
     * you must call <code>disableUpdate()</code> method at least the same
     * times as <code>enableUpdate()</code> was call.
     * 
     * @throws ElementNotFound
     * 
     */

    public void enabledUpdate() throws ElementNotFound {
        enabled++;
        if (enabled == 0 && callback) {
            generateWidgets();
        }
    }

    private void generateWidgets() throws ElementNotFound {

        for (Iterator it = widgets.keySet().iterator(); it.hasNext();) {

            String id = it.next().toString();

            Widget widget = (Widget) widgets.get(id);

            ElementTO element = removeChildElement(id);
            
            AttachWidget attachWidget = null;
            
            if (!widget.isAttached()) {
                attachWidget = new AttachWidget(widget);
            }else {
                attachWidget = (AttachWidget) widget.getParent();
            }
            
            addChildElement(element, attachWidget);
        }
    }

    private Element findElementById(Element element, String id) {
        Element result = null;
        if (element != null) {
            String currentId = DOM.getElementProperty(element, "id");
            if (id.equals(currentId)) {
                return element;
            }
            int count = DOM.getChildCount(element);
            for (int i = 0; i < count && result == null; i++) {
                Element child = DOM.getChild(element, i);
                result = findElementById(child, id);
            }
        }
        return result;
    }

    private ElementTO removeChildElement(String id) throws ElementNotFound {
        ElementTO element = new ElementTO();
        element.setId(id);

        Element child = findElementById(this.getElement(), id);
        if (child != null) {
            Element parent = DOM.getParent(child);

            element.setParent(parent);
            String style = DOM.getStyleAttribute(child, "cssText");
            if (style != null && style.length() > 0) {
                element.setStyle(style);
            }

            String className = DOM.getElementAttribute(child, "className");
            if (className != null && className.length() > 0) {
                element.setClassName(className);
            }

            element.setIndex(DOM.getChildIndex(parent, child));
            DOM.removeChild(parent, child);
        } else {
            throw new ElementNotFound(
                    "Could not find element with specified id: " + id);
        }
        return element;
    }

    private void addChildElement(ElementTO element, Widget widget) {
        Element child = widget.getElement();
        DOM.setElementAttribute(child, "id", element.getId());
        String style = element.getStyle();
        String className = element.getClassName();
        if (style != null && style.length() > 0) {
            DOM.setStyleAttribute(child, "cssText", style);
        }
        if (className != null && className.length() > 0) {
            DOM.setElementAttribute(child, "className", className);
        }
        DOM.insertChild(element.getParent(), child, element.getIndex());
    }

    class AttachWidget extends Composite {
        public AttachWidget(Widget widget) {
            initWidget(widget);
            onAttach();
        }
    }

}
