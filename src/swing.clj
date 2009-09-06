(ns swing
  (:import (javax.swing JTable JFrame 
			JScrollPane UIManager
			JPanel JTextField)
	   (java.awt.event KeyListener KeyAdapter KeyEvent
			   ActionListener)
	   (net.miginfocom.swing MigLayout)))

(UIManager/setLookAndFeel "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")

(def swing-agent (agent nil))

(defmacro swing 
  "execute on swing thread"
  [& body]
  `(send swing-agent 
	 (fn [_#]
	   (javax.swing.SwingUtilities/invokeLater (fn [] ~@body)))))

(defmacro swing-sync
  "execute on swing thread, within transaction"
  [& body]
  `(swing (dosync ~@body)))

(defmacro swing-io!
  "verify on eventDispatchThread, execute in io!,
this avoid some overhead"
  [& body]
  `(if (not (javax.swing.SwingUtilities/isEventDispatchThread))
     (throw (RuntimeException. "Not in swing event dispatch thread!!!"))
     (io! 
      ~@body)))

(defn swing-event
  "verify on event dispatch thread and execute
this avoid some overhead"
  [& body]
  `(if (not (javax.swing.SwingUtilities/isEventDispatchThread))
     (throw (RuntimeException. "Not in swing event dispatch thread!!!"))
     (do
       ~@body)))

(def *parent* nil)

(defmacro with-parent [widget & body]
  `(binding [*parent* ~widget]
     ~@body))


(defmacro frame [widget]  
  `(let [frame# (javax.swing.JFrame.)]
     (with-parent frame#
       (doto frame#
	 (.setSize 500, 500))
       (.. frame# (getContentPane) (add ~widget))
       (.pack frame#)
       (.setVisible frame# true)
       frame#)))

(defmacro scroll-pane [widget]
  `(let [pane# (javax.swing.JScrollPane.)]
     (when (= (class *parent*) JFrame)
       (.setPreferredSize pane# (.getSize *parent*)))
     (with-parent pane#
       (.. pane# (getViewport) (setView ~widget)))
     pane#))

(defn on-enter [widget fun]
  (.addActionListener widget
		      (proxy [ActionListener] []
			(actionPerformed [e]
					 (fun)))))

(defn panel [& widget-layouts]
  (let [p (JPanel. (MigLayout.))]
    (doseq [[widget layout] widget-layouts]
      (.add p widget layout))
    (when (= (class *parent*) JFrame)
      (.setPreferredSize p (.getSize *parent*)))
    p))

(defn text-field []
  (JTextField.))