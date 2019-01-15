/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.widgets;


import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.gtk.*;

/**
 * This class is the abstract superclass of all user interface objects.
 * Widgets are created, disposed and issue notification to listeners
 * when events occur which affect them.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Dispose</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation. However, it has not been marked
 * final to allow those outside of the SWT development team to implement
 * patched versions of the class in order to get around specific
 * limitations in advance of when those limitations can be addressed
 * by the team.  Any class built using subclassing to access the internals
 * of this class will likely fail to compile or run between releases and
 * may be strongly platform specific. Subclassing should not be attempted
 * without an intimate and detailed understanding of the workings of the
 * hierarchy. No support is provided for user-written classes which are
 * implemented as subclasses of this class.
 * </p>
 *
 * @see #checkSubclass
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 */
public abstract class Widget {
	/**
	 * the handle to the OS resource
	 * (Warning: This field is platform dependent)
	 * <p>
	 * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT
	 * public API. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms and should never be accessed from application code.
	 * </p>
	 *
	 * @noreference This field is not intended to be referenced by clients.
	 */
	public long /*int*/ handle;
	int style, state;
	Display display;
	EventTable eventTable;
	Object data;

	/* Global state flags
	 *
	 * Common code pattern:
	 * & - think of AND as removing.
	 * | - think of OR as adding.
	 * state & ~flag  -- Think as "removing flag"
	 * state |  flag  -- Think as "adding flag"
	 *
	 * state |= flag  -- Flag is being added to state.
	 * state &= ~flag -- Flag is being removed from state.
	 * state & flag != 0 -- true if flag is present (think >0 = true)
	 * state & flag == 0 -- true if flag is absent  (think 0 = false)
	 *
	 * (state & (flag1 | flag2)) != 0 -- true if either of the flags are present.
	 * (state & (flag1 | flag2)) == 0 -- true if both flag1 & flag2 are absent.
	 */
	static final int DISPOSED = 1<<0;
	static final int CANVAS = 1<<1;
	static final int KEYED_DATA = 1<<2;
	static final int HANDLE = 1<<3;
	static final int DISABLED = 1<<4;
	static final int MENU = 1<<5;
	static final int OBSCURED = 1<<6;
	static final int MOVED = 1<<7;
	static final int RESIZED = 1<<8;
	static final int ZERO_WIDTH = 1<<9;
	static final int ZERO_HEIGHT = 1<<10;
	static final int HIDDEN = 1<<11;
	static final int FOREGROUND = 1<<12;
	static final int BACKGROUND = 1<<13;
	static final int FONT = 1<<14;
	static final int PARENT_BACKGROUND = 1<<15;
	static final int THEME_BACKGROUND = 1<<16;

	/* A layout was requested on this widget */
	static final int LAYOUT_NEEDED	= 1<<17;

	/* The preferred size of a child has changed */
	static final int LAYOUT_CHANGED = 1<<18;

	/* A layout was requested in this widget hierachy */
	static final int LAYOUT_CHILD = 1<<19;

	/* More global state flags */
	static final int RELEASED = 1<<20;
	static final int DISPOSE_SENT = 1<<21;
	static final int FOREIGN_HANDLE = 1<<22;
	static final int DRAG_DETECT = 1<<23;

	/* Notify of the opportunity to skin this widget */
	static final int SKIN_NEEDED = 1<<24;

	/* Should sub-windows be checked when EnterNotify received */
	static final int CHECK_SUBWINDOW = 1<<25;

	/* Bidi "auto" text direction */
	static final int HAS_AUTO_DIRECTION = 0;

	/* Bidi flag and for auto text direction */
	static final int AUTO_TEXT_DIRECTION = SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;

	/* Default size for widgets */
	static final int DEFAULT_WIDTH	= 64;
	static final int DEFAULT_HEIGHT	= 64;

	/* GTK signals data */
	static final int ACTIVATE = 1;
	static final int BUTTON_PRESS_EVENT = 2;
	static final int BUTTON_PRESS_EVENT_INVERSE = 3;
	static final int BUTTON_RELEASE_EVENT = 4;
	static final int BUTTON_RELEASE_EVENT_INVERSE = 5;
	static final int CHANGED = 6;
	static final int CHANGE_VALUE = 7;
	static final int CLICKED = 8;
	static final int COMMIT = 9;
	static final int CONFIGURE_EVENT = 10;
	static final int DELETE_EVENT = 11;
	static final int DELETE_RANGE = 12;
	static final int DELETE_TEXT = 13;
	static final int ENTER_NOTIFY_EVENT = 14;
	static final int EVENT = 15;
	static final int EVENT_AFTER = 16;
	static final int EXPAND_COLLAPSE_CURSOR_ROW = 17;
	static final int EXPOSE_EVENT = 18;
	static final int DRAW = EXPOSE_EVENT;
	static final int EXPOSE_EVENT_INVERSE = 19;
	static final int FOCUS = 20;
	static final int FOCUS_IN_EVENT = 21;
	static final int FOCUS_OUT_EVENT = 22;
	static final int GRAB_FOCUS = 23;
	static final int HIDE = 24;
	static final int INPUT = 25;
	static final int INSERT_TEXT = 26;
	static final int KEY_PRESS_EVENT = 27;
	static final int KEY_RELEASE_EVENT = 28;
	static final int LEAVE_NOTIFY_EVENT = 29;
	static final int MAP = 30;
	static final int MAP_EVENT = 31;
	static final int MNEMONIC_ACTIVATE = 32;
	static final int MOTION_NOTIFY_EVENT = 33;
	static final int MOTION_NOTIFY_EVENT_INVERSE = 34;
	static final int MOVE_FOCUS = 35;
	static final int OUTPUT = 36;
	static final int POPULATE_POPUP = 37;
	static final int POPUP_MENU = 38;
	static final int PREEDIT_CHANGED = 39;
	static final int REALIZE = 40;
	static final int ROW_ACTIVATED = 41;
	static final int SCROLL_CHILD = 42;
	static final int SCROLL_EVENT = 43;
	static final int SELECT = 44;
	static final int SHOW = 45;
	static final int SHOW_HELP = 46;
	static final int SIZE_ALLOCATE = 47;
	static final int STYLE_SET = 48;
	static final int SWITCH_PAGE = 49;
	static final int TEST_COLLAPSE_ROW = 50;
	static final int TEST_EXPAND_ROW = 51;
	static final int TEXT_BUFFER_INSERT_TEXT = 52;
	static final int TOGGLED = 53;
	static final int UNMAP = 54;
	static final int UNMAP_EVENT = 55;
	static final int UNREALIZE = 56;
	static final int VALUE_CHANGED = 57;
	static final int WINDOW_STATE_EVENT = 59;
	static final int ACTIVATE_INVERSE = 60;
	static final int DAY_SELECTED = 61;
	static final int MONTH_CHANGED = 62;
	static final int STATUS_ICON_POPUP_MENU = 63;
	static final int ROW_INSERTED = 64;
	static final int ROW_DELETED = 65;
	static final int DAY_SELECTED_DOUBLE_CLICK = 66;
	static final int ICON_RELEASE = 67;
	static final int SELECTION_DONE = 68;
	static final int START_INTERACTIVE_SEARCH = 69;
	static final int BACKSPACE = 70;
	static final int BACKSPACE_INVERSE = 71;
	static final int COPY_CLIPBOARD = 72;
	static final int COPY_CLIPBOARD_INVERSE = 73;
	static final int CUT_CLIPBOARD = 74;
	static final int CUT_CLIPBOARD_INVERSE = 75;
	static final int PASTE_CLIPBOARD = 76;
	static final int PASTE_CLIPBOARD_INVERSE = 77;
	static final int DELETE_FROM_CURSOR = 78;
	static final int DELETE_FROM_CURSOR_INVERSE = 79;
	static final int MOVE_CURSOR = 80;
	static final int MOVE_CURSOR_INVERSE = 81;
	static final int DIRECTION_CHANGED = 82;
	static final int CREATE_MENU_PROXY = 83;
	static final int ROW_HAS_CHILD_TOGGLED = 84;
	static final int POPPED_UP = 85;
	static final int FOCUS_IN = 86;
	static final int FOCUS_OUT = 87;
	static final int IM_UPDATE = 88;
	static final int KEY_PRESSED = 89;
	static final int KEY_RELEASED = 90;
	static final int DECELERATE = 91;
	static final int SCROLL = 92;
	static final int SCROLL_BEGIN = 93;
	static final int SCROLL_END = 94;
	static final int ENTER = 95;
	static final int LEAVE = 96;
	static final int MOTION = 97;
	static final int CLOSE_REQUEST = 98;
	static final int LAST_SIGNAL = 99;

