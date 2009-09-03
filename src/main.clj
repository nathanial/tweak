(ns main
  (:require dataset table swing))

(table/start-application)
(table/populate-from-dataset 
 (dataset/create-with-file 
  {:date :_ :project "teledrill" :hours :_ :rate 18}
  "data/hours.clj"))
