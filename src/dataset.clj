(ns dataset)

(declare to-vector)

(defn already-filled? [template x]
  (not= :_ (get template x)))

(defn position-in [template x]
  (loop [keys (keys template), i 0]
    (if (= (first keys) x)
      i
      (recur (rest keys) (inc i)))))

(defn limit-gte [l n]
  (if (< n l)
    l
    n))

(defn fill-in [template item]
  (to-vector
   (for [x (keys template)]
     (if (already-filled? template x)
       (get template x)
       (nth item (limit-gte 0 (- (inc (position-in template x)) (count item))))))))

(defn to-vector [list]
  (apply vector list))

(defn create [template & items]
  (with-meta
   (to-vector
    (for [item items]
      (fill-in template item)))
   {:template template}))

(defn create-with-file [template file-name]
  (apply create template (read-string (slurp file-name))))

(defn row-count [dataset]
  (count dataset))

(defn column-count [dataset]
  (let [row (first dataset)]
    (count row)))

(defn dmerge [& datasets]
  (with-meta 
   (to-vector
    (apply concat datasets))
   {:template (-> datasets first meta (get :template))}))