	static final String IS_ACTIVE = "org.eclipse.swt.internal.control.isactive"; //$NON-NLS-1$
	static final String KEY_CHECK_SUBWINDOW = "org.eclipse.swt.internal.control.checksubwindow"; //$NON-NLS-1$
	static final String KEY_GTK_CSS = "org.eclipse.swt.internal.gtk.css"; //$NON-NLS-1$

	static Callback gdkSeatGrabPrepareFunc;

/**
 * Prevents uninitialized instances from being created outside the package.
 */
Widget () {}

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a widget which will be the parent of the new instance (cannot be null)
 * @param style the style of widget to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT
 * @see #checkSubclass
 * @see #getStyle
 */
public Widget (Widget parent, int style) {
	checkSubclass ();
	checkParent (parent);
	this.style = style;
	display = parent.display;
	reskinWidget ();
}

void _addListener (int eventType, Listener listener) {
	if (eventTable == null) eventTable = new EventTable ();
	eventTable.hook (eventType, listener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when an event of the given type occurs. When the
 * event does occur in the widget, the listener is notified by
 * sending it the <code>handleEvent()</code> message. The event
 * type is one of the event constants defined in class <code>SWT</code>.
 *
 * @param eventType the type of event to listen for
 * @param listener the listener which should be notified when the event occurs
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Listener
 * @see SWT
 * @see #getListeners(int)
 * @see #removeListener(int, Listener)
 * @see #notifyListeners
 */
public void addListener (int eventType, Listener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	_addListener (eventType, listener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the widget is disposed. When the widget is
 * disposed, the listener is notified by sending it the
 * <code>widgetDisposed()</code> message.
 *
 * @param listener the listener which should be notified when the receiver is disposed
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see DisposeListener
 * @see #removeDisposeListener
 */
public void addDisposeListener (DisposeListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Dispose, typedListener);
}

long /*int*/ paintWindow () {
	return 0;
}

long /*int*/ paintSurface () {
	return 0;
}

long /*int*/ cssHandle() {
	return handle;
}

static int checkBits (int style, int int0, int int1, int int2, int int3, int int4, int int5) {
	int mask = int0 | int1 | int2 | int3 | int4 | int5;
	if ((style & mask) == 0) style |= int0;
	if ((style & int0) != 0) style = (style & ~mask) | int0;
	if ((style & int1) != 0) style = (style & ~mask) | int1;
	if ((style & int2) != 0) style = (style & ~mask) | int2;
	if ((style & int3) != 0) style = (style & ~mask) | int3;
	if ((style & int4) != 0) style = (style & ~mask) | int4;
	if ((style & int5) != 0) style = (style & ~mask) | int5;
	return style;
}

long /*int*/ cellDataProc (long /*int*/ tree_column, long /*int*/ cell, long /*int*/ tree_model, long /*int*/ iter, long /*int*/ data) {
	return 0;
}

void checkOpen () {
	/* Do nothing */
}

void checkOrientation (Widget parent) {
	style &= ~SWT.MIRRORED;
	if ((style & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT)) == 0) {
		if (parent != null) {
			if ((parent.style & SWT.LEFT_TO_RIGHT) != 0) style |= SWT.LEFT_TO_RIGHT;
			if ((parent.style & SWT.RIGHT_TO_LEFT) != 0) style |= SWT.RIGHT_TO_LEFT;
		}
	}
	style = checkBits (style, SWT.LEFT_TO_RIGHT, SWT.RIGHT_TO_LEFT, 0, 0, 0, 0);
}

/**
 * Throws an exception if the specified widget can not be
 * used as a parent for the receiver.
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 */
void checkParent (Widget parent) {
	if (parent == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (parent.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	parent.checkWidget ();
	parent.checkOpen ();
}

/**
 * Checks that this class can be subclassed.
 * <p>
 * The SWT class library is intended to be subclassed
 * only at specific, controlled points (most notably,
 * <code>Composite</code> and <code>Canvas</code> when
 * implementing new widgets). This method enforces this
 * rule unless it is overridden.
 * </p><p>
 * <em>IMPORTANT:</em> By providing an implementation of this
 * method that allows a subclass of a class which does not
 * normally allow subclassing to be created, the implementer
 * agrees to be fully responsible for the fact that any such
 * subclass will likely fail between SWT releases and will be
 * strongly platform specific. No support is provided for
 * user-written classes which are implemented in this fashion.
 * </p><p>
 * The ability to subclass outside of the allowed SWT classes
 * is intended purely to enable those not on the SWT development
 * team to implement patches in order to get around specific
 * limitations in advance of when those limitations can be
 * addressed by the team. Subclassing should not be attempted
 * without an intimate and detailed understanding of the hierarchy.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 */
protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

/**
 * Throws an <code>SWTException</code> if the receiver can not
 * be accessed by the caller. This may include both checks on
 * the state of the receiver and more generally on the entire
 * execution context. This method <em>should</em> be called by
 * widget implementors to enforce the standard SWT invariants.
 * <p>
 * Currently, it is an error to invoke any method (other than
 * <code>isDisposed()</code>) on a widget that has had its
 * <code>dispose()</code> method called. It is also an error
 * to call widget methods from any thread that is different
 * from the thread that created the widget.
 * </p><p>
 * In future releases of SWT, there may be more or fewer error
 * checks and exceptions may be thrown for different reasons.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
protected void checkWidget () {
	Display display = this.display;
	if (display == null) error (SWT.ERROR_WIDGET_DISPOSED);
	if (display.thread != Thread.currentThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if ((state & DISPOSED) != 0) error (SWT.ERROR_WIDGET_DISPOSED);
}

void createHandle (int index) {
}

void createWidget (int index) {
	createHandle (index);
	setOrientation (true);
	hookEvents ();
	register ();
}

void deregister () {
	if (handle == 0) return;
	if ((state & HANDLE) != 0) display.removeWidget (handle);
}

void destroyWidget () {
	long /*int*/ topHandle = topHandle ();
	releaseHandle ();
	if (topHandle != 0 && (state & HANDLE) != 0) {
		GTK.gtk_widget_destroy (topHandle);
	}
}

/**
 * Disposes of the operating system resources associated with
 * the receiver and all its descendants. After this method has
 * been invoked, the receiver and all descendants will answer
 * <code>true</code> when sent the message <code>isDisposed()</code>.
 * Any internal connections between the widgets in the tree will
 * have been removed to facilitate garbage collection.
 * This method does nothing if the widget is already disposed.
 * <p>
 * NOTE: This method is not called recursively on the descendants
 * of the receiver. This means that, widget implementers can not
 * detect when a widget is being disposed of by re-implementing
 * this method, but should instead listen for the <code>Dispose</code>
 * event.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #addDisposeListener
 * @see #removeDisposeListener
 * @see #checkWidget
 */
public void dispose () {
	/*
	* Note:  It is valid to attempt to dispose a widget
	* more than once.  If this happens, fail silently.
	*/
	if (isDisposed ()) return;
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	release (true);
}

void error (int code) {
	SWT.error (code);
}

/**
 * Returns the application defined widget data associated
 * with the receiver, or null if it has not been set. The
 * <em>widget data</em> is a single, unnamed field that is
 * stored with every widget.
 * <p>
 * Applications may put arbitrary objects in this field. If
 * the object stored in the widget data needs to be notified
 * when the widget is disposed of, it is the application's
 * responsibility to hook the Dispose event on the widget and
 * do so.
 * </p>
 *
 * @return the widget data
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - when the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - when called from the wrong thread</li>
 * </ul>
 *
 * @see #setData(Object)
 */
public Object getData () {
	checkWidget();
	return (state & KEYED_DATA) != 0 ? ((Object []) data) [0] : data;
}
/**
 * Returns the application defined property of the receiver
 * with the specified name, or null if it has not been set.
 * <p>
 * Applications may have associated arbitrary objects with the
 * receiver in this fashion. If the objects stored in the
 * properties need to be notified when the widget is disposed
 * of, it is the application's responsibility to hook the
 * Dispose event on the widget and do so.
 * </p>
 *
 * @param	key the name of the property
 * @return the value of the property or null if it has not been set
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setData(String, Object)
 */
public Object getData (String key) {
	checkWidget();
	if (key == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (key.equals (KEY_CHECK_SUBWINDOW)) {
		return new Boolean ((state & CHECK_SUBWINDOW) != 0);
	}
	if (key.equals(IS_ACTIVE)) return Boolean.valueOf(isActive ());
	if ((state & KEYED_DATA) != 0) {
		Object [] table = (Object []) data;
		for (int i=1; i<table.length; i+=2) {
			if (key.equals (table [i])) return table [i+1];
		}
	}
	return null;
}

/**
 * Returns the <code>Display</code> that is associated with
 * the receiver.
 * <p>
 * A widget's display is either provided when it is created
 * (for example, top level <code>Shell</code>s) or is the
 * same as its parent's display.
 * </p>
 *
 * @return the receiver's display
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Display getDisplay () {
	Display display = this.display;
	if (display == null) error (SWT.ERROR_WIDGET_DISPOSED);
	return display;
}

/**
 * Returns an array of listeners who will be notified when an event
 * of the given type occurs. The event type is one of the event constants
 * defined in class <code>SWT</code>.
 *
 * @param eventType the type of event to listen for
 * @return an array of listeners that will be notified when the event occurs
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Listener
 * @see SWT
 * @see #addListener(int, Listener)
 * @see #removeListener(int, Listener)
 * @see #notifyListeners
 *
 * @since 3.4
 */
public Listener[] getListeners (int eventType) {
	checkWidget();
	if (eventTable == null) return new Listener[0];
	return eventTable.getListeners(eventType);
}

String getName () {
//	String string = getClass ().getName ();
//	int index = string.lastIndexOf ('.');
//	if (index == -1) return string;
	String string = getClass ().getName ();
	int index = string.length ();
	while ((--index > 0) && (string.charAt (index) != '.')) {}
	return string.substring (index + 1, string.length ());
}

String getNameText () {
	return "";
}

/**
 * Returns the receiver's style information.
 * <p>
 * Note that the value which is returned by this method <em>may
 * not match</em> the value which was provided to the constructor
 * when the receiver was created. This can occur when the underlying
 * operating system does not support a particular combination of
 * requested styles. For example, if the platform widget used to
 * implement a particular SWT widget always has scroll bars, the
 * result of calling this method would always have the
 * <code>SWT.H_SCROLL</code> and <code>SWT.V_SCROLL</code> bits set.
 * </p>
 *
 * @return the style bits
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getStyle () {
	checkWidget ();
	return style;
}


long /*int*/ gtk_activate (long /*int*/ widget) {
	return 0;
}

void gtk_adjustment_get (long /*int*/ hAdjustment, GtkAdjustment adjustment) {
	adjustment.lower = GTK.gtk_adjustment_get_lower (hAdjustment);
	adjustment.upper = GTK.gtk_adjustment_get_upper (hAdjustment);
	adjustment.page_increment = GTK.gtk_adjustment_get_page_increment (hAdjustment);
	adjustment.step_increment = GTK.gtk_adjustment_get_step_increment (hAdjustment);
	adjustment.page_size = GTK.gtk_adjustment_get_page_size (hAdjustment);
	adjustment.value = GTK.gtk_adjustment_get_value (hAdjustment);
}

long /*int*/ gtk_button_press_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_button_release_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_changed (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_change_value (long /*int*/ widget, long /*int*/ scroll, long /*int*/ value1, long /*int*/ value2) {
	return 0;
}

long /*int*/ gtk_clicked (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_close_request (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_commit (long /*int*/ imcontext, long /*int*/ text) {
	return 0;
}

long /*int*/ gtk_configure_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_create_menu_proxy (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_day_selected (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_day_selected_double_click (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_delete_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_delete_range (long /*int*/ widget, long /*int*/ iter1, long /*int*/ iter2) {
	return 0;
}

long /*int*/ gtk_delete_text (long /*int*/ widget, long /*int*/ start_pos, long /*int*/ end_pos) {
	return 0;
}

long /*int*/ gtk_enter_notify_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_event_after (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_expand_collapse_cursor_row (long /*int*/ widget, long /*int*/ logical, long /*int*/ expand, long /*int*/ open_all) {
	return 0;
}

long /*int*/ gtk_draw (long /*int*/ widget, long /*int*/ cairo) {
	return 0;
}

long /*int*/ gtk_focus (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_focus_in_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_focus_out_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_grab_focus (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_hide (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_icon_release (long /*int*/ widget, long /*int*/ icon_pos, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_input (long /*int*/ widget, long /*int*/ arg1) {
	return 0;
}

long /*int*/ gtk_insert_text (long /*int*/ widget, long /*int*/ new_text, long /*int*/ new_text_length, long /*int*/ position) {
	return 0;
}

long /*int*/ gtk_key_press_event (long /*int*/ widget, long /*int*/ event) {
	return sendKeyEvent (SWT.KeyDown, event) ? 0 : 1;
}

long /*int*/ gtk_key_release_event (long /*int*/ widget, long /*int*/ event) {
	return sendKeyEvent (SWT.KeyUp, event) ? 0 : 1;
}

long /*int*/ gtk_leave_notify_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_map (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_map_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

/**
 * <p>GTK3.22+ has API which allows clients of GTK to connect a menu to the "popped-up" signal.
 * This callback is triggered after the menu is popped up/shown to the user, and provides
 * information about the actual position and size of the menu, as shown to the user.</p>
 *
 * <p>SWT clients can enable this functionality by launching their application with the
 * SWT_MENU_LOCATION_DEBUGGING environment variable set to 1. If enabled, the previously mentioned
 * positioning and size information will be printed to the console. The information comes from GTK
 * internals and is stored in the method parameters.</p>
 *
 * @param widget the memory address of the menu which was popped up
 * @param flipped_rect a pointer to the GdkRectangle containing the flipped location and size of the menu
 * @param final_rect a pointer to the GdkRectangle containing the final (after all internal adjustments)
 * location and size of the menu
 * @param flipped_x a boolean flag indicating whether the menu has been inverted along the X-axis
 * @param flipped_y a boolean flag indicating whether the menu has been inverted along the Y-axis
 */
long /*int*/ gtk_menu_popped_up (long /*int*/ widget, long /*int*/ flipped_rect, long /*int*/ final_rect, long /*int*/ flipped_x, long /*int*/ flipped_y) {
	return 0;
}

long /*int*/ gtk_mnemonic_activate (long /*int*/ widget, long /*int*/ arg1) {
	return 0;
}

long /*int*/ gtk_month_changed (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_motion_notify_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_move_focus (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_output (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_populate_popup (long /*int*/ widget, long /*int*/ menu) {
	return 0;
}

long /*int*/ gtk_popup_menu (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_preedit_changed (long /*int*/ imcontext) {
	return 0;
}

long /*int*/ gtk_realize (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_row_activated (long /*int*/ tree, long /*int*/ path, long /*int*/ column) {
	return 0;
	// Note on SWT Tree/Table/List. This signal is no longer used for sending events, instead
	// Send DefaultSelection is manually emitted. We use this function to know whether a
	// 'row-activated' is triggered. See Bug 312568, 518414.
}

long /*int*/ gtk_row_deleted (long /*int*/ model, long /*int*/ path) {
	return 0;
}

long /*int*/ gtk_row_inserted (long /*int*/ model, long /*int*/ path, long /*int*/ iter) {
	return 0;
}

long /*int*/ gtk_row_has_child_toggled (long /*int*/ model, long /*int*/ path, long /*int*/ iter) {
	return 0;
}

long /*int*/ gtk_scroll_child (long /*int*/ widget, long /*int*/ scrollType, long /*int*/ horizontal) {
	return 0;
}

long /*int*/ gtk_scroll_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_select (long /*int*/ item) {
	return 0;
}

long /*int*/ gtk_selection_done (long /*int*/ menushell) {
	return 0;
}

long /*int*/ gtk_show (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_show_help (long /*int*/ widget, long /*int*/ helpType) {
	return 0;
}

long /*int*/ gtk_size_allocate (long /*int*/ widget, long /*int*/ allocation) {
	return 0;
}

long /*int*/ gtk_status_icon_popup_menu (long /*int*/ handle, long /*int*/ button, long /*int*/ activate_time) {
	return 0;
}

long /*int*/ gtk_start_interactive_search (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_style_set (long /*int*/ widget, long /*int*/ previousStyle) {
	return 0;
}

long /*int*/ gtk_switch_page (long /*int*/ widget, long /*int*/ page, long /*int*/ page_num) {
	return 0;
}

long /*int*/ gtk_test_collapse_row (long /*int*/ tree, long /*int*/ iter, long /*int*/ path) {
	return 0;
}

long /*int*/ gtk_test_expand_row (long /*int*/ tree, long /*int*/ iter, long /*int*/ path) {
	return 0;
}

long /*int*/ gtk_text_buffer_insert_text (long /*int*/ widget, long /*int*/ iter, long /*int*/ text, long /*int*/ length) {
	return 0;
}

long /*int*/ gtk_timer () {
	return 0;
}

long /*int*/ gtk_toggled (long /*int*/ renderer, long /*int*/ pathStr) {
	return 0;
}

/*
 * Bug 498165: gtk_tree_view_column_cell_get_position() sets off rendererGetPreferredWidthCallback in GTK3 which is an issue
 * if there is an ongoing MeasureEvent listener. Disabling it and re-enabling the callback after the method is called
 * prevents a stack overflow from occurring.
 */
boolean gtk_tree_view_column_cell_get_position (long /*int*/ column, long /*int*/ cell_renderer, int[] start_pos, int[] width) {
	Callback.setEnabled(false);
	boolean result = GTK.gtk_tree_view_column_cell_get_position (column, cell_renderer, start_pos, width);
	Callback.setEnabled(true);
	return result;
}

long /*int*/ gtk_unmap (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_unmap_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

long /*int*/ gtk_unrealize (long /*int*/ widget) {
	return 0;
}

long /*int*/ gtk_value_changed (long /*int*/ adjustment) {
	return 0;
}

long /*int*/ gtk_window_state_event (long /*int*/ widget, long /*int*/ event) {
	return 0;
}

int fontHeight (long /*int*/ font, long /*int*/ widgetHandle) {
	long /*int*/ context = GTK.gtk_widget_get_pango_context (widgetHandle);
	long /*int*/ lang = OS.pango_context_get_language (context);
	long /*int*/ metrics = OS.pango_context_get_metrics (context, font, lang);
	int ascent = OS.pango_font_metrics_get_ascent (metrics);
	int descent = OS.pango_font_metrics_get_descent (metrics);
	OS.pango_font_metrics_unref (metrics);
	return OS.PANGO_PIXELS (ascent + descent);
}

long /*int*/ filterProc(long /*int*/ xEvent, long /*int*/ gdkEvent, long /*int*/ data2) {
	return 0;
}

boolean filters (int eventType) {
	return display.filters (eventType);
}

char [] fixMnemonic (String string) {
	return fixMnemonic (string, true);
}

char [] fixMnemonic (String string, boolean replace) {
	return fixMnemonic (string, replace, false);
}

char [] fixMnemonic (String string, boolean replace, boolean removeAppended) {
	int length = string.length ();
	char [] text = new char [length];
	string.getChars (0, length, text, 0);
	int i = 0, j = 0;
	char [] result = new char [length * 2];
	while (i < length) {
		switch (text [i]) {
			case '&':
				if (i + 1 < length && text [i + 1] == '&') {
					result [j++] = text [i++];
				} else {
					if (replace) result [j++] = '_';
				}
				i++;
				break;
				/*
				 * In Japanese like languages where mnemonics are not taken from the
				 * source label text but appended in parentheses like "(&M)" at end. In order to
				 * allow the reuse of such label text as a tool-tip text as well, "(&M)" like
				 * character sequence has to be removed from the end of CJK-style mnemonics.
				 */
			case '(':
				if (removeAppended && i + 4 == string.length () && text [i + 1] == '&' && text [i + 3] == ')') {
					if (replace) result [j++] = ' ';
					i += 4;
					break; // break switch case only if we are removing the mnemonic otherwise fall through
				}
			case '_':
				if (replace) result [j++] = '_';
				//FALL THROUGH
			default:
				result [j++] = text [i++];
		}
	}
	return result;
}

boolean isActive () {
	return true;
}

/**
 * Returns <code>true</code> if the widget has auto text direction,
 * and <code>false</code> otherwise.
 *
 * @return <code>true</code> when the widget has auto direction and <code>false</code> otherwise
 *
 * @see SWT#AUTO_TEXT_DIRECTION
 *
 * @since 3.105
 */
public boolean isAutoDirection () {
	return false;
}

/**
 * Returns <code>true</code> if the widget has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the widget.
 * When a widget has been disposed, it is an error to
 * invoke any other method (except {@link #dispose()}) using the widget.
 * </p>
 *
 * @return <code>true</code> when the widget is disposed and <code>false</code> otherwise
 */
public boolean isDisposed () {
	return (state & DISPOSED) != 0;
}

/**
 * Returns <code>true</code> if there are any listeners
 * for the specified event type associated with the receiver,
 * and <code>false</code> otherwise. The event type is one of
 * the event constants defined in class <code>SWT</code>.
 *
 * @param eventType the type of event
 * @return true if the event is hooked
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT
 */
public boolean isListening (int eventType) {
	checkWidget ();
	return hooks (eventType);
}

boolean isValidThread () {
	return getDisplay ().isValidThread ();
}

boolean isValidSubclass() {
	return Display.isValidClass(getClass());
}

void hookEvents () {
}

/*
 * Returns <code>true</code> if the specified eventType is
 * hooked, and <code>false</code> otherwise. Implementations
 * of SWT can avoid creating objects and sending events
 * when an event happens in the operating system but
 * there are no listeners hooked for the event.
 *
 * @param eventType the event to be checked
 *
 * @return <code>true</code> when the eventType is hooked and <code>false</code> otherwise
 *
 * @see #isListening
 */
boolean hooks (int eventType) {
	if (eventTable == null) return false;
	return eventTable.hooks (eventType);
}

long /*int*/ hoverProc (long /*int*/ widget) {
	return 0;
}

boolean mnemonicHit (long /*int*/ mnemonicHandle, char key) {
	if (!mnemonicMatch (mnemonicHandle, key)) return false;
	OS.g_signal_handlers_block_matched (mnemonicHandle, OS.G_SIGNAL_MATCH_DATA, 0, 0, 0, 0, MNEMONIC_ACTIVATE);
	boolean result = GTK.gtk_widget_mnemonic_activate (mnemonicHandle, false);
	OS.g_signal_handlers_unblock_matched (mnemonicHandle, OS.G_SIGNAL_MATCH_DATA, 0, 0, 0, 0, MNEMONIC_ACTIVATE);
	return result;
}

boolean mnemonicMatch (long /*int*/ mnemonicHandle, char key) {
	long keyval1 = GDK.gdk_keyval_to_lower (GDK.gdk_unicode_to_keyval (key));
	long keyval2 = GDK.gdk_keyval_to_lower (GTK.gtk_label_get_mnemonic_keyval (mnemonicHandle));
	return keyval1 == keyval2;
}

/**
 * Notifies all of the receiver's listeners for events
 * of the given type that one such event has occurred by
 * invoking their <code>handleEvent()</code> method.  The
 * event type is one of the event constants defined in class
 * <code>SWT</code>.
 *
 * @param eventType the type of event which has occurred
 * @param event the event data
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SWT
 * @see #addListener
 * @see #getListeners(int)
 * @see #removeListener(int, Listener)
 */
public void notifyListeners (int eventType, Event event) {
	checkWidget();
	if (event == null) event = new Event ();
	sendEvent (eventType, event);
}

void postEvent (int eventType) {
	sendEvent (eventType, null, false);
}

void postEvent (int eventType, Event event) {
	sendEvent (eventType, event, false);
}

void propagateSnapshot (long /*int*/ widget, long /*int*/ snapshot) {
	long /*int*/ list = GTK.gtk_container_get_children (widget);
	long /*int*/ temp = list;
	while (temp != 0) {
		long /*int*/ child = OS.g_list_data (temp);
		if (child != 0) {
			GTK.gtk_widget_snapshot_child(widget, child, snapshot);
		}
		temp = OS.g_list_next (temp);
	}
	OS.g_list_free (list);
}

void register () {
	if (handle == 0) return;
	if ((state & HANDLE) != 0) display.addWidget (handle, this);
}

void release (boolean destroy) {
	if ((state & DISPOSE_SENT) == 0) {
		state |= DISPOSE_SENT;
		sendEvent (SWT.Dispose);
	}
	if ((state & DISPOSED) == 0) {
		releaseChildren (destroy);
	}
	if ((state & RELEASED) == 0) {
		state |= RELEASED;
		if (destroy) {
			releaseParent ();
			releaseWidget ();
			destroyWidget ();
		} else {
			releaseWidget ();
			releaseHandle ();
		}
	}
}

void releaseChildren (boolean destroy) {
}

void releaseHandle () {
	handle = 0;
	state |= DISPOSED;
	display = null;
}

void releaseParent () {
	/* Do nothing */
}

void releaseWidget () {
	deregister ();
	eventTable = null;
	data = null;
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when an event of the given type occurs. The event
 * type is one of the event constants defined in class <code>SWT</code>.
 *
 * @param eventType the type of event to listen for
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Listener
 * @see SWT
 * @see #addListener
 * @see #getListeners(int)
 * @see #notifyListeners
 */
public void removeListener (int eventType, Listener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (eventType, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when an event of the given type occurs.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the SWT
 * public API. It is marked public only so that it can be shared
 * within the packages provided by SWT. It should never be
 * referenced from application code.
 * </p>
 *
 * @param eventType the type of event to listen for
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Listener
 * @see #addListener
 *
 * @noreference This method is not intended to be referenced by clients.
 * @nooverride This method is not intended to be re-implemented or extended by clients.
 */
protected void removeListener (int eventType, SWTEventListener handler) {
	checkWidget ();
	if (handler == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (eventType, handler);
}

long /*int*/ rendererGetPreferredWidthProc (long /*int*/ cell, long /*int*/ handle, long /*int*/ minimun_size, long /*int*/ natural_size) {
	return 0;
}

long /*int*/ rendererRenderProc (long /*int*/ cell, long /*int*/ cr, long /*int*/ handle, long /*int*/ background_area, long /*int*/ cell_area, long /*int*/ flags) {
	return 0;
}

long /*int*/ rendererRenderProc (long /*int*/ cell, long /*int*/ window, long /*int*/ handle, long /*int*/ background_area, long /*int*/ cell_area, long /*int*/ expose_area, long /*int*/ flags) {
	return 0;
}

/**
 * Marks the widget to be skinned.
 * <p>
 * The skin event is sent to the receiver's display when appropriate (usually before the next event
 * is handled). Widgets are automatically marked for skinning upon creation as well as when its skin
 * id or class changes. The skin id and/or class can be changed by calling {@link Display#setData(String, Object)}
 * with the keys {@link SWT#SKIN_ID} and/or {@link SWT#SKIN_CLASS}. Once the skin event is sent to a widget, it
 * will not be sent again unless <code>reskin(int)</code> is called on the widget or on an ancestor
 * while specifying the <code>SWT.ALL</code> flag.
 * </p>
 * <p>
 * The parameter <code>flags</code> may be either:
 * <dl>
 * <dt><b>{@link SWT#ALL}</b></dt>
 * <dd>all children in the receiver's widget tree should be skinned</dd>
 * <dt><b>{@link SWT#NONE}</b></dt>
 * <dd>only the receiver should be skinned</dd>
 * </dl>
 * </p>
 * @param flags the flags specifying how to reskin
 *
 * @exception SWTException
 * <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @since 3.6
 */
public void reskin (int flags) {
	checkWidget ();
	reskinWidget ();
	if ((flags & SWT.ALL) != 0) reskinChildren (flags);
}

void reskinChildren (int flags) {
}

void reskinWidget() {
	if ((state & SKIN_NEEDED) != SKIN_NEEDED) {
		this.state |= SKIN_NEEDED;
		display.addSkinnableWidget(this);
	}
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the widget is disposed.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see DisposeListener
 * @see #addDisposeListener
 */
public void removeDisposeListener (DisposeListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Dispose, listener);
}

void sendEvent (Event event) {
	Display display = event.display;
	if (!display.filterEvent (event)) {
		if (eventTable != null) display.sendEvent(eventTable, event);
	}
}

void sendEvent (int eventType) {
	sendEvent (eventType, null, true);
}

void sendEvent (int eventType, Event event) {
	sendEvent (eventType, event, true);
}

void sendEvent (int eventType, Event event, boolean send) {
	if (eventTable == null && !display.filters (eventType)) {
		return;
	}
	if (event == null) {
		event = new Event();
	}
	event.type = eventType;
	event.display = display;
	event.widget = this;
	if (event.time == 0) {
		event.time = display.getLastEventTime ();
	}
	if (send) {
		sendEvent (event);
	} else {
		display.postEvent (event);
	}
}

boolean sendKeyEvent (int type, long /*int*/ event) {
	int length;
	long /*int*/ string;
	if (GTK.GTK4) {
		long /*int*/ [] eventString = new long /*int*/ [1];
		GDK.gdk_event_get_string(event, eventString);
		string = eventString[0];
		length = (int)/*64*/OS.g_utf16_strlen (string, -1);
	} else {
		GdkEventKey gdkEvent = new GdkEventKey ();
		OS.memmove(gdkEvent, event, GdkEventKey.sizeof);
		length = gdkEvent.length;
		string = gdkEvent.string;
	}
	if (string == 0 || OS.g_utf16_strlen (string, length) <= 1) {
		Event javaEvent = new Event ();
		javaEvent.time = GDK.gdk_event_get_time(event);
		if (!setKeyState (javaEvent, event)) return true;
		sendEvent (type, javaEvent);
		// widget could be disposed at this point

		/*
		* It is possible (but unlikely), that application
		* code could have disposed the widget in the key
		* events.  If this happens, end the processing of
		* the key by returning false.
		*/
		if (isDisposed ()) return false;
		return javaEvent.doit;
	}
	byte [] buffer = new byte [length];
	C.memmove (buffer, string, length);
	char [] chars = Converter.mbcsToWcs (buffer);
	return sendIMKeyEvent (type, event, chars) != null;
}

char [] sendIMKeyEvent (int type, long /*int*/ event, char [] chars) {
	int index = 0, count = 0, state = 0;
	long /*int*/ ptr = 0;
	if (event == 0) {
		ptr = GTK.gtk_get_current_event ();
		if (ptr != 0) {
			int eventType = GDK.gdk_event_get_event_type(ptr);
			switch (eventType) {
				case GDK.GDK_KEY_PRESS:
				case GDK.GDK_KEY_RELEASE:
					int [] eventState = new int[1];
					GDK.gdk_event_get_state(ptr, eventState);
					state = eventState[0];
					break;
				default:
					event = 0;
					break;
			}
		}
	}
	if (event == 0) {
		int [] buffer = new int [1];
		GTK.gtk_get_current_event_state (buffer);
		state = buffer [0];
	}
	while (index < chars.length) {
		Event javaEvent = new Event ();
		if (event != 0 && chars.length <= 1) {
			setKeyState (javaEvent, ptr);
		} else {
			setInputState (javaEvent, state);
		}
		javaEvent.character = chars [index];
		sendEvent (type, javaEvent);

		/*
		* It is possible (but unlikely), that application
		* code could have disposed the widget in the key
		* events.  If this happens, end the processing of
		* the key by returning null.
		*/
		if (isDisposed ()) {
			if (ptr != 0) gdk_event_free (ptr);
			return null;
		}
		if (javaEvent.doit) chars [count++] = chars [index];
		index++;
	}
	if (ptr != 0) gdk_event_free (ptr);
	if (count == 0) return null;
	if (index != count) {
		char [] result = new char [count];
		System.arraycopy (chars, 0, result, 0, count);
		return result;
	}
	return chars;
}

void sendSelectionEvent (int eventType) {
	sendSelectionEvent (eventType, null, false);
}

void sendSelectionEvent (int eventType, Event event, boolean send) {
	if (eventTable == null && !display.filters (eventType)) {
		return;
	}
	if (event == null) event = new Event ();
	long /*int*/ ptr = GTK.gtk_get_current_event ();
	if (ptr != 0) {
		int currentEventType = GDK.gdk_event_get_event_type(ptr);
		switch (currentEventType) {
			case GDK.GDK_KEY_PRESS:
			case GDK.GDK_KEY_RELEASE:
			case GDK.GDK_BUTTON_PRESS:
			case GDK.GDK_2BUTTON_PRESS:
			case GDK.GDK_BUTTON_RELEASE: {
				int [] state = new int [1];
				GDK.gdk_event_get_state (ptr, state);
				setInputState (event, state [0]);
				break;
			}
		}
		gdk_event_free (ptr);
	}
	sendEvent (eventType, event, send);
}

/**
 * Sets the application defined widget data associated
 * with the receiver to be the argument. The <em>widget
 * data</em> is a single, unnamed field that is stored
 * with every widget.
 * <p>
 * Applications may put arbitrary objects in this field. If
 * the object stored in the widget data needs to be notified
 * when the widget is disposed of, it is the application's
 * responsibility to hook the Dispose event on the widget and
 * do so.
 * </p>
 *
 * @param data the widget data
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - when the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - when called from the wrong thread</li>
 * </ul>
 *
 * @see #getData()
 */
public void setData (Object data) {
	checkWidget();
	if ((state & KEYED_DATA) != 0) {
		((Object []) this.data) [0] = data;
	} else {
		this.data = data;
	}
}

/**
 * Sets the application defined property of the receiver
 * with the specified name to the given value.
 * <p>
 * Applications may associate arbitrary objects with the
 * receiver in this fashion. If the objects stored in the
 * properties need to be notified when the widget is disposed
 * of, it is the application's responsibility to hook the
 * Dispose event on the widget and do so.
 * </p>
 *
 * @param key the name of the property
 * @param value the new value for the property
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #getData(String)
 */
public void setData (String key, Object value) {
	checkWidget();
	if (key == null) error (SWT.ERROR_NULL_ARGUMENT);

	if (key.equals (KEY_CHECK_SUBWINDOW)) {
		if (value != null && value instanceof Boolean) {
			if (((Boolean)value).booleanValue ()) {
				state |= CHECK_SUBWINDOW;
			} else {
				state &= ~CHECK_SUBWINDOW;
			}
		}
		return;
	}

	int index = 1;
	Object [] table = null;
	if ((state & KEYED_DATA) != 0) {
		table = (Object []) data;
		while (index < table.length) {
			if (key.equals (table [index])) break;
			index += 2;
		}
	}
	if (value != null) {
		if ((state & KEYED_DATA) != 0) {
			if (index == table.length) {
				Object [] newTable = new Object [table.length + 2];
				System.arraycopy (table, 0, newTable, 0, table.length);
				data = table = newTable;
			}
		} else {
			table = new Object [3];
			table [0] = data;
			data = table;
			state |= KEYED_DATA;
		}
		table [index] = key;
		table [index + 1] = value;
	} else {
		if ((state & KEYED_DATA) != 0) {
			if (index != table.length) {
				int length = table.length - 2;
				if (length == 1) {
					data = table [0];
					state &= ~KEYED_DATA;
				} else {
					Object [] newTable = new Object [length];
					System.arraycopy (table, 0, newTable, 0, index);
					System.arraycopy (table, index + 2, newTable, index, length - index);
					data = newTable;
				}
			}
		}
	}
	if (key.equals(SWT.SKIN_CLASS) || key.equals(SWT.SKIN_ID)) this.reskin(SWT.ALL);
	if (GTK.GTK_VERSION >= OS.VERSION(3, 20, 0) && key.equals(KEY_GTK_CSS) && value instanceof String) {
		long /*int*/ context = GTK.gtk_widget_get_style_context (cssHandle());
		long /*int*/ provider = GTK.gtk_css_provider_new();
		if (context != 0 && provider != 0) {
			GTK.gtk_style_context_add_provider (context, provider, GTK.GTK_STYLE_PROVIDER_PRIORITY_USER);
			GTK.gtk_css_provider_load_from_data (provider, Converter.wcsToMbcs ((String) value, true), -1, null);
			OS.g_object_unref (provider);
		}
	}
}

void setFontDescription (long /*int*/ widget, long /*int*/ font) {
	GTK.gtk_widget_override_font (widget, font);
	long /*int*/ context = GTK.gtk_widget_get_style_context (widget);
	GTK.gtk_style_context_invalidate (context);
}

boolean setInputState (Event event, int state) {
	if ((state & GDK.GDK_MOD1_MASK) != 0) event.stateMask |= SWT.ALT;
	if ((state & GDK.GDK_SHIFT_MASK) != 0) event.stateMask |= SWT.SHIFT;
	if ((state & GDK.GDK_CONTROL_MASK) != 0) event.stateMask |= SWT.CONTROL;
	if ((state & GDK.GDK_BUTTON1_MASK) != 0) event.stateMask |= SWT.BUTTON1;
	if ((state & GDK.GDK_BUTTON2_MASK) != 0) event.stateMask |= SWT.BUTTON2;
	if ((state & GDK.GDK_BUTTON3_MASK) != 0) event.stateMask |= SWT.BUTTON3;
	return true;
}

boolean setKeyState (Event javaEvent, long /*int*/ event) {
	long /*int*/ string;
	int length;
	int [] eventKeyval = new int[1];
	int group;
	GDK.gdk_event_get_keyval(event, eventKeyval);
	int [] eventState = new int[1];
	GDK.gdk_event_get_state(event, eventState);
	if (GTK.GTK4) {
		long /*int*/ [] eventString = new long /*int*/ [1];
		GDK.gdk_event_get_string(event, eventString);
		string = eventString[0];
		length = (int)/*64*/OS.g_utf16_strlen (string, -1);
		int [] eventGroup = new int [1];
		GDK.gdk_event_get_key_group(event, eventGroup);
		group = eventGroup[0];
	} else {
		GdkEventKey gdkEvent = new GdkEventKey ();
		OS.memmove(gdkEvent, event, GdkEventKey.sizeof);
		length = gdkEvent.length;
		string = gdkEvent.string;
		group = gdkEvent.group;
	}
	if (string != 0 && OS.g_utf16_strlen (string, length) > 1) return false;
	boolean isNull = false;
	javaEvent.keyCode = Display.translateKey (eventKeyval[0]);
	switch (eventKeyval[0]) {
		case GDK.GDK_BackSpace:		javaEvent.character = SWT.BS; break;
		case GDK.GDK_Linefeed:		javaEvent.character = SWT.LF; break;
		case GDK.GDK_KP_Enter:
		case GDK.GDK_Return: 		javaEvent.character = SWT.CR; break;
		case GDK.GDK_KP_Delete:
		case GDK.GDK_Delete:			javaEvent.character = SWT.DEL; break;
		case GDK.GDK_Escape:			javaEvent.character = SWT.ESC; break;
		case GDK.GDK_Tab:
		case GDK.GDK_ISO_Left_Tab: 	javaEvent.character = SWT.TAB; break;
		default: {
			if (javaEvent.keyCode == 0) {
				int [] keyval = new int [1];
				int [] effective_group = new int [1], level = new int [1], consumed_modifiers = new int [1];
				/* If current group is not a Latin layout, get the most Latin Layout group from input source. */
				Map<Integer, Integer> groupLatinKeysCount = display.getGroupKeysCount();
				if (!groupLatinKeysCount.containsKey(group)) {
					group = display.getLatinKeyGroup();
				}
				long /*int*/ keymap = GDK.gdk_keymap_get_for_display(GDK.gdk_display_get_default());
				short [] keyCode = new short [1];
				GDK.gdk_event_get_keycode(event, keyCode);
				if (GDK.gdk_keymap_translate_keyboard_state (keymap, keyCode[0],
						0, group, keyval, effective_group, level, consumed_modifiers)) {
					javaEvent.keyCode = (int) GDK.gdk_keyval_to_unicode (keyval [0]);
				}
			}
			int key = eventKeyval[0];
			if ((eventState[0] & GDK.GDK_CONTROL_MASK) != 0 && (0 <= key && key <= 0x7F)) {
				if ('a'  <= key && key <= 'z') key -= 'a' - 'A';
				if (64 <= key && key <= 95) key -= 64;
				javaEvent.character = (char) key;
				isNull = eventKeyval[0] == '@' && key == 0;
			} else {
				javaEvent.character = (char) GDK.gdk_keyval_to_unicode (key);
			}
		}
	}
	setLocationState (javaEvent, event);
	if (javaEvent.keyCode == 0 && javaEvent.character == 0) {
		if (!isNull) return false;
	}
	return setInputState (javaEvent, eventState[0]);
}

void setLocationState (Event event, long /*int*/ eventPtr) {
	int [] eventKeyval = new int[1];
	GDK.gdk_event_get_keyval(eventPtr, eventKeyval);
	switch (eventKeyval[0]) {
		case GDK.GDK_Alt_L:
		case GDK.GDK_Shift_L:
		case GDK.GDK_Control_L:
			event.keyLocation = SWT.LEFT;
			break;
		case GDK.GDK_Alt_R:
		case GDK.GDK_Shift_R:
		case GDK.GDK_Control_R:
				event.keyLocation = SWT.RIGHT;
			break;
		case GDK.GDK_KP_0:
		case GDK.GDK_KP_1:
		case GDK.GDK_KP_2:
		case GDK.GDK_KP_3:
		case GDK.GDK_KP_4:
		case GDK.GDK_KP_5:
		case GDK.GDK_KP_6:
		case GDK.GDK_KP_7:
		case GDK.GDK_KP_8:
		case GDK.GDK_KP_9:
		case GDK.GDK_KP_Add:
		case GDK.GDK_KP_Decimal:
		case GDK.GDK_KP_Delete:
		case GDK.GDK_KP_Divide:
		case GDK.GDK_KP_Down:
		case GDK.GDK_KP_End:
		case GDK.GDK_KP_Enter:
		case GDK.GDK_KP_Equal:
		case GDK.GDK_KP_Home:
		case GDK.GDK_KP_Insert:
		case GDK.GDK_KP_Left:
		case GDK.GDK_KP_Multiply:
		case GDK.GDK_KP_Page_Down:
		case GDK.GDK_KP_Page_Up:
		case GDK.GDK_KP_Right:
		case GDK.GDK_KP_Subtract:
		case GDK.GDK_KP_Up:
		case GDK.GDK_Num_Lock:
			event.keyLocation = SWT.KEYPAD;
			break;
	}
}

void setOrientation (boolean create) {
}

boolean setTabGroupFocus (boolean next) {
	return setTabItemFocus (next);
}

boolean setTabItemFocus (boolean next) {
	return false;
}

long /*int*/ shellMapProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ user_data) {
	return 0;
}

long /*int*/ sizeAllocateProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ user_data) {
	return 0;
}

long /*int*/ sizeRequestProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ user_data) {
	return 0;
}

/**
 * Converts an incoming snapshot into a gtk_draw() call, complete with
 * a Cairo context.
 *
 * @param handle the widget receiving the snapshot
 * @param snapshot the actual GtkSnapshot
 */
void snapshotToDraw (long /*int*/ handle, long /*int*/ snapshot) {
	GtkAllocation allocation = new GtkAllocation ();
	GTK.gtk_widget_get_allocation(handle, allocation);
	long /*int*/ rect = Graphene.graphene_rect_alloc();
	Graphene.graphene_rect_init(rect, 0, 0, allocation.width, allocation.height);
	long /*int*/ cairo = GTK.gtk_snapshot_append_cairo(snapshot, rect);
	gtk_draw(handle, cairo);
	Graphene.graphene_rect_free(rect);
	// Propagates the snapshot down the widget hierarchy so that other widgets
	// will draw.
	if (GTK.GTK_IS_CONTAINER(handle)) {
		propagateSnapshot(handle, snapshot);
	}
	return;
}

long /*int*/ gtk_widget_get_window (long /*int*/ widget){
	GTK.gtk_widget_realize(widget);
	return GTK.gtk_widget_get_window (widget);
}

long /*int*/ gtk_widget_get_surface (long /*int*/ widget){
	GTK.gtk_widget_realize(widget);
	return GTK.gtk_widget_get_surface (widget);
}

void gtk_widget_set_has_surface_or_window (long /*int*/ widget, boolean has) {
	if (GTK.GTK4) {
		if (has && OS.G_OBJECT_TYPE(widget) == OS.swt_fixed_get_type()) {
			return;
		}
		GTK.gtk_widget_set_has_surface(widget, has);
	} else {
		GTK.gtk_widget_set_has_window(widget, has);
	}
}

boolean gtk_widget_get_has_surface_or_window (long /*int*/ widget) {
	if (GTK.GTK4) {
		return GTK.gtk_widget_get_has_surface(widget);
	} else {
		return GTK.gtk_widget_get_has_window(widget);
	}
}

void gtk_widget_set_visible (long /*int*/ widget, boolean visible) {
	GTK.gtk_widget_set_visible (widget, visible);
}

void gdk_window_get_size (long /*int*/ drawable, int[] width, int[] height) {
	width[0] = GDK.gdk_window_get_width (drawable);
	height[0] = GDK.gdk_window_get_height (drawable);
}

void gdk_surface_get_size (long /*int*/ surface, int[] width, int[] height) {
	width[0] = GDK.gdk_surface_get_width (surface);
	height[0] = GDK.gdk_surface_get_height (surface);
}

/**
 * GTK4 does not hand out copies of events anymore, only references.
 * Call gdk_event_free() on GTK3 and g_object_unref() on GTK4.
 *
 * @param event the event to be freed
 */
void gdk_event_free (long /*int*/ event) {
	if (GTK.GTK4) {
		OS.g_object_unref(event);
	} else {
		GDK.gdk_event_free(event);
	}
}

/**
 * Wrapper function for gdk_event_get_state()
 * @param event   pointer to the GdkEvent.
 * @return the keymask to be used with constants like
 *        OS.GDK_SHIFT_MASK / OS.GDK_CONTROL_MASK / OS.GDK_MOD1_MASK etc..
 */
int gdk_event_get_state (long /*int*/ event) {
	int [] state = new int [1];
	GDK.gdk_event_get_state (event, state);
	return state[0];
}


long /*int*/ gtk_box_new (int orientation, boolean homogeneous, int spacing) {
	long /*int*/ box = GTK.gtk_box_new (orientation, spacing);
	GTK.gtk_box_set_homogeneous (box, homogeneous);
	return box;
}

void gtk_box_set_child_packing (long /*int*/ box, long /*int*/ child, boolean expand, boolean fill, int padding, int pack_type) {
	if (GTK.GTK4) {
		GTK.gtk_widget_set_hexpand(child, expand);
		GTK.gtk_widget_set_vexpand(child, expand);
		if (fill) {
			GTK.gtk_widget_set_halign(child, GTK.GTK_ALIGN_FILL);
			GTK.gtk_widget_set_valign(child, GTK.GTK_ALIGN_FILL);
		}
		GTK.gtk_box_set_child_packing(box, child, pack_type);
	} else {
		GTK.gtk_box_set_child_packing(box, child, expand, fill, padding, pack_type);
	}
}

int gdk_pointer_grab (long /*int*/ gdkResource, int grab_ownership, boolean owner_events, int event_mask, long /*int*/ confine_to, long /*int*/ cursor, int time_) {
	long /*int*/ display = 0;
	if (GTK.GTK4) {
		if( gdkResource != 0) {
			display = GDK.gdk_surface_get_display (gdkResource);
		}
	} else {
		if( gdkResource != 0) {
			display = GDK.gdk_window_get_display (gdkResource);
		} else {
			gdkResource = GDK.gdk_get_default_root_window ();
			display = GDK.gdk_window_get_display (gdkResource);
		}
	}
	if (GTK.GTK_VERSION >= OS.VERSION(3, 20, 0)) {
		long /*int*/ seat = GDK.gdk_display_get_default_seat(display);
		if (gdkSeatGrabPrepareFunc == null) {
			gdkSeatGrabPrepareFunc = new Callback(Widget.class, "GdkSeatGrabPrepareFunc", 3); //$NON-NLS-1$
		}
		long /*int*/ gdkSeatGrabPrepareFuncAddress = gdkSeatGrabPrepareFunc.getAddress();
		if (gdkSeatGrabPrepareFuncAddress == 0) SWT.error (SWT.ERROR_NO_MORE_CALLBACKS);
		return GDK.gdk_seat_grab(seat, gdkResource, GDK.GDK_SEAT_CAPABILITY_ALL_POINTING, owner_events, cursor, 0, gdkSeatGrabPrepareFuncAddress, gdkResource);
	} else {
		long /*int*/ pointer = GDK.gdk_get_pointer(display);
		return GDK.gdk_device_grab (pointer, gdkResource, grab_ownership, owner_events, event_mask, cursor, time_);
	}
}

void gdk_pointer_ungrab (long /*int*/ gdkResource, int time_) {
	long /*int*/ display = GTK.GTK4? GDK.gdk_surface_get_display(gdkResource) : GDK.gdk_window_get_display (gdkResource);
	if (GTK.GTK_VERSION >= OS.VERSION(3, 20, 0)) {
		long /*int*/ seat = GDK.gdk_display_get_default_seat(display);
		GDK.gdk_seat_ungrab(seat);
	} else {
		long /*int*/ pointer = GDK.gdk_get_pointer(display);
		GDK.gdk_device_ungrab (pointer, time_);
	}
}

static long /*int*/ GdkSeatGrabPrepareFunc (long /*int*/ gdkSeat, long /*int*/ gdkResource, long /*int*/ userData_gdkResource) {
	if (userData_gdkResource != 0) {
		if (GTK.GTK4) {
			GDK.gdk_surface_show(userData_gdkResource);
		} else {
			GDK.gdk_window_show(userData_gdkResource);
		}
	}
	return 0;
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
@Override
public String toString () {
	String string = "*Disposed*";
	if (!isDisposed ()) {
		string = "*Wrong Thread*";
		if (isValidThread ()) string = getNameText ();
	}
	return getName () + " {" + string + "}";
}

long /*int*/ topHandle () {
	return handle;
}

long /*int*/ timerProc (long /*int*/ widget) {
	return 0;
}

boolean translateTraversal (int event) {
	return false;
}

long /*int*/ windowProc (long /*int*/ handle, long /*int*/ user_data) {
	switch ((int)/*64*/user_data) {
		case ACTIVATE: return gtk_activate (handle);
		case CHANGED: return gtk_changed (handle);
		case CLICKED: return gtk_clicked (handle);
		case CLOSE_REQUEST: return gtk_close_request (handle);
		case CREATE_MENU_PROXY: return gtk_create_menu_proxy (handle);
		case DAY_SELECTED: return gtk_day_selected (handle);
		case DAY_SELECTED_DOUBLE_CLICK: return gtk_day_selected_double_click (handle);
		case HIDE: return gtk_hide (handle);
		case GRAB_FOCUS: return gtk_grab_focus (handle);
		case MAP: return gtk_map (handle);
		case MONTH_CHANGED: return gtk_month_changed (handle);
		case OUTPUT: return gtk_output (handle);
		case POPUP_MENU: return gtk_popup_menu (handle);
		case PREEDIT_CHANGED: return gtk_preedit_changed (handle);
		case REALIZE: return gtk_realize (handle);
		case START_INTERACTIVE_SEARCH: return gtk_start_interactive_search (handle);
		case SELECT: return gtk_select (handle);
		case SELECTION_DONE: return gtk_selection_done (handle);
		case SHOW: return gtk_show (handle);
		case VALUE_CHANGED: return gtk_value_changed (handle);
		case UNMAP: return gtk_unmap (handle);
		case UNREALIZE: return gtk_unrealize (handle);
		default: return 0;
	}
}

long /*int*/ windowProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ user_data) {
	switch ((int)/*64*/user_data) {
		case EXPOSE_EVENT_INVERSE: {
			if (GTK.GTK_IS_CONTAINER (handle)) {
				return gtk_draw (handle, arg0);
			}
			return 0;
		}
		case BUTTON_PRESS_EVENT_INVERSE:
		case BUTTON_RELEASE_EVENT_INVERSE:
		case MOTION_NOTIFY_EVENT_INVERSE: {
			return 1;
		}
		case BUTTON_PRESS_EVENT: return gtk_button_press_event (handle, arg0);
		case BUTTON_RELEASE_EVENT: return gtk_button_release_event (handle, arg0);
		case COMMIT: return gtk_commit (handle, arg0);
		case CONFIGURE_EVENT: return gtk_configure_event (handle, arg0);
		case DELETE_EVENT: return gtk_delete_event (handle, arg0);
		case ENTER_NOTIFY_EVENT: return gtk_enter_notify_event (handle, arg0);
		case EVENT: return gtk_event (handle, arg0);
		case EVENT_AFTER: return gtk_event_after (handle, arg0);
		case EXPOSE_EVENT: {
			if (!GTK.GTK_IS_CONTAINER (handle)) {
				return gtk_draw (handle, arg0);
			}
			return 0;
		}
		case FOCUS: return gtk_focus (handle, arg0);
		case FOCUS_IN_EVENT: return gtk_focus_in_event (handle, arg0);
		case FOCUS_OUT_EVENT: return gtk_focus_out_event (handle, arg0);
		case KEY_PRESS_EVENT: return gtk_key_press_event (handle, arg0);
		case KEY_RELEASE_EVENT: return gtk_key_release_event (handle, arg0);
		case INPUT: return gtk_input (handle, arg0);
		case LEAVE_NOTIFY_EVENT: return gtk_leave_notify_event (handle, arg0);
		case MAP_EVENT: return gtk_map_event (handle, arg0);
		case MNEMONIC_ACTIVATE: return gtk_mnemonic_activate (handle, arg0);
		case MOTION_NOTIFY_EVENT: return gtk_motion_notify_event (handle, arg0);
		case MOVE_FOCUS: return gtk_move_focus (handle, arg0);
		case POPULATE_POPUP: return gtk_populate_popup (handle, arg0);
		case SCROLL_EVENT:	return gtk_scroll_event (handle, arg0);
		case SHOW_HELP: return gtk_show_help (handle, arg0);
		case SIZE_ALLOCATE: return gtk_size_allocate (handle, arg0);
		case STYLE_SET: return gtk_style_set (handle, arg0);
		case TOGGLED: return gtk_toggled (handle, arg0);
		case UNMAP_EVENT: return gtk_unmap_event (handle, arg0);
		case WINDOW_STATE_EVENT: return gtk_window_state_event (handle, arg0);
		case ROW_DELETED: return gtk_row_deleted (handle, arg0);
		default: return 0;
	}
}

long /*int*/ windowProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ arg1, long /*int*/ user_data) {
	switch ((int)/*64*/user_data) {
		case DELETE_RANGE: return gtk_delete_range (handle, arg0, arg1);
		case DELETE_TEXT: return gtk_delete_text (handle, arg0, arg1);
		case ICON_RELEASE: return gtk_icon_release (handle, arg0, arg1);
		case ROW_ACTIVATED: return gtk_row_activated (handle, arg0, arg1);
		case SCROLL_CHILD: return gtk_scroll_child (handle, arg0, arg1);
		case STATUS_ICON_POPUP_MENU: return gtk_status_icon_popup_menu (handle, arg0, arg1);
		case SWITCH_PAGE: return gtk_switch_page (handle, arg0, arg1);
		case TEST_COLLAPSE_ROW: return gtk_test_collapse_row (handle, arg0, arg1);
		case TEST_EXPAND_ROW: return gtk_test_expand_row(handle, arg0, arg1);
		case ROW_INSERTED: return gtk_row_inserted (handle, arg0, arg1);
		case ROW_HAS_CHILD_TOGGLED: return gtk_row_has_child_toggled(handle, arg0, arg1);
		default: return 0;
	}
}

long /*int*/ windowProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ arg1, long /*int*/ arg2, long /*int*/ user_data) {
	switch ((int)/*64*/user_data) {
		case CHANGE_VALUE: return gtk_change_value (handle, arg0, arg1, arg2);
		case EXPAND_COLLAPSE_CURSOR_ROW: return gtk_expand_collapse_cursor_row (handle, arg0, arg1, arg2);
		case INSERT_TEXT: return gtk_insert_text (handle, arg0, arg1, arg2);
		case TEXT_BUFFER_INSERT_TEXT: return gtk_text_buffer_insert_text (handle, arg0, arg1, arg2);
		default: return 0;
	}
}

long /*int*/ windowProc (long /*int*/ handle, long /*int*/ arg0, long /*int*/ arg1, long /*int*/ arg2, long /*int*/ arg3, long /*int*/ user_data) {
	switch ((int)/*64*/user_data) {
		case POPPED_UP: return gtk_menu_popped_up (handle, arg0, arg1, arg2, arg3);
		default: return 0;
	}
}

void gtk_cell_renderer_get_preferred_size (long /*int*/ cell, long /*int*/ widget,  int[] width, int[] height) {
	GtkRequisition minimum_size = new GtkRequisition ();
	GTK.gtk_cell_renderer_get_preferred_size (cell, widget, minimum_size, null);
	if (width != null) width [0] = minimum_size.width;
	if (height != null) height[0] = minimum_size.height;
}

void gtk_widget_get_preferred_size (long /*int*/ widget, GtkRequisition requisition){
	GTK.gtk_widget_get_preferred_size (widget, requisition, null);
}

void gtk_image_set_from_gicon (long /*int*/ imageHandle, long /*int*/ pixbuf){
	GTK.gtk_image_set_from_gicon(imageHandle, pixbuf, GTK.GTK_ICON_SIZE_SMALL_TOOLBAR);
}
/**
 * Retrieves the amount of space around the outside of the container.
 * On GTK3: this is done using gtk_container_get_border_width.
 * On GTK4: this is done by returning the max margin on any side.
 * @param handle
 * @return amount of space around the outside of the container.
 */
int gtk_container_get_border_width_or_margin (long /*int*/ handle) {
	if (GTK.GTK4) {
		int marginTop = GTK.gtk_widget_get_margin_top(handle);
		int marginBottom = GTK.gtk_widget_get_margin_bottom(handle);
		int marginStart = GTK.gtk_widget_get_margin_start(handle);
		int marginEnd = GTK.gtk_widget_get_margin_end(handle);
		return Math.max(Math.max(marginTop, marginBottom), Math.max(marginStart, marginEnd));
	} else {
		return GTK.gtk_container_get_border_width(handle);
	}
}
/**
 * Sets the border width of the container to all sides of the container.
 * @param handle
 * @param border_width
 */
void gtk_container_set_border_width (long /*int*/ handle, int border_width) {
	if (GTK.GTK4) {
		GTK.gtk_widget_set_margin_top(handle, border_width);
		GTK.gtk_widget_set_margin_bottom(handle, border_width);
		GTK.gtk_widget_set_margin_start(handle, border_width);
		GTK.gtk_widget_set_margin_end(handle, border_width);
	} else {
		GTK.gtk_container_set_border_width (handle, border_width);
	}
}

}
