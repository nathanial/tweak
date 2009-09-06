(ns frame
  (:require swing buffer-init buffer textarea))

(defstruct Frame
  :top-level-widget
  :textarea
  :buffer)

(defn create-top-level-widget [textarea]
  (swing/frame
   (swing/scroll-pane
    textarea)))

(defn create []
  (let [buf (buffer-init/create-scratch-buffer)
	textarea (textarea/create buf)
	top-level-widget (create-top-level-widget textarea)]
    (ref
     (struct-map Frame
       :top-level-widget top-level-widget
       :textarea textarea
       :buffer buf))))

