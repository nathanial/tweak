(ns util)

(def once-agent (agent nil))

(defmacro once [& body]
  `(let [fun# (fn [_#] ~@body)]
     (send (agent nil) fun#)))

(defmacro once-blocking [& body]
  `(let [fun# (fn [_#] ~@body)]
     (send-off (agent nil) fun#)))

(defn guard [condition msg]
  (when (not condition)
    (throw (new RuntimeException msg))))

(defn find-first [pred coll]
  (first (filter pred coll)))

(defn tuplize [& colls]
  (let [n (count colls)]
    (partition n (apply interleave colls))))

(defn key-to-str [key]
  (if (keyword? key)
    (.substring (str key) 1)
    key))

