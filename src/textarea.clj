(ns textarea
  (:require keymap)
  (:import (javax.swing.text Document StyleContext Keymap)
	   (javax.swing JTextArea)
	   (java.awt.event KeyAdapter KeyListener KeyEvent)))

(defn create []
  (let [textarea (JTextArea.)]
    (doto textarea
      (.addKeyListener (keymap/create-keymap-listener)))))

