(ns motion.db)

(def num-examples 2)

(def default-db
  (reduce
    (fn [m i]
      (assoc m (keyword (str "motion.examples.demo" i "/state")) {}))
    {:name "Motion"}
    (range num-examples)))
