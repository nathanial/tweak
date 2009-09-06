(ns textarea
  (:require keymap)
  (:import (javax.swing.text Document PlainDocument StyleContext Keymap)
	   (javax.swing JTextArea)
	   (java.io File)
	   (java.awt.event KeyAdapter KeyListener KeyEvent)))

(defn create [buffer]
  (let [textarea (JTextArea.)]
    (doto textarea
      (.addKeyListener (keymap/create-keymap-listener buffer)))))

(defn create-from-file [buffer path]
  (let [textarea (create buffer)
	document (PlainDocument.)]
    (doto document
      (.insertString 0 (slurp path) nil))
    (doto textarea
      (.setDocument document))))