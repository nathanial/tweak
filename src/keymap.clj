(ns keymap
  (:import (javax.swing AbstractAction KeyStroke)
	   (java.awt.event KeyAdapter KeyListener KeyEvent)))

(def running-key-list (ref []))
(def entering-command (ref false))
(def keymap 
     {'(Ctrl X Ctrl F) #(println "open-file")})

(defn keylist-to-symbols [key-list]
  (map symbol (map #(KeyEvent/getKeyText %) key-list)))

(defn print-key-list []
  (let [ks @running-key-list]
    (when (not (empty? ks))
      (println (map #(KeyEvent/getKeyText %) ks)))))

(defn clear-running-key-list []
  (dosync 
   (ref-set running-key-list [])
   (ref-set entering-command false)))

(defn execute-commands []
  (doseq [[key action] keymap]
    (let [len (count key)
	  cmd (keylist-to-symbols (take len @running-key-list))]
      (when (= key cmd)
	(clear-running-key-list)
	(action)))))

(defn create-keymap-listener []
  (proxy [KeyAdapter] []
    (keyPressed [e]
		(let [kc (.getKeyCode e)]
		  (when (not= KeyEvent/VK_CONTROL kc)
		    (dosync 
		     (when (or (true? @entering-command) (.isControlDown e))
		       (ref-set entering-command true)
		       (when (.isControlDown e) 
			 (alter running-key-list conj KeyEvent/VK_CONTROL))
		       (alter running-key-list conj kc))
		     (execute-commands))))
		(print-key-list))))