(ns buffer-init
  (:require keymap)
  (:use buffer)
  (:import (java.util.concurrent LinkedBlockingQueue)))

(defn create-from-file [name path]
  (let [bref (ref nil)
	b (struct-map Buffer 
	    :name name 
	    :path path
	    :contents (slurp path)
	    :local-vars  {:keymap keymap/default-keymap}
	    :action-queue (LinkedBlockingQueue.)
	    :thread (create-buffer-thread bref))]
    (dosync (ref-set bref b))
    (.start (:thread @bref))
    bref))

(defn create-scratch-buffer []
  (let [bref (ref nil)
	b (struct-map Buffer
	    :name "*Scratch*"
	    :path nil
	    :contents nil
	    :local-vars {:keymap keymap/default-keymap}
	    :action-queue (LinkedBlockingQueue.)
	    :thread (create-buffer-thread bref))]
    (dosync (ref-set bref b))
    (.start (:thread @bref))
    bref))

