(ns table
  (:use swing util)
  (:import (javax.swing JTable)
	   (org.jdesktop.swingx JXTable)
	   (org.jdesktop.swingx.decorator SortOrder)
	   (javax.swing.table DefaultTableModel 
			      TableModel
			      AbstractTableModel)))

(declare populate-from-dataset command-prompt)

(def *table* (ref nil))

(defn create 
  ([]
     (let [table (JXTable.)]
       (dosync
	(ref-set *table* table))
       table)))

(defn populate-from-dataset 
  ([dataset]
     (populate-from-dataset @*table* dataset))
  ([table dataset]
     (swing
      (let [model (DefaultTableModel.)
	    headers (keys (get (meta dataset) :template))]
	(doseq [c (range 0 (dataset/column-count dataset))]
	  (.addColumn model (key-to-str (nth headers c))))
	(doseq [data dataset]
	  (.addRow model (into-array Object data)))
	(.setModel table model)))
     table))

(defn- lookup-order [order]
  (cond 
   (= :asc order) SortOrder/ASCENDING
   (= :desc order) SortOrder/DESCENDING
   (= :none order) SortOrder/UNSORTED
   :else
   (throw (RuntimeException. (str "could not map order variable " order)))))

(defn sort-header 
  ([header order]
     (sort-header @*table* header order))
  ([table header order]
     (swing
      (doto table
	(.setSortOrder header (lookup-order order))
	(.toggleSortOrder header)))))

(defn start-application []
  (create)
  (frame 
   (panel
    [(scroll-pane @*table*) "grow, push, wrap"]
    [(command-prompt) "growx, pushx"])))

(defn command-prompt []
  (let [commander (text-field)]
    (doto commander
      (on-enter #(eval (read-string (.getText commander)))))))