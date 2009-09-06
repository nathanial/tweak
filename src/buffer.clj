(ns buffer)

(defstruct Buffer
  :name
  :path 
  :contents
  :local-vars
  :action-queue
  :thread)

(def *current-buffer* nil)

(defn get-local-var [name]
  (get-in @*current-buffer* [:local-vars name]))

(defn set-local-var [name value]
  (dosync
   (alter *current-buffer* assoc-in [:local-vars name] value)))

(defn pop-action [buffer]
  (.take (:action-queue @buffer)))

(defn create-buffer-thread [buffer]
  (proxy [Thread] []
    (run []
	 (loop []
	     (let [action (pop-action buffer)]
	       (binding [*current-buffer* buffer]
		 (action)))
	     (recur)))))

(defmacro in-buffer-thread [buffer & body]
  `(let [fun# (fn [] ~@body)]
     (.put (:action-queue (deref ~buffer)) fun#)))